package com.oscarliang.spotifyclone.core.testing.util;

import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.util.HumanReadables;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.hamcrest.Matcher;

public class EspressoTestUtil {

    public static ViewAction nestedScrollTo() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(
                        isDescendantOfA(isAssignableFrom(NestedScrollView.class)),
                        withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
                );
            }

            @Override
            public String getDescription() {
                return "Perform scrolling on a NestedScrollView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                try {
                    NestedScrollView nestedScrollView =
                            (NestedScrollView) findFirstParentLayoutOfClass(view, NestedScrollView.class);
                    if (nestedScrollView == null) {
                        throw new Exception("Unable to find NestedScrollView parent");
                    }

                    CoordinatorLayout coordinatorLayout =
                            (CoordinatorLayout) findFirstParentLayoutOfClass(view, CoordinatorLayout.class);
                    if (coordinatorLayout != null) {
                        CollapsingToolbarLayout collapsingToolbarLayout =
                                findCollapsingToolbarLayoutChildIn(coordinatorLayout);
                        if (collapsingToolbarLayout != null) {
                            int toolbarHeight = collapsingToolbarLayout.getHeight();
                            nestedScrollView.startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                            nestedScrollView.dispatchNestedPreScroll(
                                    0,
                                    toolbarHeight,
                                    null,
                                    null
                            );
                        }
                    }
                    nestedScrollView.scrollTo(0, view.getTop());
                } catch (Exception e) {
                    throw new PerformException.Builder()
                            .withActionDescription(this.getDescription())
                            .withViewDescription(HumanReadables.describe(view))
                            .withCause(e)
                            .build();
                }
                uiController.loopMainThreadUntilIdle();
            }
        };
    }

    private static CollapsingToolbarLayout findCollapsingToolbarLayoutChildIn(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof CollapsingToolbarLayout) {
                return (CollapsingToolbarLayout) child;
            } else if (child instanceof ViewGroup) {
                return findCollapsingToolbarLayoutChildIn((ViewGroup) child);
            }
        }
        return null;
    }

    private static View findFirstParentLayoutOfClass(View view, Class<? extends View> parentClass) {
        ViewParent parent = new FrameLayout(view.getContext());
        ViewParent incrementView = null;
        int i = 0;
        while (parent != null && parent.getClass() != parentClass) {
            if (i == 0) {
                parent = view.getParent();
            } else {
                parent = incrementView.getParent();
            }
            incrementView = parent;
            i++;
        }
        return (View) parent;
    }

}