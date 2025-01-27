package com.example.roboticarmhandapp;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.math.BigInteger;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;

public class Hand extends Fragment implements ArduinoListener {

    SeekBar HandSeekBar;
    TextView HandAngleIndicator;
    Button openHandButton,closeHandButton;
    private Arduino arduino;
    byte seeked;
    byte[] sendSeeked = new byte[1];

    public Hand() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_hand, container, false);

        // Declaration des views
        HandSeekBar = v.findViewById(R.id.HandSeekBar);
        HandAngleIndicator = v.findViewById(R.id.HandAngleIndicator);
        openHandButton = v.findViewById(R.id.openHandButton);
        closeHandButton = v.findViewById(R.id.closeHandButton);
        arduino = new Arduino(getActivity());
        arduino.addVendorId(2341);

        HandSeekBar.setMax(180);
        HandSeekBar.setProgress(90);

        HandAngleIndicator.setText("Bougez moi !");

        HandSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int adjust = (int) (i / 2);
                int newi = adjust * 2;
                HandSeekBar.setProgress(newi);
                seeked = (byte) adjust; // byte prend des valeurs entre -128 et 127
                sendSeeked[0] = seeked;
                HandAngleIndicator.setText("Angle du moteur : " + newi);
                arduino.send(sendSeeked);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        openHandButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandSeekBar.setProgress(0);
                seeked = (byte) 0; // byte prend des valeurs entre -128 et 127
                sendSeeked[0] = seeked;
                HandAngleIndicator.setText("Angle du moteur : " + 0);
                arduino.send(sendSeeked);
            }
        });

        closeHandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandSeekBar.setProgress(180);
                seeked = (byte) 90; // byte prend des valeurs entre -128 et 127
                sendSeeked[0] = seeked;
                HandAngleIndicator.setText("Angle du moteur : " + 180);
                arduino.send(sendSeeked);
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        arduino.setArduinoListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        arduino.unsetArduinoListener();
        arduino.close();
    }

    @Override
    public void onArduinoAttached(UsbDevice device) {
        HandAngleIndicator.setText("Arduino connecté !");
        arduino.open(device);
    }

    @Override
    public void onArduinoDetached() {
        HandAngleIndicator.setText("Arduino déconnecté");
    }

    @Override
    public void onArduinoMessage(byte[] bytes) {

    }

    @Override
    public void onArduinoOpened() {

    }

    @Override
    public void onUsbPermissionDenied() {
        HandAngleIndicator.setText("Permission denied. Attempting again in 3 sec...");
        new Handler().postDelayed(() -> arduino.reopen(), 3000);
    }

}