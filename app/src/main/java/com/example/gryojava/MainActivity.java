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
    private Sensor mSensorAmbientLight;
    private Sensor mTemperatureSensor;

    /* Used to Display Values */
    private TextView mTextGyroSensorUncali;
    private TextView mTextGyroSensor;
    private TextView mTextTempSensor;
    private TextView mTextAccelSensor;
    private TextView mTextAccelSensorUncali;
    private TextView mTextAmbientLight;
    private TextView sampleInfo;
    //private TextView isCheckedInfo;

    /* Recording Sensor Data */
    private ArrayList<String> gyroOutputList = new ArrayList<>();
    private boolean isInUncaliRecordingMode = false;
    private boolean isInCaliRecordingMode = false;
    int dataCollectionLimit;
    private EditText mEdit;

    /* Buttons */
    private Button generateButton;
    private Switch collectCalibrated;
    private RadioGroup sampleRateRadioGroup;
    private RadioButton radioNormal;
    private RadioButton radioUI;
    private RadioButton radioGaming;
    private RadioButton radioFastest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Initiate Uncalibrated Data Logger */
        gyroOutputList = new ArrayList<>();

        /* Acquire list of Sensors */
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        /* Setting up Text */
        mEdit = findViewById((R.id.number_field));
        mEdit.setText("500");
        dataCollectionLimit = 500;
        //isCheckedInfo = findViewById(R.id.checkedTextView);


        /* Setting Up Button */
        setupGenerateButton();
        setupShareButton();
        setupSwitch();
        setupRadioGroup();

        /* Display List of Sensors */
        displayListOfSensors();

        /* Getting Gyro Values */
        sensorSetup();

    }

    private void sensorSetup() {

        /* Setting Text Field Output and Sensor */
        mTextGyroSensorUncali = findViewById(R.id.gyro_readings_uncali);
        mTextGyroSensor = findViewById(R.id.gyro_readings);
        mTextTempSensor = findViewById(R.id.temperature_readings);
        mTextAccelSensor = findViewById(R.id.accel_readings);
        mTextAccelSensorUncali = findViewById(R.id.accel_readings_uncali);
        mTextAmbientLight = findViewById(R.id.ambient_light_readings);

        mSensorGyroUncali = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        mSensorGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mTemperatureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mSensorAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorAccelUncali = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);
        mSensorAmbientLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);


        sampleInfo = findViewById(R.id.sample_info);

        /* Was Sensor Object Acquired Successfully? */
        if(mSensorGyroUncali == null || mSensorGyro == null) {
            mTextGyroSensorUncali.setText("NO GYROSCOPE");
        }

        if(mSensorAmbientLight == null)
            mTextAmbientLight.setText("NO LIGHT SENSOR");
        else
            mTextAmbientLight.setText(mSensorAmbientLight.toString());

        if(mSensorAccelUncali == null || mSensorAccel == null) {
            mTextAccelSensorUncali.setText("No Accelerometer Uncali");
            mTextAccelSensorUncali.setText("No Accelerometer Calibrated");
        }

        if(mTemperatureSensor == null) {
            mTextTempSensor.setText("NO TEMPERATURE SENSOR");
        }


    }
    private void changeSensitivity(int sensitivity) {
        mSensorManager.unregisterListener(this);
        mSensorManager.registerListener(this, mSensorGyroUncali, sensitivity);
        mSensorManager.registerListener(this, mSensorGyro, sensitivity);
        mSensorManager.registerListener(this, mSensorAccelUncali, sensitivity);
        mSensorManager.registerListener(this, mSensorAccel, sensitivity);
        mSensorManager.registerListener(this, mSensorAmbientLight, sensitivity);
    }

    private void setupRadioGroup() {

        sampleRateRadioGroup = findViewById(R.id.sampleRateRadioGroup);

        radioNormal = findViewById(R.id.radioOptionNormal);
        radioNormal.setChecked(true);
        radioUI = findViewById(R.id.radioOptionUI);
        radioGaming = findViewById(R.id.radioOptionGame);
        radioFastest = findViewById(R.id.radioOptionFastest);
    }
    private void setupSwitch() {
        collectCalibrated = findViewById(R.id.mode_switch);


    }
    private void setupGenerateButton() {
        generateButton = findViewById(R.id.generate_button);
        generateButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) { //Operates on Main Thread
                gyroOutputList = new ArrayList<>();
                dataCollectionLimit = Integer.parseInt(mEdit.getText().toString());

                if(!radioNormal.isChecked())
                    if(radioFastest.isChecked())
                        changeSensitivity(SensorManager.SENSOR_DELAY_FASTEST);
                    else if(radioGaming.isChecked())
                        changeSensitivity(SensorManager.SENSOR_DELAY_GAME);
                    else if(radioUI.isChecked())
                        changeSensitivity(SensorManager.SENSOR_DELAY_UI);
                else
                    changeSensitivity(SensorManager.SENSOR_DELAY_NORMAL);

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
                sendIntent.putExtra(Intent.EXTRA_TEXT, gyroOutputList.toString().replaceAll(", ", "").replaceAll("\\[|\\]", ""));
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);


            }
        });
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

        if(mTemperatureSensor != null)
            mSensorManager.registerListener(this, mTemperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);

        //if(mTextAmbientLight != null)
          //  mSensorManager.registerListener(this, mSensorAmbientLight, SensorManager.SENSOR_DELAY_NORMAL);


    }

    /* Prevents app from using Sensor when not in foreground */
    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //isCheckedInfo.setText(String.valueOf(collectCalibrated.isChecked()));

        BigDecimal value1 = BigDecimal.valueOf(event.values[0]);
        BigDecimal value2 = BigDecimal.valueOf(event.values[1]);
        BigDecimal value3 = BigDecimal.valueOf(event.values[2]);

        String gyroData = "X: " + value1 + "\nY: " + value2 + "\nZ: " + value3;
        sampleInfo.setText("No. Samples: " + gyroOutputList.size());

        /* Handling Multiple Sensors */
        int sensorType = event.sensor.getType();
        switch(sensorType) {

            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                mTextGyroSensorUncali.setText("Uncalibrated Gyro:\n" + gyroData);

                if(isInUncaliRecordingMode)
                    if(gyroOutputList.size() <= dataCollectionLimit)
                        gyroOutputList.add(value1 + "\t" + value2 + "\t" + value3 + "\n");
                    else if(gyroOutputList.size() >= dataCollectionLimit) {
                        isInUncaliRecordingMode = false;
                    }
                break;
            case Sensor.TYPE_GYROSCOPE:
                mTextGyroSensor.setText("Calibrated Gryo:\n" + gyroData);

                if(isInCaliRecordingMode)
                    if(gyroOutputList.size() <= dataCollectionLimit)
                        gyroOutputList.add(value1 + "\t" + value2 + "\t" + value3 + "\n");
                    else if(gyroOutputList.size() >= dataCollectionLimit) {
                        isInCaliRecordingMode = false;
                    }
                break;
            case Sensor.TYPE_ACCELEROMETER:
                mTextAccelSensor.setText("Calibrated Accelerometer:\n" + gyroData);

                if(isInCaliRecordingMode)
                    if(gyroOutputList.size() <= dataCollectionLimit)
                        gyroOutputList.add(value1 + "\t" + value2 + "\t" + value3 + "\n");
                    else if(gyroOutputList.size() >= dataCollectionLimit) {
                        isInCaliRecordingMode = false;
                    }
                break;
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                mTextAccelSensorUncali.setText("Uncalibrated Accelerometer:\n" + gyroData);

                if(isInCaliRecordingMode)
                    if(gyroOutputList.size() <= dataCollectionLimit)
                        gyroOutputList.add(value1 + "\t" + value2 + "\t" + value3 + "\n");
                    else if(gyroOutputList.size() >= dataCollectionLimit) {
                        isInCaliRecordingMode = false;
                    }
                break;

            case Sensor.TYPE_LIGHT:
                mTextAmbientLight.setText("Light Sensor:\n" + value1 + "C");
                if(isInCaliRecordingMode)
                    if(gyroOutputList.size() <= dataCollectionLimit)
                        gyroOutputList.add(value1 + "\n");
                    else if(gyroOutputList.size() >= dataCollectionLimit) {
                        isInCaliRecordingMode = false;
                    }

                break;


            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                mTextTempSensor.setText(String.valueOf(value1));
                break;
            default:



        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        /* Most sensors do not implement this method. Will leave blank for now */
    }

}
