package edu.stanford.aa.dronecontroller;

import android.util.Log;

import org.json.JSONObject;

import edu.stanford.aa.dronecontroller.utils.ToastUtils;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by gnoliyil on 5/8/18.
 */

public class MsgEvents {
    private static class OnConnectListener implements Emitter.Listener {
        @Override
        public void call(Object... args) {
            ToastUtils.setResultToToast("Connect!");
        }
    }

    private static class OnDisconnectListener implements Emitter.Listener {
        @Override
        public void call(Object... args) {
            ToastUtils.setResultToToast("Disconnect!");
        }
    }

    private static class OnConnectErrorListener implements Emitter.Listener {
        @Override
        public void call(Object... args) {
            ToastUtils.setResultToToast("Connection error!");
        }
    }

    private static class OnConnectTimeoutListener implements Emitter.Listener {
        @Override
        public void call(Object... args) {
            ToastUtils.setResultToToast("Connection Timeout");
        }
    }

    private static class OnMessageListener implements Emitter.Listener {
        @Override
        public void call(Object... args) {
            Log.v("MsgEvents", "message received: " + ((JSONObject) args[1]).toString());
        }
    }

    private static class OnCommandListener implements Emitter.Listener {
        @Override
        public void call(Object... args) {
            JSONObject command = (JSONObject) args[0];
            Log.v("MsgEvents", "command received: " + command.toString());
        }
    }

    public MsgEvents(){
    }

    public static void setListener() {
        Socket mSocket = MApplication.getSocket();
        if (mSocket == null) {
             ToastUtils.setResultToToast("No socket!");
        } else {
            mSocket.on(Socket.EVENT_CONNECT, new OnConnectListener());
            mSocket.on(Socket.EVENT_DISCONNECT, new OnDisconnectListener());
            mSocket.on(Socket.EVENT_CONNECT_ERROR, new OnConnectErrorListener());
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, new OnConnectTimeoutListener());
            mSocket.on("message", new OnMessageListener());
            mSocket.on("command", new OnCommandListener());
        }
    }
}
