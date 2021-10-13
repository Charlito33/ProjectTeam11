package org.adalovelacehackaton.teameleven.project;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.adalovelacehackaton.teameleven.project.databinding.ActivityBarcodeBinding;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBarcodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toneGenerator1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        surfaceView = findViewById(R.id.surface_view);
        barcodeText = findViewById(R.id.barcode_text);

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
                    });
                }
            }
        });
    }
}