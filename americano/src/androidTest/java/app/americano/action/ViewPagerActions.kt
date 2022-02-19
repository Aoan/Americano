package app.americano.action

import android.view.View
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher

object ViewPagerActions {

    fun waitForPage(matcher: Matcher<View>): ViewAction {

        return object : ViewAction {

            override fun getConstraints() = allOf(isAssignableFrom(ViewPager::class.java), isDisplayed())

            override fun getDescription() = "wait for page $matcher"

            override fun perform(uiController: UiController, view: View) {
                view as ViewPager
                if (!matcher.matches(view)) {
                    val callback = PageChangeCallback(matcher, view)
                    try {
                        IdlingRegistry.getInstance().register(callback)
                        view.addOnPageChangeListener(callback)
                        uiController.loopMainThreadUntilIdle()
                    } finally {
                        IdlingRegistry.getInstance().unregister(callback)
                        view.removeOnPageChangeListener(callback)
                    }
                }
            }
        }
    }

    private class PageChangeCallback(
        private val matcher: Matcher<View>,
        private val view: View,
    ) : IdlingResource, OnPageChangeListener {

        private lateinit var callback: IdlingResource.ResourceCallback
        private var matched = false

        override fun getName() = "Page change callback"

        override fun isIdleNow() = matched

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
            this.callback = callback
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

        override fun onPageSelected(position: Int) = Unit

        override fun onPageScrollStateChanged(state: Int) {
            if (state == SCROLL_STATE_IDLE) {
                matched = matcher.matches(view)
                callback.onTransitionToIdle()
            }
        }

    }
}