package com.shirokuma.musicplayer.musiclib;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.activeandroid.util.Log;
import com.shirokuma.musicplayer.MainActivity;
import com.shirokuma.musicplayer.PlayerEnv;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.model.Category;

import java.util.ArrayList;

public class ClassifyFragment extends Fragment {
    ArrayList<Category> categories;
    MainActivity context;

    public void onAttach(Context c) {
        super.onAttach(c);
        context = (MainActivity) c;
    }

    static final int ALBUM = 0, ARTIST = 1, FOLDER = 2, ALL = 4, PLAYLIST = 3;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_classify, container, false);
        categories = new ArrayList<Category>();
        categories.add(new Category(R.drawable.album, "album", null));
        categories.add(new Category(R.drawable.artist, "artist", null));
        categories.add(new Category(R.drawable.folder, "folder", null));
        categories.add(new Category(R.drawable.playlist, "playlist", null));
        categories.add(new Category(R.drawable.song, "All", null));
        ListView l = (ListView) root.findViewById(R.id.list);
        l.setAdapter(new ClassAdapter());
        return root;
    }

    class ClassAdapter extends BaseAdapter {
        public int getCount() {
            return categories.size();
        }

        public Object getItem(int position) {
            return categories.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_classify_list, parent, false);
                vh.title = (TextView) convertView.findViewById(R.id.title);
                vh.icon = (ImageView) convertView.findViewById(R.id.icon);
                vh.subtitle = (TextView) convertView.findViewById(R.id.subtitle);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            if (position >= getCount() || getItem(position) == null)
                return null;
            Category c = (Category) getItem(position);
            vh.title.setText(c.getTitle());
            vh.subtitle.setText(c.getSubtitle());
            vh.icon.setImageResource(c.getIcon());
            convertView.setOnClickListener(new OnClickCategoryListener(position));
            return convertView;
        }
    }

    class OnClickCategoryListener implements View.OnClickListener {
        int position;

        public OnClickCategoryListener(int p) {
            position = p;
        }

        public void onClick(View v) {
            Log.i(PlayerEnv.TAG, "on click category: position: " + position);
            if (position == ALL) {
                context.displayList(new Filter(Filter.FilterType.Song));
            } else if (position == ARTIST) {
                context.displayList(new Filter(Filter.FilterType.Artist));
            } else if (position == ALBUM) {
                context.displayList(new Filter(Filter.FilterType.Album));
            } else if (position == PLAYLIST) {
                context.displayList(new Filter(Filter.FilterType.Playlist));
            } else if (position == FOLDER) {
                context.displayList(new Filter(Filter.FilterType.Folder));
            }
        }
    }

    static class ViewHolder {
        ImageView icon;
        TextView title, subtitle;
    }
}
