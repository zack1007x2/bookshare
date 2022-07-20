// ISocketServiceInterface.aidl
package com.example.zack.ui;

// Declare any non-default types here with import statements
import com.example.zack.ui.ISocketMessageListener;
import com.example.zack.ui.bean.SocketMessage;

interface ISocketServiceInterface {

    //连接Socket
    void connectSocket();
    //断开连接
    void disConnectSocket();
    //发送消息到服务器
    void sendMessage(in SocketMessage message);
    //添加消息监听
    void addMessageListener(ISocketMessageListener listener);
    //取消监听
    void removeMessageListener(ISocketMessageListener listener);
}