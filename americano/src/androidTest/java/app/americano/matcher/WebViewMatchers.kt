package app.americano.matcher

import android.view.View
import android.webkit.WebView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.equalTo

object WebViewMatchers {

    fun isCompletelyLoaded() = withProgress(equalTo(100))

    private fun withProgress(progress: Matcher<Int>): Matcher<View> {

        return object : BoundedMatcher<View, WebView>(WebView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("with progress: ")
                progress.describeTo(description)
            }

            override fun matchesSafely(view: WebView) = progress.matches(view.progress)
        }
    }
}