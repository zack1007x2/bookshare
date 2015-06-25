package com.example.zack.novel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.zack.bookshare.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zack on 15/6/8.
 */
public class articleAdapter extends BaseAdapter {

    private List<String> articleList = new ArrayList<String>();
    private Context context;
    private String atricledata;

    public articleAdapter(Context context) {
        this.context = context;
    }

    public void RefreshList(List<String> list) {
        this.articleList.clear();
        this.articleList.addAll(list);
        this.notifyDataSetChanged();
    }

    public void setArticleList(List<String> articleList){
        this.articleList = articleList;
    }

    @Override
    public int getCount() {
        return articleList.size();
    }

    @Override
    public Object getItem(int position) {
        return articleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_article_content, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_segment = (TextView) convertView
                    .findViewById(R.id.tv_segment);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        atricledata = articleList.get(position);

        viewHolder.tv_segment.setText(atricledata);

        return convertView;
    }

    private class ViewHolder {
        TextView tv_segment;
    }
}
