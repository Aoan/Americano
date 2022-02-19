package app.americano.action

import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.util.TreeIterables
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.StringDescription

object RecyclerViewActions {

    fun scrollToLast(): ViewAction {

        return object : ViewAction {

            override fun getConstraints() = allOf(isAssignableFrom(RecyclerView::class.java), isDisplayed())

            override fun getDescription() = "scroll RecyclerView to last"

            override fun perform(uiController: UiController, view: View) {
                val adapter = (view as RecyclerView).adapter ?: return
                view.scrollToPosition(adapter.itemCount - 1)
                uiController.loopMainThreadUntilIdle()
            }
        }
    }

    fun actionOnItemView(matcher: Matcher<View>, action: ViewAction): ViewAction {

        return object : ViewAction {

            override fun getConstraints() = allOf(withParent(isAssignableFrom(RecyclerView::class.java)), isDisplayed())

            override fun getDescription() = "performing ViewAction: ${action.description} on item matching: ${StringDescription.asString(matcher)}"

            override fun perform(uiController: UiController, view: View) {
                val matches = TreeIterables.breadthFirstViewTraversal(view).filter(matcher::matches)
                when (matches.size) {
                    1 -> action.perform(uiController, matches[0])
                    0 -> throw RuntimeException("No view found $matcher")
                    else -> throw RuntimeException("Ambiguous views found $matcher")
                }
            }
        }
    }
}