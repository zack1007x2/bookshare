package com.example.zack.bookshare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.View;

import com.example.zack.bookshare.Util.MyLog;


public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private FragmentManager fragmentManager;
    private Fragment mFragment;
    private FragmentTransaction fragmentTransaction;
    private SparseArray<BaseFragment> navigateMap = new SparseArray<BaseFragment>();
    private MyLog Log = MyLog.log();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFragment();
        Log.d("onCreate");

    }

    private void initFragment() {

        fragmentManager = getSupportFragmentManager();

        navigateMap.clear();
        mapNaviToFragment(R.id.tab_shelf, new ShelfFragment());
        mapNaviToFragment(R.id.tab_wish, new WishListFragmnet());
        mapNaviToFragment(R.id.tab_readcode, new ReadISBNFragment());
        mapNaviToFragment(R.id.tab_explore, new ExploreFragment());
        mapNaviToFragment(R.id.tab_timeline, new TimelineFragment());

        hideorshow(fragmentManager, R.id.tab_shelf);

    }


    private void mapNaviToFragment(int id, BaseFragment fragment) {
        View view = findViewById(id);

        view.setOnClickListener(this);
        view.setSelected(false);
        navigateMap.put(id, fragment);
    }

    private void hideorshow(FragmentManager fm, int id) {
        Log.i("replaceFragment EntryCount: " + fm.getBackStackEntryCount()
                + " id: " + id);
        String tag = String.valueOf(id);
        fragmentTransaction = fm.beginTransaction();
        if (null == fm.findFragmentByTag(tag)) {
            fragmentTransaction.replace(R.id.contentframe, navigateMap.get(id), tag);
        } else {
            fragmentTransaction.show(navigateMap.get(id));
        }
        fragmentTransaction.commit();
        for (int i = 0, size = navigateMap.size(); i < size; i++) {
            int curId = navigateMap.keyAt(i);
            if (curId == id) {
                mFragment = navigateMap.get(id);
                findViewById(id).setSelected(true);
            } else {
                findViewById(curId).setSelected(false);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (navigateMap.indexOfKey(id) >= 0) {

            if (!view.isSelected()) {
                hideorshow(getSupportFragmentManager(), id);
            } else {
                Log.i(" ignore --- selected !!! ");
            }
        }
    }
}
