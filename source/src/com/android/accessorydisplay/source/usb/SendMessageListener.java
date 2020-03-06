package com.android.accessorydisplay.source.usb;

public interface SendMessageListener {
    void onSuccess();
    void onFaild(String msg);
}