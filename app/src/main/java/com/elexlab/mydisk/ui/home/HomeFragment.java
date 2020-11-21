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
import com.elexlab.mydisk.datasource.DataSourceCallback;
import com.elexlab.mydisk.datasource.DiskFileLoader;
import com.elexlab.mydisk.datasource.HeroLib;
import com.elexlab.mydisk.manager.FileSystemManager;
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
        root.findViewById(R.id.gotoFiles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileBrowserActivity.startActivity(getContext());
            }
        });
        FileSystemManager.getInstance().checkAndCreateMirrorDisk(getContext());

        return root;
    }
}