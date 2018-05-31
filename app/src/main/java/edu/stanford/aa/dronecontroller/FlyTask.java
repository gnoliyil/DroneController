package edu.stanford.aa.dronecontroller;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dji.common.flightcontroller.LocationCoordinate3D;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import edu.stanford.aa.dronecontroller.utils.ModuleVerificationUtil;
import edu.stanford.aa.dronecontroller.utils.ToastUtils;

/**
 * Created by Yilong on 5/31/18.
 */
public class FlyTask extends TimerTask {
    private DroneInformation droneInformation;
    /// TODO FOR TEAMS
    private LocationCoordinate3D location;
    private double velocity;
    private Timer timer;
    private List<Timer> listTimer;

    private final float epsilon;
    private final double epsilonAlt;

    FlyTask(DroneInformation information, double latitude, double longitude, float altitude, double velocity,
            Timer timer, List<Timer> listTimer) {
        this.droneInformation = information;
        this.location = new LocationCoordinate3D(latitude, longitude, altitude);
        this.velocity = velocity;
        this.timer = timer;
        this.listTimer = listTimer;
        this.epsilon = 5;
        this.epsilonAlt = 0.5;
    }

    private boolean isCloseEnough(LocationCoordinate3D a, LocationCoordinate3D b) {
        /// TODO: Implement a function to determine if two locations are close enough
        final double R = 6373.0;

        double lat1 = Math.toRadians(a.getLatitude());
        double lon1 = Math.toRadians(a.getLongitude());
        double lat2 = Math.toRadians(b.getLatitude());
        double lon2 = Math.toRadians(b.getLongitude());

        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;

        double va = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double vc = 2 * Math.atan2(Math.sqrt(va), Math.sqrt(1 - va));

        double distance = R * vc * 1000; // in metres
        return distance < epsilon && Math.abs(a.getAltitude() - b.getAltitude()) < epsilonAlt;
    }

    @Override
    public void run() {
        /// TODO FOR TEAMS
        if (isCloseEnough(droneInformation.getLocation(), this.location)) {
            // TODO: set pitch, roll, yaw, throttle using position, target position and target velocity:
            float pitch = 0, roll = 0, yaw = 0, throttle = 0;
            if (ModuleVerificationUtil.isFlightControllerAvailable()) {
                MApplication.getAircraftInstance().getFlightController()
                        .sendVirtualStickFlightControlData(new FlightControlData(pitch, roll, yaw, throttle), djiError -> {
                        });
            }
        } else {
            // cancel current task timer
            timer.cancel();
            listTimer.remove(timer);
            ToastUtils.setResultToToast("Arrived at location " + droneInformation.getLocation());
        }
    }
}
