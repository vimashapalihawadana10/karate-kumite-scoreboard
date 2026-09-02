package com.example.timerandfight;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import android.media.MediaPlayer;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.Toast;

import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class NewFightActivity extends AppCompatActivity {
    EditText edtAoName, edtAkaName;
    Button btnSaveFight;

    String finalWinner = "";
    String finalReason = "";

    FirebaseFirestore db;


    TextView txtTimer, txtWinningGap, txtAoScore, txtAkaScore, txtAoSenshu, txtAkaSenshu;

    Button btnStart, btnStop, btnReset;
    Button btnAoYuko, btnAoWazaari, btnAoIppon, btnAoMinus, btnAoSenshu;
    Button btnAkaYuko, btnAkaWazaari, btnAkaIppon, btnAkaMinus, btnAkaSenshu;

    Button btnAoC1, btnAoC2, btnAoC3, btnAoHC, btnAoH, btnAoK;
    Button btnAkaC1, btnAkaC2, btnAkaC3, btnAkaHC, btnAkaH, btnAkaK;

    CountDownTimer countDownTimer;

    MediaPlayer mediaPlayer;

    int aoScore = 0, akaScore = 0, winningGap = 6;
    boolean aoSenshu = false, akaSenshu = false;
    boolean isRunning = false, winnerShown = false;

    boolean aoC1, aoC2, aoC3, aoHC, aoH, aoK;
    boolean akaC1, akaC2, akaC3, akaHC, akaH, akaK;

    long totalTimeMillis, timeLeftMillis;
    SharedPreferences sharedPreferences;

    ArrayList<ScoreAction> scoreHistory = new ArrayList<>();

    class ScoreAction {
        String player;
        int points;

        ScoreAction(String player, int points) {
            this.player = player;
            this.points = points;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_fight);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        connectViews();

        loadSettings();
        updateScoreText();
        updateSenshuText();
        updateTimerText();
        resetWarningColors();

        btnStart.setOnClickListener(v -> {
            if (!isRunning && !winnerShown) startTimer();
        });

        btnStop.setOnClickListener(v -> stopTimer());
        btnReset.setOnClickListener(v -> resetFight());

        btnAoYuko.setOnClickListener(v -> addScore("AO", 1));
        btnAoWazaari.setOnClickListener(v -> addScore("AO", 2));
        btnAoIppon.setOnClickListener(v -> addScore("AO", 3));

        btnAkaYuko.setOnClickListener(v -> addScore("AKA", 1));
        btnAkaWazaari.setOnClickListener(v -> addScore("AKA", 2));
        btnAkaIppon.setOnClickListener(v -> addScore("AKA", 3));

        btnAoMinus.setOnClickListener(v -> undoLastScore("AO"));
        btnAkaMinus.setOnClickListener(v -> undoLastScore("AKA"));

        btnAoSenshu.setOnClickListener(v -> {
            aoSenshu = !aoSenshu;
            if (aoSenshu) akaSenshu = false;
            updateSenshuText();
        });

        btnAkaSenshu.setOnClickListener(v -> {
            akaSenshu = !akaSenshu;
            if (akaSenshu) aoSenshu = false;
            updateSenshuText();
        });

        setWarningClicks();

        db = FirebaseFirestore.getInstance();

        btnSaveFight.setOnClickListener(v -> saveFightToFirebase());

    }

    private void connectViews() {
        txtTimer = findViewById(R.id.txtTimer);
        txtWinningGap = findViewById(R.id.txtWinningGap);
        txtAoScore = findViewById(R.id.txtAoScore);
        txtAkaScore = findViewById(R.id.txtAkaScore);
        txtAoSenshu = findViewById(R.id.txtAoSenshu);
        txtAkaSenshu = findViewById(R.id.txtAkaSenshu);

        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnReset = findViewById(R.id.btnReset);

        btnAoYuko = findViewById(R.id.btnAoYuko);
        btnAoWazaari = findViewById(R.id.btnAoWazaari);
        btnAoIppon = findViewById(R.id.btnAoIppon);
        btnAoMinus = findViewById(R.id.btnAoMinus);
        btnAoSenshu = findViewById(R.id.btnAoSenshu);

        btnAkaYuko = findViewById(R.id.btnAkaYuko);
        btnAkaWazaari = findViewById(R.id.btnAkaWazaari);
        btnAkaIppon = findViewById(R.id.btnAkaIppon);
        btnAkaMinus = findViewById(R.id.btnAkaMinus);
        btnAkaSenshu = findViewById(R.id.btnAkaSenshu);

        btnAoC1 = findViewById(R.id.btnAoC1);
        btnAoC2 = findViewById(R.id.btnAoC2);
        btnAoC3 = findViewById(R.id.btnAoC3);
        btnAoHC = findViewById(R.id.btnAoHC);
        btnAoH = findViewById(R.id.btnAoH);
        btnAoK = findViewById(R.id.btnAoK);

        btnAkaC1 = findViewById(R.id.btnAkaC1);
        btnAkaC2 = findViewById(R.id.btnAkaC2);
        btnAkaC3 = findViewById(R.id.btnAkaC3);
        btnAkaHC = findViewById(R.id.btnAkaHC);
        btnAkaH = findViewById(R.id.btnAkaH);
        btnAkaK = findViewById(R.id.btnAkaK);

        btnSaveFight = findViewById(R.id.btnSaveFight);

        edtAoName = findViewById(R.id.edtAoName);
        edtAkaName = findViewById(R.id.edtAkaName);
        btnSaveFight = findViewById(R.id.btnSaveFight);
    }

    private void loadSettings() {

        sharedPreferences = getSharedPreferences("KumiteSettings", MODE_PRIVATE);

        int seconds = sharedPreferences.getInt("seconds", 120);
        winningGap = sharedPreferences.getInt("winningGap", 6);

        totalTimeMillis = seconds * 1000L;
        timeLeftMillis = totalTimeMillis;

        txtWinningGap.setText("Winning Gap: " + winningGap);
    }

    private void addScore(String player, int points) {
        if (winnerShown) return;

        if (player.equals("AO")) {
            aoScore += points;
        } else {
            akaScore += points;
        }

        scoreHistory.add(new ScoreAction(player, points));
        updateScoreText();
        checkWinnerByGap();
    }

    private void undoLastScore(String player) {
        if (winnerShown) return;

        for (int i = scoreHistory.size() - 1; i >= 0; i--) {
            ScoreAction action = scoreHistory.get(i);

            if (action.player.equals(player)) {
                if (player.equals("AO")) {
                    aoScore -= action.points;
                    if (aoScore < 0) aoScore = 0;
                } else {
                    akaScore -= action.points;
                    if (akaScore < 0) akaScore = 0;
                }

                scoreHistory.remove(i);
                updateScoreText();
                return;
            }
        }
    }

    private void updateScoreText() {
        txtAoScore.setText(String.valueOf(aoScore));
        txtAkaScore.setText(String.valueOf(akaScore));
    }

    private void updateSenshuText() {
        txtAoSenshu.setText(aoSenshu ? "S: ON" : "S: OFF");
        txtAkaSenshu.setText(akaSenshu ? "S: ON" : "S: OFF");
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                isRunning = false;
                timeLeftMillis = 0;
                updateTimerText();
                determineWinnerByTime();
            }
        }.start();

        isRunning = true;
    }

    private void stopTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        isRunning = false;
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftMillis / 1000) / 60;
        int seconds = (int) (timeLeftMillis / 1000) % 60;
        txtTimer.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void checkWinnerByGap() {
        if (winnerShown) return;

        int gap = Math.abs(aoScore - akaScore);

        if (gap >= winningGap) {
            if (aoScore > akaScore) {
                showWinner("AO", "AO reached winning gap.");
            } else {
                showWinner("AKA", "AKA reached winning gap.");
            }
        }
    }

    private void determineWinnerByTime() {
        if (winnerShown) return;

        if (aoScore > akaScore) {
            showWinner("AO", "Time finished.");
        } else if (akaScore > aoScore) {
            showWinner("AKA", "Time finished.");
        } else {
            if (aoSenshu) {
                showWinner("AO", "Score is same. AO wins by Senshu.");
            } else if (akaSenshu) {
                showWinner("AKA", "Score is same. AKA wins by Senshu.");
            } else {
                showDraw();
            }
        }
    }


    private void showWinner(String winner, String reason) {
        winnerShown = true;
        finalWinner = winner;
        finalReason = reason;

        stopTimer();
        playWinnerSound();

        new AlertDialog.Builder(this)
                .setTitle("Fight Finished")
                .setMessage(winner + " is Winner\n\nReason: " + reason)
                .setPositiveButton("OK", null)
                .show();
    }


    private void showDraw() {
        winnerShown = true;
        finalWinner = "Draw";
        finalReason = "No Senshu owner";

        stopTimer();
        playWinnerSound();

        new AlertDialog.Builder(this)
                .setTitle("Fight Finished")
                .setMessage("Match is Draw\n\nNo Senshu owner.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void saveFightToFirebase() {

        if (!winnerShown) {
            Toast.makeText(this, "Fight is not finished yet", Toast.LENGTH_SHORT).show();
            return;
        }

        String aoName = edtAoName.getText().toString().trim();
        String akaName = edtAkaName.getText().toString().trim();

        if (aoName.isEmpty()) aoName = "AO";
        if (akaName.isEmpty()) akaName = "AKA";

        String dateTime = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                Locale.getDefault()
        ).format(new Date());

        FightRecord record = new FightRecord(
                aoName,
                akaName,
                aoScore,
                akaScore,
                finalWinner,
                finalReason,
                dateTime
        );

        db.collection("fights")
                .add(record)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Fight saved to Firebase", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void playWinnerSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.winner_sound);
        mediaPlayer.start();
    }
    private void setWarningClicks() {
        btnAoC1.setOnClickListener(v -> {
            aoC1 = !aoC1;
            setWarningColor(btnAoC1, aoC1);
        });

        btnAoC2.setOnClickListener(v -> {
            aoC2 = !aoC2;
            setWarningColor(btnAoC2, aoC2);
        });

        btnAoC3.setOnClickListener(v -> {
            aoC3 = !aoC3;
            setWarningColor(btnAoC3, aoC3);
        });

        btnAoHC.setOnClickListener(v -> {
            aoHC = !aoHC;
            setWarningColor(btnAoHC, aoHC);
        });

        btnAoH.setOnClickListener(v -> {
            aoH = !aoH;
            setWarningColor(btnAoH, aoH);

            if (aoH && !winnerShown) {
                showWinner("AKA", "AO got H warning.");
            }
        });

        btnAoK.setOnClickListener(v -> {
            aoK = !aoK;
            setWarningColor(btnAoK, aoK);

            if (aoK && !winnerShown) {
                showWinner("AKA", "AO got K warning and is disqualified.");
            }
        });

        btnAkaC1.setOnClickListener(v -> {
            akaC1 = !akaC1;
            setWarningColor(btnAkaC1, akaC1);
        });

        btnAkaC2.setOnClickListener(v -> {
            akaC2 = !akaC2;
            setWarningColor(btnAkaC2, akaC2);
        });

        btnAkaC3.setOnClickListener(v -> {
            akaC3 = !akaC3;
            setWarningColor(btnAkaC3, akaC3);
        });

        btnAkaHC.setOnClickListener(v -> {
            akaHC = !akaHC;
            setWarningColor(btnAkaHC, akaHC);
        });

        btnAkaH.setOnClickListener(v -> {
            akaH = !akaH;
            setWarningColor(btnAkaH, akaH);

            if (akaH && !winnerShown) {
                showWinner("AO", "AKA got H warning.");
            }
        });

        btnAkaK.setOnClickListener(v -> {
            akaK = !akaK;
            setWarningColor(btnAkaK, akaK);

            if (akaK && !winnerShown) {
                showWinner("AO", "AKA got K warning and is disqualified.");
            }
        });
    }




    private void setWarningColor(Button button, boolean selected) {
        if (selected) {
            button.setBackgroundColor(Color.YELLOW);
            button.setTextColor(Color.BLACK);
        } else {
            button.setBackgroundColor(Color.BLACK);
            button.setTextColor(Color.WHITE);
        }
    }

    private void resetWarningColors() {
        setWarningColor(btnAoC1, false);
        setWarningColor(btnAoC2, false);
        setWarningColor(btnAoC3, false);
        setWarningColor(btnAoHC, false);
        setWarningColor(btnAoH, false);
        setWarningColor(btnAoK, false);

        setWarningColor(btnAkaC1, false);
        setWarningColor(btnAkaC2, false);
        setWarningColor(btnAkaC3, false);
        setWarningColor(btnAkaHC, false);
        setWarningColor(btnAkaH, false);
        setWarningColor(btnAkaK, false);
    }

    private void resetFight() {
        stopTimer();

        aoScore = 0;
        akaScore = 0;
        aoSenshu = false;
        akaSenshu = false;
        winnerShown = false;

        aoC1 = aoC2 = aoC3 = aoHC = aoH = aoK = false;
        akaC1 = akaC2 = akaC3 = akaHC = akaH = akaK = false;

        scoreHistory.clear();

        loadSettings();
        updateScoreText();
        updateSenshuText();
        updateTimerText();
        resetWarningColors();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }


}