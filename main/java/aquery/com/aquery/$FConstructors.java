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
        return new $Piece(fragment);
    }

    /**
     * Sets the Activity associated to the Fragment
     * Call this function once the activity is created
     */
    public void initActivity() {
        ctx = fragment.getActivity();
    }
}
