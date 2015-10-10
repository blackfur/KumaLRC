package com.shirokuma.musicplayer;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.widget.Toast;
import com.shirokuma.musicplayer.common.Utils;
import com.shirokuma.musicplayer.playback.MusicService;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KumalrcApplication extends Application {
    Timer mTimer;
    static List<Activity> mBoundActivities = new ArrayList<Activity>();
    private IWXAPI wxapi;

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, MusicService.class));
        if (Utils.isNetworkAvailable(this)) {
            wxapi = WXAPIFactory.createWXAPI(this, Utils.WEBCHAT_APPID, false);
            wxapi.registerApp(Utils.WEBCHAT_APPID);
        }
    }

    public void addBoundActivity(Activity bound) {
        mBoundActivities.add(bound);
    }

    public void removeBoundActivity(Activity bound) {
        mBoundActivities.remove(bound);
    }

    public void sleepMode(int minutes) {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        if (minutes > 0) {
            mTimer = new Timer();
            long millis = minutes * 60 * 1000;
//            long millis = 3000;
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    exit();
                }
            }, millis);
        }
    }

    public void webchatShare(String text) {
        if (wxapi == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.share_err), Toast.LENGTH_LONG).show();
            return;
        }
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = (text == null) ? String.valueOf(System.currentTimeMillis()) : "text" + System.currentTimeMillis();
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        wxapi.sendReq(req);
    }

    public void webchatHandleIntent(Intent intent, IWXAPIEventHandler handler) {
        if (wxapi == null)
            return;
        wxapi.handleIntent(intent, handler);
    }

    public void exit() {
        for (Activity binded : mBoundActivities) {
            binded.finish();
        }
        stopService(new Intent(this, MusicService.class));
    }
}
