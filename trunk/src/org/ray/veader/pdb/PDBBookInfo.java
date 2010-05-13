package org.ray.veader.pdb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.WeaselReader.PalmIO.PalmDocDB;
import org.ray.veader.util.EncodingConvertor;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Arrays;
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
Log.d("in set file", "");
        mPage = 0;
        FileChannel channel = new FileInputStream(pdb).getChannel();

        byte[] nameByte = new byte[32];
        channel.map(MapMode.READ_ONLY, 0, 32).get(nameByte);
        mName = new String(nameByte, mEncode).replace('_',' ').trim();

        mCount = channel.map(MapMode.READ_ONLY, 76, 2).asCharBuffer().get();
        //mCount = mCount -1;
Log.d("mCount", String.valueOf(mCount));
        int offset = 78;
        mRecodeOffset = new int[mCount];
        for (int i = 0; i < mCount; i++) {
            mRecodeOffset[i] = channel.map(MapMode.READ_ONLY, offset, 4)
                    .asIntBuffer().get();
            offset += 8;
        }
        this.mChapterTitles = getChaptersList();
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
                String rtn =  getMyText();
               
                return rtn;
            }else{
                return getPalmDoc();
            }
        }finally{
            isProgressing = false;
        }
    }

	public String[] getChaptersList() throws IOException {
		FileChannel channel = new FileInputStream(mFile).getChannel();
		Log.d("geting chapter list", "xx");
		channel.position(mRecodeOffset[0]);
		StringBuilder body = new StringBuilder();
		ByteBuffer bodyBuffer;
		int length = mRecodeOffset[1] - mRecodeOffset[0];
		int startpos = mRecodeOffset[0];
	

		bodyBuffer = channel.map(MapMode.READ_ONLY, startpos, length).order(
				ByteOrder.BIG_ENDIAN);
		byte[] tmpCache = new byte[bodyBuffer.capacity()];
		bodyBuffer.get(tmpCache);
		String str = new String(tmpCache, mEncode);

	
		str = cleantoc(str);
		str = replaceString(str);
		String[] _strlist = str.split("\n");
ArrayList al = new ArrayList(Arrays.asList(_strlist));
al.remove(0);
al.remove(0);

		return  (String[]) al.toArray(new String [al.size ()]);

	}
	private String cleantoc(String str){
		int intEob = 27;
		String aChar = new Character((char) intEob).toString();
		Log.d("chapter list!!!", str);
		str = str.replace(aChar, "\n");
		while (str.contains("\n\n")) {
			str = str.replace("\n\n", "\n");
		}
		str = str.replace(
				this.mName + "\n" + String.valueOf(this.mCount - 1) + "\n", "")
				.trim();
		return str;
	}
    public String getMyText() throws IOException {
        /* Record Header */
        int recordBegin = 78 + 8 * mCount;

        FileChannel channel = new FileInputStream(mFile).getChannel();

        channel.position(mRecodeOffset[mPage]);
        StringBuilder body = new StringBuilder();
        ByteBuffer bodyBuffer;
        if (mPage + 1 < mCount) {
        	Log.d("MPAGE?", String.valueOf(mPage));
            int length = mRecodeOffset[mPage + 1] - mRecodeOffset[mPage];
            int startpos = mRecodeOffset[mPage];
            int _offset=22;
            if (mPage==0){
            	startpos = startpos + _offset;
            	length = length -_offset;
            }
            //Log.d("startpos!!!", String.valueOf(startpos));
           // Log.d("length!!!", String.valueOf(length));
            bodyBuffer = channel.map(MapMode.READ_ONLY, startpos,
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
                if(mPage==0){
                str = this.cleantoc(str);
                }
                body.append(str);
                
                str.charAt(1);
            }
        } else {
        	Log.d("lastchapter", "lastchapter");
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
       // EncodingConvertor conv;
       // conv = new EncodingConvertor();
       // result = conv.convert(result, 0);
        return replaceString(result);
        
    }
    
    
    private String replaceString(String str){
    /*    return str.replace("\r", "").replace("¡@", " ").replace('¡w', '¡u')
      .replace('¡x', '¡v').replace('¡{', '¡y').replace('¡|', '¡z').replace('¡o','¡m').replace('¡p', '¡n')
      .replace('¡W', '¡V').replace('¡_', '(').replace('¡`', ')').replace('¡c', '{').replace('¡d', '}')
      .replace('¡k', '¡i').replace('¡l', '¡j').replace('¡s', '¡q').replace('¡t', '¡r').replace('¡d', '}')
      .replace((char) 0x1B, '\t').replace('¡U', '¡X').replace('¡g', '¡e').replace('¡h','¡f').replace("\0","");
*/
        return str.replace("\r", "").replace("¡@", " ").replace('¡w', '¡w')
        .replace('¡x', '¡x').replace('¡{', '¡y').replace('¡|', '¡z').replace('¡o','¡m').replace('¡p', '¡n')
        .replace('¡W', '¡V').replace('¡_', '(').replace('¡`', ')').replace('¡c', '{').replace('¡d', '}')
        .replace('¡k', '¡k').replace('¡l', '¡l').replace('¡s', '¡q').replace('¡t', '¡r').replace('¡d', '}')
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
