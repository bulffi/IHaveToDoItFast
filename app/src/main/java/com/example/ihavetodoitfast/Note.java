package com.example.ihavetodoitfast;

import java.util.Date;
import java.util.UUID;

public class Note {
    private UUID mID;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mRelatedPerson;

    public Note(){
        this(UUID.randomUUID());
    }

    public Note(UUID uuid){
        mID = uuid;
        mDate = new Date();
    }

    public UUID getID() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getRelatedPerson(){
        return mRelatedPerson;
    }

    public void setRelatedPerson(String relatedPerson){
        mRelatedPerson = relatedPerson;
    }

    public String getPhotoFileName(){
        return "IMG_"+getID().toString()+".jpg";
    }

}
