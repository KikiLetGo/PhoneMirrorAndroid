package com.elexlab.mydisk.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elexlab.mydisk.R;
import com.elexlab.mydisk.constants.Constants;
import com.elexlab.mydisk.datasource.DataSourceCallback;
import com.elexlab.mydisk.datasource.DiskFileLoader;
import com.elexlab.mydisk.pojo.FileInfo;
import com.elexlab.mydisk.ui.files.FilesBrowserAdapter;
import com.elexlab.mydisk.utils.CommonUtil;

import java.util.List;

public class FileListFragment  extends Fragment {
    private interface ShowMode{
        int NO_MIRROR = 0;
        int WITH_MIRROR = 1;
    }

    private int viewMode = ShowMode.NO_MIRROR;
    private HomeViewModel homeViewModel;
    private String dir = "";

    private DiskFileLoader diskFileLoader;
    private FilesBrowserAdapter filesBrowserAdapter;
    private ImageView ivMode;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_filelist, container, false);

        View llShowMirror = root.findViewById(R.id.llShowMirror);
        ivMode = root.findViewById(R.id.ivMode);
        llShowMirror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchModel();
            }
        });

        final RecyclerView rcvFiles = root.findViewById(R.id.rcvFiles);
        rcvFiles.setLayoutManager(new LinearLayoutManager(getContext()));

        dir = "";
        if (getArguments() != null) {
            String currentDir = getArguments().getString("dir");
            if(currentDir != null){
                dir = currentDir;
            }
        }
        final String path = dir;
        diskFileLoader = new DiskFileLoader(dir);
        filesBrowserAdapter = new FilesBrowserAdapter(this);
        rcvFiles.setAdapter(filesBrowserAdapter);
        loadFiles();
        return root;
    }
    public void switchModel(){
        if(viewMode == ShowMode.WITH_MIRROR){
            viewMode = ShowMode.NO_MIRROR;
            ivMode.setImageResource(R.mipmap.ic_eye_close);

        }else if(viewMode == ShowMode.NO_MIRROR){
            viewMode = ShowMode.WITH_MIRROR;
            ivMode.setImageResource(R.drawable.ic_eye);
        }

        loadFiles();
    }

    private void loadFiles(){
        diskFileLoader.loadFiles(dir, new DiskFileLoader.Callback() {
            @Override
            public void onLoaded(List<FileInfo> merged, List<FileInfo> locals, List<FileInfo> mirrors) {

                switch (viewMode){
                    case ShowMode.WITH_MIRROR:{
                        filesBrowserAdapter.resetFileList(merged);
                        break;
                    }
                    case ShowMode.NO_MIRROR:{
                        filesBrowserAdapter.resetFileList(locals);
                        break;
                    }
                }
            }
        });
    }




}