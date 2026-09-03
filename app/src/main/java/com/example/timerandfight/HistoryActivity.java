package com.example.timerandfight;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerFightHistory;
    TextView txtEmpty;

    FirebaseFirestore db;

    ArrayList<FightRecord> fightList;
    HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );


        // =====================================================
        // CONNECT VIEWS
        // =====================================================

        recyclerFightHistory =
                findViewById(R.id.recyclerFightHistory);

        txtEmpty =
                findViewById(R.id.txtEmpty);


        // =====================================================
        // FIREBASE
        // =====================================================

        db = FirebaseFirestore.getInstance();


        // =====================================================
        // RECYCLER VIEW
        // =====================================================

        fightList = new ArrayList<>();

        adapter = new HistoryAdapter(fightList);

        recyclerFightHistory.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerFightHistory.setAdapter(adapter);


        // =====================================================
        // LOAD HISTORY
        // =====================================================

        loadFightHistory();
    }


    private void loadFightHistory() {

        db.collection("fights")

                .orderBy(
                        "dateTime",
                        Query.Direction.DESCENDING
                )

                .get()

                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            // Remove old list before loading
                            fightList.clear();


                            // =============================================
                            // NO HISTORY FOUND
                            // =============================================

                            if (queryDocumentSnapshots.isEmpty()) {

                                txtEmpty.setVisibility(
                                        View.VISIBLE
                                );

                                recyclerFightHistory.setVisibility(
                                        View.GONE
                                );

                                return;
                            }


                            // =============================================
                            // HISTORY EXISTS
                            // =============================================

                            txtEmpty.setVisibility(
                                    View.GONE
                            );

                            recyclerFightHistory.setVisibility(
                                    View.VISIBLE
                            );


                            // =============================================
                            // LOAD EACH FIRESTORE DOCUMENT
                            // =============================================

                            for (var document :
                                    queryDocumentSnapshots) {

                                FightRecord record =
                                        document.toObject(
                                                FightRecord.class
                                        );


                                // IMPORTANT:
                                // Save the Firestore document ID.
                                //
                                // We need this ID later when the user
                                // wants to delete this particular fight.
                                record.id =
                                        document.getId();


                                fightList.add(record);
                            }


                            // Refresh RecyclerView
                            adapter.notifyDataSetChanged();
                        }
                )

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }
}