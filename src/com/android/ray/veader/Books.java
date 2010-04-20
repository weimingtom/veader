package com.android.ray.veader;

public class Books {
	   
    private String name;
    private String path;
   private String description;
   private int catalogid;
   private int id;
    public String getname() {
        return name;
    }
    public void setname(String title) {
        this.name = title;
    }
    public String getpath() {
        return path;
    }
    public int getid() {
        return this.id;
    }
    public void setid(int _id) {
        this.id = _id;
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
    public int getcatalogid() {
        return catalogid;
    }
    public void setcatalogid(int catalogid) {
        this.catalogid = catalogid;
    }
}