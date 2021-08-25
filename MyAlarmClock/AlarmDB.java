package com.example.myalarmclock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.example.myalarmclock.FeedReaderContract.FeedEntry.COLUMN_NAME_CLOCK_DAY;
import static com.example.myalarmclock.FeedReaderContract.FeedEntry.COLUMN_NAME_CLOCK_ENABLE;
import static com.example.myalarmclock.FeedReaderContract.FeedEntry.COLUMN_NAME_CLOCK_HOUR;
import static com.example.myalarmclock.FeedReaderContract.FeedEntry.COLUMN_NAME_CLOCK_ID;
import static com.example.myalarmclock.FeedReaderContract.FeedEntry.COLUMN_NAME_CLOCK_MEDIA;
import static com.example.myalarmclock.FeedReaderContract.FeedEntry.COLUMN_NAME_CLOCK_MINUTE;
import static com.example.myalarmclock.FeedReaderContract.FeedEntry.COLUMN_NAME_CLOCK_MONTH;
import static com.example.myalarmclock.FeedReaderContract.FeedEntry.COLUMN_NAME_CLOCK_NAME;
import static com.example.myalarmclock.FeedReaderContract.FeedEntry.COLUMN_NAME_CLOCK_VIBRATOR;
import static com.example.myalarmclock.FeedReaderContract.FeedEntry.COLUMN_NAME_CLOCK_YEAR;
import static com.example.myalarmclock.FeedReaderContract.FeedEntry.COLUMN_NAME_REPEAT;
import static com.example.myalarmclock.FeedReaderContract.FeedEntry.COLUMN_NAME_REPEAT_WEEKS;
import static com.example.myalarmclock.FeedReaderContract.FeedEntry.TABLE_NAME;

public class AlarmDB {

    public static String TAG = "AlarmDB";
    //    public static SQLiteDatabase db;
    public static SQLiteHelper mySQLiteHelper;

//    public static void setMySQLiteHelper(SQLiteHelper mySQLiteHelper) {
//        AlarmDB.mySQLiteHelper = mySQLiteHelper;
//    }

    //建立SQLiteOpenHelper物件              //????????
//    mySQLiteHelper = new SQLiteHelper(Context context);
//    db = mySQLiteHelper.getWritableDatabase();
    public static SQLiteDatabase db(Context context) {
        mySQLiteHelper = new SQLiteHelper(context);//初始化資料庫
        return mySQLiteHelper.getWritableDatabase();//開啟資料庫
    }

    public static ContentValues getContentValues(Post post) {
        ContentValues values = new ContentValues();//用ContentValues將所有值包起來，然後送給資料庫
        values.put(COLUMN_NAME_CLOCK_ENABLE, post.getClockEnable());
        values.put(COLUMN_NAME_CLOCK_ID, post.getClockId());
        values.put(COLUMN_NAME_CLOCK_YEAR, post.getClockYear());
        int month = post.getClockMonth();
        values.put(COLUMN_NAME_CLOCK_MONTH, month + 1);//+1:因系統月份0-11，資料庫顯示應為1-12
        values.put(COLUMN_NAME_CLOCK_DAY, post.getClockDay());
        values.put(COLUMN_NAME_CLOCK_HOUR, post.getClockHour());
        values.put(COLUMN_NAME_CLOCK_MINUTE, post.getClockMinute());
        values.put(COLUMN_NAME_CLOCK_NAME, post.getClockName());
        values.put(COLUMN_NAME_CLOCK_VIBRATOR, post.getClockVibrator());
        values.put(COLUMN_NAME_CLOCK_MEDIA, post.getClockMedia());
        values.put(COLUMN_NAME_REPEAT, post.getRepeat());//version2
        String repeatWeeks = post.getRepeatSun() + "," +
                post.getRepeatMon() + "," +
                post.getRepeatTue() + "," +
                post.getRepeatWed() + "," +
                post.getRepeatThu() + "," +
                post.getRepeatFri() + "," +
                post.getRepeatSat();
        values.put(COLUMN_NAME_REPEAT_WEEKS, repeatWeeks);//version3

        return values;
    }

    //寫入資料庫
    public static void insert(Context context, Post post) {
        ContentValues values = getContentValues(post);
        long newRowId = db(context).insert(TABLE_NAME, null, values);//寫入資料
        Log.d(TAG, "資料表插入數據是否成功(-1失敗): " + newRowId);
    }

