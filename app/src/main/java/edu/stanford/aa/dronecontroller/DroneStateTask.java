package edu.stanford.aa.dronecontroller;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimerTask;

import io.socket.client.Socket;

/**
 * Created by Yilong on 5/31/18.
 * AA 241x Drone Controller
 */
class DroneStateTask extends TimerTask {
    private static final String TAG = DroneStateTask.class.getName();
    private DroneInformation droneInformation;

    DroneStateTask(DroneInformation information) {
        this.droneInformation = information;
    }

    @Override
    public void run() {
        try {
            JSONObject droneState = droneInformation.getJSONState();
            Log.v(TAG, droneState.toString(2));
            Socket mSocket = MApplication.getSocket();
            if (mSocket != null) {
                mSocket.emit("drone state", droneState);
            } else {
                Log.v(TAG, "TimerTask error: mSocket is null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
