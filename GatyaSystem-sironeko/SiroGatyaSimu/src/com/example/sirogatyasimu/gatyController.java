package com.example.sirogatyasimu;

import android.util.Log;
import java.util.HashMap;

/**
 * @author gamiP
 *
 */
public class gatyController {
    public String TAG = "gatyController";
    public double border;
    public static String[] job = {"剣","槍","拳","斧","弓","魔"};
    
    /**
     * コンテキスト
     * @param mborder 
     */
    public gatyController(double mborder) {
        border = mborder;
    }
    
    
    /**
     * @param border 星4の確率
     * @return int
     */
    public int Gaty() {
        double n;
        int no;
        n = Math.random();
        
        if( n <= border ){
            Log.d(TAG, " 星4  " + n );
            no = 4;
        } else if (border <= n && n <= 0.4){
            Log.d(TAG, " 星3  " + n );
            no = 3;
        } else {
            Log.d(TAG, " 星2  " + n );
            no = 2;
        }
        
        return no;
    }
    
    /**
     * 結果をHashMapに補完
     * @return HashMap
     */
    public HashMap drawGaty() {
        
        HashMap gatyResult = null;
        gatyResult = new HashMap();
        gatyResult.put("star", Gaty());
        gatyResult.put("type", selectType());
        
        return gatyResult;
    }

    /**
     * タイプを決める。1～6のランダム
     * @return String
     */
    public static String selectType(){
        int min = 0;
        int max = 5;
        int n;
        
        n = (int)( Math.random()*10000 ) % ( max-min+1 ) + min;

        return job[n];
    }

}
