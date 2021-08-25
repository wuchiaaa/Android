package com.example.myalarmclock;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class SetClockFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private ImageView imgv_date;
    private TimePicker timePicker;
    private EditText editText;
    private TextView txtItem_dateTimeDay, txtItem_switch, txtItem_btn_media;
    private int nowYear, nowMonth, nowDay, setDay, nowDayOfWeek, nowHour, nowMinute;//系統當前的年、月、日、時、分
    private int selectYear, selectMonth, selectDay, selectDayOfWeek, selectHour, selectMinute;//user選擇的年、月、日、時、分
    private String date, time;
    private String vibrator_value;//判斷鈴聲與震動是否開啟的值
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Button btn_save, btn_cancel, btn_media;
    private Switch switch_vibrate;
    private static final int Ringtone = 4;
    private Uri uri_clockMedia;
    private Random r = new Random();//因需設置多個鬧鐘，故PendingIntent的第二個參數需唯一
    private int clockId = r.nextInt();
    private String TAG = "SetClockFragment";
    private final Calendar calendar = Calendar.getInstance();//datePicker的時間
    private Calendar nowCalendar = Calendar.getInstance();//當下日期
    private int action;//0為新增,1為更新
    private int clockEnable = 1;//0為停用,1為啟用
    private int repeat = 0;
    private Post post = new Post();
    private ToggleButton toggle_sun, toggle_mon, toggle_tue, toggle_wed, toggle_thu, toggle_fri, toggle_sat;
    private ArrayList<Integer> listWeek = new ArrayList<>(); //[1,2,3,4,5,6,7]

    @Nullable
    @Override //設置Fragment自己的佈置文件 set container view
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setclock, container, false);
        return view;
    }

    private void findView(View view){
        txtItem_dateTimeDay = view.findViewById(R.id.txtItem_dateTimeDay);
        imgv_date = view.findViewById(R.id.imgv_date);
        timePicker = view.findViewById(R.id.time_picker);
        editText = view.findViewById(R.id.inputClockName);
        btn_save = view.findViewById(R.id.btn_save);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        switch_vibrate = view.findViewById(R.id.switch_vibrate);
        txtItem_switch = view.findViewById(R.id.txtItem_switch);
        btn_media = view.findViewById(R.id.btn_media);
        txtItem_btn_media = view.findViewById(R.id.txtItem_btn_media);
        toggle_sun = view.findViewById(R.id.toggle_sun);
        toggle_mon = view.findViewById(R.id.toggle_mon);
        toggle_tue = view.findViewById(R.id.toggle_tue);
        toggle_wed = view.findViewById(R.id.toggle_wed);
        toggle_thu = view.findViewById(R.id.toggle_thu);
        toggle_fri = view.findViewById(R.id.toggle_fri);
        toggle_sat = view.findViewById(R.id.toggle_sat);
    }

    private void setRepeatCheck_OFF(){ //關閉所有週期點擊設定
        toggle_sun.setChecked(false);
        toggle_mon.setChecked(false);
        toggle_tue.setChecked(false);
        toggle_wed.setChecked(false);
        toggle_thu.setChecked(false);
        toggle_fri.setChecked(false);
        toggle_sat.setChecked(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override //findViewById
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //view創建完成後開始做事
        findView(view);
        setRepeatCheck_OFF();//預設不打開週期設定
        toggle_sun.setOnCheckedChangeListener(mOnCheckedChangeListener);//監聽事件
        toggle_mon.setOnCheckedChangeListener(mOnCheckedChangeListener);
        toggle_tue.setOnCheckedChangeListener(mOnCheckedChangeListener);
        toggle_wed.setOnCheckedChangeListener(mOnCheckedChangeListener);
        toggle_thu.setOnCheckedChangeListener(mOnCheckedChangeListener);
        toggle_fri.setOnCheckedChangeListener(mOnCheckedChangeListener);
        toggle_sat.setOnCheckedChangeListener(mOnCheckedChangeListener);
        initAlarmClockData();//初次設定鬧鐘or修改鬧鐘內容
        nowDayOfWeek = nowCalendar.get(Calendar.DAY_OF_WEEK);//初次設定鬧鐘or修改鬧鐘內容,Tue=3

        //=========================================================

        //鈴聲預設值
        android.media.Ringtone ringtone = RingtoneManager.getRingtone(getContext(), uri_clockMedia);
        String clockMediaName = ringtone.getTitle(getContext());
        txtItem_btn_media.setText(clockMediaName);//目前預設鈴聲的名稱
        //鈴聲選擇
        btn_media.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ringIntent = new Intent();
                ringIntent.setAction(RingtoneManager.ACTION_RINGTONE_PICKER);//打開系統鈴聲選擇器
                ringIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM); //設置類型為鬧鐘
                //如果有傳值
                ringIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri_clockMedia);//顯示已存在的鬧鐘鈴聲

                ringIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);//列表中不顯示"默認鈴聲"選項，默認是顯示的
                ringIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "選擇鬧鐘鈴聲");//設置列表對話方塊的標題，不設置，預設顯示"鈴聲"
                startActivityForResult(ringIntent, Ringtone);
            }
        });


        //震動開關設定
        switch_vibrate.setOnCheckedChangeListener(this);


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
                    Toast.makeText(getActivity(), R.string.remind, Toast.LENGTH_SHORT).show();//Toast提示內容
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        /*
         *
         * 日期時間
         * 預設鬧鐘時間為明日(day+1)時間06:00
         *
         * */
        //日期選擇
        imgv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeat = 0;

                //datepicker當前顯示頁面
                Calendar c = Calendar.getInstance();//當前日期
                nowYear = c.get(Calendar.YEAR);
                nowMonth = c.get(Calendar.MONTH);
                setDay = calendar.get(Calendar.DAY_OF_MONTH);//預設明天
                nowDay = c.get(Calendar.DAY_OF_MONTH);
                nowHour = c.get(Calendar.HOUR_OF_DAY);//24小時制
                nowMinute = c.get(Calendar.MINUTE);
                Log.d(TAG, "2.預設日期(0-11月):" + nowYear + "." + nowMonth + "." + nowDay + "/" + selectHour + ":" + selectMinute);
                final int todayOfYear = c.get(Calendar.DAY_OF_YEAR);//預設為今日+1天(轉為365天制)

                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {//監聽year,month,day的變化
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String format = setDateFormat(year, month, day);
                        calendar.set(year, month, day);//將datePicker的當前日期轉為Calendar對象
                        setRepeatCheck_OFF();//關閉所有週期設定
                        if (nowYear < year) {
                            selectYear = year;
                            selectMonth = month;
                            selectDay = day;
                            txtItem_dateTimeDay.setText(format);
                        } else if (nowYear == year) {
                            int selectDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);//datePicker的當前日期(轉為365天制)
                            int dayDiff = selectDayOfYear - todayOfYear;
//                            System.out.println("日期設定todayOfYear:" + todayOfYear);
//                            System.out.println("日期設定selectDayOfYear:" + selectDayOfYear);
//                            System.out.println("日期設定差異dayDiff: " + dayDiff);
                            if (dayDiff == 0) { //鬧鐘日期=今天日期
                                long startMillis = nowHour * 60 + nowMinute;
                                long endMillis = timePicker.getHour() * 60 + timePicker.getMinute();//獲得鬧鐘定時時間(得到點擊觸發的毫秒值)
                                long diffMillis = endMillis - startMillis;
//                                System.out.println("現在小時"+nowHour+",鬧鐘小時:"+timePicker.getHour());
//                                System.out.println("時間相差(分鐘):" + diffMillis);
                                if (diffMillis > 0) { //鬧鐘time>今天time
                                    selectYear = year;
                                    selectMonth = month;
                                    selectDay = day;
                                    txtItem_dateTimeDay.setText(format);
                                } else
                                    Toast.makeText(getActivity(), "無法設定過去時間的鬧鐘", Toast.LENGTH_SHORT).show();
                            } else if (dayDiff > 0) { //鬧鐘日期>今天日期
                                selectYear = year;//保存設置的年度
                                selectMonth = month;//保存設置的月份
                                selectDay = day;//保存設置的日期
                                txtItem_dateTimeDay.setText(format);
                            } else
                                Toast.makeText(getActivity(), "無法設定過去時間的鬧鐘", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getActivity(), "無法設定過去時間的鬧鐘", Toast.LENGTH_SHORT).show();
                    }
                }, nowYear, nowMonth, setDay).show();
            }
        });


        //時間選擇
        timePicker.setIs24HourView(true);//改成24小時制
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() { //監聽hour和minute的變化
            @Override
            public void onTimeChanged(TimePicker view, int hour, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hour);//設置小時
                calendar.set(Calendar.MINUTE, minute);//設置分鐘
                calendar.set(Calendar.SECOND, 0);//設置秒
                calendar.set(Calendar.MILLISECOND, 0);//設置毫秒
                if (repeat == 0) {
                    long todayMillis = System.currentTimeMillis();
                    long selectMillis = calendar.getTimeInMillis();//獲得timePicker鬧鐘定時時間(得到點擊觸發的毫秒值)
                    long diffMillis = selectMillis - todayMillis;
                    if (diffMillis <= 0) { //判斷如果設置的鬧鈴時間[小於等於]當前系統時間，則在第二天開始啟用該鬧鈴
                        selectDay = calendar.get(Calendar.DAY_OF_MONTH);
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        selectYear = calendar.get(Calendar.YEAR);
                        selectMonth = calendar.get(Calendar.MONTH);
                        selectDay = calendar.get(Calendar.DAY_OF_MONTH);
                        Log.d(TAG, "3.時間選擇(0-11月):" + selectYear + "." + selectMonth + "." + selectDay + "/" + selectHour + ":" + selectMinute);
                        String tomorrow = setDateFormat(selectYear, selectMonth, selectDay);
                        txtItem_dateTimeDay.setText(tomorrow);
                    } else {
                        String today = setDateFormat(selectYear, selectMonth, selectDay);
                        txtItem_dateTimeDay.setText(today);
                    }
                }else if(repeat == 1){
                    System.out.println("3.時間選擇:維持週期設定...");
                }

                SetClockFragment.this.selectHour = hour;//保存設置的小時
                SetClockFragment.this.selectMinute = minute;//保存設置的分鐘
                setTimeFormat(hour, minute);//格式化
            }
        });

        //按鈕:鬧鐘設定值->儲存or取消儲存
        setListener();

    }

