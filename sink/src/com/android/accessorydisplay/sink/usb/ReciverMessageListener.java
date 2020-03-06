package com.android.accessorydisplay.sink.usb;

public interface ReciverMessageListener {
    void onSuccess(byte[] bytes);
    void onFaild(String msg);
}