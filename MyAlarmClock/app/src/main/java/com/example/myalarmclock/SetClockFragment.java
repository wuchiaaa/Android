package com.example.myalarmclock;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;

public class SetClockFragment extends Fragment implements CompoundButton.OnCheckedChangeListener{

    private ImageView imgv_date;
    private TimePicker timePicker;
    private EditText editText;
    private TextView txtItem_dateTimeDay, txtItem_switch;
    private int year, month, day, dayOfWeek, hour, minute;
    private String date, time;
    private String Week = ",週";
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private Button btn_save;
    private Button btn_cancel;
    private Switch switch_vibrate;
    private boolean mCalculatorEnabled;
    private String vibrator_value;//判斷鈴聲與震動是否開啟的值


    @Nullable
    @Override //設置Fragment自己的佈置文件 set container view
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setclock, container, false);
        return view;
    }

    @Override //findViewById
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //view創建完成後開始做事
        txtItem_dateTimeDay = view.findViewById(R.id.txtItem_dateTimeDay);
        imgv_date = view.findViewById(R.id.imgv_date);
        timePicker = view.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);//改成24小時制
        editText = view.findViewById(R.id.inputClockName);
        btn_save = view.findViewById(R.id.btn_save);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        switch_vibrate = view.findViewById(R.id.switch_vibrate);
        txtItem_switch = view.findViewById(R.id.txtItem_switch);

        //=========================================================

        mCalculatorEnabled = Boolean.valueOf(true);//初始設定"震動打開"-->影響switch顯示
        switch_vibrate.setChecked(mCalculatorEnabled);
        switch_vibrate.setOnCheckedChangeListener(this);
        txtItem_switch.setText("Basic call");

        //=========================================================

        //日期設定
        imgv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //datepicker當前顯示頁面
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {//監聽year,month,day的變化
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String format = setDateFormat(year, month, day);
                        txtItem_dateTimeDay.setText(format);
                    }
                }, year, month, day).show();
            }
        });

        //時間設定
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() { //監聽hour和minute的變化
            @Override
            public void onTimeChanged(TimePicker view, int hour, int minute) {
                SetClockFragment.this.hour = hour;
                SetClockFragment.this.minute = minute;
                setTimeFormat(hour, minute);
            }
        });

        //鬧鐘名稱設定
        editText.addTextChangedListener(new TextWatcher() {     //setOnKeyListener:輸入都要再按一次完成才會即時才會擷取
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 10) { //判斷EditText中輸入的字符數是否已經大於10
                    editText.setText(s.toString().substring(0, 10)); //設置EditText只顯示前面10個字
                    editText.setSelection(10);//光標移到末端
                    //handler = new Handler(Looper.getMainLooper());  /*toast提醒不顯示問題->未完成*/
                    Toast.makeText(getActivity(), R.string.remind, Toast.LENGTH_SHORT).show();//Toast提示內容
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setListener();



    }

    @Override//監聽是否震動的switch
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()){
            case R.id.switch_vibrate:
                if(compoundButton.isChecked()) {
                    txtItem_switch.setText("Basic call");
                    vibrator_value = "ON";
                    Toast.makeText(getActivity(),"開關:ON",Toast.LENGTH_SHORT).show();
                }
                else {
                    txtItem_switch.setText("關");
                    vibrator_value = "OFF";
                    Toast.makeText(getActivity(),"開關:OFF",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void setListener(){
        OnClick onclick = new OnClick();
        btn_save.setOnClickListener(onclick);
        btn_cancel.setOnClickListener(onclick);
    }

    public class OnClick implements View.OnClickListener{
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)//startAlarm()要求版本
        @Override
        public void onClick(View v) {
            MainActivity activity = (MainActivity) getActivity();
            FragmentManager supportFragmentManager = activity.getSupportFragmentManager();//傳值跳轉
            FragmentTransaction transaction = supportFragmentManager.beginTransaction();//開啟事務
            ItemFragment itemFragment = new ItemFragment();
            MainFragment mainFragment = new MainFragment();
            switch (v.getId()){
                case R.id.btn_save:
                    Toast.makeText(getActivity(), "鬧鐘已設定完成!", Toast.LENGTH_LONG).show();//Toast提示內容
                    Bundle bundle = new Bundle();
                    String clockname = editText.getText().toString();
                    String timePicker = time;
                    String datePicker = date;
                    bundle.putString("clockname", clockname);//將要傳遞的資料輸入
                    bundle.putString("time", timePicker);
                    bundle.putString("date", datePicker);
                    bundle.putString("vibrator_value", vibrator_value);
                    itemFragment.setArguments(bundle);
                    transaction.replace(R.id.fl_container_main, itemFragment,"set儲存");
                    startAlarm(v);//開啟鬧鐘
//                    createAlarm(clockname, hour,minute);
                    Toast.makeText(getActivity(), vibrator_value+"<--這是ON還是OFF", Toast.LENGTH_LONG).show();//Toast提示內容
                    break;
                case R.id.btn_cancel:
                    Toast.makeText(getContext(), "鬧鐘取消設定", Toast.LENGTH_LONG).show();//Toast提示內容
                    transaction.replace(R.id.fl_container_main, mainFragment,"set取消");
                    break;
            }
            transaction.commitAllowingStateLoss();//寬容錯誤
        }
    }

    //開啟鬧鐘(設置在手機內部的鬧鐘)
    public void createAlarm(String message, int hour, int minute){
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minute);
        startActivity(intent);
    }



    //開啟鬧鐘
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)//setExact()要求版本
    public void startAlarm(View view){
//        Log.i(TAG, "startAlarm");//??????????

        //AlarmManager搭配Activity使用
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);//獲取AlarmManager
        Intent intent = new Intent(getActivity(), ClockActivity.class);//創建intent設置要啟動的組件
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        long triggerAtMillis= c.getTimeInMillis();//得到點擊觸發的毫秒值（即鬧鐘提醒時間）
        alarmIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);//PendingIntent對象設置動作，啟動的是Activity?Service?廣播
        if(System.currentTimeMillis() > triggerAtMillis){ //判斷如果當前系統時間大於設置的鬧鈴時間，則在第二天開始啟用該鬧鈴
            triggerAtMillis = triggerAtMillis + 24*60*60*1000;
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, alarmIntent);//設置單次鬧鐘的類型,啟動時間,PendingIntent對象

