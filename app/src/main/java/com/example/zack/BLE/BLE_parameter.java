package com.example.zack.BLE;

import java.util.UUID;

/**
 * Created by Zack on 15/6/18.
 */
public class BLE_parameter {

    public static final String Mac_Address = "5C:31:3E:5A:8C:89";
    public static final UUID Service_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public static final UUID CHARACTERISTIC_UUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    public static final int LOCK_CMD_BYTES = 0x00;
    public static final int UNLOCK_CMD_BYTES = 0x01;

    public static final long SCAN_PERIOD = 10000;
}
