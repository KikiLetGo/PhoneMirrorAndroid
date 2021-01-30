package com.elexlab.mydisk.manager;

import com.alibaba.fastjson.JSONArray;
import com.elexlab.mydisk.constants.Constants;
import com.elexlab.mydisk.datasource.DataSourceCallback;
import com.elexlab.mydisk.utils.HttpUtils;

import java.util.List;

public class PhoneManager {
    private static PhoneManager instance = new PhoneManager();
    public static PhoneManager getInstance(){
        return instance;
    }
    public void listPhones(final DataSourceCallback<List<String>> dataSourceCallback){
        HttpUtils.GET(Constants.LIST_PHONES, new HttpUtils.HttpRequestListener() {
            @Override
            public void onResponse(String s) {
                List<String> phones = JSONArray.parseArray(s,String.class);
                if(dataSourceCallback != null){
                    dataSourceCallback.onSuccess(phones);
                }
            }

            @Override
            public void onErrorResponse(String msg) {
                if(dataSourceCallback != null){
                    dataSourceCallback.onFailure(msg,0);
                }
            }
        });
    }

}
