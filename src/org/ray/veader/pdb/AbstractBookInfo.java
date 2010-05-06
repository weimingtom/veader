package org.ray.veader.pdb;

import java.io.File;
import java.io.IOException;


public abstract class AbstractBookInfo {
    public long mID;
    public String mName;
    public int mPage;
    public File mFile;
    public String mEncode;
    public int mFormat;
    public String[] mChapterTitles;
    private String path;
   private String description;
   private int catalogid;
 
    public AbstractBookInfo(long id){
        mID = id;
    }
    public String getname() {
        return mName;
    }
   
    public String getpath() {
        return path;
    }
    public long getid() {
        return this.mID;
    }
    public void setid(int _id) {
        this.mID = _id;
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
    public String[] getChapters() {
        return this.mChapterTitles;
    }
   
    public void setEncode( String encode){
        mEncode = encode;
    }
    
    public abstract boolean supportFormat();
    
    public void setName(String name){
        mName = name;
    }
    
    public  abstract void setFile(File pdb) throws IOException;
    
    public void setFormat(int format){
        mFormat = format;
    }
    
    public void setPage(int page) {
        mPage = page;
    }

    public boolean hasNextPage() {
        return mPage< getPageCount()-1;
    }
    
    public abstract int getPageCount();

    public boolean hasPrevPage() {
        return mPage > 0;
    }

    public void nextPage() {
        setPage(mPage + 1);
    }

    public void prevPage() {
        setPage(mPage - 1);
    }

    public abstract CharSequence getText() throws Exception;
    
    public abstract boolean isProgressing();
    
    public abstract void stop();
    
    public static AbstractBookInfo newBookInfo(File f,long id){
       String name =  f.getName();
       if(name.toLowerCase().endsWith("pdb")){
           return new PDBBookInfo(id);
       }else if(name.toLowerCase().endsWith("txt")){
           return new TxtBookInfo(id);
       }else if(name.toLowerCase().endsWith("htm") || name.toLowerCase().endsWith("html")){
           return new HtmlBookInfo(id);
       }
       
       return new PDBBookInfo(id);
    }
 
}
