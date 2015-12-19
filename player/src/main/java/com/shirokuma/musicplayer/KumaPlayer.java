package com.shirokuma.musicplayer;

import android.app.Application;
import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import com.activeandroid.ActiveAndroid;
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

public class KumaPlayer {
    public static final String ARGUMENTS_KEY_FILTER = "filter";
    public static final int SEEK_INTERVAL = 1000;
    public static final String WEBCHAT_APPID = "wx8c97b24c8801739a";
    static Timer mTimer;
    static List<Activity> mBoundActivities = new ArrayList<Activity>();
    static private IWXAPI wxapi;
    static Application application;
    static boolean initialized = false;

    public static void init(Application app) {
        if (initialized)
            return;
        application = app;
        app.startService(new Intent(app, MusicService.class));
        if (com.shiro.tools.Utils.isNetworkAvailable(app)) {
            wxapi = WXAPIFactory.createWXAPI(app, WEBCHAT_APPID, false);
            wxapi.registerApp(WEBCHAT_APPID);
        }
        // database
        ActiveAndroid.initialize(app);
        initialized = true;
    }

    public static void addBoundActivity(Activity bound) {
        mBoundActivities.add(bound);
    }

    public static void removeBoundActivity(Activity bound) {
        mBoundActivities.remove(bound);
    }

    public static void sleepMode(int minutes) {
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

    public static void webchatShare(String text) {
        if (wxapi == null) {
            if (application != null)
                Toast.makeText(application, application.getString(R.string.share_err), Toast.LENGTH_LONG).show();
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

    public static void webchatHandleIntent(Intent intent, IWXAPIEventHandler handler) {
        if (wxapi == null)
            return;
        wxapi.handleIntent(intent, handler);
    }

    public static void exit() {
        for (Activity binded : mBoundActivities) {
            binded.finish();
        }
        if (application != null)
            application.stopService(new Intent(application, MusicService.class));
    }
}
