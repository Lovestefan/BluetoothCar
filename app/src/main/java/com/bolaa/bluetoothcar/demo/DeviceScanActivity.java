package com.bolaa.bluetoothcar.demo;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bolaa.bluetoothcar.R;
import com.bolaa.bluetoothcar.scan.BluetoothService;
import com.clj.fastble.conn.BleCharacterCallback;
import com.clj.fastble.data.ScanResult;
import com.clj.fastble.exception.BleException;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Administrator on 2017/9/22 15:38
 * 邮箱：xiaobo.pu@bolaa.com
 * 手机:15223197346
 */

public class DeviceScanActivity extends Activity implements View.OnClickListener
{
    private Button startScanButton;
    private Button caoKongButton;
    private BluetoothService mBluetoothService;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_any_scan);
        startScanButton = (Button)findViewById(R.id.btn_start);
        caoKongButton = (Button)findViewById(R.id.caokong);
        startScanButton.setOnClickListener(this);
        caoKongButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.caokong:
                mBluetoothService.write(
                        mBluetoothService.getBluetoothGattCharacteristic().getService().getUuid().toString(),
                        mBluetoothService.getBluetoothGattCharacteristic().getUuid().toString(),
                        "a50d6d616d6101019cff00000000006e5a",
                        new BleCharacterCallback() {
                            @Override
                            public void onSuccess(final BluetoothGattCharacteristic characteristic) {

                            }

                            @Override
                            public void onFailure(final BleException exception) {
                            }

                            @Override
                            public void onInitiatedResult(boolean result) {

                            }
                        });
                break;
            case R.id.btn_start:
                checkPermissions();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothService!=null){
            unbindService();
        }
    }
    private void bindService(){
        Intent bindIntent = new Intent(this,BluetoothService.class);
        this.bindService(bindIntent,mFhrSCon, Context.BIND_AUTO_CREATE);
    }
    private void  unbindService(){
        this.unbindService(mFhrSCon);
    }

    private ServiceConnection mFhrSCon = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothService = ((BluetoothService.BluetoothBinder)service).getService();
            mBluetoothService.setScanCallback(callback);
            mBluetoothService.scanDevice();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothService = null;
        }
    };

    private BluetoothService.Callback callback = new BluetoothService.Callback()
    {
        @Override
        public void onStartScan() {

        }

        @Override
        public void onScanning(ScanResult scanResult) {
            Toast.makeText(DeviceScanActivity.this, "开始扫描蓝牙设备", Toast.LENGTH_LONG).show();
            if (scanResult.getDevice().getName()!=null){
                if (scanResult.getDevice().getName().contains("MZ_LEDualSpp")){
                   if (mBluetoothService != null) {
                       mBluetoothService.cancelScan();
                       mBluetoothService.connectDevice(scanResult);
                   }
               }
            }
        }

        @Override
        public void onScanComplete() {

        }

        @Override
        public void onConnecting() {

        }

        @Override
        public void onConnectFail() {
            Toast.makeText(DeviceScanActivity.this, "连接失败", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onDisConnected() {
            Toast.makeText(DeviceScanActivity.this, "连接断开", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServicesDiscovered() {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 12:
                if (grantResults.length>0){
                    for (int i=0;i<grantResults.length;i++){
                        if (grantResults[i]== PackageManager.PERMISSION_GRANTED){
                            onPermissionGranted(permissions[i]);
                        }
                    }
                }
                break;
        }
    }
    private void checkPermissions(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission:permissions){
            int permissionCheck = ContextCompat.checkSelfPermission(this,permission);
            if (permissionCheck==PackageManager.PERMISSION_GRANTED){
                onPermissionGranted(permission);
            }else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()){
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this,deniedPermissions,12);
        }
    }
    private void onPermissionGranted(String permission){
        switch (permission){
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (mBluetoothService==null){
                    bindService();
                }else {
                    mBluetoothService.scanDevice();
                }
                break;
        }
    }
}
