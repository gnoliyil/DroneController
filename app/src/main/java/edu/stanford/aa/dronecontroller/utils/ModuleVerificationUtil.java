package edu.stanford.aa.dronecontroller.utils;

import android.support.annotation.Nullable;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.flightcontroller.Simulator;
import dji.sdk.products.Aircraft;
import dji.sdk.products.HandHeld;
import edu.stanford.aa.dronecontroller.MApplication;

/**
 * Created by dji on 16/1/6.
 */
public class ModuleVerificationUtil {
    public static boolean isProductModuleAvailable() {
        return (null != MApplication.getProductInstance());
    }

    public static boolean isAircraft() {
        return MApplication.getProductInstance() instanceof Aircraft;
    }

    public static boolean isHandHeld() {
        return MApplication.getProductInstance() instanceof HandHeld;
    }

    public static boolean isCameraModuleAvailable() {
        return isProductModuleAvailable() && (null != MApplication.getProductInstance().getCamera());
    }

    public static boolean isPlaybackAvailable() {
        return isCameraModuleAvailable() && (null != MApplication.getProductInstance()
                .getCamera()
                .getPlaybackManager());
    }

    public static boolean isMediaManagerAvailable() {
        return isCameraModuleAvailable() && (null != MApplication.getProductInstance()
                .getCamera()
                .getMediaManager());
    }

    public static boolean isRemoteControllerAvailable() {
        return isProductModuleAvailable() && isAircraft() && (null != MApplication.getAircraftInstance()
                .getRemoteController());
    }

    public static boolean isFlightControllerAvailable() {
        return isProductModuleAvailable() && isAircraft() && (null != MApplication.getAircraftInstance()
                .getFlightController());
    }

    public static boolean isCompassAvailable() {
        return isFlightControllerAvailable() && isAircraft() && (null != MApplication.getAircraftInstance()
                .getFlightController()
                .getCompass());
    }

    public static boolean isFlightLimitationAvailable() {
        return isFlightControllerAvailable() && isAircraft();
    }

    public static boolean isGimbalModuleAvailable() {
        return isProductModuleAvailable() && (null != MApplication.getProductInstance().getGimbal());
    }

    public static boolean isAirlinkAvailable() {
        return isProductModuleAvailable() && (null != MApplication.getProductInstance().getAirLink());
    }

    public static boolean isWiFiLinkAvailable() {
        return isAirlinkAvailable() && (null != MApplication.getProductInstance().getAirLink().getWiFiLink());
    }

    public static boolean isLightbridgeLinkAvailable() {
        return isAirlinkAvailable() && (null != MApplication.getProductInstance()
                .getAirLink()
                .getLightbridgeLink());
    }

    @Nullable
    public static Simulator getSimulator() {
        Aircraft aircraft = MApplication.getAircraftInstance();
        if (aircraft != null) {
            FlightController flightController = aircraft.getFlightController();
            if (flightController != null) {
                return flightController.getSimulator();
            }
        }
        return null;
    }

    @Nullable
    public static FlightController getFlightController() {
        Aircraft aircraft = MApplication.getAircraftInstance();
        if (aircraft != null) {
            return aircraft.getFlightController();
        }
        return null;
    }

}
