package com.elexlab.mydisk.ui.files;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.elexlab.mydisk.R;
import com.elexlab.mydisk.ui.home.FileListFragment;
import com.elexlab.mydisk.utils.FragmentUtils;

public class FileBrowserActivity extends FragmentActivity {
    public static void startActivity(Context context){
        Intent intent = new Intent(context,FileBrowserActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_browser);
        final FileListFragment fileListFragment = new FileListFragment();
        FragmentUtils.switchFragment(this,R.id.flContainer,null,fileListFragment);


    }
}
