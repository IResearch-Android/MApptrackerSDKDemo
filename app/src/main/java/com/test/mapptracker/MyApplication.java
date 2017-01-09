package com.test.mapptracker;

import android.app.Application;

import cn.com.iresearch.mapptracker.IRMonitor;

/**
 * Created by yuxiang on 2017/1/6.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * 初始化
         *
         * Context context 上下文
         * appkey 艾瑞分配的appkey
         * youUID 使用你们自己定义的uid，以便核对数据，(可为空，默认为imei)
         * showLog 显示日志
         * IRCallBack 回调信息
         */

        /**
         * (可选) context 上下文 channel 渠道名 (也可在AndroidManifest中配置渠道名)
         */
        //IRMonitor.getInstance().setAppChannel(this, "channel");

        IRMonitor.getInstance().init(this, "test_android", "your_uid", true, MainActivity.myIRCallBack);
    }
}
