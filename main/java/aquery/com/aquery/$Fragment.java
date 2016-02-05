package aquery.com.aquery;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * A Fragment that implements the functions to work with AQuery elements
 *
 * Inherit your fragment activity from this one to be be able to use it
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class $Fragment extends Fragment {
    private final $FConstructors aqFactory = new $FConstructors(this); // The object containing all useful methods to construct AQuery objects

    public static class $ extends $FUtils {
        public $(Fragment ctx) {
            super(ctx);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        aqFactory.initActivity();
        $.initActivity();
    }

    public final $ $ = new $(this); // The object containing all useful functions

    /**
     * Returns the AQuery element containing the root of the fragment
     */
    public AQuery $() {
        return aqFactory.$();
    }
    public AQuery $(AQuery q) {
        return q;
    }

    /**
     * Returns an AQuery element containing all the views matching the given filter selector
     * @param selector
     * The selector to match. The syntax is the same as CSS-selectors.
     * For example : $("#my_id") will return the elements with the id R.id.my_id
     */
    public AQuery $(String selector) {
        return aqFactory.$(selector);
    }

    /**
     * Runs a function when the activity is ready
     * @param r
     * The function to run
     * @return
     * A reference to an AQuery containing the root of the document
     */
    public AQuery $(Runnable r) {
        return aqFactory.$(r);
    }

    /**
     * Returns an AQuery containing the given view
     */
    public AQuery $(View element) {
        return aqFactory.$(element);
    }
    /**
     * Returns an AQuery containing the given list of views
     */
    public AQuery $(List<View> views) {
        return aqFactory.$(views);
    }

    /**
     * Inflates an xml code and appends the result to first element of the parent
     * @param xml
     * The xml code to parse
     * @param parent
     * The parent
     * @return
     * An AQuery element containing the Views inflated
     */
    public AQuery $(String xml, AQuery parent) {
        return aqFactory.$(xml, parent);
    }
    /**
     * Inflates an xml code and appends the result to the first element in the set of elements
     * @param xml
     * The xml code to parse
     * @param parent
     * The parent
     * @return
     * An AQuery element containing the Views inflated
     */
    public AQuery $(String xml, View parent) {
        return aqFactory.$(xml, parent);
    }

    /**
     * Inflates an xml layout resource and appends the result to the first element of the parent
     * @param resource
     * The ID of the XML resource to inflate
     * @param parent
     * The parent
     * @return
     * An AQuery element containing the Views inflated
     */
    public AQuery $(int resource, AQuery parent) {
        return aqFactory.$(resource, parent);
    }
    /**
     * Inflates an xml layout resource and appends the result to the parent
     * @param resource
     * The ID of the XML resource to inflate
     * @param parent
     * The parent
     * @return
     * An AQuery element containing the Views inflated
     */
    public AQuery $(int resource, ViewGroup parent) {
        return aqFactory.$(resource, parent);
    }
}