//        //失敗的方法一
//        //是否震動的value，從Fragment傳到Activity
//        Bundle bundle = new Bundle();
//        bundle.putString("vibrator_value", vibrator_value);
//        intent.putExtras(bundle);
//        getActivity().startActivity(intent);//會立即啟用AlertDialog
    }

    //取消鬧鐘(If the alarm has been set, cancel it.)
    public void cancelAlarm(View view) {
//        Log.i(TAG, "cancelAlarm");
        if (alarmManager != null) {
            alarmManager.cancel(alarmIntent);
        }
    }

    //設置日期格式  //尚未加入"星期幾"
    public String setDateFormat(int year, int month, int day) {
//        switch (dayOfWeek){
//            case 1:
//                Week += "一";
//                break;
//            case 2:
//                Week += "二";
//                break;
//            case 3:
//                Week += "三";
//                break;
//            case 4:
//                Week += "四";
//                break;
//            case 5:
//                Week += "五";
//                break;
//            case 6:
//                Week += "六";
//                break;
//            case 7:
//                Week += "日";
//                break;
//            default:
//                break;
//        }
        date = year + "年" + (month + 1) + "月" + day + "日";//+ Week
        return date;
    }

    //設置時間格式
    public void setTimeFormat(int hour, int minute) {
        String str_time = "%02d";
        String str_hour = String.format(str_time, hour);
        String str_minute = String.format(str_time, minute);
        time = str_hour + ":" + str_minute;
//        Toast.makeText(SetActivity.this, time, Toast.LENGTH_LONG).show();//Toast提示內容
    }

}
