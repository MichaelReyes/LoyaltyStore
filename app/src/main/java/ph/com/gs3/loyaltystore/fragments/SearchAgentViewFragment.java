package ph.com.gs3.loyaltystore.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.AgentDeviceListAdapter;

/**
 * Created by Bryan-PC on 11/02/2016.
 */
public class SearchAgentViewFragment extends Fragment {

    public static final String TAG = SearchAgentViewFragment.class.getSimpleName();

    private SynchronizeViewFragmentEventListener synchronizeViewFragmentEventListener;

    private Activity activity;

    private AgentDeviceListAdapter agentDeviceListAdapter;

    private View rootView;

    private ListView lvDeviceList;

    private Button bScanDevices;

    public static SearchAgentViewFragment createInstance(AgentDeviceListAdapter agentDeviceListAdapter) {
        SearchAgentViewFragment searchAgentViewFragment = new SearchAgentViewFragment();
        searchAgentViewFragment.agentDeviceListAdapter = agentDeviceListAdapter;
        return searchAgentViewFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            synchronizeViewFragmentEventListener = (SynchronizeViewFragmentEventListener) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException(activity.getClass().getSimpleName() + " must implement SynchronizeViewFragmentEventListener");
        }

        this.activity = activity;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search_agent, container, false);

        this.rootView = rootView;

        lvDeviceList = (ListView) rootView.findViewById(R.id.Synchronize_lvDeviceList);
        lvDeviceList.setAdapter(agentDeviceListAdapter);
        lvDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                synchronizeViewFragmentEventListener.onCustomerDeviceClicked(
                        (WifiP2pDevice) agentDeviceListAdapter.getItem(position)
                );
            }
        });

        bScanDevices = (Button) rootView.findViewById(R.id.Synchronize_bScan);
        bScanDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronizeViewFragmentEventListener.onDiscoverPeersCommand();
            }
        });


        synchronizeViewFragmentEventListener.onViewReady();

        return rootView;
    }

    public interface SynchronizeViewFragmentEventListener {

        void onViewReady();

        void onDiscoverPeersCommand();

        void onCustomerDeviceClicked(WifiP2pDevice customerDevice);

    }

}
