package com.foodciti.foodcitipartener.utils;


import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;


public class SerialPortManager {
    private SerialPort mSerialPort = null;

    public boolean openSerialPort(String devicePath, int baudRate, boolean flowCon) {
        closeSerialPort();
        try {
            File myFile = new File(devicePath);
            mSerialPort = new SerialPort(myFile, baudRate, 0, flowCon);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    public InputStream getInputStream() {
        if (mSerialPort == null) {
            return null;
        }
        InputStream in = null;
        try {
            in = mSerialPort.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return in;
    }

    public OutputStream getOutputStream() {
        if (mSerialPort == null) {
            return null;
        }
        OutputStream out = null;
        try {
            out = mSerialPort.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return out;
    }
}
