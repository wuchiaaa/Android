package com.example.myalarmclock;

import java.io.Serializable;

//放鬧鐘設定資料
public class Post implements Serializable {

    private int clockYear;
    private int clockMonth;
    private int clockDay;
    private int clockHour;
    private int clockMinute;
    private String clockName;
    private String clockVibrator;
    private String clockMedia;
    private int clockId;//alarmManager的Id
    private int clockEnable;
    private int repeat;
    private int repeatSun;
    private int repeatMon;
    private int repeatTue;
    private int repeatWed;
    private int repeatThu;
    private int repeatFri;
    private int repeatSat;

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getRepeatSun() {
        return repeatSun;
    }

    public void setRepeatSun(int repeatSun) {
        this.repeatSun = repeatSun;
    }

    public int getRepeatMon() {
        return repeatMon;
    }

    public void setRepeatMon(int repeatMon) {
        this.repeatMon = repeatMon;
    }

    public int getRepeatTue() {
        return repeatTue;
    }

    public void setRepeatTue(int repeatTue) {
        this.repeatTue = repeatTue;
    }

    public int getRepeatWed() {
        return repeatWed;
    }

    public void setRepeatWed(int repeatWed) {
        this.repeatWed = repeatWed;
    }

    public int getRepeatThu() {
        return repeatThu;
    }

    public void setRepeatThu(int repeatThu) {
        this.repeatThu = repeatThu;
    }

    public int getRepeatFri() {
        return repeatFri;
    }

    public void setRepeatFri(int repeatFri) {
        this.repeatFri = repeatFri;
    }

    public int getRepeatSat() {
        return repeatSat;
    }

    public void setRepeatSat(int repeatSat) {
        this.repeatSat = repeatSat;
    }
//    public Post(int clockEnable, int clockId, int clockYear, int clockMonth, int clockDay, int clockHour, int clockMinute, String clockName, String clockVibrator, String clockMedia) {
//        this.clockEnable = clockEnable;
//        this.clockId = clockId;
//        this.clockYear = clockYear;
//        this.clockMonth = clockMonth;
//        this.clockDay = clockDay;
//        this.clockHour = clockHour;
//        this.clockMinute = clockMinute;
//        this.clockName = clockName;
//        this.clockVibrator = clockVibrator;
//        this.clockMedia = clockMedia;
//    }

    public int getClockEnable() {
        return clockEnable;
    }

    public void setClockEnable(int clockEnable) {
        this.clockEnable = clockEnable;
    }

    public int getClockYear() {
        return clockYear;
    }

    public void setClockYear(int clockYear) {
        this.clockYear = clockYear;
    }

    public int getClockMonth() {
        return clockMonth;
    }

    public void setClockMonth(int clockMonth) {
        this.clockMonth = clockMonth;
    }

    public int getClockDay() {
        return clockDay;
    }

    public void setClockDay(int clockDay) {
        this.clockDay = clockDay;
    }

    public int getClockHour() {
        return clockHour;
    }

    public void setClockHour(int clockHour) {
        this.clockHour = clockHour;
    }

    public int getClockMinute() {
        return clockMinute;
    }

    public void setClockMinute(int clockMinute) {
        this.clockMinute = clockMinute;
    }

    public int getClockId() {
        return clockId;
    }

    public void setClockId(int clockId) {
        this.clockId = clockId;
    }

    public String getClockMedia() {
        return clockMedia;
    }

    public void setClockMedia(String clockMedia) {
        this.clockMedia = clockMedia;
    }

    public String getClockVibrator() {
        return clockVibrator;
    }

    public void setClockVibrator(String clockVibrator) {
        this.clockVibrator = clockVibrator;
    }



    public String getClockName() {
        return clockName;
    }

    public void setClockName(String clockName) {
        this.clockName = clockName;
    }

}
