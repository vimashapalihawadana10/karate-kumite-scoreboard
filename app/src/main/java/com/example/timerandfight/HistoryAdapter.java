package com.example.timerandfight;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HistoryAdapter
        extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    ArrayList<FightRecord> fightList;

    FirebaseFirestore db;

    public HistoryAdapter(ArrayList<FightRecord> fightList) {

        this.fightList = fightList;

        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_fight_history,
                        parent,
                        false
                );

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

        // Result
        holder.txtWinner.setText(
                "Winner: " + record.winner
        );

        holder.txtReason.setText(
                "Reason: " + record.reason
        );

        holder.txtDate.setText(
                record.dateTime
        );


        // =====================================================
        // DELETE FIGHT
        // =====================================================

        holder.txtDeleteFight.setOnClickListener(v -> {

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(v.getContext());

            builder.setTitle("Delete Fight");

            builder.setMessage(
                    "Are you sure you want to delete this fight history?"
            );

            builder.setPositiveButton(
                    "Delete",
                    (dialog, which) -> {

                        deleteFight(
                                record,
                                holder.getBindingAdapterPosition(),
                                holder
                        );
                    }
            );

            builder.setNegativeButton(
                    "Cancel",
                    (dialog, which) -> dialog.dismiss()
            );

            builder.show();
        });
    }


    private void deleteFight(
            FightRecord record,
            int position,
            HistoryViewHolder holder) {

        if (position == RecyclerView.NO_POSITION) {
            return;
        }

        if (record.id == null ||
                record.id.trim().isEmpty()) {

            Toast.makeText(
                    holder.itemView.getContext(),
                    "Unable to find fight document",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        db.collection("fights")
                .document(record.id)
                .delete()

                .addOnSuccessListener(unused -> {

                    fightList.remove(position);

                    notifyItemRemoved(position);

                    notifyItemRangeChanged(
                            position,
                            fightList.size()
                    );

                    Toast.makeText(
                            holder.itemView.getContext(),
                            "Fight deleted",
                            Toast.LENGTH_SHORT
                    ).show();
                })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            holder.itemView.getContext(),
                            "Delete failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
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

        TextView txtDeleteFight;


        public HistoryViewHolder(
                @NonNull View itemView) {

            super(itemView);

            txtAoName =
                    itemView.findViewById(
                            R.id.txtAoName
                    );

            txtAkaName =
                    itemView.findViewById(
                            R.id.txtAkaName
                    );

            txtAoScore =
                    itemView.findViewById(
                            R.id.txtAoScore
                    );

            txtAkaScore =
                    itemView.findViewById(
                            R.id.txtAkaScore
                    );

            txtWinner =
                    itemView.findViewById(
                            R.id.txtWinner
                    );

            txtReason =
                    itemView.findViewById(
                            R.id.txtReason
                    );

            txtDate =
                    itemView.findViewById(
                            R.id.txtDate
                    );

            txtDeleteFight =
                    itemView.findViewById(
                            R.id.txtDeleteFight
                    );
        }
    }
}