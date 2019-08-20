package com.sayan.sdk.mediacollector.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.sayan.sdk.mediacollector.R;
import com.sayan.sdk.mediacollector.exceptions.ResourceNotFoundException;

public class ResourceUtil {

    public static int fetchAccentColor(Context context) {
        TypedValue typedValue = new TypedValue();

        TypedArray array = context.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        if (array == null){
            throw new ResourceNotFoundException("The color resource R.color.colorAccent not found");
        }
        int color = array.getColor(0, 0);

        array.recycle();

        return color;
    }

    @ColorInt
    public static int getThemeColor
            (
                    @NonNull final Context context,
                    @AttrRes final int attributeColor
            )
    {
        final TypedValue value = new TypedValue();
        context.getTheme ().resolveAttribute (attributeColor, value, true);
        return value.data;
    }
}
