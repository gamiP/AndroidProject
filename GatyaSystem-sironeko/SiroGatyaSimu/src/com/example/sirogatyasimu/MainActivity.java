
package com.example.sirogatyasimu;

import android.support.v7.app.ActionBarActivity;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.HashMap;


/**
 * @author gamiP
 *
 */
public class MainActivity extends ActionBarActivity implements View.OnClickListener{
    private gatyController mGatyController;
    private HashMap gatyResult = new HashMap();
    private TextView mTextView1;
    private TextView mTextView2;
    private ImageView charaView;
    public static boolean onClick = true;
    private int count;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button gatyBttton = (Button)findViewById(R.id.button1);
        gatyBttton.setOnClickListener(this);
        
        mTextView1 = (TextView)findViewById(R.id.textView1);
        mTextView2 = (TextView)findViewById(R.id.textView2);
        
        charaView = (ImageView)findViewById(R.id.characterView);
        PropertyValuesHolder animX = PropertyValuesHolder.ofFloat( "scaleX", 0.1f, 1f);
        PropertyValuesHolder animY = PropertyValuesHolder.ofFloat( "scaleY", 0.1f, 1f);
        PropertyValuesHolder holderRotaion = PropertyValuesHolder.ofFloat( "rotation", 0f, 1080f );
        //PropertyValuesHolder animTranX = PropertyValuesHolder.ofFloat("translationX", 400f, 0f);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(charaView, animX, animY,holderRotaion);
        anim.setDuration(3000);
        anim.start();
    }
    
    // センサーリソース
    @Override
    protected void onResume() {
        super.onResume();
        
        //3~5%の確率と言われている
        mGatyController = new gatyController(0.04);
    }
    
    private final Handler handl = new Handler();
    private final Runnable endresetTask = new Runnable() {
        @Override
        public void run() {
            onClick = true;
        }
    };
    
    /**
     * @param star 星
     * @param type　job
     * @param count 回数
     */
    public void updataView(int star,String type) {
        
        mTextView1.setText((count*25)+" ジュエル消費");
        
        String text = "星" + star + ":" + type +"がでました。";

        if(star == 4) {
            text = "おめでとう！" + text ;
            count = 0;
            onClick = false;
            handl.postDelayed(endresetTask, 1 * 1000);
        }
        
        mTextView2.setText(text);
    }
    
    @Override
    public void onClick(View v) {
        if(!onClick)return;
        int star = 0;
        String type = "";
        
        switch(v.getId()){
            case R.id.button1:
                
                gatyResult = mGatyController.drawGaty();
                
                star = (Integer) gatyResult.get("star");
                type = gatyResult.get("type").toString();
                count++;
                
                updataView(star,type);
                
                break;
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        
        if(keyCode==KeyEvent.KEYCODE_BACK){
                finish();
        }
        return false;
    }
    
    /*
     * 以下アクションバーメニュー
     * (非 Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        boolean ret = true;
        
        switch (item.getItemId()) {
          case R.id.action_detail:
              finish();
              break;
          case R.id.action_setting:
              finish();
              break;
          default:
              ret = super.onOptionsItemSelected(item);
              break;
        }
        
      return ret;
    }

}
