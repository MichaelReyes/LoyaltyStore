package ph.com.gs3.loyaltystore.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Ervinne Sodusta on 2/3/2016.
 */
public class User {

    private static final String PREF_NAME = "user_preferences";

    public static final String KEY_ID = "user_id";
    public static final String KEY_FORMALISTICS_SERVER = "user_formalistics_server";
    public static final String KEY_SERVER = "user_server";
    public static final String KEY_NAME = "user_name";
    public static final String KEY_EMAIL = "user_email";
    public static final String KEY_PASSWORD = "user_password";

    int id;
    String formalisticsServer;
    String server;
    String name;
    String email;
    String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFormalisticsServer() {
        return formalisticsServer;
    }

    public void setFormalisticsServer(String formalisticsServer) {
        this.formalisticsServer = formalisticsServer;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return server + " " + formalisticsServer + ": " + email;
    }

    public static void clear(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();

        editor.commit();
        editor.apply();

        Log.v("User", "User cleared");
    }

    public void save(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_ID, id);
        if (formalisticsServer != null) {
            editor.putString(KEY_FORMALISTICS_SERVER, formalisticsServer);
        }

        if (server != null) {
            editor.putString(KEY_SERVER, server);
        }

        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);

        editor.commit();
        editor.apply();

        Log.v("User", "User saved " + formalisticsServer + " " + server);

    }

    public static User getSavedUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        User user = new User();

        user.id = preferences.getInt(KEY_ID, 0);
        user.server = preferences.getString(KEY_SERVER, null);
        user.formalisticsServer = preferences.getString(KEY_FORMALISTICS_SERVER, null);
        user.name = preferences.getString(KEY_NAME, null);
        user.email = preferences.getString(KEY_EMAIL, null);
        user.password = preferences.getString(KEY_PASSWORD, null);

        Log.v("User", "Got user: " + user);

        return user;

    }

}
