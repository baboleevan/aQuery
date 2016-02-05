package aquery.com.aquery;

import android.app.Activity;
import android.view.View;

import java.util.List;

/**
 * An AQuery object containing the root View of the Activity
 */
public class $Document extends AQuery {
    public $Document(Activity activity) {
        super(activity);
    }
    @Override
    public View head() {
        return ctx.findViewById(android.R.id.content);
    }
    @Override
    public List<View> list() {
        return singleton(head());
    }

    /**
     * Returns the width resolution of the device, in px
     */
    @Override
    public int width() {
        return new $Utils(ctx).width();
    }
    /**
     * Returns the height resolution of the device, in px
     */
    @Override
    public int height() {
        return new $Utils(ctx).height();
    }
}
