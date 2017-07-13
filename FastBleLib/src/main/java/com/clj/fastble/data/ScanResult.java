package com.clj.fastble.data;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;


public class ScanResult implements Parcelable {

    private BluetoothDevice mDevice;
    private byte[] mScanRecord;
    private int mRssi;
    protected LinkedList<Integer> mRssiList;
    protected LinkedList<Integer> mRssiPredList;
    private long mTimestampNanos;
    final private int sizeLimit = 30;
    final private int leastRssiNum = 3;
    protected double estimateStateCov = 1.0;


    public ScanResult(BluetoothDevice device, int rssi,byte[] scanRecord,
                      long timestampNanos) {
        mRssiList = new LinkedList();
        mRssiPredList = new LinkedList();
        mDevice = device;
        mScanRecord = scanRecord;
        mRssi = rssi;
        mRssiList.addFirst(mRssi);
        mTimestampNanos = timestampNanos;
    }


    protected ScanResult(Parcel in) {
        mDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
        mScanRecord = in.createByteArray();
        mRssi = in.readInt();
        mTimestampNanos = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mDevice, flags);
        dest.writeByteArray(mScanRecord);
        dest.writeInt(mRssi);
        dest.writeLong(mTimestampNanos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ScanResult> CREATOR = new Creator<ScanResult>() {
        @Override
        public ScanResult createFromParcel(Parcel in) {
            return new ScanResult(in);
        }

        @Override
        public ScanResult[] newArray(int size) {
            return new ScanResult[size];
        }
    };

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public void setDevice(BluetoothDevice device) {
        this.mDevice = device;
    }

    public byte[] getScanRecord() {
        return mScanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.mScanRecord = scanRecord;
    }

    public int getRssi() {
        return mRssi;
    }

    protected int updateRssiList(int rssi) {
        mRssiList.addFirst(rssi);
        if(mRssiList.size()>sizeLimit) {
            mRssiList.removeLast();
        }

        if(mRssiList.size() > leastRssiNum) {
            rssi = Filter.GassianFilter(this);
        }

        mRssiPredList.addFirst(rssi);
        if(mRssiPredList.size()>sizeLimit) {
            mRssiPredList.removeLast();
        }

        return rssi;
    }

    public void setRssi(int rssi) {
        int filterRssi = updateRssiList(rssi);
        this.mRssi = filterRssi;
    }

    public long getTimestampNanos() {
        return mTimestampNanos;
    }

    public void setTimestampNanos(long timestampNanos) {
        this.mTimestampNanos = timestampNanos;
    }

    //--

    public boolean scanResultEqual(ScanResult s) { return this.mDevice.equals(s.mDevice); }

    public String getName() { return this.mDevice.getName(); }

}
