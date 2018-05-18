package com.example.ravan.invest;

import android.app.Application;

import com.example.ravan.invest.ui.WebSocketFragment;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = WebSocketModule.class)
public interface WebSocketComponent {

    void inject(WebSocketFragment fragment);

}
