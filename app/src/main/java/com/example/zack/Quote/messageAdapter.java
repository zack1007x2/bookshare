package com.example.zack.Quote;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zack.bookshare.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zack on 15/6/8.
 */
public class messageAdapter extends BaseAdapter {

    private List<messageData> messageList = new ArrayList<messageData>();
    private Context context;
    private messageData messagedata;

    public messageAdapter(Context context) {
        this.context = context;
    }

    public void RefreshList(List<messageData> list) {
        this.messageList.clear();
        this.messageList.addAll(list);
        this.notifyDataSetChanged();
    }

    public void sethomeList(List<messageData> messageList){
        this.messageList = messageList;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.message, null);
            viewHolder = new ViewHolder();
            viewHolder.tvContent_title = (TextView) convertView
                    .findViewById(R.id.tvContent_title);
//            viewHolder.ImgContent = (ImageView) convertView
//                    .findViewById(R.id.ImgContent);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        messagedata = messageList.get(position);

        viewHolder.tvContent_title.setText(messagedata.getTitle());
//        viewHolder.ImgContent.setImageResource(R.drawable.demo);

        return convertView;
    }

    private class ViewHolder {
        TextView tvContent_title;
        ImageView ImgContent;
    }
}
