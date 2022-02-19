package app.americano.matcher

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import com.google.android.material.tabs.TabLayout.TabView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.equalTo

object TabViewMatchers {

    fun withTab(title: String) = withTitle(equalTo(title))

    private fun withTitle(title: Matcher<String>): Matcher<View> {

        return object : BoundedMatcher<View, TabView>(TabView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("with tab: ")
                title.describeTo(description)
            }

            override fun matchesSafely(item: TabView) = title.matches(item.tab?.text)
        }
    }
}