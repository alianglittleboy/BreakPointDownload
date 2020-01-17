package zl.com.breakpointdownload;

import android.app.Application;

/**
 * @ClassName BaseApplication
 * @Description TODO
 * @Author zhangliang
 * @Date 2020/1/17 13:50
 * @Version 1.0
 */
public class BaseApplication extends Application {
    private static BaseApplication application;
    @Override
    public void onCreate() {
        super.onCreate();
        application=this;
    }

    public static BaseApplication getInstance() {
        return (BaseApplication) application;
    }
}
