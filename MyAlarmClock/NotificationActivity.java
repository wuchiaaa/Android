package com.example.myalarmclock;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class NotificationActivity extends AppCompatActivity{

    public static final String NOTIFICATION_ID ="NOTIFICATION_ID";
    private String TAG = "NotificationActivity";
    private long triggerAtMillis;
    private int clockId, repeat;
    private ArrayList<Integer> listWeek = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(getIntent().getIntExtra(NOTIFICATION_ID, -1));
//        manager.deleteNotificationChannel("1583157875");  //系統裡除了給這個通道打個 “deleted” 的標籤外，啥也沒幹!!只有清除應用資料或者卸載應用，才能徹底刪除已經創建註冊的Channel

        Intent intent = this.getIntent();
        clockId = intent.getIntExtra(NOTIFICATION_ID, clockId);
        triggerAtMillis = intent.getLongExtra("triggerAtMillis", triggerAtMillis);
        repeat = intent.getIntExtra("repeat", repeat);
        listWeek = intent.getIntegerArrayListExtra("listWeek");
        if(repeat == 1) {
            registerWeek(triggerAtMillis, clockId);
        }
        finish(); // since finish() is called in onCreate(), onDestroy() will be called immediately

    }

    public static PendingIntent getDismissIntent(int notificationId, Context context, long triggerAtMillis, int repeat, ArrayList<Integer> listWeek) { //notificationId=clockId
        Intent intent = new Intent(context, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NOTIFICATION_ID, notificationId);
        intent.putExtra("triggerAtMillis", triggerAtMillis);
        intent.putExtra("repeat", repeat);
        intent.putIntegerArrayListExtra("listWeek", listWeek);
        PendingIntent dismissIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        return dismissIntent;
    }


    //設定週期性鬧鐘，註冊下次響鈴的alarmManager
    public void registerWeek(long triggerAtMillis, int clockId){
        System.out.println("NotificationActivity, registerWeek()方法被呼叫");
        Calendar today = Calendar.getInstance();//calendar實例化，取得預設時間
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
        System.out.println("NotificationActivity, 尋找下一個需啟用的鬧鐘週期:" + selectDayOfWeek + ",index:" + index);
        int diffDayOfWeek = selectDayOfWeek - nowDayOfWeek;
        if (diffDayOfWeek <= 0) diffDayOfWeek += 7;//恆正數
        triggerAtMillis = triggerAtMillis + diffDayOfWeek * 24 * 60 * 60 * 1000;//註冊下次(週期性)同一時間的鬧鐘
        today.setTimeInMillis(triggerAtMillis);
        @SuppressLint("SimpleDateFormat") DateFormat dateFo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d(TAG, "NotificationActivity, 下次響鈴的時間:" + dateFo.format(today.getTime()));

        //註冊下一個broadcast
        Intent intent = new Intent(NotificationActivity.this, AlarmReceiver.class);//創建intent設置要啟動的組件
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);//獲取AlarmManager
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, clockId, intent, PendingIntent.FLAG_UPDATE_CURRENT);//設定pendingIntent接受自訂鬧鈴廣播 //FLAG_UPDATE_CURRENT: 不存在時就建立,建立好了以後就一直用它,每次使用時都會更新pi的資料
        if (Build.VERSION.SDK_INT < 19) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);//設置單次鬧鐘的類型,啟動時間,PendingIntent對象
        }
    }
}
