package app.americano.matcher

import android.view.View
import android.widget.AbsListView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.anyOf

object AbsListViewMatchers {

    private enum class Mode(val value: Int) {
        SINGLE(AbsListView.CHOICE_MODE_SINGLE),
        MULTIPLE(AbsListView.CHOICE_MODE_MULTIPLE),
        MULTIPLE_MODAL(AbsListView.CHOICE_MODE_MULTIPLE_MODAL),
    }

    fun isSingleChoice(): Matcher<View> = withChoiceMode(Mode.SINGLE)

    fun isMultipleChoice(): Matcher<View> = anyOf(withChoiceMode(Mode.MULTIPLE), withChoiceMode(Mode.MULTIPLE_MODAL))

    private fun withChoiceMode(mode: Mode): Matcher<View> {

        return object : BoundedMatcher<View, AbsListView>(AbsListView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("with choice mode: ${mode.name}")
            }

            override fun matchesSafely(view: AbsListView): Boolean {
                return view.choiceMode == mode.value
            }
        }
    }
}