package smt.project.gamebooktest;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.Random;

public class GameView extends LinearLayout {
    public static final int GAME_START = 0;//ゲーム状態固定値
    public static final int GAME_PLAY = 1;//ゲーム状態固定値
    public static final int GAME_STOP = 2;
    public static final int GAME_CLEAR = 3;
    private int game_state;//ゲーム状態決定変数
    float disp_w;//画面の幅高さ
    float disp_h;
    private MainActivity mMainActivity;//Activityクラス登録
    private int sleep;//遅延時間
    //変数設定
    public static final int MAX_MAISUU = 6;//カード枚数
    public static final int SYURUI = 2;//カード１種類の枚数
    public static final int MAISUU = 3;//カード種類の数
    private Drawable[] card_img;//
    private Drawable[] card_img2;//カード裏、使わないけどジョーカー用
    private ArrayList<CardView> cardview  = new ArrayList();//カード用オブジェクト
    
    private LoopEngine loopEngine;
    private boolean tap_tap = false;
    
    private int card_count = 0;
    private int tap_count = 0;
    private int hantei_count = 0;
    private int tap_one = -1 , tap_two = -1;
    private BookActivity mBookActivity;
    private Context mContext;
    private Resources resources;
    private LinearLayout layout1;
    private LinearLayout layout2;
    private Drawable[] backImage;
    
    private int mode;
    
    public GameView(Context context) {
        super(context);
        
        mBookActivity = (BookActivity)context;
        mContext = context;
        
        setBackgroundColor(Color.argb(215, 255, 255, 255));
        resources = mContext.getResources();
        setFocusable(true);

        //レイアウトの埋め込み
        LayoutInflater.from(context).inflate(R.layout.game, this);
        resources = getResources();//画像登録準備
        
        
        backImage  = new Drawable[2];
        backImage[0] = resources.getDrawable(R.drawable.card_moji);
        backImage[1] = resources.getDrawable(R.drawable.yattane);
        onSetLayout();
        
    }
    
    /**
     * 
     */
    public void onSetLayout() {
        
        
        if(card_img2 != null)card_img2 = null;
        card_img2 = new Drawable[2];
        
//        for(int i=0; i < 2; i++) {
//            card_img2[i] = resources.getDrawable(R.drawable.card);
//        }
        
        //レイアウト生成
        for(int i = 0; i < MAX_MAISUU; i++) {
            if(i < MAISUU) {
                layout1 = (LinearLayout)findViewById(R.id.cardlayout1);
                layout1.setGravity(Gravity.CENTER);
            }else {
                layout2 = (LinearLayout)findViewById(R.id.cardlayout2);
                layout2.setGravity(Gravity.CENTER);
                
            }
        }
        
    }
    

    /**
     * 開始処理
     * @param bookid 
     */
    public void onGameSet(int bookid) {
        //カード表絵
        String imgname = null;
        //ランダム値取得
        int shuffle[] =  Shuffle();
        int s = 0;
        
        for(int i=0; i < 2; i++) {
            if(bookid <= 3) {
                card_img2[i] = resources.getDrawable(R.drawable.card_t);
            }else {
                card_img2[i] = resources.getDrawable(R.drawable.card_j);
            }
        }
        
        
        if(card_img != null)card_img = null;
        card_img = new Drawable[MAX_MAISUU];
        
       
        for(int i = 0; i<SYURUI; i++){
            for(int j = 0; j<MAISUU; j++){
                s = shuffle[j+i*MAISUU];
                
                if(s == 0 || s == 5) s = 1;
                else if(s == 1 || s == 4) s = 2;
                else if(s == 2 || s == 3) s = 3;
                
                imgname = "card" + bookid +"_" + s;
                int id = getContext().getResources().getIdentifier(imgname, "drawable", getContext().getPackageName());
                
                card_img[j+i*MAISUU] = getContext().getResources().getDrawable(id);//new BitmapDrawable(img);
            }
        }
        
        if(0 < cardview.size()) {
            cardview.clear();
        }
        //ImageViewのステータス決定
        for(int i = 0; i < MAX_MAISUU; i++) {
            //card_img[] の　値をランダムにするべき
            cardview.add(new CardView(mContext,card_img2[1],card_img[i],shuffle[i]));
        }
        
        //レイアウト再構築
        layout1.removeAllViews();
        layout2.removeAllViews();
        for(int i = 0; i < MAX_MAISUU; i++) {
            if(i < MAISUU) {
                layout1.addView(cardview.get(i));
            }else {
                layout2.addView(cardview.get(i));
            }
        }
        
        onGameStert();
    }
    
    /**
     * 
     */
    public void onGameStert() {
        
        game_state = GAME_PLAY;
        card_count = 0;
        
        loopEngine = new LoopEngine();
        loopEngine.start();
        //Log.d("onGameStert", "----onGameStert-----");
        this.setVisibility(View.VISIBLE);
    }
    
    
    //  //メインループ
    //一定時間後にupdateを呼ぶためのオブジェクト
    class LoopEngine extends Handler {
        private boolean isUpdate;

        public void start(){
            this.isUpdate = true;
            handleMessage(new Message());
        }

        public void stop(){
            this.isUpdate = false;
        }

        @Override
        public void handleMessage(Message msg) {
            this.removeMessages(0);//既存のメッセージは削除

            if(this.isUpdate){
                gameRun();//自信が発したメッセージを取得してupdateを実行
                sendMessageDelayed(obtainMessage(0), 10);//10ミリ秒後にメッセージを出力
            }
        }
    };

    
    
