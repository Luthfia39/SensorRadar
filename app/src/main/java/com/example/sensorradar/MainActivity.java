package com.example.sensorradar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;

    private Sensor mSensorAccelerator;
    private Sensor mSensorMagnetometer;

    private TextView mtextSensorAzimuth;
    private TextView mtextSensorPitch;
    private TextView mtextSensorRoll;

    private ImageView mSpotTop;
    private ImageView mSpotBottom;
    private ImageView mSpotLeft;
    private ImageView mSpotRight;

    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];

//    batasan
    private static final float VALUE_DRIFT = 0.05f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mtextSensorAzimuth = findViewById(R.id.value_azimuth);
        mtextSensorPitch = findViewById(R.id.value_pitch);
        mtextSensorRoll = findViewById(R.id.value_roll);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mSensorAccelerator = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSpotTop = findViewById(R.id.spot_top);
        mSpotBottom = findViewById(R.id.spot_bottom);
        mSpotLeft = findViewById(R.id.spot_left);
        mSpotRight = findViewById(R.id.spot_right);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSensorAccelerator != null){
            sensorManager.registerListener(this, mSensorAccelerator, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorMagnetometer != null){
            sensorManager.registerListener(this, mSensorMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensor_type = sensorEvent.sensor.getType();

        switch (sensor_type){
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = sensorEvent.values.clone();
                break;
            default :
                return;
        }

//        mengkonversi rotation device terhadap bumi (menggenerate nilai azmuth, pitch, dan roll)
        float [] rotationMatrix = new float[9];
        boolean rotationOk = SensorManager.getRotationMatrix(rotationMatrix, null, mAccelerometerData, mMagnetometerData);
        float orientationValues [] = new float[3];
        if (rotationOk){
            SensorManager.getOrientation(rotationMatrix, orientationValues);
        }

        float azimuth = orientationValues[0];
        float pitch = orientationValues[1];
        float roll = orientationValues[2];

        mtextSensorAzimuth.setText(getResources().getString(R.string.value_format, azimuth));
        mtextSensorPitch.setText(getResources().getString(R.string.value_format, pitch));
        mtextSensorRoll.setText(getResources().getString(R.string.value_format, roll));

        if (Math.abs(pitch) < VALUE_DRIFT){
            pitch = 0;
        }
        if (Math.abs(roll) < VALUE_DRIFT){
            roll = 0;
        }
        mSpotBottom.setAlpha(0.05f);
        mSpotTop.setAlpha(0.05f);
        mSpotLeft.setAlpha(0.05f);
        mSpotRight.setAlpha(0.05f);

        if (pitch > 0){
            mSpotBottom.setAlpha(pitch);
        }else{
            mSpotTop.setAlpha(Math.abs(pitch));
        }
        if (roll > 0){
            mSpotLeft.setAlpha(roll);
        }else{
            mSpotRight.setAlpha(Math.abs(roll));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
// azimuth = utara, jika bernilai 0 maka hp menghadap ke utara
// kalo di tempat datar, pitch dan roll = 0