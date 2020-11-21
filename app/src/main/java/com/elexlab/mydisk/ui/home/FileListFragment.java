package com.elexlab.mydisk.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.elexlab.mydisk.R;
import com.elexlab.mydisk.constants.Constants;
import com.elexlab.mydisk.datasource.DataSourceCallback;
import com.elexlab.mydisk.datasource.DiskFileLoader;
import com.elexlab.mydisk.pojo.FileInfo;
import com.elexlab.mydisk.utils.CommonUtil;

import java.util.List;

public class FileListFragment  extends Fragment {

    private static int viewMode = 1;
    private HomeViewModel homeViewModel;
    private String dir = "";

    private DiskFileLoader diskFileLoader;
    private FilesAdapter filesAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_filelist, container, false);

        View llShowMirror = root.findViewById(R.id.llShowMirror);
        llShowMirror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchModel();
            }
        });


        final ListView lvFiles = root.findViewById(R.id.lvFiles);
        dir = "";
        if (getArguments() != null) {
            String currentDir = getArguments().getString("dir");
            if(currentDir != null){
                dir = currentDir;
            }
        }
        final String path = dir;
        diskFileLoader = new DiskFileLoader();
        filesAdapter = new FilesAdapter(this);
        lvFiles.setAdapter(filesAdapter);
        switchModel();
        return root;
    }

    public void switchModel(){
        if(viewMode == 0){
            diskFileLoader.loadLocalFiles(dir, new DataSourceCallback<List<FileInfo>>() {
                @Override
                public void onSuccess(List<FileInfo> fileInfos, String... extraParams) {
                    filesAdapter.reset(fileInfos);
                }

                @Override
                public void onFailure(String errMsg, int code) {

                }
            });
            viewMode = 1;
        }else if(viewMode == 1){
            diskFileLoader.loadMergedFiles(dir, new DataSourceCallback<List<FileInfo>>() {
                @Override
                public void onSuccess(List<FileInfo> fileInfos, String... extraParams) {
                    filesAdapter.reset(fileInfos);
                }

                @Override
                public void onFailure(String errMsg, int code) {

                }
            });
            viewMode = 0;

        }

    }


}