package com.cowsill.myreminders;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class MyReminder implements Parcelable {

    private String mName;
    private String mLocation;
    private double mGeofenceLatitude;
    private double mGeofenceLongtitude;
    private String mMessage;

    public MyReminder(){}

    public MyReminder(String name, String location, double geofenceLatitude, double geofenceLongtitude, String message){
        mName = name;
        mLocation = location;
        mGeofenceLatitude = geofenceLatitude;
        mGeofenceLongtitude = geofenceLongtitude;
        mMessage = message;
    }

    protected MyReminder(Parcel in) {
        mName = in.readString();
        mLocation = in.readString();
        mGeofenceLatitude = in.readDouble();
        mGeofenceLongtitude = in.readDouble();
        mMessage = in.readString();
    }

    public static final Creator<MyReminder> CREATOR = new Creator<MyReminder>() {
        @Override
        public MyReminder createFromParcel(Parcel in) {
            return new MyReminder(in);
        }

        @Override
        public MyReminder[] newArray(int size) {
            return new MyReminder[size];
        }
    };

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getLocation() { return mLocation; }

    public double getGeofenceLatitude() {
        return mGeofenceLatitude;
    }

    public double getGeofenceLongtitude() {
        return mGeofenceLongtitude;
    }

    @NonNull
    @Override
    public String toString() {
        return mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.mName);
        dest.writeString(this.mLocation);
        dest.writeDouble(this.mGeofenceLatitude);
        dest.writeDouble(this.mGeofenceLongtitude);
        dest.writeString(this.mMessage);
    }
}
