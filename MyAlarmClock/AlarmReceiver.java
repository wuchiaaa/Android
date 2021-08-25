package com.example.myalarmclock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getName();
    private ArrayList<Integer> listWeek;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 接收到事件時要做的事
        Log.d(TAG, "廣播註冊成功");

        //取得響鈴當下的時間
        Calendar today = Calendar.getInstance();//calendar實例化，取得預設時間
        today.setTimeInMillis(System.currentTimeMillis());//系統目前時間
        long triggerAtMillis = today.getTimeInMillis();//獲得設定時間

        //接受廣播發來的數據
        String vibrator_value = intent.getStringExtra("vibrator_value");
        String clockName = intent.getStringExtra("clockName");
        String ringUri = intent.getStringExtra("ringUri");
        String strClockId = intent.getStringExtra("clockId");  //12.24 篩選clockId，使鬧鐘響鈴過後顯示停用
        int clockId = Integer.parseInt(strClockId);
        System.out.println("AlarmReceiver_鬧鐘註冊的id: " + clockId);
        int repeat;
        try {   //12.29 鬧鈴設定重複
            String strRepeat = intent.getStringExtra("repeat");
            repeat = Integer.parseInt(strRepeat);
            if (repeat == 1) {
                listWeek = intent.getIntegerArrayListExtra("listWeek");
                System.out.println("AlarmReceiver======鬧鐘已響鈴完畢，因為重複鬧鈴故保持啟用。接收到的 listWeek="+listWeek);
            }else if(repeat == 0){
                AlarmDB.updateEnableClose(context, clockId);
                System.out.println("AlarmReceiver======鬧鐘已響鈴完畢，設為停用");
            }
        } catch (NumberFormatException n) {
            repeat = 0;
            AlarmDB.updateEnableClose(context, clockId);
            System.out.println("此為版本更新前已設定的單次鬧鐘");
        }

        /*
         * 取消利用activity顯示鬧鐘響鈴的dialog，改用notification顯示鬧鐘響鈴
         * */
//        //發送數據給Activity
//        intent.putExtra("vibrator_value", vibrator_value);
//        intent.putExtra("clockName", clockName);
//        intent.putExtra("ringUri", ringUri);
//        //12.24 篩選clockId，使鬧鐘響鈴過後顯示停用
//        intent.putExtra("alarmList", clockId);
//        //12.29 鬧鈴設定重複
//        intent.putExtra("repeat", repeat);
//        if(repeat.equals("1")) {
//            intent.putIntegerArrayListExtra("listWeek", listWeek);
//        }
//
//        System.out.println("AlarmReceiver======鬧鐘響鈴put");
//
//        intent.setClass(context, ClockActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//如果不加這個flag就沒有一個Task來存放新啟動的Activity。啟動一個非本應用的Activity，需要設置新的任務棧
//        context.startActivity(intent);
        System.out.println();
//---------------------------以下結果: 廣播響鈴50秒--------------------------------------

        Uri soundUri = Uri.parse(ringUri);
        String strSoundUri = String.valueOf(soundUri.hashCode());
        String strVibrator = vibrator_value.equals("OFF") ? "0" : "1";
        String CHANNEL_ID = strClockId + strSoundUri + strVibrator;//頻道id需唯一(組合方式: alarmManagerID + 鈴聲hashCode + 震動)
        System.out.println("通知頻道的唯一值：channelId = " + CHANNEL_ID );

        //由於每一個Notification都必須對使用者的點擊做出回應，為了達到此要求，所以需要建立PendingIntent
        //建立關閉通知的動作 Create an Intent for the BroadcastReceiver
        PendingIntent dismissIntent = NotificationActivity.getDismissIntent(clockId, context, triggerAtMillis, repeat, listWeek); //notificationId=clockId

        //獲得NotificationManager對象
        NotificationManager mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //建立通知頻道-->NotificationChannel 只存在於8.0以上的系統，為了顯示通知，必須要註冊通道
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "Channel Clock", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setSound(soundUri, null);//設置鈴聲
            if (strVibrator.equals("0")) {
                mChannel.enableVibration(false);
                System.out.println("鬧鐘未震動");
            }
//            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);//鎖屏顯示通知>>undone
            mManager.createNotificationChannel(mChannel);
            System.out.println("AlarmReceiver, mChannel被呼叫了...");
        }

        //建立Notification.Builder
        Notification.Builder mBuilder = new Notification.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_alarm_black); //設置小圖標
        mBuilder.setContentTitle("鬧鐘");                 //標題
        mBuilder.setContentText(clockName);              //內容
        mBuilder.setFullScreenIntent(dismissIntent, true);//懸浮視窗持續顯示
        mBuilder.addAction(R.drawable.ic_alarm_black, "關閉鬧鐘", dismissIntent);//添加按鈕操作>關閉通知

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId(CHANNEL_ID);
        }

        Notification notify = mBuilder.build();//對Notification進行賦值
        notify.flags |= Notification.FLAG_INSISTENT;//循環播放音效或震動
        mManager.notify(clockId, notify);//發送通知

    }
}
