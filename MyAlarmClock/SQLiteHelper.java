package com.example.myalarmclock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import static com.example.myalarmclock.FeedReaderContract.FeedEntry;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHelper";
    public final static int DATABASE_Version = 3;//版本
    public final static String DATABASE_NAME = "AlarmClock.db";//資料庫


    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_Version);
    }

    //當Android載入時找不到生成的資料庫檔案，就會觸發
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
        Log.d(TAG,"資料新增(onCreate): "+ SQL_CREATE);
    }

    //資料庫結構有改變了就會觸發
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("資料庫結構有改變了, 觸發...");
        if(newVersion > oldVersion) {
            switch (oldVersion) {
                case 1:
                    upgradeVersion2(db);
                    break;
                case 2: //db裡面可能已經有週期的欄位設定值
                    upgradeVersion3(db);
                    break;
            }
        }
    }


    //新增與刪除表的語句
    private static final String SQL_CREATE =
            "CREATE TABLE IF NOT EXISTS " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FeedEntry.COLUMN_NAME_CLOCK_ENABLE + " INTEGER," +
                    FeedEntry.COLUMN_NAME_CLOCK_ID + " INTEGER," +
                    FeedEntry.COLUMN_NAME_CLOCK_YEAR + " INTEGER," +
                    FeedEntry.COLUMN_NAME_CLOCK_MONTH + " INTEGER," +
                    FeedEntry.COLUMN_NAME_CLOCK_DAY + " INTEGER," +
                    FeedEntry.COLUMN_NAME_CLOCK_HOUR + " INTEGER," +
                    FeedEntry.COLUMN_NAME_CLOCK_MINUTE + " INTEGER," +
                    FeedEntry.COLUMN_NAME_CLOCK_NAME + " TEXT," +
                    FeedEntry.COLUMN_NAME_CLOCK_VIBRATOR + " TEXT," +
                    FeedEntry.COLUMN_NAME_CLOCK_MEDIA + " TEXT,"+
                    FeedEntry.COLUMN_NAME_REPEAT + " INTEGER DEFAULT 0," +
                    FeedEntry.COLUMN_NAME_REPEAT_WEEKS + " TEXT DEFAULT '0,0,0,0,0,0,0'" +
            ")";

    private static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    private void upgradeVersion2(SQLiteDatabase db){
        String SQL_UPDATE = "ALTER TABLE "+ FeedEntry.TABLE_NAME + " add REPEAT INTEGER DEFAULT 0";
        String SQL_UPDATE_WEEKS = "ALTER TABLE "+ FeedEntry.TABLE_NAME + " add REPEAT_WEEKS TEXT DEFAULT '0,0,0,0,0,0,0'";
        db.execSQL(SQL_UPDATE);
        db.execSQL(SQL_UPDATE_WEEKS);
        Log.d(TAG,"資料庫結構 oldVersion1 更新。");
    }

    private void upgradeVersion3(SQLiteDatabase db){
        //1.將舊資料表重新命名
        db.execSQL("ALTER TABLE " + FeedEntry.TABLE_NAME + " RENAME TO myAlarmClock_TEMP");
        //2.建立新資料表
        db.execSQL("CREATE TABLE IF NOT EXISTS " + FeedEntry.TABLE_NAME + " (" +
                FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FeedEntry.COLUMN_NAME_CLOCK_ENABLE + " INTEGER," +
                FeedEntry.COLUMN_NAME_CLOCK_ID + " INTEGER," +
                FeedEntry.COLUMN_NAME_CLOCK_YEAR + " INTEGER," +
                FeedEntry.COLUMN_NAME_CLOCK_MONTH + " INTEGER," +
                FeedEntry.COLUMN_NAME_CLOCK_DAY + " INTEGER," +
                FeedEntry.COLUMN_NAME_CLOCK_HOUR + " INTEGER," +
                FeedEntry.COLUMN_NAME_CLOCK_MINUTE + " INTEGER," +
                FeedEntry.COLUMN_NAME_CLOCK_NAME + " TEXT," +
                FeedEntry.COLUMN_NAME_CLOCK_VIBRATOR + " TEXT," +
                FeedEntry.COLUMN_NAME_CLOCK_MEDIA + " TEXT,"+
                FeedEntry.COLUMN_NAME_REPEAT + " INTEGER," +
                FeedEntry.COLUMN_NAME_REPEAT_WEEKS + " INTEGER" +
                ")");
        //3.將舊的資料表新增到新的資料表
        db.execSQL("INSERT INTO " + FeedEntry.TABLE_NAME + " (" +
                FeedEntry.COLUMN_NAME_CLOCK_ENABLE + ", " +
                FeedEntry.COLUMN_NAME_CLOCK_ID + ", " +
                FeedEntry.COLUMN_NAME_CLOCK_YEAR + ", " +
                FeedEntry.COLUMN_NAME_CLOCK_MONTH + ", " +
                FeedEntry.COLUMN_NAME_CLOCK_DAY + ", " +
                FeedEntry.COLUMN_NAME_CLOCK_HOUR + ", " +
                FeedEntry.COLUMN_NAME_CLOCK_MINUTE + ", " +
                FeedEntry.COLUMN_NAME_CLOCK_NAME + ", " +
                FeedEntry.COLUMN_NAME_CLOCK_VIBRATOR + ", " +
                FeedEntry.COLUMN_NAME_CLOCK_MEDIA + ", " +
                FeedEntry.COLUMN_NAME_REPEAT +  ") " +
                "SELECT " + FeedEntry.COLUMN_NAME_CLOCK_ENABLE + ", " +
                FeedEntry.COLUMN_NAME_CLOCK_ID + ", " +
                FeedEntry.COLUMN_NAME_CLOCK_YEAR + ", " +
                FeedEntry.COLUMN_NAME_CLOCK_MONTH + ", " +
                FeedEntry.COLUMN_NAME_CLOCK_DAY + ", " +
                FeedEntry.COLUMN_NAME_CLOCK_HOUR + ", " +
                FeedEntry.COLUMN_NAME_CLOCK_MINUTE + ", " +
                FeedEntry.COLUMN_NAME_CLOCK_NAME + ", " +
                FeedEntry.COLUMN_NAME_CLOCK_VIBRATOR + ", " +
                FeedEntry.COLUMN_NAME_CLOCK_MEDIA + ", " +
                FeedEntry.COLUMN_NAME_REPEAT + " " +
                " FROM myAlarmClock_TEMP"
        );
        //4.將版本2的週期設定移到新欄位
        String selectRepeat = "SELECT (" + FeedEntry.COLUMN_NAME_REPEAT_SUN + "||','||" +
                FeedEntry.COLUMN_NAME_REPEAT_MON + "||','||" +
                FeedEntry.COLUMN_NAME_REPEAT_TUE + "||','||" +
                FeedEntry.COLUMN_NAME_REPEAT_WED + "||','||" +
                FeedEntry.COLUMN_NAME_REPEAT_THU + "||','||" +
                FeedEntry.COLUMN_NAME_REPEAT_FRI + "||','||" +
                FeedEntry.COLUMN_NAME_REPEAT_SAT + ")" +
                " AS " + FeedEntry.COLUMN_NAME_REPEAT_WEEKS +
                " FROM myAlarmClock_TEMP";
        Cursor c = db.rawQuery(selectRepeat, null);
//        ArrayList<String> repeatList = new ArrayList<>();//取得所有資料
        c.moveToFirst();
        for(int i=0; i<c.getCount(); i++){
            String repeatWeeks = c.getString(0);//從[myAlarmClock_TEMP]的表中查出所有週期設定值
//            repeatList.add(repeatWeeks);
//            System.out.println("SQLiteHelper, 資料表週期欄位更新值 repeatWeeks:" + repeatWeeks);
//            System.out.println("SQLiteHelper, 資料表週期欄位 repeatList:" + repeatList);
            ContentValues values = new ContentValues();
            values.put(FeedEntry.COLUMN_NAME_REPEAT_WEEKS,repeatWeeks);
            db.update(FeedEntry.TABLE_NAME, values, "_ID = " + (i+1), null);
            c.moveToNext();
        }
        c.close();
//        System.out.println("SQLiteHelper, 資料表週期欄位 repeatList(all):" + repeatList);
        //5.刪除舊資料表
        db.execSQL("DROP TABLE myAlarmClock_TEMP");

        Log.d(TAG,"資料庫結構 oldVersion2 更新。");
    }

}

