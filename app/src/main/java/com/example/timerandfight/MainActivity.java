package com.example.timerandfight;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // Main menu cards
    LinearLayout cardNewFight;
    LinearLayout cardHistory;
    LinearLayout cardLiveDisplay;
    LinearLayout cardSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Handle system bars
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


        // =========================================
        // CONNECT MENU CARDS
        // =========================================

        cardNewFight =
                findViewById(R.id.cardNewFight);

        cardHistory =
                findViewById(R.id.cardHistory);

        cardLiveDisplay =
                findViewById(R.id.cardLiveDisplay);

        cardSettings =
                findViewById(R.id.cardSettings);


        // =========================================
        // NEW FIGHT
        // =========================================

        cardNewFight.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            NewFightActivity.class
                    );

            startActivity(intent);
        });


        // =========================================
        // FIGHT HISTORY
        // =========================================

        cardHistory.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            HistoryActivity.class
                    );

            startActivity(intent);
        });


        // =========================================
        // LIVE DISPLAY
        // =========================================

        cardLiveDisplay.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            LiveDisplayActivity.class
                    );

            startActivity(intent);
        });


        // =========================================
        // SETTINGS
        // =========================================

        cardSettings.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            SettingsActivity.class
                    );

            startActivity(intent);
        });
    }
}