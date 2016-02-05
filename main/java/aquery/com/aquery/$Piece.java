package aquery.com.aquery;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.view.View;

import java.util.List;

/**
 * An AQuery object containing the root of the fragment
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class $Piece extends AQuery {
    private Fragment fragment;
    public $Piece(Fragment fragment) {
        super(fragment.getActivity());
        this.fragment = fragment;
    }
    @Override
    public View head() {
        return fragment.getView();
    }
    @Override
    public List<View> list() {
        return singleton(head());
    }
}
