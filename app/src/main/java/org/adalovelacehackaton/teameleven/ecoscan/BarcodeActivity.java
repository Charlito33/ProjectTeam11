package org.adalovelacehackaton.teameleven.ecoscan;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.navigation.ui.AppBarConfiguration;

import org.adalovelacehackaton.teameleven.ecoscan.api.Item;
import org.adalovelacehackaton.teameleven.ecoscan.api.ProjectAPI;
import org.adalovelacehackaton.teameleven.ecoscan.databinding.ActivityBarcodeBinding;

import java.io.IOException;

public class BarcodeActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityBarcodeBinding binding;

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ToneGenerator toneGenerator1;
    private TextView barcodeText;
    private String barcodeData;

    private Button buttonCheckItem;
    private boolean newItem = false;
    private String scanCode = "";
    private Item item = null;

    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBarcodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toneGenerator1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        surfaceView = findViewById(R.id.surface_view);
        barcodeText = findViewById(R.id.barcode_text);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        accessToken = sharedPreferences.getString("access_token", "null");
        if (accessToken.equals("null")) {
            // ReLogin
            connect();
        }



        buttonCheckItem = findViewById(R.id.button_checkItem);
        buttonCheckItem.setOnClickListener(v -> {
            if (newItem) {
                Intent intent = new Intent(getApplicationContext(), AddItemActivity.class);
                intent.putExtra("scancode", scanCode);
                startActivity(intent);
            } else {
                ProjectAPI.logItemToUserAccount(accessToken, item);
                Toast.makeText(getApplicationContext(), "Added Item \"" + item.getName() + "\" to your account ! + " + item.getPointsValue() + " Points", Toast.LENGTH_SHORT).show();
            }

            finish();
        });

        initializeDetectorsAndSources();
    }

    private void initializeDetectorsAndSources() {
        Toast.makeText(getApplicationContext(), "Barcode Scanner started !", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                //.setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(BarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(BarcodeActivity.this, new String[]{
                                Manifest.permission.CAMERA
                        }, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), "To prevent memory leak, barcode scanner has been stoppped !", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    barcodeText.post(() -> {
                       if (barcodes.valueAt(0).email != null) {
                           barcodeText.removeCallbacks(null);
                           barcodeData = barcodes.valueAt(0).email.address;
                           barcodeText.setText(barcodeData);
                           toneGenerator1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                       } else {
                           barcodeData = barcodes.valueAt(0).displayValue;
                           barcodeText.setText(barcodeData);
                           toneGenerator1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                       }

                       checkItem();
                    });
                }
            }
        });
    }

    private void checkItem() {
        if (scanCode.equals(barcodeText.getText().toString())) return;
        scanCode = barcodeText.getText().toString();

        buttonCheckItem.post(() -> {
           buttonCheckItem.setEnabled(false);
           buttonCheckItem.setText(getString(R.string.wait));
        });

        ProjectAPI.getItem(barcodeText.getText().toString(), (responseCode, data, item) -> {
            if (responseCode == 200) {
                newItem = false;
                BarcodeActivity.this.item = item;

                buttonCheckItem.post(() -> {
                    buttonCheckItem.setEnabled(true);
                    buttonCheckItem.setText(getString(R.string.add));
                });
            } else if (responseCode == 404) {
                if (data.equals("Item not found !")) {
                    newItem = true;
                    buttonCheckItem.post(() -> {
                        buttonCheckItem.setEnabled(true);
                        buttonCheckItem.setText(getString(R.string.create_new_item_entry));
                    });
                } else {
                    System.err.println("Error when trying to get item !");
                    System.err.println(data);
                    Toast.makeText(getApplicationContext(), "Error when trying to get item !", Toast.LENGTH_SHORT).show();
                }
            } else {
                System.err.println("Error when trying to get item !");
                System.err.println(data);
                Toast.makeText(getApplicationContext(), "Error when trying to get item !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void connect() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("reason", "disconnected");
        startActivity(intent);
    }
}