package com.example.marcotoni.pihome;

public class EventListItem {
    private int id;
    private String name;
    private String description;
    private String type;
    private boolean isNotified;
    private boolean isChecked = false;

    public boolean isChecked() {
        return isChecked;
    }
    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isNotified() {
        return isNotified;
    }
    public void setIsNotified(int isNotified) {
        if(isNotified == 1) this.isNotified = true;
        else this.isNotified = false;
    }

    public int getID(){ return id; }
    public String getTitle() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getType(){ return type; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setType(String type) {
        this.type = type;
    }
}