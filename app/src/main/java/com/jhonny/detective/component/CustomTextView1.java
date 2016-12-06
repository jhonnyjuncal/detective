package com.jhonny.detective.component;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by JHONNY on 26/10/2016.
 */

public class CustomTextView1 extends TextView {

    public CustomTextView1(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.otf"));
    }
}
