package com.example.gryojava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    /* Sensor Objects */
    private SensorManager mSensorManager;
    private Sensor mSensorGyroUncali;
    private Sensor mSensorGyro;
    private Sensor mSensorAccelUncali;
    private Sensor mSensorAccel;
    private Sensor mSensorLight;
    private Sensor mSensorMagneto;
    private Sensor mSensorMagnetoUncali;

    /* Sensor Mode Booleans */
    private boolean isRecordingGyro;
    private boolean isRecordingAccel;
    private boolean isRecordingLight;
    private boolean isRecordingMagneto;

    /* Used to Display Values */
    private TextView mTextGyroSensorUncali;
    private TextView mTextGyroSensor;
    private TextView mTextTempSensor;
    private TextView mTextAccelSensor;
    private TextView mTextAccelSensorUncali;
    private TextView mTextAmbientLight;
    private TextView mTextMagneto;
    private TextView mTextMagnetoUncali;

    private TextView sampleInfo;


    /* Recording Sensor Data */
    private ArrayList<String> outputBuffer = new ArrayList<>();
    private boolean isInUncaliRecordingMode = false;
    private boolean isInCaliRecordingMode = false;
    int dataCollectionLimit;
    private EditText mEdit;

    /* Buttons */
    private Button generateButton;
    private Switch collectCalibrated;
        /* Sample Rate Radio Group */
    private RadioGroup sampleRateRadioGroup;
    private RadioButton radioNormal;
    private RadioButton radioUI;
    private RadioButton radioGaming;
    private RadioButton radioFastest;
        /* Sensor Mode Radio Group */
    private RadioGroup sensorModeRadioGroup;
    private RadioButton radioGyro;
    private RadioButton radioAccel;
    private RadioButton radioMagneto;
    private RadioButton radioLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Acquire list of Sensors */
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        /* Setting Up Text Input */
        collectionLimitInputSetup();

        /* Setting Up Buttons */
        setupAllButtons();

        /* Display List of Sensors */
        displayListOfSensors();

        /* Getting Gyro Values */
        sensorSetup();

    }

    /* Text Functions */
    private void collectionLimitInputSetup() {
        mEdit = findViewById((R.id.number_field));
        mEdit.setText("500");
        dataCollectionLimit = 500;
    }

    /* Sensor Functions */
    private void sensorSetup() {

        /* Setting Text Field Output and Sensor */
        mTextGyroSensorUncali = findViewById(R.id.gyro_readings_uncali);
        mTextGyroSensor = findViewById(R.id.gyro_readings);
        mTextTempSensor = findViewById(R.id.temperature_readings);
        mTextAccelSensor = findViewById(R.id.accel_readings);
        mTextAccelSensorUncali = findViewById(R.id.accel_readings_uncali);
        mTextMagneto = findViewById(R.id.magneto_readings);
        mTextMagnetoUncali = findViewById(R.id.magneto_readings_uncali);
        mTextAmbientLight = findViewById(R.id.light_readings);

        mSensorGyroUncali = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        mSensorGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorAccelUncali = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);
        mSensorLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorMagneto = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorMagnetoUncali = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);

        sampleInfo = findViewById(R.id.sample_info);

        /* Was Sensor Object Acquired Successfully? */
        if(mSensorGyroUncali == null || mSensorGyro == null) {
            mTextGyroSensorUncali.setText("NO GYROSCOPE");
        }
        if(mSensorGyroUncali == null)
            mTextGyroSensorUncali.setText("NO UNCALIBRATED GYROSCOPE");

        if(mSensorLight == null)
            mTextAmbientLight.setText("NO LIGHT SENSOR");

        if(mSensorAccelUncali == null)
            mTextAccelSensorUncali.setText("NO UNCALIBRATED ACCELEROMETER");

        if(mSensorAccel == null)
            mTextAccelSensor.setText("NO CALIBRATED ACCELEROMETER");

        if(mSensorMagnetoUncali == null)
            mTextMagnetoUncali.setText("NO UNCALIBRATED MAGNETOMETER");

        if(mSensorMagneto == null)
            mTextMagneto.setText("NO CALIBRATED MAGNETOMETER");

    }
    private void displayListOfSensors() {

        List<Sensor> listOfSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        StringBuilder sb = new StringBuilder();

        for(Sensor curSensor : listOfSensors)
            sb.append(curSensor.getName()).append(System.getProperty("line.separator"));

        TextView sensorTextView = findViewById(R.id.sensor_list);
        sensorTextView.setText(sb);
        sensorTextView.setMovementMethod(new ScrollingMovementMethod());
    }
    private void changeSensitivity(int sensitivity) {
        mSensorManager.unregisterListener(this);
        mSensorManager.registerListener(this, mSensorGyroUncali, sensitivity);
        mSensorManager.registerListener(this, mSensorGyro, sensitivity);
        mSensorManager.registerListener(this, mSensorAccelUncali, sensitivity);
        mSensorManager.registerListener(this, mSensorAccel, sensitivity);
        mSensorManager.registerListener(this, mSensorLight, sensitivity);
        mSensorManager.registerListener(this, mSensorMagneto, sensitivity);
        mSensorManager.registerListener(this, mSensorMagnetoUncali, sensitivity);
    }

    /* Button Functions */
    private void setupAllButtons() {
        setupGenerateButton();
        setupShareButton();
        setupSwitch();
        setupRadioGroup();
    }
    private void setupRadioGroup() {

        /* Sample Rate Group */
        sampleRateRadioGroup = findViewById(R.id.sampleRateRadioGroup);
        radioNormal = findViewById(R.id.radioOptionNormal);
        radioNormal.setChecked(true);
        radioUI = findViewById(R.id.radioOptionUI);
        radioGaming = findViewById(R.id.radioOptionGame);
        radioFastest = findViewById(R.id.radioOptionFastest);

        /* Sensor Mode Group */
        sensorModeRadioGroup = findViewById(R.id.sampleRateRadioGroup);
        radioGyro = findViewById(R.id.radioOptionGyro);
        radioGyro.setChecked(true);
        radioAccel = findViewById(R.id.radioOptionAccel);
        radioMagneto = findViewById(R.id.radioOptionMagneto);
        radioLight = findViewById(R.id.radioOptionLight);

    }
    private void setupSwitch() {
        collectCalibrated = findViewById(R.id.mode_switch);

    }
    private void setupGenerateButton() {
        generateButton = findViewById(R.id.generate_button);
        generateButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) { //Operates on Main Thread
                outputBuffer = new ArrayList<>();
                dataCollectionLimit = Integer.parseInt(mEdit.getText().toString());

                /* Sensitivity Radio */
                if(!radioNormal.isChecked())
                    if(radioFastest.isChecked())
                        changeSensitivity(SensorManager.SENSOR_DELAY_FASTEST);
                    else if(radioGaming.isChecked())
                        changeSensitivity(SensorManager.SENSOR_DELAY_GAME);
                    else if(radioUI.isChecked())
                        changeSensitivity(SensorManager.SENSOR_DELAY_UI);
                else
                    changeSensitivity(SensorManager.SENSOR_DELAY_NORMAL);

                /* Sensor Radio */
                if(radioGyro.isChecked()) {
                    isRecordingGyro = true;
                    isRecordingAccel = false;
                    isRecordingMagneto = false;
                    isRecordingLight = false;
                } else if(radioAccel.isChecked()) {
                    isRecordingGyro = false;
                    isRecordingAccel = true;
                    isRecordingMagneto = false;
                    isRecordingLight = false;
                } else if(radioMagneto.isChecked()) {
                    isRecordingGyro = false;
                    isRecordingAccel = false;
                    isRecordingMagneto = true;
                    isRecordingLight = false;
                } else {
                    isRecordingGyro = false;
                    isRecordingAccel = false;
                    isRecordingMagneto = false;
                    isRecordingLight = true;
                }

                if(collectCalibrated.isChecked()) {
                    isInCaliRecordingMode = true;
                    isInUncaliRecordingMode = false;
                } else {
                    isInCaliRecordingMode = false;
                    isInUncaliRecordingMode = true;
                }
            }
        });
    }
    private void setupShareButton() {
        generateButton = findViewById(R.id.share_button);
        generateButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) { //Operates on Main Thread

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, outputBuffer.toString().replaceAll(", ", "").replaceAll("\\[|\\]", ""));
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);


            }
        });
    }

    /* We use onStart() to registers sensors instead of onCreate() as the latter would cause the
    *  sensors to be sensing data and using power even with the app in the background */
    @Override
    protected void onStart() {
        super.onStart();

        if(mSensorGyroUncali != null)
            mSensorManager.registerListener(this, mSensorGyroUncali, SensorManager.SENSOR_DELAY_NORMAL);

        if(mSensorGyro != null)
            mSensorManager.registerListener(this, mSensorGyro, SensorManager.SENSOR_DELAY_NORMAL);

        if(mSensorAccelUncali != null)
            mSensorManager.registerListener(this, mSensorAccelUncali, SensorManager.SENSOR_DELAY_NORMAL);

        if(mSensorAccel != null)
            mSensorManager.registerListener(this, mSensorAccel, SensorManager.SENSOR_DELAY_NORMAL);

        if(mSensorMagneto != null)
            mSensorManager.registerListener(this, mSensorMagneto, SensorManager.SENSOR_DELAY_NORMAL);

        if(mSensorMagnetoUncali != null)
            mSensorManager.registerListener(this, mSensorMagnetoUncali, SensorManager.SENSOR_DELAY_NORMAL);

        /* Extra condition needed as Pixel has only ambient */
        if(mSensorLight != null & !mSensorLight.toString().toLowerCase().contains("ambient"))
            mSensorManager.registerListener(this, mSensorLight, SensorManager.SENSOR_DELAY_NORMAL);


    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        /* Acquiring Individual Axis Values */
        BigDecimal value1 = BigDecimal.valueOf(event.values[0]);
        BigDecimal value2 = BigDecimal.valueOf(event.values[1]);
        BigDecimal value3 = BigDecimal.valueOf(event.values[2]);

        /* Composing Relevant Strings */
        String threeAxisData = "X: " + value1 + "\nY: " + value2 + "\nZ: " + value3;
        String threeAxisRecordingDataFormat = value1 + "\t" + value2 + "\t" + value3 + "\n";
        String oneAxisRecordingDataFormat = value1 + "\n";
        sampleInfo.setText("No. Samples: " + outputBuffer.size());

        /* Handling Multiple Sensors */
        int sensorType = event.sensor.getType();
        switch(sensorType) {

                                        /* Calibrated */

            case Sensor.TYPE_GYROSCOPE:
                mTextGyroSensor.setText("Calibrated Gryo (rad/s):\n" + threeAxisData);
                if(isRecordingGyro)
                    recordThreeAxisDataCalibrated(threeAxisRecordingDataFormat);
                break;

            case Sensor.TYPE_ACCELEROMETER:
                mTextAccelSensor.setText("Calibrated Accel (m/s)^2:\n" + threeAxisData);
                if(isRecordingAccel)
                    recordThreeAxisDataCalibrated(threeAxisRecordingDataFormat);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                mTextMagneto.setText("Magneto Calibrated (μT):\n" + threeAxisData);
                if(isRecordingMagneto)
                    recordThreeAxisDataCalibrated(threeAxisRecordingDataFormat);
                break;

                                        /* Uncalibrated */

            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                mTextGyroSensorUncali.setText("Uncalibrated Gyro (rad/s):\n" + threeAxisData);
                if(isRecordingGyro)
                    recordThreeAxisDataUncalibrated(threeAxisRecordingDataFormat);
                break;

            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                mTextAccelSensorUncali.setText("Uncalibrated Accel (m/s)^2:\n" + threeAxisData);
                if(isRecordingAccel)
                    recordThreeAxisDataUncalibrated(threeAxisRecordingDataFormat);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                mTextMagnetoUncali.setText("Magneto Uncalibrated (μT):\n" + threeAxisData);
                if(isRecordingMagneto)
                    recordThreeAxisDataUncalibrated(threeAxisRecordingDataFormat);
                break;

                                        /* Misc. */

            case Sensor.TYPE_LIGHT:
                mTextAmbientLight.setText("Light Sensor (lx):\n" + value1);
                if(isRecordingLight)
                    recordOneAxisData(oneAxisRecordingDataFormat);
                break;

            default:
                break;

        }

    }
    public void recordThreeAxisDataCalibrated(String data) {

        if(isInCaliRecordingMode)
            if(outputBuffer.size() <= dataCollectionLimit)
                outputBuffer.add(data);
            else if(outputBuffer.size() >= dataCollectionLimit) {
                isInCaliRecordingMode = false;
            }

    }
    public void recordThreeAxisDataUncalibrated(String data) {
        if(isInUncaliRecordingMode)
            if(outputBuffer.size() <= dataCollectionLimit)
                outputBuffer.add(data);
            else if(outputBuffer.size() >= dataCollectionLimit) {
                isInUncaliRecordingMode = false;
            }
    }
    public void recordOneAxisData(String data) {
        if(isInCaliRecordingMode)
            if(outputBuffer.size() <= dataCollectionLimit)
                outputBuffer.add(data);
            else if(outputBuffer.size() >= dataCollectionLimit) {
                isInCaliRecordingMode = false;
            }
    }
    /* Prevents app from using Sensor when not in foreground */
    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        /* Most sensors do not implement this method. Will leave blank for now */
    }

}
