package com.example.android.location.Util;


import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

//import com.google.android.gms.location.LocationClient;
public class BackgroundLocationService extends Service implements LocationListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

	IBinder mBinder = new LocalBinder();
	Intent intent;
    private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private boolean mInProgress;

	private Boolean servicesAvailable = false;

	public class LocalBinder extends Binder {
		public BackgroundLocationService getServerInstance() {
			return BackgroundLocationService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mInProgress = false;

		servicesAvailable = servicesConnected();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
		intent = new Intent("Location");
	}

	private boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			return true;
		} else {

			return false;
		}
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		if (!servicesAvailable || mGoogleApiClient.isConnected() || mInProgress)
			return START_STICKY;

		setUpLocationClientIfNeeded();
		if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting()
				&& !mInProgress) {
			mInProgress = true;
            mGoogleApiClient.connect();
		}
		return START_STICKY;
	}
	private void setUpLocationClientIfNeeded() {
		if (mGoogleApiClient == null)
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

	}
	@Override
	public void onLocationChanged(Location loc) {
		String msg = "sender:"+loc.getLatitude() + ","
				+ loc.getLongitude()+","+loc.getAccuracy();
		intent.putExtra("Latitude", loc.getLatitude());
		intent.putExtra("Longitude", loc.getLongitude());
		intent.putExtra("Altitude",loc.getAltitude());
        Log.d("debug", msg);
        sendBroadcast(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	

	@Override
	public void onDestroy() {
		// Turn off the request flag
		mInProgress = false;
		if (servicesAvailable && mGoogleApiClient != null) {

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);

			// Destroy the current location client
            mGoogleApiClient = null;
		}
		super.onDestroy();
	}
	@Override
	public void onConnected(Bundle bundle) {
		Log.d("debug", "Connected");
		// Request location updates using static settings
        mLocationRequest = LocationRequest.create();
        mLocationRequest
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(Constants.UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
	}

    @Override
    public void onConnectionSuspended(int i) {

    }
	/*
    @Override
	public void onDisconnected() {
		mInProgress = false;
        mGoogleApiClient = null;
	}*/
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d("debug", "Connected Failed");
		mInProgress = false;
		if (connectionResult.hasResolution()) {
		} else {

		}
	}

}