package com.android.accessorydisplay.sink.usb;

public interface CommunicationListener {
    void onSuccess(int code, String msg);

    void onFaild(String msg);
}