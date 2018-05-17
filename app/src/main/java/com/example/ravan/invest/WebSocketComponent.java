package com.example.ravan.invest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = WebSocketModule.class)
public interface WebSocketComponent {
}
