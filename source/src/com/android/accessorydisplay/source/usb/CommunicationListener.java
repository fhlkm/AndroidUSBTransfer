package com.android.accessorydisplay.source.usb;

public interface CommunicationListener {
    void onSuccess(int code, String msg);

    void onFaild(String msg);
}