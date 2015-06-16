package com.example.zack.bookshare;

import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.widget.TextView;

/**
 * Created by Zack on 15/6/16.
 */
public class BaseFragment extends Fragment {

    public void update(String data, int id){
        ((TextView)this.getView().findViewById(id)).setText(data);

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

}
