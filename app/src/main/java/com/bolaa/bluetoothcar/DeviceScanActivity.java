package com.bolaa.bluetoothcar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * 作者：Administrator on 2017/9/21 16:09
 * 邮箱：xiaobo.pu@bolaa.com
 * 手机:15223197346
 */

public class DeviceScanActivity extends Activity
{
    private final static String TAG = DeviceScanActivity.class.getSimpleName();
    private final static String UUID_KEY_DATA = "494535343-8841-43F4-A8D4-ECBE34729BB3";
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;

    /** 搜索BLE终端 */
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeClass mBLE;
    private boolean mScanning;
    private BluetoothDevice BTtoLink = null;
    private String BTtoFind = "MZ_LEDualSpp";
    private BluetoothGatt gatt;
    private TextView BT_find, BT_info, BT_link;//三个textview分别表示：设备是否找到，BLE模块传输的信息，连接状态
    private String S_BT_info = "null", info = "111";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mHandler = new Handler();

        BT_find = (TextView) findViewById(R.id.BT_find);
        BT_info = (TextView) findViewById(R.id.BT_info);
        BT_link = (TextView) findViewById(R.id.BT_link);
       /* Typeface typeface = Typeface.createFromAsset(getAssets(),
                "fonts/maozedong.ttf");//设置显示字体
        BT_find.setTypeface(typeface);
        BT_info.setTypeface(typeface);
        BT_link.setTypeface(typeface);*/

        BT_find.setText("查找中");
        BT_info.setText("null");
        BT_link.setText("未连接");

