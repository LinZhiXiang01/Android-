package activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import record.AudioRecorder;

import java.text.SimpleDateFormat;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class RecordSoundActivity extends Activity implements View.OnClickListener {

    //录音以及存文件所需权限
    private static final String[] PERMISSIONS = {
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_MEDIA_AUDIO,// API 33后需要
        Manifest.permission.READ_MEDIA_IMAGES,// API 33后需要
        Manifest.permission.READ_MEDIA_VIDEO,// API 33后需要

        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,

        Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
        Manifest.permission.MANAGE_EXTERNAL_STORAGE
    };

    private static final int REQUEST_ALL_CODE = 200;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 201;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE =202;
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE =203;
    private static final int  REQUEST_CODE_MOUNT_UNMOUNT_FILESYSTEMS = 204;
    private static final int  REQUEST_CODE_MANAGE_EXTERNAL_STORAGE = 205;
    Button start;
    Button pause;
    Button pcmList;
    Button wavList;

    AudioRecorder audioRecorder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_sound);

        if(!hasPermissions()){
            ActivityCompat.requestPermissions(this, PERMISSIONS,REQUEST_ALL_CODE);

            //特殊权限Setting页面弹出
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + this.getPackageName()));
            startActivityForResult(intent, REQUEST_CODE_MANAGE_EXTERNAL_STORAGE);

        }

        init();
        addListener();
    }

    private boolean hasPermissions(){
        for(String permission : PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this,permission)!=PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    //输出权限获取结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_MANAGE_EXTERNAL_STORAGE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "存储权限获取成功！", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), "存储权限获取失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void addListener() {
        start.setOnClickListener(this);
        pause.setOnClickListener(this);
        pcmList.setOnClickListener(this);
        wavList.setOnClickListener(this);
    }

    private void init() {
        start = (Button) findViewById(R.id.start);
        pause = (Button) findViewById(R.id.pause);
        pcmList = (Button) findViewById(R.id.pcmList);
        wavList = (Button) findViewById(R.id.wavList);
        pause.setVisibility(View.GONE);
        audioRecorder = AudioRecorder.getInstance();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.start) {
            try {
                if (audioRecorder.getStatus() == AudioRecorder.Status.STATUS_NO_READY) {
                    //初始化录音
                    String fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                    audioRecorder.createDefaultAudio(fileName);
                    audioRecorder.startRecord(null);

                    start.setText("停止录音");

                    pause.setVisibility(View.VISIBLE);

                } else {
                    //停止录音
                    audioRecorder.stopRecord(this);
                    start.setText("开始录音");
                    pause.setText("暂停录音");
                    pause.setVisibility(View.GONE);
                }

            } catch (IllegalStateException e) {
                Toast.makeText(RecordSoundActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        } else if (v.getId() == R.id.pause) {
            try {
                if (audioRecorder.getStatus() == AudioRecorder.Status.STATUS_START) {
                    //暂停录音
                    audioRecorder.pauseRecord();
                    pause.setText("继续录音");


                } else {
                    audioRecorder.startRecord(null);
                    pause.setText("暂停录音");
                }
            } catch (IllegalStateException e) {
                Toast.makeText(RecordSoundActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        } else if (v.getId() == R.id.pcmList) {
            Intent showPcmList = new Intent(RecordSoundActivity.this, ListActivity.class);
            showPcmList.putExtra("type", "pcm");
            startActivity(showPcmList);


        } else if (v.getId() == R.id.wavList) {
            Intent showWavList = new Intent(RecordSoundActivity.this, ListActivity.class);
            showWavList.putExtra("type", "wav");
            startActivity(showWavList);

        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        if (audioRecorder.getStatus() == AudioRecorder.Status.STATUS_START) {
            audioRecorder.pauseRecord();
            pause.setText("继续录音");
        }

    }

    @Override
    protected void onDestroy() {
        audioRecorder.release(this);
        super.onDestroy();

    }
}
