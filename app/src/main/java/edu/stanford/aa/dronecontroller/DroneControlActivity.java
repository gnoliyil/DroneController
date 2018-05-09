package edu.stanford.aa.dronecontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import dji.common.error.DJIError;
import dji.common.flightcontroller.ConnectionFailSafeBehavior;
import dji.common.flightcontroller.FlightOrientationMode;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import edu.stanford.aa.dronecontroller.utils.DialogUtils;
import edu.stanford.aa.dronecontroller.utils.ModuleVerificationUtil;
import edu.stanford.aa.dronecontroller.utils.ToastUtils;
import io.socket.client.Socket;

public class DroneControlActivity extends AppCompatActivity {
    private static final String TAG = DroneControlActivity.class.getName();

    private Button mBtnQuery;
    private Button mBtnQuerystop;
    private Button mBtnTakeOff;
    private Button mBtnLand;
    private Button mBtnConf;
    private Button mBtnFlyTo;
    private TextView mTVDroneInfo;
    private DroneInformation droneInformation;
    private Timer droneStateTimer;
    private Timer droneControlTimer;

    private class DroneStateTask extends TimerTask {
        @Override
        public void run() {
            try {
                JSONObject droneState = droneInformation.getJSONState();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mTVDroneInfo.setText(droneState.toString(2));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
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

    private class FlyTask extends TimerTask {
        public FlyTask() {
            // TODO: add target location & altitude, target velocity, etc.
        }

        @Override
        public void run() {
            /// TODO: The fly logic:
            /*
             *    get current drone state
             *    if (dist(position, target position) < epsilon) {
             *        droneControlTimer.cancel();
             *    } else {
             *        set pitch, roll, yaw, throttle using position, target position and target velocity:
             *        if (ModuleVerificationUtil.isFlightControllerAvailable()) {
             *            MApplication.getAircraftInstance().getFlightController()
             *            .sendVirtualStickFlightControlData(new FlightControlData(pitch, roll, yaw, throttle), djiError -> { });
             *
             *        About how to control the drone using virtual stick, Prof Kroo proposed two ways:
             *        (1) Use COURSE_LOCK FlightOrientationMode, in this way we need to set the
             *            drone's orientation first. Then we can move the drone in XoY plane by
             *            changing the pitch and roll.
             *
             *            There are some videos on YouTube about how to use Course Lock, like
             *            https://www.youtube.com/watch?v=eO1hGXQkChY
             *            https://www.youtube.com/watch?v=2H_Sb3XMIQc
             *
             *        (2) Use HOME_LOCK FlightOrientation Mode. In HomeLock mode, we need to set a
             *            home point first. Then if we move "forward" by setting pitch and roll,
             *            the drone will get away from the "home point", and vice versa. A possible
             *            way is that we can set our target position as the home point and then we
             *            go "backwards" until we reach home.
             *    }
             */
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone_control);

        droneInformation = new DroneInformation();
        mTVDroneInfo = findViewById(R.id.drone_info);
        mBtnQuery = findViewById(R.id.btn_query);
        mBtnQuerystop = findViewById(R.id.btn_querystop);
        mBtnTakeOff = findViewById(R.id.btn_takeoff);
        mBtnLand = findViewById(R.id.btn_land);
        mBtnConf = findViewById(R.id.btn_config);
        mBtnFlyTo = findViewById(R.id.btn_flyto);

        mBtnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                droneStateTimer = new Timer();
                droneStateTimer.schedule(new DroneStateTask(), 0L, 1000L); // once every second
            }
        });

        mBtnQuerystop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                droneStateTimer.cancel();
                droneStateTimer = null;
                ToastUtils.setResultToToast("Timer cancelled");
            }
        });

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickHandler(view);
            }
        };
        mBtnTakeOff.setOnClickListener(onClickListener);
        mBtnLand.setOnClickListener(onClickListener);
        mBtnConf.setOnClickListener(onClickListener);
        mBtnFlyTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (droneControlTimer == null) {
                    droneControlTimer = new Timer();
                    droneControlTimer.schedule(new FlyTask(), 0L, 100L); // once every 100ms
                    // IMPORTANT: The period must be less than 200 to control the drone.
                    // TODO: The FlyTask should be initialized with params like target position and velocity
                } else {
                    droneControlTimer = null;
                    ToastUtils.setResultToToast("Fly timer cancelled");
                }
            }
        });
    }

    private void onClickHandler(View view) {
        FlightController flightController = ModuleVerificationUtil.getFlightController();
        if (flightController == null) {
            return;
        }

        switch (view.getId()) {
            case R.id.btn_takeoff:
                flightController.startTakeoff(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        DialogUtils.showDialogBasedOnError(view.getContext(), djiError);
                    }
                });
                break;
            case R.id.btn_land:
                flightController.startLanding(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        DialogUtils.showDialogBasedOnError(view.getContext(), djiError);
                    }
                });
                break;
            case R.id.btn_config:
                flightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
                flightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
                flightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.GROUND);
                flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
                flightController.setFlightOrientationMode(FlightOrientationMode.COURSE_LOCK,
                        djiError -> ToastUtils.setResultToToast("Result: " + (djiError == null ? "Success" : djiError.getDescription())));
                flightController.setConnectionFailSafeBehavior(ConnectionFailSafeBehavior.HOVER,
                        djiError -> ToastUtils.setResultToToast("Result: " + (djiError == null ? "Success" : djiError.getDescription())));
                // TODO: enabled by teams
                // flightController.setControlMode(ControlMode.MANUAL,
                //         djiError -> ToastUtils.setResultToToast("Result: " + (djiError == null ? "Success" : djiError.getDescription()))););
                break;
        }
    }
}
