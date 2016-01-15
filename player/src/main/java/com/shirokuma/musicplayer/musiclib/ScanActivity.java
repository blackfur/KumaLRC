package com.shirokuma.musicplayer.musiclib;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.shiro.tools.view.ProgressDialogWrapper;
import com.shirokuma.musicplayer.KumaPlayer;
import com.shirokuma.musicplayer.R;

import java.io.File;
import java.io.FileFilter;

public class ScanActivity extends FragmentActivity {
    ListView list;
    TextView path;
    ProgressDialogWrapper progress;
    File[] paths;
    DirAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initial view
        setContentView(R.layout.activity_scan);
        progress = new ProgressDialogWrapper(getContext());
        list = (ListView) findViewById(R.id.list);
        path = (TextView) findViewById(R.id.path_str);
        for (int i : new int[]{R.id.back, R.id.ok, R.id.cancel}) {
            findViewById(i).setOnClickListener(onClick);
        }
        // initial list data
        File defaultSdcard = Environment.getExternalStorageDirectory();
        path.setText(defaultSdcard.getAbsolutePath());
        path.setTag(defaultSdcard);
        paths = dir2subdirs(defaultSdcard);
        adapter = new DirAdapter();
        list.setAdapter(adapter);
        list.setOnItemClickListener(onItemClick);
    }

    AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            File selectDir = (File) parent.getAdapter().getItem(position);
            if (selectDir == null)
                return;
            Log.e(KumaPlayer.TAG, "==== change path ====");
            path.setText(selectDir.getAbsolutePath());
            path.setTag(selectDir);
            paths = dir2subdirs(selectDir);
            adapter.notifyDataSetChanged();
        }
    };
    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.back) {
                File currentDir = (File) path.getTag();
                if (currentDir != null) {
                    File parentDir = currentDir.getParentFile();
                    if (parentDir != null) {
                        Log.e(KumaPlayer.TAG, "==== back path ====");
                        Log.e(KumaPlayer.TAG, parentDir.getAbsolutePath());
                        path.setText(parentDir.getAbsolutePath());
                        path.setTag(parentDir);
                        paths = dir2subdirs(parentDir);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e(KumaPlayer.TAG, "parent directory null");
                    }
                }
            } else if (v.getId() == R.id.ok) {
                progress.loading(R.string.loading);
            } else if (v.getId() == R.id.cancel) {
                finish();
            }
        }
    };

    private Activity getContext() {
        return this;
    }

    /**
     * get all sub directories under giving directory(with full path)
     *
     * @return
     */
    public static File[] dir2subdirs(File parent) {
        if (!parent.exists() || !parent.isDirectory())
            return null;
        FileFilter dirFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        return parent.listFiles(dirFilter);
    }

    private class DirAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (paths != null)
                return paths.length;
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (paths != null && paths.length > position)
                return paths[position];
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (paths != null && paths.length > position) {
                ViewHolder holder;
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = getLayoutInflater().inflate(R.layout.path_item, null);
                    holder.title = (TextView) convertView.findViewById(R.id.title);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.title.setText(paths[position].getName());
            }
            return convertView;
        }
    }

    private static class ViewHolder {
        public TextView title;
    }
}
