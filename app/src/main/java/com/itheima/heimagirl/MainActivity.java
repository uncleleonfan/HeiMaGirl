package com.itheima.heimagirl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Gson mGson = new Gson();
    private List<ResultBean.Result> mDataList = new ArrayList<ResultBean.Result>();

    @BindView(R.id.list_view)
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        sendSyncRequest();
        sendAsyncRequest();

        initView();
    }

    private void initView() {
        mListView.setAdapter(mBaseAdapter);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == mDataList.size() - 1) {
                        loadMoreData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void loadMoreData() {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = "http://gank.io/api/data/福利/10/" + mDataList.size() / 10 + 1;
        Request request = new Request.Builder().get().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.d(TAG, "onResponse: " + result);
                ResultBean resultBean = mGson.fromJson(result, ResultBean.class);
                mDataList.addAll(resultBean.results);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBaseAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private BaseAdapter mBaseAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.view_list_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mPublish.setText(mDataList.get(position).publishedAt);
            Glide.with(MainActivity.this).load(mDataList.get(position).url).into(viewHolder.mImage);
            return convertView;
        }
    };

    private class ViewHolder {
        ImageView mImage;
        TextView mPublish;

        public ViewHolder(View root) {
            mImage = (ImageView) root.findViewById(R.id.image);
            mPublish = (TextView) root.findViewById(R.id.publish);
        }
    }

    private void sendAsyncRequest() {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = "http://gank.io/api/data/福利/10/1";
        Request request = new Request.Builder().get().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.d(TAG, "onResponse: " + result);
                ResultBean resultBean = mGson.fromJson(result, ResultBean.class);
                mDataList.addAll(resultBean.results);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBaseAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void sendSyncRequest() {
        //同步网络请求需要在子线程中执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                String url = "http://gank.io/api/data/福利/10/1";
                Request request = new Request.Builder().get().url(url).build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    String result = response.body().string();
                    //注意response.body().string()不能执行两次
                    //Log.d(TAG, "loadData: " + response.body().string());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
