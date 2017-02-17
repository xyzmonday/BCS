package com.richfit.common_lib.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.richfit.common_lib.R;
import com.richfit.common_lib.utils.L;


/**
 * 带按钮的EditText。
 */
public class RichEditText extends AppCompatEditText implements TextWatcher {

    private Drawable mClearTextIcon;


    public RichEditText(final Context context) {
        super(context);
        init(context);
    }

    public RichEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RichEditText(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        Drawable drawable = getCompoundDrawables()[2];
        if (drawable == null)
            drawable = ContextCompat.getDrawable(context, R.mipmap.icon_find);
        //DrawableCompat，包装drawable使得它可以使用hintColor。
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, getCurrentHintTextColor());
        mClearTextIcon = wrappedDrawable;
        mClearTextIcon.setBounds(0, 0, mClearTextIcon.getIntrinsicHeight(), mClearTextIcon.getIntrinsicHeight());
        setSuffixIconVisible(true);
        addTextChangedListener(this);
    }

    public interface OnRichEditTouchListener {
        void onTouchRichEdit(View view, String text);
    }

    private OnRichEditTouchListener mListener;

    public void setOnRichEditTouchListener(OnRichEditTouchListener listener) {
        this.mListener = listener;
    }

    /**
     * 注意onTouchListener的onTouch事件先于onTouchEvent方法执行
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                final int x = (int) event.getX();
                if (mClearTextIcon.isVisible() && x > getWidth() - getPaddingRight() - mClearTextIcon.getIntrinsicWidth() * 2) {
                    if (mListener != null && isEnabled()) {
                        mListener.onTouchRichEdit(this, getText().toString().trim());

                    }
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public final void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (isFocused()) {
            setSuffixIconVisible(text.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 设置Icon是否可见
     *
     * @param visible
     */
    public void setSuffixIconVisible(final boolean visible) {
        mClearTextIcon.setVisible(visible, false);
        final Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(
                compoundDrawables[0],
                compoundDrawables[1],
                visible ? mClearTextIcon : null,
                compoundDrawables[3]);
    }

}
