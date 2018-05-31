package edu.stanford.aa.dronecontroller;

import java.util.Timer;
import java.util.TimerTask;

import dji.common.error.DJIError;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import edu.stanford.aa.dronecontroller.utils.ToastUtils;

/**
 * Created by gnoliyil on 5/31/18.
 * AA 241x Drone Controller
 */

public class DrawShapeTasks {
    private float mPitch, mRoll, mYaw, mThrottle;
    private Timer mSendVirtualStickDataTimer;
    private SendVirtualStickDataTask mSendVirtualStickDataTask;
    private FlightController flightController;

    public DrawShapeTasks() {
    }

    public void setFlightController(FlightController flightController) {
        this.flightController = flightController;
    }

    public void drawSquare() {
        DrawSquareTask drawSquareTask = new DrawSquareTask();
        Timer drawSquareTaskTimer = new Timer();
        drawSquareTaskTimer.schedule(drawSquareTask, 0);
        ToastUtils.setResultToToast("created drawSquareTask");
    }

    public void drawTriangle() {
        DrawTriangleTask drawTriangleTask = new DrawTriangleTask();
        Timer drawTriangleTaskTimer = new Timer();
        drawTriangleTaskTimer.schedule(drawTriangleTask, 0);
        ToastUtils.setResultToToast("created drawTriangleTask");
    }

    class DrawSquareTask extends TimerTask {
        @Override
        public void run() {
            long timeToRun = 2000;  // milliseconds
            float velocity = 1;     // m/s

            for (int edge = 0; edge < 5; edge++) {
                ToastUtils.setResultToToast("Drawing edge #" + edge);
                switch (edge) {
                    case 0: {
                        mPitch = velocity;
                        mRoll = 0;
                        mYaw = 0;
                        break;
                    }
                    case 1: {
                        mPitch = 0;
                        mRoll = velocity;
                        mYaw = 0;
                        break;
                    }
                    case 2: {
                        mPitch = -velocity;
                        mRoll = 0;
                        mYaw = 0;
                        break;
                    }
                    case 3: {
                        mPitch = 0;
                        mRoll = -velocity;
                        mYaw = 0;
                        break;
                    }
                    case 4: {
                        mPitch = 0;
                        mRoll = 0;
                        mYaw = 0;
                        break;
                    }
                }

                if (null == mSendVirtualStickDataTimer) {
                    mSendVirtualStickDataTask = new SendVirtualStickDataTask();
                    mSendVirtualStickDataTimer = new Timer();
                    mSendVirtualStickDataTimer.schedule(mSendVirtualStickDataTask, 100, 200);
                }

                if (edge == 4)
                    break;

                try {
                    Thread.sleep(timeToRun);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class DrawTriangleTask extends TimerTask {

        @Override
        public void run() {
            long timeToRun = 2000;
            long velocity = 1;

            for (int edge = 0; edge < 4; edge++) {
                ToastUtils.setResultToToast("Drawing edge #" + edge);
                switch (edge) {
                    case 0: {
                        mPitch = velocity;
                        mRoll = 0;
                        break;
                    }
                    case 1: {
                        mPitch = -0.5f * velocity;
                        mRoll = 0.866f * velocity;
                        break;
                    }
                    case 2: {
                        mPitch = -0.5f * velocity;
                        mRoll = -0.866f * velocity;
                        break;
                    }
                    case 3: {
                        mPitch = 0;
                        mRoll = 0;
                        break;
                    }
                }

                if (null == mSendVirtualStickDataTimer) {
                    mSendVirtualStickDataTask = new SendVirtualStickDataTask();
                    mSendVirtualStickDataTimer = new Timer();
                    mSendVirtualStickDataTimer.schedule(mSendVirtualStickDataTask, 100, 200);
                }

                if (edge == 3)
                    break;

                try {
                    Thread.sleep(timeToRun);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class SendVirtualStickDataTask extends TimerTask {
        @Override
        public void run() {
            if (flightController != null) {
                flightController.sendVirtualStickFlightControlData(
                        new FlightControlData(
                                mPitch, mRoll, mYaw, mThrottle
                        ), new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {

                            }
                        }
                );
            }
        }
    }
}
