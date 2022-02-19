package app.americano.action

import android.view.View
import android.widget.CompoundButton
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

object CheckableViewActions {

    fun check(): ViewAction = CheckViewAction(true)

    fun uncheck(): ViewAction = CheckViewAction(false)

    private class CheckViewAction(val checked: Boolean) : ViewAction {

        override fun getConstraints(): Matcher<View> {
            return allOf(
                isDisplayed(),
                isClickable(),
                isEnabled(),
                isAssignableFrom(CompoundButton::class.java))
        }

        override fun getDescription() = if (checked) "check compound button" else "uncheck compound button"

        override fun perform(uiController: UiController, view: View) {
            view as CompoundButton
            view.isChecked = checked
            uiController.loopMainThreadUntilIdle()
        }
    }
}