package com.android.accessorydisplay.source;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.accessorydisplay.source.usb.ReciverMessageListener;
import com.android.accessorydisplay.source.usb.SendMessageListener;
import com.android.accessorydisplay.source.usb.UsbCommunication;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {
    UsbCommunication usbCommunication;
    String usbMessage;
    public String TAG="usb:MainActivity";
    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";
    UsbManager manager;
    UsbAccessory accessory;
    PendingIntent permissionIntent;
    UsbAccessory[] accessoryList;
    ParcelFileDescriptor mFileDescriptor;
    TextView mDisaply=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.source_activity);
        mDisaply = (TextView) findViewById(R.id.logTextView);
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        accessory = (UsbAccessory) getIntent().getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
        accessoryList = manager.getAccessoryList();
        if(null !=accessoryList && null != accessoryList ){
            Log.d(TAG, "accessoryList is not null");
        }
        if(null == accessory) {
            accessory = (accessoryList == null ? null : accessoryList[0]);
        }
        PendingIntent     mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        if (accessory != null) {
            if (manager.hasPermission(accessory)) {
                openAccessoryAndSendMessage(accessory);
            } else {
                manager.requestPermission(accessory, mPermissionIntent);
            }
        } else {
            Log.d(TAG, "mAccessory is null");
        }
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccessoryAndSendMessage(accessory);
                readMessage();
            }
        });

    }
    public void readMessage(){
        if(null == mFileDescriptor) {
            mFileDescriptor = manager.openAccessory(accessory);
        }
        if (mFileDescriptor != null) {
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            final FileInputStream  mInputStream = new FileInputStream(fd);
            Thread thread = new Thread(null, new Runnable() {
                @Override
                public void run() {
                    try {
                        final StringBuilder sb= new StringBuilder();
                        byte[] buf = new byte[1024];
                        int length = 0;
                        //循环读取文件内容，输入流中将最多buf.length个字节的数据读入一个buf数组中,返回类型是读取到的字节数。
                        //当文件读取到结尾时返回 -1,循环结束。
                        while((length = mInputStream.read(buf)) != -1){//keep take the thread
                            String info = new String(buf,0,length);
                            sb.append(info);
//                                    System.out.print(new String(buf,0,length));
                            Log.d(TAG, info);
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mDisaply.setText("From sink: "+sb.toString());
                                }
                            });

                        }
                        mInputStream.close();
                        Log.d(TAG, "read data: " + sb.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, "AccessoryController");
            thread.start();
            Log.d(TAG, "reading.... " );
        } else {
            Log.d(TAG, "accessory open fail");
        }
    }
    private void openAccessoryAndSendMessage(UsbAccessory accessory ) {
        if(null == mFileDescriptor) {
            mFileDescriptor = manager.openAccessory(accessory);
        }
        if (mFileDescriptor != null) {
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            final FileOutputStream mOutputStream = new FileOutputStream(fd);
            Thread thread = new Thread(null, new Runnable() {
                @Override
                public void run() {


                    try {
                        Log.d(TAG, "accessory send");
                        mOutputStream.write("Hello from accessory".getBytes());// we can keep the ourputStream and keep writing
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }, "AccessoryController");
            thread.start();
        } else {
            Log.d(TAG, "accessory open fail");
        }
    }


    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(accessory != null){
                            //call method to set up accessory communication
                            Toast.makeText(MainActivity.this,"Discovery accessory,click connect,you can read message from sink",Toast.LENGTH_LONG).show();
                            openAccessoryAndSendMessage(accessory);
                        }
                    }
                    else {
                        Log.d(TAG, "permission denied for accessory " + accessory);
                        manager.requestPermission(accessory, permissionIntent);
                        Toast.makeText(MainActivity.this,"Ask permission",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };




    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this," permission granted",Toast.LENGTH_LONG).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Toast.makeText(MainActivity.this," permission dennied",Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void sendMessage(){
        String sendmessage="From source";
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
