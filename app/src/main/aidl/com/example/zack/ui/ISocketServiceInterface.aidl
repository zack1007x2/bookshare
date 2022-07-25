// ISocketServiceInterface.aidl
package com.example.zack.ui;

// Declare any non-default types here with import statements
import com.example.zack.ui.ISocketMessageListener;
import com.example.zack.ui.bean.SocketMessage;

interface ISocketServiceInterface {
    void connectSocket();
    void disConnectSocket();
    void sendMessage(in SocketMessage message);
    void addMessageListener(ISocketMessageListener listener);
    void removeMessageListener(ISocketMessageListener listener);
}