package com.example.timerandfight;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewFightActivity extends AppCompatActivity {

    // ==========================================
    // PLAYER NAME + SAVE
    // ==========================================

    EditText edtAoName, edtAkaName;
    Button btnSaveFight;

    String finalWinner = "";
    String finalReason = "";

    FirebaseFirestore db;


    // ==========================================
    // TEXT VIEWS
    // ==========================================

    TextView txtTimer;
    TextView txtWinningGap;
    TextView txtAoScore;
    TextView txtAkaScore;
    TextView txtAoSenshu;
    TextView txtAkaSenshu;


    // ==========================================
    // MAIN BUTTONS
    // ==========================================

    Button btnStart;
    Button btnStop;
    Button btnReset;


    // ==========================================
    // AO BUTTONS
    // ==========================================

    Button btnAoYuko;
    Button btnAoWazaari;
    Button btnAoIppon;
    Button btnAoMinus;
    Button btnAoSenshu;


    // ==========================================
    // AKA BUTTONS
    // ==========================================

    Button btnAkaYuko;
    Button btnAkaWazaari;
    Button btnAkaIppon;
    Button btnAkaMinus;
    Button btnAkaSenshu;


    // ==========================================
    // AO WARNING BUTTONS
    // ==========================================

    Button btnAoC1;
    Button btnAoC2;
    Button btnAoC3;
    Button btnAoHC;
    Button btnAoH;
    Button btnAoK;


    // ==========================================
    // AKA WARNING BUTTONS
    // ==========================================

    Button btnAkaC1;
    Button btnAkaC2;
    Button btnAkaC3;
    Button btnAkaHC;
    Button btnAkaH;
    Button btnAkaK;


    // ==========================================
    // TIMER
    // ==========================================

    CountDownTimer countDownTimer;

    long totalTimeMillis;
    long timeLeftMillis;


    // ==========================================
    // SCORE
    // ==========================================

    int aoScore = 0;
    int akaScore = 0;

    int winningGap = 6;


    // ==========================================
    // SENSHU
    // ==========================================

    boolean aoSenshu = false;
    boolean akaSenshu = false;


    // ==========================================
    // FIGHT STATUS
    // ==========================================

    boolean isRunning = false;
    boolean winnerShown = false;

    // Used for READY / PAUSED status
    boolean fightStarted = false;


    // ==========================================
    // AO WARNINGS
    // ==========================================

    boolean aoC1 = false;
    boolean aoC2 = false;
    boolean aoC3 = false;
    boolean aoHC = false;
    boolean aoH = false;
    boolean aoK = false;


    // ==========================================
    // AKA WARNINGS
    // ==========================================

    boolean akaC1 = false;
    boolean akaC2 = false;
    boolean akaC3 = false;
    boolean akaHC = false;
    boolean akaH = false;
    boolean akaK = false;


    // ==========================================
    // SETTINGS
    // ==========================================

    SharedPreferences sharedPreferences;


    // ==========================================
    // SOUND
    // ==========================================

    MediaPlayer mediaPlayer;


    // ==========================================
    // SCORE HISTORY
    // Used for undo-last-score
    // ==========================================

    ArrayList<ScoreAction> scoreHistory = new ArrayList<>();


    class ScoreAction {

        String player;
        int points;

        ScoreAction(String player, int points) {

            this.player = player;
            this.points = points;
        }
    }


    // ==========================================================
    // ON CREATE
    // ==========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_new_fight);


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
                });


        // Connect XML views
        connectViews();


        // Firebase
        db = FirebaseFirestore.getInstance();


        // Load fight settings
        loadSettings();


        // Initial UI
        updateScoreText();
        updateSenshuText();
        updateTimerText();
        resetWarningColors();


        // ==========================================
        // START BUTTON
        // ==========================================

        btnStart.setOnClickListener(v -> {

            if (!isRunning && !winnerShown) {

                fightStarted = true;

                startTimer();

                // Sends player names + current fight
                updateLiveFight();
            }
        });


        // ==========================================
        // STOP BUTTON
        // ==========================================

        btnStop.setOnClickListener(v -> {

            if (winnerShown) {
                return;
            }

            stopTimer();

            updateLiveFight();
        });


        // ==========================================
        // RESET BUTTON
        // ==========================================

        btnReset.setOnClickListener(v -> {

            resetFight();
        });


        // ==========================================
        // AO SCORE BUTTONS
        // ==========================================

        btnAoYuko.setOnClickListener(v -> {

            addScore("AO", 1);
        });


        btnAoWazaari.setOnClickListener(v -> {

            addScore("AO", 2);
        });


        btnAoIppon.setOnClickListener(v -> {

            addScore("AO", 3);
        });


        // ==========================================
        // AKA SCORE BUTTONS
        // ==========================================

        btnAkaYuko.setOnClickListener(v -> {

            addScore("AKA", 1);
        });


        btnAkaWazaari.setOnClickListener(v -> {

            addScore("AKA", 2);
        });


        btnAkaIppon.setOnClickListener(v -> {

            addScore("AKA", 3);
        });


        // ==========================================
        // REMOVE LAST SCORE
        // ==========================================

        btnAoMinus.setOnClickListener(v -> {

            undoLastScore("AO");
        });


        btnAkaMinus.setOnClickListener(v -> {

            undoLastScore("AKA");
        });


        // ==========================================
        // AO SENSHU
        // ==========================================

        btnAoSenshu.setOnClickListener(v -> {

            if (winnerShown) {
                return;
            }

            aoSenshu = !aoSenshu;

            if (aoSenshu) {

                akaSenshu = false;
            }

            updateSenshuText();

            updateLiveFight();
        });


        // ==========================================
        // AKA SENSHU
        // ==========================================

        btnAkaSenshu.setOnClickListener(v -> {

            if (winnerShown) {
                return;
            }

            akaSenshu = !akaSenshu;

            if (akaSenshu) {

                aoSenshu = false;
            }

            updateSenshuText();

            updateLiveFight();
        });


        // Warning button listeners
        setWarningClicks();


        // ==========================================
        // SAVE FINISHED FIGHT TO HISTORY
        // ==========================================

        btnSaveFight.setOnClickListener(v -> {

            saveFightToFirebase();
        });


        // ==========================================
        // INITIAL LIVE FIREBASE STATE
        // ==========================================

        updateLiveFight();
    }


    // ==========================================================
    // CONNECT XML VIEWS
    // ==========================================================

    private void connectViews() {

        txtTimer =
                findViewById(R.id.txtTimer);

        txtWinningGap =
                findViewById(R.id.txtWinningGap);

        txtAoScore =
                findViewById(R.id.txtAoScore);

        txtAkaScore =
                findViewById(R.id.txtAkaScore);

        txtAoSenshu =
                findViewById(R.id.txtAoSenshu);

        txtAkaSenshu =
                findViewById(R.id.txtAkaSenshu);


        // Main buttons

        btnStart =
                findViewById(R.id.btnStart);

        btnStop =
                findViewById(R.id.btnStop);

        btnReset =
                findViewById(R.id.btnReset);


        // AO score buttons

        btnAoYuko =
                findViewById(R.id.btnAoYuko);

        btnAoWazaari =
                findViewById(R.id.btnAoWazaari);

        btnAoIppon =
                findViewById(R.id.btnAoIppon);

        btnAoMinus =
                findViewById(R.id.btnAoMinus);

        btnAoSenshu =
                findViewById(R.id.btnAoSenshu);


        // AKA score buttons

        btnAkaYuko =
                findViewById(R.id.btnAkaYuko);

        btnAkaWazaari =
                findViewById(R.id.btnAkaWazaari);

        btnAkaIppon =
                findViewById(R.id.btnAkaIppon);

        btnAkaMinus =
                findViewById(R.id.btnAkaMinus);

        btnAkaSenshu =
                findViewById(R.id.btnAkaSenshu);


        // AO warnings

        btnAoC1 =
                findViewById(R.id.btnAoC1);

        btnAoC2 =
                findViewById(R.id.btnAoC2);

        btnAoC3 =
                findViewById(R.id.btnAoC3);

        btnAoHC =
                findViewById(R.id.btnAoHC);

        btnAoH =
                findViewById(R.id.btnAoH);

        btnAoK =
                findViewById(R.id.btnAoK);


        // AKA warnings

        btnAkaC1 =
                findViewById(R.id.btnAkaC1);

        btnAkaC2 =
                findViewById(R.id.btnAkaC2);

        btnAkaC3 =
                findViewById(R.id.btnAkaC3);

        btnAkaHC =
                findViewById(R.id.btnAkaHC);

        btnAkaH =
                findViewById(R.id.btnAkaH);

        btnAkaK =
                findViewById(R.id.btnAkaK);


        // Fighter names

        edtAoName =
                findViewById(R.id.edtAoName);

        edtAkaName =
                findViewById(R.id.edtAkaName);


        // Save fight

        btnSaveFight =
                findViewById(R.id.btnSaveFight);
    }


    // ==========================================================
    // LOAD SETTINGS
    // ==========================================================

    private void loadSettings() {

        sharedPreferences =
                getSharedPreferences(
                        "KumiteSettings",
                        MODE_PRIVATE
                );


        int seconds =
                sharedPreferences.getInt(
                        "seconds",
                        120
                );


        winningGap =
                sharedPreferences.getInt(
                        "winningGap",
                        6
                );


        totalTimeMillis =
                seconds * 1000L;


        timeLeftMillis =
                totalTimeMillis;


        txtWinningGap.setText(
                "Winning Gap: " + winningGap
        );
    }


    // ==========================================================
    // ADD SCORE
    // ==========================================================

    private void addScore(String player, int points) {

        if (winnerShown) {

            return;
        }


        if (player.equals("AO")) {

            aoScore += points;

        } else {

            akaScore += points;
        }


        scoreHistory.add(
                new ScoreAction(
                        player,
                        points
                )
        );


        updateScoreText();


        // Check if score gap has finished the fight
        checkWinnerByGap();


        // Update TV / monitor
        updateLiveFight();
    }


    // ==========================================================
    // REMOVE LAST SCORE FOR PLAYER
    // ==========================================================

    private void undoLastScore(String player) {

        if (winnerShown) {

            return;
        }


        for (int i = scoreHistory.size() - 1;
             i >= 0;
             i--) {


            ScoreAction action =
                    scoreHistory.get(i);


            if (action.player.equals(player)) {


                if (player.equals("AO")) {

                    aoScore -= action.points;

                    if (aoScore < 0) {

                        aoScore = 0;
                    }

                } else {

                    akaScore -= action.points;

                    if (akaScore < 0) {

                        akaScore = 0;
                    }
                }


                scoreHistory.remove(i);


                updateScoreText();


                // Update TV / monitor
                updateLiveFight();


                return;
            }
        }
    }


    // ==========================================================
    // UPDATE SCORE TEXT
    // ==========================================================

    private void updateScoreText() {

        txtAoScore.setText(
                String.valueOf(aoScore)
        );


        txtAkaScore.setText(
                String.valueOf(akaScore)
        );
    }


    // ==========================================================
    // UPDATE SENSHU TEXT
    // ==========================================================

    private void updateSenshuText() {

        txtAoSenshu.setText(
                aoSenshu
                        ? "S: ON"
                        : "S: OFF"
        );


        txtAkaSenshu.setText(
                akaSenshu
                        ? "S: ON"
                        : "S: OFF"
        );
    }


    // ==========================================================
    // START TIMER
    // ==========================================================

    private void startTimer() {

        if (timeLeftMillis <= 0) {

            return;
        }


        countDownTimer =
                new CountDownTimer(
                        timeLeftMillis,
                        1000
                ) {


                    @Override
                    public void onTick(
                            long millisUntilFinished) {


                        timeLeftMillis =
                                millisUntilFinished;


                        updateTimerText();


                        // Send timer to TV every second
                        updateLiveFight();
                    }


                    @Override
                    public void onFinish() {


                        isRunning = false;

                        timeLeftMillis = 0;


                        updateTimerText();


                        determineWinnerByTime();


                        // Final timer state
                        updateLiveFight();
                    }

                }.start();


        isRunning = true;

        fightStarted = true;


        // Firebase status becomes RUNNING
        updateLiveFight();
    }


    // ==========================================================
    // STOP TIMER
    // ==========================================================

    private void stopTimer() {

        if (countDownTimer != null) {

            countDownTimer.cancel();

            countDownTimer = null;
        }


        isRunning = false;
    }


    // ==========================================================
    // UPDATE TIMER TEXT
    // ==========================================================

    private void updateTimerText() {

        int minutes =
                (int) (timeLeftMillis / 1000) / 60;


        int seconds =
                (int) (timeLeftMillis / 1000) % 60;


        txtTimer.setText(
                String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        minutes,
                        seconds
                )
        );
    }


    // ==========================================================
    // CHECK WINNER BY WINNING GAP
    // ==========================================================

    private void checkWinnerByGap() {

        if (winnerShown) {

            return;
        }


        int gap =
                Math.abs(
                        aoScore - akaScore
                );


        if (gap >= winningGap) {


            if (aoScore > akaScore) {

                showWinner(
                        "AO",
                        "AO reached winning gap."
                );

            } else {

                showWinner(
                        "AKA",
                        "AKA reached winning gap."
                );
            }
        }
    }


    // ==========================================================
    // DETERMINE WINNER WHEN TIMER ENDS
    // ==========================================================

    private void determineWinnerByTime() {

        if (winnerShown) {

            return;
        }


        if (aoScore > akaScore) {

            showWinner(
                    "AO",
                    "Time finished."
            );


        } else if (akaScore > aoScore) {


            showWinner(
                    "AKA",
                    "Time finished."
            );


        } else {


            // Same score

            if (aoSenshu) {


                showWinner(
                        "AO",
                        "Score is same. AO wins by Senshu."
                );


            } else if (akaSenshu) {


                showWinner(
                        "AKA",
                        "Score is same. AKA wins by Senshu."
                );


            } else {


                showDraw();
            }
        }
    }


    // ==========================================================
    // SHOW WINNER
    // ==========================================================

    private void showWinner(
            String winner,
            String reason) {


        if (winnerShown) {

            return;
        }


        winnerShown = true;

        finalWinner = winner;
        finalReason = reason;


        stopTimer();


        // Send finished match to big screen
        updateLiveFight();


        playWinnerSound();


        new AlertDialog.Builder(this)

                .setTitle("Fight Finished")

                .setMessage(
                        winner
                                + " is Winner\n\nReason: "
                                + reason
                )

                .setPositiveButton(
                        "OK",
                        null
                )

                .show();
    }


    // ==========================================================
    // SHOW DRAW
    // ==========================================================

    private void showDraw() {

        if (winnerShown) {

            return;
        }


        winnerShown = true;

        finalWinner = "Draw";

        finalReason =
                "No Senshu owner";


        stopTimer();


        // Send draw to big screen
        updateLiveFight();


        playWinnerSound();


        new AlertDialog.Builder(this)

                .setTitle("Fight Finished")

                .setMessage(
                        "Match is Draw\n\nNo Senshu owner."
                )

                .setPositiveButton(
                        "OK",
                        null
                )

                .show();
    }


    // ==========================================================
    // LIVE FIREBASE
    //
    // Everything needed by the future TV screen is sent
    // to:
    //
    // liveFights/current
    // ==========================================================

    private void updateLiveFight() {

        if (db == null) {

            return;
        }


        String aoName =
                edtAoName
                        .getText()
                        .toString()
                        .trim();


        String akaName =
                edtAkaName
                        .getText()
                        .toString()
                        .trim();


        if (aoName.isEmpty()) {

            aoName = "AO";
        }


        if (akaName.isEmpty()) {

            akaName = "AKA";
        }


        Map<String, Object> liveFight =
                new HashMap<>();


        // ==========================================
        // PLAYER INFORMATION
        // ==========================================

        liveFight.put(
                "aoName",
                aoName
        );


        liveFight.put(
                "akaName",
                akaName
        );


        // ==========================================
        // SCORES
        // ==========================================

        liveFight.put(
                "aoScore",
                aoScore
        );


        liveFight.put(
                "akaScore",
                akaScore
        );


        // ==========================================
        // TIMER
        // ==========================================

        liveFight.put(
                "timerSeconds",
                timeLeftMillis / 1000
        );


        // Fight setting
        liveFight.put(
                "winningGap",
                winningGap
        );


        // ==========================================
        // SENSHU
        // ==========================================

        liveFight.put(
                "aoSenshu",
                aoSenshu
        );


        liveFight.put(
                "akaSenshu",
                akaSenshu
        );


        // ==========================================
        // AO WARNINGS
        // ==========================================

        liveFight.put("aoC1", aoC1);
        liveFight.put("aoC2", aoC2);
        liveFight.put("aoC3", aoC3);
        liveFight.put("aoHC", aoHC);
        liveFight.put("aoH", aoH);
        liveFight.put("aoK", aoK);


        // ==========================================
        // AKA WARNINGS
        // ==========================================

        liveFight.put("akaC1", akaC1);
        liveFight.put("akaC2", akaC2);
        liveFight.put("akaC3", akaC3);
        liveFight.put("akaHC", akaHC);
        liveFight.put("akaH", akaH);
        liveFight.put("akaK", akaK);


        // ==========================================
        // WARNING COUNTS
        // ==========================================

        liveFight.put(
                "aoWarnings",
                getAoWarningCount()
        );


        liveFight.put(
                "akaWarnings",
                getAkaWarningCount()
        );


        // ==========================================
        // STATUS
        // ==========================================

        String status;


        if (winnerShown) {

            status = "FINISHED";

        } else if (isRunning) {

            status = "RUNNING";

        } else if (fightStarted) {

            status = "PAUSED";

        } else {

            status = "READY";
        }


        liveFight.put(
                "status",
                status
        );


        // ==========================================
        // WINNER
        // ==========================================

        liveFight.put(
                "winner",
                finalWinner
        );


        liveFight.put(
                "reason",
                finalReason
        );


        // ==========================================
        // SEND TO FIRESTORE
        // ==========================================

        db.collection("liveFights")

                .document("current")

                .set(liveFight)

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            NewFightActivity.this,
                            "Live update failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }


    // ==========================================================
    // COUNT AO WARNINGS
    // ==========================================================

    private int getAoWarningCount() {

        int count = 0;


        if (aoC1) count++;
        if (aoC2) count++;
        if (aoC3) count++;
        if (aoHC) count++;
        if (aoH) count++;
        if (aoK) count++;


        return count;
    }


    // ==========================================================
    // COUNT AKA WARNINGS
    // ==========================================================

    private int getAkaWarningCount() {

        int count = 0;


        if (akaC1) count++;
        if (akaC2) count++;
        if (akaC3) count++;
        if (akaHC) count++;
        if (akaH) count++;
        if (akaK) count++;


        return count;
    }


    // ==========================================================
    // SAVE FINISHED FIGHT TO FIREBASE HISTORY
    // Collection: fights
    // ==========================================================

    private void saveFightToFirebase() {

        if (!winnerShown) {

            Toast.makeText(
                    this,
                    "Fight is not finished yet",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        String aoName =
                edtAoName
                        .getText()
                        .toString()
                        .trim();


        String akaName =
                edtAkaName
                        .getText()
                        .toString()
                        .trim();


        if (aoName.isEmpty()) {

            aoName = "AO";
        }


        if (akaName.isEmpty()) {

            akaName = "AKA";
        }


        String dateTime =
                new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm",
                        Locale.getDefault()
                ).format(
                        new Date()
                );


        FightRecord record =
                new FightRecord(
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

                .addOnSuccessListener(
                        documentReference -> {

                            Toast.makeText(
                                    this,
                                    "Fight saved to Firebase",
                                    Toast.LENGTH_SHORT
                            ).show();
                        })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Save failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }


    // ==========================================================
    // WINNER SOUND
    // ==========================================================

    private void playWinnerSound() {

        if (mediaPlayer != null) {

            mediaPlayer.release();

            mediaPlayer = null;
        }


        mediaPlayer =
                MediaPlayer.create(
                        this,
                        R.raw.winner_sound
                );


        if (mediaPlayer != null) {

            mediaPlayer.start();
        }
    }


    // ==========================================================
    // WARNING BUTTON LISTENERS
    // ==========================================================

    private void setWarningClicks() {


        // ==========================================
        // AO C1
        // ==========================================

        btnAoC1.setOnClickListener(v -> {

            if (winnerShown) return;


            aoC1 = !aoC1;


            setWarningColor(
                    btnAoC1,
                    aoC1
            );


            updateLiveFight();
        });


        // ==========================================
        // AO C2
        // ==========================================

        btnAoC2.setOnClickListener(v -> {

            if (winnerShown) return;


            aoC2 = !aoC2;


            setWarningColor(
                    btnAoC2,
                    aoC2
            );


            updateLiveFight();
        });


        // ==========================================
        // AO C3
        // ==========================================

        btnAoC3.setOnClickListener(v -> {

            if (winnerShown) return;


            aoC3 = !aoC3;


            setWarningColor(
                    btnAoC3,
                    aoC3
            );


            updateLiveFight();
        });


        // ==========================================
        // AO HC
        // ==========================================

        btnAoHC.setOnClickListener(v -> {

            if (winnerShown) return;


            aoHC = !aoHC;


            setWarningColor(
                    btnAoHC,
                    aoHC
            );


            updateLiveFight();
        });


        // ==========================================
        // AO H
        // ==========================================

        btnAoH.setOnClickListener(v -> {

            if (winnerShown) return;


            aoH = !aoH;


            setWarningColor(
                    btnAoH,
                    aoH
            );


            if (aoH) {

                showWinner(
                        "AKA",
                        "AO got H warning."
                );

            } else {

                updateLiveFight();
            }
        });


        // ==========================================
        // AO K
        // ==========================================

        btnAoK.setOnClickListener(v -> {

            if (winnerShown) return;


            aoK = !aoK;


            setWarningColor(
                    btnAoK,
                    aoK
            );


            if (aoK) {

                showWinner(
                        "AKA",
                        "AO got K warning and is disqualified."
                );

            } else {

                updateLiveFight();
            }
        });


        // ==========================================
        // AKA C1
        // ==========================================

        btnAkaC1.setOnClickListener(v -> {

            if (winnerShown) return;


            akaC1 = !akaC1;


            setWarningColor(
                    btnAkaC1,
                    akaC1
            );


            updateLiveFight();
        });


        // ==========================================
        // AKA C2
        // ==========================================

        btnAkaC2.setOnClickListener(v -> {

            if (winnerShown) return;


            akaC2 = !akaC2;


            setWarningColor(
                    btnAkaC2,
                    akaC2
            );


            updateLiveFight();
        });


        // ==========================================
        // AKA C3
        // ==========================================

        btnAkaC3.setOnClickListener(v -> {

            if (winnerShown) return;


            akaC3 = !akaC3;


            setWarningColor(
                    btnAkaC3,
                    akaC3
            );


            updateLiveFight();
        });


        // ==========================================
        // AKA HC
        // ==========================================

        btnAkaHC.setOnClickListener(v -> {

            if (winnerShown) return;


            akaHC = !akaHC;


            setWarningColor(
                    btnAkaHC,
                    akaHC
            );


            updateLiveFight();
        });


        // ==========================================
        // AKA H
        // ==========================================

        btnAkaH.setOnClickListener(v -> {

            if (winnerShown) return;


            akaH = !akaH;


            setWarningColor(
                    btnAkaH,
                    akaH
            );


            if (akaH) {

                showWinner(
                        "AO",
                        "AKA got H warning."
                );

            } else {

                updateLiveFight();
            }
        });


        // ==========================================
        // AKA K
        // ==========================================

        btnAkaK.setOnClickListener(v -> {

            if (winnerShown) return;


            akaK = !akaK;


            setWarningColor(
                    btnAkaK,
                    akaK
            );


            if (akaK) {

                showWinner(
                        "AO",
                        "AKA got K warning and is disqualified."
                );

            } else {

                updateLiveFight();
            }
        });
    }


    // ==========================================================
    // WARNING COLOR
    // ==========================================================

    private void setWarningColor(
            Button button,
            boolean selected) {


        if (selected) {

            button.setBackgroundColor(
                    Color.YELLOW
            );


            button.setTextColor(
                    Color.BLACK
            );


        } else {


            button.setBackgroundColor(
                    Color.BLACK
            );


            button.setTextColor(
                    Color.WHITE
            );
        }
    }


    // ==========================================================
    // RESET WARNING COLORS
    // ==========================================================

    private void resetWarningColors() {

        setWarningColor(
                btnAoC1,
                false
        );


        setWarningColor(
                btnAoC2,
                false
        );


        setWarningColor(
                btnAoC3,
                false
        );


        setWarningColor(
                btnAoHC,
                false
        );


        setWarningColor(
                btnAoH,
                false
        );


        setWarningColor(
                btnAoK,
                false
        );


        setWarningColor(
                btnAkaC1,
                false
        );


        setWarningColor(
                btnAkaC2,
                false
        );


        setWarningColor(
                btnAkaC3,
                false
        );


        setWarningColor(
                btnAkaHC,
                false
        );


        setWarningColor(
                btnAkaH,
                false
        );


        setWarningColor(
                btnAkaK,
                false
        );
    }


    // ==========================================================
    // RESET FIGHT
    // ==========================================================

    private void resetFight() {

        stopTimer();


        // Scores

        aoScore = 0;

        akaScore = 0;


        // Senshu

        aoSenshu = false;

        akaSenshu = false;


        // Fight status

        winnerShown = false;

        fightStarted = false;


        // Clear winner

        finalWinner = "";

        finalReason = "";


        // AO warnings

        aoC1 = false;

        aoC2 = false;

        aoC3 = false;

        aoHC = false;

        aoH = false;

        aoK = false;


        // AKA warnings

        akaC1 = false;

        akaC2 = false;

        akaC3 = false;

        akaHC = false;

        akaH = false;

        akaK = false;


        // Score action history

        scoreHistory.clear();


        // Reload timer and winning gap

        loadSettings();


        // Update Android UI

        updateScoreText();

        updateSenshuText();

        updateTimerText();

        resetWarningColors();


        // Reset live big screen

        updateLiveFight();
    }


    // ==========================================================
    // ON DESTROY
    // ==========================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();


        if (countDownTimer != null) {

            countDownTimer.cancel();

            countDownTimer = null;
        }


        if (mediaPlayer != null) {

            mediaPlayer.release();

            mediaPlayer = null;
        }
    }
}