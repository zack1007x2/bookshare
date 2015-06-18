package com.example.zack.bookshare;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.zack.Quote.messageAdapter;
import com.example.zack.Quote.messageData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zack on 15/6/16.
 */
public class ExploreFragment extends BaseFragment implements
        android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener {

    private ListView lvMessage;
    private ProgressBar progressbar;
    private SwipeRefreshLayout swipe;
    private CountDownTimer timer;
    private messageAdapter adapter;
    private List<messageData> messageList = new ArrayList<messageData>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        //生假資料
        if(messageList.isEmpty()) {
            for (int i = 0; i < 10; i++) {
                messageData mData = new messageData();
                mData.setTitle("Title" + i);
                messageList.add(mData);
            }
        }
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        lvMessage = (ListView) view.findViewById(R.id.lvMessage);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        swipe.setOnRefreshListener(this);
        // 頂部刷新的樣式
        swipe.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);
        if (messageList.size() > 0) {
            adapter = new messageAdapter(getActivity());
            adapter.sethomeList(messageList);
            lvMessage.setAdapter(adapter);
        } else {
            progressbar.setVisibility(View.VISIBLE);
        }

        return view;
    }


    public void onPause(){
        super.onPause();
        messageList.clear();
    }

    @Override
    public void onRefresh() {

    }
}
