package comp5216.sydney.edu.au.hiketogether;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Event implements Serializable {
    private String id;
    private String creatorID;
    private String name;
    private String address;
    private String description;
    private int difficulty;
    private int teamSize;
    private long createTimeStamp;
    private ArrayList<String> members;
    private ArrayList<String> imageURLs;

    // 构造函数
    public Event() {
        // 默认构造函数，用于 Firestore 数据库的反序列化
    }

    public Event(String id, String creatorID, String name, String address, String description, int difficulty, int teamSize, long createTimeStamp, ArrayList<String> members, ArrayList<String> imageURLs) {
        this.id = id;
        this.creatorID = creatorID;
        this.name = name;
        this.address = address;
        this.description = description;
        this.difficulty = difficulty;
        this.teamSize = teamSize;
        this.createTimeStamp = createTimeStamp;
        this.members = members;
        this.imageURLs = imageURLs;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getCreatorID() { return creatorID; }
    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public int getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getTeamSize() {
        return teamSize;
    }
    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    public long getCreateTimeStamp() {
        return createTimeStamp;
    }
    public void setCreateTimeStamp(long createTimeStamp) {
        this.createTimeStamp = createTimeStamp;
    }

    public ArrayList<String> getMembers() {
        return members;
    }
    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public ArrayList<String> getImageURLs() {
        return imageURLs;
    }
    public void setImageURLs(ArrayList<String> imageURLs) {
        this.imageURLs = imageURLs;
    }

}

