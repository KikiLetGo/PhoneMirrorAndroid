package com.elexlab.mydisk.manager;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.widget.Toast;

import com.elexlab.mydisk.MyDiskApplication;
import com.elexlab.mydisk.datasource.DiskFileLoader;
import com.elexlab.mydisk.pojo.FileInfo;
import com.elexlab.mydisk.ui.misc.ProgressListener;
import com.elexlab.mydisk.ui.wiget.PercentProgressDialog;
import com.elexlab.mydisk.utils.HeroLog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MediaManager {
    private static final String TAG = MediaManager.class.getName();
    private static MediaManager instance = new MediaManager();
    public static MediaManager getInstance(){
        return instance;
    }
    public void backupImages(final Context context){
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null, null, null,null);
        List<FileInfo> fileInfos = new ArrayList<>();
        while (cursor.moveToNext()) {

            String img = cursor.getString(
                    cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            HeroLog.d("MediaManager",img);
            FileInfo fileInfo = new FileInfo(img);
            fileInfos.add(fileInfo);
        }
        backUpFiles(context,fileInfos);


    }

    public void backupVideos(Context context){
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,null, null, null,null);
        List<FileInfo> fileInfos = new ArrayList<>();

        while (cursor.moveToNext()) {


            String img = cursor.getString(
                    cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            HeroLog.d("MediaManager",img);
            FileInfo fileInfo = new FileInfo(img);
            fileInfos.add(fileInfo);
        }
        backUpFiles(context,fileInfos);

    }

    public void backupContact(Context context){
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                HeroLog.d("MediaManager",cursor.getString(0));
                HeroLog.d("MediaManager",cursor.getString(1));

            }
            cursor.close();
        }
    }

    private void backUpFiles(final Context context,List<FileInfo> fileInfos){
        final PercentProgressDialog percentProgressDialog = new PercentProgressDialog(context);
        percentProgressDialog.showProgress("同步中",false);


        FileSystemManager.getInstance().uploadFiles(fileInfos, new ProgressListener<FileInfo>() {
            @Override
            public void onProgress(final float progress, FileInfo fileInfo) {
                MyDiskApplication.getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        String pstr = new DecimalFormat( "0.00" ).format(progress*100);
                        percentProgressDialog.resetProgress(String.valueOf(pstr+"%"));
                    }
                });

            }

            @Override
            public void onComplete() {
                MyDiskApplication.getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        percentProgressDialog.stopProgress();
                        Toast.makeText(context,"文件夹内文件同步成功～",Toast.LENGTH_LONG).show();
                    }
                });


            }

            @Override
            public void onError(int code, final String message) {
                MyDiskApplication.getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        percentProgressDialog.stopProgress();
                        Toast.makeText(context,"同步发生错误:"+message,Toast.LENGTH_LONG).show();
                    }
                });


            }
        });
    }
}
