package com.elexlab.mydisk.pojo;

import com.elexlab.mydisk.constants.Constants;

public class FileInfo extends HttpPojo{
    public interface FileType{
        String DIR = "dir";
        String DOCUMENT = "document";
    }
    public interface StoreLocation{
        int MIRROR = 0;
        int LOCAL = MIRROR+1;
        int LOCAL_MIRROR = LOCAL+1;
    }
    private String fileType;
    private String name;
    private String path;
    private int storeLocation;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(int storeLocation) {
        this.storeLocation = storeLocation;
    }

    public String getUrl(){
        if(storeLocation == StoreLocation.MIRROR){
            String url = Constants.DOWNLOAD_FILE + path+name;
            return url;

        }else {
            return Constants.Path.LOCAL_DISK_ROOT+path+name;
        }
    }

    public boolean isDir(){
        return FileType.DIR.equals(fileType);
    }
    public boolean isFile(){
        return FileType.DOCUMENT.equals(fileType);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null||!(obj instanceof FileInfo)){
            return super.equals(obj);
        }
        FileInfo outter = (FileInfo) obj;
        return (path+name).equals(outter.path+outter.name);
    }

}
