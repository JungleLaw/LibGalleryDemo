package com.zte.android.lib.media.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;

import com.zte.android.lib.media.R;


/**
 * Created by Jungle on 2017/7/28.
 */

public class DropView extends RelativeLayout {
    private static final long COLLAPSE_TRANSITION_DURATION = 250L;
    private static final int DROP_DOWN_CONTAINER_MIN_DEFAULT_VIEWS = 1;

    @Nullable
    private View expandedView;
    private View vEmptyDropView;
    private RelativeLayout vDropContainer;
    private boolean isTransitioning;
    private ObjectAnimator shadowFadeOutAnimator;
    private TransitionSet expandTransitionSet;
    private TransitionSet collapseTransitionSet;
    private boolean isExpanded = false;
    private DropListener dropListener;
    private int backgroundColor;
    private int overlayColor;

    public DropView(Context context) {
        this(context, null, 0);
    }

    public DropView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DropView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        initAttributes();
        inflate(getContext(), R.layout.view_drop_view, this);
        setupViews();
        setupTransitionSets();
    }

    private void initAttributes() {
        if (backgroundColor == 0) {
            backgroundColor = ContextCompat.getColor(getContext(), android.R.color.transparent);
        }
        if (overlayColor == 0) {
            overlayColor = ContextCompat.getColor(getContext(), R.color.drop_view_empty_bg);
        }
    }

    private void setupViews() {
        vEmptyDropView = findViewById(R.id.empty_drop_space);
        vDropContainer = (RelativeLayout) findViewById(R.id.drop_container);

        vEmptyDropView.setOnClickListener(mEmptyDropDownSpaceClickListener);
        vEmptyDropView.setBackgroundColor(overlayColor);
    }

    private void setupTransitionSets() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            shadowFadeOutAnimator = ObjectAnimator.ofFloat(vEmptyDropView, View.ALPHA, 0f);
            shadowFadeOutAnimator.setDuration(COLLAPSE_TRANSITION_DURATION);
            shadowFadeOutAnimator.setInterpolator(new AccelerateInterpolator());
            shadowFadeOutAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    vEmptyDropView.setVisibility(View.GONE);
                    vEmptyDropView.setAlpha(1f);
                }
            });
            expandTransitionSet = createTransitionSet();
            collapseTransitionSet = createTransitionSet();
            collapseTransitionSet.setDuration(COLLAPSE_TRANSITION_DURATION);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private TransitionSet createTransitionSet() {
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.addTarget(vDropContainer);
        Fade fade = new Fade();
        fade.addTarget(vEmptyDropView);
        TransitionSet transitionSet = new TransitionSet();
        transitionSet.addTransition(changeBounds);
        transitionSet.addTransition(fade);
        transitionSet.setInterpolator(new AccelerateDecelerateInterpolator());
        transitionSet.addListener(new TransitionListenerAdapter() {
            @Override
            public void onTransitionStart(Transition transition) {
                super.onTransitionStart(transition);
                isTransitioning = true;
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                super.onTransitionEnd(transition);
                isTransitioning = false;
            }
        });
        return transitionSet;
    }

    public void setDropListener(DropListener dropListener) {
        this.dropListener = dropListener;
    }

    /**
     * Sets the view that will always be visible only when the {@link DropView} is in expanded mode.
     * The height of your provided view and your provided header view will determine the height of
     * the entire {@link DropView} in expanded mode.
     * (only if you set <code>wrap_content</code> to the {@link DropView}).
     *
     * @param expandedView your header view
     */
    public void setExpandedView(@NonNull View expandedView) {
        this.expandedView = expandedView;
        if (vDropContainer.getChildCount() > DROP_DOWN_CONTAINER_MIN_DEFAULT_VIEWS) {
            for (int i = DROP_DOWN_CONTAINER_MIN_DEFAULT_VIEWS; i < vDropContainer.getChildCount(); i++) {
                vDropContainer.removeViewAt(i);
            }
        }
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vDropContainer.addView(expandedView, params);
        expandedView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    /**
     * Animates and expands the drop down, displaying the provided expanded view. Must set expanded
     * view before this for the drop down to expand.
     *
     * @see #setExpandedView(View)
     */
    public void expandDrop() {
        if (!isExpanded && !isTransitioning && expandedView != null) {
            beginDelayedExpandTransition();
            if (dropListener != null) {
                dropListener.onExpandDropDown();
            }
            vEmptyDropView.setVisibility(View.VISIBLE);
            expandedView.setVisibility(View.VISIBLE);
            isExpanded = true;
        }
    }

    /**
     * Animates and collapses the drop down, displaying only the provided header view. Must set expanded
     * view before this for the drop down to collapse.
     *
     * @see #setExpandedView(View)
     */
    public void collapseDrop() {
        if (isExpanded && !isTransitioning && expandedView != null) {
            beginDelayedCollapseTransition();
            expandedView.setVisibility(View.GONE);
            if (dropListener != null) {
                dropListener.onCollapseDropDown();
            }
            isExpanded = false;
        }
    }

    /**
     * @return true if the view is expanded, false otherwise.
     */
    public boolean isExpanded() {
        return isExpanded;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void beginDelayedExpandTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(this, expandTransitionSet);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void beginDelayedCollapseTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            shadowFadeOutAnimator.start();
            TransitionManager.beginDelayedTransition(vDropContainer, collapseTransitionSet);
        } else {
            vEmptyDropView.setVisibility(View.GONE);
        }
    }

    private final OnClickListener mEmptyDropDownSpaceClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            collapseDrop();
        }
    };

    @TargetApi(Build.VERSION_CODES.KITKAT)
    class TransitionListenerAdapter implements Transition.TransitionListener {

        @Override
        public void onTransitionStart(Transition transition) {

        }

        @Override
        public void onTransitionEnd(Transition transition) {

        }

        @Override
        public void onTransitionCancel(Transition transition) {

        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }
    }

    /**
     * A listener that wraps functionality to be performed when the drop down is successfully expanded
     * or collapsed.
     */
    public interface DropListener {
        /**
         * This method will only be triggered when {@link #expandDrop()} is called successfully.
         */
        void onExpandDropDown();

        /**
         * This method will only be triggered when {@link #collapseDrop()} is called successfully.
         */
        void onCollapseDropDown();
    }
}
