package aquery.com.aquery;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * The class containing all useful functions to create AQuery objects.
 * Those methods are used directly in $Activity object
 */
@SuppressWarnings("unused")
public class $Constructors {

    protected Activity ctx; // The activity associated to the AQuery objects that will be created

    /**
     *Constructor of $Constructors
     * @param ctx
     * A reference to the activity
     */
    public $Constructors(Activity ctx) {
        this.ctx = ctx;
    }

    /**
     * Returns the AQuery element containing the root of the activity
     */
    public AQuery $() {
        return new $Document(ctx);
    }
    /**
     * Runs a function when the activity is ready
     * @param r
     * The function to run
     * @return
     * A reference to an AQuery containing the root of the document
     */
    public AQuery $(Runnable r) {
        AQuery res = $();
        res.head().post(r);
        return res;
    }

    /**
     * Returns an AQuery element containing all the views matching the given filter selector
     * @param selector
     * The selector to match. The syntax is the same as CSS-selectors.
     * For example : $("#my_id") will return the elements with the id R.id.my_id
     */
    public AQuery $(String selector) {
        return $().find(selector);
    }
    /**
     * Returns an AQuery containing the given view
     */
    public AQuery $(View element) {
        return new $Element(ctx, element);
    }
    /**
     * Returns an AQuery containing the given list of views
     */
    public AQuery $(List<View> views) {
        return new $Array(ctx, views);
    }
    /**
     * Inflates an xml code and appends the result to the parent
     * @param xml
     * The xml code to parse
     * @param parent
     * The parent
     * @return
     * An AQuery element containing the Views inflated
     */
    public AQuery $(String xml, View parent) {
        return new $Array(ctx, xml, parent);
    }
    /**
     * Inflates an xml code and appends the result to the parent
     * @param xml
     * The xml code to parse
     * @param parent
     * The parent
     * @param append
     * true to appends the result to the parent. Default is true
     * @return
     * An AQuery element containing the Views inflated
     */
    public AQuery $(String xml, View parent, boolean append) {
        return new $Array(ctx, xml, parent, append);
    }
    /**
     * Inflates an xml code and appends the result to the first element of the parent
     * @param xml
     * The xml code to parse
     * @param parent
     * The parent
     * @return
     * An AQuery element containing the Views inflated
     */
    public AQuery $(String xml, AQuery parent) {
        return new $Array(ctx, xml, parent);
    }
    /**
     * Inflates an xml code and appends the result to the first element of the parent
     * @param xml
     * The xml code to parse
     * @param parent
     * The parent
     * @param append
     * true to appends the result to the parent. Default is true
     * @return
     * An AQuery element containing the Views inflated
     */
    public AQuery $(String xml, AQuery parent, boolean append) {
        return new $Array(ctx, xml, parent, append);
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
        return $(resource,parent, true);
    }
    /**
     * Inflates an xml layout resource and appends the result to the first element of the parent
     * @param resource
     * The ID of the XML resource to inflate
     * @param parent
     * The parent
     * @param append
     * true to appends the result to the parent. Default is true
     * @return
     * An AQuery element containing the Views inflated
     */
    public AQuery $(int resource, AQuery parent, boolean append) {
        return $(resource,(ViewGroup)parent.head(), append);
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
        return $(resource, parent, true);
    }
    /**
     * Inflates an xml layout resource and appends the result to the parent
     * @param resource
     * The ID of the XML resource to inflate
     * @param parent
     * The parent
     * @param append
     * true to appends the result to the parent. Default is true
     * @return
     * An AQuery element containing the Views inflated
     */
    public AQuery $(int resource, ViewGroup parent, boolean append) {
        LayoutTransition lt;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            lt = parent.getLayoutTransition();
            if (lt != null)
                parent.setLayoutTransition(null);
        }
        else
            lt = null;
        View[] children = new View[parent.getChildCount()];
        for (int i=0;i<children.length;i++)
            children[i] = parent.getChildAt(i);
        parent.removeAllViews();
        LayoutInflater.from(ctx).inflate(resource, parent);
        View res = parent.getChildAt(0);
        parent.removeAllViews();
        for (View aChildren : children)
            parent.addView(aChildren);
        if (lt != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
                parent.setLayoutTransition(lt);
        }
        if (append)
            parent.addView(res);
        return $(res);
    }
}
