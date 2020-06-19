
package au.com.letsjoinin.android;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdressCaptureActivity extends Activity {
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{ Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    // Licensing
    private float areaOfInterestRatio = 0.5f;
    private boolean landscape;
    private double screen_ratio = 0.5d;
    private static final double MAX_AREA_OF_INTEREST_RATIO = 0.7d;
    private static final double MIN_AREA_OF_INTEREST_RATIO = 0.1d;
    private int count = 0;
    RelativeLayout layout;
    ///////////////////////////////////////////////////////////////////////////////
    // Some application settings that can be changed to modify application behavior:
    // The camera zoom. Optically zooming with a good camera often improves results
    // even at close range and it might be required at longer ranges.

    // Auxiliary variables
    private boolean inPreview = false; // Camera preview is started
    private Handler handler = new Handler(); // Posting some delayed actions;

    private LinearLayout emptyLayout;
    private ImageView img_drag;
    private LinearLayout.LayoutParams emptyLayoutParams;
    private LinearLayout resultsLayout;
    private LinearLayout sizable_panel;
    private LinearLayout.LayoutParams resultsLayoutParams;
    private MoveButtonTouchListener moveButtonTouchListener;
    private boolean InitialEntry = true;
    private int adj;
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        return;
                    }
                }
                // all permissions were granted
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        checkPermissions();
        boolean z;
        int orientationFlag = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        if (orientationFlag == 1 || orientationFlag == 3) {
            z = true;
        } else {
            z = false;
        }
        this.landscape = z;
        // Retrieve some ui components
//        SharedPreferences pref = getSharedPreferences("PrefRatio", 0);
//        String doubleStr = pref.getString("screen_ratio", null);
//        if (!TextUtils.isEmpty(doubleStr)) {
//            double ratio = Double.parseDouble(doubleStr);
//            areaOfInterestRatio = Float.parseFloat(doubleStr);
//            screen_ratio = ratio;
//        }
        // Initialize the recognition language spinner

        // Manually create preview surface. The only reason for this is to
        // avoid making it public top level class

        layout = (RelativeLayout) findViewById(R.id.layout);
        img_drag = (ImageView) findViewById(R.id.img_drag);
        this.emptyLayout = findViewById(R.id.empty_layout);

        this.resultsLayout = (LinearLayout) findViewById(R.id.results_layout);
        this.sizable_panel = (LinearLayout) findViewById(R.id.sizable_panel);
        this.emptyLayoutParams = (LinearLayout.LayoutParams) this.emptyLayout.getLayoutParams();
        this.resultsLayoutParams = (LinearLayout.LayoutParams) this.resultsLayout.getLayoutParams();

    }

    @Override
    public void onResume() {
        super.onResume();
        this.moveButtonTouchListener = new MoveButtonTouchListener();
        this.img_drag.setOnTouchListener(this.moveButtonTouchListener);

    }

    @Override
    public void onPause() {
        // Clear all pending actions
        handler.removeCallbacksAndMessages(null);
        // Stop the text capture service
        super.onPause();
    }




    // Returns orientation of camera

    /*50/2 52 Tambaram Velach
Main Road, Ezhil Nagar,
Selaiyur, Chennai, 6000
*/




    private class MoveButtonTouchListener implements View.OnTouchListener {
        boolean isMoving;
        float touchDelta;

        private MoveButtonTouchListener() {
            this.isMoving = false;
            this.touchDelta = 0.0f;
        }

        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() != 0) {
                double newRatio;
                if (landscape) {
                    newRatio = (double) ((event.getRawX() - this.touchDelta) / ((float) layout.getWidth()));
                } else {
                    newRatio = (double) ((event.getRawY() - this.touchDelta) / ((float) layout.getHeight()));
                }
                newRatio = constraintsOnAreaOfInterestRatio(newRatio);
                switch (event.getAction()) {
                    case 1:
                    case 3:
                        if (this.isMoving) {
                            this.isMoving = false;
                            screen_ratio = newRatio;
                            setAreaOfInterestRatio(newRatio, false);
                            break;
                        }
                        break;
                    case 2:
                        if (this.isMoving) {
                            setAreaOfInterestRatio(newRatio, true);
                            break;
                        }
                        break;
                    default:
                        break;
                }
            }
            float rawX;
            if (landscape) {
                rawX = event.getRawX() - (areaOfInterestRatio * ((float) layout.getWidth()));
            } else {
                rawX = event.getRawY() - (areaOfInterestRatio * ((float) layout.getHeight()));
            }
            this.touchDelta = rawX;
            this.isMoving = true;
            return true;
        }
    }

    public void setAreaOfInterestRatio(double ratio, boolean fast) {
        ratio = constraintsOnAreaOfInterestRatio(ratio);
        setAreaOfInterestRatio(ratio);

    }
    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public void setAreaOfInterestRatio(double ratio) {
        this.areaOfInterestRatio = (float) ratio;

        this.emptyLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        this.resultsLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        this.emptyLayoutParams.weight = 1.0f - this.areaOfInterestRatio;
        this.resultsLayoutParams.weight = this.areaOfInterestRatio;

        emptyLayout.setLayoutParams(emptyLayoutParams);
        resultsLayout.setLayoutParams(resultsLayoutParams);





        Log.d("AreaOfInteres","emptyLayoutParams - "+emptyLayoutParams.weight);
        Log.d("AreaOfInteres","resultsLayoutParams - "+resultsLayoutParams.weight);
    }

    public double constraintsOnAreaOfInterestRatio(double ratio) {
        if (ratio < MIN_AREA_OF_INTEREST_RATIO) {
            ratio = MIN_AREA_OF_INTEREST_RATIO;
        }
        if (ratio > MAX_AREA_OF_INTEREST_RATIO) {
            return MAX_AREA_OF_INTEREST_RATIO;
        }
        return ratio;
    }




}