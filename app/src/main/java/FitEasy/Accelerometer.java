package FitEasy;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.FitEasy.accelerometer.R;


public class Accelerometer extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    //private SensorEventListener sensorEventListener;
    private TextView accelerometerValuesTextView;
    private TextView stepCountTextView;
    private int stepCount = 0;
    private float previousX = 0.0f;
    private float previousY = 0.0f;
    private float previousZ = 0.0f;
    private ProgressBar stepProgressBar;
    //        private ColorBar stepColorBar;

    //private float previousX, previousY, previousZ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            if(accelerometerSensor !=null){
                sensorManager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
            }
        }else{
            Toast.makeText(this,"Sensor not detected.",Toast.LENGTH_SHORT).show();
        }


        Log.d("YourActivity", "Accelerometer sensor registered: " + (accelerometerSensor != null));



        // Initialize the TextViews
        accelerometerValuesTextView = findViewById(R.id.accelerometerValues);
        stepCountTextView = findViewById(R.id.stepCountTextView);
//            stepColorBar = findViewById(R.id.stepColorBar);

        stepProgressBar = findViewById(R.id.stepProgressBar);
    }

    private float gamma = 0.8f; // Filter smoothing factor
    private float[] gravity = new float[3]; // Gravity values
    private float[] linearAcceleration = new float[3]; // Linear acceleration values


    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            if(accelerometerSensor !=null){
                sensorManager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
            }
        }else{
            Toast.makeText(this,"Sensor not detected.",Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {

        System.out.println("Sensor changed hhd");
        // Get accelerometer values
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // Apply a high-pass filter to remove low-frequency components
        float filteredX = gamma * previousX + (1 - gamma) * x;
        float filteredY = gamma * previousY + (1 - gamma) * y;
        float filteredZ = gamma * previousZ + (1 - gamma) * z;

        // Apply a high-pass filter to remove gravity
        gravity[0] = gamma * gravity[0] + (1 - gamma) * x;
        gravity[1] = gamma * gravity[1] + (1 - gamma) * y;
        gravity[2] = gamma * gravity[2] + (1 - gamma) * z;

        // Calculate linear acceleration by subtracting gravity and low-frequency components
        linearAcceleration[0] = x - gravity[0] - filteredX;
        linearAcceleration[1] = y - gravity[1] - filteredY;
        linearAcceleration[2] = z - gravity[2] - filteredZ;

        // Apply noise reduction techniques (averaging)
        int numSamples = 5; // Number of samples to average
        float sumX = 0, sumY = 0, sumZ = 0;

        for (int i = 0; i < numSamples; i++) {
            sumX += linearAcceleration[0];
            sumY += linearAcceleration[1];
            sumZ += linearAcceleration[2];
        }

        // Calculate the average values
        float averageX = sumX / numSamples;
        float averageY = sumY / numSamples;
        float averageZ = sumZ / numSamples;


        // Update your UI or perform any other actions with the values
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            accelerometerValuesTextView.setText("X: " + averageX + ", Y:" + averageY + ", Z:" + averageZ);}

        // Use the averaged values for further processing or step detection
        float currentX = averageX;
        float currentY = averageY;
        float currentZ = averageZ;

        // Check for a step by detecting peaks in the accelerometer readings
        if (isStepDetected(previousX, previousY, previousZ, currentX, currentY, currentZ)) {
            stepCount++;

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


//        @Override
//        public void onSensorChanged(SensorEvent event) {
//            // Get the accelerometer values
//            float x = event.values[0];
//            float y = event.values[1];
//            float z = event.values[2];
//
//            // Update your UI or perform any other actions with the values
//            accelerometerValuesTextView.setText("Accelerometer Values: " + x + ", " + y + ", " + z);

//
//            float currentX = event.values[0];
//            float currentY = event.values[1];
//            float currentZ = event.values[2];
//
//            // Check for a step by detecting peaks in the accelerometer readings
//            if (isStepDetected(previousX, previousY, previousZ, currentX, currentY, currentZ)) {
//                stepCount++;
//
//                // Update UI or perform any necessary actions for a detected step
//                updateStepCount(stepCount);
//            }
//
//            previousX = currentX;
//            previousY = currentY;
//            previousZ = currentZ;
//        }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
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
        magnitudeTextView.setText(" "+ magnitude );

        // Display a toast message
        Toast.makeText(this, "Magnitude: " + magnitude, Toast.LENGTH_SHORT).show();

        // Send the magnitude value to the game via Bluetooth or any other communication method
        //sendMagnitudeToGame(magnitude);
    }

    private int normalizeValue(float value, float min, float max) {
        return (int) ((value - min) / (max - min) * 255);
    }

    private void updateProgressBar(int stepCount) {
        int maxSteps = 100; // Maximum number of steps for the ProgressBar
        int progress = (int) ((stepCount / (float) maxSteps) * 100);
        stepProgressBar.setProgress(progress);

//          private void updateColorBar(int stepCount) {
//            int maxSteps = 80; // Maximum number of steps for the ProgressBar
//           int Color = (int) ((stepCount / (float) maxSteps) * 100);
//           stepColorBar.setColor(Color);

    }

}




//        public boolean isStepDetected(float previousX, float previousY, float previousZ, float currentX, float currentY, float currentZ) {
//            // Adjust these thresholds based on the characteristics of your accelerometer data
//            float stepThreshold = 2.0f; // Minimum difference in acceleration values for a step to be detected
//
//            // Check if the acceleration has crossed the threshold in the positive direction
//            if (previousY < stepThreshold && currentY >= stepThreshold) {
//                return true;
//            }
//
//            // Check if the acceleration has crossed the threshold in the negative direction
//            if (previousY > -stepThreshold && currentY <= -stepThreshold) {
//                return true;
//            }
//
//            if (previousX < stepThreshold && currentX >= stepThreshold) {
//                return true;
//            }
//
//            // Check if the acceleration has crossed the threshold in the negative direction
//            if (previousX > -stepThreshold && currentX <= -stepThreshold) {
//                return true;
//            }
//
//            if (previousZ < stepThreshold && currentZ >= stepThreshold) {
//                return true;
//            }
//
//            // Check if the acceleration has crossed the threshold in the negative direction
//            return previousZ > -stepThreshold && currentZ <= -stepThreshold;
//
//
//            // Check for a step by detecting peaks in the accelerometer readings
//            if (isStepDetected(previousX, previousY, previousZ, currentX, currentY, currentZ)) {
//                stepCount++;
//
//                // Calculate magnitude of acceleration
//                float magnitude = calculateMagnitude(currentX, currentY, currentZ);
//
//                // Update UI or perform any necessary actions with the magnitude value
//                updateMagnitude(magnitude);
//
//                // Update UI or perform any necessary actions for a detected step
//                updateStepCount(stepCount);
////            }
//        public boolean isStepDetected(float previousX, float previousY, float previousZ, float currentX, float currentY, float currentZ) {
//            // Adjust these thresholds based on the characteristics of your accelerometer data
//            float stepThreshold = 2.0f; // Minimum difference in acceleration values for a step to be detected
//
//            // Check if the acceleration has crossed the threshold in the positive direction
//            if (previousY < stepThreshold && currentY >= stepThreshold) {
//                return true;
//            }
//
//            // Check if the acceleration has crossed the threshold in the negative direction
//            if (previousY > -stepThreshold && currentY <= -stepThreshold) {
//                return true;
//            }
//
//            if (previousX < stepThreshold && currentX >= stepThreshold) {
//                return true;
//            }
//
//            // Check if the acceleration has crossed the threshold in the negative direction
//            if (previousX > -stepThreshold && currentX <= -stepThreshold) {
//                return true;
//            }
//
//            if (previousZ < stepThreshold && currentZ >= stepThreshold) {
//                return true;
//            }
//
//            // Check if the acceleration has crossed the threshold in the negative direction
//            if (previousZ > -stepThreshold && currentZ <= -stepThreshold) {
//                return true;
//            }
//
//            // No step detected
//            return false;
//        }
//
//        // Call this method when you detect a step
//        public void handleStepDetection(float previousX, float previousY, float previousZ, float currentX, float currentY, float currentZ) {
//            if (isStepDetected(previousX, previousY, previousZ, currentX, currentY, currentZ)) {
//                stepCount++;
//
//                // Calculate magnitude of acceleration
//                float magnitude = calculateMagnitude(currentX, currentY, currentZ);
//
//                // Update UI or perform any necessary actions with the magnitude value
//                updateMagnitude(magnitude);
//
//                // Update UI or perform any necessary actions for a detected step
//                updateStepCount(stepCount);
//            }
//        }
//
//
//        private float calculateMagnitude(float x, float y, float z) {
//            return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
//        }
//
//
//        private void updateStepCount(int count) {
//            stepCountTextView.setText(String.valueOf(count));
//        }
//
//        private void updateMagnitude(float magnitude) {
//            // Perform any necessary actions with the magnitude value
//            // For example, update UI elements, display a toast, or send the value to the game
//
//            // Update UI element
//            TextView magnitudeTextView = findViewById(R.id.magnitudeTextView);
//            magnitudeTextView.setText("Magnitude: " + magnitude);
//
//            // Display a toast message
//            Toast.makeText(this, "Magnitude: " + magnitude, Toast.LENGTH_SHORT).show();
//
//            // Send the magnitude value to the game via Bluetooth or any other communication method
//            //sendMagnitudeToGame(magnitude);
//        }
//        private int normalizeValue(float value, float min, float max) {
//            return (int) ((value - min) / (max - min) * 255);
//        }
//
//    }
//

//
//    int normalizedValue = normalizeValue(magnitude, min, max);
//    String dataToSend = "Hello, game device!";
//    byte[] data = new byte[]{(byte) normalizedValue};
//    try{
//
//    }



//    private void sendMagnitudeToGame(float magnitude) {
//        // Implement the code to send the magnitude value to the game
//        // For example, using Bluetooth communication or API requests
//        // You can use the previously provided code for Bluetooth communication to establish a connection and send data to the game
//        // Replace the placeholder URL with your actual game's URL
//
//        String gameUrl = "https://www.fiteasy.shop/";
//        // Send the magnitude value to the game using the desired method (Bluetooth, API, etc.)
//        // Example: Send the magnitude value via Bluetooth using the connected socket
//        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
//            try {
//                // Convert the magnitude value to bytes and send it via Bluetooth
//                byte[] magnitudeBytes = ByteBuffer.allocate(4).putFloat(magnitude).array();
//                bluetoothSocket.getOutputStream().write(magnitudeBytes);
//            } catch (IOException e) {
//                // Handle any IO errors
//                e.printStackTrace();
//            }
//        } else {
//            // Handle the case when the Bluetooth socket is not connected
//            // You can display an error message or try to establish the connection again
//        }
//    }


//        // Alternatively, you can send the magnitude value to the game via API requests
//        // Example: Send the magnitude value to the game using an HTTP POST request
//        OkHttpClient client = new OkHttpClient();
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody requestBody = RequestBody.create(mediaType, "{\"magnitude\": " + magnitude + "}");
//        Request request = new Request.Builder()
//                .url(gameUrl)
//                .post(requestBody)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                // Handle any API request failures
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                // Handle the API response
//                if (response.isSuccessful()) {
//                    // The magnitude value was successfully sent to the game
//                    // You can perform any necessary actions based on the response
//                } else {
//                    // The API request was not successful
//                    // You can handle the error case or retry the request if needed
//                }
//            }
//        });
//    }





//    public class YourActivity extends AppCompatActivity implements SensorEventListener {
//
//        private TextView accelerometerValuesTextView;
//
////        @Override
////        protected void onCreate(Bundle savedInstanceState) {
////            super.onCreate(savedInstanceState);
////            setContentView(R.layout.activity_main);
////            // Initialize the TextView
////            accelerometerValuesTextView = findViewById(R.id.accelerometerValues);
////
////        }
//
////        @Override
////        public void onSensorChanged(SensorEvent event) {
////            // Get the accelerometer values
////            float x = event.values[0];
////            float y = event.values[1];
////            float z = event.values[2];
////
////            // Update your UI or perform any other actions with the values
////            accelerometerValuesTextView.setText("Accelerometer Values: " + x + ", " + y + ", " + z);
////        }
////
////
//        private TextView stepCountTextView;
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_main);
//            // Initialize the TextView
//            accelerometerValuesTextView = findViewById(R.id.accelerometerValues);
//
//            // Find the TextView by its ID
//            TextView stepCountTextView = findViewById(R.id.stepCountTextView);
//
//           // Update the step count text whenever a step is detected
//            private void updateStepCount(int count) {
//                stepCountTextView.setText(String.valueOf(count));
//            }
//
//
//        }
//        @Override
//        public void onSensorChanged(SensorEvent event) {
//            // Get the accelerometer values
//            float x = event.values[0];
//            float y = event.values[1];
//            float z = event.values[2];
//
//            // Update your UI or perform any other actions with the values
//            accelerometerValuesTextView.setText("Accelerometer Values: " + x + ", " + y + ", " + z);
//
//
//
//            float currentY = event.values[1]; // Assuming Y-axis is vertical
//            float currentX = event.values[0];
//            float currentZ = event.values[2];
//
//
//
//            // Check for a step by detecting peaks in the accelerometer readings
//            if (isStepDetected(previousX,previousY,previousZ, currentX,currentY,currentZ)) {
//                stepCount++;
//
//                // Update UI or perform any necessary actions for a detected step
//                updateStepCount(stepCount);
//            }
//
//            previousX = currentX;
//            previousY = currentY;
//            previousZ = currentZ;
//
//        }
//
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//            // Do nothing
//        }
//
//
//
//    }
//    private boolean isStepDetected(float  previousX,float  previousY,float  previousZ, float currentX,float currentY,float currentZ) {
//        // Adjust these thresholds based on the characteristics of your accelerometer data
//        float stepThreshold = 2.0f; // Minimum difference in acceleration values for a step to be detected
//
//        // Check if the acceleration has crossed the threshold in the positive direction
//        if (previousY < stepThreshold && currentY >= stepThreshold) {
//            return true;
//        }
//
//        // Check if the acceleration has crossed the threshold in the negative direction
//        if (previousY > -stepThreshold && currentY <= -stepThreshold) {
//            return true;
//        }
//
//        if (previousX < stepThreshold && currentX >= stepThreshold) {
//            return true;
//        }
//
//        // Check if the acceleration has crossed the threshold in the negative direction
//        if (previousX > -stepThreshold && currentX <= -stepThreshold) {
//            return true;
//        }
//
//        if (previousZ < stepThreshold && currentZ >= stepThreshold) {
//            return true;
//        }
//
//        // Check if the acceleration has crossed the threshold in the negative direction
//        if (previousZ > -stepThreshold && currentZ <= -stepThreshold) {
//            return true;
//        }
//
//
//
//        return false;
//    }
//
//
//
//
//    private void updateStepCount(int count) {
//        // Update a TextView or any other UI element with the step count
//        stepCountTextView.setText(String.valueOf(count));
//    }
//}



