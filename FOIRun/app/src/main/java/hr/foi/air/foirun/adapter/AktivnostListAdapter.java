package hr.foi.air.foirun.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hr.foi.air.database.entities.ActivityType;
import hr.foi.air.database.entities.Aktivnost;
import hr.foi.air.foirun.MainActivity;
import hr.foi.air.foirun.R;
import hr.foi.air.foirun.events.OnExerciseClick;

public class AktivnostListAdapter extends BaseAdapter {

    private  Context mContext;
    private LayoutInflater inflater;
    private List<Aktivnost> itemsItems;
    private boolean isExercise;
    private ViewHolder holder;
    private Aktivnost aktivnost;
    private OnExerciseClick mCallback;
    private int position;


    public AktivnostListAdapter(MainActivity mActivity, List<Aktivnost> itemsItems, boolean isExercise) {
        this.isExercise = isExercise;
        this.mContext = mActivity.getBaseContext();
        this.itemsItems = itemsItems;
        this.mCallback = mActivity;
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
    public View getView(final int position, View scoreView, ViewGroup parent) {
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
            holder.arrowRight = (ImageView) scoreView.findViewById(R.id.arrow_right);

            holder.arrowRight.setVisibility(isExercise ? View.VISIBLE : View.INVISIBLE);

            holder.arrowRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onClick(itemsItems.get(position));
                }
            });

            scoreView.setTag(holder);

        } else {
            holder = (ViewHolder) scoreView.getTag();
        }

        aktivnost = itemsItems.get(position);
        holder.name.setText(aktivnost.getName());

        ActivityType type = ActivityType.getById(aktivnost.getType_id());

        holder.activity.setText(String.format( "%s", type.getName() ));

        float distance = (float)aktivnost.getDistance() / 1000;
        holder.distance.setText(String.format( "%.2f km", distance ));

        long milis = aktivnost.getTime();
        int seconds = (int) (milis / 1000) % 60 ;
        int minutes = (int) ((milis / (1000*60)) % 60);
        int hours   = (int) ((milis / (1000*60*60)) % 24);

        holder.time.setText(String.format( "%02d:%02d:%02d", hours, minutes, seconds ));

        return scoreView;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.position = selectedPosition;
    }

    static class ViewHolder {

        TextView activity;

        TextView distance;

        TextView time;

        TextView name;

        ImageView arrowRight;
    }

}
