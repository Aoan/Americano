package app.americano.action

import android.view.View
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import app.americano.matcher.BottomSheetViewMatchers.State
import app.americano.matcher.BottomSheetViewMatchers.hasBottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior

object BottomSheetViewActions {

    fun expand(): ViewAction = BottomSheetAction(State.EXPANDED)

    fun collapse(): ViewAction = BottomSheetAction(State.COLLAPSED)

    fun hide(): ViewAction = BottomSheetAction(State.HIDDEN)

    private class BottomSheetAction(val state: State) : ViewAction {

        override fun getConstraints() = hasBottomSheetBehavior()

        override fun getDescription(): String {
            return when (state) {
                State.EXPANDED -> "expand bottom sheet"
                State.COLLAPSED -> "collapse bottom sheet"
                State.HIDDEN -> "hide bottom sheet"
            }
        }

        override fun perform(uiController: UiController, view: View) {
            val callback = BottomSheetCallback(state)
            val behavior = BottomSheetBehavior.from(view)
            if (behavior.state != state.value) {
                behavior.state = state.value
                try {
                    IdlingRegistry.getInstance().register(callback)
                    behavior.addBottomSheetCallback(callback)
                    uiController.loopMainThreadUntilIdle()
                } finally {
                    IdlingRegistry.getInstance().unregister(callback)
                    behavior.removeBottomSheetCallback(callback)
                }
            }
        }
    }

    private class BottomSheetCallback(
        private val state: State,
    ) : IdlingResource, BottomSheetBehavior.BottomSheetCallback() {

        private lateinit var callback: IdlingResource.ResourceCallback

        private var idle = false

        override fun getName() = "Bottom sheet behavior callback"

        override fun isIdleNow() = idle

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
            this.callback = callback
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            idle = newState == state.value
            callback.onTransitionToIdle()
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
    }
}