package com.shirokuma.musicplayer.setting;

import android.app.ProgressDialog;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.common.NavBackActivity;

public class ScanActivity extends NavBackActivity {
    ProgressDialog progressDialog;
    MediaScannerConnection.OnScanCompletedListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MediaScannerConnection.scanFile(this, new String[]{Environment
                .getExternalStorageDirectory().getAbsolutePath()}, null, listener);
    }

    @Override
    protected void initData() {
        listener = new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        };
        progressDialog = ProgressDialog.show(this, "", getString(R.string.scanning));
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected int setContentViewRes() {
        return R.layout.activity_scan;
    }
}
