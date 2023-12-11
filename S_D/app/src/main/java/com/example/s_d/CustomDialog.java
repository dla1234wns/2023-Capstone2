package com.example.s_d;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialog extends Dialog {

    private TextView dialogText;
    private Button okBtn;

    public CustomDialog(Context context) {
        super(context);
        init();
    }

    public CustomDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected CustomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogText = findViewById(R.id.dialog_text);
        okBtn = findViewById(R.id.ok_Btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setMessage(String message) {
        dialogText.setText(message);
    }
}