        if (!getPackageManager().hasSystemFeature(   //判断主机是否支BLE牙设备
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE is not supported", Toast.LENGTH_SHORT)
                    .show();
            finish();

        }
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);//获得Android设备中的bluetoothmanager
        mBluetoothAdapter = bluetoothManager.getAdapter();//获得bluetoothadapter
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }
        mBluetoothAdapter.enable(); //强制使能Bluetoothadapter，打开Android设备蓝牙
        mBLE = new BluetoothLeClass(this); //BLuetoothLeClass类
        if (!mBLE.initialize()) {
            Log.e(TAG, "Unable to initialize Bluetooth");
            finish();

        }

        // 发现BLE终端的Service时回调
        mBLE.setOnServiceDiscoverListener(mOnServiceDiscover);

        // 收到BLE终端数据交互的事件
        mBLE.setOnDataAvailableListener(mOnDataAvailable);
        mBLE.setOnConnectListener(mOnConnectListener);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        scanLeDevice(true);//搜索BLE设备
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        scanLeDevice(false);
        mBLE.disconnect();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        mBLE.close();
    }

    private void scanLeDevice(final boolean enable) {
        // TODO Auto-generated method stub
        if (enable) {

            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

                }
            }, SCAN_PERIOD);//在搜索时间内，关闭搜索标志，不对搜索回调函数进行响应
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);

        } else {

            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);

        }

    }

    /**
     * 搜索到BLE终端服务的事件
     */
    private BluetoothLeClass.OnServiceDiscoverListener mOnServiceDiscover = new BluetoothLeClass.OnServiceDiscoverListener() {

        @Override
        public void onServiceDiscover(BluetoothGatt gatt) {
            DeviceScanActivity.this.gatt = gatt;
            displayGattServices(mBLE.getSupportedGattService());
        }
    };
    /**
     * 搜索到设备以后的连接事件
     */
    private BluetoothLeClass.OnConnectListener mOnConnectListener = new BluetoothLeClass.OnConnectListener()
    {
        @Override
        public void onConnect(BluetoothGatt gatt) {
            gatt.connect();
        }
    };
    /**
     * 收到BLE终端数据交互的事件
     */
    private BluetoothLeClass.OnDataAvailableListener mOnDataAvailable = new BluetoothLeClass.OnDataAvailableListener() {

        /**
         * BLE终端数据写入的事件
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS)
                Log.e(TAG,
                        "onCharRead "
                                + gatt.getDevice().getName()
                                + " read "
                                + characteristic.getUuid().toString()
                                + " -> "
                                + Utils.bytesToHexString(characteristic
                                .getValue()));

        }

        /**
         * 对BLE终端读取数据
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic) {
            info = new String(characteristic.getValue());//对得到的byte数组进行解码，构造新的string
            Log.e(TAG, "onCharWrite " + gatt.getDevice().getName() + " write "
                    + characteristic.getUuid().toString() + " -> " + info);
            if (!S_BT_info.equals(info)) {//判断读取的数据是否发生变化，如果变化，更新UI
                DeviceScanActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        BT_link.setText("已连接");
                        StringBuilder sb = new StringBuilder();//详情参见：http://blog.csdn.net/rmn190/article/details/1492013
                        sb.append(info);
                        sb.append("度");
                        BT_info.setText(sb.toString());
                        sb = null;
                    }
                });

            }

        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {//搜索回调函数：
        String BT_name = null;
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    BT_name = device.getName();
                    if (BT_name!=null){
                        if (BT_name.equals(BTtoFind)) { //如果是要找的设备，更新UI上信息，设置搜索标志，停止响应搜索回调函数，连接BLE设备
                            /** 连接事件 */
                            Toast.makeText(DeviceScanActivity.this,BT_name,Toast.LENGTH_LONG).show();
                            BT_find.setText("已找到设备！");
                            mScanning = false;
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        }
                    }
                }
            });

        }
    };

    private void displayGattServices(BluetoothGattService bluetoothGattService) {

        if (bluetoothGattService == null)
            return;

        // -----Service的字段信息-----//
        int type = bluetoothGattService.getType();
        Log.e(TAG, "-->service type:" + Utils.getServiceType(type));
        Log.e(TAG, "-->includedServices size:"
                + bluetoothGattService.getIncludedServices().size());
        Log.e(TAG, "-->service uuid:" + bluetoothGattService.getUuid());

        // -----Characteristics的字段信息-----//
        List<BluetoothGattCharacteristic> gattCharacteristics = bluetoothGattService
                .getCharacteristics();
        for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
            Log.e(TAG, "---->char uuid:" + gattCharacteristic.getUuid());

            int permission = gattCharacteristic.getPermissions();
            Log.e(TAG,
                    "---->char permission:"
                            + Utils.getCharPermission(permission));

            int property = gattCharacteristic.getProperties();
            Log.e(TAG, "---->char property:" + Utils.getCharPropertie(property));

            byte[] data = gattCharacteristic.getValue();
            if (data != null && data.length > 0) {
                Log.e(TAG, "---->char value:" + new String(data));
            }

            // UUID_KEY_DATA是可以跟蓝牙模块串口通信的Characteristic
            Log.e("as",gattCharacteristic.getUuid().toString());
            if (gattCharacteristic.getUuid().toString().equals(UUID_KEY_DATA)) {
                // 测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBLE.readCharacteristic(gattCharacteristic);
                    }
                }, 500);

                // 接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
                mBLE.setCharacteristicNotification(gattCharacteristic, true);
                // 设置数据内容
                gattCharacteristic.setValue("send data->");
                // 往蓝牙模块写入数据
                mBLE.writeCharacteristic(gattCharacteristic);
            }

            // -----Descriptors的字段信息-----//
            List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic
                    .getDescriptors();
            for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) {
                Log.e(TAG, "-------->desc uuid:" + gattDescriptor.getUuid());
                int descPermission = gattDescriptor.getPermissions();
                Log.e(TAG,
                        "-------->desc permission:"
                                + Utils.getDescPermission(descPermission));

                byte[] desData = gattDescriptor.getValue();
                if (desData != null && desData.length > 0) {
                    Log.e(TAG, "-------->desc value:" + new String(desData));
                }
            }

        }

    }
}
