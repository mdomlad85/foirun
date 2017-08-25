package hr.foi.air.foirun.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import hr.foi.air.database.entities.Achievement;
import hr.foi.air.foirun.R;

/**
 * Created by Matej on 22/08/2017.
 */

public class AchievementsListAdapter extends RecyclerView.Adapter<AchievementsListAdapter.ViewHolder> {

    private List<Achievement> mAchievements;

    private Context context;

    public AchievementsListAdapter(List<Achievement> mAchievements, Context context) {
        this.mAchievements = mAchievements;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View achievementRow = inflater.inflate(R.layout.achievement_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(achievementRow);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(AchievementsListAdapter.ViewHolder holder, int position) {
        Achievement achievement = mAchievements.get(position);
        holder.nameTv.setText(achievement.getName());

        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String dateFormated = df.format("dd.MM.yyyy hh:mm:ss", achievement.getDate()).toString();

        holder.dateTv.setText(dateFormated);

    }

    @Override
    public int getItemCount() {
        return mAchievements.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTv;
        public TextView nameTv;


        public ViewHolder(View itemView) {
            super(itemView);

            dateTv = (TextView) itemView.findViewById(R.id.date_row);
            nameTv = (TextView) itemView.findViewById(R.id.name_row);
        }
    }


}