    //查詢資料庫
    public static List<Post> showAll(Context context) {
        List<Post> clockList = new ArrayList<>();
        // 查詢資料庫並載入
        Cursor cursor = db(context).rawQuery(" SELECT * FROM " + TABLE_NAME, null);
        cursor.moveToFirst();//創建資料集後，移動到第一筆

        //呼出資料庫的資料
        for (int i = 0; i < cursor.getCount(); i++) {
            //從資料庫撈出來的資料
            int clockEnable = cursor.getInt(1);
            int clockId = cursor.getInt(2);
            int clockYear = cursor.getInt(3);
            int clockMonth = cursor.getInt(4);
            int clockDay = cursor.getInt(5);
            int clockHour = cursor.getInt(6);
            int clockMinute = cursor.getInt(7);
            String clockName = cursor.getString(8);
            String clockVibrator = cursor.getString(9);
            String clockMedia = cursor.getString(10);
            Post post = new Post();
            post.setClockEnable(clockEnable);
            post.setClockId(clockId);
            post.setClockYear(clockYear);
            post.setClockMonth(clockMonth);
            post.setClockDay(clockDay);
            post.setClockHour(clockHour);
            post.setClockMinute(clockMinute);
            post.setClockName(clockName);
            post.setClockVibrator(clockVibrator);
            post.setClockMedia(String.valueOf(clockMedia));

            //===============================
            int repeat = cursor.getInt(11);
            post.setRepeat(repeat);
            //version3 欄位縮減
            String repeatWeeks = cursor.getString(12);
            String[] repeatWeek = repeatWeeks.split(",");
            int repeatSun = Integer.parseInt(repeatWeek[0]);
            int repeatMon = Integer.parseInt(repeatWeek[1]);
            int repeatTue = Integer.parseInt(repeatWeek[2]);
            int repeatWed = Integer.parseInt(repeatWeek[3]);
            int repeatThu = Integer.parseInt(repeatWeek[4]);
            int repeatFri = Integer.parseInt(repeatWeek[5]);
            int repeatSat = Integer.parseInt(repeatWeek[6]);
            post.setRepeatSun(repeatSun);
            post.setRepeatMon(repeatMon);
            post.setRepeatTue(repeatTue);
            post.setRepeatWed(repeatWed);
            post.setRepeatThu(repeatThu);
            post.setRepeatFri(repeatFri);
            post.setRepeatSat(repeatSat);
            //===============================

            clockList.add(post);//新增
            cursor.moveToNext();//移到下一筆資料
        }
        System.out.println("AlarmDB, 呼出資料庫的資料: " + clockList);//post物件

        cursor.close();
        return clockList;
    }

    //更新資料庫(單筆鬧鐘全部)
    public static void update(Context context, Post post) {
        ContentValues values = getContentValues(post);
        db(context).update(TABLE_NAME, values, "CLOCK_ID=" + post.getClockId(), null);
    }

    //以ID更新特定資料(鬧鐘停用)
    public static void updateEnableClose(Context context, int getClockId){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_CLOCK_ENABLE, 0);//停用鬧鐘，將DB欄位的值改為0
        db(context).update(TABLE_NAME, values, "CLOCK_ID=" + getClockId, null);
    }

    //以ID更新特定資料(鬧鐘啟用)
    public static void updateEnableOpen(Context context, Post post, int getClockId){
        ContentValues values = getContentValues(post);
        values.put(COLUMN_NAME_CLOCK_ENABLE, 1);//啟用鬧鐘，將DB欄位的值改為1
        values.put(COLUMN_NAME_CLOCK_YEAR, post.getClockYear());
        values.put(COLUMN_NAME_CLOCK_MONTH, post.getClockMonth());
        values.put(COLUMN_NAME_CLOCK_DAY, post.getClockDay());
        db(context).update(TABLE_NAME, values, "CLOCK_ID=" + getClockId, null);
    }

    //以ID刪除特定資料
    public static void delete(Context context, Post post) {
        db(context).delete(TABLE_NAME, "CLOCK_ID= " + post.getClockId(), null);//whereClause 參數代表刪除的過濾條件，whereArgs 參數表示過濾條件的值
    }

//    //以ID查詢特定資料-->暫不需要
//    public static List<Post> search(Context context, int getClockId) {
//        List<Post> clockList = new ArrayList<>();
//        Cursor cursor = db(context).rawQuery(" SELECT * FROM " + TABLE_NAME + " WHERE CLOCK_ID=" + "'" + getClockId + "'", null);
//        //呼出資料庫的查詢結果
//        for (int i = 0; i < cursor.getCount(); i++) {
//            //從資料庫撈出來的資料
//            int clockEnable = cursor.getInt(1);
//            int clockId = cursor.getInt(2);
//            int clockYear = cursor.getInt(3);
//            int clockMonth = cursor.getInt(4);
//            int clockDay = cursor.getInt(5);
//            int clockHour = cursor.getInt(6);
//            int clockMinute = cursor.getInt(7);
//            String clockName = cursor.getString(8);
//            String clockVibrator = cursor.getString(9);
//            String clockMedia = cursor.getString(10);
//            Post post = new Post(clockEnable,clockId, clockYear, clockMonth, clockDay, clockHour, clockMinute, clockName,clockVibrator, clockMedia);
//            clockList.add(post);//新增
//        }
//        System.out.println("&&資料庫的查詢結果:" + clockList);//post物件
//
//        cursor.close();
//        return clockList;
//    }

}
