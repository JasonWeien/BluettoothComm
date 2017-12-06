package lafaya.bluettoothcomm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import me.aflak.bluetooth.Bluetooth;

/**
 * Created by JeffYoung on 2016/9/6.
 */
public class BluetoothThread extends AppCompatActivity implements Bluetooth.CommunicationCallback{
    private boolean registered=false;
    private Bluetooth b;
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        b = new Bluetooth(this);
        b.enableBluetooth();

        b.setCommunicationCallback(this);

        int pos = getIntent().getExtras().getInt("pos");
        name = b.getPairedDevices().get(pos).getName();
        Toast.makeText(getApplicationContext(),"Connecting",Toast.LENGTH_LONG).show();
        b.connectToDevice(b.getPairedDevices().get(pos));

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        registered=true;

    }
    public void onConnect(BluetoothDevice device) {
//        private Button bluetooth_connect;

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
 //               bluetooth_connect.setText("发送");
//                bluetooth_connect.setEnabled(true);
                Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onError(String message) {
        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisconnect(BluetoothDevice device, String message) {
        Toast.makeText(getApplicationContext(),"Disconnected,Connecting again...",Toast.LENGTH_LONG).show();
        b.connectToDevice(device);
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onConnectError(final BluetoothDevice device, String message) {
        Toast.makeText(getApplicationContext(),"Error,Trying again in 3 sec.",Toast.LENGTH_LONG).show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        b.connectToDevice(device);
                    }
                }, 2000);
            }
        });
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                Intent intent1 = new Intent(BluetoothThread.this, MainActivity.class);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        if(registered) {
                            unregisterReceiver(mReceiver);
                            registered=false;
                        }
                        startActivity(intent1);
                        finish();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if(registered) {
                            unregisterReceiver(mReceiver);
                            registered=false;
                        }
                        startActivity(intent1);
                        finish();
                        break;
                }
            }
        }
    };
}
