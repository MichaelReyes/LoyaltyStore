package ph.com.gs3.loyaltystore.models.values;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import java.io.Serializable;

/**
 * Created by Ervinne Sodusta on 8/17/2015.
 */
public class Retailer implements Serializable {

    public static final String TAG = Retailer.class.getSimpleName();

    private long storeId;
    private String deviceId;
    private String advertisment;
    private String storeName;
    private String serverUrl;
    private String macAddress;
    private int servicePortNumber;

    public static Retailer getDeviceRetailerFromSharedPreferences(Context context) {
        Retailer retailer = new Retailer();

        SharedPreferences settings = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);

        retailer.storeId = settings.getLong("STORE_ID", 0);
        retailer.deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        retailer.storeName = settings.getString("STORE_NAME", "");
        retailer.advertisment = settings.getString("ADVERTISEMENT", "");
        retailer.servicePortNumber = settings.getInt("SERVICE_PORT_NUMBER", 3001);
        retailer.serverUrl = settings.getString("SERVER_URL", "");
        retailer.macAddress = settings.getString("MAC_ADDRESS","");

        return retailer;
    }

    @Override
    public String toString() {
        return storeName + ":Service Port = " + servicePortNumber;
    }

    public void save(Context context) {

        SharedPreferences settings = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putLong("STORE_ID",storeId);
        editor.putString("STORE_NAME", storeName);
        editor.putString("ADVERTISEMENT", advertisment);
        editor.putInt("SERVICE_PORT_NUMBER", servicePortNumber);
        editor.putString("SERVER_URL", serverUrl);

        editor.commit();
    }

    public DeviceInfo getDeviceInfo() {
        DeviceInfo deviceInfo = new DeviceInfo();

        deviceInfo.setOwnerDisplayName(storeName);
        deviceInfo.setType(DeviceInfo.Type.RETAILER);

        return deviceInfo;
    }

    //<editor-fold desc="Getters & Setters">

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAdvertisment() {
        return advertisment;
    }

    public void setAdvertisment(String advertisment) {
        this.advertisment = advertisment;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getServicePortNumber() {
        return servicePortNumber;
    }

    public void setServicePortNumber(int servicePortNumber) {
        this.servicePortNumber = servicePortNumber;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public long getStoreId() {
        return storeId;
    }

    public void setStoreId(long storeId) {
        this.storeId = storeId;
    }

    //</editor-fold>
}
