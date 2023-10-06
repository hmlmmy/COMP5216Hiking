package comp5216.sydney.edu.au.hiketogether;

import java.util.ArrayList;

public class Event {
    private String id;
    private String name;
    private String address;
    private int teamSize;
    private ArrayList<String> members;
    private String picture;

    // 构造函数
    public Event() {
        // 默认构造函数，用于 Firestore 数据库的反序列化
    }

    public Event(String id, String name, String address, int teamSize, ArrayList<String> members, String picture) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.teamSize = teamSize;
        this.members = members;
        this.picture = picture;
    }

    // Getter 方法
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public String getPicture() {
        return picture;
    }

    // Setter 方法
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}

