package com.foodciti.foodcitipartener.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class CheckConnection {

	NetworkInfo networkInfo, mobileInfo;
	WifiManager wifiSpeedTest;
	Context context;

	public CheckConnection(Context context) {
		this.context = context;
	}

	public Boolean checkNow() {

		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			networkInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			mobileInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

			if (networkInfo.isConnected() || mobileInfo.isConnected()) {
				return true;
			}
		} catch (Exception e) {
			System.out
					.println("CheckConnectivity Exception: " + e.getMessage());
		}

		return false;
	}

	public static boolean isNetworkAvailable(Context mContext) {
		ConnectivityManager connectivityManager =
				(ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}

		return false;
	}
	public String getNetworkType() {
		String type = "Unknown";
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifiNetwork = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo mobileNetwork = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo maxNetwork = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
			if (wifiNetwork.isConnectedOrConnecting()) {

				type = "Wifi";

			} else if (mobileNetwork.isConnectedOrConnecting()) {
				switch (mobileNetwork.getSubtype()) {
				case 7:
					type = "1xRTT";
					break;

				case 4:
					type = "CDMA";
					break;
				case 2:
					type = "EDGE";
					break;
				case 14:
					type = "eHRPD";
					break;

				case 5:
					type = "EVDO rev. 0";
					break;

				case 6:
					type = "EVDO rev. A";
					break;

				case 12:
					type = "EVDO rev. B";
					break;

				case 1:
					type = "GPRS";
					break;

				case 8:
					type = "HSDPA";
					break;

				case 10:
					type = "HSPA";
					break;

				case 15:
					type = "HSPA+";
					break;

				case 9:
					type = "HSUPA";
					break;

				case 11:
					type = "iDen";

				case 13:
					type = "LTE";

				case 3:
					type = "UMTS";

				case 0:
					type = "Unknown";

				default:
					type = "Unknown";
				}

			}
			else if(maxNetwork.isConnectedOrConnecting())
			{
				type="4G";
			}
			

		} catch (Exception e) {
			System.out
					.println("CheckConnectivity Exception: " + e.getMessage());
		}

		return type;
	}

}
