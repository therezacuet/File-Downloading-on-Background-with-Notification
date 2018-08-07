package com.thereza.cmedtask01.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.thereza.cmedtask01.data.AppConstant;
import com.thereza.cmedtask01.model.Download;
import com.thereza.cmedtask01.network.ApiClient;
import com.thereza.cmedtask01.network.HttpParams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class DownloadService extends IntentService {

    private int totalFileSize;

    public DownloadService() {
        super("Download Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        initDownload();
    }

    private void initDownload() {

        Call<ResponseBody> request = ApiClient.getApiClient().downloadFile();
        try {

            downloadFile(request.execute().body());

        } catch (IOException e) {

            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private void downloadFile(ResponseBody body) throws IOException {

        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), HttpParams.API_END_POINT);

        OutputStream output = new FileOutputStream(outputFile);
        long total = 0;
        long startTime = System.currentTimeMillis();
        int timeCount = 1;
        while ((count = bis.read(data)) != -1) {

            total += count;
            totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
            double current = Math.round(total / (Math.pow(1024, 2)));

            int progress = (int) ((total * 100) / fileSize);

            long currentTime = System.currentTimeMillis() - startTime;

            Download download = new Download();
            download.setTotalFileSize(totalFileSize);

            if (currentTime > 1000 * timeCount) {

                download.setCurrentFileSize((int) current);
                download.setProgress(progress);
                sendIntent(download);
                timeCount++;
            }

            output.write(data, 0, count);
        }
        onDownloadComplete();
        output.flush();
        output.close();
        bis.close();

    }

    private void sendIntent(Download download) {

        Intent intent = new Intent(AppConstant.MESSAGE_PROGRESS);
        intent.putExtra("download", download);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }

    private void onDownloadComplete() {

        Download download = new Download();
        download.setProgress(100);
        sendIntent(download);

    }
}
