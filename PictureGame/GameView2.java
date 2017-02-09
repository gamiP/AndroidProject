package smt.project.gamebooktest;

import android.content.Context;
import android.content.res.Resources;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import android.view.View.OnClickListener;

public class GameView2 extends FrameLayout implements OnClickListener{
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
    public static final int MAISUU = 3;//レイアウト配置枚数
    
    private Drawable[] card_img;//
    private Drawable[] card_img2;//カード裏、使わないけどジョーカー用
    private ArrayList<CardView2> cardview  = new ArrayList();//カード用オブジェクト
    
    private LoopEngine loopEngine;
    private boolean tap_tap = false;
    private boolean judgflag = false;
    private boolean gameflag = false;
    
    public static final int HIT = 1;
    public static final int OUT = 0;
    
    private int card_count = 0;
    private int tap_count = 0;
    private int hantei_count = 0;
    private int tap_one = -1 , tap_two = -1 , tap_three = -1;
    
    private Button judgButton;
    
    private BookActivity mBookActivity;
    private Context mContext;
    private Resources resources;
    private LinearLayout layout1;
    private LinearLayout layout2;
    private Drawable[] backImage;
    
    public GameView2(Context context) {
        super(context);
        
        mBookActivity = (BookActivity)context;
        mContext = context;
        
        setBackgroundColor(Color.argb(215, 255, 255, 255));
        resources = mContext.getResources();
        setFocusable(true);

        //レイアウトの埋め込み
        LayoutInflater.from(context).inflate(R.layout.game2, this);
        resources = getResources();//画像登録準備
        
        judgButton = (Button)findViewById(R.id.clickJudg);
        judgButton.setOnClickListener(this);
        
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
        
        for(int i=0; i < 2; i++) {
            card_img2[i] = resources.getDrawable(R.drawable.card_t);
        }
        
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
        //Bitmap img = null;
        String imgname = null;
        //ランダム値取得
        int shuffle[] =  Shuffle();
        
        //f
        if(card_img != null)card_img = null;
        card_img = new Drawable[MAX_MAISUU];
        
        if(0 < cardview.size()) {
            cardview.clear();
        }
        
        for(int i = 0; i< MAISUU; i++){//1
            int s = (shuffle[i]+1);
            
            imgname = "card" + 1 +"_" + s;
            int id = getContext().getResources().getIdentifier(imgname, "drawable", getContext().getPackageName());
            
            card_img[i] = getContext().getResources().getDrawable(id);//new BitmapDrawable(img);
            
            cardview.add(new CardView2(mContext, card_img2[1] , card_img[i] , HIT ));
            
        }
        for(int i = 0; i < MAISUU; i++){//3
            int s = (shuffle[i]+1);
            
            imgname = "card" + 2 +"_" + s;
            int id = getContext().getResources().getIdentifier(imgname, "drawable", getContext().getPackageName());
            
            card_img[i + MAISUU] = getContext().getResources().getDrawable(id);//new BitmapDrawable(img);
            
            cardview.add(new CardView2(mContext, card_img2[1] , card_img[i + MAISUU] , OUT ));
        }
        
        
//        //ImageViewのステータス決定
//        for(int i = 0; i < MAX_MAISUU; i++) {
//            int s = shuffle[i];
//            
//            if(s==0 || s==1 || s== 2) s = 1;
//            
//            else s =0;
//            cardview.add(new CardView2(mContext, card_img2[1] , card_img[i] , s ));
//        }
        
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
        judgflag = false;
        gameflag = false;
        tap_tap = false;
        card_count = 0;
        
        loopEngine = new LoopEngine();
        loopEngine.start();
        
        this.setVisibility(View.VISIBLE);
    }
    
    

