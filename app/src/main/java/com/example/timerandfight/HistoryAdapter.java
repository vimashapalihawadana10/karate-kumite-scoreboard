package com.example.timerandfight;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    ArrayList<FightRecord> fightList;

    public HistoryAdapter(ArrayList<FightRecord> fightList) {
        this.fightList = fightList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fight_history, parent, false);

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull HistoryViewHolder holder,
            int position) {

        FightRecord record = fightList.get(position);

        // Player names
        holder.txtAoName.setText(record.aoName);
        holder.txtAkaName.setText(record.akaName);

        // Scores
        holder.txtAoScore.setText(
                String.valueOf(record.aoScore)
        );

        holder.txtAkaScore.setText(
                String.valueOf(record.akaScore)
        );

        // Result information
        holder.txtWinner.setText(
                "Winner: " + record.winner
        );

        holder.txtReason.setText(
                "Reason: " + record.reason
        );

        holder.txtDate.setText(
                record.dateTime
        );

        // SHARE RESULT
        holder.txtShareResult.setOnClickListener(v -> {

            Intent intent = new Intent(
                    v.getContext(),
                    ShareResultActivity.class
            );

            intent.putExtra(
                    "aoName",
                    record.aoName
            );

            intent.putExtra(
                    "akaName",
                    record.akaName
            );

            intent.putExtra(
                    "aoScore",
                    record.aoScore
            );

            intent.putExtra(
                    "akaScore",
                    record.akaScore
            );

            intent.putExtra(
                    "winner",
                    record.winner
            );

            intent.putExtra(
                    "reason",
                    record.reason
            );

            intent.putExtra(
                    "dateTime",
                    record.dateTime
            );

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return fightList.size();
    }

    public static class HistoryViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtAoName;
        TextView txtAkaName;

        TextView txtAoScore;
        TextView txtAkaScore;

        TextView txtWinner;
        TextView txtReason;
        TextView txtDate;

        TextView txtShareResult;

        public HistoryViewHolder(
                @NonNull View itemView) {

            super(itemView);

            txtAoName =
                    itemView.findViewById(R.id.txtAoName);

            txtAkaName =
                    itemView.findViewById(R.id.txtAkaName);

            txtAoScore =
                    itemView.findViewById(R.id.txtAoScore);

            txtAkaScore =
                    itemView.findViewById(R.id.txtAkaScore);

            txtWinner =
                    itemView.findViewById(R.id.txtWinner);

            txtReason =
                    itemView.findViewById(R.id.txtReason);

            txtDate =
                    itemView.findViewById(R.id.txtDate);

            txtShareResult =
                    itemView.findViewById(R.id.txtShareResult);
        }
    }
}