//    public class YourActivity extends AppCompatActivity implements SensorEventListener {
//
//
//        @Override
//        public void onSensorChanged(SensorEvent event) {
//            // Get the accelerometer values
//            float x = event.values[0];
//            float y = event.values[1];
//            float z = event.values[2];
//
//            // Update your UI or perform any other actions with the values
//            accelerometerValuesTextView.setText("Accelerometer Values: " + x + ", " + y + ", " + z);
//        }
//
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//            // Handle accuracy changes if needed
//        }
//        TextView accelerometerValuesTextView = findViewById(R.id.accelerometerValues);
//// As you receive accelerometer data, update the text of accelerometerValuesTextView with the new values
//
//    }



//
//    private boolean isStepDetected(float previousY, float currentY) {
//        // Adjust these thresholds based on the characteristics of your accelerometer data
//        float stepThreshold = 2.0f; // Minimum difference in acceleration values for a step to be detected
//
//        // Check if the acceleration has crossed the threshold in the positive direction
//        if (previousY < stepThreshold && currentY >= stepThreshold) {
//            return true;
//        }
//
//        // Check if the acceleration has crossed the threshold in the negative direction
//        if (previousY > -stepThreshold && currentY <= -stepThreshold) {
//            return true;
//        }
//
//        return false;
//    }
//
//
//
//    private void updateStepCount(int count) {
//        // Update a TextView or any other UI element with the step count
//        stepCountTextView.setText(String.valueOf(count));
//    }
//}
