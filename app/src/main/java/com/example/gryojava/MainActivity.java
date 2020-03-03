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
    private boolean isRecordingAll;

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

    private ArrayList<String> gyroBuffer = new ArrayList<>();
    private ArrayList<String> accelBuffer = new ArrayList<>();
    private ArrayList<String> magnetoBuffer = new ArrayList<>();
    private ArrayList<String> lightBuffer = new ArrayList<>();


    private boolean isInUncaliRecordingMode = false;
    private boolean isInCaliRecordingMode = false;
    private boolean hasLightSensor = true;
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
    private RadioButton radioAll;

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
    private void unregisterListeners() {
        mSensorManager.unregisterListener(this);
    }
    private void registerListeners() {
        mSensorManager.registerListener(this, mSensorGyroUncali, mSensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorGyro, mSensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorAccelUncali, mSensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorAccel, mSensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorLight, mSensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorMagneto, mSensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorMagnetoUncali, mSensorManager.SENSOR_DELAY_NORMAL);
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
        radioAll = findViewById(R.id.radioOptionAll);

    }
    private void setupSwitch() {
        collectCalibrated = findViewById(R.id.mode_switch);

    }
    private void setupGenerateButton() {
        generateButton = findViewById(R.id.generate_button);
        generateButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) { //Operates on Main Thread

                /* Reset Buffers */
                outputBuffer = new ArrayList<>();
                gyroBuffer = new ArrayList<>();
                gyroBuffer.add("--------------- GYRO VALUES ------------\n");
                accelBuffer = new ArrayList<>();
                accelBuffer.add("--------------- ACCEL VALUES ------------\n");
                magnetoBuffer = new ArrayList<>();
                magnetoBuffer.add("--------------- MAGNETO VALUES ------------\n");
                lightBuffer = new ArrayList<>();
                lightBuffer.add("--------------- LIGHT VALUES ------------\n");

                /* Obtain New Data Collection Goal */
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
                    isRecordingAll = false;
                } else if(radioAccel.isChecked()) {
                    isRecordingGyro = false;
                    isRecordingAccel = true;
                    isRecordingMagneto = false;
                    isRecordingLight = false;
                    isRecordingAll = false;
                } else if(radioMagneto.isChecked()) {
                    isRecordingGyro = false;
                    isRecordingAccel = false;
                    isRecordingMagneto = true;
                    isRecordingLight = false;
                    isRecordingAll = false;
                } else if(radioLight.isChecked()){
                    isRecordingGyro = false;
                    isRecordingAccel = false;
                    isRecordingMagneto = false;
                    isRecordingLight = true;
                    isRecordingAll = false;
                } else if(radioAll.isChecked()) {
                    isRecordingGyro = false;
                    isRecordingAccel = false;
                    isRecordingMagneto = false;
                    isRecordingLight = false;
                    isRecordingAll = true;
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

                isInUncaliRecordingMode = false;
                isInCaliRecordingMode = false;

                if(isRecordingAll) {

                    outputBuffer = new ArrayList<>();

                    outputBuffer.addAll(gyroBuffer);
                    outputBuffer.addAll(accelBuffer);
                    outputBuffer.addAll(magnetoBuffer);
                    outputBuffer.addAll(lightBuffer);

                    int absoluteLimit = outputBuffer.size();
                    int transferLimit = 1500;
                    int noRounds = (absoluteLimit/transferLimit) + 1;


                    /* Android transfer limit == 1500 lines */
                    for(int multiplier = 1; multiplier <= noRounds; multiplier ++) {
                        StringBuilder sb = new StringBuilder();

                        /* Filling Up Current Transfer Array */
                        for(int pos = transferLimit * (multiplier-1); (pos < (transferLimit * multiplier)) & (pos < absoluteLimit); pos++)
                            sb.append(outputBuffer.get(pos));

                        System.out.println("OutputBuffer Size: " + outputBuffer.size());
                        System.out.println(sb.length());

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString().replaceAll(", ", "").replaceAll("\\[|\\]", ""));
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        startActivity(shareIntent);
                    }

                } else {

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, outputBuffer.toString().replaceAll(", ", "").replaceAll("\\[|\\]", ""));
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);

                }



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

        if(mSensorLight.toString().toLowerCase().contains("ambient"))
            hasLightSensor = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        int sensorType = event.sensor.getType();

        /* Acquiring Individual Axis Values */
        BigDecimal value1 = BigDecimal.valueOf(event.values[0]);
        BigDecimal value2 = null;
        BigDecimal value3 = null;
        String threeAxisData = "";
        String threeAxisRecordingDataFormat = "";


        if(sensorType != Sensor.TYPE_LIGHT) {
            value2 = BigDecimal.valueOf(event.values[1]);
            value3 = BigDecimal.valueOf(event.values[2]);
            threeAxisData = "X: " + value1 + "\nY: " + value2 + "\nZ: " + value3;
            threeAxisRecordingDataFormat = value1 + "\t" + value2 + "\t" + value3 + "\n";


        }
        /* Composing Relevant Strings */

        String oneAxisRecordingDataFormat = value1 + "\n";

        if(!isRecordingAll)
            sampleInfo.setText("No. Samples: " + outputBuffer.size());
        else
            sampleInfo.setText("No. Samples: " + (accelBuffer.size() + gyroBuffer.size() + lightBuffer.size() + magnetoBuffer.size()) +
                                " [" + gyroBuffer.size() + "/" + accelBuffer.size() + "/" + magnetoBuffer.size() + "/" + lightBuffer.size() + "]");


        /* Handling Multiple Sensors */

        switch(sensorType) {

                                        /* Calibrated */

            case Sensor.TYPE_GYROSCOPE:
                mTextGyroSensor.setText("Calibrated Gryo (rad/s):\n" + threeAxisData);

                if(isRecordingAll)
                    recordThreeAxisDataCalibrated(threeAxisRecordingDataFormat, gyroBuffer);
                else if(isRecordingGyro)
                    recordThreeAxisDataCalibrated(threeAxisRecordingDataFormat);
                break;

            case Sensor.TYPE_ACCELEROMETER:
                mTextAccelSensor.setText("Calibrated Accel (m/s)^2:\n" + threeAxisData);

                if(isRecordingAll)
                    recordThreeAxisDataCalibrated(threeAxisRecordingDataFormat, accelBuffer);
                if(isRecordingAccel)
                    recordThreeAxisDataCalibrated(threeAxisRecordingDataFormat);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                mTextMagneto.setText("Magneto Calibrated (μT):\n" + threeAxisData);
                if(isRecordingAll)
                    recordThreeAxisDataCalibrated(threeAxisRecordingDataFormat, magnetoBuffer);
                else if(isRecordingMagneto)
                    recordThreeAxisDataCalibrated(threeAxisRecordingDataFormat);
                break;

                                        /* Uncalibrated */

            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                mTextGyroSensorUncali.setText("Uncalibrated Gyro (rad/s):\n" + threeAxisData);
                if(isRecordingAll)
                    recordThreeAxisDataUncalibrated(threeAxisRecordingDataFormat, gyroBuffer);
                else if(isRecordingGyro)
                    recordThreeAxisDataUncalibrated(threeAxisRecordingDataFormat);
                break;

            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                mTextAccelSensorUncali.setText("Uncalibrated Accel (m/s)^2:\n" + threeAxisData);
                if(isRecordingAll)
                    recordThreeAxisDataUncalibrated(threeAxisRecordingDataFormat, accelBuffer);
                else if(isRecordingAccel)
                    recordThreeAxisDataUncalibrated(threeAxisRecordingDataFormat);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                mTextMagnetoUncali.setText("Magneto Uncalibrated (μT):\n" + threeAxisData);
                if(isRecordingAll)
                    recordThreeAxisDataUncalibrated(threeAxisRecordingDataFormat, magnetoBuffer);
                else if(isRecordingMagneto)
                    recordThreeAxisDataUncalibrated(threeAxisRecordingDataFormat);
                break;

                                        /* Misc. */

            case Sensor.TYPE_LIGHT:
                mTextAmbientLight.setText("Light Sensor (lx):\n" + value1);
                if(isRecordingAll)
                    recordOneAxisData(oneAxisRecordingDataFormat, lightBuffer);
                else if(isRecordingLight)
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
    public void recordThreeAxisDataCalibrated(String data, ArrayList<String> currentBuffer) {

        if(isInCaliRecordingMode)
            if(currentBuffer.size() <= dataCollectionLimit)
                currentBuffer.add(data);
            else if(currentBuffer.size() >= dataCollectionLimit & areAllBuffersAtLimit()) {
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
    public void recordThreeAxisDataUncalibrated(String data, ArrayList<String> currentBuffer) {
        if(isInUncaliRecordingMode)
            if(currentBuffer.size() <= dataCollectionLimit)
                currentBuffer.add(data);
            else if(currentBuffer.size() >= dataCollectionLimit & areAllBuffersAtLimit()) {
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
        else if(isInUncaliRecordingMode)
            if(outputBuffer.size() <= dataCollectionLimit)
                outputBuffer.add(data);
            else if(outputBuffer.size() >= dataCollectionLimit) {
                isInUncaliRecordingMode = false;
            }
    }
    public void recordOneAxisData(String data, ArrayList<String> currentBuffer) {
            if(isInCaliRecordingMode)
                if(currentBuffer.size() <= dataCollectionLimit)
                    currentBuffer.add(data);
                else if(currentBuffer.size() >= dataCollectionLimit & areAllBuffersAtLimit()) {
                    isInCaliRecordingMode = false;
                }
            else if(isInUncaliRecordingMode)
                if(currentBuffer.size() <= dataCollectionLimit)
                    currentBuffer.add(data);
                else if(currentBuffer.size() >= dataCollectionLimit & areAllBuffersAtLimit()) {
                    isInUncaliRecordingMode = false;
                }

        }

    public boolean areAllBuffersAtLimit() { //Application is a bit redundant, but still wanted.

        if(gyroBuffer.size() >= dataCollectionLimit)
            if(accelBuffer.size() >= dataCollectionLimit)
                if(magnetoBuffer.size() >= dataCollectionLimit)
                    if(lightBuffer.size() >= dataCollectionLimit | (lightBuffer.size() == 1 | accelBuffer.size() == 1))
                        return true;
        return false;

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
