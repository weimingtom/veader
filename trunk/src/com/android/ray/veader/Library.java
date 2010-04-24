package com.android.ray.veader;

public class Library {
	   
    private String name;
    private int id ;
    private String path;
   private String description;
   private int categoryID;
    public int getCatagoryID() {
        return categoryID;
    }
    public String getDescription() {
        return description;
    }
    public int getid() {
        return this.id;
    }
    public String getname() {
        return name;
    }
    public String getpath() {
        return path;
    }
    public void setCatagoryID(int categoryID) {
        this.categoryID = categoryID;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setid(int _id) {
        this.id = _id;
    }
    public void setname(String name) {
        this.name = name;
    }
    public void setpath(String path) {
        this.path = path;
    }
}