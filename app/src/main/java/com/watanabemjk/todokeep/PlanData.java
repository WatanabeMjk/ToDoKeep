package com.watanabemjk.todokeep;

public class PlanData {
    protected int id;
    int year;
    int month;
    int day;
    int hour;
    int minute;
    private String daysOfWeek;
    protected String title;
    protected String detail;
    private int backColor;

    PlanData(int id, int year, int month, int day, int hour, int minute, String daysOfWeek, String title, String detail, int backColor){
        this.id = id;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.daysOfWeek = daysOfWeek;
        this.title = title;
        this.detail = detail;
        this.backColor = backColor;
    }

    int getId(){return id;}

    int getYear() {return year;}

    int getMonth() {return month;}

    int getDay() {return day;}

    int getHour() {return hour;}

    int getMinute() {return minute;}

    String getDaysOfWeek() {return daysOfWeek;}

    String getTitle() {return title;}

    String getDetail() {return detail;}

    int getBackColor() {return backColor;}
}
