package com.android.accessorydisplay.source.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import static com.android.accessorydisplay.source.usb.UsbCommunication.USB_OPEN_SUCCESS;

public class OpenDevicesReceiver extends BroadcastReceiver {

    private OpenDevicesListener mOpenDevicesListener;//usb设备连接的回调接口

    public OpenDevicesReceiver(OpenDevicesListener openDevicesListener) {
        mOpenDevicesListener = openDevicesListener;
    }

    private CommunicationListener listener;//sdk中用户打开usb连接成功的回调

    public void setCommunicationListener(CommunicationListener listener) {//设置usb设备连接成功的回调
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);//获取附件设备
        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {//判断设备是都有usb
            if (usbDevice != null) {
                mOpenDevicesListener.openAccessoryModel(usbDevice);
                listener.onSuccess(USB_OPEN_SUCCESS,"USB权限开启成功");
                Toast.makeText(context, "USB权限开启成功", Toast.LENGTH_SHORT).show();
            } else {
                mOpenDevicesListener.openDevicesError();
                listener.onFaild("USB设备连接异常");
            }
        } else {
            //打开权限失败
            mOpenDevicesListener.openDevicesError();
            listener.onFaild("用户未授权USB权限");
        }
    }

    public interface OpenDevicesListener {
        /**
         * 打开Accessory模式
         *
         * @param usbDevice
         */
        void openAccessoryModel(UsbDevice usbDevice);

        /**
         * 打开设备(手机)失败
         */
        void openDevicesError();
    }
}
