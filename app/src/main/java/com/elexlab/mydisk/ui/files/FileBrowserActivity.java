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
import com.elexlab.mydisk.datasource.HeroLib;
import com.elexlab.mydisk.manager.PhoneManager;
import com.elexlab.mydisk.ui.home.FileListFragment;
import com.elexlab.mydisk.utils.CommonUtil;
import com.elexlab.mydisk.utils.FragmentUtils;
import com.elexlab.mydisk.utils.HeroLog;

public class FileBrowserActivity extends FragmentActivity {
    public static void startActivity(Context context){
        PhoneManager.getInstance().setDevice(CommonUtil.getDeviceId(HeroLib.getInstance().appContext));

        Intent intent = new Intent(context,FileBrowserActivity.class);
        context.startActivity(intent);
    }
    public static void startActivity(Context context,String device){
        Intent intent = new Intent(context,FileBrowserActivity.class);
        intent.putExtra("device",device);
        PhoneManager.getInstance().setDevice(device);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_browser);

        String device = getIntent().getStringExtra("device");
        HeroLog.d("FileBrowserActivity","device:"+device);
        if(device == null){
            device =  CommonUtil.getDeviceId(HeroLib.getInstance().appContext);
        }
        final FileListFragment fileListFragment = new FileListFragment(device);
        FragmentUtils.switchFragment(this,R.id.flContainer,null,fileListFragment,false);


    }
}
