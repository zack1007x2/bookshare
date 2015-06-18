package com.example.zack.BLE;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Zack on 15/6/18.
 */
public class BTManager {

    private BluetoothAdapter adapter;
    private Context context;
    private BluetoothListener listener;
    private List<BluetoothDevice> devices;
    private BluetoothSocket socket;
    public BTManager(Context context){
        adapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
        devices = new ArrayList<BluetoothDevice>();
    }

    public void setListener(BluetoothListener listener) {
        this.listener = listener;
    }
    /**
     * 打開藍牙
     */
    public void openBluetooth(){
        if (adapter == null){
            Toast.makeText(context, "不支援藍牙！", Toast.LENGTH_SHORT).show();
        }
        if (!adapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            context.startActivity(intent);
//            adapter.enable();
        }
    }
    /**
     * 獲取已經配對的藍牙設備
     * @return
     */
    public Set<BluetoothDevice> getPairedBluetooth(){
        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        return devices;
    }
    /**
     * 搜索周圍的藍牙設備
     */
    public void seachBluetooth(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(receiver, intentFilter);
        adapter.startDiscovery();
    }
    /**
     * 連接到指定的藍牙設配
     * @param device
     * @throws Exception
     */
    public void connectBluetooth(BluetoothDevice device,String SPP_UUID) throws Exception{
        UUID uuid = UUID.fromString(SPP_UUID);
        socket = device.createRfcommSocketToServiceRecord(uuid);
        socket.connect();
    }
    private BroadcastReceiver receiver = new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devices.add(device);
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){ //搜索完成
                listener.seach(devices);
            }
        }
    };
    public interface BluetoothListener{
        public void seach(List<BluetoothDevice> devices);
    }
    /**
     * 關閉本機的藍牙
     */
    public void closeBluetooth(){
        if(adapter != null && adapter.isEnabled())
            adapter.disable();
    }
    /**
     * 發送數據到指定藍牙設配
     * @param data
     * @throws IOException
     */
    public void sendDataToBluetooth(byte[] data) throws IOException {
        if(socket != null){
            OutputStream out = socket.getOutputStream();
            out.write(data);
        }else{
            throw new NullPointerException("目標藍牙未連接...");
        }
    }
}
