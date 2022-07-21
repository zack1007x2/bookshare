package com.example.zack.core;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.zack.Util.MyLog;
import com.example.zack.common.Custom;
import com.example.zack.common.SocketServiceSP;
import com.example.zack.common.Util;
import com.example.zack.ui.ISocketMessageListener;
import com.example.zack.ui.ISocketServiceInterface;
import com.example.zack.ui.MainActivity;
import com.example.zack.ui.R;
import com.example.zack.ui.bean.SocketMessage;

import java.lang.ref.WeakReference;
import java.net.URISyntaxException;

import androidx.core.app.NotificationCompat;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by HouBin on 2017/3/14.
 * 与服务器保持长连接的Service
 */

public class SocketService extends Service {
    //Service实例，用于在Activity中进行连接断开发消息等图形界面化的操作

    //Socket的弱引用
    private WeakReference<Socket> mSocket;

    //消息发出的时间（不管是心跳包还是普通消息，发送完就会跟新时间）
    private long sendTime = 0;


    private final int MSG_WHAT_CONNECT = 111; //连接Socket
    private final int MSG_WHAT_DISCONNECT = 112;//断开Socket
    private final int MSG_WHAT_SENDMESSAGE = 113;//发送消息

    private MyLog Log = MyLog.log();
    /**
     * 处理Socket的连接断开发消息的Handler机制
     */
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WHAT_CONNECT:
                    if (isServerClose()) {
                        connectSocket();
                    } else {
                        Toast.makeText(SocketService.this, "Socket已经连接上了", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MSG_WHAT_DISCONNECT:
                    if (isServerClose())
                        Toast.makeText(SocketService.this, "Socket已经断开了", Toast.LENGTH_SHORT).show();
                    else
                        interruptSocket();
                    break;
                case MSG_WHAT_SENDMESSAGE:
                    if (isServerClose()) {
                        Toast.makeText(SocketService.this, "请先连接Socket", Toast.LENGTH_SHORT).show();
                    } else {
                        SocketMessage socketMessage = (SocketMessage) msg.obj;
                        try {
                            SocketService.this.sendMessage(socketMessage);
                        } catch (RemoteException e) {
                            Log.e(e);
                        }
                    }
                    break;
            }
        }
    };

    //读取服务器端发来的消息的线程
