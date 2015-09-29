package com.shirokuma.musicplayer.share;

import android.os.Bundle;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.common.BaseActivity;
import com.shirokuma.musicplayer.common.Utils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class ShareActivity extends BaseActivity {
    private IWXAPI webchatapi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {
        webchatapi = WXAPIFactory.createWXAPI(this, Utils.WEBCHAT_APP_ID, true);
        webchatapi.registerApp(Utils.WEBCHAT_APP_ID);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected int setContentViewRes() {
        return R.layout.activity_share;
    }
}
