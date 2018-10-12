package com.zqf.androiduiproject.view;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zqf.androiduiproject.R;
import com.zqf.androiduiproject.view.util.RecyclerViewUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {

    private RecyclerView.Adapter adapter;
    private RecyclerViewUtil recyclerViewUtil;
    private List<News> data=new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.loading);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.GONE);

        for(int i=0;i<10;i++){
            News news = new News();
            news.title = "title"+i;
            news.content = "this is the "+i+"th content.";
            data.add(news);
        }

        adapter = new RecyclerAdapter();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerViewUtil = new RecyclerViewUtil(this,recyclerView,layoutManager);
        recyclerViewUtil.setAdapter(adapter);

        //设置分隔线
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerViewUtil.setOnItemClickListener(new RecyclerViewUtil.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Toast.makeText(MainActivity.this,"click "+position,Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewUtil.setOnItemLongClickListener(new RecyclerViewUtil.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position, View view) {
                if(data.size()>position){
                    data.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }
        });
        recyclerViewUtil.setLoadMoreEnable(true);
        recyclerViewUtil.setOnLoadMoreListener(new RecyclerViewUtil.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Toast.makeText(MainActivity.this,"已经到底，加载更多....",Toast.LENGTH_SHORT).show();
                recyclerViewUtil.setLoadMoreEnable(false);
                loadNews();
            }
        });
    }

    public void add(View view){
        Date date = new Date();
        News news = new News();
        news.title = "new title";
        news.content = "Added time:"+date.toString();
        data.add(news);
        adapter.notifyItemInserted(data.size()-1);
    }

    public void del(View view){
        if(data.size()>0){
            data.remove(0);
            adapter.notifyItemRemoved(0);
        }
    }

    private void loadNews(){
        new LoadNewsAsyncTask().execute();
//        for(int i=0;i<5;i++){
//            News news = new News();
//            news.title = "load more "+i;
//            news.content = "this is the "+i+"th content.";
//            data.add(news);
//        }
//        adapter.notifyDataSetChanged();
//        recyclerViewUtil.setLoadMoreEnable(true);
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.recycler_item_layout,parent,false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.titleTv.setText(data.get(position).title);
            holder.contentTv.setText(data.get(position).content);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            TextView titleTv;
            TextView contentTv;

            public MyViewHolder(View itemView) {
                super(itemView);
                titleTv = itemView.findViewById(R.id.title);
                contentTv = itemView.findViewById(R.id.content);
            }
        }

    }

    class LoadNewsAsyncTask extends AsyncTask<Void,Integer,Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {

                for(int i=0;i<5;i++){
                    Thread.sleep(500);
                    News news = new News();
                    news.title = "load more "+i;
                    news.content = "this is the "+i+"th content.";
                    onProgressUpdate(i*100/4);
                    data.add(news);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        recyclerViewUtil.setLoadMoreEnable(true);
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.i("AsynctaskValues:",values[0]+"");
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressBar.setVisibility(View.GONE);
        }
    }

    class News{
        public String title;
        public String content;
    }

}
