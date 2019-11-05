package com.jordansilva.imageloader.ui

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.jordansilva.imageloader.R
import com.jordansilva.imageloader.util.EspressoIdlingResource
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.idlingResource)
    }

    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.idlingResource)
    }

    @Test
    fun whenSearchWithValidInput_thenDisplayItems() {
        val query = "kittens"
        inputEditQueryText(query)
        verifyListHasItems()
        verifyEditQueryText(query)
    }

    @Test
    fun whenScrollList_thenDisplayMoreItems() {
        val query = "cats"
        inputEditQueryText(query)

        val recyclerView = onView(withId(R.id.recyclerView))
        repeat(10) {
            recyclerView.perform(swipeUp())
            sleep(500)
        }

        verifyEditQueryText(query)
        verifyListHasItems(10)
    }

    private fun verifyListHasItems(position: Int = 0) {
        sleep(600)
        val imageView = onView(
            allOf(
                withId(R.id.imageItem),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.recyclerView),
                        position
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        imageView.check(matches(isDisplayed()))
    }

    private fun inputEditQueryText(query: String) {
        val editQuery = onViewDisplayed(R.id.editQuery)
        editQuery.check(matches(withText("")))
        editQuery.perform(typeText(query), closeSoftKeyboard(), pressImeActionButton())
    }

    private fun verifyEditQueryText(query: String) {
        onViewDisplayed(R.id.editQuery)
            .check(matches(withText(query)))
    }

    private fun onViewDisplayed(id: Int) = onView(allOf(withId(id), isDisplayed()))

    private fun childAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
