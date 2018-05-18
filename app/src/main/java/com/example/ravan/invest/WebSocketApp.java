package com.example.ravan.invest;

import android.app.Application;
import android.content.Context;

import timber.log.Timber;

public class WebSocketApp extends Application {
    private WebSocketComponent mainComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        mainComponent = DaggerWebSocketComponent.builder().webSocketModule(
                new WebSocketModule(this)).build();

    }

    public static WebSocketComponent getComponent(Context context) {
        return ((WebSocketApp) context.getApplicationContext()).mainComponent;
    }
}