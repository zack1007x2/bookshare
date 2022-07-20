// ISocketMessageListener.aidl
package com.example.zack.ui;

import com.example.zack.ui.bean.SocketMessage;

// Declare any non-default types here with import statements

interface ISocketMessageListener {

    void updateMessageList(out SocketMessage message);
}