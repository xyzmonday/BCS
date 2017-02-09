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


/**
 * 带按钮的EditText。
 */
public class AdvancedEditText extends AppCompatEditText implements TextWatcher {

    private Drawable mSuffixIcon;

    private Drawable mPrefxiIcon;


    public AdvancedEditText(final Context context) {
        super(context);
        init(context);
    }

    public AdvancedEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AdvancedEditText(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        Drawable[] drawables = getCompoundDrawables();
        Drawable leftDrawable = drawables[0];
        Drawable rightDrawable = drawables[2];
        if (leftDrawable == null) {
            //查询按钮
            leftDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.icon_search);
        }
        mPrefxiIcon = DrawableCompat.wrap(leftDrawable);
        if (rightDrawable == null) {
            rightDrawable = ContextCompat.getDrawable(getContext(), R.drawable.vector_close);
        }
        mSuffixIcon = rightDrawable;
        setSuffixIconVisible(false);
        addTextChangedListener(this);
    }

    public interface OnRichEditTouchListener {
        void onTouchSuffixIcon(View view, String text);
        void onTouchPrefixIcon(View view, String text);
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
                if (mSuffixIcon.isVisible() && x > getWidth() - getPaddingRight() - mSuffixIcon.getIntrinsicWidth()) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (mListener != null) {
                            mListener.onTouchSuffixIcon(this, getText().toString().trim());
                        }
                    }
                    return true;
                }
                if (mPrefxiIcon.isVisible() && x >= 0 && x <= mPrefxiIcon.getIntrinsicWidth() + getPaddingLeft()) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (mListener != null) {
                            mListener.onTouchPrefixIcon(this, getText().toString().trim());
                        }
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
            setPrefixIconTintColor(text.length() > 0 ? R.color.colorAccent : R.color.white);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 设置右边的icon是否可见
     *
     * @param visible
     */
    private void setSuffixIconVisible(final boolean visible) {
        mSuffixIcon.setVisible(visible, false);
        final Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(
                compoundDrawables[0],
                compoundDrawables[1],
                visible ? mSuffixIcon : null,
                compoundDrawables[3]);
    }

    /**
     * 设置左边的icon的着色
     */
    private void setPrefixIconTintColor(int tintColor) {
        DrawableCompat.setTint(mPrefxiIcon,
                ContextCompat.getColor(getContext(), tintColor));
    }

}
