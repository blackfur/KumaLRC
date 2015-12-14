package com.shiro.memo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.shiro.memo.model.Entry;

import java.io.*;

public class FuncActivity extends AppCompatActivity {
    private static final String IMPORT_DATA = Environment.getExternalStorageDirectory() + "/shiro-memo-import.dat";
    private static final String DELIMIT = "#";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_func);
        for (View v : new View[]{findViewById(R.id.add), findViewById(R.id.backup), findViewById(R.id.restore), findViewById(R.id.import_from_text)})
            v.setOnClickListener(onClick);
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.add:
                    startActivity(new Intent(FuncActivity.this, RedactActivity.class));
                    break;
                case R.id.backup:
                    if (com.shiro.memo.model.Util.export())
                        Toast.makeText(FuncActivity.this, R.string.success, Toast.LENGTH_LONG).show();
                    else Toast.makeText(FuncActivity.this, R.string.fail, Toast.LENGTH_LONG).show();
                    break;
                case R.id.restore:
                    if (com.shiro.memo.model.Util.restore())
                        Toast.makeText(FuncActivity.this, R.string.success, Toast.LENGTH_LONG).show();
                    else Toast.makeText(FuncActivity.this, R.string.fail, Toast.LENGTH_LONG).show();
                    break;
                case R.id.import_from_text:
                    loading(R.string.loading);
                    new Thread(importTask).start();
                    break;
            }
        }
    };
    Runnable importTask = new Runnable() {
        @Override
        public void run() {
            File importDat = new File(IMPORT_DATA);
            if (importDat.exists()) {
                ActiveAndroid.beginTransaction();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(importDat));
                    for (String line; (line = br.readLine()) != null; ) {
                        if (line.length() < 2 || TextUtils.equals(line, DELIMIT))
                            continue;
                        From from = new Select().from(Entry.class).where("content = ?", line);
                        if (from.count() == 0) new Entry(line).save();
                    }
                    br.close();
                    ActiveAndroid.setTransactionSuccessful();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    ActiveAndroid.endTransaction();
                    dismiss();
                    toast(R.string.finish);
                }
            } else toast(R.string.fail);
        }
    };

    private void toast(final int strRes) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FuncActivity.this, strRes, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loading(final int strRes) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null)
                    progressDialog = ProgressDialog.show(FuncActivity.this, "", getString(strRes));
                else {
                    progressDialog.setMessage(getString(strRes));
                    progressDialog.show();
                }
            }
        });
    }

    public void dismiss() {
        if (progressDialog != null)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });
    }
}
