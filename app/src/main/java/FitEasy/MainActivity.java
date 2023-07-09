package FitEasy;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;


import com.FitEasy.accelerometer.R;


import java.text.DecimalFormat;



//
//
//
//public class MainActivity extends AppCompatActivity {
//
//    private SensorManager mSensorManager;
//    private Sensor mAccelerometer;
//
//    private double accelerationCurrentValue;
//    private double accelerationPreviousValue;
//    TextView txt_currentAccel,txt_prevAccel,txt_acceleration;
//
//
//    private SensorEventListener sensorEventListener = new SensorEventListener() {
//        @Override
//        public void onSensorChanged(SensorEvent sensorEvent) {
//            float x = sensorEvent.values[0];
//            float y = sensorEvent.values[1];
//            float z = sensorEvent.values[2];
//
//            accelerationCurrentValue = Math.sqrt((x*x + y*y + z*z));
//
//            double changeInAcceleration = Math.abs(accelerationCurrentValue - accelerationPreviousValue);
//            accelerationPreviousValue = accelerationCurrentValue;
//
//            txt_currentAccel.setText("Current = " + (int) accelerationCurrentValue);
//            txt_prevAccel.setText("Prev = " + (int) accelerationPreviousValue);
//            txt_acceleration.setText("Acceleration change = " +  (int) changeInAcceleration);
//
//        }
//
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        txt_acceleration = findViewById(R.id.txt_accel);
//        txt_currentAccel = findViewById(R.id.txt_currentAccel);
//        txt_prevAccel = findViewById(R.id.txt_prevAccel);
//
//        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
//        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//
//
//
//
//    }
//    protected void onResume() {
//        super.onResume();
//        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//    }
//
//    protected void onPause() {
//        super.onPause();
//        mSensorManager.unregisterListener(sensorEventListener);
//    }
//}

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    DecimalFormat decimalFormat;
    private TextView accValueView;
    private Accelerometer accelerometer;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private SensorManager sensorManager;
    double ax,ay,az;   // these are the acceleration in x,y and z axis
    private float previousX = 0.0f;
    private float previousY = 0.0f;
    private float previousZ = 0.0f;

    private Sensor accelerometerSensor;
    //private SensorEventListener sensorEventListener;
    private TextView accelerometerValuesTextView;
    private TextView stepCountTextView;
    private int stepCount = 0;

    private ProgressBar stepProgressBar;

    private float gamma = 0.8f; // Filter smoothing factor
    private float[] gravity = new float[3]; // Gravity values
    private float[] linearAcceleration = new float[3]; // Linear acceleration values




        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                accelerometer = new Accelerometer();
                sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
                sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
                decimalFormat = new DecimalFormat("0.00");
                stepCountTextView = findViewById(R.id.stepCountTextView);
