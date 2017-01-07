package hr.foi.air.foirun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

import hr.foi.air.database.entities.ActivityType;
import hr.foi.air.database.entities.Aktivnost;
import hr.foi.air.foirun.R;

public class AktivnostListAdapter extends BaseAdapter {

    private  Context mContext;
    private LayoutInflater inflater;
    private List<Aktivnost> itemsItems;



    public AktivnostListAdapter(Context context, List<Aktivnost> itemsItems) {

        this.mContext = context;
        this.itemsItems = itemsItems;

    }

    @Override
    public int getCount() {
        return itemsItems.size();
    }

    @Override
    public Object getItem(int location) {
        return itemsItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View scoreView, ViewGroup parent) {
        ViewHolder holder;
        if (inflater == null) {
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (scoreView == null) {

            scoreView = inflater.inflate(R.layout.aktivnost_row, parent, false);
            holder = new ViewHolder();
            holder.activity = (TextView) scoreView.findViewById(R.id.activity_row);
            holder.distance = (TextView) scoreView.findViewById(R.id.distance_row);
            holder.time = (TextView) scoreView.findViewById(R.id.time_row);
            holder.name = (TextView) scoreView.findViewById(R.id.name_row);

            scoreView.setTag(holder);

        } else {
            holder = (ViewHolder) scoreView.getTag();
        }

        final Aktivnost m = itemsItems.get(position);
        holder.name.setText(m.getName());

        ActivityType type = ActivityType.getById(m.getType_id());

        holder.activity.setText(String.format( "%s", type.getName() ));

        holder.distance.setText(String.format( "%.2f m", m.getDistance() ));

        long milis = m.getTime();
        int seconds = (int) (milis / 1000) % 60 ;
        int minutes = (int) ((milis / (1000*60)) % 60);
        int hours   = (int) ((milis / (1000*60*60)) % 24);

        holder.time.setText(String.format( "%02d:%02d:%02d", hours, minutes, seconds ));

        return scoreView;
    }

    static class ViewHolder {

        TextView activity;

        TextView distance;

        TextView time;

        TextView name;
    }

}
