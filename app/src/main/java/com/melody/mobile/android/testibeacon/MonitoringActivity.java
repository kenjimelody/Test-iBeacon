package com.melody.mobile.android.testibeacon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import com.melody.mobile.android.testibeacon.ibeacon.IBeacon;
import com.melody.mobile.android.testibeacon.ibeacon.IBeaconProtocol;
import com.melody.mobile.android.testibeacon.ibeacon.Utils;
import com.melody.mobile.android.testibeacon.ibeacon.listener.IBeaconListener;
import java.util.ArrayList;

/**
 * Created by Thanisak Piyasaksiri on 10/8/15 AD.
 */
public class MonitoringActivity extends AppCompatActivity implements IBeaconListener {

    private static ArrayList<IBeacon> _beacons;
    private static IBeaconProtocol _ibp;

    protected static final String TAG = "MonitoringActivity";
    private static final int REQUEST_BLUETOOTH_ENABLE = 1;

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(_beacons == null)
            _beacons = new ArrayList<IBeacon>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _beacons.clear();
                scanBeacons();
                Snackbar.make(view, "Scanning...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        _ibp = IBeaconProtocol.getInstance(this);
        _ibp.setListener(this);
    }

    @Override
    protected void onStop() {

        _ibp.stopScan();
        super.onStop();
    }

    private void scanBeacons(){
        // Check Bluetooth every time
        Log.i(Utils.LOG_TAG,"Scanning");

        // Filter based on default easiBeacon UUID, remove if not required
        //_ibp.setScanUUID(UUID here);

        if(!IBeaconProtocol.configureBluetoothAdapter(this)){

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH_ENABLE );

        }else{

            if(_ibp.isScanning())
                _ibp.stopScan();

            _ibp.reset();
            _ibp.startScan();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_BLUETOOTH_ENABLE){
            if(resultCode == Activity.RESULT_OK) {

                scanBeacons();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void enterRegion(IBeacon ibeacon) {

    }

    @Override
    public void exitRegion(IBeacon ibeacon) {

    }

    @Override
    public void beaconFound(IBeacon ibeacon) {

        if(ibeacon.getUuidHexStringDashed().equalsIgnoreCase("74278BDA-B644-4520-8F0C-720EAF059939"))
            _beacons.add(ibeacon);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                for (int i=0; i<_beacons.size(); i++) {

                    IBeacon iBeacon = (IBeacon) _beacons.get(i);
                    Log.e("TAG", "UUDID: " + iBeacon.getUuid());
                    Log.e("TAG", "UUDIDHexStringDashed: " + iBeacon.getUuidHexStringDashed());
                    Log.e("TAG", "Major: " + iBeacon.getMajor());
                    Log.e("TAG", "Minor: " + iBeacon.getMinor());
                    Log.e("TAG", "Distance: " + iBeacon.getProximity() + "m.");
                }
            }
        });
    }

    @Override
    public void searchState(int state) {

    }

    @Override
    public void operationError(int status) {

    }
}
