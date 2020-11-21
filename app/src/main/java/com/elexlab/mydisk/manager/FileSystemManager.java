package com.elexlab.mydisk.manager;

import android.content.Context;
import android.os.Environment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.elexlab.mydisk.constants.Constants;
import com.elexlab.mydisk.datasource.HeroLib;
import com.elexlab.mydisk.model.FileDir;
import com.elexlab.mydisk.utils.CommonUtil;
import com.elexlab.mydisk.utils.HeroLog;
import com.elexlab.mydisk.utils.HttpUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileSystemManager {
    private static final String TAG = FileSystemManager.class.getName();
    private static FileSystemManager instance = new FileSystemManager();
    public static FileSystemManager getInstance(){
        return instance;
    }

    public interface FileActionListener{
        void onCompletion(String msg);
        void onError(String msg);
    }

    public void checkAndCreateMirrorDisk(Context context){
        String deviceId = CommonUtil.getDeviceId(context);
        HeroLog.d(TAG,"deviceId:"+deviceId);
        HttpUtils.GET(Constants.HOST + "/checkMirrorDisk?device="+deviceId, new HttpUtils.HttpRequestListener() {
            @Override
            public void onResponse(String s) {
                HeroLog.d(TAG,"is MirrorDisk created:"+s);
                if(!Boolean.valueOf(s)){
                    createMirrorDisk();
                }

            }

            @Override
            public void onErrorResponse(String msg) {

            }
        });
    }
    public void uploadFile(String path,String localPath,final FileActionListener listener){
        File file = new File(localPath);



        OkHttpClient client = new OkHttpClient();


        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", path,
                        RequestBody.create(MediaType.parse("multipart/form-data"), new File(localPath)))
                .build();


        //MediaType contentType = MediaType.parse("multipart/form-data"); // 上传文件的Content-Type
        //RequestBody body = RequestBody.create(contentType, file); // 上传文件的请求体
        Request request = new Request.Builder()
                .url(Constants.UPLOAD_FILE+"&path="+path) // 上传地址
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 文件上传成功
                if (response.isSuccessful()) {
                    String res= response.body().string();
                    HeroLog.i(TAG, "onResponse: " +res);
                    if(listener != null){
                        listener.onCompletion(res);
                    }
                } else {
                    HeroLog.i(TAG,  "onResponse: " + response.message());
                    if(listener != null){
                        listener.onError(response.body().string());
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 文件上传失败
                HeroLog.i(TAG,  "onFailure: " + e.getMessage());
            }
        });
    }

    private void createMirrorDisk(){

        final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        final FileDir rootDir = new FileDir();
        Executors.newCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                deepSearchFiles(path,rootDir);
                String allFileJson = JSON.toJSONString(rootDir, SerializerFeature.DisableCircularReferenceDetect);
                long endTime = System.currentTimeMillis();
                HeroLog.d(TAG,"costTime:"+(endTime-startTime));
                HeroLog.d(TAG,"allFileJson:"+allFileJson);
                try {
                    FileWriter fileWriter = new FileWriter(new File(path+"/fileStructs.json"));
                    fileWriter.write(allFileJson);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Map<String,String> form = new HashMap<>();
                form.put("fileDir",allFileJson);
                form.put("deviceId",CommonUtil.getDeviceId(HeroLib.getInstance().appContext));
                HttpUtils.POST(Constants.HOST + "/createMirrorDisk", form, new HttpUtils.HttpRequestListener() {
                    @Override
                    public void onResponse(String s) {

                    }

                    @Override
                    public void onErrorResponse(String msg) {

                    }
                });


            }
        });



    }
    private void deepSearchFiles(String path,FileDir fileDir){
        List<String> documents = new ArrayList<>();
        List<FileDir> fileDirs = new ArrayList<>();
        File dir = new File(path);
        for(File file:dir.listFiles()){
            if(file.getName().startsWith(".")){
                continue;
            }
            if(file.isDirectory()){
                FileDir sunFileDir = new FileDir();
                sunFileDir.setName(file.getName());
                deepSearchFiles(file.getAbsolutePath(),sunFileDir);
                fileDirs.add(sunFileDir);
            }else{
                documents.add(file.getName());
            }
        }
        fileDir.setDocuments(documents);
        fileDir.setDirs(fileDirs);
    }


}
