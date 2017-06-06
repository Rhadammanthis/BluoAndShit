package mx.triolabs.pp.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import mx.triolabs.pp.R;

/**
 * Created by hugomedina on 12/28/16.
 */


public class CustomDialog extends Dialog implements View.OnClickListener{


    Button bPositive, bNegative;
    TextView tvTitle, tvContent, tvProgressContent;
    LinearLayout llProgress, llAlert;

    private Builder mBuilder;

    public CustomDialog(Builder builder) {
        super(builder.context);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mBuilder = builder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        initComponents();
    }

    private void initComponents() {

        bPositive = (Button) findViewById(R.id.dialog_b_positive);
        bNegative = (Button) findViewById(R.id.dialog_b_negative);
        tvTitle = (TextView) findViewById(R.id.dialog_tv_title);
        tvContent = (TextView) findViewById(R.id.dialog_tv_content);
        tvProgressContent = (TextView) findViewById(R.id.dialog_tv_dialog_content);
        llProgress = (LinearLayout) findViewById(R.id.dialog_ll_progress);
        llAlert = (LinearLayout) findViewById(R.id.dialog_ll_alert);

        //Construct dialog with builder's components
        tvTitle.setText(mBuilder.title);
        tvContent.setText(mBuilder.content);
        tvProgressContent.setText(mBuilder.progressContent);
        bPositive.setText(mBuilder.positiveText);
        bNegative.setText(mBuilder.negativeText);
        bPositive.setOnClickListener(mBuilder.onPositive == null ? this : mBuilder.onPositive);
        bNegative.setOnClickListener(mBuilder.onNegative == null ? this : mBuilder.onNegative);
        bNegative.setVisibility(mBuilder.negativeText == null ? View.GONE : View.VISIBLE);
        llAlert.setVisibility(mBuilder.isProgress ? View.GONE : View.VISIBLE);
        llProgress.setVisibility(mBuilder.isProgress ? View.VISIBLE : View.GONE);
        this.setCanceledOnTouchOutside(mBuilder.cancelOnTouchOutside);

    }

    @Override
    public void show(){
        super.show();
    }

    @Override
    public void onClick(View view) {
        super.dismiss();
    }

    public static class Builder{
        Context context;
        CharSequence title, content, positiveText = "OK", negativeText = null, progressContent;
        Boolean cancelOnTouchOutside = true, isProgress = false;
        View.OnClickListener onPositive = null, onNegative;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        public Builder title(CharSequence title){
            this.title = title;
            return this;
        }

        public Builder content(CharSequence content){
            this.content = content;
            return this;
        }

        public Builder onPositive(View.OnClickListener onPositive){
            this.onPositive = onPositive;
            return this;
        }

        public Builder onNegative(View.OnClickListener onNegative){
            this.onNegative = onNegative;
            return this;
        }

        public Builder positiveText(CharSequence positiveText){
            this.positiveText = positiveText;
            return this;
        }

        public Builder negativeText(CharSequence negativeText){
            this.negativeText = negativeText;
            return this;
        }

        public Builder canceledOnTouchOutside(boolean cancelOnTouchOutside){
            this.cancelOnTouchOutside = cancelOnTouchOutside;
            return this;
        }

        public Builder progress(boolean isProgress){
            this.isProgress = isProgress;
            return this;
        }

        public Builder progressContent(CharSequence content){
            this.progressContent = content;
            return this;
        }

        @UiThread
        private CustomDialog build() {
            return new CustomDialog(this);
        }

        @UiThread
        public CustomDialog show() {
            CustomDialog dialog = build();
            dialog.show();
            return dialog;
        }
    }
}
