package zl.com.breakpointdownload.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;

import zl.com.breakpointdownload.BaseApplication;

/**
 * @ClassName FileUtils
 * @Description TODO
 * @Author zhangliang
 * @Date 2020/1/16 16:56
 * @Version 1.0
 */
public class FileUtils {

    //判断是否安装SDCard
    public static boolean isSdOk(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;
    }
    //创建一个文件夹，用来存放下载的文件
    public static File getRootFile(){
//        File rootFile = Environment.getExternalStorageDirectory();
        File rootFile = BaseApplication.getInstance().getCacheDir();

//        File rootFile = new File(sd,);
        if (!rootFile.exists()){
            rootFile.mkdirs();
        }
        Log.i("liang","rootFile="+rootFile+",rootFile.exists()="+rootFile.exists());
        return rootFile;
    }
}
