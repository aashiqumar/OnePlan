package com.store.oneplan.TimeTable;

public class TimeTableModal {

    private String Title;
    private String Description;
    private String Time;
    private String Date;

    public TimeTableModal() {
    }

    public TimeTableModal(String title, String description, String time, String date) {
        Title = title;
        Description = description;
        Time = time;
        Date = date;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
