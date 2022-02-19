package app.americano.action

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.content.res.Configuration.ORIENTATION_UNDEFINED
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ListView
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage.RESUMED
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*

object ViewActions {

    fun requestFocus(): ViewAction {

        return object : ViewAction {

            override fun getDescription() = "Request focus"

            override fun getConstraints() = any(View::class.java)

            override fun perform(uiController: UiController, view: View) {
                view.requestFocus()
                uiController.loopMainThreadUntilIdle()
            }
        }
    }

    fun rotateScreenOrientation(): ViewAction {

        return object : ViewAction {

            override fun getConstraints() = isRoot()

            override fun getDescription() = "Rotate screen orientation"

            override fun perform(uiController: UiController, view: View) {
                val activity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED).first()
                activity.requestedOrientation = when (activity.resources.configuration.orientation) {
                    ORIENTATION_UNDEFINED, ORIENTATION_PORTRAIT -> SCREEN_ORIENTATION_LANDSCAPE
                    else -> SCREEN_ORIENTATION_PORTRAIT
                }
                uiController.loopMainThreadUntilIdle()
            }
        }
    }

    fun scrollTo(): ViewAction {

        return object : ViewAction {

            private val action = scrollTo()

            override fun getConstraints(): Matcher<View> {
                return allOf(
                        withEffectiveVisibility(VISIBLE),
                        isDescendantOfA(
                                anyOf(
                                        isAssignableFrom(ScrollView::class.java),
                                        isAssignableFrom(HorizontalScrollView::class.java),
                                        isAssignableFrom(ListView::class.java),
                                        isAssignableFrom(NestedScrollView::class.java)
                                )
                        )
                )
            }

            override fun getDescription(): String = action.description

            override fun perform(uiController: UiController, view: View) = action.perform(uiController, view)
        }
    }
}