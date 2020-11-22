package com.elexlab.mydisk.datasource;

import android.util.ArraySet;

import com.elexlab.mydisk.constants.Constants;
import com.elexlab.mydisk.pojo.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DiskFileLoader {
    private List<FileInfo> localCaches;
    private List<FileInfo> mirrorCaches;
    private List<FileInfo> mergedCaches;
    private String dir;

    public interface Callback{
        void onLoaded(List<FileInfo> merged,List<FileInfo> locals,List<FileInfo> mirrors);
    }
    public DiskFileLoader(String dir) {
        this.dir = dir;
    }

    public void loadFiles(final String dir, final Callback callback){
        if(mergedCaches != null && localCaches != null && mirrorCaches != null){
            if(callback != null){
                callback.onLoaded(mergedCaches,localCaches,mirrorCaches);
            }
            return;
        }
        if(mirrorCaches != null && localCaches != null){
            mergedCaches = mergeLocalAndMirrorFile(localCaches,mirrorCaches);
            if(callback != null){
                callback.onLoaded(mergedCaches,localCaches,mirrorCaches);
            }
            return;
        }
        loadLocalFiles(new DataSourceCallback<List<FileInfo>>() {
            @Override
            public void onSuccess(final List<FileInfo> localFileInfos, String... extraParams) {
                loadMirrorFiles(new DataSourceCallback<List<FileInfo>>() {
                    @Override
                    public void onSuccess(List<FileInfo> fileInfos, String... extraParams) {
                        mergeLocalAndMirrorFile(localFileInfos,fileInfos);
                        if(callback != null){
                            callback.onLoaded(mergedCaches,localCaches,mirrorCaches);
                        }
                    }

                    @Override
                    public void onFailure(String errMsg, int code) {

                    }
                });
            }

            @Override
            public void onFailure(String errMsg, int code) {

            }
        });

    }
    private List<FileInfo> mergeLocalAndMirrorFile(List<FileInfo> localFileInfos,List<FileInfo> mirrorFileInfos){

        List<FileInfo> mergedList = new ArrayList<>();

        for(FileInfo localFileInfo:localFileInfos){
            FileInfo result = null;
            for(FileInfo mirrorFileInfo:mirrorFileInfos){
                if(localFileInfo.equals(mirrorFileInfo)){
                    localFileInfo.setStoreLocation(FileInfo.StoreLocation.LOCAL_MIRROR);
                    mirrorFileInfo.setStoreLocation(FileInfo.StoreLocation.LOCAL_MIRROR);
                    result = localFileInfo;
                    break;
                }
            }
            if(result != null){
                mergedList.add(result);
                mirrorFileInfos.remove(result);
            }else{
                mergedList.add(localFileInfo);
            }

        }
        //now mirrorFileInfo only contains fileInfo that not exist in local
        for(FileInfo mirrorFileInfo:mirrorFileInfos){
            mergedList.add(mirrorFileInfo);
        }
        sortFileInfos(mergedList);
        mergedCaches = mergedList;
        return mergedList;
    }
    private void loadLocalFiles(final DataSourceCallback<List<FileInfo>> callback){
        if(localCaches != null){
            if(callback != null){
                callback.onSuccess(localCaches);
            }
            return;
        }
        DataCondition dataCondition = new DataCondition();
        dataCondition.getParamMap().put("dir",dir);
        new LocalFileDataSource().getDatas(new DataSourceCallback<List<FileInfo>>() {
            @Override
            public void onSuccess(List<FileInfo> localFileInfos, String... extraParams) {
                sortFileInfos(localFileInfos);
                localCaches =localFileInfos;
                if(callback != null){
                    callback.onSuccess(localCaches);
                }
            }

            @Override
            public void onFailure(String errMsg, int code) {

            }
        }, dataCondition,FileInfo.class);
    }

    private void loadMirrorFiles(final DataSourceCallback<List<FileInfo>> callback){
        if(mirrorCaches != null){
            if(callback != null){
                callback.onSuccess(localCaches);
            }
            return;
        }
        FileInfoDataSource fileInfoDataSource = new FileInfoDataSource();
        fileInfoDataSource.setUrl(Constants.LIST_DIR_BASE + dir);
        DataCondition dataCondition = new DataCondition();
        fileInfoDataSource.getDatas(new DataSourceCallback<List<FileInfo>>() {
            @Override
            public void onSuccess(final List<FileInfo> fileInfos, String... extraParams) {
                sortFileInfos(fileInfos);
                mirrorCaches = fileInfos;
                if(callback != null){
                    callback.onSuccess(fileInfos);
                }
            }

            @Override
            public void onFailure(String errMsg, int code) {

            }
        }, null, FileInfo.class);
    }

    private void sortFileInfos(List<FileInfo> fileInfos){
        Collections.sort(fileInfos, new Comparator<FileInfo>() {
            @Override
            public int compare(FileInfo o1, FileInfo o2) {
                if (o1.isDir() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDir())
                    return 1;
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
}
