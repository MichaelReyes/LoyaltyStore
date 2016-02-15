package ph.com.gs3.loyaltystore.adapters;


import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;

import java.util.List;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.models.values.DeviceInfo;

/**
 * Created by Michael Reyes on 02/02/2016.
 */
public class AgentDeviceListAdapter extends BaseAdapter {

    private Context context;
    private List<WifiP2pDevice> agentDeviceList;

    public AgentDeviceListAdapter(Context context, List<WifiP2pDevice> agentDeviceList) {
        this.context = context;
        this.agentDeviceList = agentDeviceList;
    }

    @Override
    public int getCount() {
        return agentDeviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return agentDeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        CustomerDeviceViewHolder viewHolder;

        WifiP2pDevice customerDevice = (WifiP2pDevice) getItem(position);
        DeviceInfo deviceInfo = null;
        try {
//            deviceInfo = DeviceInfo.unserialize(customerDevice.secondaryDeviceType);
            deviceInfo = DeviceInfo.unserialize(customerDevice.deviceName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_agent_device, parent, false);

            viewHolder = new CustomerDeviceViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (CustomerDeviceViewHolder) row.getTag();

        if (deviceInfo == null) {
            viewHolder.tvDisplayName.setText(customerDevice.deviceName);
        } else {
            viewHolder.tvDisplayName.setText(deviceInfo.getOwnerDisplayName());
        }

        viewHolder.tvDeviceAddress.setText("(" + customerDevice.deviceAddress + ")");

        return row;
    }

    private static class CustomerDeviceViewHolder {

        final TextView tvDisplayName;
        final TextView tvDeviceAddress;

        public CustomerDeviceViewHolder(View view) {

            tvDisplayName = (TextView) view.findViewById(R.id.Agent_tvDisplayName);
            tvDeviceAddress = (TextView) view.findViewById(R.id.Agent_tvDeviceAddress);

        }

    }
}
