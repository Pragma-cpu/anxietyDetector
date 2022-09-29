package com.huawei.audiodevicekit.bluetoothsample.view;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.huawei.audiobluetooth.api.Cmd;
import com.huawei.audiobluetooth.api.data.SensorData;
import com.huawei.audiobluetooth.layer.protocol.mbb.DeviceInfo;
import com.huawei.audiobluetooth.utils.DateUtils;
import com.huawei.audiobluetooth.utils.LocaleUtils;
import com.huawei.audiobluetooth.utils.LogUtils;
import com.huawei.audiodevicekit.R;
import com.huawei.audiodevicekit.bluetoothsample.contract.SampleBtContract;
import com.huawei.audiodevicekit.bluetoothsample.presenter.SampleBtPresenter;
import com.huawei.audiodevicekit.bluetoothsample.view.adapter.SingleChoiceAdapter;
import com.huawei.audiodevicekit.mvp.view.support.BaseAppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SampleBtActivity
    extends BaseAppCompatActivity<SampleBtContract.Presenter, SampleBtContract.View>
    implements SampleBtContract.View {
    private static final String TAG = "SampleBtActivity";
    private static final String data = "SensorData{serviceId=43, commandId=48, sensorType=22, sensorTypes=[ACC, GYRO, PROX], time=97061, packageNumber=10, volt=4210, chargeCurrent=0, battPercent=100, temperature=29, hallData=0, earSide=1, roleState=2, wearDetect=1, knockDetect=0, knockScore=0, movementDetect=0, touchEvent=[0, 0, 0, 0], earSideDetect=0, accelDataLen=20, accelData=[Acc{x=-3434, y=2529, z=-353}, Acc{x=-3330, y=2502, z=-310}, Acc{x=-3238, y=2480, z=-305}, Acc{x=-3168, y=2478, z=-328}, Acc{x=-3139, y=2480, z=-349}, Acc{x=-3128, y=2495, z=-360}, Acc{x=-3125, y=2511, z=-353}, Acc{x=-3120, y=2536, z=-324}, Acc{x=-3082, y=2560, z=-288}, Acc{x=-3034, y=2582, z=-263}, Acc{x=-2979, y=2597, z=-232}, Acc{x=-2925, y=2614, z=-224}, Acc{x=-2875, y=2626, z=-227}, Acc{x=-2829, y=2645, z=-217}, Acc{x=-2799, y=2667, z=-219}, Acc{x=-2769, y=2691, z=-214}, Acc{x=-2773, y=2719, z=-202}, Acc{x=-2790, y=2754, z=-190}, Acc{x=-2822, y=2794, z=-175}, Acc{x=-2866, y=2842, z=-159}, Acc{x=0, y=0, z=0}, Acc{x=0, y=0, z=0}, Acc{x=0, y=0, z=0}, Acc{x=0, y=0, z=0}, Acc{x=0, y=0, z=0}], accTimeStamp=97035000, gyroDataLen=20, gyroData=[Gyro{roll=-2763, pitch=8857, yaw=14732}, Gyro{roll=-3068, pitch=8979, yaw=14854}, Gyro{roll=-3373, pitch=8552, yaw=14427}, Gyro{roll=-3861, pitch=7576, yaw=12902}, Gyro{roll=-4410, pitch=6173, yaw=10950}, Gyro{roll=-4959, pitch=4343, yaw=8449}, Gyro{roll=-5325, pitch=2513, yaw=5643}, Gyro{roll=-5752, pitch=561, yaw=3081}, Gyro{roll=-5874, pitch=-1391, yaw=1068}, Gyro{roll=-5813, pitch=-3282, yaw=31}, Gyro{roll=-5874, pitch=-5173, yaw=-91}, Gyro{roll=-5935, pitch=-7430, yaw=580}, Gyro{roll=-5874, pitch=-9809, yaw=1861}, Gyro{roll=-5691, pitch=-12310, yaw=3630}, Gyro{roll=-5447, pitch=-14628, yaw=5704}, Gyro{roll=-4837, pitch=-16397, yaw=7839}, Gyro{roll=-4227, pitch=-17678, yaw=9669}, Gyro{roll=-3434, pitch=-18349, yaw=10584}, Gyro{roll=-2702, pitch=-18532, yaw=10767}, Gyro{roll=-2275, pitch=-18410, yaw=10706}, Gyro{roll=0, pitch=0, yaw=0}, Gyro{roll=0, pitch=0, yaw=0}, Gyro{roll=0, pitch=0, yaw=0}, Gyro{roll=0, pitch=0, yaw=0}, Gyro{roll=0, pitch=0, yaw=0}], capSensorDataLen=6, capSensorData=[36855, 1, 379, 0, 566, 0], capRawData=[-6589, 116673, -4524, 88532, -1119, 78428]}";
    private TextView tvDevice;

    private TextView tvStatus;

    private ListView listView;

    private TextView tvSendCmdResult;

    private Button btnSearch;

    private Button btnConnect;

    private Button btnRec;

    private Button btnDisconnect;

    private Spinner spinner;

    private Button btnSendCmd;

    private RecyclerView rvFoundDevice;

    private SingleChoiceAdapter mAdapter;

    private Cmd mATCmd = Cmd.VERSION;

    private String mMac;

    private List<Map<String, String>> maps;

    private SimpleAdapter simpleAdapter;

    private TextView tvDataCount;

    private Context context;

    private File file;
    private File fileaudio;

    private MediaRecorder mediaRecorder;
    public SimpleDateFormat formatter;
    public TextView formatted;

    AudioManager am;
    int ran;
    int flag=0;
    public Date date;

    String url="https://mobilehci-app.herokuapp.com/predict";
    String anxiety;
    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public SampleBtContract.Presenter createPresenter() {
        return new SampleBtPresenter();
    }

    @Override
    public SampleBtContract.View getUiImplement() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        date = new Date();
        ran=(int)Math.random() * (10000 - 1 + 1) + 1;
        btnRec=findViewById(R.id.btn_record);
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                Log.d(TAG, "Audio SCO state: " + state);
                if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
                    btnRec.setOnClickListener(v -> {
                        if(flag==0) {
                            startrecording();
                            flag = 1;
                            btnRec.setText("STOP AUDIO");
                        }else{

                            stoprecording();
                            flag = 0;
                            btnRec.setText("START AUDIO");

                        }}
                    );
                    unregisterReceiver(this);
                }

            }
        }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED));

        Log.d(TAG, "starting bluetooth");
        am.startBluetoothSco();
    }





    @Override
    protected int getResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        tvDevice = findViewById(R.id.tv_device);
        tvStatus = findViewById(R.id.tv_status);
        tvDataCount = findViewById(R.id.tv_data_count);
        listView = findViewById(R.id.listview);
        tvSendCmdResult = findViewById(R.id.tv_send_cmd_result);
        btnSearch = findViewById(R.id.btn_search);
        btnConnect = findViewById(R.id.btn_connect);
        btnDisconnect = findViewById(R.id.btn_disconnect);
        spinner = findViewById(R.id.spinner);
        btnSendCmd = findViewById(R.id.btn_send_cmd);
        rvFoundDevice = findViewById(R.id.found_device);
        formatted=findViewById(R.id.formatted);
        initSpinner();
        initRecyclerView();
        maps = new ArrayList<>();


        simpleAdapter = new SimpleAdapter(this, maps, android.R.layout.simple_list_item_1,
            new String[] {"data"}, new int[] {android.R.id.text1});
        listView.setAdapter(simpleAdapter);
    }

    private void initSpinner() {
        List<Map<String, String>> data = new ArrayList<>();
        for (Cmd cmd : Cmd.values()) {
            if (cmd.isEnable()) {
                HashMap<String, String> map = new HashMap<>();
                Boolean isChinese = LocaleUtils.isChinese(this);
                String name = isChinese ? cmd.getNameCN() : cmd.getName();
                map.put("title", cmd.getType() + "-" + name);
                data.add(map);
            }
        }
        spinner.setAdapter(
            new SimpleAdapter(this, data, R.layout.item_spinner, new String[] {"title"},
                new int[] {R.id.tv_name}));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.i(TAG, "onItemSelected position = " + position);
                String title = data.get(position).get("title");
                String type = Objects.requireNonNull(title).split("-")[0];
                try {
                    int typeValue = Integer.parseInt(type);
                    mATCmd = Cmd.getATCmdByType(typeValue);
                } catch (NumberFormatException e) {
                    LogUtils.e(TAG, "parseInt fail e = " + e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                LogUtils.i(TAG, "onNothingSelected parent = " + parent);
            }
        });
    }

    private void initRecyclerView() {
        SingleChoiceAdapter.SaveOptionListener mOptionListener = new SingleChoiceAdapter.SaveOptionListener() {
            @Override
            public void saveOption(String optionText, int pos) {
                LogUtils.i(TAG, "saveOption optionText = " + optionText + ",pos = " + pos);
                mMac = optionText.substring(1, 18);
                boolean connected = getPresenter().isConnected(mMac);
                if (connected) {
                    getPresenter().disConnect(mMac);
                } else {
                    getPresenter().connect(mMac);
                }
            }

            @Override
            public void longClickOption(String optionText, int pos) {
                LogUtils.i(TAG, "longClickOption optionText = " + optionText + ",pos = " + pos);
            }
        };
        mAdapter = new SingleChoiceAdapter(this, new ArrayList<>());
        mAdapter.setSaveOptionListener(mOptionListener);
        rvFoundDevice.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvFoundDevice.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        getPresenter().initBluetooth(this);
    }

    @Override
    protected void setOnclick() {
        super.setOnclick();
        btnConnect.setOnClickListener(v -> getPresenter().connect(mMac));
        btnDisconnect.setOnClickListener(v -> getPresenter().disConnect(mMac));
        btnSendCmd.setOnClickListener(v -> {
            Log.d("welcome","test");
            getFileTxt("This is demo","sensor"+(date.getTime())+ran+".txt");
//            for(int  lj=0;lj<3;lj++) {
//                formatted.setText(dataformatter(data));
//                formatted.invalidate();
//            }
            float dataformatted[]=dataformatter(data);
//            for(int i=0;i<dataformatted.length;i++)
//                Log.d("dataform",dataformatted[i]+"");
            // API CALL STARTS HERE
            StringRequest STRINGREQ = new StringRequest(Request.Method.POST, url,  new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Log.d("expression=", "here we are");
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        anxiety = jsonObject.getString("anxiety:");
                        Log.d("anxiety=", anxiety + "");
                        if(Float.parseFloat(anxiety)==0.0){
                            formatted.post(new Runnable() {
                              public void run() {
                                  formatted.setText("Not anxious");
                              }

                         });}
                        else {
                            formatted.post(new Runnable() {
                                public void run() {
                                    formatted.setText("Anxious");
                                }

                            });}

//                        formatted.invalidate();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.d("error",  " Error: " + new String(error.networkResponse.data));

                }
            }) {
                @Override
                public Map<String,String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    try {
                        params.put("temperature", (int) (dataformatted[0]) + "");
                        params.put("capSensorDatar1", (int) (dataformatted[1]) + "");
                        params.put("capSensorDatar2", (int) (dataformatted[2]) + "");
                        params.put("capSensorDatar3", (int) (dataformatted[3]) + "");
                        params.put("capSensorDatar4", (int) (dataformatted[4]) + "");
                        params.put("capSensorDatar5", (int) (dataformatted[5]) + "");
                        params.put("capSensorDatar6", (int) (dataformatted[6]) + "");
                        params.put("capRawDatar1", (int) (dataformatted[7]) + "");
                        params.put("capRawDatar2", (int) (dataformatted[8]) + "");
                        params.put("capRawDatar3", (int) (dataformatted[9]) + "");
                        params.put("capRawDatar4", (int) (dataformatted[10]) + "");
                        params.put("capRawDatar5", (int) (dataformatted[11]) + "");
                        params.put("capRawDatar6", (int) (dataformatted[12]) + "");
                        params.put("Accx_mean", dataformatted[13] + "");
                        params.put("Accy_mean", dataformatted[14] + "");
                        params.put("Accz_mean", dataformatted[15] + "");
                        params.put("Gyroyaw_mean", dataformatted[16] + "");
                        params.put("Gyroroll_mean", dataformatted[17] + "");
                        params.put("Gyropitch_mean", dataformatted[18] + "");


                    } catch (Exception e) {
                    }
                    //return params;
                    return params;
                }

            };
            STRINGREQ.setRetryPolicy(new DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue QUEUE = Volley.newRequestQueue(SampleBtActivity.this);
            QUEUE.add(STRINGREQ);
            getPresenter().sendCmd(mMac, mATCmd.getType());
        });
        btnSearch.setOnClickListener(v -> getPresenter().checkLocationPermission(this));


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getPresenter().processLocationPermissionsResult(requestCode, grantResults);
    }

    @Override
    public void onDeviceFound(DeviceInfo info) {
        if (mAdapter == null) {
            return;
        }
        runOnUiThread(() -> mAdapter
            .pushData(String.format("[%s] %s", info.getDeviceBtMac(), "HUAWEI Eyewear")));
    }

    @Override
    public void onStartSearch() {
        if (mAdapter != null) {
            runOnUiThread(() -> mAdapter.clearData());
        }
    }

    @Override
    public void onDeviceChanged(BluetoothDevice device) {
        if (tvDevice != null) {
            runOnUiThread(() -> tvDevice
                .setText(String.format("[%s] %s", device.getAddress(), "HUAWEI Eyewear")));
        }
    }

    @Override
    public void onConnectStateChanged(String stateInfo) {
        if (tvStatus != null) {
            runOnUiThread(() -> tvStatus.setText(stateInfo));
        }
    }

    @Override
    public void onSensorDataChanged(SensorData sensorData) {
        runOnUiThread(() -> {
            Map<String, String> map = new HashMap<>();
            map.put("data", sensorData.toString());
            maps.add(0, map);
            tvDataCount.setText(getString(R.string.sensor_data, maps.size()));
            simpleAdapter.notifyDataSetChanged();
            getFileTxt(sensorData.toString()+"\n","sensor"+(date.getTime())+ran+".txt");
            float dataformatted[]=dataformatter(sensorData.toString());
            // API CALL STARTS HERE
            StringRequest STRINGREQ = new StringRequest(Request.Method.POST, url,  new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        anxiety = jsonObject.getString("anxiety:");
                        Log.d("anxiety=", anxiety + "");
                        if(Float.parseFloat(anxiety)==0.0){
                            formatted.post(new Runnable() {
                                public void run() {
                                    formatted.setText("Not anxious");
                                }

                            });}
                        else {
                            formatted.post(new Runnable() {
                                public void run() {
                                    formatted.setText("Anxious");
                                }

                            });}

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.d("error",  " Error: " + new String(error.networkResponse.data));

                }
            }) {
                @Override
                public Map<String,String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    try {
                        params.put("temperature", (int) (dataformatted[0]) + "");
                        params.put("capSensorDatar1", (int) (dataformatted[1]) + "");
                        params.put("capSensorDatar2", (int) (dataformatted[2]) + "");
                        params.put("capSensorDatar3", (int) (dataformatted[3]) + "");
                        params.put("capSensorDatar4", (int) (dataformatted[4]) + "");
                        params.put("capSensorDatar5", (int) (dataformatted[5]) + "");
                        params.put("capSensorDatar6", (int) (dataformatted[6]) + "");
                        params.put("capRawDatar1", (int) (dataformatted[7]) + "");
                        params.put("capRawDatar2", (int) (dataformatted[8]) + "");
                        params.put("capRawDatar3", (int) (dataformatted[9]) + "");
                        params.put("capRawDatar4", (int) (dataformatted[10]) + "");
                        params.put("capRawDatar5", (int) (dataformatted[11]) + "");
                        params.put("capRawDatar6", (int) (dataformatted[12]) + "");
                        params.put("Accx_mean", dataformatted[13] + "");
                        params.put("Accy_mean", dataformatted[14] + "");
                        params.put("Accz_mean", dataformatted[15] + "");
                        params.put("Gyroyaw_mean", dataformatted[16] + "");
                        params.put("Gyroroll_mean", dataformatted[17] + "");
                        params.put("Gyropitch_mean", dataformatted[18] + "");


                    } catch (Exception e) {
                    }
                    return params;
                }
            };
            STRINGREQ.setRetryPolicy(new DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue QUEUE = Volley.newRequestQueue(SampleBtActivity.this);
            QUEUE.add(STRINGREQ);
        });
    }

    @Override
    public void onSendCmdSuccess(Object result) {
        runOnUiThread(() -> {
            String info = DateUtils.getCurrentDate() + "\n" + result.toString();
            tvSendCmdResult.setText(info);
        });
    }

    @Override
    public void onError(String errorMsg) {
        runOnUiThread(
            () -> Toast.makeText(SampleBtActivity.this, errorMsg, Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().deInit();
    }

    public void getFileTxt(String content,String filename)
    {

        try {
//            Log.d("welcome",Environment.getExternalStorageState()+" ok");
            Log.d("welcome",getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+" ok");
            File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            Log.d("welcome",path.toString()+"ok");
            file = new File(path, filename);
            FileWriter fw=null;
            BufferedWriter bw = null;
            Log.d("path",file.getAbsolutePath()+file.exists());
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file.getAbsolutePath(), true);
            bw= new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        }catch(Exception e) {
            Log.e("Write Errro", e.getMessage());

        }
    }
    private boolean checkPerm(){
        int first= ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        int second= ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return first== PackageManager.PERMISSION_GRANTED && second== PackageManager.PERMISSION_GRANTED;
    }
    public void startrecording(){
        if (checkPerm()==true){
//            Toast.makeText(this,"Recording started",Toast.LENGTH_SHORT);
            File pathaudio = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            fileaudio = new File(pathaudio, "recording"+(date.getTime())+ran+".mp3");
            mediaRecorder=new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setAudioEncodingBitRate(16);
            mediaRecorder.setAudioSamplingRate(44100);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mediaRecorder.setOutputFile(fileaudio);
            }
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }else{
            ActivityCompat.requestPermissions(this,new String[]{
            Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }
    public void stoprecording() {
        ran=(int)Math.random() * (10000 - 1 + 1) + 1;
//        mediaRecorder.stop();
        mediaRecorder.release();
//        Toast.makeText(this,"Recording stopped",Toast.LENGTH_SHORT);

    }
    public float[] dataformatter(String dat) {
        float x=0,y=0,z=0;
        float roll=0,pitch=0,yaw=0;
        int temperature,cd1,cd2,cd3,cd4,cd5,cd6,cdr1,cdr2,cdr3,cdr4,cdr5,cdr6;
        float data[]=new float[19];
//        String x="";
        String[] arrOfStr = dat.split("accelData=\\[");
        String temp[]=arrOfStr[1].split("\\], accTimeStamp");
        String acc0[]=temp[0].split("Acc\\{");
        for(int k=1;k<acc0.length;k++){
            String r1[]=(acc0[k]).split("\\{");

            x=x+Float.parseFloat((((acc0[k]).split(","))[0]).split("=")[1]);
            y=y+Float.parseFloat((((acc0[k]).split(","))[1]).split("=")[1]);
            z=z+Float.parseFloat(((((acc0[k]).split(","))[2]).split("=")[1]).split("\\}")[0]);
//            x=(((acc0[k]).split(","))[0]);

//            Log.d("xval",z+","+k+"");
        }
        x=x/(acc0.length-1);
        y=y/(acc0.length-1);
        z=z/(acc0.length-1);

        String[] arr1fStr = dat.split("gyroData=\\[");
        String temp1[]=arr1fStr[1].split("\\], capSensorDataLen");
        String acc1[]=temp1[0].split("Gyro\\{");
        for(int k=1;k<acc1.length;k++){
            String r2[]=(acc1[k]).split("\\{");

            roll=roll+Float.parseFloat((((acc1[k]).split(","))[0]).split("=")[1]);
            pitch=pitch+Float.parseFloat((((acc1[k]).split(","))[1]).split("=")[1]);
            yaw=yaw+Float.parseFloat(((((acc1[k]).split(","))[2]).split("=")[1]).split("\\}")[0]);
//            x=(((acc0[k]).split(","))[0]);

//            Log.d("xval",z+","+k+"");
        }
        roll=roll/(acc1.length-1);
        pitch=pitch/(acc1.length-1);
        yaw=yaw/(acc1.length-1);
        temperature=Integer.parseInt((dat.split("temperature=")[1]).split(", hallData")[0]);
        String captemp[]=((dat.split("capSensorData=\\[")[1]).split("\\],")[0]).split(", ");
        cd1=Integer.parseInt(captemp[0]);
        cd2=Integer.parseInt(captemp[1]);
        cd3=Integer.parseInt(captemp[2]);
        cd4=Integer.parseInt(captemp[3]);
        cd5=Integer.parseInt(captemp[4]);
        cd6=Integer.parseInt(captemp[5]);
        String captempr[]=((dat.split("capRawData=\\[")[1]).split("\\]\\}")[0]).split(", ");
        cdr1=Integer.parseInt(captempr[0]);
        cdr2=Integer.parseInt(captempr[1]);
        cdr3=Integer.parseInt(captempr[2]);
        cdr4=Integer.parseInt(captempr[3]);
        cdr5=Integer.parseInt(captempr[4]);
        cdr6=Integer.parseInt(captempr[5]);
//        Log.d("xval",captemp);


        data[0]=temperature;
        data[1]=cd1;
        data[2]=cd2;
        data[3]=cd3;
        data[4]=cd4;
        data[5]=cd5;
        data[6]=cd6;
        data[7]=cdr1;
        data[8]=cdr2;
        data[9]=cdr3;
        data[10]=cdr4;
        data[11]=cdr5;
        data[12]=cdr6;
        data[13]=x;
        data[14]=y;
        data[15]=z;
        data[16]=yaw;
        data[17]=roll;
        data[18]=pitch;


//        return(temperature+","+cd1+","+cd2+","+cd3+","+cd4+","+cd5+","+cd6+","+cdr1+","+cdr2+","+cdr3+","+cdr4+","+cdr5+","+cdr6+","+x+","+y+","+z+","+yaw+","+roll+","+pitch);
        return(data);
    }
}
