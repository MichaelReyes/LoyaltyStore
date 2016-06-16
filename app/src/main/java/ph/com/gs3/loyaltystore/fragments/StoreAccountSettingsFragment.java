package ph.com.gs3.loyaltystore.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import ph.com.gs3.loyaltystore.R;

/**
 * Created by Bryan-PC on 28/04/2016.
 */
public class StoreAccountSettingsFragment extends Fragment {

    public static final String TAG = StoreAccountSettingsFragment.class.getSimpleName();

    private Context context;
    private FragmentActivity activity;

    private EditText etRetailName;
    private EditText etServerUrl;

    private Button bSave;
    private Button bRegister;
    private Button bSignOut;

    private StoreAccountSettingsFragmentListener listener;

    public StoreAccountSettingsFragment createInstance(){

        StoreAccountSettingsFragment storeAccountSettingsFragment = new StoreAccountSettingsFragment();
        return storeAccountSettingsFragment;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (FragmentActivity) context;

        if(context instanceof StoreAccountSettingsFragmentListener){
            listener = (StoreAccountSettingsFragmentListener) context;
        }else{
            throw new RuntimeException(
                    getContext().getClass().getSimpleName() +
                            " must implement StoreAccountSettingsFragmentListener"
            );
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (FragmentActivity) activity;
        this.context = activity;

        if(context instanceof StoreAccountSettingsFragmentListener){
            listener = (StoreAccountSettingsFragmentListener) activity;
        }else{
            throw new RuntimeException(
                    getContext().getClass().getSimpleName() +
                            " must implement StoreAccountSettingsFragmentListener"
            );
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings_store_account, container, false);

        etRetailName = (EditText) rootView.findViewById(R.id.Settings_etStoreName);
        etServerUrl = (EditText) rootView.findViewById(R.id.Settings_etServerAddress);

        bRegister = (Button) rootView.findViewById(R.id.Settings_bRegister);
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRegisterStore(etServerUrl.getText().toString());
            }
        });

        bSave = (Button) rootView.findViewById(R.id.Settings_bSaveStoreAccount);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onSaveStoreAccountSettings(
                        etServerUrl.getText().toString(),
                        etRetailName.getText().toString()

                );

            }
        });

        bSignOut = (Button) rootView.findViewById(R.id.Settings_bSignOut);
        bSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSignOut();
            }
        });

        listener.onStoreSettingsFragmentViewReady();

        return rootView;
    }

    public void setStoreName(String storeName){
        etRetailName.setText(storeName);
    }

    public void setUrl(String url){
        etServerUrl.setText(url);
    }

    public interface StoreAccountSettingsFragmentListener{

        void onStoreSettingsFragmentViewReady();

        void onSaveStoreAccountSettings(String serverURL, String storeName);

        void onRegisterStore(String serverURL);

        void onSignOut();

    }
}
