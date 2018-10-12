package com.zqf.androiduiproject.view.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerViewUtil {
    private boolean mLoadMoreEnable = false;

    private RecyclerView.LayoutManager mLayoutManager;
    private final RecyclerView mRecyclerView;
    private GestureDetector mGestureDetector;
    private RecyclerView.SimpleOnItemTouchListener mSimpleOnItemTouchListener;
    private OnLoadMoreListener mOnLoadMoreListener;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public RecyclerViewUtil(Context context, RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager){
        mRecyclerView = recyclerView;
        mLayoutManager = layoutManager;
        mRecyclerView.setLayoutManager(layoutManager);
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                if (mOnItemLongClickListener != null) {
                    View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (childView != null){
                        int position = mRecyclerView.getChildLayoutPosition(childView);
                        mOnItemLongClickListener.onItemLongClick(position, childView);
                    }
                }
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (mOnItemClickListener != null) {
                    View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (childView != null) {
                        int position = mRecyclerView.getChildLayoutPosition(childView);
                        mOnItemClickListener.onItemClick(position, childView);
                        return true;
                    }
                }
                return super.onSingleTapUp(e);
            }
        });
        mSimpleOnItemTouchListener = new RecyclerView.SimpleOnItemTouchListener(){
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if (mGestureDetector.onTouchEvent(e)){
                    return true;
                }
                return false;
            }
        };
        recyclerView.addOnItemTouchListener(mSimpleOnItemTouchListener);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        mOnItemLongClickListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener){
        mOnLoadMoreListener = listener;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && isBottom()) {
                    if (mOnLoadMoreListener != null&&mLoadMoreEnable) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                }
            }
        });
    }

    public void setAdapter(RecyclerView.Adapter adapter){
        mRecyclerView.setAdapter(adapter);
    }

    public boolean isLoadMoreEnable() {
        return mLoadMoreEnable;
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        this.mLoadMoreEnable = loadMoreEnable;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private boolean isBottom(){
        if (mRecyclerView == null)
            return false;
        if(mLayoutManager.canScrollVertically()){
            return mRecyclerView.computeVerticalScrollExtent() + mRecyclerView.computeVerticalScrollOffset() >= mRecyclerView.computeVerticalScrollRange();
        }else if (mLayoutManager.canScrollHorizontally()){
            return mRecyclerView.computeHorizontalScrollExtent() + mRecyclerView.computeHorizontalScrollOffset() >= mRecyclerView.computeHorizontalScrollRange();
        }
        return false;
    }

    public interface OnLoadMoreListener{
        void onLoadMore();
    }

    public interface OnItemClickListener{
        void onItemClick(int position,View view);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(int position,View view);
    }

}
