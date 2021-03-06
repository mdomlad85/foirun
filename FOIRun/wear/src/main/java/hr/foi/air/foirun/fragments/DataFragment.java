package hr.foi.air.foirun.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import hr.foi.air.foirun.R;

/**
 * A fragment that shows a list of DataItems received from the phone
 */
public class DataFragment extends Fragment {

    private DataItemAdapter mDataItemListAdapter;
    private TextView mIntroText;
    private boolean mInitialized;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.data_fragment, container, false);
        ListView dataItemList = (ListView) view.findViewById(R.id.dataItem_list);
        mIntroText = (TextView) view.findViewById(R.id.intro);
        mDataItemListAdapter = new DataItemAdapter(getActivity(),
                android.R.layout.simple_list_item_1);
        dataItemList.setAdapter(mDataItemListAdapter);
        mInitialized = true;
        return view;
    }

    public void appendItem(String title, String text) {
        if (!mInitialized) {
            return;
        }
        mIntroText.setVisibility(View.INVISIBLE);
        mDataItemListAdapter.add(new Event(title, text));
    }

    private static class DataItemAdapter extends ArrayAdapter<Event> {

        private final Context mContext;

        public DataItemAdapter(Context context, int unusedResource) {
            super(context, unusedResource);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(android.R.layout.two_line_list_item, null);
                convertView.setTag(holder);
                holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
                holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Event event = getItem(position);
            holder.text1.setText(event.title);
            holder.text2.setText(event.text);
            return convertView;
        }

        private class ViewHolder {

            TextView text1;
            TextView text2;
        }
    }

    private class Event {

        String title;
        String text;

        public Event(String title, String text) {
            this.title = title;
            this.text = text;
        }
    }
}
