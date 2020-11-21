package com.elexlab.mydisk.datasource;

/**
 * The call back when we request a datasource
 * Created by BruceYoung on 15/11/12.
 */
public interface DataSourceCallback<T> {
    public void onSuccess(T t, String... extraParams);
    public void onFailure(String errMsg, int code);
}
