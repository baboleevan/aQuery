package aquery.com.aquery;

import android.app.Activity;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An AQuery object containing ta single element
 */
public class $Element extends AQuery {
    private View elt; // The element

    public $Element(Activity ctx, View elt) {
        super(ctx);
        this.elt = elt;
    }

    @Override
    public View head() {
        return elt;
    }
    @Override
    public AQuery first() {
        return this;
    }

    @Override
    public List<View> list() {
        return singleton(elt);
    }

    private ArrayList<AnimationParams> animations = new ArrayList<>();

    private static HashMap<View,$Element> animatingElements = new HashMap<>(); // A map associating a View to a $Element, containing the list of every currently animating views

    /**
     * Animates a given View
     * @param ctx
     * The activity in which the view is
     * @param v
     * The view to animate
     * @param parameters
     * The animation parameters
     * @return
     * The AQuery created
     */
    protected static AQuery animateView(Activity ctx, View v, AnimationParams parameters) {
        $Element res = getAnimatingElement(ctx, v);
        res.animate(parameters);
        return res;
    }
    /**
     * Animates the view, with an animation created at the last moment
     * @param ctx
     * The activity in which the view is
     * @param v
     * The view to animate
     * @param handler
     * A function that will be called when the animation is about to start. This function must return the animation parameters
     * @return
     * The AQuery created
     */
    protected static AQuery postAnimate(Activity ctx, View v, AnimationHandler handler) {
        $Element res = getAnimatingElement(ctx, v);
        res.animate(new AnimationParams(handler));
        return res;
    }

    /**
     * Stop the running animations associating with a given view
     * @param v
     * The view
     * @param clearQueue
     * true to also clear next animations associated with the view
     * @param jumpToEnd
     * true to complete the current animation immediatly
     */
    protected static AQuery stopAnimations(View v, boolean clearQueue, boolean jumpToEnd) {
        $Element res = animatingElements.get(v);
        if (res != null)
            res.stopRunnings(clearQueue, jumpToEnd);
        return res;
    }

    /**
     * Checks if a given View is currently animating
     */
    protected static boolean isAnimating(View v) {
        $Element res = animatingElements.get(v);
        if (res == null)
            return false;
        return (res.runningAnimations.size() > 0);
    }

    /**
     * Returns the $Element object associated to the View. If it hasn't been created yet, creates it
     */
    private static $Element getAnimatingElement(Activity ctx, View v) {
        $Element res = animatingElements.get(v);
        if (res == null) {
            res = new $Element(ctx,v);
            animatingElements.put(v, res);
        }
        return res;
    }

    /**
     * Starts the animation if no animation is currently running. Adds it to the queue otherwise
     */
    private AQuery animate(AnimationParams parameters) {
        if (parameters.isQueue()) {
            animations.add(parameters);
            if (animations.size() == 1)
                nextStep();
        }
        else
            launchAnimate(parameters);
        return this;
    }


    private ArrayList<AnimationParams> runningAnimations = new ArrayList<>(); // The currently running animations associated to the $Element

    /**
     * Add a running animation to the list of running animations
     */
    private void addRunning(AnimationParams parameters) {
        for (AnimationParams s : runningAnimations) {
            ArrayList<PropertyTransition> transitions = s.getTransitions();
            for (int i=transitions.size()-1;i>=0;i--) {
                PropertyTransition transition = transitions.get(i);
                for (PropertyTransition t : parameters.getTransitions()) {
                    if (t.attr.equals(transition.attr)) {
                        transitions.remove(i);
                        break;
                    }
                }
            }
        }
        runningAnimations.add(parameters);
    }

    /**
     * Stops the currently running animations
     * @param clearQueue
     * true to also clear next animations associated with the view
     * @param jumpToEnd
     * true to complete the current animation immediatly
     */
    private void stopRunnings(boolean clearQueue, boolean jumpToEnd) {
        AnimationParams firstAnimation = null;
        if (jumpToEnd) {
            if (animations.size() > 0)
                firstAnimation = animations.get(0);
        }
        if (clearQueue)
            animations.clear();
        for (AnimationParams s : runningAnimations) {
            if (jumpToEnd) {
                s.process(this, 1);
                s.onFinish(elt);
            }
            s.getTransitions().clear();
        }
        if (firstAnimation != null)
            launchAnimate(firstAnimation);
    }

    /**
     * Remove the running animation
     */
    private void removeRunning(AnimationParams parameters) {
        runningAnimations.remove(parameters);
    }

    /**
     * Launches the next animation
     */
    private void nextStep() {
        launchAnimate(animations.get(0));
    }
    /**
     * Initialize the animation and launch it
     */
    private void launchAnimate(AnimationParams parameters) {
        parameters.create(this);
        addRunning(parameters);
        parameters.initTransitions(this);
        parameters.start(this);
        animateAux(parameters, 0);
    }

    /**
     * An auxiliary function to launchAnimate. Process each step of the animation
     * @param parameters
     * The animation parameters
     * @param elapsedTime
     * The elapsed time, in MS
     */
    private void animateAux(final AnimationParams parameters, int elapsedTime) {
        final int nextTime = (parameters.getTransitions().size() == 0) ? parameters.getTime() : Math.min(parameters.getTime(), elapsedTime + TPF);
        elt.postDelayed(new Runnable() {
            @Override
            public void run() {
                float t = (parameters.getTime() != 0) ? (float) nextTime / parameters.getTime() : 1;
                parameters.process($Element.this, t);
                parameters.frame($Element.this, t);
                if (nextTime < parameters.getTime())
                    animateAux(parameters, Math.min(parameters.getTime(), nextTime));
                else {
                    parameters.onFinish(elt);
                    boolean isNext = false;
                    if (parameters.isQueue()) {
                        if (animations.remove(parameters)) {
                            isNext = (animations.size() > 0);
                            if (isNext)
                                nextStep();
                        }
                    }
                    removeRunning(parameters);
                    if (!isNext && (runningAnimations.size() == 0))
                        animatingElements.remove(elt);
                }
            }
        }, nextTime - elapsedTime);
    }
}
