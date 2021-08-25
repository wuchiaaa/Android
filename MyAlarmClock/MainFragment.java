package com.example.myalarmclock;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainFragment extends Fragment {

    private Button btn_add;
    private List<Post> clockList = new ArrayList<>();//定義以clockList實體類為物件的資料集合
    private SetClockFragment setClockFragment;
    private String TAG = "MainFragment";
    private MyAdapter myAdapter;
    private RecyclerView recyclerView;
    public AlarmManager alarmManager;
    public PendingIntent pendingIntent;
    private Switch switch_openNClose;//主動顯示鬧鐘啟用或停用
    //12.31
//    public SQLiteHelper mySQLiteHelper;
//    public SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated...");
        btn_add = view.findViewById(R.id.btn_add);
        switch_openNClose = (Switch) view.findViewById(R.id.openNClose);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_main);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume...");

        //傳值跳轉
        MainActivity activity = (MainActivity) getActivity();
        FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
        //開啟事務
        final FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        setClockFragment = new SetClockFragment();

        //按鈕:增加鬧鐘
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//跳頁
                transaction.replace(R.id.fl_container_main, setClockFragment, "set");
                transaction.commitAllowingStateLoss();//寬容錯誤
            }
        });

        //查詢資料庫所有鬧鐘
        clockList = AlarmDB.showAll(getContext());

        //調用MyAdapter實現view
        myAdapter = new MyAdapter(getActivity(), clockList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));//引數是：上下文、列表方向（橫向還是縱向）、是否倒敘
        recyclerView.setAdapter(myAdapter);

        //自定義RecyclerView的item監聽事件
        myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) { //修改
                Log.d("傳遞給setOnItemClick", "我是item,data是:" + position);//0、1、2等

                Post post = clockList.get(position);//Post資料
                System.out.println("Post資料:" + post);
                Bundle bundle = new Bundle();
                bundle.putSerializable("alarmList", post);//要傳遞資料
                setClockFragment.setArguments(bundle);
                transaction.replace(R.id.fl_container_main, setClockFragment, "set");
                transaction.commitAllowingStateLoss();//寬容錯誤
            }

            @Override
            public void OnItemLongClick(View view, final int position) { //刪除&取消註冊
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("刪除");
                builder.setMessage("確定刪除嗎?");
                builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        System.out.println("刪除前的列表長度:"+clockList.size());
                        Post post = clockList.get(position);//Post資料
                        AlarmDB.delete(getContext(), clockList.get(position));//告訴DB刪除該筆post資料
//                        System.out.printf("刪除第%d個鬧鐘的資料\n", positionId);
                        clockList.remove(position);//1.先調用方法刪除
//                        System.out.println("刪除的position位置:"+positionId);
                        //刪除動畫
                        myAdapter.notifyItemRemoved(position);//2.通知演示插入動畫
                        myAdapter.notifyItemRangeChanged(position, clockList.size() - position);
//                        myAdapter.notifyDataSetChanged();//3.通知重新綁定所有數據與介面
//                        System.out.println("刪除後的列表長度:"+clockList.size());
                        //取消註冊鬧鐘alarmManager
                        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);//獲取AlarmManager
                        Intent intent = new Intent(getContext(), AlarmReceiver.class);
                        pendingIntent = PendingIntent.getBroadcast(getContext(), post.getClockId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(pendingIntent);
                        Log.d(TAG, "鬧鐘已刪除，取消已註冊的鬧鐘。");
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }

            @Override
            public void onCheckedChanged(CompoundButton buttonView, int position, boolean isChecked) { //鬧鐘啟用或停用
                Post post = clockList.get(position);//Post資料fromDB
                int clockId = post.getClockId();
                int repeat = post.getRepeat();

                alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);//獲取AlarmManager
                Intent intent = new Intent(getContext(), AlarmReceiver.class);
                intent.putExtra("vibrator_value", post.getClockVibrator());//傳值給broadcast
                intent.putExtra("clockName", post.getClockName());
                intent.putExtra("ringUri", post.getClockMedia());
                intent.putExtra("clockId", String.valueOf(clockId));//intent只能傳一種型別，故轉成str
                intent.putExtra("repeat", post.getRepeat());

                pendingIntent = PendingIntent.getBroadcast(getContext(), clockId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (buttonView.isChecked()) {
                    Calendar today = Calendar.getInstance();//現在時間
                    today.setTimeInMillis(System.currentTimeMillis());//設定系統目前時間
                    long triggerAtMillis = 0;

                    if(repeat == 0) {
                        //重新註冊鬧鐘alarmManager
                        int clockYear = post.getClockYear();
                        int clockMonth = post.getClockMonth();
                        clockMonth -= 1;//系統月份0-11
                        int clockDay = post.getClockDay();
                        int clockHour = post.getClockHour();
                        int clockMinute = post.getClockMinute();
                        System.out.println("@@該位置的post物件設定時間(月份0-11): " + clockYear + "/" + clockMonth + "/" + clockDay + "-" + clockHour + ":" + clockMinute);
                        Calendar c = Calendar.getInstance();//鬧鐘設定時間
                        c.set(clockYear, clockMonth, clockDay, clockHour, clockMinute, 0);
                        triggerAtMillis = c.getTimeInMillis();//獲得鬧鐘定時時間(得到點擊觸發的毫秒值)
                        if (System.currentTimeMillis() > triggerAtMillis) { //判斷如果當前系統時間大於設置的鬧鈴時間，則在第二天開始啟用該鬧鈴
                            triggerAtMillis = triggerAtMillis + 24 * 60 * 60 * 1000;
                            Log.d(TAG, "當前系統時間大於設置的鬧鈴時間，將在明天開始啟用該鬧鈴");
                            today.add(Calendar.DAY_OF_MONTH, 1);
                            int clockYearNew = today.get(Calendar.YEAR);
                            int clockMonthNew = today.get(Calendar.MONTH);//寫入資料庫的月份1-12
                            clockMonthNew += 1;//資料庫月份1-12
                            int clockDayNew = today.get(Calendar.DAY_OF_MONTH);
                            System.out.println("@@該位置的post物件更新後的設定時間: " + clockYearNew + "/" + clockMonthNew + "/" + clockDayNew + "-" + clockHour + ":" + clockMinute);
                            post.setClockYear(clockYearNew);
                            post.setClockMonth(clockMonthNew);
                            post.setClockDay(clockDayNew);
                        }
                    } else if (repeat == 1) {
                        int clockYear = today.get(Calendar.YEAR);
                        int clockMonth = today.get(Calendar.MONTH);
                        int clockDay = today.get(Calendar.DAY_OF_MONTH);
                        int clockHour = post.getClockHour();
                        int clockMinute = post.getClockMinute();
                        int nowDayOfWeek = today.get(Calendar.DAY_OF_WEEK);
                        int clockDayOfWeek;
                        today.set(clockYear, clockMonth, clockDay, clockHour, clockMinute, 0);//設定定時時間
                        triggerAtMillis = today.getTimeInMillis();//獲得設定時間

                        ArrayList<Integer> listWeek = new ArrayList<>(); //[1,2,3,4,5,6,7]
                        for (int i = 1; i < 8; i++) {
                            listWeek.add(i);
                        }
                        int sun = post.getRepeatSun();
                        int mon = post.getRepeatMon();
                        int tue = post.getRepeatTue();
                        int wed = post.getRepeatWed();
                        int thu = post.getRepeatThu();
                        int fri = post.getRepeatFri();
                        int sat = post.getRepeatSat();

                        if (sun == 0) listWeek.set(0, 0);//如果未設訂週期，改為0
                        if (mon == 0) listWeek.set(1, 0);
                        if (tue == 0) listWeek.set(2, 0);
                        if (wed == 0) listWeek.set(3, 0);
                        if (thu == 0) listWeek.set(4, 0);
                        if (fri == 0) listWeek.set(5, 0);
                        if (sat == 0) listWeek.set(6, 0);
                        System.out.println("MainFragment, listWeek目前設定的週期:" + listWeek);//[0, 0, 0, 4, 0, 0, 7]

                        //判斷今天是星期幾,且是否有設定週期
                        int todayRepeat = -1;
                        if (nowDayOfWeek == 1) todayRepeat = sun;
                        else if (nowDayOfWeek == 2) todayRepeat = mon;
                        else if (nowDayOfWeek == 3) todayRepeat = tue;
                        else if (nowDayOfWeek == 4) todayRepeat = wed;//今天
                        else if (nowDayOfWeek == 5) todayRepeat = thu;
                        else if (nowDayOfWeek == 6) todayRepeat = fri;
                        else if (nowDayOfWeek == 7) todayRepeat = sat;

                        if (todayRepeat == 1) { //今日有設週期性鬧鐘
                            if (System.currentTimeMillis() > triggerAtMillis) { //判斷如果當前系統時間大於設置的鬧鈴時間，則尋找下一個需啟用的鬧鐘週期
                                int index = -1;
                                clockDayOfWeek = nowDayOfWeek;
                                while (index < 0) {
                                    clockDayOfWeek += 1;//查找星期是否有在list裡面,有則代表設定該星期響鈴
                                    index = listWeek.indexOf(clockDayOfWeek); //nowDayOfWeek=4
                                    if (clockDayOfWeek == 8) {
                                        clockDayOfWeek = 0;
                                    }
                                }
                                System.out.println("尋找下一個需啟用的鬧鐘週期:" + clockDayOfWeek + ",index:" + index);
                                int diffDayOfWeek = clockDayOfWeek - nowDayOfWeek;
                                if (diffDayOfWeek <= 0) diffDayOfWeek += 7;
                                today.add(Calendar.DAY_OF_MONTH, diffDayOfWeek);//從今日起算，相隔幾個天數要響鈴
                            }
                        } else { //今天未設置週期，故查找下一個響鈴的
                            int index = -1;
                            clockDayOfWeek = nowDayOfWeek;
                            while (index < 0) {
                                clockDayOfWeek += 1;//查找星期是否有在list裡面,有則代表設定該星期響鈴
                                index = listWeek.indexOf(clockDayOfWeek); //nowDayOfWeek=4
                                if (clockDayOfWeek == 8) {
                                    clockDayOfWeek = 0;
                                }
                            }
                            System.out.println("尋找下一個需啟用的鬧鐘週期:" + clockDayOfWeek + ",index:" + index);
                            int diffDayOfWeek = clockDayOfWeek - nowDayOfWeek;
                            if (diffDayOfWeek <= 0) diffDayOfWeek += 7;
                            today.add(Calendar.DAY_OF_MONTH, diffDayOfWeek);//從今日起算，相隔幾個天數要響鈴
                        }
                        clockYear = today.get(Calendar.YEAR);
                        clockMonth = today.get(Calendar.MONTH);
                        clockDay = today.get(Calendar.DAY_OF_MONTH);
                        today.set(clockYear, clockMonth, clockDay, clockHour, clockMinute, 0);//設定定時時間
                        triggerAtMillis = today.getTimeInMillis();//獲得設定時間
                        @SuppressLint("SimpleDateFormat") DateFormat dateFo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        today.setTimeInMillis(triggerAtMillis);
                        Log.d(TAG, "設定需要響鈴的時間:" + dateFo.format(today.getTime()));

                    }

                    AlarmDB.updateEnableOpen(getActivity(), post, clockId);
                    //啟用鬧鐘
                    if (Build.VERSION.SDK_INT < 19) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                    }
                    Toast.makeText(getActivity(), "鬧鐘已設定完成" + position, Toast.LENGTH_SHORT).show();
                } else {
                    //取消註冊鬧鐘alarmManager
                    Log.d(TAG, "關閉鬧鐘，取消已註冊的鬧鐘。");
                    alarmManager.cancel(pendingIntent);
                    AlarmDB.updateEnableClose(getActivity(), clockId);
                }
            }
        });

    }

    @Override
    public void onPause() { //失去用戶焦點，但仍是可見的
        super.onPause();
        Log.d(TAG, "onPause...");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy...");
    }
}
