package app.americano.matcher

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

object BottomSheetViewMatchers {

    enum class State(@BottomSheetBehavior.State val value: Int) {
        EXPANDED(BottomSheetBehavior.STATE_EXPANDED),
        COLLAPSED(BottomSheetBehavior.STATE_COLLAPSED),
        HIDDEN(BottomSheetBehavior.STATE_HIDDEN)
    }

    fun withState(state: State): Matcher<View> {

        return object : TypeSafeMatcher<View>(View::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("with state: ${state.name}")
            }

            override fun matchesSafely(item: View): Boolean {
                return BottomSheetBehavior.from(item).state == state.value
            }
        }
    }

    fun hasBottomSheetBehavior(): Matcher<View> {

        return object : TypeSafeMatcher<View>(View::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("has bottom sheet behavior")
            }

            override fun matchesSafely(item: View): Boolean {
                val params = item.layoutParams as? CoordinatorLayout.LayoutParams ?: return false
                return params.behavior is BottomSheetBehavior<*>
            }
        }
    }
}