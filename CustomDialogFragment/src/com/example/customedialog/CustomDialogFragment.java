package com.example.customedialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author smt3209
 *
 */
public class CustomDialogFragment extends DialogFragment {
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
         
        Dialog dialog = new Dialog(getActivity());
        // タイトル非表示
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // フルスクリーン
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.dialog);
        // 背景を透明にする
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        // ダイアログ外タップで消えないように設定  
        dialog.setCanceledOnTouchOutside(false); 
        
        // OK ボタンのリスナ
        dialog.findViewById(R.id.positive_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        // ×ボタンのリスナ
        dialog.findViewById(R.id.close_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        
        return dialog;
    }
}
