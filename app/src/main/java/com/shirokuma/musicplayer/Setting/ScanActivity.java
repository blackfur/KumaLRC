package com.shirokuma.musicplayer.setting;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.common.BaseActivity;
import com.shirokuma.musicplayer.common.NavBackActivity;
import com.shirokuma.musicplayer.common.Utils;

import java.io.File;

public class ScanActivity extends NavBackActivity {
    BroadcastReceiver mReceiver;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent scanIntent = new Intent(Utils.ACTION_MEDIA_SCANNER_SCAN_DIR);
        scanIntent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
        sendBroadcast(scanIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReceiver == null)
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (TextUtils.equals(Intent.ACTION_MEDIA_SCANNER_STARTED, intent.getAction())) {
                        Toast.makeText(ScanActivity.this, getString(R.string.scanning), Toast.LENGTH_SHORT).show();
                        progressDialog = ProgressDialog.show(ScanActivity.this, "", getString(R.string.loading));
                    } else if (TextUtils.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED, intent.getAction())) {
                        Toast.makeText(ScanActivity.this, getString(R.string.scanned), Toast.LENGTH_SHORT).show();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                    }
                }
            };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    protected void initData() {
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
