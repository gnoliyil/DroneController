package edu.stanford.aa.dronecontroller;

import android.util.Log;

import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dji.common.battery.BatteryState;
import dji.common.flightcontroller.Attitude;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.LocationCoordinate3D;
import dji.sdk.base.BaseProduct;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.products.HandHeld;

/**
 * Created by gnoliyil on 5/3/18.
 */

public class DroneInformation {
    private static final String TAG = DroneInformation.class.getName();
    private final BaseProduct mProduct;

    private double batteryCurrent;
    private double batteryVoltage;
    private double batteryPercentage;

    private LocationCoordinate3D location;
    private Attitude attitude;
    private double velocityX, velocityY, velocityZ;
    private double flightTimeInSeconds;

    public DroneInformation() {
        mProduct = MApplication.getProductInstance();
        MApplication.getEventBus().register(this);

        batteryCurrent = batteryVoltage = batteryPercentage = 0;
        velocityX = velocityY = velocityZ = 0;

        setBattery();
    }

    private void setBattery() {
        mProduct.getBattery().setStateCallback(batteryState -> {
            batteryCurrent = batteryState.getCurrent();
            batteryVoltage = batteryState.getVoltage();
            batteryPercentage = batteryState.getChargeRemainingInPercent();
        });
    }

    private void setControllerState() {
        if (MApplication.isAircraftConnected()) {
            Aircraft aircraft = (Aircraft) mProduct;
            FlightControllerState controllerState = aircraft.getFlightController().getState();

            attitude = controllerState.getAttitude();
            velocityX = controllerState.getVelocityX();
            velocityY = controllerState.getVelocityY();
            velocityZ = controllerState.getVelocityZ();
            location = controllerState.getAircraftLocation();
            flightTimeInSeconds = controllerState.getFlightTimeInSeconds();
        } else {
            Log.v(TAG, "Error: no Aircraft connected");
        }
    }

    public LocationCoordinate3D getLocation() {
        return location;
    }

    public Attitude getAttitude() {
        return attitude;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public double getVelocityZ() {
        return velocityZ;
    }

    public JSONObject getJSONState() throws JSONException {
        setControllerState();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("attitude",
                    new JSONObject().put("roll", attitude.roll)
                            .put("pitch", attitude.pitch)
                            .put("yaw", attitude.yaw));
        } catch (JSONException e) {
            jsonObject.put("attitude",
                    new JSONObject().put("roll", 0)
                            .put("pitch", 0)
                            .put("yaw", 0));
        }

        jsonObject.put("velocity",
                new JSONObject().put("x", velocityX)
                                .put("y", velocityY)
                                .put("z", velocityZ));

        try {
            jsonObject.put("location",
                new JSONObject().put("latitude", location.getLatitude())
                                .put("longitude", location.getLongitude())
                                .put("altitude", location.getAltitude()));
        } catch (JSONException e) {
            jsonObject.put("location",
                new JSONObject().put("latitude", 0)
                                .put("longitude", 0)
                                .put("altitude", 0));
        }

        jsonObject.put("flightTimeInSeconds", flightTimeInSeconds);
        jsonObject.put("battery",
                new JSONObject().put("percentage", batteryPercentage)
                                .put("current", batteryCurrent)
                                .put("voltage", batteryVoltage));
        return jsonObject;
    }

    @Subscribe
    public void onConnectivityChange(MainActivity.ConnectivityChangeEvent event) {
        Log.v(TAG, "on connectivity change");
        MApplication.getEventBus().unregister(this);
    }

}
