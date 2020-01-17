package zl.com.breakpointdownload;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zl.com.breakpointdownload.utils.OkManager;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.progress_circular)
    ProgressBar progressCircular;
    @BindView(R.id.btn)
    Button btn;
    @BindView(R.id.btn1)
    Button btn1;

    OkManager okManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        String url = "http://172.16.7.58/swd.rar";//下载地址，可以通过hfs 自行搭建一个，很简单
        okManager = new OkManager(url, progressCircular);
    }

    @OnClick({R.id.btn, R.id.btn1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn:
                okManager.start();
                break;
            case R.id.btn1:
                okManager.stop();
                break;
        }
    }
}
