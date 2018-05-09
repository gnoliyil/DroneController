package edu.stanford.aa.dronecontroller;
import android.app.Application;
import android.content.Context;
import com.secneo.sdk.Helper;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.nio.MappedByteBuffer;

import dji.sdk.base.BaseProduct;
import dji.sdk.products.Aircraft;
import dji.sdk.products.HandHeld;
import dji.sdk.sdkmanager.DJISDKManager;
import io.socket.client.Socket;

/**
 * Created by gnoliyil on 5/3/18.
 */

public class MApplication extends Application {
    public static final String TAG = MApplication.class.getName();

    private static BaseProduct product;
    private static Bus bus = new Bus(ThreadEnforcer.ANY);
    private static Application app = null;
    private static Socket socket = null;

    public static Application getInstance() {
        return app;
    }

    public static void setSocket(Socket skt) {
        socket = skt;
    }

    public static synchronized Socket getSocket() {
        return socket;
    }

    public static BaseProduct getProductInstance() {
        if (null == product)
            product = DJISDKManager.getInstance().getProduct();
        return product;
    }

    public static boolean isAircraftConnected() {
        return getProductInstance() != null && getProductInstance() instanceof Aircraft;
    }

    public static boolean isHandheldConnected() {
        return getProductInstance() != null && getProductInstance() instanceof HandHeld;
    }

    public static synchronized Aircraft getAircraftInstance() {
        if (isAircraftConnected())
            return (Aircraft) getProductInstance();
        else
            return null;
    }

    public static synchronized HandHeld getHandHeldInstance() {
        if (isHandheldConnected())
            return (HandHeld) getProductInstance();
        else
            return null;
    }

    public static Bus getEventBus() {
        return bus;
    }

    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        Helper.install(MApplication.this);
        app = this;
    }
}