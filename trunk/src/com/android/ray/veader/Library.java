package com.android.ray.veader;

public class Library {
	   
    private String name;
    private int id ;
    private String path;
   private String description;
   private int categoryID;
    public String getname() {
        return name;
    }
    public void setname(String name) {
        this.name = name;
    }
    public int getid() {
        return this.id;
    }
    public void setid(int _id) {
        this.id = _id;
    }
    public String getpath() {
        return path;
    }
    public void setpath(String path) {
        this.path = path;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getCatagoryID() {
        return categoryID;
    }
    public void setCatagoryID(int categoryID) {
        this.categoryID = categoryID;
    }
}