package com.test.mapptracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.com.iresearch.mapptracker.IRCallBack;
import cn.com.iresearch.mapptracker.IRMonitor;
import cn.com.iresearch.mapptracker.test.R;

public class MainActivity extends AppCompatActivity implements IRCallBack {

    private String TAG = MainActivity.class.getSimpleName();
    private int REQUEST_CODE = 0x10;
    private TextView tv_pre, tv_send, tv_result;
    private Handler handler = new Handler();

    public static final MyIRCallBack myIRCallBack = new MyIRCallBack();

    private final static class MyIRCallBack implements IRCallBack {

        private IRCallBack irCallBack;

        public void setIrCallBack(IRCallBack irCallBack) {
            this.irCallBack = irCallBack;
        }

        @Override
        public void preSend() {
            if (irCallBack != null) {
                irCallBack.preSend();
            }
        }

        @Override
        public void sendSuccess() {
            if (irCallBack != null) {
                irCallBack.sendSuccess();
            }
        }

        @Override
        public void sendFail(String s) {
            if (irCallBack != null) {
                irCallBack.sendFail(s);
            }
        }
    }

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * 下面这行代码的逻辑是为了方便测试而做，正常开发可无视
         */
        myIRCallBack.setIrCallBack(this);


        initView();


    }

    private void initView() {
        tv_pre = (TextView) findViewById(R.id.pre_send);
        tv_send = (TextView) findViewById(R.id.start_send);
        tv_result = (TextView) findViewById(R.id.send_result);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //onResume调用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestOurPermissions();
        } else {
            IRMonitor.getInstance().onResume(MainActivity.this);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        //onPause中调用
        IRMonitor.getInstance().onPause(this);
    }


    //-----------------------CallBack---------------------------

    @Override
    public void preSend() {
        // 数据上报前
        System.out.println("上报数据前,记录一次");


        handler.post(new Runnable() {
            @Override
            public void run() {
                tv_pre.setTextColor(getResources().getColor(R.color.color_send_ok));
                tv_pre.setText(getString(R.string.pre_send) + "\t" + getTime_yyyyMMddHHmmss());
            }
        });
    }

    @Override
    public void sendSuccess() {
        // 数据上报，成功
        System.out.println("上报数据成功,记录一次");


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_result.setTextColor(getResources().getColor(R.color.color_send_ok));
                tv_result.setText(getString(R.string.send_result_succeed) + "\t" + getTime_yyyyMMddHHmmss());
            }
        }, 500);
    }

    @Override
    public void sendFail(String msg) {
        // 数据上报，失败
        System.out.println("上报数据失败,记录一次:" + msg);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_result.setTextColor(getResources().getColor(R.color.color_send_fail));
                tv_result.setText(getString(R.string.send_result_fail) + "\t" + getTime_yyyyMMddHHmmss());
            }
        }, 500);
    }

    public static String getTime_yyyyMMddHHmmss() {
        try {
            Date newTime = new Date(System.currentTimeMillis());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            return simpleDateFormat.format(newTime);
        } catch (Exception ex) {
            return "";
        }
    }


    private void requestOurPermissions() {
        ArrayList<String> requests = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            requests.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //申请READ_PHONE_STATE权限
            requests.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (requests.size() == 0) {
            IRMonitor.getInstance().onResume(this);
        } else {
            String[] permissions = new String[requests.size()];
            ActivityCompat.requestPermissions(MainActivity.this, requests.toArray(permissions), REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == REQUEST_CODE && grantResults.length != 0) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    requestOurPermissions();
                    return;
                }
            }
            IRMonitor.getInstance().onResume(this);
        }
    }
}
