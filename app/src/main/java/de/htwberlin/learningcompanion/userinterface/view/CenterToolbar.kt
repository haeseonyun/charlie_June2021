package de.htwberlin.learningcompanion.userinterface.view

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar

import de.htwberlin.learningcompanion.R
import org.jetbrains.anko.textAppearance

/**
 * Created by Max on 2019-06-01.
 *
 * @author Max Oehme
 */
class CenterToolbar : Toolbar {
    private var centeredTitleTextView = TextView(context)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        centeredTitleTextView.apply {
            setSingleLine()
            ellipsize = TextUtils.TruncateAt.END
            gravity = Gravity.CENTER

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setTextAppearance(R.style.TextAppearance_AppCompat_Widget_ActionBar_Title)
            } else {
                setTextAppearance(context, R.style.TextAppearance_AppCompat_Widget_ActionBar_Title)
            }

            layoutParams = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER
            }
        }

        addView(centeredTitleTextView)
    }

    override fun setTitle(@StringRes resId: Int) {
        val s = resources.getString(resId)
        title = s
    }

    override fun setTitle(title: CharSequence) {
        centeredTitleTextView.text = title
    }

    override fun getTitle(): CharSequence {
        return centeredTitleTextView.text.toString()
    }

    fun setTypeface(font: Typeface) {
        centeredTitleTextView.typeface = font
    }
}
