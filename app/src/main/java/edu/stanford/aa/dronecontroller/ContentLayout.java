package edu.stanford.aa.dronecontroller;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.net.URISyntaxException;

import dji.keysdk.DJIKey;
import dji.sdk.base.BaseProduct;
import dji.sdk.products.Aircraft;
import edu.stanford.aa.dronecontroller.utils.ToastUtils;
import io.socket.client.IO;

/**
 * Created by gnoliyil on 5/3/18.
 */

class ContentLayout extends ConstraintLayout {
    public static final String TAG = ContentLayout.class.getName();

    // UI components
    private TextView mTextConnectionStatus;
    private TextView mTextProduct;
    private Button mBtnOpen;
    private Handler mHandlerUI;

    private EditText mEditIpAddress;
    private EditText mEditPort;
    private Button mBtnConnect;

    // DJI components
    private BaseProduct mProduct;
    private DJIKey firmwareKey;


    public ContentLayout(Context context) {
        super(context);
    }

    public ContentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        Log.v(TAG, "onFinishInflate");
        super.onFinishInflate();
        MApplication.getEventBus().register(this);
        initUI();
    }

    private void initUI() {
        Log.v(TAG, "InitUI");

        mTextConnectionStatus = findViewById(R.id.text_connection_status);
        mTextProduct = findViewById(R.id.text_product_info);
        mBtnOpen = findViewById(R.id.btn_open);

        mEditIpAddress = findViewById(R.id.edit_ip);
        mBtnConnect = findViewById(R.id.btn_connect);

        mBtnOpen.setOnClickListener(view -> {
            Log.v(TAG, "Open button clicked");
            switchToDroneControlActivity();
        });

        mBtnConnect.setOnClickListener(view -> {
            String url = mEditIpAddress.getText().toString();
            try {
                IO.Options options = new IO.Options();
                options.reconnectionAttempts = 5;
                MApplication.setSocket(IO.socket(url, options));
                MsgEvents.setListener();
                MApplication.getSocket().connect();
            } catch (URISyntaxException e) {
                ToastUtils.setResultToToast("Error setting up socket");
                Log.v(TAG, e.getMessage());
            }
        });
    }

    private void switchToDroneControlActivity() {
        Intent intent = new Intent(this.getContext(), DroneControlActivity.class);
        this.getContext().startActivity(intent);
    }

    @Override
    protected void onAttachedToWindow() {
        Log.v(TAG, "onAttachedToWindow");
        refreshSDKRelativeUI();
        mHandlerUI = new Handler(Looper.getMainLooper());
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.v(TAG, "onDetachedFromWindow");
        mHandlerUI.removeCallbacksAndMessages(null);
        mHandlerUI = null;
        super.onDetachedFromWindow();
    }

    @Subscribe
    public void onConnectivityChange(MainActivity.ConnectivityChangeEvent event) {
        Log.v(TAG, "on connectivity change");
        if (mHandlerUI != null) {
            mHandlerUI.post(() -> refreshSDKRelativeUI());
        }
    }

    private void refreshSDKRelativeUI() {
        mProduct = MApplication.getProductInstance();
        if (mProduct != null && mProduct.isConnected()) {
            mBtnOpen.setEnabled(true);

            String str = mProduct instanceof Aircraft ? "DJIAircraft" : "DJIHandheld";
            mTextConnectionStatus.setText(str + " connected");

            if (mProduct.getModel() != null) {
                mTextProduct.setText(mProduct.getModel().getDisplayName());
            } else {
                mTextProduct.setText("model unknown");
            }
        } else {
            mBtnOpen.setEnabled(false);

            mTextProduct.setText("");
            mTextConnectionStatus.setText("No product connected");
        }
    }
}
