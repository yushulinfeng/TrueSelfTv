package org.out.yslf.trueselftv.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by YuShuLinFeng on 2016/8/13.
 */
public abstract class EasyListAdapter<HOLDER, MODEL> extends BaseAdapter {
    protected Context context = null;
    protected List<MODEL> items = null;

    public EasyListAdapter(Context context, List<MODEL> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        if (items == null) return 0;
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        if (items == null) return null;
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        int itemRes = getLayoutRes();
        if (itemRes == 0) return view;
        if (view == null) { ////// 暂时使用这种优化方式
            view = LayoutInflater.from(context).inflate(itemRes, null);
        }
        getView(getViewHolder(view), position);//目前感觉还不错
        return view;
    }

    private HOLDER getViewHolder(View rootView) {
        Object tag = rootView.getTag();
        if (tag != null) {
            try {
                //noinspection unchecked
                return (HOLDER) tag;
            } catch (Exception ignore) {
                // 继续向下执行
            }
        }
        HOLDER holder = getNewHolder(rootView);
        if (holder != null)
            rootView.setTag(holder);
        return holder;
    }

    protected abstract HOLDER getNewHolder(View rootView);

    protected abstract int getLayoutRes();

    protected abstract void getView(HOLDER holder, int position);
}
