package com.mouse.app.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.inuker.bluetooth.library.search.SearchResult;
import com.mouse.app.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * @author bobo
 * @date 2018/9/24
 */
public class DeviceListAdapter extends BaseAdapter implements Comparator<SearchResult> {

    private Activity context;

    private List<SearchResult> mDataList;
    private AdressListenser adressListenser;


    public interface AdressListenser {
        void beginConncet(String adress);
    }

    public DeviceListAdapter(Activity context, AdressListenser adressListenser) {
        this.context = context;
        this.adressListenser = adressListenser;
        mDataList = new ArrayList<SearchResult>();
    }

    public void setDataList(List<SearchResult> datas) {
        mDataList.clear();
        mDataList.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public SearchResult getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int compare(SearchResult lhs, SearchResult rhs) {
        return rhs.rssi - lhs.rssi;
    }

    private static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRSSI;
        TextView deviceState;
        TextView tvConnect;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(
                    R.layout.listitem_device, null, false);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            viewHolder.deviceRSSI = (TextView) view.findViewById(R.id.device_RSSI);
            viewHolder.deviceState = (TextView) view.findViewById(R.id.state);
            viewHolder.tvConnect = view.findViewById(R.id.tvConnect);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final SearchResult result = (SearchResult) getItem(position);
        viewHolder.deviceName.setText(result.getName());
        viewHolder.deviceAddress.setText(result.getAddress());
        viewHolder.deviceRSSI.setText(String.format("Rssi: %d", result.rssi));


        viewHolder.tvConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adressListenser!=null){
                    adressListenser.beginConncet(getItem(position).getAddress());
                }
            }
        });

        return view;
    }
}

