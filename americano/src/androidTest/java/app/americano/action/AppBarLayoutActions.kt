package app.americano.action

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.android.material.appbar.AppBarLayout
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

object AppBarLayoutActions {

    fun collapse(): ViewAction = AppBarLayoutAction(false)

    fun expand(): ViewAction = AppBarLayoutAction(true)

    private class AppBarLayoutAction(val expand: Boolean) : ViewAction {

        override fun getConstraints(): Matcher<View> {
            return allOf(
                isAssignableFrom(AppBarLayout::class.java),
                isDisplayed(),
                withParent(isAssignableFrom(CoordinatorLayout::class.java))
            )
        }

        override fun getDescription(): String {
            return if (expand) "expand toolbar" else "collapse toolbar"
        }

        override fun perform(uiController: UiController, view: View) {
            (view as AppBarLayout).setExpanded(expand, false)
            uiController.loopMainThreadUntilIdle()
        }
    }

}