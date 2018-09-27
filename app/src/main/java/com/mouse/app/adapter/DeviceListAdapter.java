package com.mouse.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    private int tempPosition = -1;  //记录已经点击的CheckBox的位置
    private int selectPosition = -1;

    public DeviceListAdapter(Activity context) {
        this.context = context;
        mDataList = new ArrayList<SearchResult>();
    }

    public void setDataList(List<SearchResult> datas) {
        mDataList.clear();
        mDataList.addAll(datas);
        notifyDataSetChanged();
    }

    //返回当前CheckBox选中的位置,便于获取值.
    public int getSelectPosition() {
        return selectPosition;
    }

    public List<SearchResult> getmDataList() {
        return mDataList;
    }


    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
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
        CheckBox checkbox;
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
            viewHolder.checkbox = view.findViewById(R.id.checkbox);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final SearchResult result = (SearchResult) getItem(position);
        viewHolder.deviceName.setText(result.getName());
        viewHolder.deviceAddress.setText(result.getAddress());
        viewHolder.deviceRSSI.setText(String.format("Rssi: %d", result.rssi));

        if (position == tempPosition) {
            viewHolder.checkbox.setChecked(true);
        } else {
            viewHolder.checkbox.setChecked(false);
        }

        viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener
                () {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    tempPosition = position;
                } else {
                    tempPosition = -1;
                }
                notifyDataSetChanged();
            }
        });

        return view;
    }
}

