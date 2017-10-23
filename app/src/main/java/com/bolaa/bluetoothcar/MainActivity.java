package com.bolaa.bluetoothcar;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
{
    private Button autoButton;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        autoButton = (Button)findViewById(R.id.button1);
        autoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                bluetoothAdapter.cancelDiscovery();
                if (!bluetoothAdapter.isEnabled())
                {
                    bluetoothAdapter.enable();
                }else{
                    bluetoothAdapter.startDiscovery();
                }

            }
        });
    }
}
