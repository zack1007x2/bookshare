package com.example.zack.core;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

class JWebSocketClient extends WebSocketClient {

   public JWebSocketClient(URI serverUri) {
      super(serverUri);
   }

   @Override
   public void onOpen(ServerHandshake handshakedata) {

   }

   @Override
   public void onMessage(String message) {

   }

   @Override
   public void onClose(int code, String reason, boolean remote) {

   }

   @Override
   public void onError(Exception ex) {

   }
}
