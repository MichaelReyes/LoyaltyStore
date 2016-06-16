package ph.com.gs3.loyaltystore.models.values;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Bryan-PC on 04/05/2016.
 */
public class InitialInventoryForFirstAppUse {

    public static final String TAG = InitialInventoryForFirstAppUse.class.getSimpleName();

    private float initialValue;
    private boolean firstTimeAppUseFlag;

    public static InitialInventoryForFirstAppUse getDeviceRetailerFromSharedPreferences(Context context) {
        InitialInventoryForFirstAppUse initialInventoryForFirstAppUse = new InitialInventoryForFirstAppUse();

        SharedPreferences settings = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);

        initialInventoryForFirstAppUse.initialValue = settings.getFloat("INITIAL_VALUE", 1000);
        initialInventoryForFirstAppUse.firstTimeAppUseFlag = settings.getBoolean("FIRST_TIME_USE_FLAG", true);

        return initialInventoryForFirstAppUse;
    }

    public void save(Context context) {

        SharedPreferences settings = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putFloat("INITIAL_VALUE",initialValue);
        editor.putBoolean("FIRST_TIME_USE_FLAG", firstTimeAppUseFlag);

        editor.commit();
    }

    public float getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(float initialValue) {
        this.initialValue = initialValue;
    }

    public boolean isFirstTimeAppUseFlag() {
        return firstTimeAppUseFlag;
    }

    public void setFirstTimeAppUseFlag(boolean firstTimeAppUseFlag) {
        this.firstTimeAppUseFlag = firstTimeAppUseFlag;
    }
}
