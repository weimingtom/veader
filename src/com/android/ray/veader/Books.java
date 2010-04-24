package com.android.ray.veader;

public class Books {
	   
    private String name;
    private String path;
   private String description;
   private int catalogid;
   private int id;
    public int getcatalogid() {
        return catalogid;
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
    public void setcatalogid(int catalogid) {
        this.catalogid = catalogid;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setid(int _id) {
        this.id = _id;
    }
    public void setname(String title) {
        this.name = title;
    }
    public void setpath(String path) {
        this.path = path;
    }
}