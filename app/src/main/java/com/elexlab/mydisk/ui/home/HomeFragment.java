package com.elexlab.mydisk.ui.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.elexlab.mydisk.R;
import com.elexlab.mydisk.datasource.DataSourceCallback;
import com.elexlab.mydisk.datasource.DiskFileLoader;
import com.elexlab.mydisk.datasource.HeroLib;
import com.elexlab.mydisk.manager.FileSystemManager;
import com.elexlab.mydisk.manager.MediaManager;
import com.elexlab.mydisk.pojo.FileInfo;
import com.elexlab.mydisk.ui.files.FileBrowserActivity;
import com.elexlab.mydisk.utils.HeroLog;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        root.findViewById(R.id.llFile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileBrowserActivity.startActivity(getContext());
            }
        });
        root.findViewById(R.id.llImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaManager.getInstance().backupImages(getContext());
            }
        });
        root.findViewById(R.id.llVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaManager.getInstance().backupVideos(getContext());
            }
        });
        root.findViewById(R.id.llContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaManager.getInstance().backupContact(getContext());
            }
        });
        FileSystemManager.getInstance().isMirrorCreated(getContext(), new DataSourceCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean, String... extraParams) {
                if(!aBoolean){
                    AlertDialog.Builder builder =  new AlertDialog.Builder(getContext());
                    builder.setTitle("未能检测到远程镜像，是否创建？");
                    final Dialog dialog = builder.setNegativeButton("不是现在", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                            .setPositiveButton("麻溜创建", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                   createMirrorDisk();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();


                }
            }

            @Override
            public void onFailure(String errMsg, int code) {

            }
        });
        //FileSystemManager.getInstance().checkAndCreateMirrorDisk(getContext());

        return root;
    }

    private void createMirrorDisk(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_loading,null);

        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .create();
        dialog.show();
        FileSystemManager.getInstance().createMirrorDisk(new DataSourceCallback() {
            @Override
            public void onSuccess(Object o, String... extraParams) {
                dialog.dismiss();
                Toast.makeText(getContext(),"创建成功",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String errMsg, int code) {
                dialog.dismiss();
                Toast.makeText(getContext(),"创建失败～",Toast.LENGTH_LONG).show();


            }
        });
    }
}