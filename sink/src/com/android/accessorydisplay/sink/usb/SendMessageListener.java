package com.android.accessorydisplay.sink.usb;

public interface SendMessageListener {
    void onSuccess();
    void onFaild(String msg);
}