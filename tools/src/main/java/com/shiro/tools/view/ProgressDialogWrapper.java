package com.shiro.tools.view;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * asynchronously display/dismiss progress dialog, avoid manipulate UI on non main thread exception.
 */
public class ProgressDialogWrapper {
    public ProgressDialog dialog;
    private Activity activity;

    public ProgressDialogWrapper(Activity a) {
        activity = a;
        dialog = new ProgressDialog(activity);
    }

    public void dismiss() {
        if (dialog != null)
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (dialog.isShowing())
                        dialog.dismiss();
                }
            });
    }

    public void loading(final String s) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog == null)
                    dialog = ProgressDialog.show(activity, "", s);
                else {
                    dialog.setMessage(s);
                    dialog.show();
                }
            }
        });
    }

    public void loading(final int strRes) {
        loading(activity.getString(strRes));
    }

    public void hint(final String s) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog == null)
                    dialog = ProgressDialog.show(activity, "", s);
                else {
                    dialog.setMessage(s);
                    if (!dialog.isShowing())
                        dialog.show();
                }
            }
        });
    }
}

