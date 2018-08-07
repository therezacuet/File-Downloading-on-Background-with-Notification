package com.thereza.cmedtask01.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thereza.cmedtask01.R;
import com.thereza.cmedtask01.data.AppConstant;
import com.thereza.cmedtask01.model.Download;
import com.thereza.cmedtask01.service.DownloadService;
import com.thereza.cmedtask01.utils.NetworkUtil;
import com.thereza.cmedtask01.utils.NotificationUtils;

public class MainActivity extends BaseActivity {

    private Context mContext;
    private TextView tvProgressText;
    private ProgressBar pbDownload,pbLoading;
    private Button btnDownload;
    private boolean isPaused, isDownloadStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable();
        initView();
        initListener();

    }

    private void initVariable() {
        mContext = getApplicationContext();
        registerReceiver();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        tvProgressText = findViewById(R.id.tv_progress);
        pbDownload = findViewById(R.id.pb_download);
        btnDownload = findViewById(R.id.btn_download);
        pbLoading = findViewById(R.id.pb_loading);
    }
    private void initListener(){

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()){
                    if (NetworkUtil.getConnectivityStatus(mContext) == AppConstant.VALUE_ZERO){
                        showSnackBar(mContext.getString(R.string.net_work_not_available), mContext.getString(R.string.action_connect));
                    }else {
                        startDownload();
                        pbLoading.setVisibility(View.VISIBLE);
                    }
                } else {
                    requestPermission();
                }
            }
        });
    }

    private void startDownload(){

        Intent intent = new Intent(this,DownloadService.class);
        startService(intent);

    }

    private void registerReceiver(){

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstant.MESSAGE_PROGRESS);
        bManager.registerReceiver(broadcastReceiver, intentFilter);

    }

    protected void onPause(){
        super.onPause();
        isPaused=true;
        if (isDownloadStart){
            NotificationUtils.generateNotifcation(mContext);
        }

    }

    protected void onResume(){
        super.onResume();
        if (isPaused){
            isPaused=false;
            NotificationUtils.cancelNotification();
        }

    }
    protected void onDestroy(){
        NotificationUtils.cancelNotification();
        super.onDestroy();

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(AppConstant.MESSAGE_PROGRESS)){
                isDownloadStart = true;
                pbDownload.setVisibility(View.VISIBLE);
                pbLoading.setVisibility(View.GONE);
                Download download = intent.getParcelableExtra("download");
                pbDownload.setProgress(download.getProgress());
                if(download.getProgress() == 100){
                    tvProgressText.setText("File Download Complete");
                    if (isPaused){
                        NotificationUtils.onDownloadComplete();
                    }
                } else {
                    if (isPaused){
                        NotificationUtils.sendNotification(download,download.getTotalFileSize());
                    }
                    tvProgressText.setText(String.format("Downloaded (%d/%d) MB",download.getCurrentFileSize(),download.getTotalFileSize()));
                }
            }
        }
    };



    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(){

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},AppConstant.PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AppConstant.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDownload();
                } else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout),"Permission Denied, Please allow to proceed !", Snackbar.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

}
