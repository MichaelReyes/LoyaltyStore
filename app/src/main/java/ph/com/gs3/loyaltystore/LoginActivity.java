package ph.com.gs3.loyaltystore;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.models.User;
import ph.com.gs3.loyaltystore.models.api.ServiceGenerator;
import ph.com.gs3.loyaltystore.models.api.UsersAPI;
import ph.com.gs3.loyaltystore.models.api.objects.FormalisticsAPIResponse;
import ph.com.gs3.loyaltystore.models.api.objects.FormalisticsLoginResponse;
import ph.com.gs3.loyaltystore.models.api.objects.FormalisticsUser;
import ph.com.gs3.loyaltystore.models.api.objects.UserDeviceLogRequest;
import ph.com.gs3.loyaltystore.models.values.Retailer;
import retrofit2.Call;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    public static final String TAG = LoginActivity.class.getSimpleName();

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private FormalisticsUserLoginTask formalisticsAuthTask = null;

    // UI references.
    private AutoCompleteTextView etFormalisticsServer;
    private AutoCompleteTextView etServer;
    private AutoCompleteTextView etEmail;
    private EditText etPassword;
    private View vProgress;
    private View vFormLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        etFormalisticsServer = (AutoCompleteTextView) findViewById(R.id.Login_etFormalisticsServer);
        etServer = (AutoCompleteTextView) findViewById(R.id.Login_etServer);

        etEmail = (AutoCompleteTextView) findViewById(R.id.Login_etEmail);
        populateAutoComplete();

        etPassword = (EditText) findViewById(R.id.Login_etPassword);
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        vFormLogin = findViewById(R.id.login_form);
        vProgress = findViewById(R.id.login_progress);

        //  dev mode
//        etFormalisticsServer.setText("192.168.1.7:9007");
        etFormalisticsServer.setText("192.168.0.140:9007");
//        etServer.setText("192.168.1.7");
        //etServer.setText("192.168.0.120:85");
        etEmail.setText("donbenitos@gmail.com");
        etPassword.setText("password");

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(etEmail, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (formalisticsAuthTask != null) {
            return;
        }

        // Reset errors.
        etEmail.setError(null);
        etPassword.setError(null);

        // Store values at the time of the login attempt.
        String formalisticsServer = etFormalisticsServer.getText().toString();
        String server = etServer.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //  check for server
        if (TextUtils.isEmpty(formalisticsServer)) {
            etFormalisticsServer.setError("Formalistics Server is required!");
            focusView = etFormalisticsServer;
            cancel = true;
        }

/*
        if (TextUtils.isEmpty(server)) {
            etServer.setError("Server is required!");
            focusView = etServer;
            cancel = true;
        }
*/

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_field_required));
            focusView = etEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            etEmail.setError(getString(R.string.error_invalid_email));
            focusView = etEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
/*
            mAuthTask = new UserLoginTask(formalisticsServer, server, email, password);
            mAuthTask.execute((Void) null);
*/
            formalisticsAuthTask = new FormalisticsUserLoginTask(formalisticsServer, server, email, password);
            formalisticsAuthTask.execute((Void) null);

        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 3;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            vFormLogin.setVisibility(show ? View.GONE : View.VISIBLE);
            vFormLogin.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    vFormLogin.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            vProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            vProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    vProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            vProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            vFormLogin.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        etEmail.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */

    public class FormalisticsUserLoginTask extends AsyncTask<Void, Void, Boolean>
    {

        private final String formalisticsServer;
        private final String server;
        private final String email;
        private final String password;

        FormalisticsUserLoginTask(String formalisticsServer, String server, String email, String password) {
            this.formalisticsServer = prependHttp(formalisticsServer);
            this.server = prependHttp(server);
            this.email = email;
            this.password = password;
        }

        private String prependHttp(String server) {

            if (!server.toLowerCase().contains("http://")) {
                return "http://" + server;
            } else {
                return server;
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(LoginActivity.this);

            ServiceGenerator serviceGenerator = new ServiceGenerator(LoginActivity.this, formalisticsServer);
            UsersAPI usersAPI = serviceGenerator.createService(UsersAPI.class);

            Call<FormalisticsLoginResponse> call = usersAPI.formalisticsLogin(email, password);
            try {
                retrofit2.Response response = call.execute();

                if (response != null) {
                    FormalisticsLoginResponse loginResponse = (FormalisticsLoginResponse) response.body();
                    if (loginResponse == null) {
                        return false;
                    }

                    FormalisticsUser formalisticsUser = loginResponse.results;
                    Gson gson = new Gson();
                    Log.d(TAG, "Formalistics user login response : " + gson.toJson(loginResponse.results));

                    User loggedInUser = new User();

                    if (formalisticsUser != null) {
                        loggedInUser.setId((int) formalisticsUser.id);
                        loggedInUser.setEmail(formalisticsUser.email);
                        loggedInUser.setName(formalisticsUser.display_name);
                        loggedInUser.setFormalisticsServer(formalisticsServer);
                        loggedInUser.setPassword(password);
                        loggedInUser.setServer(server);
                        loggedInUser.save(LoginActivity.this);

                        Log.v(TAG, loginResponse.toString());
                        Log.v(TAG, loggedInUser.toString());

                        if(retailer.getStoreId() != 0 && !"".equals(retailer.getStoreName())){

                            UserDeviceLogRequest userDeviceLogRequest = new UserDeviceLogRequest();
                            userDeviceLogRequest.user_id = loggedInUser.getId();
                            userDeviceLogRequest.user_name = loggedInUser.getName();
                            userDeviceLogRequest.branch_id = retailer.getStoreId();
                            userDeviceLogRequest.branch_name = retailer.getStoreName();

                            Log.d(TAG, "UserDeviceLogRequest : " + gson.toJson(userDeviceLogRequest)
                                    + " ~~~ " + gson.toJson(retailer));

                            Call<FormalisticsAPIResponse> logUserTimeInCall
                                    = usersAPI.logUserTimeIn(userDeviceLogRequest);

                            retrofit2.Response logUserTimeInResponse = logUserTimeInCall.execute();

                            if(logUserTimeInResponse != null){
                                FormalisticsAPIResponse apiResponse =
                                        (FormalisticsAPIResponse) logUserTimeInResponse.body();

                                Log.d(TAG, apiResponse.status);

                            }

                        }




                        return true;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            formalisticsAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
                Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                startActivity(intent);
            } else {
//                etPassword.setError(getString(R.string.error_incorrect_password));
//                etPassword.requestFocus();
                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            formalisticsAuthTask = null;
            showProgress(false);
        }

    }
}

