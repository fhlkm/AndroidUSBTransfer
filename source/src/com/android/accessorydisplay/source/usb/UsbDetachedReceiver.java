package com.android.accessorydisplay.source.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UsbDetachedReceiver extends BroadcastReceiver {

    private UsbDetachedListener mUsbDetachedListener; //usb连接断开的回调

    /**
     * 构造方法中添加回调参数
     * @param usbDetachedListener
     */
    public UsbDetachedReceiver(UsbDetachedListener usbDetachedListener) {
        mUsbDetachedListener = usbDetachedListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mUsbDetachedListener.usbDetached();
    }

    public interface UsbDetachedListener {
        /**
         * usb断开连接
         */
        void usbDetached();
    }
}