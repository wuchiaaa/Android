/*
* 暫時取消使用此activity
* 因:取消利用activity顯示鬧鐘響鈴的dialog，改用notification顯示鬧鐘響鈴
* */
package com.example.myalarmclock;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class ClockActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private String vibrator_value, clockName, ringUri;
    private AlarmReceiver alarmReceiver;
    private String TAG = "ClockActivity";
    private int repeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        Log.d("ClockActivity", "鬧鐘註冊成功");

        //[方法二]
        alarmReceiver = new AlarmReceiver();
//        mediaPlayer = MediaPlayer.create(this, R.raw.music);//鈴聲不可被使用者選取

        Intent intent = this.getIntent();
        //鬧鐘dialog內容:標籤名稱、震動、鈴聲
        ringUri = intent.getStringExtra("ringUri");
        clockName = intent.getStringExtra("clockName");
        vibrator_value = intent.getStringExtra("vibrator_value");

        //12.29 鬧鈴設定重複
        String strRepeat = intent.getStringExtra("repeat");
        repeat = Integer.parseInt(strRepeat);

        //12.24 篩選clockId，使鬧鐘響鈴過後顯示停用
        String str_clockId = intent.getStringExtra("clockId");
        assert str_clockId != null; //斷言??
        int clockId = Integer.parseInt(str_clockId);
        if(repeat == 0) {
            AlarmDB.updateEnableClose(ClockActivity.this, clockId);
            System.out.println("ClockActivity======鬧鐘已響鈴完畢，設為停用");
        }else if(repeat == 1){
            ArrayList<Integer> listWeek= intent.getIntegerArrayListExtra("listWeek");//如果repeat==1,查找響鈴後需要設定的下一個鬧鐘時間週期為何?
            System.out.println("ClockActivity======鬧鐘已響鈴完畢，因為重複鬧鈴故保持啟用,接收到的 listWeek="+listWeek);

            System.out.println("ClockActivity======鬧鐘已響鈴完畢，因為重複鬧鈴故保持啟用");

            intent = new Intent(ClockActivity.this, AlarmReceiver.class);//創建intent設置要啟動的組件
            intent.putExtra("vibrator_value", vibrator_value);//傳值給broadcast
            intent.putExtra("clockName", clockName);
            intent.putExtra("ringUri", ringUri);
            //12.24 篩選clockId，使鬧鐘響鈴過後>>>仍顯示啟用
            intent.putExtra("clockId", String.valueOf(clockId));//intent只能傳一種型別，故轉成str
            //12.29 鬧鐘設定重複
            intent.putExtra("repeat", strRepeat);

            //取得響鈴當下的時間
            Calendar today = Calendar.getInstance();//calendar實例化，取得預設時間、預設時區
            today.setTimeInMillis(System.currentTimeMillis());//系統目前時間
            long triggerAtMillis = today.getTimeInMillis();//獲得設定時間;
            @SuppressLint("SimpleDateFormat") DateFormat dateFo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Log.d(TAG, "響鈴當下的時間(毫秒):"+ dateFo.format(today.getTime()));

            //==============================
            int nowDayOfWeek = today.get(Calendar.DAY_OF_WEEK);
            int index = -1;
            int selectDayOfWeek = nowDayOfWeek;
            while (index < 0) {
                selectDayOfWeek += 1;//查找星期是否有在list裡面,有則代表設定該星期響鈴
                index = listWeek.indexOf(selectDayOfWeek); //nowDayOfWeek=4
                if (selectDayOfWeek == 8) {
                    selectDayOfWeek = 0;
                }
            }
            System.out.println("ClockActivity, 尋找下一個需啟用的鬧鐘週期:" + selectDayOfWeek + ",index:" + index);
            int diffDayOfWeek = selectDayOfWeek - nowDayOfWeek;
            if (diffDayOfWeek <= 0) diffDayOfWeek += 7;//恆正數
            triggerAtMillis = triggerAtMillis + diffDayOfWeek * 24 * 60 * 60 * 1000;//註冊下周同一時間的鬧鐘
            today.setTimeInMillis(triggerAtMillis);
            Log.d(TAG, "下次響鈴當下的時間:" + dateFo.format(today.getTime()));
            //==============================

            //註冊下一個broadcast
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);//獲取AlarmManager
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, clockId, intent, PendingIntent.FLAG_UPDATE_CURRENT);//設定pendingIntent接受自訂鬧鈴廣播 //FLAG_UPDATE_CURRENT: 不存在時就建立,建立好了以後就一直用它,每次使用時都會更新pi的資料
            if (Build.VERSION.SDK_INT < 19) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);//設置單次鬧鐘的類型,啟動時間,PendingIntent對象
            }


        }


        //震動效果
        if(vibrator_value.equals("ON")){
            vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(new long[]{100, 1000, 1000, 1000}, 0);//停0.1秒 震1秒 停1秒 震1秒。第二組參數:帶入-1是不重複
        }
        //播放選取的鈴聲,靜音為null
        mediaPlayer = new MediaPlayer();
        Uri soundUri = Uri.parse(ringUri);
        if(soundUri != null) {
            try{
                mediaPlayer = MediaPlayer.create(this, soundUri);
                mediaPlayer.setLooping(true);//循環播放鈴聲
                mediaPlayer.start();
            }catch(NullPointerException n){
                Log.e(TAG, "RuntimeException");
            }
        }
        //創建一個鬧鐘提醒的對話框,點擊確定關閉鈴聲與頁面
        new AlertDialog.Builder(ClockActivity.this).setTitle("鬧鐘").setMessage(clockName)
                .setPositiveButton("關閉鬧鈴", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//按鈕點選的監聽程式
                        if(vibrator_value.equals("ON")) vibrator.cancel();
                        try{
                            mediaPlayer.stop();
                        }catch(NullPointerException n){
                            Log.e(TAG, "RuntimeException");
                        }
                        ClockActivity.this.finish();
                    }
                }).show();


        //------------notification寫的地方>不適用----------------------------------------------------
