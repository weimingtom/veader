package com.android.ray.veader;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {

   public static final String APP_NAME = "pdb reader";  
   
   private DataHelper dataHelper;   
   
   public DataHelper getDataHelper() {
      return this.dataHelper;
   }
   
   @Override
   public void onCreate() {
      super.onCreate();
      
      //Log.d(APP_NAME, "APPLICATION onCreate");
      this.dataHelper = new DataHelper(this);      
   }

   @Override
   public void onTerminate() {
      Log.d(APP_NAME, "APPLICATION onTerminate");      
      super.onTerminate();      
   }

   public void setDataHelper(DataHelper dataHelper) {
      this.dataHelper = dataHelper;
   }
}
