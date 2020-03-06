package com.android.accessorydisplay.source.usb;

public interface ReciverMessageListener {
    void onSuccess(byte[] bytes);
    void onFaild(String msg);
}