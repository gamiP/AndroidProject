
package smt.project.gamebooktest;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

public class CardView extends ImageView implements AnimationListener, View.OnClickListener {
    private int imageNo; // カード番号
    private boolean state; // 裏か表か false=裏、true=表
    private boolean clear; // 存在するかしないか false=しない、true=する
    private boolean ret; // 存在するかしないか false=しない、true=する

    private int Animationtime = 200;
    private int returnAnimationtime = 700;
    //private float imagesize = 200f;
    private int imageSize_w;
    private int imageSize_h;
    private Drawable[] Imageobj = new Drawable[2];// カード画像、表、裏
    ScaleAnimation scale;
    
    public CardView(Context context, Drawable img1, Drawable img2, int imageNo) {
        super(context);

        imageSize_w = ((BitmapDrawable)img1).getBitmap().getWidth();//400;// (int)mCardGameSet.disp_w/4;
        imageSize_h = ((BitmapDrawable)img1).getBitmap().getHeight();//400;// (int)mCardGameSet.disp_h/2;
        Imageobj[0] = img1;
        Imageobj[1] = img2;

        clear = true;
        this.imageNo = imageNo;
        ret = false;
        state = false;

        setPadding(10, 10, 10, 10);//カードのパディング
        setImage();
        setLayoutParams(new LayoutParams(imageSize_w, imageSize_h));
        //setScaleType(ScaleType.FIT_XY);// XYいっぱいまで拡大
        setOnClickListener(this);
    }

    public void setImageNo(int no) {
        this.imageNo = no;
    }

    public int getImageNo() {
        return imageNo;
    }

    public void setRet(boolean ret) {
        this.ret = ret;
    }

    public void setState(boolean state) {

        this.state = state;
    }

    public void setClear(boolean clear) {
        this.clear = clear;
    }

    public boolean getState() {

        return state;
    }

    private void setImage() {

        if (clear) {
            if (!state) {
                setImageBitmap(((BitmapDrawable) Imageobj[0]).getBitmap());
            } else {
                setImageBitmap(((BitmapDrawable) Imageobj[1]).getBitmap());
            }
        } else {
            setImageBitmap(null);
        }
    }
    
    
    @Override
    public void onClick(View v) {
      Log.d("onClick", "onClick--->:");
        
            if (clear) {
                // 90°回転
                scale = new ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f, imageSize_w / 2, imageSize_h / 2);
                scale.setAnimationListener(this);
                scale.setFillAfter(true);
                scale.setFillEnabled(true);
                scale.setDuration(Animationtime);
                scale.setInterpolator(new LinearInterpolator());
                startAnimation(scale);
            }
            setClickable(false);
    }

    public void onReturnAnimation() {
        
        if (clear) {
            if (ret) {
                ret = false;
                // もう一度90°回転
                scale = new ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f, imageSize_w / 2, imageSize_h / 2);
                scale.setAnimationListener(this);
                scale.setFillAfter(true);
                scale.setFillEnabled(true);
                scale.setDuration(Animationtime + 500);
                scale.setInterpolator(new LinearInterpolator());
                startAnimation(scale);

            }
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // state = !state;//カードのフラグ
        if (clear) {
            state = !state;// カードのフラグ
            setImage();
            // もう一度90°回転
            scale = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, imageSize_w / 2, imageSize_h / 2);
            scale.setFillAfter(true);
            scale.setFillEnabled(true);
            scale.setDuration(Animationtime + 100);
            scale.setInterpolator(new LinearInterpolator());
            startAnimation(scale);
            
        }
        //setClickable(true);
    }

    public void onanime() {

        ret = false;
        scale.cancel();
        scale = null;

    }

    public void onClearAnimation() {
        
        if (clear) {
            ScaleAnimation scale = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, imageSize_w / 2,
                    imageSize_h / 2);
            // scale.setAnimationListener(this);
            scale.setFillAfter(true);
            scale.setFillEnabled(true);
            scale.setDuration(Animationtime + 200);
            scale.setInterpolator(new LinearInterpolator());
            // フェード
            AlphaAnimation feedout = new AlphaAnimation(1, 0);
            feedout.setDuration(Animationtime + 200);
            // これらのアニメーションを合成して同時進行させる
            AnimationSet anim_set = new AnimationSet(true);
            anim_set.addAnimation(scale);
            anim_set.addAnimation(feedout);
            anim_set.setFillAfter(true);
            startAnimation(anim_set);
        }
        
        
    }
    
    @Override
    public void onAnimationRepeat(Animation animation) {
        //Log.d("onAnimationRepeat", "state--->:" + state);
    }

    @Override
    public void onAnimationStart(Animation animation) {
        //Log.d("onAnimationStart", "state--->:" + state);
    }

}
