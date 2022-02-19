package app.americano.matcher

import androidx.test.espresso.matcher.BoundedMatcher
import androidx.appcompat.widget.Toolbar
import android.view.View
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Description
import org.hamcrest.Matcher

object ToolbarMatchers {

    fun withTitle(title: String) = withTitle(equalTo(title))

    private fun withTitle(title: Matcher<String>): Matcher<View> {

        return object : BoundedMatcher<View, Toolbar>(Toolbar::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("with toolbar title: ")
                title.describeTo(description)
            }

            override fun matchesSafely(item: Toolbar) = title.matches(item.title)
        }
    }
}