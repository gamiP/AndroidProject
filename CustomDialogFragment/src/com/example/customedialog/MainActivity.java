package com.example.customedialog;

import android.view.Menu;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;


/**
 * @author smt3209
 *
 */
public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        onCreatDialog();
    }
    
    /**
     * ダイアログ作成
     */
    public void onCreatDialog() {
        
        CustomDialogFragment mCustomDialog = new CustomDialogFragment();
        mCustomDialog.show(this.getSupportFragmentManager(), "ぬるぽ");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      //getMenuInflater().inflate(R.menu.main, menu);
      return true;
    }
    
//    新しいプラットフォームに組み込まれている断片とローダのサポートは、使用する必要があります
//  getSupportFragmentManager（）とgetSupportLoaderManager（）メソッドとは対照的に、
//    このクラスを使用する場合は、それぞれ、それらの機能にアクセスします。
    //http://www.united-bears.co.jp/blog/archives/160
   //http://dev.classmethod.jp/smartphone/android/android-tips-45-custom-dialog/
}
     
       

