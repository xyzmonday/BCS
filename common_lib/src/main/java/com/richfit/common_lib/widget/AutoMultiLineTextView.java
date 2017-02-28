package com.richfit.common_lib.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.richfit.common_lib.R;

public class AutoMultiLineTextView extends android.support.v7.widget.AppCompatTextView {
    private static final String TAG = "yff";
    private static final String namespace = "http://schemas.android.com/apk/res/android";
    private String text;
    private float textSize;
    private Paint paint1 = new Paint();
    private float paddingLeft;
    private float paddingRight;
    private float textShowWidth;
    private int textColor;


    public AutoMultiLineTextView(Context context) {
        this(context, null);
    }

    public AutoMultiLineTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }


    public AutoMultiLineTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        text = attrs.getAttributeValue(
                namespace, "text");
        if (TextUtils.isEmpty(text)) {
            text = "";
        }
        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.AutoMultiLineTextView);

        paddingLeft = attributes.getDimension(R.styleable.AutoMultiLineTextView_m_paddingLeft, 0);
        paddingRight = attributes.getDimension(R.styleable.AutoMultiLineTextView_m_paddingRight, 0);
        textColor = attributes.getColor(R.styleable.AutoMultiLineTextView_m_textColor, Color.GREEN);
        //默认大小15sp
        textSize = attributes.getDimension(R.styleable.AutoMultiLineTextView_m_textSize,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));

        attributes.recycle();
        paint1.setTextSize(textSize);
        paint1.setColor(textColor);
        paint1.setAntiAlias(true);
        textShowWidth = ((Activity) context).getWindowManager()
                .getDefaultDisplay().getWidth()
                - paddingLeft - paddingRight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int lineCount = 0;
        char[] textCharArray = text.toCharArray();
        float drawWidth = 0;
        float charWidth;
        for (int i = 0; i < textCharArray.length; i++) {
            charWidth = paint1.measureText(textCharArray, i, 1);

            if (textShowWidth - drawWidth < charWidth) {
                lineCount++;
                drawWidth = 0;
            }
            canvas.drawText(textCharArray, i, 1, paddingLeft + drawWidth,
                    (lineCount + 1) * textSize, paint1);
            drawWidth += charWidth;
        }
        setHeight((lineCount + 1) * (int) textSize + 5);
    }


    public void setMultiText(CharSequence text) {
        this.text = text.toString();
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }
}
