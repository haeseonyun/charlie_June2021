package de.htwberlin.learningcompanion.userinterface.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


/**
 * Created by Max on 31.01.19.
 *
 * @author Max Oehme
 */
open class NonScrollViewpager(
        context: Context,
        attrs: AttributeSet?
): ViewPager(context, attrs) {

    private var isPagingEnabled = true

    constructor(context: Context) : this(context, null)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return this.isPagingEnabled && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event)
    }

    fun setPagingEnabled(b: Boolean) {
        this.isPagingEnabled = b
    }
}