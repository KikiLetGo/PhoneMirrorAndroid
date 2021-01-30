package com.elexlab.mydisk.ui.gallery;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.elexlab.mydisk.MyDiskApplication;
import com.elexlab.mydisk.R;
import com.elexlab.mydisk.datasource.DataSourceCallback;
import com.elexlab.mydisk.manager.PhoneManager;

import java.util.List;


public class PhoneGalleryDialog extends Dialog {
    private Context context;
    private String strShop;


    public PhoneGalleryDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }
    public PhoneGalleryDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置布局
        setContentView(R.layout.phone_gallery_dialog);

        PhoneManager.getInstance().listPhones(new DataSourceCallback<List<String>>() {
            @Override
            public void onSuccess(final List<String> strings, String... extraParams) {
                MyDiskApplication.getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        showPhones(strings);
                    }
                });
            }

            @Override
            public void onFailure(String errMsg, int code) {

            }
        });

    }

    private void showPhones(List<String> phones){
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        LinearLayout llPhones = findViewById(R.id.llPhones);
        for(String phone:phones){
            View view = mInflater.inflate(R.layout.item_phone,
                    llPhones, false);
            TextView tvName = view.findViewById(R.id.tvName);
            tvName.setText(phone);
            llPhones.addView(view);
        }


    }
}


