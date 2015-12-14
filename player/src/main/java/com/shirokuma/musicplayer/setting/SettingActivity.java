package com.shirokuma.musicplayer.setting;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.common.BaseActivity;

public class SettingActivity extends BaseActivity {
    private View mBtnBack, btnLogin, btnScan;
    private ToggleButton toggleShuffle, toggleSaveLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initView() {
        mBtnBack = findViewById(R.id.btn_back);
        toggleShuffle = (ToggleButton) findViewById(R.id.shuffle);
        toggleSaveLast = (ToggleButton) findViewById(R.id.save_last);
        btnLogin = findViewById(R.id.login);
        btnScan = findViewById(R.id.scan);
        final TextView version = (TextView) findViewById(R.id.version);
        mBtnBack.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    version.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                mBtnBack.setOnClickListener(mBtnListener);
                toggleShuffle.setChecked(MediaSetting.getInstance(getApplicationContext()).getShuffle());
                toggleSaveLast.setChecked(MediaSetting.getInstance(getApplicationContext()).getSaveLast());
                btnLogin.setOnClickListener(mBtnListener);
                btnScan.setOnClickListener(mBtnListener);
            }
        }, 200);
    }

    @Override
    protected int setContentViewRes() {
        return R.layout.activity_setting;
    }

    @Override
    protected void onDestroy() {
        MediaSetting.getInstance(getApplicationContext()).setShuffle(toggleShuffle.isChecked());
        MediaSetting.getInstance(getApplicationContext()).setSaveLast(toggleSaveLast.isChecked());
        super.onDestroy();
    }

    private ProgressDialog mProgress;
    protected View.OnClickListener mBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_back:
                    finish();
                    break;
                case R.id.scan:
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{Environment
                            .getExternalStorageDirectory().getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            if (mProgress != null) {
                                mProgress.dismiss();
                                mProgress = null;
                            }
                        }
                    });
                    mProgress = ProgressDialog.show(SettingActivity.this, "", getString(R.string.scanning));
                    break;
            }
        }
    };
}
