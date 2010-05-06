package org.ray.veader;

public class clsChapter {
	   
    private String name, chaptertitle;
    private int id ;

    public int getid() {
        return this.id;
    }
    public  clsChapter(String _name, String _title){
    	this.name = _name;
    	this.chaptertitle = _title;
    }
    public String getname() {
        return name;
    }
  public String getTitle() {
        return chaptertitle;
    }
    public void setid(int _id) {
        this.id = _id;
    }
    public void setTitle(String title) {
        this.chaptertitle = title;
    }

}