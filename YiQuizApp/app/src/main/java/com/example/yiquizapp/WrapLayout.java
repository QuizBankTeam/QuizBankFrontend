package com.example.yiquizapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class WrapLayout extends ViewGroup {

    public WrapLayout(Context context) {
        super(context);
    }

    public WrapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int maxHeight = getWrapHeight(widthMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), maxHeight);
    }

    private int getWrapHeight(int widthMeasureSpec) {
        WrapLayoutParams params;
        int row = 1;
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec); //layout擁有最大的width
        int occupancyWidth = 0; //佔用的width
        int occupancyHeight = 0; //佔用的height

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            params = (WrapLayoutParams) child.getLayoutParams();
//            除了本身的width,還需加上左右兩邊的margin
            int childWidth = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
//             除了本身的height,還需加上上下兩邊的margin
            int childHeight = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;
            occupancyWidth += childWidth;
//            if 超過最大的寬度,將child分配到下一行
            if (occupancyWidth > maxWidth) {
                row++;
                occupancyWidth = childWidth;
            }
            occupancyHeight = childHeight * row;
        }

        return occupancyHeight;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        WrapLayoutParams params;
        int row = 0; //因為是座標,排滿一排才增加一行,因此預設為0
        int occupancyWidthPostion = left; //目前佔用的左座標
        int occupancyHeightPostion = top; // 目前佔用的top座標
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            params = (WrapLayoutParams) child.getLayoutParams(); //為了取得邊界
            int childWidth = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            int childHeight = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;
            if (occupancyWidthPostion + childWidth > right) {
                row++;
                occupancyWidthPostion = left;
            }
            occupancyHeightPostion = row * childHeight;
            /**
             * layout(left,top,right,bottom)
             * left = 左邊座標＝目前佔用的左座標+leftmargin
             * top(距離parent top的座標) = 目前佔用的top座標(隨著row增加會增加)
             * right ＝左邊座標＋child's width
             * bottom = 目前佔用的top座標＋child's height
             */
            child.layout(occupancyWidthPostion + params.leftMargin, occupancyHeightPostion,
                    occupancyWidthPostion + childWidth, occupancyHeightPostion + childHeight);
            occupancyWidthPostion += childWidth;
        }
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new WrapLayoutParams(getContext(), attrs);
    }

    public class WrapLayoutParams extends ViewGroup.MarginLayoutParams {

        public WrapLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }
    }

}