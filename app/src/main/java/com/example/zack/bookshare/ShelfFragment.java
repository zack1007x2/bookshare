package com.example.zack.bookshare;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zack.BLE.BLEManager;
import com.example.zack.BLE.BLE_parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Zack on 15/6/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ShelfFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout LL_Device;
    private TextView tv_Name, tv_MAC, tvScan;
    private Handler mHandler;
    private BLEManager mBLEManager;
    private boolean isScanning, isSuccess;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private List<ScanFilter> filters;
    private BluetoothDevice mBluetoothDevice;

    private static final int REQUEST_ENABLE_BT = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shelf, container, false);
        init(view);

        return view;
    }

    private void init(View view) {
        LL_Device = (LinearLayout) view.findViewById(R.id.LL_Device_info);
        tv_Name = (TextView) view.findViewById(R.id.tvName);
        tv_MAC = (TextView) view.findViewById(R.id.tvMAC);
        tvScan = (TextView) view.findViewById(R.id.tvScan);
        tvScan.setOnClickListener(this);
        LL_Device.setOnClickListener(this);
        isScanning = false;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        mBLEManager = new BLEManager(this.getActivity());
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!mBLEManager.isSupportBLE(getActivity())) {
            Toast.makeText(getActivity(), "Not Support BLE", Toast.LENGTH_SHORT).show();
        }

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

    }

    public void onResume() {
        super.onResume();

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tvScan:
                if (isScanning) {
                    tvScan.setText("Scanning......");
                    tvScan.setEnabled(false);
                } else {
                    tvScan.setText("Scan");
                    Toast.makeText(getActivity(), "CLICK!", Toast.LENGTH_SHORT).show();
                    scanLeDevice(true);
                }
                break;
            case R.id.LL_Device_info:
                if (isSuccess) {
                    Intent intent = new Intent();
                    intent.putExtra("DeviceName", mBluetoothDevice.getName());
                    intent.putExtra("DeviceAddress", mBluetoothDevice.getAddress());
                    intent.putExtra("Device",mBluetoothDevice);
                    intent.setClass(getActivity(), LockControlActivity.class);
                    getActivity().startActivity(intent);

                }

                break;
        }

    }


    private void scanLeDevice(final boolean enable) {
        isSuccess = false;
        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings
                .SCAN_MODE_LOW_LATENCY).build();
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isScanning = false;

                    UUID device_uuid = BLE_parameter.CHARACTERISTIC_UUID;
                    mBluetoothLeScanner.stopScan(mLeScanCallback);
                }
            }, BLE_parameter.SCAN_PERIOD);

            isScanning = true;
            mBluetoothLeScanner.startScan(scanFilters(), settings, mLeScanCallback);
        } else {
            isScanning = false;
            mBluetoothLeScanner.stopScan(mLeScanCallback);
        }
    }

    // Device scan callback.
    @SuppressLint("NewApi")
    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            mBluetoothDevice = result.getDevice();
            tv_MAC.setText(mBluetoothDevice.getAddress());
            tv_Name.setText(mBluetoothDevice.getName());
            isSuccess = true;
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public String toString() {
            return super.toString();
        }
    };


    private List<ScanFilter> scanFilters() {
        ScanFilter filter = new ScanFilter.Builder().setDeviceAddress(BLE_parameter.Mac_Address)
                .setServiceUuid(new ParcelUuid(BLE_parameter.Service_UUID)).build();
        List<ScanFilter> list = new ArrayList<ScanFilter>(1);
        list.add(filter);
        return list;
    }
}
