package edu.stanford.aa.dronecontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;

import dji.common.error.DJIError;
import dji.common.flightcontroller.ConnectionFailSafeBehavior;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import edu.stanford.aa.dronecontroller.utils.DialogUtils;
import edu.stanford.aa.dronecontroller.utils.ModuleVerificationUtil;
import edu.stanford.aa.dronecontroller.utils.ToastUtils;

public class DroneControlActivity extends AppCompatActivity {
    private static final String TAG = DroneControlActivity.class.getName();

    private Button mBtnQuery;
    private Button mBtnQuerystop;
    private Button mBtnTakeOff;
    private Button mBtnLand;
    private Button mBtnConf;
    private Button mBtnFlyTo;

    private TextView mTVDroneInfo;

    public DroneInformation droneInformation;
    public Timer droneStateTimer;
    public ArrayList<Timer> droneControlTimers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone_control);

        // initialize msgEvents
        MsgEvents.setActivity(this);

        droneInformation = new DroneInformation();
        mTVDroneInfo = findViewById(R.id.drone_info);
        mBtnQuery = findViewById(R.id.btn_query);
        mBtnQuerystop = findViewById(R.id.btn_querystop);
        mBtnTakeOff = findViewById(R.id.btn_takeoff);
        mBtnLand = findViewById(R.id.btn_land);
        mBtnConf = findViewById(R.id.btn_config);
        mBtnFlyTo = findViewById(R.id.btn_flyto);

        EditText mEditLongitude = findViewById(R.id.edit_longitude);
        EditText mEditLatitude = findViewById(R.id.edit_latitude);
        EditText mEditAltitude = findViewById(R.id.edit_altitude);
        EditText mEditVelocity = findViewById(R.id.edit_vx);

        mBtnQuery.setOnClickListener(view -> {
            droneStateTimer = new Timer();
            droneStateTimer.schedule(new DroneStateTask(droneInformation), 0L, 1000L); // once every second
        });

        mBtnQuerystop.setOnClickListener(view -> {
            droneStateTimer.cancel();
            droneStateTimer = null;
            ToastUtils.setResultToToast("Timer cancelled");
        });

        View.OnClickListener onClickListener = this::onClickHandler;
        mBtnTakeOff.setOnClickListener(onClickListener);
        mBtnLand.setOnClickListener(onClickListener);
        mBtnConf.setOnClickListener(onClickListener);
        mBtnFlyTo.setOnClickListener(view -> {
            // TODO: The FlyTask should be initialized with params like target position and velocity
            Timer controlTimer = new Timer();
            droneControlTimers.add(controlTimer);

            float latitude = Float.parseFloat(mEditLatitude.getText().toString());
            float longitude = Float.parseFloat(mEditLongitude.getText().toString());
            float altitude = Float.parseFloat(mEditAltitude.getText().toString());
            float velocity = Float.parseFloat(mEditVelocity.getText().toString());

            FlyTask newTask = new FlyTask(droneInformation, latitude, longitude, altitude, velocity,
                    controlTimer, droneControlTimers);

            controlTimer.schedule(newTask, 0L, 100L); // once every 100ms
            // IMPORTANT: The period must be less than 200 to control the drone.
        });
    }

    private void onClickHandler(View view) {
        FlightController flightController = ModuleVerificationUtil.getFlightController();
        if (flightController == null) {
            return;
        }

        switch (view.getId()) {
            case R.id.btn_takeoff:
                flightController.startTakeoff(djiError -> DialogUtils.showDialogBasedOnError(view.getContext(), djiError));
                break;
            case R.id.btn_land:
                flightController.startLanding(djiError -> DialogUtils.showDialogBasedOnError(view.getContext(), djiError));
                break;
            case R.id.btn_config:
                /* TODO: Teams can change their control mode to use different modes for yaw / pitch-roll and throttle */
                flightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
                flightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
                flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);

                flightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.GROUND);

                flightController.setConnectionFailSafeBehavior(ConnectionFailSafeBehavior.HOVER,
                        djiError -> ToastUtils.setResultToToast("Result: " + (djiError == null ? "Success" : djiError.getDescription())));
                // TODO: teams can enable MANUAL control mode here.
                // flightController.setControlMode(ControlMode.MANUAL,
                //         djiError -> ToastUtils.setResultToToast("Result: " + (djiError == null ? "Success" : djiError.getDescription()))););
                break;
        }
    }

    public TextView getMTVDroneInfo() {
        return mTVDroneInfo;
    }
}
