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
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;

public class Utils {
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
        File fromFile = new File(from);
        if (fromFile.exists()) {
            FileInputStream fromStream = new FileInputStream(fromFile);
            File toFile = new File(to);
            if (!toFile.exists()) {
                toFile.getParentFile().mkdirs();
                toFile.createNewFile();
            }
            FileOutputStream toStream = new FileOutputStream(toFile);
            FileChannel fromChannel = null;
            FileChannel toChannel = null;
            try {
                fromChannel = fromStream.getChannel();
                toChannel = toStream.getChannel();
                fromChannel.transferTo(0, fromChannel.size(), toChannel);
                return true;
            } finally {
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
        } else return false;
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
            reader = new BufferedReader(new InputStreamReader(stream, encode == null ? "GB2312" : encode));
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

}