//    private ReadThread mReadThread;

    //监控服务被杀死重启的广播，保持服务不被杀死
    private BroadcastReceiver restartBR;

    public SocketService() {
        super();
    }

    /**
     * 创建Service的同时要创建Socket
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("onCreate()");
        SocketServiceSP.getInstance(this).saveSocketServiceStatus(true);//保存了Service的开启状态
        //收到Service被杀死的广播，立即重启
        restartBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("SocketServer重启了......");
                String action = intent.getAction();
                if (!TextUtils.isEmpty(action) && action.equals("socketService_killed")) ;
                Intent sIntent = new Intent(SocketService.this, SocketService.class);
                startService(sIntent);
                SocketServiceSP.getInstance(SocketService.this).saveSocketServiceStatus(true);//保存了Service的开启状态
            }
        };
        registerReceiver(restartBR, new IntentFilter("socketService_killed"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("onStartCommand(Intent intent, int flags, int startId)");
//        return super.onStartCommand(intent, flags, startId);

        if(isServerClose())
            connectSocket();

        sendNormalNotification("Sotera");
        startForeground(1, fore);
        return START_STICKY;//设置START_STICKY为了使服务被意外杀死后可以重启
    }


    private Emitter.Listener onConnect = args -> {
        Log.d("connected...");

    };

    private void restartSocket(){
        new Handler().postDelayed(() -> {
            if(!mSocket.get().connected()){
                mSocket.get().connect();
            }
        },5000);
    }

    private Emitter.Listener onConnectError = args -> Log.d("Error onConnectError...");

    private Emitter.Listener onDisconnect = args -> {
        restartSocket();
        Log.d("Error onDisconnect...");
    };
    private Emitter.Listener onMessage = args -> {
        String a = "";
        for (Object k:args) {
            a += k.toString();
        }
        sendNormalNotification(a);
    };

    /**
     * 客户端通过Socket与服务端建立连接
     */
    public void connectSocket() {
        new Thread(() -> {
//            URI uri = URI.create("http://"+Custom.SERVER_HOST+":"+Custom.SERVER_PORT);
//            Log.d(uri.toString());
            Socket socket = null;
            try {
                socket = IO.socket("http://10.40.246.23:9000");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel channel = new NotificationChannel(
                        "zack", "DemoCode", NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager manager = getSystemService(NotificationManager.class);
                assert manager != null;
                manager.createNotificationChannel(channel);
            }
            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            socket.on("message", onMessage);
            socket.connect();
            Log.d("Socket连接成功。。。。。。");
            socket.send("hello");
            socket.emit("message", "hello");
            //                Socket socket = new Socket(InetAddress.getByName(Custom.SERVER_HOST), Custom.SERVER_PORT);
//                socket.setSoTimeout(Custom.SOCKET_CONNECT_TIMEOUT * 1000);

            mSocket = new WeakReference<>(socket);
//            mReadThread = new ReadThread(socket);
//            mReadThread.start();
            mHandler.postDelayed(activeRunnable, Custom.SOCKET_ACTIVE_TIME * 1000);//开启定时器，定时发送心跳包，保持长连接
        }).start();
    }

    /**
     * 发送心跳包的任务
     */
    private Runnable activeRunnable = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - sendTime >= Custom.SOCKET_ACTIVE_TIME * 1000) {
                SocketMessage message = new SocketMessage();
                message.setType(Custom.MESSAGE_ACTIVE);
                message.setMessage("");
                message.setFrom(Custom.NAME_CLIENT);
                message.setTo(Custom.NAME_SERVER);
                try {
                    if (!sendMessage(message)) {
//                        if (mReadThread != null)
//                            mReadThread.release();
                        releaseLastSocket(mSocket);
                        connectSocket();
                    }
                } catch (RemoteException e) {
                    Log.e(e);
                }
            }
            mHandler.postDelayed(this, Custom.SOCKET_ACTIVE_TIME * 1000);
        }
    };

    /**
     * 发送消息到服务端
     *
     * @param message
     * @return
     */
    public boolean sendMessage(SocketMessage message) throws RemoteException {
        message.setUserId("001");
        if (mSocket == null || mSocket.get() == null) {
            return false;
        }
        Socket socket = mSocket.get();
        socket.send(Util.initJsonObject(message).toString());
//        try {
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//            if (!socket.isClosed()) {
//                String jMessage = Util.initJsonObject(message).toString() + "\n";
//                writer.write(jMessage);
//                writer.flush();
//                Log.e(TAG, "发送消息：" + jMessage);
//                sendTime = System.currentTimeMillis();//每次发送成数据，就改一下最后成功发送的时间，节省心跳间隔时间 
//                if (message.getType() == Custom.MESSAGE_EVENT) {//通知实现了消息监听器的界面，让其跟新消息列表
//                    messageListener.updateMessageList(message);
//                }
//            } else {
//                return false;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
        return true;
    }

    /**
     * 读取消息的线程
     */