//==============以上onViewCreated()方法===================================================================

    //初次設定鬧鐘or修改鬧鐘內容
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initAlarmClockData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            /*
             * 鬧鐘修改頁面
             * */
            action = 1;
            Post post = (Post) bundle.getSerializable("alarmList");//反序列化
            if (post != null) {
                clockId = post.getClockId();
                repeat = post.getRepeat();
                //------以下為可修改的值----------------
                //日期or週期
                if (repeat == 0) { //一次性鬧鐘
                    Log.d(TAG, "REPEAT=0,一次性鬧鐘的修改頁面...");
                    selectYear = post.getClockYear();
                    selectMonth = post.getClockMonth();//資料庫取出月份1-12
                    selectMonth -= 1;                  //系統月份0-11
                    selectDay = post.getClockDay();
                    selectHour = post.getClockHour();
                    selectMinute = post.getClockMinute();

                    String strDate = setDateFormat(selectYear, selectMonth, selectDay);//僅用於顯示
                    txtItem_dateTimeDay.setText(strDate);
                    Log.d(TAG, "4.鬧鐘修改頁面(0-11月):" + selectYear + "." + selectMonth + "." + selectDay + "/" + selectHour + ":" + selectMinute);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        LocalDate today = LocalDate.now();
                        LocalDate newDate = LocalDate.of(selectYear, selectMonth + 1, selectDay);//from 1 (January) to 12 (December)
                        long daysDiff = ChronoUnit.DAYS.between(today, newDate);
                        System.out.printf("目標日期距離今天的時間差：%d 天\n", daysDiff);
                        if ((int) daysDiff <= 0) {
                            Calendar c = Calendar.getInstance();//當前日期
                            nowHour = c.get(Calendar.HOUR_OF_DAY);//24小時制
                            nowMinute = c.get(Calendar.MINUTE);
                            timePicker.setHour(selectHour);//設定timePicker
                            timePicker.setMinute(selectMinute);
                            int selectTime = timePicker.getHour() * 60 + timePicker.getMinute();
                            int nowTime = nowHour * 60 + nowMinute;
                            System.out.println("5.鬧鐘偵測頁面_selectTime:" + selectTime);
                            System.out.println("5.鬧鐘偵測頁面_nowMinute:" + nowTime);
                            if (selectTime <= nowTime) {
                                c.add(Calendar.DAY_OF_MONTH, 1);//修改畫面:鬧鐘時間<現在時間，預設日期改為明天
                                selectYear = c.get(Calendar.YEAR);
                                selectMonth = c.get(Calendar.MONTH);
                                selectDay = c.get(Calendar.DAY_OF_MONTH);
                                strDate = setDateFormat(selectYear, selectMonth, selectDay);//僅用於顯示
                                txtItem_dateTimeDay.setText(strDate);
                                Log.d(TAG, "5.鬧鐘偵測頁面(0-11月):" + selectYear + "." + selectMonth + "." + selectDay + "/" + selectHour + ":" + selectMinute);
                            }else{  //修改畫面:鬧鐘時間>現在時間，預設日期改為今天
                                selectYear = c.get(Calendar.YEAR);
                                selectMonth = c.get(Calendar.MONTH);
                                selectDay = c.get(Calendar.DAY_OF_MONTH);
                                strDate = setDateFormat(selectYear, selectMonth, selectDay);//僅用於顯示
                                txtItem_dateTimeDay.setText(strDate);
                                Log.d(TAG, "5.鬧鐘偵測頁面(0-11月):" + selectYear + "." + selectMonth + "." + selectDay + "/" + selectHour + ":" + selectMinute);
                            }
                        }
                    } else {
                        System.out.println("LocalDate.of 印不出來");
                    }
                } else if (repeat == 1) {//週期性鬧鐘
                    Log.d(TAG, "REPEAT=1,週期性鬧鐘的修改頁面...");
                    if (post.getRepeatSun() == 1) toggle_sun.setChecked(true);
                    if (post.getRepeatMon() == 1) toggle_mon.setChecked(true);
                    if (post.getRepeatTue() == 1) toggle_tue.setChecked(true);
                    if (post.getRepeatWed() == 1) toggle_wed.setChecked(true);
                    if (post.getRepeatThu() == 1) toggle_thu.setChecked(true);
                    if (post.getRepeatFri() == 1) toggle_fri.setChecked(true);
                    if (post.getRepeatSat() == 1) toggle_sat.setChecked(true);
                }

                //時間
                selectHour = post.getClockHour();
                selectMinute = post.getClockMinute();
                timePicker.setHour(selectHour);
                timePicker.setMinute(selectMinute);
                setTimeFormat(selectHour, selectMinute);

                //鈴聲
                uri_clockMedia = Uri.parse(post.getClockMedia());

                //是否震動
                if (post.getClockVibrator().equals("OFF")) {
                    vibrator_value = "OFF";
                    switch_vibrate.setChecked(false);
                    txtItem_switch.setText("關");
                } else if (post.getClockVibrator().equals("ON")) {
                    vibrator_value = "ON";
                    switch_vibrate.setChecked(true);
                    txtItem_switch.setText("Basic call");
                }

                //鬧鐘名稱
                editText.setText(post.getClockName());
            }
        } else {
            /*
             * 初次設定鬧鐘
             * */
            action = 0;
            //鈴聲預設值
            uri_clockMedia = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);//取得目前的預設鈴聲
            //震動預設值
            switch_vibrate.setChecked(true);//初始設定"震動打開"-->影響switch顯示
            vibrator_value = "ON";
            txtItem_switch.setText("Basic call");
            //時間預設值
            timePicker.setHour(6);
            timePicker.setMinute(0);
            setTimeFormat(6, 0);
            selectHour = 6;
            selectMinute = 0;
            //日期預設值-->單次，故顯示日期
            calendar.add(Calendar.DAY_OF_MONTH, 1); //預設鬧鐘時間為明天(day+1)時間06:00
            selectYear = calendar.get(Calendar.YEAR);
            selectMonth = calendar.get(Calendar.MONTH);
            selectDay = calendar.get(Calendar.DAY_OF_MONTH);
            String tomorrow = setDateFormat(selectYear, selectMonth, selectDay);
            txtItem_dateTimeDay.setText(tomorrow);
            Log.d(TAG, "1.初次設定鬧鐘(0-11月,隔日):" + selectYear + "." + selectMonth + "." + selectDay + "/" + selectHour + ":" + selectMinute);
        }
    }

    //設置鈴聲之後的回調函數
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != Ringtone) {  //if(requestCode != RESULT_OK){   //activity上用的
            return;
        } else {
            //得到選擇的鈴聲URI，如果是靜音EXTRA_RINGTONE_PICKED_URI將會返回null --> NullPointerException
            try {
                uri_clockMedia = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            } catch (NullPointerException np) {
                Log.d("SetClockFragment", "pick nothing");
            }

            Log.e("onActivityResult====", "鈴聲選擇:" + uri_clockMedia);//Log.e:報Error
            //獲取鈴聲類型名稱
            android.media.Ringtone ringtone = RingtoneManager.getRingtone(getContext(), uri_clockMedia);
            String clockMediaName = ringtone.getTitle(getContext());
            txtItem_btn_media.setText(clockMediaName);
        }
    }

    //監聽是否震動的switch
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.switch_vibrate:
                if (compoundButton.isChecked()) {
                    txtItem_switch.setText("Basic call");
                    vibrator_value = "ON";
                } else {
                    txtItem_switch.setText("關");
                    vibrator_value = "OFF";
                }
                break;
        }
    }

    //監聽是否打開週期的toggleButton
    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            repeat = 1;
            switch (buttonView.getId()) {
                case R.id.toggle_sun:
                    if (buttonView.isChecked()) post.setRepeatSun(1);//預設false,改寫成true
                    else post.setRepeatSun(0);
                    break;
                case R.id.toggle_mon:
                    if (buttonView.isChecked()) post.setRepeatMon(1);
                    else post.setRepeatMon(0);
                    break;
                case R.id.toggle_tue:
                    if (buttonView.isChecked()) post.setRepeatTue(1);
                    else post.setRepeatTue(0);
                    break;
                case R.id.toggle_wed:
                    if (buttonView.isChecked()) post.setRepeatWed(1);
                    else post.setRepeatWed(0);
                    break;
                case R.id.toggle_thu:
                    if (buttonView.isChecked()) post.setRepeatThu(1);
                    else post.setRepeatThu(0);
                    break;
                case R.id.toggle_fri:
                    if (buttonView.isChecked()) post.setRepeatFri(1);
                    else post.setRepeatFri(0);
                    break;
                case R.id.toggle_sat:
                    if (buttonView.isChecked()) post.setRepeatSat(1);
                    else post.setRepeatSat(0);
                    break;
                default:
                    break;
            }
            ArrayList<String> weeks = new ArrayList<String>();//用於顯示
            if (post.getRepeatSun() == 1) weeks.add("週日");
            if (post.getRepeatMon() == 1) weeks.add("週一");
            if (post.getRepeatTue() == 1) weeks.add("週二");
            if (post.getRepeatWed() == 1) weeks.add("週三");
            if (post.getRepeatThu() == 1) weeks.add("週四");
            if (post.getRepeatFri() == 1) weeks.add("週五");
            if (post.getRepeatSat() == 1) weeks.add("週六");
            StringBuilder weekList = new StringBuilder("每 ");
            for (int i = 0; i < weeks.size(); i++) {
                weekList.append(weeks.get(i)).append(",");
            }
            if (weeks.size() == 7) txtItem_dateTimeDay.setText("每天");
            else txtItem_dateTimeDay.setText(weekList.toString());

            if (weeks.size() == 0) {
                //日期預設值-->單次，故顯示日期
                repeat = 0;
                Calendar today = Calendar.getInstance();//calendar實例化，取得預設時間、預設時區
                today.setTimeInMillis(System.currentTimeMillis());//設定系統目前時間
                selectYear = today.get(Calendar.YEAR);
                selectMonth = today.get(Calendar.MONTH);
                selectDay = today.get(Calendar.DAY_OF_MONTH);
                today.set(selectYear, selectMonth, selectDay, selectHour, selectMinute, 0);//設定定時
                if (System.currentTimeMillis() > today.getTimeInMillis()) { //判斷如果當前系統時間大於設置的鬧鈴時間，則將日期設為隔日
                    today.add(Calendar.DAY_OF_MONTH, 1);
                    selectYear = today.get(Calendar.YEAR);
                    selectMonth = today.get(Calendar.MONTH);
                    selectDay = today.get(Calendar.DAY_OF_MONTH);
                }
                String tomorrow = setDateFormat(selectYear, selectMonth, selectDay);
                txtItem_dateTimeDay.setText(tomorrow);
                Log.d(TAG, "6.初次設定鬧鐘(0-11月,隔日),週期已被全部取消的情況下:" + selectYear + "." + selectMonth + "." + selectDay + "/" + selectHour + ":" + selectMinute);
            }
        }
    };


    public void setListener() {
        OnClick onclick = new OnClick();
        btn_save.setOnClickListener(onclick);
        btn_cancel.setOnClickListener(onclick);
    }

    //鬧鐘設定值:儲存or取消儲存
    public class OnClick implements View.OnClickListener {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)//startAlarm()要求版本
        @Override
        public void onClick(View v) {
            MainActivity activity = (MainActivity) getActivity();
            FragmentManager supportFragmentManager = activity.getSupportFragmentManager();//傳值跳轉
            FragmentTransaction transaction = supportFragmentManager.beginTransaction();//開啟事務
            MainFragment toMainFragment = ((MainActivity) getActivity()).mainFragment;//避免replace的時候重新new一次Fragment，故從Activity調用
            post.setClockEnable(clockEnable);
            post.setClockId(clockId);
            post.setClockYear(selectYear);
            post.setClockMonth(selectMonth);
            post.setClockDay(selectDay);
            post.setClockHour(selectHour);
            post.setClockMinute(selectMinute);
            post.setClockName(editText.getText().toString());
            post.setClockVibrator(vibrator_value);
            post.setClockMedia(String.valueOf(uri_clockMedia));
            post.setRepeat(repeat);//1或0
            Log.d(TAG, "save/cancel, repeat的值:" + repeat);

            switch (v.getId()) {
                case R.id.btn_save:
                    Toast.makeText(getActivity(), "鬧鐘已設定完成!", Toast.LENGTH_SHORT).show();//Toast提示內容
                    if (action == 0) AlarmDB.insert(getContext(), post);//post插入資料庫
                    else if (action == 1) AlarmDB.update(getContext(), post);//post更新資料庫
                    startAlarm(v);//開啟鬧鐘
                    break;
                case R.id.btn_cancel:
                    Log.d("SetClock鬧鐘取消設定(僅用於顯示)", "日期:" + date + ",時間:" + time + ",震動:" + vibrator_value);
                    Toast.makeText(getContext(), "鬧鐘取消設定", Toast.LENGTH_SHORT).show();//Toast提示內容
                    break;
            }
            transaction.replace(R.id.fl_container_main, toMainFragment, "toMainFragment");
            transaction.commitAllowingStateLoss();//寬容錯誤
        }
    }

    //開啟鬧鐘 //AlarmManager搭配Activity使用
    private void startAlarm(View view) {
        Calendar today = Calendar.getInstance();//calendar實例化，取得預設時間、預設時區
        today.setTimeInMillis(System.currentTimeMillis());//設定系統目前時間
        long triggerAtMillis = 0;
        if (repeat == 0) {
            Log.d("startAlarm_鬧鐘設定日期(程式日期)", "年度:" + selectYear + ",月份(0-11):" + selectMonth + ",日:" + selectDay + ",時:" + selectHour + ",分:" + selectMinute);
            today.set(selectYear, selectMonth, selectDay, selectHour, selectMinute, 0);//設定定時
            triggerAtMillis = today.getTimeInMillis();//獲得鬧鐘定時時間(得到點擊觸發的毫秒值)
            if (System.currentTimeMillis() > triggerAtMillis) { //判斷如果當前系統時間大於設置的鬧鈴時間，則在第二天開始啟用該鬧鈴
                triggerAtMillis = triggerAtMillis + 24 * 60 * 60 * 1000;
                Toast.makeText(getActivity(), "設定時間早於現在時間，將於第二天啟動鬧鐘", Toast.LENGTH_LONG).show();
            }
        } else if (repeat == 1) {
            selectYear = today.get(Calendar.YEAR);
            selectMonth = today.get(Calendar.MONTH);
            selectDay = today.get(Calendar.DAY_OF_MONTH);
            today.set(selectYear, selectMonth, selectDay, selectHour, selectMinute, 0);//設定定時時間
            triggerAtMillis = today.getTimeInMillis();//獲得設定時間

            for (int i = 1; i < 8; i++) {    //[1,2,3,4,5,6,7]
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
            System.out.println("listWeek目前設定的週期:" + listWeek);//[0, 0, 0, 4, 0, 0, 7]

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
                    selectDayOfWeek = nowDayOfWeek;
                    while (index < 0) {
                        selectDayOfWeek += 1;//查找星期是否有在list裡面,有則代表設定該星期響鈴
                        index = listWeek.indexOf(selectDayOfWeek); //nowDayOfWeek=4
                        if (selectDayOfWeek == 8) {
                            selectDayOfWeek = 0;
                        }
                    }
                    System.out.println("當前系統時間大於設置時間，尋找下一個鬧鐘的時間:" + selectDayOfWeek + ",index:" + index);
                    int diffDayOfWeek = selectDayOfWeek - nowDayOfWeek;
                    if (diffDayOfWeek <= 0) diffDayOfWeek += 7;
                    today.add(Calendar.DAY_OF_MONTH, diffDayOfWeek);//從今日起算，相隔幾個天數要響鈴
                }
            } else { //今天未設置週期，故查找下一個響鈴的
                int index = -1;
                selectDayOfWeek = nowDayOfWeek;
                while (index < 0) {
                    selectDayOfWeek += 1;//查找星期是否有在list裡面,有則代表設定該星期響鈴
                    index = listWeek.indexOf(selectDayOfWeek); //nowDayOfWeek=4
                    if (selectDayOfWeek == 8) {
                        selectDayOfWeek = 0;
                    }
                }
                System.out.println("當前日期未設置週期鬧鐘，尋找下一個鬧鐘的時間:" + selectDayOfWeek + ",index:" + index);
                int diffDayOfWeek = selectDayOfWeek - nowDayOfWeek;
                if (diffDayOfWeek <= 0) diffDayOfWeek += 7;
                today.add(Calendar.DAY_OF_MONTH, diffDayOfWeek);//從今日起算，相隔幾個天數要響鈴
            }
            selectYear = today.get(Calendar.YEAR);
            selectMonth = today.get(Calendar.MONTH);
            selectDay = today.get(Calendar.DAY_OF_MONTH);
            today.set(selectYear, selectMonth, selectDay, selectHour, selectMinute, 0);//設定定時時間
            triggerAtMillis = today.getTimeInMillis();//獲得設定時間
            @SuppressLint("SimpleDateFormat") DateFormat dateFo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            today.setTimeInMillis(triggerAtMillis);
            Log.d(TAG, "設定需要響鈴的時間(週期性):" + dateFo.format(today.getTime()));
        }


        //[方法二]Broadcast: SetClockFragment->AlarmReceiver->ClockActivity
        Intent intent = new Intent(getContext(), AlarmReceiver.class);//創建intent設置要啟動的組件
        intent.putExtra("vibrator_value", vibrator_value);//傳值給broadcast
        intent.putExtra("clockName", editText.getText().toString());
        intent.putExtra("ringUri", String.valueOf(uri_clockMedia));
        intent.putExtra("clockId", String.valueOf(clockId));//12.24 篩選clockId，使鬧鐘響鈴過後顯示停用//intent只能傳一種型別，故轉成str
        intent.putExtra("repeat", String.valueOf(repeat));//12.29 設定重複鬧鈴
        if(repeat==1){
            intent.putIntegerArrayListExtra("listWeek",listWeek);
        }
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);//獲取AlarmManager
        pendingIntent = PendingIntent.getBroadcast(getContext(), clockId, intent, PendingIntent.FLAG_UPDATE_CURRENT);//設定pendingIntent接受自訂鬧鈴廣播 //FLAG_UPDATE_CURRENT: 不存在時就建立,建立好了以後就一直用它,每次使用時都會更新pi的資料
        if (Build.VERSION.SDK_INT < 19) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);//設置單次鬧鐘的類型,啟動時間,PendingIntent對象
        }
    }

    //設置日期格式  //尚未加入"星期幾"
    private String setDateFormat(int year, int month, int day) {
        date = year + "年" + (month + 1) + "月" + day + "日";//+ Week
        return date;
    }

    //設置時間格式
    private void setTimeFormat(int hour, int minute) {
        String str_time = "%02d";
        String str_hour = String.format(str_time, hour);
        String str_minute = String.format(str_time, minute);
        time = str_hour + ":" + str_minute;
    }

}