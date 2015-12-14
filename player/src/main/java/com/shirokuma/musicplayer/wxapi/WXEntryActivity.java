package com.shirokuma.musicplayer.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;
import com.shirokuma.musicplayer.KumalrcApplication;
import com.shirokuma.musicplayer.R;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ((KumalrcApplication) getApplication()).webchatHandleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp resp) {
        int result = 0;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.errcode_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                break;
            default:
                result = R.string.errcode_unknown;
                break;
        }
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        finish();
    }
}
