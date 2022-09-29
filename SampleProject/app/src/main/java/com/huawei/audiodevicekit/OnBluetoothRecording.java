package com.huawei.audiodevicekit;

public interface OnBluetoothRecording {
    void onStartRecording(boolean state,boolean bluetoothFlag);
    void onCancelRecording();
}
