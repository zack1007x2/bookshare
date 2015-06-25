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
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Zack on 15/6/16.
 */
public class WishListFragmnet extends BaseFragment {
    String filePath;
    TextView tvContent;
    ScrollView SV;
    String str;
    ProgressDialog pd;
    StringBuilder sb = new StringBuilder();
    int counter;
//    public static int scrollX = 0;
//    public static int scrollY = -1;

    Handler mHandler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    tvContent.setText(sb);
                    break;
            }

        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        tvContent = (TextView) view.findViewById(R.id.tvContent);
        pd = new ProgressDialog(getActivity());
        pd.setMessage("載入中");
        pd.setCancelable(false);
        SV = (ScrollView)view.findViewById(R.id.scrollView);
        new DownloadFilesTask().execute();
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                read();
//            }
//        }).start();


        return view;
    }

//    @Override
//    public void onPause()
//    {
//        super.onPause();
//        scrollX = SV.getScrollX();
//        scrollY = SV.getScrollY();
//    }
//
//    @Override
//    public void onResume()
//    {
//        super.onResume();
////this is important. scrollTo doesn't work in main thread.
//        SV.post(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                SV.scrollTo(scrollX, scrollY);
//            }
//        });
//    }


    public void read() {
            filePath = Environment.getExternalStorageDirectory().getPath()+"/novel_test";
            File f= new File(filePath);
            if(f.isDirectory()) {
                Log.d("Zack", filePath);
            }
            int numRead = 0;
            counter =0;


                //Open input stream for reading the text file MyTextFile.txt


            try {

                InputStream is = new FileInputStream(filePath+"/data.txt");

                // create new input stream reader
                InputStreamReader instrm = new InputStreamReader(is,"UTF-8");

                // Create the object of BufferedReader object
                BufferedReader bufferedReader = new BufferedReader(instrm);
                str=null;
                if (bufferedReader!=null) {
                    if (bufferedReader.ready()) {

                        try {
                            while ((numRead = bufferedReader.read()) >= 0) {

                                //convert asci to char and then to string
                                str = String.valueOf((char) numRead);

                                if ((str != null)&& (str.toString() != "")) {
                                    counter++;
                                    sb.append(str);

//                                    synchronized(str) {
//                                        this.getActivity().runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                tvContent.append(str);
//                                            }
//                                        });
//                                    }


                                }

                                if (!bufferedReader.ready()){
                                    //no more characters to read
                                    break;
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //loop exited, check for null
                        if (sb != null) {
//                            Log.i("Zack", sb.toString());
//                            tvContent.setText(sb.toString());
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
//            tvContent.setText(sb.toString());


    }


    private class DownloadFilesTask extends AsyncTask<Void, Void, String> {

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
            Log.d("Zack", "onPostExecute + "+counter);
            mHandler.sendEmptyMessage(1);
            pd.dismiss();

        }

    }

}
