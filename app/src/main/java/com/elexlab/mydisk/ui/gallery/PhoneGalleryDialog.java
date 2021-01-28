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

import com.elexlab.mydisk.R;


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
        LinearLayout llPhones = findViewById(R.id.llPhones);
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        for(int i=0;i<10;i++){
            View view = mInflater.inflate(R.layout.item_phone,
                    llPhones, false);
            llPhones.addView(view);
        }
    }
}


