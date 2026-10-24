package com.example.timerandfight;

import android.app.AlertDialog;
import android.content.Intent;
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


        // =====================================================
        // SHOW FIGHT INFORMATION
        // =====================================================

        holder.txtAoName.setText(record.aoName);

        holder.txtAkaName.setText(record.akaName);


        holder.txtAoScore.setText(
                String.valueOf(record.aoScore)
        );

        holder.txtAkaScore.setText(
                String.valueOf(record.akaScore)
        );


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
        // SHARE RESULT
        // =====================================================

        holder.txtShareResult.setOnClickListener(v -> {

            Intent intent = new Intent(
                    v.getContext(),
                    ShareResultActivity.class
            );


            // Send fight information to ShareResultActivity

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


            // Open Share Result screen
            v.getContext().startActivity(intent);
        });


        // =====================================================
        // DELETE FIGHT
        // =====================================================

        holder.txtDeleteFight.setOnClickListener(v -> {

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(
                            v.getContext()
                    );

            builder.setTitle(
                    "Delete Fight"
            );

            builder.setMessage(
                    "Are you sure you want to delete this fight history?"
            );


            // DELETE BUTTON
            builder.setPositiveButton(
                    "Delete",
                    (dialog, which) -> {

                        int currentPosition =
                                holder.getBindingAdapterPosition();

                        if (currentPosition !=
                                RecyclerView.NO_POSITION) {

                            deleteFight(
                                    record,
                                    currentPosition,
                                    holder
                            );
                        }
                    }
            );


            // CANCEL BUTTON
            builder.setNegativeButton(
                    "Cancel",
                    (dialog, which) ->
                            dialog.dismiss()
            );


            builder.show();
        });
    }


    // =========================================================
    // DELETE FIGHT FROM FIREBASE
    // =========================================================

    private void deleteFight(
            FightRecord record,
            int position,
            HistoryViewHolder holder) {


        if (position ==
                RecyclerView.NO_POSITION) {

            return;
        }


        // Check Firestore document ID
        if (record.id == null ||
                record.id.trim().isEmpty()) {

            Toast.makeText(
                    holder.itemView.getContext(),
                    "Unable to find fight document",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // Delete from Firestore
        db.collection("fights")
                .document(record.id)
                .delete()

                .addOnSuccessListener(unused -> {

                    // Remove from RecyclerView list
                    fightList.remove(position);

                    notifyItemRemoved(position);


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


    // =========================================================
    // NUMBER OF HISTORY ITEMS
    // =========================================================

    @Override
    public int getItemCount() {

        return fightList.size();
    }


    // =========================================================
    // VIEW HOLDER
    // =========================================================

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
        TextView txtDeleteFight;


        public HistoryViewHolder(
                @NonNull View itemView) {

            super(itemView);


            // PLAYER NAMES
            txtAoName =
                    itemView.findViewById(
                            R.id.txtAoName
                    );

            txtAkaName =
                    itemView.findViewById(
                            R.id.txtAkaName
                    );


            // SCORES
            txtAoScore =
                    itemView.findViewById(
                            R.id.txtAoScore
                    );

            txtAkaScore =
                    itemView.findViewById(
                            R.id.txtAkaScore
                    );


            // RESULT INFORMATION
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


            // SHARE RESULT
            txtShareResult =
                    itemView.findViewById(
                            R.id.txtShareResult
                    );


            // DELETE HISTORY
            txtDeleteFight =
                    itemView.findViewById(
                            R.id.txtDeleteFight
                    );
        }
    }
}