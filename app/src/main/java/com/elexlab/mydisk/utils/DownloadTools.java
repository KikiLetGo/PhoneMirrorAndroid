package com.elexlab.mydisk.utils;



import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;

/** 调用前需添加权限  <uses-permission android:name="android.permission.INTERNET" />*/
public class DownloadTools
{
    public interface DownLoadCallback{
        void onSuccess();
        void onFail(String msg);
    }
    /** 获取指定网络文件url，保存至本地文件路径filePath */
    public static void DownloadFile(final String url, final String filePath,final DownLoadCallback downLoadCallback)
    {
        ConfirmFile(filePath);

        Executors.newCachedThreadPool().execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    URL webUrl = new URL(url);
                    URLConnection con = webUrl.openConnection();	// 打开连接
                    InputStream in = con.getInputStream();			// 获取InputStream

                    File f = new File(filePath);					// 创建文件输出流
                    FileOutputStream fo = new FileOutputStream(f);

                    byte[] buffer = new byte[1024 * 1024];
                    int len = 0;
                    while( (len = in.read(buffer)) > 0)		// 读取文件
                    {
                        fo.write(buffer, 0, len); 			// 写入文件
                    }

                    in.close();

                    fo.flush();
                    fo.close();
                    if(downLoadCallback != null){
                        downLoadCallback.onSuccess();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /** 创建目录和文件 */
    public static void ConfirmFile(String filePath)
    {
        try
        {
            File f = new File(filePath);
            File parent = f.getParentFile();

            if (!parent.exists()) parent.mkdirs();
            if (!f.exists()) f.createNewFile();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}