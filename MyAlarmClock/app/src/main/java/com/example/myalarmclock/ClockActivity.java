package com.example.myalarmclock;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ClockActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private String vibrator_value;//判斷鈴聲與震動是否開啟的值

//    private SetClockFragment setClockFragment;
//    @Override
//    public void setData(String data) {//實現接口???
//        vibrator_value = data;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);


//        setClockFragment = new SetClockFragment();
//        FragmentManager manager = getFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.add(R.id.layout_container_fragment, new SetClockFragment());
//        transaction.commit();


        //獲取從Fragment中傳過來的值
        Intent intent = getIntent();
        vibrator_value = intent.getStringExtra("vibrator_value");
        if (vibrator_value == null || vibrator_value.equals("ON")) {
            vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(new long[]{100, 1000, 1000, 1000}, 0);//停0.1秒 震1秒 停1秒 震1秒。第二組參數:帶入-1是不重複
//        }


            mediaPlayer = MediaPlayer.create(this, R.raw.music);
            mediaPlayer.start();

            Toast.makeText(this, vibrator_value + "<--這是ON還是OFF", Toast.LENGTH_LONG).show();//Toast提示內容

            //創建一個鬧鐘提醒的對話框,點擊確定關閉鈴聲與頁面
            new AlertDialog.Builder(ClockActivity.this).setTitle("鬧鐘").setMessage("快起床~")
                    .setPositiveButton("關閉鬧鈴", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//按鈕點選的監聽程式
                            mediaPlayer.stop();
                            if (vibrator_value == null || vibrator_value.equals("ON")) {
                                vibrator.cancel();
                            }


                            ClockActivity.this.finish();
                        }
                    }).show();
        }
    }



}