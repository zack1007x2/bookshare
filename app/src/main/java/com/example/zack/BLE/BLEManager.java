package com.example.zack.BLE;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by Zack on 15/6/18.
 */
public class BLEManager extends BTManager {


    public BLEManager(Context context) {
        super(context);
    }

    public boolean isSupportBLE(Context context) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }else{
            return true;
        }
    }
}
