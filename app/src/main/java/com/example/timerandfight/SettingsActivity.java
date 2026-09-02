package com.example.timerandfight;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    TextView txtTime, txtGap;

    ImageButton btnTimeUp, btnTimeDown;
    ImageButton btnGapUp, btnGapDown;

    Button btnSave;

    SharedPreferences sharedPreferences;

    int totalSeconds;
    int winningGap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtTime = findViewById(R.id.txtTime);
        txtGap = findViewById(R.id.txtGap);

        btnTimeUp = findViewById(R.id.btnTimeUp);
        btnTimeDown = findViewById(R.id.btnTimeDown);

        btnGapUp = findViewById(R.id.btnGapUp);
        btnGapDown = findViewById(R.id.btnGapDown);

        btnSave = findViewById(R.id.btnSave);

        sharedPreferences = getSharedPreferences("KumiteSettings", MODE_PRIVATE);

        totalSeconds = sharedPreferences.getInt("seconds", 120);
        winningGap = sharedPreferences.getInt("winningGap", 6);

        updateTimeText();
        updateGapText();

        btnTimeUp.setOnClickListener(v -> {

            totalSeconds += 30;

            if (totalSeconds > 600)
                totalSeconds = 600;

            updateTimeText();

        });

        btnTimeDown.setOnClickListener(v -> {

            totalSeconds -= 30;

            if (totalSeconds < 30)
                totalSeconds = 30;

            updateTimeText();

        });

        btnGapUp.setOnClickListener(v -> {

            winningGap++;

            if (winningGap > 20)
                winningGap = 20;

            updateGapText();

        });

        btnGapDown.setOnClickListener(v -> {

            winningGap--;

            if (winningGap < 1)
                winningGap = 1;

            updateGapText();

        });

        btnSave.setOnClickListener(v -> {

            sharedPreferences.edit()
                    .putInt("seconds", totalSeconds)
                    .putInt("winningGap", winningGap)
                    .apply();

            Toast.makeText(this,
                    "Settings Saved",
                    Toast.LENGTH_SHORT).show();

            finish();

        });

    }

    private void updateTimeText() {

        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        txtTime.setText(
                String.format("%02d:%02d", minutes, seconds)
        );

    }

    private void updateGapText() {

        txtGap.setText(String.valueOf(winningGap));

    }

}