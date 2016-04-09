package com.shirokuma.musicplayer.musiclib;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.shiro.tools.Utils;
import com.shiro.tools.view.ProgressDialogWrapper;
import com.shirokuma.musicplayer.PlayerEnv;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.model.Song;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

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
            Log.e(PlayerEnv.TAG, "==== change path ====");
            path.setText(selectDir.getAbsolutePath());
            path.setTag(selectDir);
            paths = dir2subdirs(selectDir);
            adapter.notifyDataSetChanged();
        }
    };

    void updir() {
        File currentDir = (File) path.getTag();
        if (currentDir != null) {
            File parentDir = currentDir.getParentFile();
            if (parentDir != null) {
                Log.e(PlayerEnv.TAG, "==== back path ====");
                Log.e(PlayerEnv.TAG, parentDir.getAbsolutePath());
                path.setText(parentDir.getAbsolutePath());
                path.setTag(parentDir);
                paths = dir2subdirs(parentDir);
                adapter.notifyDataSetChanged();
            } else {
                Log.e(PlayerEnv.TAG, "parent directory null");
            }
        }
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.back) {
                updir();
            } else if (v.getId() == R.id.ok) {
                progress.loading(R.string.loading);
                new Thread(scanTask).start();
            } else if (v.getId() == R.id.cancel) {
                finish();
            }
        }
    };
    DialogInterface.OnClickListener onDialogClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            finish();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            updir();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    Runnable scanTask = new Runnable() {
        @Override
        public void run() {
            File searchDir = (File) path.getTag();
            try {
                if (searchDir != null) {
                    FileFilter audioFilter = new FileFilter() {
                        public boolean accept(File file) {
                            if (file.isFile()) {
                                String name = file.getName();
                                String extension = name.substring(name.lastIndexOf('.') + 1);
                                return extension.equalsIgnoreCase("mp3");
                            }
                            return false;
                        }
                    };
                    Log.e(PlayerEnv.TAG, "==== read then store audio file meta data ====");
                    //
                    ActiveAndroid.beginTransaction();
                    for (File f : searchDir.listFiles(audioFilter)) {
                        String path = f.getAbsolutePath();
                        Log.e(PlayerEnv.TAG, "==== " + path + " ====");
                        if (!new Select().from(Song.class).where("path=?", path).exists()) {
                            MusicMetadataSet src_set = new MyID3().read(f);
                            Log.e(PlayerEnv.TAG, "MusicMetadataSet: " + src_set);
                            IMusicMetadata metadata = src_set.getSimplified();
                            Log.e(PlayerEnv.TAG, "IMusicMetadata: " + metadata);
                            Song newSong = new Song(metadata.getSongTitle(), metadata.getArtist(), metadata.getAlbum(), f.getAbsolutePath());
                            Log.e(PlayerEnv.TAG, "finding lyrics: " + path);
                            String lrc = path.substring(0, path.lastIndexOf('.')) + ".lrc";
                            if (new File(lrc).exists()) {
                                Log.e(PlayerEnv.TAG, "found: " + lrc);
                                newSong.lrc = lrc;
                            }
                            long result = newSong.save();
                            Log.e(PlayerEnv.TAG, "saved result: " + result);
                        } else {
                            Log.e(PlayerEnv.TAG, "already exists");
                        }
                        progress.hint(f.getAbsolutePath());
                    }
                    ActiveAndroid.setTransactionSuccessful();
                }
            } catch (IOException e) {
                Log.e(PlayerEnv.TAG, e.getMessage() == null ? "Null Pointer" : e.getMessage());
            } finally {
                ActiveAndroid.endTransaction();
            }
            progress.dismiss();
            Utils.warn(getContext(), R.string.success, onDialogClick);
        }
    };
}