/*

        mediaPlayer = MediaPlayer.create(this, R.raw.music);//鈴聲不可被使用者選取
        mediaPlayer.start();
        clockName = "小豬起床囉~";

        //由於每一個Notification都必須對使用者的點擊做出回應，為了達到此要求，所以需要建立PendingIntent
        //建立關閉通知的動作 Create an Intent for the BroadcastReceiver
        Intent closeIntent = new Intent(this, NotificationReceiver.class);
//        closeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent dismissIntent = PendingIntent.getBroadcast(this, 0, closeIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent dismissIntent = NotificationActivity.getDismissIntent(1, this);
        //建立Dismiss
//        NotificationCompat.Action dismissAction = new NotificationCompat.Action.Builder(R.drawable.ic_alarm_black, "關閉鬧鐘", dismissIntent).build();


        //建立Notification.Builder
        //uri尚未建立!!!
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "clockId");
        mBuilder.setSmallIcon(R.drawable.ic_alarm_black); //設置小圖標
        mBuilder.setContentTitle("鬧鐘");                 //標題
        mBuilder.setContentText(clockName);              //內容
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
//        mBuilder.setContentIntent(dismissIntent);//每個通知都應該對點按操作做出回應，通常是在應用中打開對應於該通知的Activity(PendingIntent的第三個參數改成new Intent()，即不開啟任何Activiy)
//        mBuilder.setAutoCancel(true);//用戶點按通知後，自動移除通知
        mBuilder.addAction(R.drawable.ic_alarm_black, "關閉鬧鐘", dismissIntent);//添加按鈕操作>關閉通知
//        mBuilder.addAction(dismissAction);//添加按鈕操作>關閉通知
//        mBuilder.setCategory(NotificationCompat.CATEGORY_ALARM);

        NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //建立通知頻道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  //NotificationChannel 只存在於8.0以上的系統，為了顯示通知，必須要註冊通道
            NotificationChannel  mChannel = new NotificationChannel("clockId", "Channel Clock", NotificationManager.IMPORTANCE_HIGH);
            if(mManager != null) {
                mManager.createNotificationChannel(mChannel);
                mBuilder.setChannelId("clockId");
//                mChannel.enableLights(true);//是否在桌面ICON右上角展示小紅點
//                mChannel.enableVibration(true);
//                AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                        .setUsage(AudioAttributes.USAGE_ALARM)
//                        .build();
//                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);//預設的鈴聲
                System.out.println("AlarmReceiver, mChannel被呼叫了...");
            }
        }
        if(mManager != null) {
            mManager.notify(1, mBuilder.build());
        }
*/
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() { //比如停止訪問數據庫
        super.onDestroy();
    }



}

