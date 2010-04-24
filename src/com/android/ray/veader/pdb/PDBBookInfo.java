package com.android.ray.veader.pdb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.WeaselReader.PalmIO.PalmDocDB;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.zip.DataFormatException;
import java.util.zip.InflaterInputStream;

/*
 * #define dmDBNameLength 32/* 31 chars + 1 null terminator
 * 
 * struct pdb_header { // 78 bytes total char name[ dmDBNameLength ]; DWord
 * attributes; Word version; DWord create_time; DWord modify_time; DWord
 * backup_time; DWord modificationNumber; DWord appInfoID; DWord sortInfoID;
 * char type[4]; char creator[4]; DWord id_seed; DWord nextRecordList; Word
 * numRecords; };
 */
public class PDBBookInfo extends AbstractBookInfo {
    public int mCount;
    public int[] mRecodeOffset;
    public boolean isProgressing;

    public PDBBookInfo(long id){
        super(id);
    }

    //@Override
    public void setFile(File pdb) throws IOException {
        mFile = pdb;

        mPage = 0;
        FileChannel channel = new FileInputStream(pdb).getChannel();

        byte[] nameByte = new byte[32];
        channel.map(MapMode.READ_ONLY, 0, 32).get(nameByte);
        mName = new String(nameByte, mEncode).replace('_',' ').trim();

        mCount = channel.map(MapMode.READ_ONLY, 76, 2).asCharBuffer().get();
Log.d("mCount", String.valueOf(mCount));
        int offset = 78;
        mRecodeOffset = new int[mCount];
        for (int i = 0; i < mCount; i++) {
            mRecodeOffset[i] = channel.map(MapMode.READ_ONLY, offset, 4)
                    .asIntBuffer().get();
            offset += 8;
        }
        
        channel.close();
        

    }
   
    //@Override
    public int getPageCount() {
        return mCount;
    }
    
    boolean isStop;
    public  void stop(){
        isStop = true;
    }
    
    public  boolean isProgressing(){
        return isProgressing;
    }
    
    
    public String getText() throws IOException, DataFormatException {
        isProgressing = true;
        try{
            if(mFormat <2){
                return getMyText();
            }else{
                return getPalmDoc();
            }
        }finally{
            isProgressing = false;
        }
    }
    
    public String getMyText() throws IOException {
        /* Record Header */
        int recordBegin = 78 + 8 * mCount;

        FileChannel channel = new FileInputStream(mFile).getChannel();

        channel.position(mRecodeOffset[mPage]);
        StringBuilder body = new StringBuilder();
        ByteBuffer bodyBuffer;
        if (mPage + 1 < mCount) {
            int length = mRecodeOffset[mPage + 1] - mRecodeOffset[mPage];
            bodyBuffer = channel.map(MapMode.READ_ONLY, mRecodeOffset[mPage],
                    length).order(ByteOrder.BIG_ENDIAN);
            byte[] tmpCache = new byte[bodyBuffer.capacity()];
            bodyBuffer.get(tmpCache);
            if(mFormat==1){
                byte[] ttt = new byte[8192];
                InflaterInputStream input = new InflaterInputStream(new ByteArrayInputStream(tmpCache));
                int c=0;
                while((c = input.read(ttt))>0){
                    String str = new String(ttt,0,c, mEncode);
                    body.append(replaceString(str));
                    if(isStop){
                        isStop = false;
                        break;
                    }
                    
                }
                input.close();
            }else{
                String str = new String(tmpCache,mEncode);
                body.append(str);
            }
        } else {
            bodyBuffer = ByteBuffer.wrap(new byte[8192]);
            int idx;
            while ((idx = channel.read(bodyBuffer)) > 0) {
                String str = new String(bodyBuffer.array(), mEncode);
                body.append(str);
                if(isStop){
                    isStop = false;
                    break;
                }
            }
        }
        channel.close();

        
        return filter(body);

    }
    
    
    
    public String getPalmDoc() throws IOException, DataFormatException {
        PalmDocDB palmDoc = new PalmDocDB(mFile,mEncode);
        mCount = palmDoc.getNumDataRecords();
        Log.d("getpalmdoc, page:",String.valueOf(mPage) );
        String result = palmDoc.readTextRecord(mPage);
        palmDoc.close();
        return result;

    }
    
    /**
     * filter palm doc tag
     */
    private String filter(StringBuilder body){
        int begin=-1;
        int c=-1;

        while((c = body.indexOf("\\v",c+1)) >0 ){
            if(begin>-1){
                body.delete(begin, c);
                begin = -1;
            }else{
                begin = c;
            }
        }
       
        c=-1;
        while((c = body.indexOf("\\a"))>-1){
            char myChar = (char)Integer.parseInt(body.substring(c+2, c+5));
            body.replace(c, c+5,  String.valueOf(myChar));
        }
        String result = body.toString();
        result = result.replaceAll("\\\\Sd=\\\".*\\\"|\\\\(Sd|Fn|Cn|[TwQq])=\".*\"|\\\\((Sp|Sb|Sd|Fn)|[pxcriuovtnsbqlBkI\\-])", "")
        .replace("\\\\", "\\");
        return replaceString(result);
        
    }
    
    
    private String replaceString(String str){
        return str.replace("\r", "").replace("¡@", " ").replace('¡w', '¡u')
      .replace('¡x', '¡v').replace('¡{', '¡y').replace('¡|', '¡z').replace('¡o','¡m').replace('¡p', '¡n')
      .replace('¡W', '¡V').replace('¡_', '(').replace('¡`', ')').replace('¡c', '{').replace('¡d', '}')
      .replace('¡k', '¡i').replace('¡l', '¡j').replace('¡s', '¡q').replace('¡t', '¡r').replace('¡d', '}')
      .replace((char) 0x1B, '\t').replace('¡U', '¡X').replace('¡g', '¡e').replace('¡h','¡f').replace("\0","");

     }

    public Bitmap getImage() throws IOException {
        /* Record Header */
        int recordBegin = 78 + 8 * mCount;
        Bitmap result =null;
        FileChannel channel = new FileInputStream(mFile).getChannel();

        channel.position(mRecodeOffset[mPage]);

        ByteBuffer bodyBuffer;
        if (mPage + 1 < mCount) {
            int length = mRecodeOffset[mPage + 1] - mRecodeOffset[mPage];
            bodyBuffer = channel.map(MapMode.READ_ONLY, mRecodeOffset[mPage],
                    length);
            byte[] tmpCache = new byte[bodyBuffer.capacity()];
            bodyBuffer.get(tmpCache);
            FileOutputStream o = new FileOutputStream("/sdcard/test.bmp");
            o.write(tmpCache);
            o.flush();
            o.getFD().sync();
            o.close();

            
            result = BitmapFactory.decodeByteArray(tmpCache, 0, length);
        } else {
//            bodyBuffer = ByteBuffer.wrap(new byte[8192]);
//            int idx;
//            while ((idx = channel.read(bodyBuffer)) > 0) {
//                String str = new String(bodyBuffer.array(), mEncode);
//                body.append(replaceString(str));
//            }
        }
        
        
        channel.close();
        return result;
    }

    //@Override
    public boolean supportFormat() {
        return true;
    }


}
