package app.americano.action

import android.view.View
import android.view.ViewTreeObserver
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.any
import org.hamcrest.StringDescription

object WaitActions {

    fun waitUntilDismiss(): ViewAction {

        return object : ViewAction {

            override fun getConstraints() = allOf(any(View::class.java), isDisplayed())

            override fun getDescription(): String = "wait for view dismiss"

            override fun perform(uiController: UiController, view: View) {
                val callback = ViewDismissCallback()
                try {
                    IdlingRegistry.getInstance().register(callback)
                    view.addOnAttachStateChangeListener(callback)
                    uiController.loopMainThreadUntilIdle()
                } finally {
                    IdlingRegistry.getInstance().unregister(callback)
                    view.removeOnAttachStateChangeListener(callback)
                }
            }
        }
    }

    fun waitUntil(matcher: Matcher<View>): ViewAction {

        return object : ViewAction {

            override fun getConstraints() = any(View::class.java)

            override fun getDescription() = "wait until: ${StringDescription().let(matcher::describeTo)}"

            override fun perform(uiController: UiController, view: View) {
                if (!matcher.matches(view)) {
                    val callback = ViewPropertyChangeCallback(matcher, view)
                    try {
                        IdlingRegistry.getInstance().register(callback)
                        view.viewTreeObserver.addOnDrawListener(callback)
                        uiController.loopMainThreadUntilIdle()
                    } finally {
                        IdlingRegistry.getInstance().unregister(callback)
                        view.viewTreeObserver.removeOnDrawListener(callback)
                    }
                }
            }
        }
    }

    fun waitFor(delay: Long): ViewAction {

        return object : ViewAction {

            override fun getConstraints() = isRoot()

            override fun getDescription() = "wait for $delay milliseconds"

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }

    private class ViewDismissCallback : IdlingResource, View.OnAttachStateChangeListener {

        private lateinit var callback: IdlingResource.ResourceCallback

        private var dismissed = false

        override fun getName() = "View dismiss callback"

        override fun isIdleNow() = dismissed

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
            this.callback = callback
        }

        override fun onViewDetachedFromWindow(v: View) {
            dismissed = true
            callback.onTransitionToIdle()
        }

        override fun onViewAttachedToWindow(v: View) = Unit
    }

    private class ViewPropertyChangeCallback(
        private val matcher: Matcher<View>,
        private val view: View,
    ) : IdlingResource, ViewTreeObserver.OnDrawListener {

        private lateinit var callback: IdlingResource.ResourceCallback

        private var matched = false

        override fun getName() = "View property change callback"

        override fun isIdleNow() = matched

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
            this.callback = callback
        }

        override fun onDraw() {
            matched = matcher.matches(view)
            callback.onTransitionToIdle()
        }
    }

}