package com.example.ravan.invest;

import android.app.Application;

import com.example.ravan.invest.db.DbModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                DbModule.class,
        }
)

public final class WebSocketModule {
    private final Application application;

    WebSocketModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }
}