    /**
     * 終了処理
     */
    public boolean onGameClear() {
        
        if (loopEngine != null) {
            loopEngine.stop();
            loopEngine = null;
        }
        
        //クリアかどうか
        if(gameflag == true) {
            mBookActivity.onOpenNextView(true);//
        }
        
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
                
                //三枚目
                if(tap_one != i && tap_two != i && tap_three == -1) {
                    tap_three = i;
                    tap_count++;
                }

                //三枚クリックしたら
                if( tap_count == 3) {
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
            tap_tap = false;
            
            Hantei();
            hantei_count = 0;
        }

    }
    
    public int card_one =  -1;
    public int card_tow = -1;
    public int card_three = -1;

    /**
     * 
     */
    public void Hantei(){
        Log.d("GAMEVIEW2", "Hantei");

        for(int i = 0 ; i < cardview.size() ; i++ ) {
            cardview.get(i).setClickable(false);
        }
        
        card_one =  cardview.get(tap_one).getImageNo();
        card_tow = cardview.get(tap_two).getImageNo();
        card_three = cardview.get(tap_three).getImageNo();
        
        judgflag = true;//ボタンが押せるように

    }
    
    @Override
    public void onClick(View v) {
        if(judgflag == false)return;
        
        Log.d("onGameStert", "----clickJudg-----");
        
        if(v == judgButton){
        
            if((card_one + card_tow + card_three) == 3){
            
                gameflag = true;//クリア
            
                game_state = GAME_STOP;
                
                tap_one = -1;
                tap_two = -1;
                tap_three = -1;

            }else{
            
                game_state = GAME_STOP;
                
                tap_one = -1;
                tap_two = -1;
                tap_three = -1;
            
            }
        }

    }
    
    
    /**
     * 
     */
    public void gameRstart() {

        if(gameflag) {
//            //アニメーションを一度止める
//            cardview.get(tap_one).onAnime();
//            cardview.get(tap_two).onAnime();
//            cardview.get(tap_three).onAnime();
//            
//            tap_tap = false;
//            tap_one = -1;
//            tap_two = -1;
//            tap_three = -1;
            
            onGameClear();
            Log.d("onGameStert", "----gameClear-----");
            //game_state = GAME_PLAY;
        }else {
            
            
//            //アニメーションを一度止める
//            cardview.get(tap_one).onAnime();
//            cardview.get(tap_two).onAnime();
//            cardview.get(tap_three).onAnime();
//            
//            tap_one = -1;
//            tap_two = -1;
//            tap_three = -1;
            
            
            onGameClear();
            onGameSet(1);
            
            Log.d("onGameStert", "----gameReset-----");
//            //すべてめくれたらクリア
//            if(card_count == MAX_MAISUU) {
//                game_state = GAME_CLEAR;
//            }else {
//                game_state = GAME_PLAY;
//            }

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

        int[] a = new int[MAISUU];
        Random rand = new Random();
        for (int i = 0; i < MAISUU; i++) {
            a[i] = rand.nextInt(MAISUU);
            int x = a[i];

            for (i = 0; i < MAISUU; i++)
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
    
    
//    //座標乱数取得
//    public String[] imageRansu() {
//
//        int x_hani = 420;//840 - image_1.getWidth()/2;
//        int y_hani = 480 - image_1.getHeight();
//
//        double ransu_x = Math.random() * x_hani;
//        double ransu_y = Math.random() * y_hani;
//
//        // Math.floor(...)で、小数点以下を切り捨てる
//        ransu_x = 120 + (Math.floor(ransu_x));
//        ransu_y = (image_1.getHeight()/2) + (Math.floor(ransu_y));
//        // 30～90の範囲でランダムな整数が欲しいなら
//        // randnum = 30 + Math.floor( Math.random() * 91 );
//        
//        DecimalFormat format = new DecimalFormat("0");
//        System.out.println(format.format(ransu_x)); //1
//        System.out.println(format.format(ransu_y)); //1.1
//        
//        String[] ransu = new String[2];
//        ransu[0] = String.valueOf(format.format(ransu_x));
//        ransu[1] = String.valueOf(format.format(ransu_y));
//
//        return ransu;
//    }

}