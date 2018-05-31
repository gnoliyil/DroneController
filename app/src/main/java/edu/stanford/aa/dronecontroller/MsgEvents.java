package edu.stanford.aa.dronecontroller;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;

import edu.stanford.aa.dronecontroller.utils.ToastUtils;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by gnoliyil on 5/8/18.
 * AA 241x Drone Controller
 */

public class MsgEvents {
    private static final String TAG = MsgEvents.class.getName();
    private static DroneControlActivity droneControl = null;

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
            Log.v(TAG, "message received: " + args[1].toString());
        }
    }

    private static class OnCommandListener implements Emitter.Listener {
        @Override
        public void call(Object... args) {
            if (droneControl == null) {
                Log.e(TAG, "droneControl not ready!");
                return;
            }

            // TODO: parse the "command" to get "destination" and "velocity", then create a FlyTask and run it.
            JSONObject command = (JSONObject) args[0];
            Log.v(TAG, "command received: " + command.toString());
            try {
                JSONObject destination = command.getJSONObject("destination");
                double velocity = command.getDouble("velocity");
                double latitude = destination.getDouble("latitude");
                double longitude = destination.getDouble("longitude");
                float altitude = (float) destination.getDouble("altitude");

                Timer controlTimer = new Timer();
                droneControl.droneControlTimers.add(controlTimer);

                FlyTask newTask = new FlyTask(droneControl.droneInformation, latitude, longitude,
                        altitude, velocity, controlTimer, droneControl.droneControlTimers);

                controlTimer.schedule(newTask, 0, 100);
                // IMPORTANT: The period must be less than 200 to control the drone.
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    static void setActivity(DroneControlActivity droneControl){
        MsgEvents.droneControl = droneControl;
    }

    static void setListener() {
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
            // TODO: implement other events if needed
        }
    }
}
