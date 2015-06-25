package com.example.zack.bookshare;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.zack.novel.articleAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zack on 15/6/16.
 */
public class TimelineFragment extends BaseFragment {

    ListView lvNovel;
    String filePath;
    private articleAdapter adapter;
    String str;
    ProgressDialog pd;
    StringBuilder sb = new StringBuilder();
    private List<String> articleList = new ArrayList<String>();
    private articleAdapter mArticleAdapter;
    private int counter;
    private int ABC;
    private boolean isNextLine;

    Handler mHandler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
//                    lvNovel.notify();
                    break;
                case 2:

                    break;
            }

        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        lvNovel = (ListView)view.findViewById(R.id.lv_novel);
        pd = new ProgressDialog(getActivity());
        pd.setMessage("載入中");
        pd.setCancelable(false);
        lvNovel.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("onScroll","firstVisibleItem = " +firstVisibleItem+"visibleItemCount = " +
                        visibleItemCount+"totalItemCount ="+ totalItemCount);

            }
        });
        lvNovel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lvNovel.setSelection(236);
//                Log.d("getSelectedItemPosition", "Position = "+lvNovel.getChildCount());
            }
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        mArticleAdapter = new articleAdapter(getActivity());
        new ReadFilesTask().execute();

    }



    private class ReadFilesTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            Log.d("Zack", "doInBackground");
            read();
            return "";
        }
        @Override
        protected  void onPreExecute(){
            pd.show();

        }
        @Override
        protected void onPostExecute(String result){
            mArticleAdapter.setArticleList(articleList);
            lvNovel.setAdapter(mArticleAdapter);
//            mHandler.sendEmptyMessage(2);
            pd.dismiss();

        }

    }

    public void read() {
        filePath = Environment.getExternalStorageDirectory().getPath()+"/novel_test";
        File f= new File(filePath);
        if(f.isDirectory()) {
            Log.d("Zack", filePath);
        }
        int numRead = 0;
        int counter =0;

        try {

            InputStream is = new FileInputStream(filePath+"/data.txt");

            InputStreamReader instrm = new InputStreamReader(is,"UTF-8");

            BufferedReader bufferedReader = new BufferedReader(instrm);
            str=null;
            if (bufferedReader!=null) {
                if (bufferedReader.ready()) {

                    try {

                        String thisline = null;
                        while((thisline = bufferedReader.readLine())!=null){
                            articleList.add(thisline);
                            if(thisline.contains("第") && thisline.contains("章") && thisline
                                    .length()<20){
                                Log.d("Zack",thisline);
                            }
                            if (!bufferedReader.ready()){
                                //no more characters to read
                                break;
                            }
                        }

//                        while ((numRead = bufferedReader.read()) >= 0) {
//                            isNextLine =((char) numRead=='\n');
//
//                            str = String.valueOf((char) numRead);
//
//                            if ((str != null)&& (str.toString() != "")) {
//                                sb.append(str);
//                                counter++;
//                                if(counter>20){
//                                    if(isNextLine) {
//                                        ABC++;
//                                        articleList.add(sb.toString());
//                                        sb.setLength(0);
//                                        counter = 0;
//                                    }
//                                }
//                            }
//
//                            if (!bufferedReader.ready()){
//                                //no more characters to read
//                                ABC++;
//                                articleList.add(sb.toString());
//                                break;
//                            }
//                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (sb != null) {
//                            Log.i("Zack", sb.toString());
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
