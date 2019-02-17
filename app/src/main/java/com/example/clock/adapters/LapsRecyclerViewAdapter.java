package com.example.clock.adapters;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clock.R;
import com.example.clock.models.Lap;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LapsRecyclerViewAdapter extends RecyclerView.Adapter<LapsRecyclerViewAdapter.LapsViewHolder> {

    private List<Lap> mListLaps = new ArrayList<>();

    @NonNull
    @Override
    public LapsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.row_lap,
                viewGroup,
                false);
        return new LapsViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LapsViewHolder lapsViewHolder, int i) {
        lapsViewHolder.textId.setText((i + 1) + ".");
        lapsViewHolder.textTime.setText(getFormattedTimeFromMill(mListLaps.get(i).getTime()));
    }

    @Override
    public int getItemCount() {
        return mListLaps.size();
    }

    public void addNewLap(Lap lap) {
        mListLaps.add(lap);
        notifyItemInserted(mListLaps.size() - 1);
    }

    public void deleteAllLaps() {
        mListLaps.clear();
        notifyDataSetChanged();
    }

    private String getFormattedTimeFromMill(long milliseconds) {
        int seconds = (int) milliseconds / 1_000 % 60;
        int minutes = (int) milliseconds / 1_000 / 60 % 60;
        int hours = (int) milliseconds / 1_000 / 60 / 60 % 24;

        return String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                hours,
                minutes,
                seconds);
    }

    class LapsViewHolder extends RecyclerView.ViewHolder {

        private TextView textId, textTime;

        LapsViewHolder(@NonNull View itemView) {
            super(itemView);

            textId = itemView.findViewById(R.id.text_row_lap_id);
            textTime = itemView.findViewById(R.id.text_row_lap_time);
        }

    }

}
