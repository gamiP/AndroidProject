package com.mya.novelproject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

/**
 * @author gamiP
 *
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final String file_name = "noveldata.txt";
    private ArrayList<String> textbox = new ArrayList<String>();
    private int index;
    private static int TIMEOUT_MESSAGE = 1;
    private static int INTERVAL = 1;//速度閾値
    private TextView serifText;
    private TextView nameText;
    private Handler readHandler = null;
    int readcount = 0;
    private String targetname = "";
    private String text = "";
    private String put_word = "";
    private String put_text = "";
    
    private SharedPreferences pref;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        nameText = (TextView) findViewById(R.id.nametext);
        serifText = (TextView) findViewById(R.id.seriftext);
        //プリファレンス
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //txtから全文読み込み
        try {
            AssetManager as = getResources().getAssets();   
            InputStream is = as.open(file_name);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
            
            //一行区切り
            String str;
            while((str = bufferedreader.readLine()) != null){
                textbox.add(str);
            }
            
//            for(int i = 0;i<textbox.size();i++) {
//                Log.d(TAG, "読み込み完了-->" + i +":"+ textbox.get(i));
//            }
            
            bufferedreader.close();
             
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e){
        }
        
        //textファイル修正
        textbox = TextResult(textbox);
        
        //初期にテキストセットしていますが不要です。
        nameText.setText("名前とか");
        serifText.setText("本文");
        //テキスト表示の枠のクリックのみ
        serifText.setOnClickListener(new ClickListener());
        
        index = 0;
    }
    
    private ArrayList<String> TextResult(ArrayList<String> TextBox){
        
        ArrayList<String> nullBox = new ArrayList<String>();
        ArrayList<String> newTextBox = new ArrayList<String>();
        
        String f1 = "##";
        boolean nameflag = true;
        /*
        *今回はフラグにできる文字は２文字にしています。ここは３文字でも１文字でも可能
        *テキストに数字を入れてフラグ管理とかしやすくできるんじゃないかな？
        */
        String name = "";
        String bun = "";
        String zenbun = "";
        String flag = "";
        
        //改行（null）を排除
        for (int i=0; i < TextBox.size();i++) {
            if (0 < TextBox.get(i).length()) nullBox.add(TextBox.get(i));
            //Log.d(TAG, "文字列の長さ-->" + TextBox.get(i).length());
        }

        //配列作成
        for(int i = 0; i < nullBox.size();i++) {
            
            nameflag = true;
            
            if (nullBox.get(i).matches(".*" + f1 + ".*")) {
                name = nullBox.get(i).replace(f1,"");
                //## フラグ 名前の変更。
                flag = ""; 
                nameflag = false;
            }
            else {
                //上２文字はフラグ様なので取り除いておく
                flag = nullBox.get(i).substring(0,2);
                bun = nullBox.get(i).replace(flag,"");
            }
            
            if(nameflag) {
                zenbun = name + "#" + bun;
                Log.d(TAG, "結合本文-->" + i +"行:"+ zenbun);
                newTextBox.add(zenbun);
            }
        }
        
        if(0 < nullBox.size()) {
            nullBox.clear();
            nullBox = null;
        }
        
        return newTextBox;
    }
    

    //クリックされた時の処理
    class ClickListener implements View.OnClickListener {
        public void onClick(View v) {
            
            //ループ処理
            if(index == textbox.size()) {
                index = 0;  
            }
            
            if(readHandler == null) {
                
                SplitSentenceName(textbox.get(index));
                nameText.setText(Html.fromHtml(targetname));
                
                readHandler = new ReadHandler();
                readHandler.sendEmptyMessage(TIMEOUT_MESSAGE);

            }else{
                //テキストを呼んでいるときにタップで全文表示
                readHandler = null;
                //Log.d(TAG, "elseチェック-->" + text);
                serifText.setText(text);
            }

        }
    }
    
   
    /**
     * @param v
     */
    public void onClickSaveButton(View v) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("INDEX", (index-1));
        editor.commit();
    }
    /**
     * @param v
     */
    public void onClickloadButton(View v) {
        //index = 7;
        index = pref.getInt("INDEX", 0);
        
        SplitSentenceName(textbox.get(index));
        
        nameText.setText(Html.fromHtml(targetname));
        serifText.setText(text);
        //indexはここで増やす
        index++;
    }
    
    /**
     * @param zenbun 整形済み一文
     */
    public void SplitSentenceName(String zenbun) {
        // 検索文字より前の文字列取り出し
        int in = zenbun.indexOf("#");
        //文から名前を取得
        String name = zenbun.substring(0,in);
        //文から名前の削除
        text = zenbun.replaceAll(name+"#","");
        targetname = name;
    }
    
    
    /**
     * @author gamiP
     * 文字列を一文字ずつ出力するハンドラ
     */
    public class ReadHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            // 文字列を配列に１文字ずつセット
            
            char data[] = text.toCharArray();
             
            // 配列数を取得
            int arr_num = data.length;
            
            if(readHandler != null && readcount < arr_num){
                
                if (msg.what == TIMEOUT_MESSAGE) {
                    put_word = String.valueOf(data[readcount]);
                    put_text = put_text + put_word;
                    
                    serifText.setText(put_text);
                    readHandler.sendEmptyMessageDelayed(TIMEOUT_MESSAGE, INTERVAL * 50);
                    readcount++;
                }else{
                    super.dispatchMessage(msg);
                }
            }else{
                // ハンドラ初期化
                readcount = 0;
                put_word = "";
                put_text = "";
                readHandler = null;
                //indexはここで増やす
                index++;
            }
        }
    }
    
    //戻り
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}

