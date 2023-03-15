package com.example.sensorradar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
// azimuth = utara, jika bernilai 0 maka hp menghadap ke utara
// kalo di tempat datar, pitch dan roll = 0