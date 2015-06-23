package com.example.zack.bookshare;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.zack.BLE.BLE_parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zack on 15/6/18.
 */
public class LockControlActivity extends Activity {

    private String mDeviceName, mDeviceAddress;
    private BluetoothDevice mBluetoothDevice;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static String RSSI_DATA =
            "com.example.bluetooth.le.RSSI_DATA";

    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;


    private BluetoothGattService mBluetoothGattService;
    private BluetoothGatt mBluetoothGatt;

    List<BluetoothGattService> gattService = new ArrayList<BluetoothGattService>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mDeviceName = intent.getStringExtra("DeviceName");
        mDeviceAddress = intent.getStringExtra("DeviceAddress");
        //gatt
        mBluetoothDevice = intent.getParcelableExtra("Device");

        if(mBluetoothDevice==null){
            Log.d("Zack","mBluetoothDevice IS NULL");
        }
        mBluetoothGatt = mBluetoothDevice.connectGatt(this, false, mgattCallBack);

//        gattService = mBluetoothGatt.getServices();


    }


    private BluetoothGattCallback mgattCallBack = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
//                broadcastUpdate(intentAction);
                Log.i("Zack", "Connected to GATT server.");
                Log.i("Zack", "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i("Zack", "Disconnected from GATT server.");
//                broadcastUpdate(intentAction);
            }
        }

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("Zack", "GATT_SUCCESS");
                mBluetoothGattService = mBluetoothGatt.getService(BLE_parameter.Service_UUID);
                if(mBluetoothGattService!=null){
                    Log.d("Zack", "Service CATCH"+mBluetoothGattService.getUuid());
                }
                byte[] aa =  new byte[1];
                aa[0] = BLE_parameter.LOCK_CMD_BYTES;
                mBluetoothGattService.getCharacteristic(BLE_parameter.CHARACTERISTIC_UUID)
                        .setValue(aa);

                mBluetoothGatt.writeCharacteristic(mBluetoothGattService.getCharacteristic(BLE_parameter.CHARACTERISTIC_UUID));


//                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.i("Zack", "onServicesDiscovered received: " + status);
            }
        }

        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                Log.i("Zack", "onCharacteristicRead Success");
            }
        }
    };
}
