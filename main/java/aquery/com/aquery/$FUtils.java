package aquery.com.aquery;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.view.View;

/**
 * The $Utils function adapted to Fragments
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class $FUtils extends $Utils {
    private Fragment fragment;

    public $FUtils(Fragment fragment) {
        super(null);
        this.fragment = fragment;
    }

    /**
     * Returns the first element of the fragment
     */
    @Override
    public AQuery root() {
        return new $Element(ctx, fragment.getView());
    }

    /**
     * Returns the elements that have the given ID
     */
    @Override
    public AQuery id(int id) {
        return new $Piece(fragment).find(id);
    }

    /**
     * Returns all the elements of the fragment
     */
    @Override
    public AQuery all() {
        return new $Piece(fragment).descendants();
    }

    /**
     * Returns the width of the fragment, in pixels
     */
    @Override
    public int width() {
        View root = fragment.getView();
        if (root == null)
            return 0;
        return root.getWidth();
    }
    /**
     * Returns the height of the fragment, in pixels
     */
    @Override
    public int height() {
        View root = fragment.getView();
        if (root == null)
            return 0;
        return root.getHeight();
    }
    /**
     * Returns the width resolution of the device, in px
     */
    public int screenWidth() {
        return super.width();
    }
    /**
     * Returns the height resolution of the device, in px
     */
    public int screenHeight() {
        return super.height();
    }

    /**
     * Returns the activity root, wrapped into an AQuery object.
     * Useful to make, from the fragment, operations on the whole Activity views
     */
    public AQuery activityRoot() {
        return new $Document(ctx);
    }

    /**
     * Sets the Activity associated to the Fragment.
     * Call this function once the activity is created
     */
    public void initActivity() {
        ctx = fragment.getActivity();
    }
}
