/*
* 寫入SQLite需用到的各種參數
* */
package com.example.myalarmclock;

import android.provider.BaseColumns;

public class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "myAlarmClock";
        //設定鬧鐘資料表_表格欄位名稱
        public static final String COLUMN_NAME_CLOCK_ENABLE = "CLOCK_ENABLE";
        public static final String COLUMN_NAME_CLOCK_ID = "CLOCK_ID";
        public static final String COLUMN_NAME_CLOCK_YEAR = "CLOCK_YEAR";
        public static final String COLUMN_NAME_CLOCK_MONTH = "CLOCK_MONTH";
        public static final String COLUMN_NAME_CLOCK_DAY = "CLOCK_DAY";
        public static final String COLUMN_NAME_CLOCK_HOUR = "CLOCK_HOUR";
        public static final String COLUMN_NAME_CLOCK_MINUTE = "CLOCK_MINUTE";
        public static final String COLUMN_NAME_CLOCK_NAME = "CLOCK_NAME";
        public static final String COLUMN_NAME_CLOCK_VIBRATOR = "CLOCK_VIBRATOR";
        public static final String COLUMN_NAME_CLOCK_MEDIA = "CLOCK_MEDIA";

        //version3
        public static final String COLUMN_NAME_REPEAT = "REPEAT";
        public static final String COLUMN_NAME_REPEAT_WEEKS = "REPEAT_WEEKS";//新增的欄位

        //version2->要刪除的欄位
        public static final String COLUMN_NAME_REPEAT_SUN = "REPEAT_SUN";
        public static final String COLUMN_NAME_REPEAT_MON = "REPEAT_MON";
        public static final String COLUMN_NAME_REPEAT_TUE = "REPEAT_TUE";
        public static final String COLUMN_NAME_REPEAT_WED = "REPEAT_WED";
        public static final String COLUMN_NAME_REPEAT_THU = "REPEAT_THU";
        public static final String COLUMN_NAME_REPEAT_FRI = "REPEAT_FRI";
        public static final String COLUMN_NAME_REPEAT_SAT = "REPEAT_SAT";

    }
}
