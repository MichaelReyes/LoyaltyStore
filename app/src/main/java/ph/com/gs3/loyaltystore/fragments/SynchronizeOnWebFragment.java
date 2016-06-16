package ph.com.gs3.loyaltystore.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.models.User;
import ph.com.gs3.loyaltystore.models.values.Retailer;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Created by Bryan-PC on 01/05/2016.
 */
public class SynchronizeOnWebFragment extends SyncDataFragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = SynchronizeOnWebFragment.class.getSimpleName();

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    private AutoCompleteTextView etFormalisticsServer;
    private AutoCompleteTextView etServer;
    private AutoCompleteTextView etEmail;
    private EditText etPassword;
    private View vProgress;
    private View vFormLogin;

    private SynchronizeOnWebEventListener listener;

    public SynchronizeOnWebFragment(){

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SynchronizeOnWebFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SynchronizeOnWebFragment createInstance() {
        SynchronizeOnWebFragment fragment = new SynchronizeOnWebFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //  maintain arguments here
            //  mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SynchronizeOnWebEventListener) {
            listener = (SynchronizeOnWebEventListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SynchronizeOnWebEventListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_synchronize_on_web, container, false);

        initializeDataSyncViews(rootView);

        User user = User.getSavedUser(getContext());

        etFormalisticsServer = (AutoCompleteTextView) rootView.findViewById(R.id.Login_etFormalisticsServer);

        Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(getContext());
        etFormalisticsServer.setText(user.getFormalisticsServer());

        etServer = (AutoCompleteTextView) rootView.findViewById(R.id.Login_etServer);

        etEmail = (AutoCompleteTextView) rootView.findViewById(R.id.Login_etEmail);
        etEmail.setText(user.getEmail());
        populateAutoComplete();

        etPassword = (EditText) rootView.findViewById(R.id.Login_etPassword);
        etPassword.setText(user.getPassword());
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });

        vFormLogin = rootView.findViewById(R.id.login_form);
        vProgress = rootView.findViewById(R.id.login_progress);

        //  dev mode
        /*etFormalisticsServer.setText("192.168.0.120:9007");
        etServer.setText("192.168.0.73:85");
        etEmail.setText("donbenitos@gmail.com");
        etPassword.setText("password");*/

        bSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User currentUser = User.getSavedUser(getContext());
                currentUser.setEmail(etEmail.getText().toString());
                currentUser.setFormalisticsServer(prependHttp(etFormalisticsServer.getText().toString()));
                currentUser.setServer(prependHttp(etFormalisticsServer.getText().toString()));
                currentUser.setPassword(etPassword.getText().toString());
                currentUser.save(getContext());

                Log.d(TAG, "Before Sync : " + prependHttp(etFormalisticsServer.getText().toString()) );

                listener.onSynchronizeOnWeb();
                //enableSyncButton(false);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    private String prependHttp(String server) {

        if (!server.toLowerCase().contains("http://") && !server.toLowerCase().contains("https://")) {
            return "http://" + server;
        } else {
            return server;
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
        if (getContext().checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(etEmail, "Contacts permissions are needed for providing email\n" +
                    "        completions.", Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        return new CursorLoader(getContext(),
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        etEmail.setAdapter(adapter);
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

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    public interface SynchronizeOnWebEventListener{
        void onSynchronizeOnWeb();
    }


}