//    class ReadThread extends Thread {
//        private WeakReference<JWebSocketClient> mReadSocket;
//        private boolean isStart = true;
//
//        public ReadThread(JWebSocketClient socket) {
//            mReadSocket = new WeakReference<JWebSocketClient>(socket);
//        }
//
//        public void release() {
//            isStart = false;
//            releaseLastSocket(mReadSocket);
//        }
//
//        @Override
//        public void run() {
//            super.run();
//            JWebSocketClient socket = mReadSocket.get();
//            if (socket != null && !socket.isClosed()) {
//                try {
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    while (isStart) {
//                        if (reader.ready()) {
//                            String message = reader.readLine();
//                            Log.e(TAG, "收到消息：" + message);
//                            SocketMessage sMessage = Util.parseJson(message);
//                            if (sMessage.getType() == Custom.MESSAGE_ACTIVE) {//处理心跳回执
//
//                            } else if (sMessage.getType() == Custom.MESSAGE_EVENT) {//事件消息
//                                if (messageListener != null)
//                                    messageListener.updateMessageList(sMessage);
//                                sendNotification(sMessage);
//                            } else if (sMessage.getType() == Custom.MESSAGE_CLOSE) {//断开连接消息回执
//                                mHandler.removeCallbacks(activeRunnable);
//                                release();
//                                releaseLastSocket(mSocket);
//                            }
//                        }
//                        Thread.sleep(100);//每隔0.1秒读取一次，节省点资源
//                    }
//                } catch (IOException e) {
//                    release();
//                    releaseLastSocket(mSocket);
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


    //通知的ID，为了分开显示，需要根据Id区分
    private int nId = 2;

    /**
     * 收到时间消息，发送通知提醒
     *
     * @param sMessage
     */
    private void sendNotification(SocketMessage sMessage) {
        //为了版本兼容，使用v7包的ＢＵＩＬＤＥＲ
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        //状态栏显示的提示，有的手机不显示
        builder.setTicker("简单的Notification");
        //通知栏标题
        builder.setContentTitle("from" + sMessage.getFrom() + "的消息");
        //通知栏内容
        builder.setContentText(sMessage.getMessage());
        //通知内容摘要
        builder.setSubText(sMessage.getUserId());
        //在通知右侧的时间下面用来展示一些其他信息
//        builder.setContentInfo("其他");
        //用来显示同种通知的数量，如果设置了ContentInfo属性，则NUmber属性会被覆盖，因为二者显示的位置相同
//        builder.setNumber(3);
        //可以点击通知栏的删除按钮
        builder.setAutoCancel(true);
        //系统状态栏显示的小图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //通知下拉显示的大图标
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        //点击通知跳转的INTENT
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);
        builder.setContentIntent(pendingIntent);//点击跳转
        //通知默认的声音，震动，呼吸灯
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(nId, notification);
        nId++;
    }
    Notification fore =null;
    private void sendNormalNotification(String message) {
        Log.e("sendNormalNotification");
        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(SocketService.this,"zack")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Sotera！")
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        fore = builder.build();
        manager.notify(nId, fore);
        nId++;
    }

    /**
     * 释放Socket，并关闭
     *
     * @param socket
     */
    private void releaseLastSocket(WeakReference<Socket> socket) {
        if (socket != null) {
            Socket so = socket.get();
            if (so != null && so.isActive())
                so.close();
            socket.clear();
            Log.e("Socket断开连接。。。。。。");
        }
    }

    /**
     * 判断是否断开连接，断开返回true,没有返回false
     *
     * @return
     */
    public boolean isServerClose() {
        try {
            if (mSocket != null && mSocket.get() != null) {
                mSocket.get().send(0);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
                return false;
            }
        } catch (Exception se) {
            Log.e(se);
            return true;
        }
        return true;
    }

    /**
     * 销毁Service同时要销毁Socket
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy()");
//        if (mReadThread != null)
//            mReadThread.release();
        releaseLastSocket(mSocket);
        sendBroadcast(new Intent("socketService_killed"));
        SocketServiceSP.getInstance(SocketService.this).saveSocketServiceStatus(false);
        unregisterReceiver(restartBR);
    }

    /**
     * 对外提供的断开Socket连接的方法（向服务器发送断开的包，服务器收到后会与之断开）
     */
    public void interruptSocket() {
        new Thread(() -> {
            SocketMessage message = new SocketMessage();
            message.setType(Custom.MESSAGE_CLOSE);
            message.setMessage("");
            message.setFrom(Custom.NAME_CLIENT);
            message.setTo(Custom.NAME_SERVER);
            try {
                sendMessage(message);
            } catch (RemoteException e) {
                Log.e(e);
            }
        }).start();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("onUnbind(Intent intent)");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.e("onBind(Intent intent)");
        super.onRebind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("onRebind(Intent intent) ");
        return mBinder;
    }

    /**************************************************
     * AIDL
     ********************************************************/

    private ISocketMessageListener messageListener;
    private Binder mBinder = new ISocketServiceInterface.Stub() {
        private static final String ITAG = "ISocketServiceInterface";

        /**
         * 客户端要求连接Socket
         * @throws RemoteException
         */
        @Override
        public void connectSocket() throws RemoteException {
            Log.e("connectSocket");
            mHandler.sendEmptyMessage(MSG_WHAT_CONNECT);
        }

        /**
         * 客户端要求断开Socket连接
         * @throws RemoteException
         */
        @Override
        public void disConnectSocket() throws RemoteException {
            Log.e("disConnectSocket");
            mHandler.sendEmptyMessage(MSG_WHAT_DISCONNECT);
        }

        /**
         * 客户端向服务器端发送消息
         * @param message
         * @throws RemoteException
         */
        @Override
        public void sendMessage(SocketMessage message) throws RemoteException {
            Log.e("sendMessage");
            Message msg = Message.obtain();
            msg.what = MSG_WHAT_SENDMESSAGE;
            msg.obj = message;
            mHandler.sendMessage(msg);
        }

        /**
         * 客户端添加消息监听器，监听服务器端发来的消息
         * @param listener
         * @throws RemoteException
         */
        @Override
        public void addMessageListener(ISocketMessageListener listener) throws RemoteException {
            Log.e("addMessageListener");
            messageListener = listener;
        }
        @Override
        public void removeMessageListener(ISocketMessageListener listener) throws RemoteException {
            Log.e("removeMessageListener");
            messageListener = null;
        }
    };
}
