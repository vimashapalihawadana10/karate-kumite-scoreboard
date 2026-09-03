package com.example.timerandfight;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LiveDisplayActivity extends AppCompatActivity {

    TextView txtLiveUrl;
    Button btnOpenLiveDisplay;
    Button btnShareLiveDisplay;
    Button btnCopyLiveDisplay;

    // Replace this with your real Firebase Hosting URL
    private static final String LIVE_DISPLAY_URL =
            "https://timerandfight.web.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_live_display);

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


        txtLiveUrl =
                findViewById(R.id.txtLiveUrl);

        btnOpenLiveDisplay =
                findViewById(R.id.btnOpenLiveDisplay);

        btnShareLiveDisplay =
                findViewById(R.id.btnShareLiveDisplay);

        btnCopyLiveDisplay =
                findViewById(R.id.btnCopyLiveDisplay);


        txtLiveUrl.setText(
                LIVE_DISPLAY_URL
        );


        // =========================================
        // OPEN LIVE DISPLAY
        // =========================================

        btnOpenLiveDisplay.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(LIVE_DISPLAY_URL)
                    );

            startActivity(intent);
        });


        // =========================================
        // SHARE LIVE DISPLAY LINK
        // =========================================

        btnShareLiveDisplay.setOnClickListener(v -> {

            Intent shareIntent =
                    new Intent(
                            Intent.ACTION_SEND
                    );

            shareIntent.setType("text/plain");

            shareIntent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "Kumite Live Scoreboard"
            );

            shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Open the live Kumite scoreboard:\n"
                            + LIVE_DISPLAY_URL
            );


            startActivity(
                    Intent.createChooser(
                            shareIntent,
                            "Share Live Display"
                    )
            );
        });


        // =========================================
        // COPY LIVE DISPLAY LINK
        // =========================================

        btnCopyLiveDisplay.setOnClickListener(v -> {

            ClipboardManager clipboardManager =
                    (ClipboardManager)
                            getSystemService(
                                    Context.CLIPBOARD_SERVICE
                            );


            ClipData clip =
                    ClipData.newPlainText(
                            "Kumite Live Display",
                            LIVE_DISPLAY_URL
                    );


            clipboardManager.setPrimaryClip(
                    clip
            );


            Toast.makeText(
                    this,
                    "Live Display link copied",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}