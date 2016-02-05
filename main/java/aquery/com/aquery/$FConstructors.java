package aquery.com.aquery;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;

/**
 * The $Constructors adapted to fragments
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class $FConstructors extends $Constructors {
    private Fragment fragment;

    /**
     * Constructor of $FConstructors
     *
     * @param fragment A reference to the fragment
     */
    public $FConstructors(Fragment fragment) {
        super(null);
        this.fragment = fragment;
    }

    /**
     * Returns the AQuery element containing the root of the activity
     */
    public AQuery $() {
        return new $Element(ctx, fragment.getView());
    }

    /**
     * Returns an AQuery element containing all the views matching the given filter selector
     * @param selector
     * The selector to match. The syntax is the same as CSS-selectors.
     * For example : $("#my_id") will return the elements with the id R.id.my_id
     */
    public AQuery $(String selector) {
        return new $Array(ctx, AQuery.intersection($().parent().find(selector).list(), $().family().list()));
    }

    /**
     * Sets the Activity associated to the Fragment
     * Call this function once the activity is created
     */
    public void initActivity() {
        ctx = fragment.getActivity();
    }
}
