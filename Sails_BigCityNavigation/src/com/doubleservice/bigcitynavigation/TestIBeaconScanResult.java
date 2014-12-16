package com.doubleservice.bigcitynavigation;

import android.net.wifi.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;

public class TestIBeaconScanResult{
    /** The network name. */
    //public String SSID;
    /** Ascii encoded SSID. This will replace SSID when we deprecate it. @hide */
    //public String wifiSsid;
    /** The address of the access point. */
    //public String BSSID;
 
    public String Major,Minor;
    /**
     * The detected signal level in dBm. At least those are the units used by
     * the TI driver.
     */
    //public int level;
    public int rssi;
    /** {@hide} */
    public TestIBeaconScanResult(String Major, String Minor, int rssi) {
        //this.wifiSsid = wifiSsid;
        this.Major = Major;//wifiSsid.toString();
        this.Minor = Minor;
         this.rssi = rssi;
     }
   @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String none = "<none>";
        sb.append("Major: ").
            append(Major).
            append(", Minor: ").
            append(Minor == null ? none : Minor).
            append(", rssi: ").
            append(rssi);
                    return sb.toString();
    }
   

}
