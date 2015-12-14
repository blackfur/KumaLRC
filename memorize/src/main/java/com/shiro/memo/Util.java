package com.shiro.memo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Util {
    public static boolean copy(String from, String to) throws IOException {
        File fromFile = new File(from);
        if (fromFile.exists()) {
            FileInputStream fromStream = new FileInputStream(fromFile);
            File toFile = new File(to);
            if (!toFile.exists()) toFile.createNewFile();
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
                    if (positiveCallback != null) positiveCallback.process(null);
                }
            });
        if (negativeOnclick != null) builder.setNegativeButton(android.R.string.no, negativeOnclick);
        else
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (negativeCallback != null) negativeCallback.process(null);
                }
            });
        builder.show();
    }

    public interface Listener {
        Object process(Object msg);
    }

    public static void alert(Context context, String message) {
        alert(context, message, null, defaultOnclick, null, null);
    }

    public static void alert(Context context, String message, Listener positiveCallback) {
        alert(context, message, positiveCallback, null, null, defaultOnclick);
    }
}
