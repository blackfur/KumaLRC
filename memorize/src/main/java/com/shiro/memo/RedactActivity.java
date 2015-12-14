package com.shiro.memo;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.activeandroid.query.Delete;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.shiro.memo.model.Entry;

import java.util.ArrayList;
import java.util.List;

public class RedactActivity extends AppCompatActivity {
    ArrayList<Entry> dat = new ArrayList<Entry>();
    Handler mWorkHandler;
    SwipeRefreshLayout mRefreshView;
    EditText redactContent;
    View save, update, finish, cancel, clear, delete;
    RecyclerView entries;
    private EntryAdapter adapter;
    private HandlerThread mWorkThr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
        // process
        reload();
    }

    void initView() {
        setContentView(R.layout.activity_redact);
        // init buttons
        redactContent = (EditText) findViewById(R.id.redact_content);
//        int[] ids = new int[]{R.id.update, R.id.save, R.id.clear, R.id.cancel, R.id.finish};
        save = findViewById(R.id.save);
        delete = findViewById(R.id.delete);
        update = findViewById(R.id.update);
        clear = findViewById(R.id.clear);
        cancel = findViewById(R.id.cancel);
        finish = findViewById(R.id.finish);
        View[] views = new View[]{update, delete, save, clear, cancel, finish};
        for (int i = 0; i < views.length; i++) {
            views[i].setOnClickListener(onclick);
        }
        // init entries list
        entries = (RecyclerView) findViewById(R.id.entries);
        entries.setHasFixedSize(true);
        entries.setLayoutManager(new LinearLayoutManager(this));
        entries.setItemAnimator(new DefaultItemAnimator());
        adapter = new EntryAdapter();
        entries.setAdapter(adapter);
        // init swipe view
        mRefreshView = (SwipeRefreshLayout) findViewById(R.id.swipe_view);
        mRefreshView.setOnRefreshListener(onRefresh);
        mRefreshView.setColorSchemeColors(Color.GRAY, Color.GREEN, Color.BLUE, Color.RED, Color.CYAN);
        mRefreshView.setDistanceToTriggerSync(20);// in dips
        mRefreshView.setSize(SwipeRefreshLayout.DEFAULT);
    }

    void initData() {
        mWorkThr = new HandlerThread(RedactActivity.class.getSimpleName());
        mWorkThr.start();
        mWorkHandler = new Handler(mWorkThr.getLooper());
    }

    private void reload() {
        if (!mRefreshView.isRefreshing()) {
            mRefreshView.setRefreshing(true);
            mLoadTask.reset();
            mWorkHandler.postDelayed(mLoadTask, 200);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWorkThr.quit();
    }

    LoadItemTask mLoadTask = new LoadItemTask();

    class LoadItemTask implements Runnable {
        private final int PAGE_SIZE = 16;
        int offset;

        public void reset() {
            offset = 0;
            dat.clear();
        }

        @Override
        public void run() {
            final List<Entry> more = new Select().from(Entry.class).limit(PAGE_SIZE).offset(offset).execute();
            if (more.size() > 0)
                offset += more.size();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRefreshView.setRefreshing(false);
                    if (more.size() > 0) {
                        dat.addAll(more);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    SwipeRefreshLayout.OnRefreshListener onRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mWorkHandler.postDelayed(mLoadTask, 200);
        }
    };
    View.OnClickListener onclick = new View.OnClickListener() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onClick(View v) {
            Entry item;
            int i = v.getId();
            if (i == R.id.save) {
                if (redactContent.getText().length() > 0) {
                    String contentStr = redactContent.getText().toString();
                    From from = new Select().from(Entry.class).where("content = ?", contentStr);
                    if (from.count() > 0) {
                        Toast.makeText(RedactActivity.this, R.string.duplicate, Toast.LENGTH_LONG).show();
                    } else {
                        new Entry(contentStr).save();
                        mWorkHandler.postDelayed(mLoadTask, 200);
                        Toast.makeText(RedactActivity.this, R.string.success, Toast.LENGTH_LONG).show();
                        redactContent.setText("");
                    }
                } else {
                    Toast.makeText(RedactActivity.this, R.string.nothing, Toast.LENGTH_LONG).show();
                }
            } else if (i == R.id.clear) {
                redactContent.setText("");
            } else if (i == R.id.finish) {
                finish();
            } else if (i == R.id.content) {
                item = (Entry) v.getTag();
                redactContent.setText(item.content);
                save.setVisibility(View.GONE);
                update.setVisibility(View.VISIBLE);
                update.setTag(item);
                delete.setVisibility(View.VISIBLE);
                delete.setTag(item);
                finish.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
            } else if (i == R.id.cancel) {
                save.setVisibility(View.VISIBLE);
                update.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                finish.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);
            } else if (i == R.id.update) {
                item = (Entry) v.getTag();
                if (redactContent.getText().length() > 0) {
                    String contentStr = redactContent.getText().toString();
                    new Update(Entry.class).set("content = ?", contentStr).where("id = ?", item.getId()).execute();
                    item.content = contentStr;
                    int pos = entries.getVerticalScrollbarPosition();
                    adapter.notifyDataSetChanged();
                    entries.setVerticalScrollbarPosition(pos);
                    Toast.makeText(RedactActivity.this, R.string.success, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RedactActivity.this, R.string.nothing, Toast.LENGTH_LONG).show();
                }
            } else if (i == R.id.delete) {
                item = (Entry) v.getTag();
                new Delete().from(Entry.class).where("id = ?", item.getId()).execute();
                dat.remove(item);
                int pos = entries.getVerticalScrollbarPosition();
                adapter.notifyDataSetChanged();
                entries.setVerticalScrollbarPosition(pos);
                Toast.makeText(RedactActivity.this, R.string.success, Toast.LENGTH_LONG).show();
            }
        }
    };

    class EntryAdapter extends RecyclerView.Adapter<ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            // create a new view
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.entries_item, viewGroup, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            vh.content.setOnClickListener(onclick);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            Entry item = dat.get(dat.size() - i - 1);
            viewHolder.content.setText(item.content);
            viewHolder.content.setTag(item);
        }

        @Override
        public int getItemCount() {
            return dat.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView content;

        public ViewHolder(View v) {
            super(v);
            content = (TextView) v.findViewById(R.id.content);
        }
    }
}
