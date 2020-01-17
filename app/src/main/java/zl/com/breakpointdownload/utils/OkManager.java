package zl.com.breakpointdownload.utils;

import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @ClassName OkManager
 * @Description TODO
 * @Author zhangliang
 * @Date 2020/1/16 16:55
 * @Version 1.0
 */
public class OkManager {
    private File rootFile;//文件的路径
    private File file;//文件
    private long downLoadSize;//下载文件的长度
    private final ThreadPoolExecutor executor;// 线程池
    private boolean isDown = false; //是否已经下载过了（下载后点击暂停） 默认为false
    private String name; //名称
    private String path;// 下载的网址
    private RandomAccessFile raf; // 读取写入IO方法
    private long totalSize = 0;
    private MyThread thread;//线程
    private Handler handler;//Handler 方法
    private ProgressBar progress;// 下载进度方法，内部定义的抽象方法


    /**
     * 构造方法  OKhttp
     *
     * @param path     网络连接路径
     * @param progress 更新路径
     */
    public OkManager(String path, ProgressBar progress) {
        this.path = path;
        this.progress = progress;
        this.handler = new Handler();
        this.name = path.substring(path.lastIndexOf("/") + 1);
        rootFile = FileUtils.getRootFile();
        executor = new ThreadPoolExecutor(5, 5, 50, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3000));

        initProgress();
    }

    /**
     * 刷新下载进度
     */
    public void initProgress() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                file = new File(rootFile, name);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        totalSize = getContentLength(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    final int pro = (int) ((file.length() / (totalSize * 1.0)) * 100);
                    Log.i("liang", "pro=" + pro + ",file.length()=" + file.length() + ",totalSize=" + totalSize);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progress.setProgress(pro);
                        }
                    });
                }
            }
        });
    }

    /**
     * 自定义线程
     */
    class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            downLoadFile();
        }
    }

    /**
     * 这就是下载方法
     */
    void downLoadFile() {
        Log.i("liang", "downLoadFile start");
        try {
            if (file == null) {//判断是否拥有相应的文件
                file = new File(rootFile, name); //很正常的File() 方法
                if (!file.exists()) {
                    file.createNewFile();
                }
                raf = new RandomAccessFile(file, "rwd");//实例化一下我们的RandomAccessFile()方法
            } else {
                downLoadSize = file.length();// 文件的大小
                if (raf == null) {//判断读取是否为空
                    raf = new RandomAccessFile(file, "rwd");
                }
                raf.seek(downLoadSize);
            }
            totalSize = getContentLength(path);//获取文件的大小
            Log.i("liang", "totalSize");
            if (downLoadSize == totalSize) {// 判断是否下载完成
                //已经下载完成
                return;
            }

            Log.i("liang", "downLoadFile end");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(path).
                    addHeader("Range", "bytes=" + downLoadSize + "-" + totalSize).build();
            Response response = client.newCall(request).execute();
            InputStream ins = response.body().byteStream();
            //上面的就是简单的OKHttp连接网络，通过输入流进行写入到本地
            int len = 0;
            byte[] by = new byte[1024];
            long endTime = System.currentTimeMillis();
            while ((len = ins.read(by)) != -1 && isDown) {//如果下载没有出错并且已经开始下载，循环进行以下方法
                raf.write(by, 0, len);
                downLoadSize += len;
                if (System.currentTimeMillis() - endTime > 1000) {
                    final double dd = downLoadSize / (totalSize * 1.0);
                    DecimalFormat format = new DecimalFormat("#0.00");
                    String value = format.format((dd * 100)) + "%";//计算百分比
                    Log.i("liang", "==================" + value);
                    handler.post(new Runnable() {//通过Handler发送消息到UI线程，更新
                        @Override
                        public void run() {
                            progress.setProgress((int) (dd * 100));
                        }
                    });
                }
            }
            response.close();//最后要把response关闭
        } catch (Exception e) {
            Log.i("liang", "e=" + e.getMessage());
            e.getMessage();
        }
    }

    /**
     * 线程开启方法
     */
    public void start() {
        Log.i("liang", "start");
        if (thread == null) {
            thread = new MyThread();
            isDown = true;
            executor.execute(thread);
        }
    }

    /**
     * 线程停止方法
     */
    public void stop() {
        Log.i("liang", "stop");
        if (thread != null) {
            isDown = false;
            executor.remove(thread);
            thread = null;
        }
    }

    //通过OkhttpClient获取文件的大小
    public long getContentLength(String url) throws IOException {
        Log.i("liang", "getContentLength url=" + url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        long length = response.body().contentLength();
        response.close();
        return length;
    }

    public interface IProgress {
        void onProgress(int progress);
    }
}