//            stepColorBar = findViewById(R.id.stepColorBar);

                stepProgressBar = findViewById(R.id.stepProgressBar);
                accValueView = findViewById(R.id.accelerometerValues);
        }


                @Override
                public void onSensorChanged(SensorEvent event) {

                        // Get accelerometer values
//        float x = event.values[0];
//        float y = event.values[1];
//        float z = event.values[2];
//
//        // Apply a high-pass filter to remove low-frequency components
//        float filteredX = gamma * previousX + (1 - gamma) * x;
//        float filteredY = gamma * previousY + (1 - gamma) * y;
//        float filteredZ = gamma * previousZ + (1 - gamma) * z;
//
//        // Apply a high-pass filter to remove gravity
//        gravity[0] = gamma * gravity[0] + (1 - gamma) * x;
//        gravity[1] = gamma * gravity[1] + (1 - gamma) * y;
//        gravity[2] = gamma * gravity[2] + (1 - gamma) * z;
//
//        // Calculate linear acceleration by subtracting gravity and low-frequency components
//        linearAcceleration[0] = x - gravity[0] - filteredX;
//        linearAcceleration[1] = y - gravity[1] - filteredY;
//        linearAcceleration[2] = z - gravity[2] - filteredZ;
//
//        // Apply noise reduction techniques (averaging)
//        int numSamples = 5; // Number of samples to average
//        float sumX = 0, sumY = 0, sumZ = 0;
//
//        for (int i = 0; i < numSamples; i++) {
//            sumX += linearAcceleration[0];
//            sumY += linearAcceleration[1];
//            sumZ += linearAcceleration[2];
//        }
//
//        // Calculate the average values
//        float averageX = sumX / numSamples;
//        float averageY = sumY / numSamples;
//        float averageZ = sumZ / numSamples;
//
//
//        // Update your UI or perform any other actions with the values
//        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
//            accValueView.setText("        X: " + averageX + ", Y:" + averageY + ", Z:" + averageZ);}
//        System.out.println("Sensor changed.");
                        if (event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION){
                                ax=event.values[0];
                                ay=event.values[1];
                                az=event.values[2];

                                // Apply noise reduction techniques (averaging)
                                int numSamples = 5; // Number of samples to average
                                float sumX = 0, sumY = 0, sumZ = 0;

                                for (int i = 0; i < numSamples; i++) {
                                        sumX += ax;
                                        sumY += ay;
                                        sumZ += az;
                                }

                                // Calculate the average values
                                float averageX = sumX / numSamples;
                                float averageY = sumY / numSamples;
                                float averageZ = sumZ / numSamples;
//            System.out.println("Sensor values : "+ax+ay+az);
                                accValueView.setText("        Accelerometer values : "+decimalFormat.format(averageX)+" "+decimalFormat.format(averageY)+" "+decimalFormat.format(averageZ));


                                // Use the averaged values for further processing or step detection
                                float currentX = averageX;
                                float currentY = averageY;
                                float currentZ = averageZ;

//            System.out.println("Step detected : "+isStepDetected(previousX, previousY, previousZ, currentX, currentY, currentZ));
                                // Check for a step by detecting peaks in the accelerometer readings
                                handleStepDetection(previousX, previousY, previousZ, currentX, currentY, currentZ);
                                if (isStepDetected(previousX, previousY, previousZ, currentX, currentY, currentZ)) {
                                        stepCount++;
                                        System.out.println(stepCount);

                                        // Update UI or perform any necessary actions for a detected step
                                        updateStepCount(stepCount);
//                // Update the ColorBar based on step count
//                updateColorBar(stepCount);
                                        // Update the Progress Bar based on step count
                                        updateProgressBar(stepCount);


                                }

                                previousX = currentX;
                                previousY = currentY;
                                previousZ = currentZ;

                        }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }

                public boolean isStepDetected(float previousX, float previousY, float previousZ, float currentX, float currentY, float currentZ) {
                        // Adjust these thresholds based on the characteristics of your accelerometer data
                        float stepThreshold = 2.0f; // Minimum difference in acceleration values for a step to be detected

                        // Check if the acceleration has crossed the threshold in the positive direction
                        if (previousY < stepThreshold && currentY >= stepThreshold) {
                                return true;
                        }

                        // Check if the acceleration has crossed the threshold in the negative direction
                        if (previousY > -stepThreshold && currentY <= -stepThreshold) {
                                return true;
                        }

                        if (previousX < stepThreshold && currentX >= stepThreshold) {
                                return true;
                        }

                        // Check if the acceleration has crossed the threshold in the negative direction
                        if (previousX > -stepThreshold && currentX <= -stepThreshold) {
                                return true;
                        }

                        if (previousZ < stepThreshold && currentZ >= stepThreshold) {
                                return true;
                        }

                        // Check if the acceleration has crossed the threshold in the negative direction
                        if (previousZ > -stepThreshold && currentZ <= -stepThreshold) {
                                return true;
                        }

                        // No step detected
                        return false;
                }

                // Call this method when you detect a step
                public void handleStepDetection(float previousX, float previousY, float previousZ, float currentX, float currentY, float currentZ) {
                        if (isStepDetected(previousX, previousY, previousZ, currentX, currentY, currentZ)) {
                                stepCount++;

                                // Calculate magnitude of acceleration
                                float magnitude = calculateMagnitude(currentX, currentY, currentZ);

                                // Update UI or perform any necessary actions with the magnitude value
                                updateMagnitude(magnitude);

                                // Update UI or perform any necessary actions for a detected step
                                updateStepCount(stepCount);

//                // Update the Color Bar based on step count
//                updateColorBar(stepCount);
                                // Update the Progress Bar  based on step count
                                updateProgressBar(stepCount);
                        }
                }

                private float calculateMagnitude(float x, float y, float z) {
                        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
                }


                private void updateStepCount(int count) {
                        stepCountTextView.setText(String.valueOf(count));
                }

                private void updateMagnitude(float magnitude) {
                        // Perform any necessary actions with the magnitude value
                        // For example, update UI elements, display a toast, or send the value to the game

                        // Update UI element
                        TextView magnitudeTextView = findViewById(R.id.magnitudeTextView);
                        magnitudeTextView.setText("Magnitude : "+ magnitude );

                        // Display a toast message
//        Toast.makeText(this, "Magnitude: " + magnitude, Toast.LENGTH_SHORT).show();

                        // Send the magnitude value to the game via Bluetooth or any other communication method
                        //sendMagnitudeToGame(magnitude);
                }

                private int normalizeValue(float value, float min, float max) {
                        return (int) ((value - min) / (max - min) * 255);
                }

                private void updateProgressBar(int stepCount) {
                        int maxSteps = 500; // Maximum number of steps for the ProgressBar
                        final int[] progress = {(int) ((stepCount / (float) maxSteps) * 100)};
                        System.out.println("Progress : "+ progress[0]);
                        //progressBar.setVisibility(View.VISIBLE);
                        Handler mHandler = new Handler();
                        stepProgressBar.setMax(500);
                        stepProgressBar.setProgress(progress[0]);


//          private void updateColorBar(int stepCount) {
//            int maxSteps = 80; // Maximum number of steps for the ProgressBar
//           int Color = (int) ((stepCount / (float) maxSteps) * 100);
//           stepColorBar.setColor(Color);

                }
}