    /**
     * 終了処理
     */
    public boolean onGameClear() {
        
        if (loopEngine != null) {
            loopEngine.stop();
            loopEngine = null;
        }
        
//        //自身をしまう
//        this.setVisibility(View.GONE);
//        //クリア画面の表示
//        mBookActivity.setClearViewBgColor(true);//クリア画面の背景色変化
        mBookActivity.onOpenNextView(true);//
        
        if(layout1 != null) {
            layout1.removeAllViews();
        }
        if(layout2 != null) {
            layout2.removeAllViews();
        }
        if(card_img != null)card_img = null;
        
        return true;
    }


    
    //game_stateが１ならここをループ
    public void PlayDraw(){

        //カード枚表示
        for(int i=0;i<cardview.size();i++){

            if(cardview.get(i).getState()) {

                //一枚目
                if(tap_one == -1) {
                    tap_one = i;
                    tap_count++;
                }

                //二枚目
                if(tap_one != i && tap_two == -1) {
                    tap_two = i;
                    tap_count++;
                }

                if( tap_count == 2) {

                    tap_tap = true;
                    tap_count = 0;

                    break;
                }

            }else if(i == cardview.size()) {
                tap_count = 0;
            }

        }

        //２枚目タップしていればカウント＋１
        if(tap_tap == true) ++hantei_count;
        //２枚目タップしててカウントが30になれば判定
        //ここで２枚めくったらすぐ消えたりするのを防いでいます
        if(tap_tap == true && hantei_count == 30){

            Hantei();
            hantei_count = 0;


        }

    }

    /**
     * 
     */
    public void Hantei(){

        int card_one =  cardview.get(tap_one).getImageNo();
        int card_tow = cardview.get(tap_two).getImageNo();

        if((card_one + card_tow) == 5){
        //if((card_one + card_tow) == 7){
            //アニメーション
            cardview.get(tap_one).onClearAnimation();
            cardview.get(tap_two).onClearAnimation();
            //すべてのフラグをOFF
            cardview.get(tap_one).setClear(false);
            cardview.get(tap_two).setClear(false);

            cardview.get(tap_one).setState(false);
            cardview.get(tap_two).setState(false);

            tap_tap = false;

            //imgeviewのタッチフラグを戻す
            cardview.get(tap_one).setClickable(true);
            cardview.get(tap_two).setClickable(true);
            
            game_state = GAME_STOP;

        }else{
            //アニメーション
            //すべてのフラグをOFF
            cardview.get(tap_one).setRet(true);
            cardview.get(tap_two).setRet(true);

            cardview.get(tap_one).onReturnAnimation();
            cardview.get(tap_two).onReturnAnimation();

            //imgeviewのタッチフラグを戻す
            cardview.get(tap_one).setClickable(true);
            cardview.get(tap_two).setClickable(true);
            
            game_state = GAME_STOP;
        }

    }
    
    
    /**
     * 
     */
    public void gameRstart() {

        if(tap_tap) {
            //アニメーションを一度止める
            cardview.get(tap_one).onanime();
            cardview.get(tap_two).onanime();

            tap_tap = false;
            tap_one = -1;
            tap_two = -1;
            game_state = GAME_PLAY;
        }else {
            //アニメーションを一度止める
            cardview.get(tap_one).onanime();
            cardview.get(tap_two).onanime();

            tap_one = -1;
            tap_two = -1;
            card_count +=2;

            //すべてめくれたらクリア
            if(card_count == MAX_MAISUU) {
                game_state = GAME_CLEAR;
            }else {
                game_state = GAME_PLAY;
            }

        }
    }

    /**
     * 
     */
    public void gameRun() {

        //ゲーム状態によってスイッチ処理
        switch(game_state){

            case GAME_START:
                //Log.d("GAME_START", "GAME_START:");
                break;

            case GAME_PLAY:
                PlayDraw();
                //Log.d("GAME_PLAY", "GAME_PLAY:");             
                break;
            case GAME_STOP:
                gameRstart();
                //Log.d("GAME_STOP", "GAME_STOP:");
                break;
            case GAME_CLEAR:
                onGameClear();
                //Log.d("GAME_CLEAR", "GAME_CLEAR:");
                break;
        }

        try {
            Thread.sleep(sleep);
        } catch (Exception e){}
    }
    
    
    @Override
    protected void onDraw(Canvas canvas) {
        int i = 0;
        
        if(game_state == GAME_CLEAR) i = 1;//修正 2014 0924
        
        int back_w = ((BitmapDrawable)backImage[i]).getBitmap().getWidth();
        int back_h = ((BitmapDrawable)backImage[i]).getBitmap().getHeight();
        
        Rect back_src = new Rect(0, 0, back_w, back_h);
        Rect back_dst = new Rect(0, 0, getWidth(), getHeight());
        canvas.drawBitmap(((BitmapDrawable)backImage[i]).getBitmap(), back_src, back_dst, null);
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)  {
        // タッチされたらまずonInterceptTouchEventが呼ばれる
        // ここでtrueを返せば親ViewのonTouchEvent
        // ここでfalseを返せば子ViewのonClickやらonLongClickやら
        //Log.d("GameView", "-----onClick------");
        if(tap_tap) {
            return true;
        }else {
            return false;
        }
    }
    
    /**
     * カードシャッフル処理
     * @return
     */
    public int[] Shuffle(){

        int[] a = new int[MAX_MAISUU];
        Random rand = new Random();
        for (int i = 0; i < MAX_MAISUU; i++) {
            a[i] = rand.nextInt(MAX_MAISUU);
            int x = a[i];

            for (i = 0; i < MAX_MAISUU; i++)
                if (a[i] == x)       
                    break;
        }

        return a;
    }
    
    /**
     * 自身のviewをしまう
     */
    public void setGone(){
      this.setVisibility(View.GONE);
    }

}