package com.shiro.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;

public class Utils {
    private final static String TAG = "kumaplayer";
    private static boolean DEBUG = true;

    public static void alert(final Activity context, final String message) {
        if (!DEBUG)
            return;
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(context).setMessage(message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    // toast in ui thread
    public static void uitoast(final Activity a, final String msg) {
        a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(a, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static ProgressDialog loading(Activity ctx, ProgressDialog progressDialog, int res) {
        if (progressDialog != null) {
            progressDialog.setMessage(ctx.getString(res));
            progressDialog.show();
        } else {
            progressDialog = ProgressDialog.show(ctx, "", ctx.getString(res));
        }
        return progressDialog;
    }

    public static void dismiss(Activity ctx, final ProgressDialog progress) {
        if (progress != null) {
            ctx.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progress.dismiss();
                }
            });
        }
    }

    public static boolean copy(String from, String to) throws IOException {
        Log.e(TAG, "==== coping ====" + from);
        File fromFile = new File(from);
        if (fromFile.exists()) {
            Log.e(TAG, "==== source exists ====");
            FileInputStream fromStream = new FileInputStream(fromFile);
            File toFile = new File(to);
            if (!toFile.exists()) {
                toFile.getParentFile().mkdirs();
                toFile.createNewFile();
            }
            FileOutputStream toStream = new FileOutputStream(toFile);
            FileChannel fromChannel = null;
            FileChannel toChannel = null;
            Log.e(TAG, "==== transfer ====");
            try {
                fromChannel = fromStream.getChannel();
                toChannel = toStream.getChannel();
                fromChannel.transferTo(0, fromChannel.size(), toChannel);
                Log.e(TAG, "==== backup success ====");
                return true;
            } finally {
                Log.e(TAG, "==== close channel ====");
                try {
                    if (fromChannel != null) {
                        fromChannel.close();
                    }
                } finally {
                    if (toChannel != null) {
                        toChannel.close();
                    }
                }
            }
        } else {
            Log.e(TAG, "==== source not found ====");
            return false;
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    static DialogInterface.OnClickListener defaultOnclick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    public static void alert(Context context, String message, final Listener positiveCallback, final DialogInterface.OnClickListener positiveOnclick, final Listener negativeCallback, final DialogInterface.OnClickListener negativeOnclick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setMessage(message);
        if (positiveOnclick != null) builder.setPositiveButton(android.R.string.no, positiveOnclick);
        else
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (positiveCallback != null) positiveCallback.process();
                }
            });
        if (negativeOnclick != null) builder.setNegativeButton(android.R.string.no, negativeOnclick);
        else
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (negativeCallback != null) negativeCallback.process();
                }
            });
        builder.show();
    }

    public static void alert(Context context, String message) {
        alert(context, message, null, defaultOnclick, null, null);
    }

    public static void alert(Context context, String message, Listener positiveCallback) {
        alert(context, message, positiveCallback, null, null, defaultOnclick);
    }

    public static final String MORSE_CODE_BOOK[] = {"·-", "-···", "-·-·", "-··", "·", "··-·",
            "--·", "····", "··", "·---", "-·-", "·-··", "--", "-·",
            "---", "·--·", "--·-", "·-·", "···", "-", "··-", "···-",
            "·--", "-··-", "-·--", "--··"
    };

    public static void setOptionMenuIconEnable(Menu menu, boolean enable) {
        try {
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
            m.invoke(menu, enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int dp2px(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public static String file2str(String path, String encode) {
        BufferedReader reader;
        try {
            FileInputStream stream = new FileInputStream(path);
            reader = new BufferedReader(new InputStreamReader(stream, encode == null ? "utf-8" : encode));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return stringBuilder.toString();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static String str2morse(String text) {
        StringBuffer buf = new StringBuffer(text.length() * 3);
        int count = 0;
        for (char c : text.toUpperCase().toCharArray()) {
            if (count++ > 0) buf.append(", ");
            buf.append(MORSE_CODE_BOOK[c - 'A']);
        }
        return buf.toString();
    }

    public static void shortcut(Context c, Class launchActivity, int titleRes, int drawableRes) {
        Intent addIntent = new Intent();
        Intent shortcutIntent = new Intent(c.getApplicationContext(), launchActivity);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, c.getString(titleRes));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(c.getApplicationContext(), drawableRes));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        c.getApplicationContext().sendBroadcast(addIntent);
    }

    public static int[] getCenter(View v) {
        v.measure(0, 0);
        return new int[]{v.getMeasuredWidth() / 2, v.getMeasuredHeight() / 2};
    }

    public static void warn(final Activity c, final int msgRes, final String msg, final ProgressDialog progress, final DialogInterface.OnClickListener callback) {
        c.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (msgRes > 0)
                    new AlertDialog.Builder(c).setMessage(c.getString(msgRes)).setCancelable(true).setPositiveButton(android.R.string.ok, callback).show();
                else if (msg != null)
                    new AlertDialog.Builder(c).setMessage(msg).setCancelable(true).setPositiveButton(android.R.string.ok, callback).show();
                if (progress != null)
                    progress.dismiss();
            }
        });
    }

    public static void warn(final Activity c, final String msg) {
        warn(c, 0, msg, null, null);
    }

    public static void warn(final Activity c, final int strRes, final ProgressDialog progress) {
        warn(c, strRes, null, progress, null);
    }

    public static void warn(final Activity c, final String msg, final ProgressDialog progress) {
        warn(c, 0, msg, progress, null);
    }

    public static void warn(final Activity c, final int msgRes, final ProgressDialog progress, DialogInterface.OnClickListener callback) {
        warn(c, msgRes, null, progress, callback);
    }

    public static void warn(final Activity c, final int msgRes, DialogInterface.OnClickListener callback) {
        warn(c, msgRes, null, null, callback);
    }

    public static void warn(final Activity c, final int msgRes) {
        warn(c, msgRes, null, null, null);
    }

    public static PopupWindow showPopupWindow(View showView, View anchorView) {
//        final PopupWindow popupWindow = new PopupWindow(showView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        final PopupWindow popupWindow = new PopupWindow(showView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        popupWindow.setTouchable(true);
//        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(anchorView.getContext().getResources().getDrawable(
                R.drawable.white_round));
//        popupWindow.showAsDropDown(anchorView);
        popupWindow.showAtLocation(anchorView.getRootView(), Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    /**
     * only use this function after list has been attached to parent
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        Log.e(TAG, "==== adapter height ====");
        ListAdapter listAdapter = listView.getAdapter();
        Log.e(TAG, "==== get adapter ====");
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        Log.e(TAG, "==== calculate height ====");
        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        Log.e(TAG, "==== set height ====");
    }
}
