package com.example.timerandfight;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ShareResultActivity extends AppCompatActivity {

    TextView txtShareDate;
    TextView txtShareWinner;
    TextView txtShareScore;
    TextView txtShareReason;

    ImageView imgWinner;

    Button btnChoosePhoto;
    Button btnShare;

    FightRecord record;

    Uri winnerPhotoUri;

    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_share_result);

        txtShareDate = findViewById(R.id.txtShareDate);
        txtShareWinner = findViewById(R.id.txtShareWinner);
        txtShareScore = findViewById(R.id.txtShareScore);
        txtShareReason = findViewById(R.id.txtShareReason);

        imgWinner = findViewById(R.id.imgWinner);

        btnChoosePhoto = findViewById(R.id.btnChoosePhoto);
        btnShare = findViewById(R.id.btnShare);

        // Get fight information
        record = new FightRecord();

        record.aoName = getIntent().getStringExtra("aoName");
        record.akaName = getIntent().getStringExtra("akaName");

        record.aoScore = getIntent().getIntExtra("aoScore", 0);
        record.akaScore = getIntent().getIntExtra("akaScore", 0);

        record.winner = getIntent().getStringExtra("winner");
        record.reason = getIntent().getStringExtra("reason");
        record.dateTime = getIntent().getStringExtra("dateTime");

        displayFightInformation();

        btnChoosePhoto.setOnClickListener(v -> choosePhoto());

        btnShare.setOnClickListener(v -> shareResult());
    }

    private void displayFightInformation() {

        txtShareDate.setText(record.dateTime);

        txtShareWinner.setText(record.winner);

        txtShareScore.setText(
                record.akaScore + "  -  " + record.aoScore
        );

        txtShareReason.setText(
                "Won by " + record.reason
        );
    }

    private void choosePhoto() {

        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");

        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode == PICK_IMAGE &&
                resultCode == RESULT_OK &&
                data != null) {

            winnerPhotoUri = data.getData();

            imgWinner.setImageURI(winnerPhotoUri);
        }
    }

    private void shareResult() {

        try {

            View shareCard = findViewById(R.id.shareCard);

            Bitmap bitmap = Bitmap.createBitmap(
                    shareCard.getWidth(),
                    shareCard.getHeight(),
                    Bitmap.Config.ARGB_8888
            );

            Canvas canvas = new Canvas(bitmap);

            shareCard.draw(canvas);

            File cachePath = new File(
                    getCacheDir(),
                    "images"
            );

            cachePath.mkdirs();

            File file = new File(
                    cachePath,
                    "fight_result.png"
            );

            FileOutputStream stream =
                    new FileOutputStream(file);

            bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    stream
            );

            stream.close();

            Uri contentUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    file
            );

            Intent shareIntent =
                    new Intent(Intent.ACTION_SEND);

            shareIntent.setType("image/png");

            shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    contentUri
            );

            shareIntent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            startActivity(
                    Intent.createChooser(
                            shareIntent,
                            "Share Fight Result"
                    )
            );

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to share result",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}