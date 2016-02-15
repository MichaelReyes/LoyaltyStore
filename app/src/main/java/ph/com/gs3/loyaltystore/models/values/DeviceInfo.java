package ph.com.gs3.loyaltystore.models.values;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Ervinne Sodusta on 8/18/2015.
 */
public class DeviceInfo implements Serializable {

    private String ownerDisplayName;
    private Type type;

    public enum Type {
        RETAILER, CUSTOMER, AGENT
    }

    public String serialize() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("O", ownerDisplayName);

        switch (type) {
            case AGENT:
                json.put("T", 2);
                break;
            case RETAILER:
                json.put("T", 1);
                break;
            case CUSTOMER:
                json.put("T", 0);
                break;
        }

        return json.toString();
    }

    public static DeviceInfo unserialize(String serializedDeviceInfo) throws JSONException {

        if (serializedDeviceInfo == null) {
            return null;
        }

        JSONObject json = new JSONObject(serializedDeviceInfo);
        DeviceInfo deviceInfo = new DeviceInfo();

        deviceInfo.setOwnerDisplayName(json.getString("O"));

        switch(json.getInt("T")) {
            case 2:
                deviceInfo.setType(Type.AGENT);
                break;
            case 1:
                deviceInfo.setType(Type.RETAILER);
                break;
            case 0:
                deviceInfo.setType(Type.CUSTOMER);
                break;
        }

        return deviceInfo;

    }

    public String getOwnerDisplayName() {
        return ownerDisplayName;
    }

    public void setOwnerDisplayName(String ownerDisplayName) {
        this.ownerDisplayName = ownerDisplayName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
