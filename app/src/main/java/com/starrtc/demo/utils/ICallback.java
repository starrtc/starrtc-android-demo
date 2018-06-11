package com.starrtc.demo.utils;

public interface ICallback {
    abstract  void callback(boolean reqSuccess, String statusCode, String data);
}
