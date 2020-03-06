package com.android.accessorydisplay.sink;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.accessorydisplay.sink.usb.CommunicationListener;
import com.android.accessorydisplay.sink.usb.ReciverMessageListener;
import com.android.accessorydisplay.sink.usb.SendMessageListener;
import com.android.accessorydisplay.sink.usb.UsbCommunication;


public class MainActivity extends Activity {
    UsbCommunication usbCommunication;
    String usbMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sink_activity);
        usbCommunication  =  UsbCommunication.getInstance(this);//初始化usb连接对象
        usbCommunication.openCommunication(new CommunicationListener() {//开启usb连接
            @Override
            public void onSuccess(int code, String msg) {//连接成功
//                sendMessage.setEnabled(true);
                getUsbMessage();//接收usb数据
            }

            @Override
            public void onFaild(String msg) {//连接失败
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });


        findViewById(R.id.botton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        findViewById(R.id.receive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsbMessage();
            }
        });


    }


    public void sendMessage(){
        String sendmessage="Hello From Host";
        usbCommunication.sendMessage(sendmessage.getBytes(), new SendMessageListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "信息发送成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFaild(String msg) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getUsbMessage() {
        //接收数据
        usbCommunication.receiveMessage(new ReciverMessageListener() {
            @Override
            public void onSuccess(byte[] bytes) {
                usbMessage = new String(bytes);
                Toast.makeText(MainActivity.this,usbMessage+" msg",Toast.LENGTH_LONG).show();
                ((TextView)  findViewById(R.id.logTextView)).setText(usbMessage);
            }

            @Override
            public void onFaild(String msg) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        UsbCommunication.getInstance(this).closeCommunication();
    }

}
