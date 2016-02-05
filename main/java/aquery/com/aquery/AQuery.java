package aquery.com.aquery;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsSeekBar;
import android.widget.AbsoluteLayout;
import android.widget.Adapter;
import android.widget.AdapterViewAnimator;
import android.widget.AdapterViewFlipper;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TabWidget;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewAnimator;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The main class of the AQuery Framework
 * Contains a set of elements (Views), and provide many methods to access and edit the properties of these elements
 */
public abstract class AQuery {
    protected Activity ctx; // A reference to the activity where the views are located

    /**
     * Creates an AQuery object
     * @param ctx
     * The activity where the views are
     */
    public AQuery(Activity ctx) {
        this.ctx = ctx;
    }

    /**
     * Returns the first View of the set of elements
     * @return
     */
    public abstract View head();

    /**
     * Returns the first View of the set of elements, wrapped into a new AQuery instance
     * @return
     */
    public AQuery first() {
        return new $Element(ctx,head());
    }

    /**
     * List all views of the set of elements
     */
    public abstract List<View> list();

    /**
     * Returns the last view of the set of elements
     */
    public View last() {
        List<View> views = list();
        return views.get(views.size() - 1);
    }
    /**
     * Returns the last View of the set of elements, wrapped into a new AQuery instance
     */
    public AQuery tail() {
        return new $Element(ctx,last());
    }

    /**
     * Returns the id-th View of the set of elements
     * @param id
     * The position of the View
     */
    public View get(int id) {
        List<View> views = list();
        if (id < 0)
            id += views.size();
        return views.get(id);
    }
    /**
     * Returns the id-th View of the set of elements, wrapped into a new AQuery instance
     * @param id
     * The position of the View
     */
    public AQuery eq(int id) {
        return new $Element(ctx, get(id));
    }

    /**
     * Returns the number of Views of the set of elements
     */
    public int length() {
        return list().size();
    }

    /**
     * An class to handle the conversion between XML code and it's JAVA equivalent
     * The AQuery class has final member called ATTRS which is a map associating an attribute to an instance of an AttrSetter
     */
    abstract static class AttrSetter {
        /**
         * Returns the value of the attribute taken by the view
         * For example, if the attribute concerned is "id", this method should return v.getId()
         * @param v
         * The view
         */
        abstract Object get(View v);

        /**
         * Changes the value of the attribute taken by the view
         * For example, if the attribute concerned is "id", this method should call v.setId(value)
         * @param q
         * The AQuery object implicated
         * @param v
         * The view
         * @param value
         * The value to take
         */
        abstract void prop(AQuery q, View v, Object value);

        /**
         * Casts a String value into the appropriate type
         * For example, if the attribute concerned is the id, and value = "50", then this method should return 50
         * Note that this method should also handle Strings like "@id/the_id", which is the interest of it
         *
         * @param q
         * The AQuery object implicated
         * @param v
         * The view implicated
         * @param value
         * The value to format
         */
        abstract Object format(AQuery q, View v, String value);
        Object format($Element jElt, String value) {
            return format(jElt,jElt.head(),value);
        }

        /**
         * Changes the value of the attribute taken by the view
         * For example, if the attribute concerned is "id", this method should call v.setId(value)
         * @param q
         * The AQuery object implicated
         * @param v
         * The view
         * @param value
         * The value to take, as a String
         */
        void set(AQuery q, View v, String value) {
            prop(q, v, format(q,v, value));
        }

        /**
         * Returns the functions to call when the user wants to modify the attribute progressively
         * @param q
         * The element to modify
         * @param begin
         * The value of the attribute at the beginning of the transition
         * @param end
         * The value of the attribute at the end of the transition
         * @return
         * A Transition object containing the functions to call to do the transition
         */
        abstract Transition getTransition($Element q, Object begin, Object end);
    }

    /**
     * An interface to handle any type of TypedAttr
     */
    private interface PropListener {
        /**
         * The function called when the user wants to get the value of the attribute
         * @param v
         * The view
         * @return
         * The value of the attribute
         */
        Object get(View v);

        /**
         * The function called when the user wants to change the value of the attribute
         * @param q
         * The AQuery implicated
         * @param v
         * The view
         * @param value
         * The new value to take
         */
        void prop(AQuery q, View v, Object value);
    }
    /**
     * An interface to handle any type of CustomAttr
     */
    private interface AttrListener extends PropListener {
        Object format(AQuery q, View v, String value);
    }

    /**
     * A class that allows you to define an AttrSetter for a general XML property
     */
    private static class CustomAttr extends AttrSetter {
        private AttrListener l; // The interface containing the functions to call to get and edit the value of the attribute

        /**
         * Constructor of CustomAttr
         * @param callback
         * The functions called when the user wants to get or set the value of the attribute
         */
        public  CustomAttr(AttrListener callback) {
            l = callback;
        }

        @Override
        Object get(View v) {
            return l.get(v);
        }

        @Override
        void prop(AQuery q, View v, Object value) {
            l.prop(q,v,value);
        }

        @Override
        Transition getTransition($Element q, Object begin, Object end) {
            return new NoTransition(begin); // No perticular transition in the general case
        }

        @Override
        Object format(AQuery q, View v, String value) {
            return l.format(q,v,value);
        }
    }
    /**
     * An abstract class that allows you to define an AttrSetter for a defined type of variable (int, String, etc)
     * Each type of variable concerned has its associated class, which is a subclass of this one
     *
     * The interest of this class is to override the format method to handle basic cases
     */
    abstract static class TypedAttr extends AttrSetter {
        private PropListener l; // The interface containing the functions to call to get and edit the value of the attribute

        /**
         * Constructor of TypedAttr
         * @param callback
         * The functions called when the user wants to get or set the value of the attribute
         */
        public TypedAttr(PropListener callback) {
            l = callback;
        }

        @Override
        Object get(View v) {
            return l.get(v);
        }

        @Override
        void prop(AQuery q, View v, Object value) {
            l.prop(q, v, value);
        }
    }

    /**
     * The TypedAttr for int types of variable
     */
    private static class IntAttr extends TypedAttr {
        public IntAttr(PropListener callback) {
            super(callback);
        }

        @Override
        Object format(AQuery q, View v, String value) {
            return q.formatInt(value);
        }

        @Override
        Transition getTransition($Element q, Object begin, Object end) {
            return new IntTransition(intCast(begin), intCast(end));
        }
    }
    /**
     * The TypedAttr for long types of variable
     */
    private static class LongAttr extends TypedAttr {
        public LongAttr(PropListener callback) {
            super(callback);
        }

        @Override
        Object format(AQuery q, View v, String value) {
            return q.formatLong(value);
        }

        @Override
        Transition getTransition($Element q, Object begin, Object end) {
            return new LongTransition(longCast(begin), longCast(end));
        }
    }
    /**
     * The TypedAttr for bool types of variable
     */
    private static class BoolAttr extends TypedAttr {
        public BoolAttr(PropListener callback) {
            super(callback);
        }

        @Override
        Object format(AQuery q, View v, String value) {
            return q.formatBool(value);
        }

        @Override
        Transition getTransition($Element q, Object begin, Object end) {
            return new NoTransition(begin);
        }
    }
    /**
     * The TypedAttr for float types of variable
     */
    private static class FloatAttr extends TypedAttr {
        public FloatAttr(PropListener callback) {
            super(callback);
        }

        @Override
        Object format(AQuery q, View v, String value) {
            return q.formatFloat(value);
        }

        @Override
        Transition getTransition($Element q, Object begin, Object end) {
            return new FloatTransition(floatCast(begin), floatCast(end));
        }
    }
    /**
     * The TypedAttr for String types of variable
     */
    private static class StringAttr extends TypedAttr {
        public StringAttr(PropListener callback) {
            super(callback);
        }

        @Override
        Object format(AQuery q, View v, String value) {
            return q.formatString(value);
        }

        @Override
        Transition getTransition($Element q, Object begin, Object end) {
            return new NoTransition(begin);
        }
    }
    /**
     * The TypedAttr for View ids
     */
    private static class IdAttr extends TypedAttr {
        public IdAttr(PropListener callback) {
            super(callback);
        }

        @Override
        Object format(AQuery q, View v, String value) {
            return q.formatId(value);
        }

        @Override
        Transition getTransition($Element q, Object begin, Object end) {
            return new NoTransition(begin);
        }
    }
    /**
     * The TypedAttr for dimensions (in dp, sp, etc)
     */
    private static class DimenAttr extends TypedAttr {
        public DimenAttr(PropListener callback) {
            super(callback);
        }

        @Override
        Object format(AQuery q, View v, String value) {
            return q.formatDimen(value);
        }

        @Override
        Transition getTransition($Element q, Object begin, Object end) {
            return new FloatTransition(floatCast(begin), floatCast(end));
        }
    }
    /**
     * The TypedAttr for sizes
     * It's basically the same as DimenAttr except that it also supports wrap_content and match_parent
     */
    private static abstract class SizeAttr extends TypedAttr {
        public SizeAttr(PropListener callback) {
            super(callback);
        }

        @Override
        Object format(AQuery q, View v, String value) {
            return q.formatSize(value);
        }
    }
    /**
     * The SizeAttr for width sizes
     */
    private static class WidthAttr extends SizeAttr {
        public WidthAttr(PropListener callback) {
            super(callback);
        }

        @Override
        Transition getTransition($Element q, Object begin, Object end) {
            return new SizeTransition(intCast(end), intCast(end), q.widthCast(begin), q.widthCast(end));
        }
    }
    /**
     * The SizeAttr for height sizes
     */
    private static class HeightAttr extends SizeAttr {
        public HeightAttr(PropListener callback) {
            super(callback);
        }

        @Override
        Transition getTransition($Element q, Object begin, Object end) {
            return new SizeTransition(intCast(end), intCast(end), q.heightCast(begin), q.heightCast(end));
        }
    }
    /**
     * The TypedAttr for colors
     */
    private static class ColorAttr extends TypedAttr {
        public ColorAttr(PropListener callback) {
            super(callback);
        }

        @Override
        Object format(AQuery q, View v, String value) {
            return q.formatColor(value);
        }

        @Override
        Transition getTransition($Element q, Object begin, Object end) {
            return new ColorTransition((int) begin, (int) end);
        }
    }
    /**
     * The TypedAttr for Drawables
     */
    private static class DrawableAttr extends TypedAttr {
        public DrawableAttr(PropListener callback) {
            super(callback);
        }

        @Override
        Object format(AQuery q, View v, String value) {
            return q.formatDrawable(value);
        }

        @Override
        Transition getTransition($Element q, Object begin, Object end) {
            return new DrawableTransition(drawableCast(begin), drawableCast(end));
        }
    }
    /**
     * The TypedAttr for any type of resource
     */
    private static class ResAttr extends TypedAttr {
        public ResAttr(PropListener callback) {
            super(callback);
        }

        @Override
        Object format(AQuery q, View v, String value) {
            return q.formatResource(value);
        }

        @Override
        Transition getTransition($Element q, Object begin, Object end) {
            return new NoTransition(begin);
        }
    }
    /**
     * The TypedAttr for tint colors
     */
    private static class TintAttr extends TypedAttr {
        public TintAttr(PropListener callback) {
            super(callback);
        }

        @Override
        Object format(AQuery q, View v, String value) {
            return q.formatTint(value);
        }

        @Override
        Transition getTransition($Element q, Object begin, Object end) {
            return new NoTransition(begin);
        }
    }
    /**
     * The TypedAttr for modes
     */
    private static class ModeAttr extends TypedAttr {
        public ModeAttr(PropListener callback) {
            super(callback);
        }

        @Override
        Object format(AQuery q, View v, String value) {
            return formatMode(value);
        }

        @Override
        Transition getTransition($Element q, Object begin, Object end) {
            return new NoTransition(begin);
        }
    }

    /**
     * A map containing all existing XML attributes and the JAVA code equivalent that allows to get and edit the value of this attribute
     * For example, ATTRS.get("id") will returns the functions v->v.getId() and v,value->v.setId(value)
     */
    private static final Map<String, AttrSetter> ATTRS = initAttrs();

    /**
     * Initialize the ATTRS variable
     * @return
     * The variable, an instance of Map<String, AttrSetter>
     */
    private static Map<String, AttrSetter> initAttrs() {
        HashMap<String, AttrSetter> res = new HashMap<>();
        put(res,"id", new IdAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.getId();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setId((int) value);
            }
        }));
        put(res,"text", new StringAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getText().toString();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                TextView tv = (TextView) v;
                try {
                    tv.setText((String) value);
                } catch (ClassCastException e) {
                    try {
                        tv.setText((int) value);
                    } catch (ClassCastException e2) {
                        throw e;
                    }
                }
            }
        }));
        put(res,"src", new DrawableAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((ImageView) v).getDrawable();
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void prop(AQuery q, View v, Object value) {
                ImageView iv = (ImageView) v;
                try {
                    iv.setImageDrawable((Drawable) value);
                } catch (ClassCastException e) {
                    try {
                        iv.setImageBitmap((Bitmap) value);
                    } catch (ClassCastException e2) {
                        try {
                            iv.setImageResource((int) value);
                        } catch (ClassCastException e3) {
                            throw e;
                        }
                    }
                }
            }
        }));
        put(res,"layout_width", new WidthAttr(new PropListener() {
            @Override
            public Object get(View v) {
                ViewGroup.LayoutParams lp = v.getLayoutParams();
                if (lp == null)
                    return 0;
                return lp.width;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                new $Element(q.ctx, v).width(intCast(value));
            }
        }));
        put(res,"width", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.getWidth();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setWidth(intCast(value));
            }
        }));
        put(res,"layout_height", new HeightAttr(new PropListener() {
            @Override
            public Object get(View v) {
                ViewGroup.LayoutParams lp = v.getLayoutParams();
                if (lp == null)
                    return 0;
                return lp.height;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                new $Element(q.ctx, v).height(intCast(value));
            }
        }));
        put(res,"height", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.getWidth();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setHeight(intCast(value));
            }
        }));
        put(res,"layout_weight", new FloatAttr(new PropListener() {
            @Override
            public Object get(View v) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) v.getLayoutParams();
                return lp.weight;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) v.getLayoutParams();
                lp.weight = floatCast(value);
                v.setLayoutParams(lp);
            }
        }));
        put(res,"gravity", new CustomAttr(new AttrListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public Object get(View v) {
                try {
                    return ((TextView) v).getGravity();
                }
                catch (ClassCastException e) {
                    try {
                        Spinner sv = (Spinner) v;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                            return sv.getGravity();
                    }
                    catch (ClassCastException e2) {
                        try {
                            RelativeLayout rl = (RelativeLayout) v;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                return rl.getGravity();
                        }
                        catch (ClassCastException e3) {
                            try {
                                GridView gv = (GridView) v;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                    return gv.getGravity();
                            }
                            catch (ClassCastException e4) {
                                throw e;
                            }
                        }
                    }
                }
                return Gravity.NO_GRAVITY;
            }

            @SuppressWarnings({"deprecation", "ConstantConditions"})
            @Override
            public void prop(AQuery q, View v, Object value) {
                int iValue = (int) value;
                try {
                    ((TextView) v).setGravity(iValue);
                }
                catch (ClassCastException e) {
                    try {
                        ((LinearLayout) v).setGravity(iValue);
                    }
                    catch (ClassCastException e2) {
                        try {
                            ((RelativeLayout) v).setGravity(iValue);
                        }
                        catch (ClassCastException e3) {
                            try {
                                ((GridView) v).setGravity(iValue);
                            }
                            catch (ClassCastException e4) {
                                try {
                                    ((Gallery) v).setGravity(iValue);
                                }
                                catch (ClassCastException e5) {
                                    try {
                                        Spinner sv = (Spinner) v;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                            sv.setGravity(iValue);
                                    }
                                    catch (ClassCastException e6) {
                                        throw e;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatGravity(value);
            }
        }));
        put(res,"orientation", new CustomAttr(new AttrListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public Object get(View v) {
                try {
                    return ((LinearLayout) v).getOrientation();
                }
                catch (ClassCastException e) {
                    try {
                        return ((GestureOverlayView) v).getOrientation();
                    }
                    catch (ClassCastException e2) {
                        try {
                            GridLayout gl = (GridLayout) v;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                                return gl.getOrientation();
                        }
                        catch (ClassCastException e3) {
                            throw e;
                        }
                        return LinearLayout.HORIZONTAL;
                    }
                }
            }

            @SuppressWarnings({"ResourceType", "ConstantConditions"})
            @Override
            public void prop(AQuery q, View v, Object value) {
                int iValue = (int) value;
                try {
                    ((LinearLayout) v).setOrientation(iValue);
                }
                catch (ClassCastException e) {
                    try {
                        ((GestureOverlayView) v).setOrientation(iValue);
                    }
                    catch (ClassCastException e2) {
                        try {
                            GridLayout gl = (GridLayout) v;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                                gl.setOrientation(iValue);
                        }
                        catch (ClassCastException e3) {
                            throw e;
                        }
                    }
                }
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatOrientation(value);
            }
        }));
        put(res,"layout_gravity", new CustomAttr(new AttrListener() {
            @Override
            public Object get(View v) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) v.getLayoutParams();
                return lp.gravity;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) v.getLayoutParams();
                lp.gravity = (int) value;
                v.setLayoutParams(lp);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatGravity(value);
            }
        }));
        put(res,"layout_marginTop", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return mlp(v).topMargin;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ViewGroup.MarginLayoutParams lp = mlp(v);
                lp.topMargin = intCast(value);
                v.setLayoutParams(lp);
            }
        }));
        put(res,"layout_marginLeft", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return mlp(v).leftMargin;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ViewGroup.MarginLayoutParams lp = mlp(v);
                lp.leftMargin = intCast(value);
                v.setLayoutParams(lp);
            }
        }));
        put(res,"layout_marginRight", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return mlp(v).rightMargin;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ViewGroup.MarginLayoutParams lp = mlp(v);
                lp.rightMargin = intCast(value);
                v.setLayoutParams(lp);
            }
        }));
        put(res,"layout_marginBottom", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return mlp(v).bottomMargin;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ViewGroup.MarginLayoutParams lp = mlp(v);
                lp.bottomMargin = intCast(value);
                v.setLayoutParams(lp);
            }
        }));
        put(res,"layout_marginStart", new DimenAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public Object get(View v) {
                return mlp(v).getMarginStart();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ViewGroup.MarginLayoutParams lp = mlp(v);
                lp.setMarginStart(intCast(value));
                v.setLayoutParams(lp);
            }
        }));
        put(res,"layout_marginEnd", new DimenAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public Object get(View v) {
                return mlp(v).getMarginEnd();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ViewGroup.MarginLayoutParams lp = mlp(v);
                lp.setMarginEnd(intCast(value));
                v.setLayoutParams(lp);
            }
        }));
        put(res,"layout_margin", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return mlp(v).leftMargin;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ViewGroup.MarginLayoutParams lp = mlp(v);
                int margin = intCast(value);
                lp.setMargins(margin, margin, margin, margin);
                v.setLayoutParams(lp);
            }
        }));
        put(res,"paddingLeft", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.getPaddingLeft();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setPadding(intCast(value), v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
            }
        }));
        put(res,"paddingTop", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.getPaddingTop();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setPadding(v.getPaddingLeft(), intCast(value), v.getPaddingRight(), v.getPaddingBottom());
            }
        }));
        put(res,"paddingRight", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.getPaddingRight();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), intCast(value), v.getPaddingBottom());
            }
        }));
        put(res,"paddingBottom", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.getPaddingBottom();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), intCast(value));
            }
        }));
        put(res, "paddingStart", new DimenAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public Object get(View v) {
                return v.getPaddingStart();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setPaddingRelative(intCast(value), v.getPaddingTop(), v.getPaddingEnd(), v.getPaddingBottom());
            }
        }));
        put(res, "paddingEnd", new DimenAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public Object get(View v) {
                return v.getPaddingEnd();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setPadding(v.getPaddingStart(), v.getPaddingTop(), intCast(value), v.getPaddingBottom());
            }
        }));
        put(res, "padding", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.getPaddingLeft();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                int padding = intCast(value);
                v.setPadding(padding, padding, padding, padding);
            }
        }));
        addLPRuleChecker(res, "layout_alignParentTop", RelativeLayout.ALIGN_PARENT_TOP);
        addLPRuleChecker(res, "layout_alignParentLeft", RelativeLayout.ALIGN_PARENT_LEFT);
        addLPRuleChecker(res, "layout_alignParentRight", RelativeLayout.ALIGN_PARENT_RIGHT);
        addLPRuleChecker(res, "layout_alignParentBottom", RelativeLayout.ALIGN_PARENT_BOTTOM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            addLPRuleChecker(res, "layout_alignParentStart", RelativeLayout.ALIGN_PARENT_START);
            addLPRuleChecker(res, "layout_alignParentEnd", RelativeLayout.ALIGN_PARENT_END);
            addLPRuleGetter(res, "layout_alignStart", RelativeLayout.ALIGN_START);
            addLPRuleGetter(res, "layout_alignEnd", RelativeLayout.ALIGN_END);
            addLPRuleGetter(res, "layout_toStartOf", RelativeLayout.START_OF);
            addLPRuleGetter(res, "layout_toEndOf", RelativeLayout.END_OF);
        }
        addLPRuleGetter(res, "layout_alignBaseline", RelativeLayout.ALIGN_BASELINE);
        addLPRuleGetter(res, "layout_above", RelativeLayout.ABOVE);
        addLPRuleGetter(res, "layout_below", RelativeLayout.BELOW);
        addLPRuleChecker(res, "layout_centerHorizontal", RelativeLayout.CENTER_HORIZONTAL);
        addLPRuleChecker(res, "layout_centerVertical", RelativeLayout.CENTER_VERTICAL);
        addLPRuleChecker(res, "layout_centerInParent", RelativeLayout.CENTER_IN_PARENT);
        addLPRuleGetter(res, "layout_toLeftOf", RelativeLayout.LEFT_OF);
        addLPRuleGetter(res, "layout_toRightOf", RelativeLayout.RIGHT_OF);
        put(res, "animateLayoutChanges", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return (((ViewGroup) v).getLayoutTransition() != null);
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ViewGroup vg = (ViewGroup) v;
                LayoutTransition lt = vg.getLayoutTransition();
                if ((boolean) value) {
                    if (lt == null)
                        vg.setLayoutTransition(new LayoutTransition());
                } else if (lt != null)
                    vg.setLayoutTransition(null);
            }
        }));
        put(res,"layout_column", new IntAttr(new PropListener() {
            @Override
            public Object get(View v) {
                TableRow.LayoutParams lp = (TableRow.LayoutParams) v.getLayoutParams();
                return lp.column;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                TableRow.LayoutParams lp = (TableRow.LayoutParams) v.getLayoutParams();
                lp.column = (int) value;
                v.setLayoutParams(lp);
            }
        }));
        put(res,"layout_row", new IntAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public Object get(View v) {
                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) v.getLayoutParams();
                return lp.rowSpec;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void prop(AQuery q, View v, Object value) {
                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) v.getLayoutParams();
                lp.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, (int) value);
                v.setLayoutParams(lp);
            }
        }));
        put(res,"layout_columnSpan", new IntAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public Object get(View v) {
                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) v.getLayoutParams();
                return lp.columnSpec;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void prop(AQuery q, View v, Object value) {
                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) v.getLayoutParams();
                lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, (int) value);
                v.setLayoutParams(lp);
            }
        }));
        put(res,"layout_rowWeight", new FloatAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public Object get(View v) {
                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) v.getLayoutParams();
                return lp.rowSpec;
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) v.getLayoutParams();
                lp.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, floatCast(value));
                v.setLayoutParams(lp);
            }
        }));
        put(res,"layout_columnWeight", new FloatAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public Object get(View v) {
                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) v.getLayoutParams();
                return lp.columnSpec;
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) v.getLayoutParams();
                lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, floatCast(value));
                v.setLayoutParams(lp);
            }
        }));
        put(res,"layout_x", new DimenAttr(new PropListener() {
            @SuppressWarnings("deprecation")
            @Override
            public Object get(View v) {
                AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams) v.getLayoutParams();
                return lp.x;
            }

            @SuppressWarnings("deprecation")
            @Override
            public void prop(AQuery q, View v, Object value) {
                AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams) v.getLayoutParams();
                lp.x = intCast(value);
                v.setLayoutParams(lp);
            }
        }));
        put(res,"layout_y", new DimenAttr(new PropListener() {
            @SuppressWarnings("deprecation")
            @Override
            public Object get(View v) {
                AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams) v.getLayoutParams();
                return lp.y;
            }

            @SuppressWarnings("deprecation")
            @Override
            public void prop(AQuery q, View v, Object value) {
                AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams) v.getLayoutParams();
                lp.y = intCast(value);
                v.setLayoutParams(lp);
            }
        }));
        put(res,"background", new DrawableAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.getBackground();
            }

            @SuppressWarnings({"deprecation", "ConstantConditions"})
            @Override
            public void prop(AQuery q, View v, Object value) {
                try {
                    v.setBackgroundDrawable((Drawable) value);
                } catch (ClassCastException e) {
                    try {
                        v.setBackgroundDrawable(new BitmapDrawable(q.ctx.getResources(), (Bitmap) value));
                    } catch (ClassCastException e2) {
                        try {
                            v.setBackgroundResource((int) value);
                        } catch (ClassCastException e3) {
                            throw e;
                        }
                    }
                }
            }
        }));
        put(res,"textColor", new ColorAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getCurrentTextColor();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setTextColor((int) value);
            }
        }));
        put(res,"textSize", new CustomAttr(new AttrListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getTextSize();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setTextSize(floatCast(value));
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return q.toSP(q.formatDimen(value));
            }
        }));
        put(res,"visibility", new CustomAttr(new AttrListener() {
            @Override
            public Object get(View v) {
                return v.getVisibility();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setVisibility((int) value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatVisibility(value);
            }
        }));
        put(res,"weightSum", new FloatAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((LinearLayout) v).getWeightSum();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((LinearLayout) v).setWeightSum(floatCast(value));
            }
        }));
        put(res,"alpha", new FloatAttr(new PropListener() {
            @Override
            public Object get(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    return v.getAlpha();
                else
                    return 1;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    v.setAlpha(floatCast(value));
            }
        }));
        put(res,"onClick", new CustomAttr(new AttrListener() {
            @Override
            public Object get(View v) {
                return null;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setOnClickListener((View.OnClickListener) value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return q.formatOnClick(value);
            }
        }));
        put(res,"accessibilityLiveRegion", new IntAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public Object get(View v) {
                return v.getAccessibilityLiveRegion();
            }

            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setAccessibilityLiveRegion((int) value);
            }
        }));
        put(res,"accessibilityTraversalAfter", new IntAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public Object get(View v) {
                return v.getAccessibilityTraversalAfter();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setAccessibilityTraversalAfter((int) value);
            }
        }));
        put(res,"accessibilityTraversalBefore", new IntAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public Object get(View v) {
                return v.getAccessibilityTraversalBefore();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setAccessibilityTraversalBefore((int) value);
            }
        }));
        put(res,"addStatesFromChildren", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((ViewGroup) v).addStatesFromChildren();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ViewGroup) v).setAddStatesFromChildren((boolean) value);
            }
        }));
        put(res,"alwaysDrawnWithCache", new BoolAttr(new PropListener() {
            @SuppressWarnings("deprecation")
            @Override
            public Object get(View v) {
                return ((ViewGroup) v).isAlwaysDrawnWithCacheEnabled();
            }

            @SuppressWarnings("deprecation")
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ViewGroup) v).setAlwaysDrawnWithCacheEnabled((boolean) value);
            }
        }));
        put(res,"animationCache", new BoolAttr(new PropListener() {
            @SuppressWarnings("deprecation")
            @Override
            public Object get(View v) {
                return ((ViewGroup) v).isAnimationCacheEnabled();
            }

            @SuppressWarnings("deprecation")
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ViewGroup) v).setAnimationCacheEnabled((boolean) value);
            }
        }));
        put(res,"backgroundTint", new TintAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return v.getBackgroundTintList();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setBackgroundTintList((ColorStateList) value);
            }
        }));
        put(res,"backgroundTintMode", new ModeAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return v.getBackgroundTintMode();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setBackgroundTintMode((PorterDuff.Mode) value);
            }
        }));
        put(res,"clickable", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.isClickable();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setClickable((boolean) value);
            }
        }));
        put(res,"clipChildren", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public Object get(View v) {
                return ((ViewGroup) v).getClipChildren();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ViewGroup) v).setClipChildren((boolean) value);
            }
        }));
        put(res,"clipToPadding", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((ViewGroup) v).getClipToPadding();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ViewGroup) v).setClipToPadding((boolean) value);
            }
        }));
        put(res,"contentDescription", new StringAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.getContentDescription().toString();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setContentDescription((String) value);
            }
        }));
        put(res,"contextClickable", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public Object get(View v) {
                return v.isContextClickable();
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setContextClickable((boolean) value);
            }
        }));
        put(res,"descendantFocusability", new CustomAttr(new AttrListener() {
            @Override
            public Object get(View v) {
                return ((ViewGroup) v).getDescendantFocusability();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ViewGroup) v).setDescendantFocusability((int) value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatFocusability(value);
            }
        }));
        put(res,"drawingCacheQuality", new CustomAttr(new AttrListener() {
            @Override
            public Object get(View v) {
                return v.getDrawingCacheQuality();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setDrawingCacheQuality((int) value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatQuality(value);
            }
        }));
        put(res,"duplicateParentState", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.isDuplicateParentStateEnabled();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setDuplicateParentStateEnabled((boolean) value);
            }
        }));
        put(res,"elevation", new DimenAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return v.getElevation();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setElevation(floatCast(value));
            }
        }));
        put(res,"fadeScrollbars", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.isScrollbarFadingEnabled();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setScrollbarFadingEnabled((boolean) value);
            }
        }));
        putAll(res, new String[]{"fadingEdge", "requiresFadingEdge"}, new CustomAttr(new AttrListener() {
            @Override
            public Object get(View v) {
                return orientationFlags(v.isHorizontalFadingEdgeEnabled(), v.isVerticalFadingEdgeEnabled());
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                boolean[] orientations = (boolean[]) value;
                v.setHorizontalFadingEdgeEnabled(orientations[0]);
                v.setVerticalFadingEdgeEnabled(orientations[1]);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatOrientations(value);
            }
        }));
        put(res, "fadingEdgeLength", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                if (v.isHorizontalFadingEdgeEnabled())
                    return v.getHorizontalFadingEdgeLength();
                if (v.isVerticalFadingEdgeEnabled())
                    return v.getVerticalFadingEdgeLength();
                return 0;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setFadingEdgeLength(intCast(value));
            }
        }));
        put(res,"filterTouchesWhenObscured", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.GINGERBREAD)
            @Override
            public Object get(View v) {
                return v.getFilterTouchesWhenObscured();
            }

            @TargetApi(Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setFilterTouchesWhenObscured((boolean) value);
            }
        }));
        put(res,"fitsSystemWindows", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return v.getFitsSystemWindows();
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setFitsSystemWindows((boolean) value);
            }
        }));
        put(res,"focusable", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.isFocusable();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setFocusable((boolean) value);
            }
        }));
        put(res,"focusableInTouchMode", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.isFocusableInTouchMode();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setFocusableInTouchMode((boolean) value);
            }
        }));
        put(res,"hapticFeedbackEnabled", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.isHapticFeedbackEnabled();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setHapticFeedbackEnabled((boolean) value);
            }
        }));
        put(res,"importantForAccessibility", new IntAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return v.getImportantForAccessibility();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setImportantForAccessibility((int) value);
            }
        }));
        put(res,"isScrollContainer", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return v.isScrollContainer();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setScrollContainer((boolean) value);
            }
        }));
        put(res,"keepScreenOn", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.getKeepScreenOn();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setKeepScreenOn((boolean) value);
            }
        }));
        put(res,"labelFor", new IdAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public Object get(View v) {
                return v.getLabelFor();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setLabelFor((int) value);
            }
        }));
        put(res,"layerType", new CustomAttr(new AttrListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return v.getLayerType();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setLayerType((int) value, null);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatLayer(value);
            }
        }));
        put(res,"layoutDirection", new CustomAttr(new AttrListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public Object get(View v) {
                return v.getLayoutDirection();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setLayoutDirection((int) value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatDirection(value);
            }
        }));
        put(res,"layoutAnimation", new CustomAttr(new AttrListener() {
            @Override
            public Object get(View v) {
                return ((ViewGroup) v).getLayoutAnimation();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ViewGroup) v).setLayoutAnimation((LayoutAnimationController) value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return q.formatAnimation(value);
            }
        }));
        put(res,"layoutMode", new CustomAttr(new AttrListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public Object get(View v) {
                return ((ViewGroup) v).getLayoutMode();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ViewGroup) v).setLayoutMode((int) value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatLayout(value);
            }
        }));
        put(res,"longClickable", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.isLongClickable();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setLongClickable((boolean) value);
            }
        }));
        put(res,"measureWithLargestChild", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return ((LinearLayout) v).isMeasureWithLargestChildEnabled();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((LinearLayout) v).setMeasureWithLargestChildEnabled((boolean) value);
            }
        }));
        put(res,"nestedScrollingEnabled", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return v.isNestedScrollingEnabled();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setNestedScrollingEnabled((boolean) value);
            }
        }));
        put(res,"nextFocusDown", new IdAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.getNextFocusDownId();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setNextFocusDownId((int) value);
            }
        }));
        put(res,"nextFocusForward", new IdAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return v.getNextFocusForwardId();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setNextFocusForwardId((int) value);
            }
        }));
        put(res,"nextFocusLeft", new IdAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.getNextFocusLeftId();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setNextFocusLeftId((int) value);
            }
        }));
        put(res,"nextFocusRight", new IdAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.getNextFocusRightId();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setNextFocusRightId((int) value);
            }
        }));
        put(res,"nextFocusUp", new IdAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.getNextFocusUpId();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setNextFocusUpId((int) value);
            }
        }));
        put(res,"outlineProvider", new CustomAttr(new AttrListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return v.getOutlineProvider();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setOutlineProvider((ViewOutlineProvider) value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatProvider(value);
            }
        }));
        put(res,"overScrollMode", new CustomAttr(new AttrListener() {
            @TargetApi(Build.VERSION_CODES.GINGERBREAD)
            @Override
            public Object get(View v) {
                return v.getOverScrollMode();
            }

            @TargetApi(Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setOverScrollMode((int) value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatScroll(value);
            }
        }));
        put(res,"persistentDrawingCache", new CustomAttr(new AttrListener() {
            @Override
            public Object get(View v) {
                return ((ViewGroup) v).getPersistentDrawingCache();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ViewGroup) v).setPersistentDrawingCache((int) value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatCache(value);
            }
        }));
        put(res,"rotation", new FloatAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return v.getRotation();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setRotation(floatCast(value));
            }
        }));
        put(res,"rotationX", new FloatAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return v.getRotationX();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setRotationX(floatCast(value));
            }
        }));
        put(res,"rotationY", new FloatAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return v.getRotationY();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setRotationY(floatCast(value));
            }
        }));
        put(res,"saveEnabled", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.isSaveEnabled();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setSaveEnabled((boolean) value);
            }
        }));
        put(res,"scaleX", new FloatAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return v.getScaleX();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setScaleX(floatCast(value));
            }
        }));
        put(res,"scaleY", new FloatAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return v.getScaleY();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setScaleY(floatCast(value));
            }
        }));
        put(res,"scrollIndicators", new CustomAttr(new AttrListener() {
            @Override
            public Object format(AQuery q, View v, String value) {
                return formatIndicator(value);
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public Object get(View v) {
                return v.getScrollIndicators();
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setScrollIndicators((int) value);
            }
        }));
        put(res,"scrollbarDefaultDelayBeforeFade", new IntAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return v.getScrollBarDefaultDelayBeforeFade();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setScrollBarDefaultDelayBeforeFade(intCast(value));
            }
        }));
        put(res,"scrollbarFadeDuration", new IntAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return v.getScrollBarFadeDuration();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setScrollBarFadeDuration(intCast(value));
            }
        }));
        put(res,"scrollbars", new CustomAttr(new AttrListener() {
            @Override
            public Object get(View v) {
                return orientationFlags(v.isHorizontalScrollBarEnabled(),v.isVerticalScrollBarEnabled());
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                boolean[] orientations = (boolean[]) value;
                v.setHorizontalScrollBarEnabled(orientations[0]);
                v.setVerticalScrollBarEnabled(orientations[1]);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatOrientations(value);
            }
        }));
        put(res,"scrollbarSize", new DimenAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return v.getScrollBarSize();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setScrollBarSize(intCast(value));
            }
        }));
        put(res,"scrollbarStyle", new CustomAttr(new AttrListener() {
            @Override
            public Object get(View v) {
                return v.getScrollBarStyle();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setScrollBarStyle((int) (value));
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatStyle(value);
            }
        }));
        put(res,"scrollX", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.getScrollX();
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setScrollX(intCast(value));
            }
        }));
        put(res,"scrollY", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.getScrollY();
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setScrollY(intCast(value));
            }
        }));
        put(res,"showDividers", new CustomAttr(new AttrListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return ((LinearLayout) v).getShowDividers();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((LinearLayout) v).setShowDividers((int) value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatDividers(value);
            }
        }));
        put(res,"soundEffectsEnabled", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.isSoundEffectsEnabled();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setSoundEffectsEnabled((boolean) value);
            }
        }));
        put(res,"splitMotionEvents", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return ((ViewGroup) v).isMotionEventSplittingEnabled();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ViewGroup) v).setMotionEventSplittingEnabled((boolean) value);
            }
        }));
        put(res,"stateListAnimator", new CustomAttr(new AttrListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return v.getStateListAnimator();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setStateListAnimator((StateListAnimator) value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return value;
            }
        }));
        put(res,"tag", new CustomAttr(new AttrListener() {
            @Override
            public Object get(View v) {
                return v.getTag();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setTag(value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return q.formatString(value);
            }
        }));
        put(res,"textAlignment", new CustomAttr(new AttrListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public Object get(View v) {
                return v.getTextAlignment();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setTextAlignment((int) value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatTextAlign(value);
            }
        }));
        put(res,"textDirection", new CustomAttr(new AttrListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public Object get(View v) {
                return v.getTextDirection();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setTextDirection((int) value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatDirection(value);
            }
        }));
        put(res,"touchscreenBlocksFocus", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((ViewGroup) v).getTouchscreenBlocksFocus();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ViewGroup) v).setTouchscreenBlocksFocus((boolean) value);
            }
        }));
        put(res,"transformPivotX", new FloatAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return v.getPivotX();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setPivotX(floatCast(value));
            }
        }));
        put(res,"transformPivotY", new FloatAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return v.getPivotY();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setPivotY(floatCast(value));
            }
        }));
        put(res,"transitionGroup", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((ViewGroup) v).isTransitionGroup();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ViewGroup) v).setTransitionGroup((boolean) value);
            }
        }));
        put(res,"transitionName", new StringAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return v.getTransitionName();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setTransitionName((String) value);
            }
        }));
        put(res,"translationX", new DimenAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return v.getTranslationX();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setTranslationX(floatCast(value));
            }
        }));
        put(res,"translationY", new DimenAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return v.getTranslationY();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setTranslationY(floatCast(value));
            }
        }));
        put(res,"translationZ", new DimenAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return v.getTranslationZ();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setTranslationZ(floatCast(value));
            }
        }));
        put(res,"verticalScrollbarPosition", new CustomAttr(new AttrListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return v.getVerticalScrollbarPosition();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setVerticalScrollbarPosition((int) value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatPosition(value);
            }
        }));
        put(res,"autoLink", new CustomAttr(new AttrListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getAutoLinkMask();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setAutoLinkMask((int) value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatAutoLink(value);
            }
        }));
        put(res,"cursorVisible", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((TextView) v).isCursorVisible();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setCursorVisible((boolean) value);
            }
        }));
        put(res,"capitalize", new CustomAttr(new AttrListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getInputType();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setInputType((int) value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatCaps(value);
            }
        }));
        put(res,"digits", new StringAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getKeyListener();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setKeyListener(DigitsKeyListener.getInstance((String) value));
            }
        }));
        put(res,"drawableBottom", new DrawableAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getCompoundDrawables()[3];
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                TextView tv = (TextView)v;
                Drawable[] d = tv.getCompoundDrawables();
                tv.setCompoundDrawables(d[0], d[1], d[2], drawableCast(value));
            }
        }));
        put(res,"drawableLeft", new DrawableAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getCompoundDrawables()[0];
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                TextView tv = (TextView)v;
                Drawable[] d = tv.getCompoundDrawables();
                tv.setCompoundDrawables(drawableCast(value), d[1], d[2], d[3]);
            }
        }));
        put(res,"drawableRight", new DrawableAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getCompoundDrawables()[2];
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                TextView tv = (TextView)v;
                Drawable[] d = tv.getCompoundDrawables();
                tv.setCompoundDrawables(d[0], d[1], drawableCast(value), d[3]);
            }
        }));
        put(res,"drawableTop", new DrawableAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getCompoundDrawables()[1];
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                TextView tv = (TextView)v;
                Drawable[] d = tv.getCompoundDrawables();
                tv.setCompoundDrawables(d[0], drawableCast(value), d[2], d[3]);
            }
        }));
        put(res,"drawableStart", new DrawableAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public Object get(View v) {
                return ((TextView) v).getCompoundDrawablesRelative()[0];
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void prop(AQuery q, View v, Object value) {
                TextView tv = (TextView)v;
                Drawable[] d = tv.getCompoundDrawablesRelative();
                tv.setCompoundDrawablesRelative(drawableCast(value), d[1], d[2], d[3]);
            }
        }));
        put(res,"drawableEnd", new DrawableAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public Object get(View v) {
                return ((TextView) v).getCompoundDrawablesRelative()[2];
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void prop(AQuery q, View v, Object value) {
                TextView tv = (TextView)v;
                Drawable[] d = tv.getCompoundDrawablesRelative();
                tv.setCompoundDrawablesRelative(d[0], d[1], drawableCast(value), d[3]);
            }
        }));
        put(res,"editable", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return (null != ((TextView) v).getEditableText());
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setFilters((boolean) value ? new InputFilter[]{
                        new InputFilter() {
                            public CharSequence filter(CharSequence src, int start,
                                                       int end, Spanned dst, int dstart, int dend) {
                                return src.length() < 1 ? dst.subSequence(dstart, dend) : "";
                            }
                        }
                } : null);
            }
        }));
        put(res,"editorExtras", new ResAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getInputExtras(false);
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                try {
                    ((TextView) v).setInputExtras((int) value);
                }
                catch (XmlPullParserException e) {
                    throw new IllegalArgumentException(e);
                }
                catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }));
        put(res,"elegantTextHeight", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return false;
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setElegantTextHeight((boolean) value);
            }
        }));
        put(res,"ellipsize", new CustomAttr(new AttrListener() {
            @Override
            public Object format(AQuery q, View v, String value) {
                return formatEllipse(value);
            }

            @Override
            public Object get(View v) {
                return ((TextView) v).getEllipsize();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setEllipsize((TextUtils.TruncateAt) value);
            }
        }));
        put(res,"ems", new IntAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                TextView tv = (TextView)v;
                return (tv.getMinEms()+tv.getMaxEms())/2;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setEms(intCast(value));
            }
        }));
        put(res,"fontFamily", new CustomAttr(new AttrListener() {
            @Override
            public Object format(AQuery q, View v, String value) {
                return Typeface.create(value,((TextView) v).getTypeface().getStyle());
            }

            @Override
            public Object get(View v) {
                return ((TextView) v).getTypeface();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setTypeface((Typeface) value);
            }
        }));
        put(res,"fontFeatureSettings", new StringAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((TextView) v).getFontFeatureSettings();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setFontFeatureSettings((String) value);
            }
        }));
        put(res,"freezesText", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getFreezesText();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setFreezesText((boolean) value);
            }
        }));
        put(res,"hint", new StringAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getHint();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                TextView tv = (TextView) v;
                try {
                    tv.setHint((String) value);
                } catch (ClassCastException e) {
                    try {
                        tv.setHint((int) value);
                    } catch (ClassCastException e2) {
                        throw e;
                    }
                }
            }
        }));
        put(res,"imeActionId", new IdAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getImeActionId();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setImeActionLabel(((TextView) v).getImeActionLabel(), (int) value);
            }
        }));
        put(res,"imeActionLabel", new StringAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getImeActionLabel().toString();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setImeActionLabel((String) value, ((TextView) v).getImeActionId());
            }
        }));
        put(res,"imeOptions", new CustomAttr(new AttrListener() {
            @Override
            public Object format(AQuery q, View v, String value) {
                return formatOptions(value);
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public Object get(View v) {
                try {
                    return ((TextView) v).getImeOptions();
                }
                catch (ClassCastException e) {
                    try {
                        SearchView sv = (SearchView) v;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            return sv.getImeOptions();
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                    return EditorInfo.IME_NULL;
                }
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void prop(AQuery q, View v, Object value) {
                int iValue = (int) value;
                try {
                    ((TextView) v).setImeOptions(iValue);
                }
                catch (ClassCastException e) {
                    try {
                        SearchView sv = (SearchView) v;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                            sv.setImeOptions(iValue);
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                }
            }
        }));
        put(res,"includeFontPadding", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((TextView) v).getIncludeFontPadding();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setIncludeFontPadding((boolean) value);
            }
        }));
        put(res,"inputType", new CustomAttr(new AttrListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void prop(AQuery q, View v, Object value) {
                int iValue = (int) value;
                try {
                    ((TextView) v).setInputType(iValue);
                }
                catch (ClassCastException e) {
                    try {
                        SearchView sv = (SearchView) v;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                            sv.setInputType(iValue);
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                }
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatInputType(value);
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public Object get(View v) {
                try {
                    return ((TextView) v).getInputType();
                }
                catch (ClassCastException e) {
                    try {
                        SearchView sv = (SearchView) v;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            return sv.getInputType();
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                    return InputType.TYPE_NULL;
                }
            }
        }));
        put(res,"letterSpacing", new FloatAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setLetterSpacing(floatCast(value));
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((TextView) v).getLetterSpacing();
            }
        }));
        put(res,"lineSpacingExtra", new FloatAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                TextView tv = (TextView)v;
                tv.setLineSpacing(floatCast(value), tv.getLineSpacingMultiplier());
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((TextView) v).getLineSpacingExtra();
            }
        }));
        put(res,"lineSpacingMultiplier", new FloatAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                TextView tv = (TextView)v;
                tv.setLineSpacing(tv.getLineSpacingExtra(), floatCast(value));
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((TextView) v).getLineSpacingMultiplier();
            }
        }));
        put(res,"lines", new IntAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getLineCount();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setLines((int) value);
            }
        }));
        put(res,"linksClickable", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getLinksClickable();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setLinksClickable((boolean) value);
            }
        }));
        put(res,"marqueeRepeatLimit", new IntAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((TextView) v).getMarqueeRepeatLimit();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setMarqueeRepeatLimit((int) value);
            }
        }));
        put(res,"maxEms", new IntAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((TextView) v).getMaxEms();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setMaxEms(intCast(value));
            }
        }));
        put(res,"maxHeight", new DimenAttr(new PropListener() {
            @SuppressWarnings("ConstantConditions")
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                try {
                    return ((TextView) v).getMaxHeight();
                }
                catch (ClassCastException e) {
                    try {
                        return ((ImageView) v).getMaxHeight();
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                }
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void prop(AQuery q, View v, Object value) {
                int iValue = intCast(value);
                try {
                    ((TextView) v).setMaxHeight(iValue);
                }
                catch (ClassCastException e) {
                    try {
                        ((ImageView) v).setMaxHeight(iValue);
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                }
            }
        }));
        put(res,"maxLength", new IntAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getFilters();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setFilters(new InputFilter[]{new InputFilter.LengthFilter((int) value)});
            }
        }));
        put(res,"maxLines", new IntAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((TextView) v).getMaxLines();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setMaxLines((int) value);
            }
        }));
        put(res,"maxWidth", new DimenAttr(new PropListener() {
            @SuppressWarnings("ConstantConditions")
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                try {
                    return ((TextView) v).getMaxWidth();
                }
                catch (ClassCastException e) {
                    try {
                        return ((ImageView) v).getMaxWidth();
                    }
                    catch (ClassCastException e2) {
                        try {
                            return ((SearchView) v).getMaxWidth();
                        }
                        catch (ClassCastException e3) {
                            throw e;
                        }
                    }
                }
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void prop(AQuery q, View v, Object value) {
                int iValue = intCast(value);
                try {
                    ((TextView) v).setMaxWidth(iValue);
                }
                catch (ClassCastException e) {
                    try {
                        ((ImageView) v).setMaxWidth(iValue);
                    }
                    catch (ClassCastException e2) {
                        try {
                            SearchView sv = (SearchView) v;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                sv.setMaxWidth(iValue);
                        }
                        catch (ClassCastException e3) {
                            throw e;
                        }
                    }
                }
            }
        }));
        put(res,"minEms", new IntAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((TextView) v).getMinEms();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setMinEms(intCast(value));
            }
        }));
        put(res,"minHeight", new DimenAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                try {
                    return ((TextView) v).getMinHeight();
                }
                catch (ClassCastException e) {
                    return v.getMinimumHeight();
                }
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                int iValue = intCast(value);
                try {
                    ((TextView) v).setMinHeight(iValue);
                }
                catch (ClassCastException e) {
                    v.setMinimumHeight(iValue);
                }
            }
        }));
        put(res,"minLines", new DimenAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((TextView) v).getMinLines();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setMinLines(intCast(value));
            }
        }));
        put(res,"minWidth", new DimenAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                try {
                    return ((TextView) v).getMinWidth();
                }
                catch (ClassCastException e) {
                    return v.getMinimumWidth();
                }
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                int iValue = intCast(value);
                try {
                    ((TextView) v).setMinWidth(iValue);
                }
                catch (ClassCastException e) {
                    v.setMinimumWidth(iValue);
                }
            }
        }));
        put(res,"numeric", inputTypeAttr(InputType.TYPE_NUMBER_FLAG_DECIMAL));
        put(res,"password", inputTypeAttr(InputType.TYPE_TEXT_VARIATION_PASSWORD));
        put(res,"phoneNumber", inputTypeAttr(InputType.TYPE_CLASS_PHONE));
        put(res,"privateImeOptions", new StringAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getPrivateImeOptions();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setPrivateImeOptions((String) value);
            }
        }));
        put(res,"scrollHorizontally", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public Object get(View v) {
                return v.isHorizontalScrollBarEnabled();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setHorizontallyScrolling((boolean) value);
            }
        }));
        put(res,"selectAllOnFocus", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return false;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setSelectAllOnFocus((boolean) value);
            }
        }));
        put(res,"shadowColor", new ColorAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((TextView) v).getShadowColor();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                TextView tv = (TextView)v;
                tv.setShadowLayer(tv.getShadowRadius(), tv.getShadowDx(), tv.getShadowDy(), (int) value);
            }
        }));
        put(res,"shadowDx", new FloatAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((TextView) v).getShadowDx();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                TextView tv = (TextView)v;
                tv.setShadowLayer(tv.getShadowRadius(), floatCast(value), tv.getShadowDy(), tv.getShadowColor());
            }
        }));
        put(res,"shadowDy", new FloatAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((TextView) v).getShadowDy();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                TextView tv = (TextView)v;
                tv.setShadowLayer(tv.getShadowRadius(),tv.getShadowDx(),floatCast(value),tv.getShadowColor());
            }
        }));
        put(res,"shadowRadius", new FloatAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((TextView) v).getShadowRadius();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                TextView tv = (TextView)v;
                tv.setShadowLayer(floatCast(value), tv.getShadowDx(), tv.getShadowDy(), tv.getShadowColor());
            }
        }));
        put(res,"singleLine", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return (1 == ((TextView) v).getInputType());
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setSingleLine((boolean) value);
            }
        }));
        put(res,"textAllCaps", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return false;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setAllCaps((boolean) value);
            }
        }));
        put(res,"textAppearance", new ResAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return null;
            }

            @SuppressWarnings("deprecation")
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setTextAppearance(q.ctx, (int) value);
            }
        }));
        put(res,"textColorHighlight", new ColorAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((TextView) v).getHighlightColor();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setHighlightColor((int) value);
            }
        }));
        put(res,"textColorHint", new ColorAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getHintTextColors();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setHintTextColor((int) value);
            }
        }));
        put(res,"textColorLink", new ColorAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getLinkTextColors();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setLinkTextColor((int) value);
            }
        }));
        put(res,"textIsSelectable", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return ((TextView) v).isTextSelectable();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextView) v).setTextIsSelectable((boolean) value);
            }
        }));
        put(res,"textScaleX", new FloatAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getTextScaleX();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setScaleX(floatCast(value));
            }
        }));
        put(res,"textStyle", new CustomAttr(new AttrListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getTypeface().getStyle();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                TextView tv = ((TextView) v);
                tv.setTypeface(tv.getTypeface(),(int) value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatTextStyle(value);
            }
        }));
        put(res,"typeface", new CustomAttr(new AttrListener() {
            @Override
            public Object get(View v) {
                return ((TextView) v).getTypeface();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                TextView tv = ((TextView) v);
                tv.setTypeface((Typeface) value);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return formatTypeface(value);
            }
        }));
        put(res,"adjustViewBounds", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((ImageView) v).getAdjustViewBounds();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ImageView) v).setAdjustViewBounds((boolean) value);
            }
        }));
        put(res,"baseline", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.getBaseline();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ImageView) v).setBaseline(intCast(value));
            }
        }));
        put(res,"baselineAlignBottom", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return ((ImageView) v).getBaselineAlignBottom();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ImageView) v).setBaselineAlignBottom((boolean) value);
            }
        }));
        put(res,"cropToPadding", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((ImageView) v).getCropToPadding();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ImageView) v).setCropToPadding((boolean) value);
            }
        }));
        put(res,"scaleType", new CustomAttr(new AttrListener() {
            @Override
            public Object format(AQuery q, View v, String value) {
                return formatScale(value);
            }

            @Override
            public Object get(View v) {
                return ((ImageView) v).getScaleType();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ImageView) v).setScaleType((ImageView.ScaleType) value);
            }
        }));
        put(res,"tint", new TintAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((ImageView) v).getImageTintList();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ImageView) v).setImageTintList((ColorStateList) value);
            }
        }));
        put(res,"tintMode", new ModeAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((ImageView) v).getImageTintMode();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ImageView) v).setImageTintMode((PorterDuff.Mode) value);
            }
        }));
        put(res,"dropDownHorizontalOffset", new IntAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((Spinner) v).getDropDownHorizontalOffset();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((Spinner) v).setDropDownHorizontalOffset(intCast(value));
            }
        }));
        put(res,"dropDownVerticalOffset", new IntAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((Spinner) v).getDropDownVerticalOffset();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((Spinner) v).setDropDownVerticalOffset(intCast(value));
            }
        }));
        put(res,"dropDownWidth", new WidthAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((Spinner) v).getDropDownWidth();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((Spinner) v).setDropDownWidth((int) value);
            }
        }));
        put(res,"popupBackground", new DrawableAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((Spinner) v).getPopupBackground();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                Spinner sv = (Spinner) v;
                try {
                    sv.setPopupBackgroundDrawable((Drawable) value);
                }
                catch (ClassCastException e) {
                    sv.setPopupBackgroundResource((int) value);
                }
            }
        }));
        put(res,"prompt", new StringAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((Spinner) v).getPrompt();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((Spinner) v).setPrompt((String) value);
            }
        }));
        put(res,"entries", new CustomAttr(new AttrListener() {
            @Override
            public Object format(AQuery q, View v, String value) {
                return q.formatStringArray(value);
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public Object get(View v) {
                Adapter adapter;
                try {
                    adapter = ((Spinner) v).getAdapter();
                }
                catch (ClassCastException e) {
                    try {
                        adapter = ((ListView) v).getAdapter();
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                }
                String[] res = new String[adapter.getCount()];
                for (int i = 0; i < adapter.getCount(); i++)
                    res[i] = (String) adapter.getItem(i);
                return res;
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void prop(AQuery q, View v, Object value) {
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(q.ctx, android.R.layout.simple_spinner_item, (String[]) value);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                try {
                    ((Spinner) v).setAdapter(spinnerArrayAdapter);
                }
                catch (ClassCastException e) {
                    try {
                        ((ListView) v).setAdapter(spinnerArrayAdapter);
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                }
            }
        }));
        put(res,"checked", new BoolAttr(new PropListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public Object get(View v) {
                return ((Checkable) v).isChecked();
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((Checkable) v).setChecked((boolean) value);
            }
        }));
        put(res, "enabled", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.isEnabled();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setEnabled((boolean) value);
            }
        }));
        put(res,"ignoreGravity", new IdAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return 0;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((RelativeLayout) v).setIgnoreGravity((int) value);
            }
        }));
        put(res,"button", new DrawableAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((CompoundButton) v).getCompoundDrawables();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((CompoundButton) v).setButtonDrawable((Drawable) value);
            }
        }));
        put(res,"buttonTint", new TintAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((CompoundButton) v).getButtonTintList();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((CompoundButton) v).setButtonTintList((ColorStateList) value);
            }
        }));
        put(res,"buttonTintMode", new ModeAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((RadioButton) v).getButtonTintMode();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((RadioButton) v).setButtonTintMode((PorterDuff.Mode) value);
            }
        }));
        put(res,"checkedButton", new IdAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((RadioGroup) v).getCheckedRadioButtonId();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((RadioGroup) v).check((int) value);
            }
        }));
        put(res,"showText", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((Switch) v).getShowText();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((Switch) v).setShowText((boolean) value);
            }
        }));
        put(res,"splitTrack", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((Switch) v).getSplitTrack();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((Switch) v).setSplitTrack((boolean) value);
            }
        }));
        put(res,"switchMinWidth", new DimenAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((Switch) v).getSwitchMinWidth();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((Switch) v).setSwitchMinWidth(intCast(value));
            }
        }));
        put(res,"switchPadding", new DimenAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((Switch) v).getSwitchPadding();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((Switch) v).setSwitchPadding(intCast(value));
            }
        }));
        put(res,"switchTextAppearance", new ResAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return null;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((Switch) v).setSwitchTextAppearance(q.ctx, (int) value);
            }
        }));
        put(res,"textOff", new StringAttr(new PropListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public Object get(View v) {
                try {
                    return ((ToggleButton) v).getTextOff();
                }
                catch (ClassCastException e) {
                    try {
                        Switch s = (Switch) v;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                            return s.getTextOff();
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                    return null;
                }
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void prop(AQuery q, View v, Object value) {
                String sValue = (String) value;
                try {
                    ((ToggleButton) v).setTextOff(sValue);
                }
                catch (ClassCastException e) {
                    try {
                        Switch s = (Switch) v;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                            s.setTextOff(sValue);
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                }
            }
        }));
        put(res,"textOn", new StringAttr(new PropListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public Object get(View v) {
                try {
                    return ((ToggleButton) v).getTextOn();
                }
                catch (ClassCastException e) {
                    try {
                        Switch s = (Switch) v;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                            return s.getTextOn();
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                    return null;
                }
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void prop(AQuery q, View v, Object value) {
                String sValue = (String) value;
                try {
                    ((ToggleButton) v).setTextOn(sValue);
                }
                catch (ClassCastException e) {
                    try {
                        Switch s = (Switch) v;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                            s.setTextOn(sValue);
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                }
            }
        }));
        put(res,"thumb", new DrawableAttr(new PropListener() {
            @SuppressWarnings("ConstantConditions")
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                try {
                    return ((SeekBar) v).getThumb();
                }
                catch (ClassCastException e) {
                    try {
                        return ((Switch) v).getThumbDrawable();
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                }
            }

            @SuppressWarnings("ConstantConditions")
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                try {
                    ((SeekBar) v).setThumb((Drawable) value);
                }
                catch (ClassCastException e) {
                    Switch sv = (Switch) v;
                    try {
                        sv.setThumbDrawable((Drawable) value);
                    } catch (ClassCastException e2) {
                        sv.setThumbResource((int) value);
                    }
                }
            }
        }));
        put(res,"thumbTextPadding", new DimenAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((Switch) v).getThumbTextPadding();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((Switch) v).setThumbTextPadding(intCast(value));
            }
        }));
        put(res,"track", new DrawableAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((Switch) v).getTrackDrawable();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                Switch sv = (Switch)v;
                try {
                    sv.setTrackDrawable((Drawable) value);
                }
                catch (ClassCastException e) {
                    sv.setTrackResource((int) value);
                }
            }
        }));
        put(res,"checkMark", new DrawableAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((CheckedTextView) v).getCheckMarkDrawable();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                CheckedTextView ctv = ((CheckedTextView) v);
                try {
                    ctv.setCheckMarkDrawable((Drawable) value);
                }
                catch (ClassCastException e) {
                    ctv.setCheckMarkDrawable((int) value);
                }
            }
        }));
        put(res,"checkMarkTint", new TintAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((CheckedTextView) v).getCheckMarkTintList();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((CheckedTextView) v).setCheckMarkTintList((ColorStateList) value);
            }
        }));
        put(res,"checkMarkTintMode", new ModeAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((CheckedTextView) v).getCheckMarkTintList();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((CheckedTextView) v).setCheckMarkTintMode((PorterDuff.Mode) value);
            }
        }));
        put(res,"format", new StringAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((Chronometer) v).getFormat();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((Chronometer) v).setFormat((String) value);
            }
        }));
        put(res,"format12Hour", new StringAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public Object get(View v) {
                return ((TextClock) v).getFormat12Hour();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextClock) v).setFormat12Hour((String) value);
            }
        }));
        put(res,"format24Hour", new StringAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public Object get(View v) {
                return ((TextClock) v).getFormat24Hour();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextClock) v).setFormat24Hour((String) value);
            }
        }));
        put(res,"timeZone", new StringAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public Object get(View v) {
                return ((TextClock) v).getTimeZone();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TextClock) v).setTimeZone((String) value);
            }
        }));
        put(res,"indeterminate", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((ProgressBar) v).isIndeterminate();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ProgressBar) v).setIndeterminate((boolean) value);
            }
        }));
        put(res,"indeterminateDrawable", new DrawableAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((ProgressBar) v).getIndeterminateDrawable();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ProgressBar) v).setIndeterminateDrawable((Drawable) value);
            }
        }));
        put(res,"indeterminateTint", new TintAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((ProgressBar) v).getIndeterminateTintList();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ProgressBar) v).setIndeterminateTintList((ColorStateList) value);
            }
        }));
        put(res,"indeterminateTintMode", new ModeAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((ProgressBar) v).getIndeterminateTintMode();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ProgressBar) v).setIndeterminateTintMode((PorterDuff.Mode) value);
            }
        }));
        put(res,"interpolator", new ResAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((ProgressBar) v).getInterpolator();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ProgressBar pb = (ProgressBar)v;
                try {
                    pb.setInterpolator(q.ctx, (int) value);
                }
                catch (ClassCastException e) {
                    pb.setInterpolator((android.view.animation.Interpolator) value);
                }
            }
        }));
        put(res,"max", new IntAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((ProgressBar) v).getMax();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ProgressBar) v).setMax(intCast(value));
            }
        }));
        put(res,"progress", new IntAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((ProgressBar) v).getProgress();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ProgressBar) v).setProgress(intCast(value));
            }
        }));
        put(res,"progressBackgroundTint", new TintAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((ProgressBar) v).getProgressBackgroundTintList();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ProgressBar) v).setProgressBackgroundTintList((ColorStateList) value);
            }
        }));
        put(res,"progressBackgroundTintMode", new ModeAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((ProgressBar) v).getProgressBackgroundTintMode();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ProgressBar) v).setProgressBackgroundTintMode((PorterDuff.Mode) value);
            }
        }));
        put(res,"progressDrawable", new DrawableAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((ProgressBar) v).getProgressDrawable();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ProgressBar) v).setProgressDrawable((Drawable) value);
            }
        }));
        put(res,"progressTint", new TintAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((ProgressBar) v).getProgressTintList();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ProgressBar) v).setProgressTintList((ColorStateList) value);
            }
        }));
        put(res,"secondaryProgress", new IntAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((ProgressBar) v).getSecondaryProgress();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ProgressBar) v).setSecondaryProgress((int) value);
            }
        }));
        put(res,"secondaryProgressTint", new TintAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((ProgressBar) v).getSecondaryProgressTintList();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ProgressBar) v).setSecondaryProgressTintList((ColorStateList) value);
            }
        }));
        put(res,"secondaryProgressTintMode", new ModeAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((ProgressBar) v).getSecondaryProgressTintMode();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ProgressBar) v).setSecondaryProgressTintMode((PorterDuff.Mode) value);
            }
        }));
        put(res,"thumbTint", new TintAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((AbsSeekBar) v).getThumbTintList();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((AbsSeekBar) v).setThumbTintList((ColorStateList) value);
            }
        }));
        put(res,"thumbTintMode", new ModeAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object get(View v) {
                return ((AbsSeekBar) v).getThumbTintMode();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((AbsSeekBar) v).setThumbTintMode((PorterDuff.Mode) value);
            }
        }));
        put(res,"isIndicator", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((RatingBar) v).isIndicator();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((RatingBar) v).setIsIndicator((boolean) value);
            }
        }));
        put(res,"numStars", new IntAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((RatingBar) v).getNumStars();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((RatingBar) v).setNumStars((int) value);
            }
        }));
        put(res,"rating", new FloatAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((RatingBar) v).getRating();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((RatingBar) v).setRating(floatCast(value));
            }
        }));
        put(res,"stepSize", new FloatAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((RatingBar) v).getStepSize();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((RatingBar) v).setStepSize(floatCast(value));
            }
        }));
        put(res,"inflatedId", new IdAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((ViewStub) v).getInflatedId();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ViewStub) v).setInflatedId((int) value);
            }
        }));
        put(res,"layout", new ResAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((ViewStub) v).getLayoutResource();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ViewStub) v).setLayoutResource((int) value);
            }
        }));
        put(res,"cacheColorHint", new ColorAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((AbsListView) v).getCacheColorHint();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((AbsListView) v).setCacheColorHint((int) value);
            }
        }));
        put(res,"choiceMode", new CustomAttr(new AttrListener() {
            @Override
            public Object format(AQuery q, View v, String value) {
                return formatChoice(value);
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return ((AbsListView) v).getChoiceMode();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((AbsListView) v).setChoiceMode((int) value);
            }
        }));
        put(res,"drawSelectorOnTop", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return false;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((AbsListView) v).setDrawSelectorOnTop((boolean) value);
            }
        }));
        put(res,"fastScrollEnabled", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((AbsListView) v).isFastScrollEnabled();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((AbsListView) v).setFastScrollEnabled((boolean) value);
            }
        }));
        put(res,"listSelector", new IntAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((AbsListView) v).getSelector();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((AbsListView) v).setSelection((int) value);
            }
        }));
        put(res,"scrollingCache", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((AbsListView) v).isScrollingCacheEnabled();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((AbsListView) v).setScrollingCacheEnabled((boolean) value);
            }
        }));
        put(res,"smoothScrollbar", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((AbsListView) v).isSmoothScrollbarEnabled();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((AbsListView) v).setSmoothScrollbarEnabled((boolean) value);
            }
        }));
        put(res,"stackFromBottom", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((AbsListView) v).isStackFromBottom();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((AbsListView) v).setStackFromBottom((boolean) value);
            }
        }));
        put(res,"textFilterEnabled", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((AbsListView) v).isTextFilterEnabled();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((AbsListView) v).setTextFilterEnabled((boolean) value);
            }
        }));
        put(res,"transcriptMode", new CustomAttr(new AttrListener() {
            @Override
            public Object format(AQuery q, View v, String value) {
                return formatTranscript(value);
            }

            @Override
            public Object get(View v) {
                return ((AbsListView) v).getTranscriptMode();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((AbsListView) v).setTranscriptMode((int) value);
            }
        }));
        put(res,"columnWidth", new DimenAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((GridView) v).getColumnWidth();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GridView) v).setColumnWidth(intCast(value));
            }
        }));
        put(res,"horizontalSpacing", new DimenAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((GridView) v).getHorizontalSpacing();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GridView) v).setHorizontalSpacing(intCast(value));
            }
        }));
        put(res,"numColumns", new IntAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return ((GridView) v).getNumColumns();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GridView) v).setNumColumns((int) value);
            }
        }));
        put(res,"stretchMode", new CustomAttr(new AttrListener() {
            @Override
            public Object format(AQuery q, View v, String value) {
                return formatStretch(value);
            }

            @Override
            public Object get(View v) {
                return ((GridView) v).getStretchMode();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GridView) v).setStretchMode((int) value);
            }
        }));
        put(res,"verticalSpacing", new DimenAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((GridView) v).getVerticalSpacing();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GridView) v).setVerticalSpacing(intCast(value));
            }
        }));
        put(res,"divider", new DrawableAttr(new PropListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public Object get(View v) {
                try {
                    return ((ListView) v).getDivider();
                }
                catch (ClassCastException e) {
                    try {
                        LinearLayout ll = (LinearLayout) v;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            return ll.getDividerDrawable();
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                    return null;
                }
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void prop(AQuery q, View v, Object value) {
                Drawable dValue = (Drawable) value;
                try {
                    ((ListView) v).setDivider(dValue);
                }
                catch (ClassCastException e) {
                    try {
                        ((TabWidget) v).setDividerDrawable(dValue);
                    }
                    catch (ClassCastException e2) {
                        try {
                            LinearLayout ll = (LinearLayout) v;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                ll.setDividerDrawable(dValue);
                        }
                        catch (ClassCastException e3) {
                            throw e;
                        }
                    }
                }
            }
        }));
        put(res,"dividerHeight", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((ListView) v).getDividerHeight();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ListView) v).setDividerHeight(intCast(value));
            }
        }));
        put(res,"footerDividersEnabled", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public Object get(View v) {
                return ((ListView) v).areFooterDividersEnabled();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ListView) v).setFooterDividersEnabled((boolean) value);
            }
        }));
        put(res,"headerDividersEnabled", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public Object get(View v) {
                return ((ListView) v).areHeaderDividersEnabled();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ListView) v).setHeaderDividersEnabled((boolean) value);
            }
        }));
        put(res,"childDivider", new DrawableAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return null;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ExpandableListView) v).setChildDivider((Drawable) value);
            }
        }));
        put(res,"childIndicator", new DrawableAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return null;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ExpandableListView) v).setChildIndicator((Drawable) value);
            }
        }));
        put(res,"childIndicatorLeft", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return 0;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ExpandableListView) v).setChildIndicatorBounds(intCast(value), -1);
            }
        }));
        put(res,"childIndicatorRight", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return 0;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ExpandableListView) v).setChildIndicatorBounds(-1, intCast(value));
            }
        }));
        put(res,"childIndicatorStart", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return 0;
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ExpandableListView) v).setChildIndicatorBoundsRelative(intCast(value), -1);
            }
        }));
        put(res,"childIndicatorEnd", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return 0;
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ExpandableListView) v).setChildIndicatorBoundsRelative(-1, intCast(value));
            }
        }));
        put(res,"groupIndicator", new DrawableAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return null;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ExpandableListView) v).setGroupIndicator((Drawable) value);
            }
        }));
        put(res,"indicatorLeft", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return 0;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ExpandableListView) v).setIndicatorBounds(intCast(value), -1);
            }
        }));
        put(res,"indicatorRight", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return 0;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ExpandableListView) v).setIndicatorBounds(-1, intCast(value));
            }
        }));
        put(res,"indicatorStart", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return 0;
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ExpandableListView) v).setIndicatorBoundsRelative(intCast(value), -1);
            }
        }));
        put(res,"indicatorEnd", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return 0;
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((ExpandableListView) v).setIndicatorBoundsRelative(-1, intCast(value));
            }
        }));
        put(res,"animationDuration", new IntAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return v.getAnimation().getDuration();
            }

            @SuppressWarnings("deprecation")
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((Gallery) v).setAnimationDuration(intCast(value));
            }
        }));
        put(res,"spacing", new DimenAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return 0;
            }

            @SuppressWarnings("deprecation")
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((Gallery) v).setSpacing(intCast(value));
            }
        }));
        put(res,"unselectedAlpha", new FloatAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return 0f;
            }

            @SuppressWarnings("deprecation")
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((Gallery) v).setUnselectedAlpha(floatCast(value));
            }
        }));
        put(res,"animateFirstView", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public Object get(View v) {
                return ((ViewAnimator) v).getAnimateFirstView();
            }

            @SuppressWarnings("ConstantConditions")
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                boolean bValue = (boolean) value;
                try {
                    ((AdapterViewAnimator) v).setAnimateFirstView(bValue);
                }
                catch (ClassCastException e) {
                    try {
                        ((ViewAnimator) v).setAnimateFirstView(bValue);
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                }
            }
        }));
        put(res,"inAnimation", new CustomAttr(new AttrListener() {
            @Override
            public Object format(AQuery q, View v, String value) {
                return q.getIdentifier("anim", value);
            }

            @SuppressWarnings("ConstantConditions")
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                try {
                    return ((AdapterViewAnimator) v).getInAnimation();
                }
                catch (ClassCastException e) {
                    try {
                        return ((ViewAnimator) v).getInAnimation();
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                }
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                try {
                    AdapterViewAnimator ava = (AdapterViewAnimator) v;
                    try {
                        ava.setInAnimation(q.ctx, (int) value);
                    } catch (ClassCastException e) {
                        try {
                            ava.setInAnimation((ObjectAnimator) value);
                        }
                        catch (ClassCastException e2) {
                            throw e;
                        }
                    }
                }
                catch (ClassCastException e) {
                    ViewAnimator va = (ViewAnimator) v;
                    try {
                        va.setInAnimation(q.ctx, (int) value);
                    }
                    catch (ClassCastException e2) {
                        try {
                            va.setInAnimation((Animation) value);
                        }
                        catch (ClassCastException e3) {
                            throw e;
                        }
                    }
                }
            }
        }));
        put(res,"outAnimation", new CustomAttr(new AttrListener() {
            @Override
            public Object format(AQuery q, View v, String value) {
                return q.getIdentifier("anim", value);
            }

            @SuppressWarnings("ConstantConditions")
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                try {
                    return ((AdapterViewAnimator) v).getOutAnimation();
                }
                catch (ClassCastException e) {
                    try {
                        return ((ViewAnimator) v).getOutAnimation();
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                }
            }

            @SuppressWarnings("ConstantConditions")
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                try {
                    AdapterViewAnimator ava = (AdapterViewAnimator) v;
                    try {
                        ava.setOutAnimation(q.ctx, (int) value);
                    } catch (ClassCastException e) {
                        ava.setOutAnimation((ObjectAnimator) value);
                    }
                }
                catch (ClassCastException e) {
                    ViewAnimator va = (ViewAnimator) v;
                    try {
                        va.setOutAnimation(q.ctx, (int) value);
                    }
                    catch (ClassCastException e2) {
                        va.setOutAnimation((Animation) value);
                    }
                }
            }
        }));
        put(res,"autoStart", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return ((AdapterViewFlipper) v).isAutoStart();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((AdapterViewFlipper) v).setAutoStart((boolean) value);
            }
        }));
        put(res,"flipInterval", new IntAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((AdapterViewFlipper) v).getFlipInterval();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((AdapterViewFlipper) v).setFlipInterval(intCast(value));
            }
        }));
        put(res,"foreground", new DrawableAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public Object get(View v) {
                return v.getForeground();
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setForeground((Drawable) value);
            }
        }));
        put(res,"foregroundGravity", new CustomAttr(new AttrListener() {
            @Override
            public Object format(AQuery q, View v, String value) {
                return formatGravity(value);
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public Object get(View v) {
                return v.getForegroundGravity();
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setForegroundGravity((int) value);
            }
        }));
        put(res,"foregroundTint", new TintAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public Object get(View v) {
                return v.getForegroundTintList();
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setForegroundTintList((ColorStateList) value);
            }
        }));
        put(res,"foregroundTintMode", new ModeAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public Object get(View v) {
                return v.getForegroundTintMode();
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void prop(AQuery q, View v, Object value) {
                v.setForegroundTintMode((PorterDuff.Mode) value);
            }
        }));
        put(res,"measureAllChildren", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public Object get(View v) {
                return ((FrameLayout) v).getMeasureAllChildren();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((FrameLayout) v).setMeasureAllChildren((boolean) value);
            }
        }));
        put(res,"dateTextAppearance", new ResAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((CalendarView) v).getDateTextAppearance();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((CalendarView) v).setDateTextAppearance((int) value);
            }
        }));
        put(res,"firstDayOfWeek", new IntAttr(new PropListener() {
            @SuppressWarnings("ConstantConditions")
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                try {
                    return ((CalendarView) v).getFirstDayOfWeek();
                }
                catch (ClassCastException e) {
                    try {
                        DatePicker dp = (DatePicker) v;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            return dp.getFirstDayOfWeek();
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                    return 0;
                }
            }

            @SuppressWarnings("ConstantConditions")
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                int iValue = (int) value;
                try {
                    ((CalendarView) v).setFirstDayOfWeek(iValue);
                }
                catch (ClassCastException e) {
                    try {
                        DatePicker dp = (DatePicker) v;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            dp.setFirstDayOfWeek(iValue);
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                }
            }
        }));
        put(res,"focusedMonthDateColor", new ColorAttr(new PropListener() {
            @SuppressWarnings("deprecation")
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((CalendarView) v).getFocusedMonthDateColor();
            }

            @SuppressWarnings("deprecation")
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((CalendarView) v).setFocusedMonthDateColor((int) value);
            }
        }));
        put(res,"maxDate", new LongAttr(new PropListener() {
            @SuppressWarnings("ConstantConditions")
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                try {
                    return ((CalendarView) v).getMaxDate();
                }
                catch (ClassCastException e) {
                    try {
                        return ((DatePicker) v).getMaxDate();
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                }
            }

            @SuppressWarnings("ConstantConditions")
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                long lValue = longCast(value);
                try {
                    ((CalendarView) v).setMaxDate(lValue);
                }
                catch (ClassCastException e) {
                    try {
                        ((DatePicker) v).setMaxDate(lValue);
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                }
            }
        }));
        put(res,"minDate", new LongAttr(new PropListener() {
            @SuppressWarnings("ConstantConditions")
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                try {
                    return ((CalendarView) v).getMinDate();
                }
                catch (ClassCastException e) {
                    try {
                        return ((DatePicker) v).getMinDate();
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                }
            }

            @SuppressWarnings("ConstantConditions")
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                long lValue = longCast(value);
                try {
                    ((CalendarView) v).setMinDate(lValue);
                }
                catch (ClassCastException e) {
                    try {
                        ((DatePicker) v).setMinDate(lValue);
                    }
                    catch (ClassCastException e2) {
                        throw e;
                    }
                }
            }
        }));
        put(res,"selectedDateVerticalBar", new DrawableAttr(new PropListener() {
            @SuppressWarnings("deprecation")
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((CalendarView) v).getSelectedDateVerticalBar();
            }

            @SuppressWarnings("deprecation")
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                try {
                    ((CalendarView) v).setSelectedDateVerticalBar((Drawable) value);
                }
                catch (ClassCastException e) {
                    ((CalendarView) v).setSelectedDateVerticalBar((int) value);
                }
            }
        }));
        put(res,"selectedWeekBackgroundColor", new ColorAttr(new PropListener() {
            @SuppressWarnings("deprecation")
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((CalendarView) v).getSelectedWeekBackgroundColor();
            }

            @SuppressWarnings("deprecation")
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((CalendarView) v).setSelectedWeekBackgroundColor((int) value);
            }
        }));
        put(res,"showWeekNumber", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return ((CalendarView) v).getShowWeekNumber();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((CalendarView) v).setShowWeekNumber((boolean) value);
            }
        }));
        put(res,"shownWeekCount", new IntAttr(new PropListener() {
            @SuppressWarnings("deprecation")
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((CalendarView) v).getShownWeekCount();
            }

            @SuppressWarnings("deprecation")
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((CalendarView) v).setShownWeekCount((int) value);
            }
        }));
        put(res,"unfocusedMonthDateColor", new ColorAttr(new PropListener() {
            @SuppressWarnings("deprecation")
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((CalendarView) v).getUnfocusedMonthDateColor();
            }

            @SuppressWarnings("deprecation")
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((CalendarView) v).setUnfocusedMonthDateColor((int) value);
            }
        }));
        put(res,"weekDayTextAppearance", new ResAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((CalendarView) v).getWeekDayTextAppearance();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((CalendarView) v).setWeekDayTextAppearance((int) value);
            }
        }));
        put(res,"weekNumberColor", new ColorAttr(new PropListener() {
            @SuppressWarnings("deprecation")
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((CalendarView) v).getWeekNumberColor();
            }

            @SuppressWarnings("deprecation")
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((CalendarView) v).setWeekNumberColor((int) value);
            }
        }));
        put(res,"weekSeparatorLineColor", new ColorAttr(new PropListener() {
            @SuppressWarnings("deprecation")
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((CalendarView) v).getWeekSeparatorLineColor();
            }

            @SuppressWarnings("deprecation")
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((CalendarView) v).setWeekSeparatorLineColor((int) value);
            }
        }));
        put(res,"calendarViewShown", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return ((DatePicker) v).getCalendarViewShown();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((DatePicker) v).setCalendarViewShown((boolean) value);
            }
        }));
        put(res,"spinnersShown", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return ((DatePicker) v).getSpinnersShown();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((DatePicker) v).setSpinnersShown((boolean) value);
            }
        }));
        put(res,"eventsInterceptionEnabled", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((GestureOverlayView) v).isEventsInterceptionEnabled();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GestureOverlayView) v).setEventsInterceptionEnabled((boolean) value);
            }
        }));
        put(res,"fadeEnabled", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((GestureOverlayView) v).isFadeEnabled();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GestureOverlayView) v).setFadeEnabled((boolean) value);
            }
        }));
        put(res,"fadeOffset", new LongAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((GestureOverlayView) v).getFadeOffset();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GestureOverlayView) v).setFadeOffset(longCast(value));
            }
        }));
        put(res,"gestureColor", new ColorAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((GestureOverlayView) v).getGestureColor();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GestureOverlayView) v).setGestureColor((int) value);
            }
        }));
        put(res,"gestureStrokeAngleThreshold", new FloatAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((GestureOverlayView) v).getGestureStrokeAngleThreshold();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GestureOverlayView) v).setGestureStrokeAngleThreshold(floatCast(value));
            }
        }));
        put(res,"gestureStrokeLengthThreshold", new FloatAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((GestureOverlayView) v).getGestureStrokeLengthThreshold();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GestureOverlayView) v).setGestureStrokeAngleThreshold(floatCast(value));
            }
        }));
        put(res,"gestureStrokeSquarenessThreshold", new FloatAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((GestureOverlayView) v).getGestureStrokeSquarenessTreshold();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GestureOverlayView) v).setGestureStrokeSquarenessTreshold(floatCast(value));
            }
        }));
        put(res,"gestureStrokeType", new CustomAttr(new AttrListener() {
            @Override
            public Object format(AQuery q, View v, String value) {
                return formatStroke(value);
            }

            @Override
            public Object get(View v) {
                return ((GestureOverlayView) v).getGestureStrokeType();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GestureOverlayView) v).setGestureStrokeType((int) value);
            }
        }));
        put(res,"gestureStrokeWidth", new FloatAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((GestureOverlayView) v).getGestureStrokeWidth();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GestureOverlayView) v).setGestureStrokeWidth(floatCast(value));
            }
        }));
        put(res,"uncertainGestureColor", new ColorAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((GestureOverlayView) v).getGestureColor();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GestureOverlayView) v).setGestureColor((int) value);
            }
        }));
        put(res,"fillViewport", new BoolAttr(new PropListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public Object get(View v) {
                try {
                    return ((HorizontalScrollView) v).isFillViewport();
                }
                catch (ClassCastException e) {
                    try {
                        return ((NestedScrollView) v).isFillViewport();
                    }
                    catch (ClassCastException e2) {
                        try {
                            return ((ScrollView) v).isFillViewport();
                        }
                        catch (ClassCastException e3) {
                            throw e;
                        }
                    }
                }
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void prop(AQuery q, View v, Object value) {
                boolean bValue = (boolean) value;
                try {
                    ((HorizontalScrollView) v).setFillViewport(bValue);
                }
                catch (ClassCastException e) {
                    try {
                        ((NestedScrollView) v).setFillViewport(bValue);
                    }
                    catch (ClassCastException e2) {
                        try {
                            ((ScrollView) v).setFillViewport(bValue);
                        }
                        catch (ClassCastException e3) {
                            throw e;
                        }
                    }
                }
            }
        }));
        put(res,"alignmentMode", new CustomAttr(new AttrListener() {
            @Override
            public Object format(AQuery q, View v, String value) {
                return formatAlignment(value);
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public Object get(View v) {
                return ((GridLayout) v).getAlignmentMode();
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GridLayout) v).setAlignmentMode((int) value);
            }
        }));
        put(res,"columnCount", new IntAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public Object get(View v) {
                return ((GridLayout) v).getColumnCount();
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GridLayout) v).setColumnCount((int) value);
            }
        }));
        put(res,"columnOrderPreserved", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public Object get(View v) {
                return ((GridLayout) v).isColumnOrderPreserved();
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GridLayout) v).setColumnOrderPreserved((boolean) value);
            }
        }));
        put(res,"rowOrderPreserved", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public Object get(View v) {
                return ((GridLayout) v).isRowOrderPreserved();
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GridLayout) v).setRowOrderPreserved((boolean) value);
            }
        }));
        put(res,"useDefaultMargins", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public Object get(View v) {
                return ((GridLayout) v).getUseDefaultMargins();
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((GridLayout) v).setUseDefaultMargins((boolean) value);
            }
        }));
        put(res,"baselineAligned", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((LinearLayout) v).isBaselineAligned();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((LinearLayout) v).setBaselineAligned((boolean) value);
            }
        }));
        put(res,"baselineAlignedChildIndex", new IntAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((LinearLayout) v).getBaselineAlignedChildIndex();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((LinearLayout) v).setBaselineAlignedChildIndex((int) value);
            }
        }));
        put(res,"iconifiedByDefault", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public Object get(View v) {
                return ((SearchView) v).isIconfiedByDefault();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((SearchView) v).setIconifiedByDefault((boolean) value);
            }
        }));
        put(res,"queryHint", new StringAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Object get(View v) {
                return ((SearchView) v).getQueryHint();
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((SearchView) v).setQueryHint((String) value);
            }
        }));
        put(res,"tabStripEnabled", new BoolAttr(new PropListener() {
            @TargetApi(Build.VERSION_CODES.FROYO)
            @Override
            public Object get(View v) {
                return ((TabWidget) v).isStripEnabled();
            }

            @TargetApi(Build.VERSION_CODES.FROYO)
            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TabWidget) v).setStripEnabled((boolean) value);
            }
        }));
        put(res,"tabStripLeft", new DrawableAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return null;
            }

            @TargetApi(Build.VERSION_CODES.FROYO)
            @Override
            public void prop(AQuery q, View v, Object value) {
                TabWidget tw = (TabWidget)v;
                try {
                    tw.setLeftStripDrawable((Drawable) value);
                }
                catch (ClassCastException e) {
                    tw.setLeftStripDrawable((int) value);
                }
            }
        }));
        put(res,"tabStripRight", new DrawableAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return null;
            }

            @TargetApi(Build.VERSION_CODES.FROYO)
            @Override
            public void prop(AQuery q, View v, Object value) {
                TabWidget tw = (TabWidget)v;
                try {
                    tw.setRightStripDrawable((Drawable) value);
                }
                catch (ClassCastException e) {
                    tw.setRightStripDrawable((int) value);
                }
            }
        }));
        put(res,"collapseColumns", new CustomAttr(new AttrListener() {
            @Override
            public Object get(View v) {
                TableLayout tl = (TableLayout) v;
                ArrayList<Integer> columns = new ArrayList<>(tl.getChildCount());
                for (int i=0;i<tl.getChildCount();i++) {
                    if (tl.isColumnCollapsed(i))
                        columns.add(i);
                }
                int[] res = new int[columns.size()];
                for (int i=0;i<columns.size();i++)
                    res[i] = columns.get(i);
                return res;
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                int[] iValue = (int[]) value;
                TableLayout tl = (TableLayout) v;
                boolean[] columsCollapsed = new boolean[tl.getChildCount()];
                for (int col : iValue)
                    columsCollapsed[col] = true;
                for (int i=0;i<tl.getChildCount();i++)
                    tl.setColumnCollapsed(i, columsCollapsed[i]);
            }

            @Override
            public Object format(AQuery q, View v, String value) {
                return q.formatIntArray(value);
            }
        }));
        put(res,"shrinkColumns", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TableLayout) v).isShrinkAllColumns();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TableLayout) v).setShrinkAllColumns((boolean) value);
            }
        }));
        put(res,"stretchColumns", new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                return ((TableLayout) v).isStretchAllColumns();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                ((TableLayout) v).setStretchAllColumns((boolean) value);
            }
        }));
        put(res,"content", new CustomAttr(new AttrListener() {
            @Override
            public Object format(AQuery q, View v, String value) {
                return q.formatString(value);
            }

            @SuppressWarnings("deprecation")
            @Override
            public Object get(View v) {
                return ((SlidingDrawer) v).getContent();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
            }
        }));
        put(res,"handle", new CustomAttr(new AttrListener() {
            @Override
            public Object format(AQuery q, View v, String value) {
                return value;
            }

            @SuppressWarnings("deprecation")
            @Override
            public Object get(View v) {
                return ((SlidingDrawer) v).getHandle();
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
            }
        }));
        return res;
    }

    /**
     * Puts an object in a Map array
     */
    private static <K,V> void put(Map<K,V> array, K key, V value) {
        /* Uncomment this code to check duplicates
        if (array.containsKey(key))
            throw new IllegalArgumentException("Array already contains this key");
        //*/
        array.put(key,value);
    }
    /**
     * Associates several keys to a same object in a Map array
     */
    private static <K,V> void putAll(Map<K,V> array, K[] keys, V value) {
        for (K key : keys)
            put(array, key,value);
    }

    /**
     * Gets the value of an attribute for the first element in the set of matched elements
     * For example, elt.prop("id") returns the id of the element
     *
     * @param key
     * The attribute name
     * @return
     */
    public Object prop(String key) {
        try {
            return propIfMethod(key);
        }
        catch (NoSuchMethodError e) {
            return null;
        }
    }

    private Object propIfMethod(String key) throws NoSuchMethodError {
        return getPropIfExists(key).get(head());
    }

    /**
     * Returns the value of an attribute and casts it as a boolean
     * @param key
     * The attribute name
     */
    public boolean propb(String key) {
        try {
            return (boolean) propIfMethod(key);
        }
        catch (NoSuchMethodError e) {
            return false;
        }
    }
    /**
     * Returns the value of an attribute and casts it as an integer
     * @param key
     * The attribute name
     */
    public int propi(String key) {
        try {
            return intCast(propIfMethod(key));
        }
        catch (NoSuchMethodError e) {
            return 0;
        }
    }
    /**
     * Returns the value of an attribute and casts it as a float
     * @param key
     * The attribute name
     */
    public float propf(String key) {
        try {
            return floatCast(propIfMethod(key));
        }
        catch (NoSuchMethodError e) {
            return 0;
        }
    }
    /**
     * Returns the value of an attribute and casts it as a double
     * @param key
     * The attribute name
     */
    public double propd(String key) {
        try {
            return doubleCast(propIfMethod(key));
        }
        catch (NoSuchMethodError e) {
            return 0;
        }
    }
    /**
     * Returns the value of an attribute and casts it as a String
     * @param key
     * The attribute name
     */
    public String props(String key) {
        return (String) prop(key);
    }
    /**
     * Returns the value of an attribute and casts it as a String array
     * @param key
     * The attribute name
     */
    public String[] propsa(String key) {
        return (String[]) prop(key);
    }

    /**
     * Returns the value of an attribute and parses it as a String
     * @param key
     * The attribute name
     */
    public String attr(String key) {
        Object res = prop(key);
        if (res == null)
            return null;
        return String.valueOf(res);
    }

    /**
     * Throws an exception indicating that the property does not exist
     */
    private static void unkownProp(String key) {
        throw new IllegalArgumentException("Property \""+ key +"\" does not exist");
    }
    /**
     * Sets an attribute for every elements
     * For example, elt.prop("id", R.id.myid) gives the id R.id.myid to every elements
     *
     * @param key
     * The attribute name
     * @param value
     * The new value of the attribute
     */
    public AQuery prop(String key, Object value) {
        AttrSetter callback = getPropIfExists(key);
        for (View v : list()) {
            try {
                callback.prop(this, v, value);
            }
            catch (NoSuchMethodError e) {
            }
        }
        return this;
    }

    /**
     * Sets an attribute for every elements, by taking a String into argument
     * The interest of this method is that it can handle all the formats that Android XML handles
     *
     * For example, elt.prop("id", "@id/myid") gives the id R.id.myid to every elements
     *
     * @param key
     * The attribute name
     * @param value
     * An XML-like String containing the new value of the attribute
     */
    public AQuery attr(String key, String value) {
        AttrSetter callback = getAttrIfExists(key);
        for (View v : list()) {
            try {
                callback.set(this, v, value);
            }
            catch (NoSuchMethodError e) {
            }
        }
        return this;
    }

    /**
     * Throws an exception indicating that the attribute does not exist
     */
    private static void unkownAttr(String key) {
        throw new IllegalArgumentException("Attribute \""+ key +"\" does not exist");
    }

    /**
     * returns the AttrSetter associated to the attribute
     * returns null if the attribute doesn't exist
     */
    protected static AttrSetter getAttr(String attribute) {
        return ATTRS.get(attribute);
    }
    /**
     * returns the AttrSetter associated to the attribute, if it exists
     * Otherwise, throws an exception indicating that the attribute does not exist
     */
    protected static AttrSetter getAttrIfExists(String attribute) {
        AttrSetter res = getAttr(attribute);
        if (res == null)
            unkownAttr(attribute);
        return res;
    }

    /**
     * returns the AttrSetter associated to the attribute, if it exists
     * Otherwise, throws an exception indicating that the property does not exist
     */
    protected static AttrSetter getPropIfExists(String key) {
        AttrSetter res = getAttr(key);
        if (res == null)
            unkownProp(key);
        return res;
    }

    /**
     * Sets a RelativeLayout.LayoutParams rule (like alignWithParentBottom)
     */
    private void setRule(View v, int alignParent, Object value) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
        if ((Boolean) value)
            lp.addRule(alignParent, RelativeLayout.TRUE);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            lp.removeRule(alignParent);
        else
            lp.addRule(alignParent, 0);
        v.setLayoutParams(lp);
    }
    /**
     * Adds an RelativeLayout.LayoutParams-rule AttrSetter to the map, which checks if the rule is satisfied
     */
    private static void addLPRuleChecker(HashMap<String,AttrSetter> array, String ruleName, final int ruleID) {
        array.put(ruleName, new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
                return (lp.getRules()[ruleID] == RelativeLayout.TRUE);
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                q.setRule(v, ruleID, value);
            }
        }));
    }
    /**
     * Adds an RelativeLayout.LayoutParams-rule AttrSetter to the map, which gets the value of a rule
     */
    private static void addLPRuleGetter(HashMap<String,AttrSetter> array, String ruleName, final int ruleID) {
        array.put(ruleName, new IdAttr(new PropListener() {
            @Override
            public Object get(View v) {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
                return lp.getRules()[ruleID];
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
                lp.addRule(ruleID, (int) value);
            }
        }));
    }

    /**
     * Checks if an integer-value has the flag
     */
    private static boolean hasFlag(int value, int flag) {
        return (value == (value|flag));
    }

    /**
     * Returns the BoolAttr that checks if a TextView inputtype has the specified flag
     */
    private static BoolAttr inputTypeAttr(final int flag) {
        return new BoolAttr(new PropListener() {
            @Override
            public Object get(View v) {
                int it = ((TextView) v).getInputType();
                return hasFlag(it,flag);
            }

            @Override
            public void prop(AQuery q, View v, Object value) {
                TextView tv = (TextView) v;
                if ((boolean) value)
                    tv.setInputType(flag);
                else
                    tv.setInputType(tv.getInputType() & ~flag);
            }
        });
    }

    /**
     * Returns the position of the given view in the set of elements
     * Returns -1 if the element is not in the set
     */
    public int index(View element) {
        return list().indexOf(element);
    }
    /**
     * Returns the position of the given element in the set of elements
     * Returns -1 if the element is not in the set
     */
    public int index(AQuery element) {
        return index(element.head());
    }

    /**
     * Reduce the set of matched elements to a subset specified by a range of indices.
     * @param start
     * The beginning index. May be negative to indicate an offset from the end of the set
     * @param end
     * The ending index. May be negative to indicate an offset from the end of the set
     */
    public AQuery slice(int start, int end) {
        ArrayList<View> res = new ArrayList<>(end-start-1);
        List<View> views = list();
        if (start < 0)
            start += views.size();
        if (end < 0)
            end += views.size();
        for (int i=start;i<end;i++)
            res.add(views.get(i));
        return new $Array(ctx, res);
    }

    /**
     * Returns the width of the first element in the set of elements
     * Be careful : this might returns 0 if the Activity is not fully loaded when you call this function
     *
     * In that case, use layoutWidth() function instead, which could be enough in most cases,
     * or call measure() function to run your code at the appropriate moment
     *
     * @return
     * The width of the view, in px
     */
    public int width() {
        return head().getWidth();
    }

    /**
     * Sets the width of the elements.
     * This functions does exactly the same thing as layoutWidth function
     * @param w
     * The desired width, in px
     */
    public AQuery width(int w) {
        return layoutWidth(w);
    }

    /**
     * Returns the height of the first element in the set of elements
     * Be careful : this might returns 0 if the Activity is not fully loaded when you call this function
     *
     * In that case, use layoutHeight() function instead, which could be enough in most cases,
     * or call $(Runnable callback) to run your code at the appropriate moment
     *
     * @return
     * The width of the view, in px
     */
    public int height() {
        return head().getHeight();
    }
    /**
     * Sets the height of the elements.
     * This functions does exactly the same thing as layoutHeight function
     * @param h
     * The desired height, in px
     */
    public AQuery height(int h) {
        return layoutHeight(h);
    }

    /**
     * Returns the padding left of the first element in the set of elements, in px
     */
    public int paddingLeft() {
        return head().getPaddingLeft();
    }
    /**
     * Sets the padding left of the elements, in px
     */
    public AQuery paddingLeft(int padding) {
        for (View v : list())
            v.setPadding(padding, v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
        return this;
    }
    /**
     * Returns the padding start of the first element in the set of elements, in px
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public int paddingStart() {
        return head().getPaddingStart();
    }
    /**
     * Sets the padding start of the elements, in px
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public AQuery paddingStart(int padding) {
        for (View v : list())
            v.setPaddingRelative(padding, v.getPaddingTop(), v.getPaddingEnd(), v.getPaddingBottom());
        return this;
    }
    /**
     * Returns the padding top of the first element in the set of elements, in px
     */
    public int paddingTop() {
        return head().getPaddingTop();
    }
    /**
     * Sets the padding top of the elements, in px
     */
    public AQuery paddingTop(int padding) {
        for (View v : list())
            v.setPadding(v.getPaddingLeft(), padding, v.getPaddingRight(), v.getPaddingBottom());
        return this;
    }
    /**
     * Returns the padding right of the first element in the set of elements, in px
     */
    public int paddingRight() {
        return head().getPaddingRight();
    }
    /**
     * Sets the padding right of the elements, in px
     */
    public AQuery paddingRight(int padding) {
        for (View v : list())
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), padding, v.getPaddingBottom());
        return this;
    }
    /**
     * Returns the padding end of the first element in the set of elements, in px
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public int paddingEnd() {
        return head().getPaddingEnd();
    }
    /**
     * Sets the padding end of the elements, in px
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public AQuery paddingEnd(int padding) {
        for (View v : list())
            v.setPaddingRelative(v.getPaddingStart(), v.getPaddingTop(), padding, v.getPaddingBottom());
        return this;
    }
    /**
     * Returns the padding bottom of the first element in the set of elements, in px
     */
    public int paddingBottom() {
        return head().getPaddingBottom();
    }
    /**
     * Sets the padding bottom of the elements, in px
     */
    public AQuery paddingBottom(int padding) {
        for (View v : list())
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), padding);
        return this;
    }
    /**
     * Returns an int[] containing all the paddings of the first element in the set of elements
     * The paddings are given in the CSS-based-convention order : top,right,bottom,left
     */
    public int[] paddings() {
        return new int[]{paddingTop(),paddingRight(),paddingBottom(),paddingLeft()};
    }
    /**
     * Sets all the paddings of the elements, in px
     */
    public AQuery paddings(int padding) {
        paddings(padding, padding, padding, padding);
        return this;
    }

    /**
     * Sets all the paddings of the elements, in px
     * @param verticalPaddings
     * The top and bottom paddings
     * @param horizontalPaddings
     * The left and right paddings
     */
    public AQuery paddings(int verticalPaddings, int horizontalPaddings) {
        paddings(verticalPaddings, horizontalPaddings, verticalPaddings, horizontalPaddings);
        return this;
    }
    /**
     * Sets all the paddings of the elements, in px
     */
    public AQuery paddings(int top, int right, int bottom, int left) {
        for (View v : list())
            v.setPadding(left, top, right, bottom);
        return this;
    }

    /**
     * Returns the margin left of the first element in the set of elements, in px
     * If the elements doesn't have margin layout attributes, returns 0
     */
    public int marginLeft() {
        try {
            return mlp(head()).leftMargin;
        } catch (ClassCastException e) {
            return 0;
        }
    }
    /**
     * Sets the margin left of the elements, in px
     */
    public AQuery marginLeft(int margin) {
        for (View v : list()) {
            ViewGroup.MarginLayoutParams lp;
            try {
                lp = mlp(v);
            } catch (ClassCastException e) {
                continue;
            }
            lp.leftMargin = margin;
            v.setLayoutParams(lp);
        }
        return this;
    }
    /**
     * Returns the margin start of the first element in the set of elements, in px
     * If the elements doesn't have margin layout attributes, returns 0
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public int marginStart() {
        try {
            return mlp(head()).getMarginStart();
        } catch (ClassCastException e) {
            return 0;
        }
    }
    /**
     * Sets the margin start of the elements, in px
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public AQuery marginStart(int margin) {
        for (View v : list()) {
            ViewGroup.MarginLayoutParams lp;
            try {
                lp = mlp(v);
            } catch (ClassCastException e) {
                continue;
            }
            lp.setMarginStart(margin);
            v.setLayoutParams(lp);
        }
        return this;
    }
    /**
     * Returns the margin top of the first element in the set of elements, in px
     * If the elements doesn't have margin layout attributes, returns 0
     */
    public int marginTop() {
        try {
            return mlp(head()).topMargin;
        } catch (ClassCastException e) {
            return 0;
        }
    }
    /**
     * Sets the margin top of the elements, in px
     */
    public AQuery marginTop(int margin) {
        for (View v : list()) {
            ViewGroup.MarginLayoutParams lp;
            try {
                lp = mlp(v);
            } catch (ClassCastException e) {
                continue;
            }
            lp.topMargin = margin;
            v.setLayoutParams(lp);
        }
        return this;
    }
    /**
     * Returns the margin right of the first element in the set of elements, in px
     * If the elements doesn't have margin layout attributes, returns 0
     */
    public int marginRight() {
        try {
            return mlp(head()).rightMargin;
        } catch (ClassCastException e) {
            return 0;
        }
    }
    /**
     * Sets the margin right of the elements, in px
     */
    public AQuery marginRight(int margin) {
        for (View v : list()) {
            ViewGroup.MarginLayoutParams lp;
            try {
                lp = mlp(v);
            } catch (ClassCastException e) {
                continue;
            }
            lp.rightMargin = margin;
            v.setLayoutParams(lp);
        }
        return this;
    }
    /**
     * Returns the margin end of the first element in the set of elements, in px
     * If the elements doesn't have margin layout attributes, returns 0
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public int marginEnd() {
        try {
            return mlp(head()).getMarginEnd();
        } catch (ClassCastException e) {
            return 0;
        }
    }
    /**
     * Sets the margin end of the elements, in px
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public AQuery marginEnd(int margin) {
        for (View v : list()) {
            ViewGroup.MarginLayoutParams lp;
            try {
                lp = mlp(v);
            } catch (ClassCastException e) {
                continue;
            }
            lp.setMarginEnd(margin);
            v.setLayoutParams(lp);
        }
        return this;
    }
    /**
     * Returns the margin bottom of the first element in the set of elements, in px
     * If the elements doesn't have margin layout attributes, returns 0
     */
    public int marginBottom() {
        try {
            return mlp(head()).bottomMargin;
        } catch (ClassCastException e) {
            return 0;
        }
    }
    /**
     * Sets the margin bottom of the elements, in px
     */
    public AQuery marginBottom(int margin) {
        for (View v : list()) {
            ViewGroup.MarginLayoutParams lp;
            try {
                lp = mlp(v);
            } catch (ClassCastException e) {
                continue;
            }
            lp.bottomMargin = margin;
            v.setLayoutParams(lp);
        }
        return this;
    }
    /**
     * Returns an int[] containing all the margins of the first element in the set of elements
     * The paddings are given in the CSS-based-convention order : top,right,bottom,left
     */
    public int[] margins() {
        return new int[]{marginTop(),marginRight(),marginBottom(),marginLeft()};
    }
    /**
     * Sets all the margins of the elements, in px
     */
    public AQuery margins(int margin) {
        margins(margin, margin, margin, margin);
        return this;
    }

    /**
     * Sets the margins of the elements, in px
     * @param verticalMargins
     * The top and bottom margins
     * @param horizontalMargins
     * The left and right margins
     * @return
     */
    public AQuery margins(int verticalMargins, int horizontalMargins) {
        margins(verticalMargins, horizontalMargins, verticalMargins, horizontalMargins);
        return this;
    }
    /**
     * Sets all the margins of the elements, in px
     */
    public AQuery margins(int top, int right, int bottom, int left) {
        for (View v : list()) {
            ViewGroup.MarginLayoutParams lp;
            try {
                lp = mlp(v);
            } catch (ClassCastException e) {
                continue;
            }
            lp.setMargins(left,top,right,bottom);
            v.setLayoutParams(lp);
        }
        return this;
    }

    /**
     * Returns the LayoutParams of the View, casted as a ViewGroup.MarginLayoutParams
     * If the LayoutParams object is null, creates one one the fly
     */
    private static ViewGroup.MarginLayoutParams mlp(View v) {
        ViewGroup.MarginLayoutParams res = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        if (res == null)
            return new ViewGroup.MarginLayoutParams(0,0);
        return res;
    }

    /**
     * Returns the value of the layout_width attribute of the first element in the set of elements
     */
    public int layoutWidth() {
        return head().getLayoutParams().width;
    }
    /**
     * Returns the value of the layout_height attribute of the first element in the set of elements
     */
    public int layoutHeight() {
        return head().getLayoutParams().height;
    }
    /**
     * Sets the value of the layout_width attribute of the elements
     * @param w
     * The desired width in pixels, or MATCH_PARENT or WRAP_CONTENT
     */
    public AQuery layoutWidth(int w) {
        for (View v : list()) {
            ViewGroup.LayoutParams lp = v.getLayoutParams();
            if (lp == null)
                lp = new ViewGroup.LayoutParams(w,ViewGroup.LayoutParams.WRAP_CONTENT);
            else
                lp.width = w;
            v.setLayoutParams(lp);
        }
        return this;
    }
    /**
     * Sets the value of the layout_height attribute of the elements
     * @param h
     * The desired height in pixels, or MATCH_PARENT or WRAP_CONTENT
     */
    public AQuery layoutHeight(int h) {
        for (View v : list()) {
            ViewGroup.LayoutParams lp = v.getLayoutParams();
            if (lp == null)
                lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,h);
            else
                lp.height = h;
            v.setLayoutParams(lp);
        }
        return this;
    }

    /**
     * Get the current computed width for the first element in the set of matched elements, including paddings
     */
    public int outerWidth() {
        return width()+paddingLeft()+paddingRight();
    }
    /**
     * Get the current computed height for the first element in the set of matched elements, including paddings
     * @param includeMargins
     * true to include also margins
     */
    public int outerWidth(boolean includeMargins) {
        if (includeMargins)
            return outerWidth() + marginLeft()+marginRight();
        return outerWidth();
    }
    /**
     * Get the current computed height for the first element in the set of matched elements, including paddings
     */
    public int outerHeight() {
        return height()+paddingTop()+paddingBottom();
    }

    /**
     * Get the current computed height for the first element in the set of matched elements, including paddings
     * @param includeMargins
     * true to include also margins
     */
    public int outerHeight(boolean includeMargins) {
        if (includeMargins)
            return outerHeight() + marginTop()+marginBottom();
        return outerHeight();
    }
    /**
     * Get the current computed width for the first element in the set of matched elements, including paddings
     * Does exactly the same thing as outerWidth() method
     */
    public int innerWidth() {
        return outerWidth();
    }
    /**
     * Set the width of the elements so that their new innerWidth() value take the specified one
     */
    public AQuery innerWidth(int value) {
        width(Math.max(0, width() + value - innerWidth()));
        return this;
    }
    /**
     * Get the current computed width for the first element in the set of matched elements, including paddings
     * Does exactly the same thing as outerHeight() method
     */
    public int innerHeight() {
        return outerHeight();
    }
    /**
     * Set the height of the elements so that their new innerHeight() value take the specified one
     */
    public AQuery innerHeight(int value) {
        height(Math.max(0, height() + value - innerHeight()));
        return this;
    }

    /**
     * Returns the width occupied by the View content, in px
     */
    public int scrollWidth() {
        View v = head();
        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return v.getMeasuredWidth();
    }
    /**
     * Returns the width  occupied by the View content, in px
     */
    public int scrollHeight() {
        View v = head();
        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return v.getMeasuredHeight();
    }

    /**
     * An interface used in measure function
     */
    public interface MeasureListener {
        /**
         * The function called when the View is measured
         * @param v
         * The view
         * @param width
         * The width measured
         * @param height
         * The height measured
         */
        void onMeasure(View v, int width, int height);
    }
    public void measure(final MeasureListener callback) {
        for (final View v : list()) {
            v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    callback.onMeasure(v, v.getWidth(), v.getHeight());
                }
            });
        }
    }

    /**
     * Returns the layout params of the first element in the set of elements
     */
    public ViewGroup.LayoutParams lp() {
        return head().getLayoutParams();
    }
    /**
     * Sets the layout params of the elements
     */
    public void lp(ViewGroup.LayoutParams params) {
        for (View v : list())
            v.setLayoutParams(params);
    }

    /**
     * Hides the View
     */
    private static void hide(View v) {
        v.setVisibility(View.GONE);
    }
    /**
     * Shows the View
     */
    private static void show(View v) {
        v.setVisibility(View.VISIBLE);
    }
    /**
     * Checks if the View is visible
     */
    protected static boolean visible(View v) {
        return (v.getVisibility() != View.GONE);
    }

    /**
     * Checks if the first element in the set of elements is visible
     * @return
     * true if it's visible, false otherwise
     */
    public boolean visible() {
        return visible(head());
    }
    /**
     * Shows the elements
     */
    public AQuery show() {
        for (View v : list())
            show(v);
        return this;
    }
    /**
     * Hides the elements
     */
    public AQuery hide() {
        for (View v : list())
            hide(v);
        return this;
    }

    /**
     * Hides the visible elements, and shows the others
     */
    public AQuery toggle() {
        for (View v : list()) {
            if (visible(v))
                hide(v);
            else
                show(v);
        }
        return this;
    }

    /**
     * Bind two or more handlers to the matched elements, to be executed on alternate clicks
     * @param handlers
     * The first handler will be executed the first
     * The second handler will be executed the second
     * etc
     * When all the handlers have been executed, we go back to the first handler
     */
    public void toggle(final View.OnClickListener... handlers) {
        for (View v : list()) {
            final int[] state = {0};
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handlers[state[0]].onClick(v);
                    if (++state[0] == handlers.length)
                        state[0] = 0;
                }
            });
        }
    }

    /**
     * Hides progressively the elements
     */
    public AQuery fadeOut() {
        return fadeOut(DEFAULT_DELAY);
    }

    /**
     * Hides progressively the elements
     * @param duration
     * The duration of the animation, in ms
     */
    public AQuery fadeOut(int duration) {
        return fadeOut(duration, null);
    }

    /**
     * Hides progressively the elements
     * @param duration
     * The duration of the animation, in ms
     * @param complete
     * The function to call when the animation is complete
     * @return
     */
    public AQuery fadeOut(int duration, final CompleteListener complete) {
        return animate(Transition.prop("alpha", 0), duration, new CompleteListener() {
            @Override
            public void complete(View v) {
                if (complete != null)
                    complete.complete(v);
                hide(v);
            }
        }, false);
    }
    /**
     * Shows progressively the elements
     */
    public AQuery fadeIn() {
        return fadeIn(DEFAULT_DELAY);
    }
    /**
     * Shows progressively the elements
     * @param duration
     * The duration of the animation, in ms
     */
    public AQuery fadeIn(int duration) {
        return fadeIn(duration, null);
    }

    /**
     * Shows progressively the elements
     * @param duration
     * The duration of the animation, in ms
     * @param complete
     * The function to call when the animation is complete
     */
    public AQuery fadeIn(int duration, final CompleteListener complete) {
        for (View v : list()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                v.setAlpha(0);
        }
        return animate(Transition.prop("alpha", 1), duration, new AnimationListener() {
            @Override
            public void start(View v) {
                show(v);
            }

            @Override
            public void step(View v, float t) {
            }

            @Override
            public void complete(View v) {
                if (complete != null)
                    complete.complete(v);
            }

            @Override
            public void always(View v) {
            }
        }, false);
    }

    /**
     * Triggers a click on the elements
     */
    public AQuery click() {
        for (View v : list())
            v.performClick();
        return this;
    }

    /**
     * Triggers a touch on the elements
     * @param e
     * The data to the pass to the touch event
     */
    public AQuery touch(MotionEvent e) {
        for (View v : list())
            v.dispatchTouchEvent(e);
        return this;
    }

    /**
     * Sets the function to be called when the user clicks on one of the elements
     * @param l
     * The function to call on click
     */
    public AQuery click(View.OnClickListener l) {
        for (View v : list())
            v.setOnClickListener(l);
        return this;
    }
    /**
     * Sets the function to be called when the user clicks on one of the elements
     * @param functionName
     * The name of the function in the activity class.
     * The function must be public and take a View for the first argument
     */
    public AQuery click(String functionName) {
        click(formatOnClick(functionName));
        return this;
    }

    /**
     * Sets the function to be called when the user touches one of the elements
     * @param l
     * The function to call on touch
     */
    public AQuery touch(View.OnTouchListener l) {
        for (View v : list())
            v.setOnTouchListener(l);
        return this;
    }
    /**
     * Sets the function to be called when the user puts the finger on one of the elements
     * @param l
     * The function to call on touch
     */
    public AQuery hover(View.OnHoverListener l) {
        for (View v : list()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                v.setOnHoverListener(l);
        }
        return this;
    }

    /**
     * An interface user in hover() method
     */
    public interface IOHoverListener {
        /**
         * The function called when the user starts touching the View
         */
        void enter(View v, MotionEvent event);
        /**
         * The function called when the user stops touching the View
         */
        void leave(View v, MotionEvent event);
    }

    /**
     * Sets the functions to be called when the user touches down and touches up one of the elements
     * @param handler
     * The functions to call
     */
    public AQuery hover(final IOHoverListener handler) {
        for (View v : list()) {
            final int[] exitID = {0};
            final boolean[] longClicked = {false};
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            longClicked[0] = false;
                            handler.enter(v, event);
                            final int aExitID = exitID[0];
                            v.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (exitID[0] == aExitID) {
                                        if (v.performLongClick())
                                            longClicked[0] = true;
                                    }
                                }
                            }, ViewConfiguration.getLongPressTimeout());
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_UP:
                            exitID[0]++;
                            handler.leave(v, event);
                            if (!longClicked[0] && isPointInsideView(event.getX(), event.getY(), v))
                                v.performClick();
                            break;
                        default:
                            exitID[0]++;
                            handler.leave(v, event);
                    }
                    ctx.onTouchEvent(event);
                    return true;
                }
            });
        }
        return this;
    }

    /**
     * Checks if a point is inside a view
     * @param x
     * The x-coordinate of the point
     * @param y
     * The y-coordinate of the point
     * @param view
     * The view
     * @return
     * true if the point is on the view, false otherwise
     */
    private static boolean isPointInsideView(float x, float y, View view) {
        return ((x >= 0) && (y >= 0) && (x < view.getWidth()) && (y < view.getHeight()));
    }

    /**
     * Converts an element to a list containing the element
     */
    public static <T> ArrayList<T> singleton(T elt) {
        ArrayList<T> res = new ArrayList<>();
        res.add(elt);
        return res;
    }

    /**
     * Changes a property of the elements when the user touches it
     * @param attr
     * The attribute to change
     */
    public AQuery hover(PropertyTransition attr) {
        return hover(singleton(attr));
    }
    /**
     * Changes the properties of the elements when the user touches it
     * @param attrs
     * The attributes to change
     */
    public AQuery hover(PropertyTransition[] attrs) {
        return hover(attrs,0);
    }
    /**
     * Changes a property of the elements when the user touches it
     * @param attr
     * The attribute to change
     * @param timeMS
     * The duration of the animation
     */
    public AQuery hover(PropertyTransition attr, int timeMS) {
        return hover(singleton(attr),timeMS);
    }
    /**
     * Changes the properties of the elements when the user touches it
     * @param attrs
     * The attributes to change
     * @param timeMS
     * The duration of the animation
     */
    public AQuery hover(PropertyTransition[] attrs, int timeMS) {
        return hover(attrs,timeMS,DEFAULT_EASING);
    }
    /**
     * Changes a property of the elements when the user touches it
     * @param attr
     * The attribute to change
     * @param timeMS
     * The duration of the animation
     */
    public AQuery hover(PropertyTransition attr, int timeMS, StartListener onStartHover, StartListener onStartExit) {
        return hover(singleton(attr), timeMS, onStartHover, onStartExit);
    }
    /**
     * Changes the properties of the elements when the user touches it
     * @param attrs
     * The attributes to change
     * @param timeMS
     * The duration of the animation
     */
    public AQuery hover(PropertyTransition[] attrs, int timeMS, StartListener onStartHover, StartListener onStartExit) {
        return hover(attrs,timeMS,DEFAULT_EASING,toAnimListener(onStartHover),toAnimListener(onStartExit));
    }
    /**
     * Changes a property of the elements when the user touches it
     * @param attr
     * The attribute to change
     * @param timeMS
     * The duration of the animation
     * @param easing
     * The easing function
     */
    public AQuery hover(PropertyTransition attr, int timeMS, String easing) {
        return hover(singleton(attr), timeMS, easing);
    }
    /**
     * Changes the properties of the elements when the user touches it
     * @param attrs
     * The attributes to change
     * @param timeMS
     * The duration of the animation
     * @param easing
     * The easing function
     */
    public AQuery hover(PropertyTransition[] attrs, int timeMS, String easing) {
        return hover(attrs,timeMS,easing,(AnimationListener)null,(AnimationListener)null);
    }
    /**
     * Changes a property of the elements when the user touches it
     * @param attr
     * The attribute to change
     * @param timeMS
     * The duration of the animation
     * @param easing
     * The easing function
     */
    public AQuery hover(PropertyTransition attr, int timeMS, String easing, StartListener onStartHover, StartListener onStartExit) {
        return hover(singleton(attr), timeMS, easing, onStartHover, onStartExit);
    }
    /**
     * Changes the properties of the elements when the user touches it
     * @param attrs
     * The attributes to change
     * @param timeMS
     * The duration of the animation
     * @param easing
     * The easing function
     */
    public AQuery hover(PropertyTransition[] attrs, int timeMS, String easing, StartListener onStartHover, StartListener onStartExit) {
        return hover(attrs,timeMS,easing,toAnimListener(onStartHover),toAnimListener(onStartExit));
    }
    /**
     * Changes a property of the elements when the user touches it
     * @param attr
     * The attribute to change
     * @param timeMS
     * The duration of the animation
     * @param easing
     * The easing function
     * @param hoverCallback
     * The callback of the animation launched when the user starts touching the element
     * @param outCallback
     * The callback of the animation launched when the user stops touching the element
     */
    public AQuery hover(PropertyTransition attr, int timeMS, String easing, AnimationListener hoverCallback, AnimationListener outCallback) {
        return hover(singleton(attr), timeMS, easing, hoverCallback, outCallback);
    }
    /**
     * Changes the properties of the elements when the user touches it
     * @param attrs
     * The attributes to change
     * @param timeMS
     * The duration of the animation
     * @param easing
     * The easing function
     * @param hoverCallback
     * The callback of the animation launched when the user starts touching the element
     * @param outCallback
     * The callback of the animation launched when the user stops touching the element
     */
    public AQuery hover(PropertyTransition[] attrs, int timeMS, String easing, AnimationListener hoverCallback, AnimationListener outCallback) {
        return hover(attrs, timeMS, toEasing(easing), hoverCallback, outCallback);
    }

    /**
     * Changes the properties of the elements when the user touches it
     * @param attrs
     * The attributes to change
     * @param timeMS
     * The duration of the animation
     * @param easing
     * The easing function
     * @param hoverCallback
     * The callback of the animation launched when the user starts touching the element
     * @param outCallback
     * The callback of the animation launched when the user stops touching the element
     */
    public AQuery hover(final PropertyTransition[] attrs, final int timeMS, final EaseListener easing, final AnimationListener hoverCallback, final AnimationListener outCallback) {
        for (View v : list()) {
            final PropertyTransition[] oldAttrs = new PropertyTransition[attrs.length];
            $Element elt = new $Element(ctx,v);
            for (int i=0;i<attrs.length;i++)
                oldAttrs[i] = Transition.prop(attrs[i].attr,elt.prop(attrs[i].attr));
            elt.hover(new IOHoverListener() {
                @Override
                public void enter(View v, MotionEvent event) {
                    $Element.animateView(ctx, v, new AnimationParams(attrs, timeMS, easing, hoverCallback, false));
                }

                @Override
                public void leave(View v, MotionEvent event) {
                    $Element.animateView(ctx, v, new AnimationParams(oldAttrs, timeMS, easing, outCallback, false));
                }
            });
        }
        return this;
    }

    /**
     * Sets the function to be called when the user long-clicks on one of the elements
     * @param l
     * The function to call on click
     */
    public AQuery longClick(View.OnLongClickListener l) {
        for (View v : list())
            v.setOnLongClickListener(l);
        return this;
    }
    /**
     * Sets the function to be called when the makes a context menu appear one of the elements
     * @param l
     * The function to call on context menu
     */
    public AQuery contextMenu(View.OnCreateContextMenuListener l) {
        for (View v : list())
            v.setOnCreateContextMenuListener(l);
        return this;
    }

    /**
     * Gives the focus on of the first element of the set of elements
     */
    public AQuery focus() {
        head().requestFocus();
        return this;
    }
    /**
     * Gives the focus on of the first element of the set of elements
     * @param showKeyboard
     * if true, shows the keyboard as well
     */
    public AQuery focus(boolean showKeyboard) {
        focus();
        showKeyboard();
        return this;
    }
    /**
     * Sets the function to be called when one of the elements gets the focus
     * @param l
     * The function to call on focus
     */
    public AQuery focus(View.OnFocusChangeListener l) {
        for (View v : list())
            v.setOnFocusChangeListener(l);
        return this;
    }
    /**
     * Selects the text of the element. Works only on EditTexts
     */
    public AQuery select() {
        ((EditText) head()).selectAll();
        return this;
    }
    /**
     * Selects the text of the element. Works only on EditTexts
     * @param showKeyboard
     * if true, shows the keyboard as well
     */
    public AQuery select(boolean showKeyboard) {
        select();
        showKeyboard();
        return this;
    }
    /**
     * Selects a specified amount of text of the element. Works only on EditTexts
     */
    public AQuery select(int start, int stop) {
        ((EditText) head()).setSelection(start, stop);
        return this;
    }

    /**
     * Puts the cursor at the specifies area of the element. Works only on EditTexts
     */
    public AQuery cursor(int index) {
        ((EditText) head()).setSelection(index);
        return this;
    }
    /**
     * Removes the focus on the first element in the set of elements
     */
    public AQuery blur() {
        head().clearFocus();
        return this;
    }
    /**
     * Clears the focus on of the first element in the set of elements
     * @param hideKeyboard
     * if true, hides the keyboard as well
     */
    public AQuery blur(boolean hideKeyboard) {
        blur();
        hideKeyboard();
        return this;
    }
    public void showKeyboard() {
        showKeyboard(0);
    }
    /**
     * Shows the software keyboard to the screen
     * Assumes that the first element in the set of elements already has the focus
     * If it's not the case, call focus(true) instead
     * @param flags
     * Provides additional operating flags. Currently may be
     * 0 or have the InputMethodManager.HIDE_IMPLICIT_ONLY bit set.
     */
    public void showKeyboard(final int flags) {
        head().post(new Runnable() {
            @Override
            public void run() {
                ((InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(head(), flags);
            }
        });
    }
    /**
     * Hides the software keyboard from the screen
     * Assumes that the first element in the set of elements already has the focus
     * If it's not the case, call blur(true) instead
     */
    public void hideKeyboard() {
        hideKeyboard(0);
    }
    /**
     * Hides the software keyboard from the screen
     * Assumes that the first element in the set of elements already has the focus
     * If it's not the case, call blur(true) instead
     * @param flags
     * Provides additional operating flags. Currently may be
     * 0 or have the InputMethodManager.HIDE_IMPLICIT_ONLY bit set.
     */
    public void hideKeyboard(final int flags) {
        head().post(new Runnable() {
            @Override
            public void run() {
                ((InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(head().getWindowToken(), flags);
            }
        });
    }

    /**
     * Sets the function to be called when the user presses a key on one of the elements
     * @param l
     * The function to call on key pressed
     */
    public AQuery key(View.OnKeyListener l) {
        for (View v : list())
            v.setOnKeyListener(l);
        return this;
    }
    /**
     * Sets the function to be called when the user changed something on one of the elements
     * @param l
     * The function to call on key pressed
     */
    public AQuery change(TextView.OnEditorActionListener l) {
        for (View v : list())
            ((TextView) v).setOnEditorActionListener(l);
        return this;
    }

    public AQuery check(CompoundButton.OnCheckedChangeListener l) {
        for (View v : list())
            ((CompoundButton) v).setOnCheckedChangeListener(l);
        return this;
    }

    /**
     * Get the current horizontal position of the scroll bar for the first element in the set of elements
     */
    public int scrollLeft() {
        return head().getScrollX();
    }
    /**
     * Sets the current horizontal position of the scroll bar for each element
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public AQuery scrollLeft(int x) {
        head().setScrollX(x);
        return this;
    }
    /**
     * Get the current horizontal position of the scroll bar for the first element in the set of elements
     */
    public int scrollTop() {
        return head().getScrollY();
    }
    /**
     * Sets the current vertical position of the scroll bar for each element
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public AQuery scrollTop(int y) {
        head().setScrollY(y);
        return this;
    }

    /**
     * Get the current coordinates of the first element in the set of matched elements,
     * relative to the root view of the activity
     * @return
     * An int array containing the x and y coordinate, in that order
     */
    public int[] offset() {
        int[] pos = new int[2], origin = new int[2];
        head().getLocationInWindow(pos);
        new $Document(ctx).head().getLocationInWindow(origin);
        pos[0] -= origin[0];
        pos[1] -= origin[1];
        return pos;
    }
    /**
     * Get the current x coordinate of the first element in the set of matched elements,
     * relative to the root view of the activity
     */
    public int offsetLeft() {
        return offset()[0];
    }
    /**
     * Get the current y coordinate of the first element in the set of matched elements,
     * relative to the root view of the activity
     */
    public int offsetTop() {
        return offset()[1];
    }

    /**
     * Insert each element to the end of the first element in the set of elements
     * @param q
     * The element to append
     */
    public AQuery append(AQuery q) {
        ViewGroup parent = (ViewGroup) head();
        for (View child : q.list())
            parent.addView(child);
        return this;
    }
    /**
     * Parses the XML code and appends the result to each elements
     * @param xml
     * The code to parse
     * @deprecated This code is really slow to execute. Prefer creating an XML file in your layout folder
     * and calling append(int) method instead
     */
    public AQuery append(String xml) {
        for (View parent : list())
            new $Array(ctx, xml, parent);
        return this;
    }
    /**
     * Parses the XML layout and appends the result to each elements
     * @param layout
     * The ressoure id of the layout
     */
    public AQuery append(int layout) {
        for (View parent : list())
            new $Constructors(ctx).$(layout, (ViewGroup) parent);
        return this;
    }
    /**
     * Insert the first element in the set of elements to the end of the target
     * @param q
     * The target elements
     */
    public AQuery appendTo(AQuery q) {
        q.append(this);
        return this;
    }

    /**
     * Insert each element to the beginning of the first element in the set of elements
     * @param q
     * The element to append
     */
    public AQuery prepend(AQuery q) {
        ViewGroup parent = (ViewGroup) head();
        for (View child : q.list())
            parent.addView(child, 0);
        return this;
    }
    /**
     * Parses the XML code and prepends the result to each elements
     * @param xml
     * The code to parse
     * @deprecated This code is really slow to execute. Prefer creating an XML file in your layout folder
     * and calling prepend(int) method instead
     */
    public AQuery prepend(String xml) {
        for (View parent : list())
            new $Element(ctx, parent).prepend(new $Array(ctx, xml, parent, false));
        return this;
    }
    /**
     * Parses the XML layout and prepends the result to each elements
     * @param layout
     * The ressoure id of the layout
     */
    public AQuery prepend(int layout) {
        for (View parent : list())
            new $Element(ctx, parent).prepend(new $Constructors(ctx).$(layout, (ViewGroup) parent, false));
        return this;
    }
    /**
     * Insert the first element in the set of elements to the beginning of the target
     * @param q
     * The target elements
     */
    public AQuery prependTo(AQuery q) {
        q.prepend(this);
        return this;
    }

    /**
     * Returns the position of the View in the list of the children having the same parent
     * @param child
     * The child
     */
    private static int findPosition(View child) {
        return findPosition((ViewGroup) child.getParent(), child);
    }
    /**
     * Returns the position, from the end, of the View in the list of the children having the same parent
     * @param child
     * The child
     */
    private static int findLastPosition(View child) {
        return findLastPosition((ViewGroup) child.getParent(), child);
    }
    /**
     * Returns the position of the View in the list of the children having the same parent
     * @param child
     * The child
     * @param parent
     * The parent
     */
    private static int findPosition(ViewGroup parent, View child) {
        int res;
        for (res=0;parent.getChildAt(res)!=child;res++);
        return res;
    }
    /**
     * Returns the position, from the end, of the View in the list of the children having the same parent
     * @param child
     * The child
     * @param parent
     * The parent
     */
    private static int findLastPosition(ViewGroup parent, View child) {
        return parent.getChildCount()-findPosition(parent,child);
    }

    /**
     * Insert each element after the first element in the set of elements
     */
    public AQuery after(AQuery q) {
        View elt = head();
        ViewGroup parent = (ViewGroup) elt.getParent();
        int position = findPosition(parent, elt);
        for (View child : q.list()) {
            position++;
            parent.addView(child, position);
        }
        return this;
    }
    /**
     * Parses the XML code and insert the result after each element
     * @param xml
     * The code to parse
     * @deprecated This code is really slow to execute. Prefer creating an XML file in your layout folder
     * and calling after(int) method instead
     */
    public AQuery after(String xml) {
        for (View elt : list())
            new $Element(ctx, elt).after(new $Array(ctx, xml, elt.getParent(), false));
        return this;
    }
    /**
     * Parses the XML layout and insert the result after each element
     * @param layout
     * The ressoure id of the layout
     */
    public AQuery after(int layout) {
        for (View elt : list())
            new $Element(ctx, elt).after(new $Constructors(ctx).$(layout, (ViewGroup) elt.getParent(), false));
        return this;
    }

    /**
     * Insert the first element in the set of elements after the target
     * @param q
     * The target elements
     * @return
     */
    public AQuery insertAfter(AQuery q) {
        q.after(this);
        return this;
    }
    /**
     * Insert each element before the first element in the set of elements
     */
    public AQuery before(AQuery q) {
        View elt = head();
        ViewGroup parent = (ViewGroup) elt.getParent();
        int position = findPosition(parent, elt);
        for (View child : q.list()) {
            parent.addView(child, position);
            position++;
        }
        return this;
    }
    /**
     * Parses the XML code and insert the result before each element
     * @param xml
     * The code to parse
     * @deprecated This code is really slow to execute. Prefer creating an XML file in your layout folder
     * and calling before(int) method instead
     */
    public AQuery before(String xml) {
        for (View elt : list())
            new $Element(ctx, elt).before(new $Array(ctx, xml, elt.getParent(), false));
        return this;
    }
    /**
     * Parses the XML layout and insert the result before each element
     * @param layout
     * The ressoure id of the layout
     */
    public AQuery before(int layout) {
        for (View elt : list())
            new $Element(ctx, elt).before(new $Constructors(ctx).$(layout, (ViewGroup) elt.getParent(), false));
        return this;
    }
    /**
     * Insert the first element in the set of elements before the target
     * @param q
     * The target elements
     * @return
     */
    public AQuery insertBefore(AQuery q) {
        q.before(this);
        return this;
    }

    /**
     * Returns the element directly before each element in the set of elements
     */
    public AQuery prev() {
        List<View> elts = list();
        ArrayList<View> res = new ArrayList<>(elts.size());
        for (View elt : elts) {
            ViewGroup parent = (ViewGroup) elt.getParent();
            int position = findPosition(parent, elt);
            if (position > 0)
                res.add(parent.getChildAt(position-1));
        }
        return new $Array(ctx, res);
    }
    /**
     * Returns the element directly before each element and keeps the one who match the selector
     */
    public AQuery prev(String selector) {
        return next().filter(selector);
    }
    /**
     * Returns the element before each element in the set of elements
     */
    public AQuery prevAll() {
        List<View> elts = list();
        ArrayList<View> res = new ArrayList<>();
        for (View elt : elts) {
            ViewGroup parent = (ViewGroup) elt.getParent();
            int position = findPosition(parent, elt);
            for (int i=0;i<position;i++)
                res.add(parent.getChildAt(i));
        }
        return new $Array(ctx, res);
    }
    /**
     * Returns the element before each element and keeps the one who match the selector
     */
    public AQuery prevAll(String selector) {
        return prevAll().filter(selector);
    }
    /**
     * Returns the element directly after each element in the set of elements
     */
    public AQuery next() {
        List<View> elts = list();
        ArrayList<View> res = new ArrayList<>(elts.size());
        for (View elt : elts) {
            ViewGroup parent = (ViewGroup) elt.getParent();
            int position = 1+findPosition(parent, elt);
            if (position < parent.getChildCount())
                res.add(parent.getChildAt(position));
        }
        return new $Array(ctx, res);
    }
    /**
     * Returns the element directly after each element and keeps the one who match the selector
     */
    public AQuery next(String selector) {
        return next().filter(selector);
    }
    /**
     * Returns the element after each element in the set of elements
     */
    public AQuery nextAll() {
        List<View> elts = list();
        ArrayList<View> res = new ArrayList<>();
        for (View elt : elts) {
            ViewGroup parent = (ViewGroup) elt.getParent();
            int position = 1+findPosition(parent, elt);
            for (int i=position;i<parent.getChildCount();i++)
                res.add(parent.getChildAt(i));
        }
        return new $Array(ctx, res);
    }
    /**
     * Returns the element after each element and keeps the one who match the selector
     */
    public AQuery nextAll(String selector) {
        return nextAll().filter(selector);
    }

    /**
     * Removes the element from the activity
     */
    public AQuery remove() {
        for (View v : list())
            ((ViewGroup) v.getParent()).removeView(v);
        return this;
    }
    /**
     * Removes all the children of the elements
     */
    public AQuery empty() {
        for (View v : list())
            ((ViewGroup) v).removeAllViews();
        return this;
    }

    /**
     * Sets the inner XML of the View
     * @param xml
     * The new innerXML
     * @deprecated This code is really slow to execute. Prefer creating an XML file in your layout folder
     * and calling xml(int) method instead
     */
    public AQuery xml(String xml) {
        empty();
        new $Array(ctx, xml, this);
        return this;
    }
    /**
     * Sets the inner XML of the View from a layout resource
     * @param layout
     * The ressoure id of the layout
     */
    public AQuery xml(int layout) {
        empty();
        for (View v : list())
            LayoutInflater.from(ctx).inflate(layout, (ViewGroup) v);
        return this;
    }

    /**
     * Returns an AJAX callback that appends the XML content to the elements when the page is loaded
     */
    private $Utils.AjaxQuery.NetworkListener displayOnLoad() {
        return new $Utils.AjaxQuery.NetworkListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void done(String res) {
                xml(res);
            }

            @Override
            public void fail(Exception e) {
            }

            @Override
            public void always(boolean success) {
            }
        };
    }

    /**
     * Returns an AJAX callback that appends the XML content to the elements when the page is loaded
     * @param l
     * The other callbacks to call in addition to the default behaviour
     */
    private $Utils.AjaxQuery.NetworkListener displayAndFire(final $Utils.AjaxQuery.NetworkListener l) {
        return new $Utils.AjaxQuery.NetworkListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void done(String res) {
                xml(res);
                l.done(res);
            }

            @Override
            public void fail(Exception e) {
                l.fail(e);
            }

            @Override
            public void always(boolean success) {
                l.always(success);
            }
        };
    }

    /**
     * Loads data from the server and place the returned XML into the elements
     * @param url
     * The url of the page to load
     */
    public AQuery load(String url) {
        new $Utils(ctx).get(url, displayOnLoad());
        return this;
    }
    /**
     * Loads data from the server and place the returned XML into the elements
     * @param url
     * The url of the page to load
     * @param callback
     * The functions to call when an event occurs
     */
    public AQuery load(String url, final $Utils.AjaxQuery.NetworkListener callback) {
        new $Utils(ctx).get(url, displayAndFire(callback));
        return this;
    }

    /**
     * Loads data from the server and place the returned XML into the elements
     * @param url
     * The url of the page to load
     * @param data
     * The POST data, in the form "param1=value1&param2=value2"
     */
    public AQuery load(String url, String data) {
        new $Utils(ctx).post(url, data, displayOnLoad());
        return this;
    }
    /**
     * Loads data from the server and place the returned XML into the elements
     * @param url
     * The url of the page to load
     * @param data
     * An object containing the POST data
     */
    public AQuery load(String url, $Utils.AjaxQuery.PostData[] data) {
        new $Utils(ctx).post(url, data, displayOnLoad());
        return this;
    }
    /**
     * Loads data from the server and place the returned XML into the elements
     * @param url
     * The url of the page to load
     * @param data
     * The POST data, in the form "param1=value1&param2=value2"
     * @param callback
     * The functions to call when an event occurs
     */
    public AQuery load(String url, String data, $Utils.AjaxQuery.NetworkListener callback) {
        new $Utils(ctx).post(url, data, displayAndFire(callback));
        return this;
    }
    /**
     * Loads data from the server and place the returned XML into the elements
     * @param url
     * The url of the page to load
     * @param data
     * An object containing the POST data
     * @param callback
     * The functions to call when an event occurs
     */
    public AQuery load(String url, $Utils.AjaxQuery.PostData[] data, $Utils.AjaxQuery.NetworkListener callback) {
        new $Utils(ctx).post(url, data, displayAndFire(callback));
        return this;
    }

    /**
     * Returns the direct parents of the elements
     */
    public $Array parent() {
        List<View> children = list();
        ArrayList<View> res = new ArrayList<>(children.size());
        for (View child : children)
            res.add((View) child.getParent());
        return new $Array(ctx, res);
    }

    /**
     * Returns the ID of the first element in the set of elements
     */
    public int id() {
        return head().getId();
    }

    /**
     * Sets the id of the elements
     */
    public AQuery id(int value) {
        for (View v : list())
            v.setId(value);
        return this;
    }
    /**
     * Returns the text content of first element in the set of elements
     */
    public String text() {
        return ((TextView) head()).getText().toString();
    }
    /**
     * Sets the text content of the element
     */
    public AQuery text(String value) {
        for (View v : list())
            ((TextView) v).setText(value);
        return this;
    }
    /**
     * Sets the text content of the element from a String resource
     */
    public AQuery text(int resourceID) {
        for (View v : list())
            ((TextView) v).setText(resourceID);
        return this;
    }

    /**
     * Sets the image content of the element from a drawable
     * Works only on ImageViews
     */
    public AQuery src(Drawable drawable) {
        for (View v : list())
            ((ImageView) v).setImageDrawable(drawable);
        return this;
    }
    /**
     * Sets the image content of the element from a bitmap
     * Works only on ImageViews
     */
    public AQuery src(Bitmap bmp) {
        for (View v : list())
            ((ImageView) v).setImageBitmap(bmp);
        return this;
    }
    /**
     * Sets the image content of the element from a drawable resource
     * Works only on ImageViews
     */
    public AQuery src(int resourceID) {
        for (View v : list())
            ((ImageView) v).setImageResource(resourceID);
        return this;
    }
    /**
     * Returns the text content of first element in the set of elements
     * This method does exactly the same thing as text() method
     */
    public String val() {
        return text();
    }
    /**
     * Sets the text content of the element
     * This method does exactly the same thing as text(String) method
     */
    public AQuery val(String value) {
        return text(value);
    }
    /**
     * Sets the text content of the element
     * This method does exactly the same thing as text(int) method
     */
    public AQuery val(int value) {
        return text(value);
    }

    /**
     * Returns the background drawable of the first element in the set of elements
     */
    public Drawable background() {
        return head().getBackground();
    }
    /**
     * Sets the background drawable of the elements
     */
    @SuppressWarnings("deprecation")
    public void background(Drawable value) {
        for (View v : list())
            v.setBackgroundDrawable(value);
    }
    /**
     * Returns the background color of the first element in the set of elements
     * If the element has no background color, returns Color.TRANSPARENT
     */
    public int backgroundColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            try {
                return ((ColorDrawable) background()).getColor();
            }
            catch (ClassCastException e) {
            }
            catch (NullPointerException e) {
            }
        }
        return Color.TRANSPARENT;
    }
    /**
     * Sets the background color of the elements
     * @param value
     * The color int code
     */
    public void backgroundColor(int value) {
        for (View v : list())
            v.setBackgroundColor(value);
    }

    /**
     * Sets the background color of the elements
     * @param value
     * The color value, in one of the supported color formats
     */
    public void backgroundColor(String value) {
        backgroundColor(formatColor(value));
    }

    /**
     * An interface used for general transitions in an animation
     */
    public interface TransitionListener {
        /**
         * Function to call when the animation starts
         */
        Object getStart();
        /**
         * Function to call at each step of the animation
         * @param t
         * A number between 0 (animation beginning) and 1 (animation complete) representing the progress of the animation
         */
        Object getMid(float t);
        /**
         * Function to call when the animation finishes
         */
        Object getEnd();
    }

    /**
     * A handler used to create animation only when it will start
     */
    protected interface AnimationHandler {
        /**
         * Returns the animation to launch
         * This function is called when the animation is about to start
         */
        AnimationParams create(View v);
    }

    /**
     * The class to handle any type of transitions
     */
    public abstract static class Transition {
        /**
         * Function called when the animation starts
         */
        abstract Object getStart();
        /**
         * Function called at each step of the animation
         * @param t
         * A number between 0 (animation beginning) and 1 (animation complete) representing the progress of the animation
         */
        abstract Object getMid(float t);
        /**
         * Function called when the animation finishes
         */
        abstract Object getEnd();

        /**
         * Returns a linear interpolation of 2 ints.
         * That is, if t=0, returns a, if t=1, returns b, otherwise returns an intermediate number between a and b
         */
        protected int progress(int a, int b, float t) {
            return Math.round(fprogress(a, b, t));
        }
        /**
         * Returns a linear interpolation of 2 longs.
         */
        protected long lprogress(long a, long b, float t) {
            return Math.round(dprogress(a, b, t));
        }
        /**
         * Returns a linear interpolation of 2 floats.
         */
        protected float fprogress(float a, float b, float t) {
            return a+t*(b-a);
        }
        /**
         * Returns a linear interpolation of 2 doubles.
         */
        protected double dprogress(double a, double b, float t) {
            return a+t*(b-a);
        }

        /**
         * Returns the adapted transition for a perticullar property
         * @param key
         * The XML attribute
         * @param val
         * The value at the end of the transition
         */
        public static PropertyTransition prop(String key, Object val) {
            return new PropertyTransition(key, val);
        }
        /**
         * Returns the adapted transition for a perticullar property
         * @param key
         * The XML attribute
         * @param val
         * The value at the end of the transition, in a XML-like String format
         */
        public static PropertyTransition attr(String key, String val) {
            return new PropertyTransition(key, val);
        }

        /**
         * Creates a custom transition for an animation
         * @param key
         * The attribute to change
         * @param t
         * The functions to call at each step of the animation
         */
        public static PropertyTransition custom(String key, Transition t) {
            return new PropertyTransition(key, t);
        }
    }

    /**
     * The transitions for colors
     */
    public static class ColorTransition extends Transition {
        private int c1, c2; // The start and end color
        public ColorTransition(int beginColor, int endColor) {
            c1 = beginColor;
            c2 = endColor;
        }
        @Override
        public Object getStart() {
            return c1;
        }
        @Override
        public Object getEnd() {
            return c2;
        }
        @Override
        public Object getMid(float t) {
            return Color.argb(progress(Color.alpha(c1),Color.alpha(c2), t), progress(Color.red(c1),Color.red(c2), t), progress(Color.green(c1),Color.green(c2), t), progress(Color.blue(c1),Color.blue(c2), t));
        }
    }
    /**
     * The transitions for drawables
     */
    public static class DrawableTransition extends Transition {
        private Drawable d1;
        private Drawable d2;
        private MergeDrawable dt;

        public DrawableTransition(Drawable beginDrawable, Drawable endDrawable) {
            d1 = beginDrawable;
            d2 = endDrawable;
            dt = new MergeDrawable(d1,d2, 0);
        }

        @Override
        public Object getStart() {
            return d1;
        }
        @Override
        public Object getEnd() {
            return d2;
        }
        @Override
        public Object getMid(float t) {
            dt.setWeight(t);
            return dt;
        }
    }
    /**
     * The transitions for ints
     */
    public static class IntTransition extends Transition {
        private int n1, n2;
        public IntTransition(int beginInt, int endInt) {
            n1 = beginInt;
            n2 = endInt;
        }
        @Override
        public Object getStart() {
            return n1;
        }
        @Override
        public Object getEnd() {
            return n2;
        }
        @Override
        public Object getMid(float t) {
            return progress(n1, n2, t);
        }
    }
    /**
     * The transitions for longs
     */
    public static class LongTransition extends Transition {
        private long n1, n2;
        public LongTransition(long beginInt, long endInt) {
            n1 = beginInt;
            n2 = endInt;
        }
        @Override
        public Object getStart() {
            return n1;
        }
        @Override
        public Object getEnd() {
            return n2;
        }
        @Override
        public Object getMid(float t) {
            return lprogress(n1, n2, t);
        }
    }
    /**
     * The transitions for sizes
     */
    public static class SizeTransition extends IntTransition {
        private int px1, px2;
        public SizeTransition(int beginSize, int endSize) {
            this(beginSize, endSize, beginSize, endSize);
        }
        public SizeTransition(int beginSize, int endSize, int beginPx, int endPx) {
            super(beginSize, endSize);
            px1 = beginPx;
            px2 = endPx;
        }
        @Override
        public Object getMid(float t) {
            return progress(px1, px2, t);
        }
    }
    /**
     * The transitions for floats
     */
    public static class FloatTransition extends Transition {
        private float n1, n2;
        public FloatTransition(float beginFloat, float endFloat) {
            n1 = beginFloat;
            n2 = endFloat;
        }
        @Override
        public Object getStart() {
            return n1;
        }
        @Override
        public Object getEnd() {
            return n2;
        }
        @Override
        public Object getMid(float t) {
            return fprogress(n1, n2, t);
        }
    }
    /**
     * The transitions in the general case
     */
    public static class CustomTransition extends Transition {
        private TransitionListener l;
        public CustomTransition(TransitionListener callback) {
            l = callback;
        }
        @Override
        public Object getStart() {
            return l.getStart();
        }
        @Override
        public Object getEnd() {
            return l.getEnd();
        }
        @Override
        public Object getMid(float t) {
            return l.getMid(t);
        }
    }
    /**
     * A transition that does nothing
     * Use this class for properties where it's meaningless to do a transition
     * For example, a transition for "gravity" attribute would not mean anythin
     */
    private static class NoTransition extends Transition {
        private Object v;

        public NoTransition(Object value) {
            v = value;
        }

        @Override
        Object getStart() {
            return v;
        }

        @Override
        Object getMid(float t) {
            return v;
        }

        @Override
        Object getEnd() {
            return v;
        }
    }

    /**
     * A class to handle make the link between an attribute and its associated transition
     */
    public static class PropertyTransition {
        public final String attr; // The attribute
        private Transition function; // The functions to call at each step of the transition
        private Object val; // The value at the end of the transition
        private String sVal; // The XML-String value at the end of the transition
        private boolean isFunc = false, isProp = false, isAttr = false;

        /**
         * Constrctuor of PropertyTransition
         * @param key
         * The attribute
         * @param function
         * The functions to call at each step of the transition
         */
        private PropertyTransition(String key, Transition function) {
            this.attr = key;
            this.function = function;
            isFunc = true;
        }
        /**
         * Constrctuor of PropertyTransition
         * @param key
         * The attribute
         * @param val
         * The XML-String value at the end of the transition
         */
        private PropertyTransition(String key, String val) {
            this.attr = key;
            this.sVal = val;
            isAttr = true;
        }
        /**
         * Constrctuor of PropertyTransition
         * @param key
         * The attribute
         * @param val
         * The value at the end of the transition
         */
        private PropertyTransition(String key, Object val) {
            this.attr = key;
            this.val = val;
            isProp = true;
        }

        /**
         * Creates the adapted transition associated to the attribute
         * Call this function just before the animation begins
         * @param q
         * The element concerned by the animation
         */
        protected PropertyTransition create($Element q) {
            Transition res;
            if (isFunc)
                res = function;
            else {
                AttrSetter setter = getPropIfExists(attr);
                try {
                    if (isProp)
                        res = setter.getTransition(q, setter.get(q.head()), val);
                    else if (isAttr)
                        res = setter.getTransition(q, setter.get(q.head()), setter.format(q, sVal));
                    else
                        res = null;
                }
                catch (NoSuchMethodError e) {
                    res = null;
                }
            }
            return new PropertyTransition(attr,res);
        }

        /**
         * Processes an animation step
         * @param q
         * The element concerned by the animation
         * @param t
         * A number between 0 (animation beginning) and 1 (animation complete) representing the progress of the animation
         * @param l
         * The easing function
         */
        public void process(AQuery q, float t, EaseListener l) {
            Object prop;
            if (t <= 0)
                prop = function.getStart();
            else if (t >= 1)
                prop = function.getEnd();
            else
                prop = function.getMid(l.ease(t));
            q.prop(attr, prop);
        }
    }

    /**
     * An interface to call a function when the animation begins
     */
    public interface StartListener {
        void start(View v);
    }
    /**
     * An interface to call a function when the animation ends
     */
    public interface CompleteListener {
        void complete(View v);
    }

    /**
     * An interface to call a function at each step of the animation
     */
    public interface AnimationListener extends StartListener,CompleteListener {
        void step(View v, float t);
        void always(View v);
    }
    /**
     * An interface to handle the easing function, that is, the speed of the animation
     * See http://easings.net/ for more details
     */
    public interface EaseListener {
        public static final String SWING = "swing", LINEAR = "linear",
                EASE_IN_QUAD = "easeInQuad", EASE_OUT_QUAD = "easeOutQuad", EASE_IN_OUT_QUAD = "easeInOutQuad",
                EASE_IN_CUBIC = "easeInCubic", EASE_OUT_CUBIC = "easeOutCubic", EASE_IN_OUT_CUBIC = "easeInOutCubic",
                EASE_IN_QUART = "easeInQuart", EASE_OUT_QUART = "easeOutQuart", EASE_IN_OUT_QUART = "easeInOutQuart",
                EASE_IN_QUINT = "easeInQuint", EASE_OUT_QUINT = "easeOutQuint", EASE_IN_OUT_QUINT = "easeInOutQuint",
                EASE_IN_SINE = "easeInSine", EASE_OUT_SINE = "easeOutSine", EASE_IN_OUT_SINE = "easeInOutSine",
                EASE_IN_EXPO = "easeInExpo", EASE_OUT_EXPO = "easeOutExpo", EASE_IN_OUT_EXPO = "easeInOutExpo",
                EASE_IN_CIRC = "easeInCirc", EASE_OUT_CIRC = "easeOutCirc", EASE_IN_OUT_CIRC = "easeInOutCirc",
                EASE_IN_ELASTIC = "easeInElastic", EASE_OUT_ELASTIC = "easeOutElastic", EASE_IN_OUT_ELASTIC = "easeInOutElastic",
                EASE_IN_BACK = "easeInBack", EASE_OUT_BACK = "easeOutBack", EASE_IN_OUT_BACK = "easeInOutBack",
                EASE_IN_BOUNCE = "easeInBounce", EASE_OUT_BOUNCE = "easeOutBounce", EASE_IN_OUT_BOUNCE = "easeInOutBounce";

        /**
         * The easing function.
         * @param t
         * A number between 0 (beginning of the animation) and 1 (ending of the animation)
         * representing the time progress of the animation
         * @return
         * A number between 0 (start value) and 1 (end value) representing the value to take at the instant t
         */
        float ease(float t);
    }

    /**
     * A class to handle animations parameters : duration, easing function, etc
     */
	protected static class AnimationParams {
		private ArrayList<PropertyTransition> transitions;
        private int timeMS;
        private EaseListener easing;
        private AnimationListener callbacks;
        private boolean queue;

        /**
         * Constructor of AnimationParams
         * @param attrs
         * The attributes to change
         * @param timeMS
         * The animation duration, in MS
         * @param easing
         * The easing function
         * @param l
         * The functions to call at each step of the animation
         */
        public AnimationParams(PropertyTransition[] attrs, int timeMS, EaseListener easing, AnimationListener l) {
            this(attrs,timeMS,easing,l, true);
        }
        /**
         * Constructor of AnimationParams
         * @param attrs
         * The attributes to change
         * @param timeMS
         * The animation duration, in MS
         * @param easing
         * The easing function
         * @param l
         * The functions to call at each step of the animation
         * @param queue
         * false to run the animation immediatly, true to wait until all running animations are complete.
         * Default is true
         */
        public AnimationParams(PropertyTransition[] attrs, int timeMS, EaseListener easing, AnimationListener l, boolean queue) {
            transitions = new ArrayList<>(Arrays.asList(attrs));
            this.timeMS = timeMS;
            this.easing = easing;
            callbacks = l;
            this.queue = queue;
        }

        private AnimationHandler handler; // The handler, only used if you want to create the animation at the last moment
        /**
         * Creates an animation to initialize at the last moment
         * @param handler
         * The function called when the animation is about to start
         * That function must return the animation to actually run
         */
        public AnimationParams(AnimationHandler handler) {
            this.handler = handler;
            this.queue = true;
        }

        /**
         * The function to call at each step of the animation
         * @param elt
         * The element concerned by the animation
         * @param t
         * A number between 0 (animation beginning) and 1 (animation complete) representing the progress of the animation
         */
        public void process($Element elt, float t) {
            for (PropertyTransition attr : transitions)
                attr.process(elt, t, easing);
        }

        /**
         * The function to call when the animation is finished
         * @param v
         * The view concerned by the animation
         */
        public void onFinish(View v) {
            if (callbacks != null) {
                if (transitions.size() != 0)
                    callbacks.complete(v);
                callbacks.always(v);
            }
        }

        /**
         * Returns the duration of the animation, in MS
         */
        public int getTime() {
            return timeMS;
        }
        /**
         * Returns the easing function
         */
        public EaseListener getEasing() {
            return easing;
        }
        /**
         * Returns false if the animation runned immediatly, true if it waited until all running animations are complete.
         */
        public boolean isQueue() {
            return queue;
        }

        /**
         * Returns the transitions
         */
        public ArrayList<PropertyTransition> getTransitions() {
            return transitions;
        }

        /**
         * The function to call when the animation starts
         */
        protected void start($Element q) {
            if (callbacks != null)
                callbacks.start(q.head());
        }
        /**
         * The function to call at each step of the animation
         */
        protected void frame($Element q, float t) {
            if (callbacks != null)
                callbacks.step(q.head(), t);
        }
        /**
         * The function to call before initializing the animation
         * if the animation is to be created at the last moment, it will be created here
         */
        protected void create($Element q) {
            if (handler != null) {
                AnimationParams res = handler.create(q.head());
                transitions = res.transitions;
                timeMS = res.timeMS;
                easing = res.easing;
                callbacks = res.callbacks;
                queue = true;
            }
        }
        /**
         * The function to initialize the animation
         * Call this function just before actually starting it (methods start() and end())
         */
        public void initTransitions($Element q) {
            ArrayList<PropertyTransition> attrs = new ArrayList<>(transitions.size());
            for (int i=0;i<transitions.size();i++)
                attrs.add(transitions.get(i).create(q));
            transitions = attrs;
        }
    }

    private static final int DEFAULT_DELAY = 400; // Default time for animations
    private static final String DEFAULT_EASING = EaseListener.SWING; // Default easing function
    private static final HashMap<String,EaseListener> easingFunctions = initEasings(); // The list of easing functions

    /**
     * Initialises all the predefined easing functions
     *
     * Thoses functions were retrieved from http://stackoverflow.com/questions/5207301/jquery-easing-functions-without-using-a-plugin
     */
    private static HashMap<String,EaseListener> initEasings() {
        HashMap<String,EaseListener> res = new HashMap<>();
        res.put(EaseListener.LINEAR, new EaseListener() {
            @Override
            public float ease(float t) {
                return t;
            }
        });
        putAll(res, new String[]{EaseListener.SWING, EaseListener.EASE_OUT_QUAD}, new EaseListener() {
            @Override
            public float ease(float t) {
                return -t * (t - 2);
            }
        });
        res.put(EaseListener.EASE_IN_QUAD, new EaseListener() {
            @Override
            public float ease(float t) {
                return t * t;
            }
        });
        res.put(EaseListener.EASE_IN_OUT_QUAD, new EaseListener() {
            @Override
            public float ease(float t) {
                t *= 2;
                if (t < 1)
                    return t*t/2;
                return -((--t)*(t-2) - 1)/2;
            }
        });
        res.put(EaseListener.EASE_IN_CUBIC, new EaseListener() {
            @Override
            public float ease(float t) {
                return t*t*t;
            }
        });
        res.put(EaseListener.EASE_OUT_CUBIC, new EaseListener() {
            @Override
            public float ease(float t) {
                t--;
                return t*t*t + 1;
            }
        });
        res.put(EaseListener.EASE_IN_OUT_CUBIC, new EaseListener() {
            @Override
            public float ease(float t) {
                t *= 2;
                if (t < 1)
                    return t*t*t/2;
                return ((t-=2)*t*t + 2)/2;
            }
        });
        res.put(EaseListener.EASE_IN_QUART, new EaseListener() {
            @Override
            public float ease(float t) {
                return t*t*t*t;
            }
        });
        res.put(EaseListener.EASE_OUT_QUART, new EaseListener() {
            @Override
            public float ease(float t) {
                t--;
                return 1 - t*t*t*t;
            }
        });
        res.put(EaseListener.EASE_IN_OUT_QUART, new EaseListener() {
            @Override
            public float ease(float t) {
                t *= 2;
                if (t < 1) return t*t*t*t/2;
                return -((t-=2)*t*t*t - 2)/2;
            }
        });
        res.put(EaseListener.EASE_IN_QUINT, new EaseListener() {
            @Override
            public float ease(float t) {
                return t*t*t*t*t;
            }
        });
        res.put(EaseListener.EASE_OUT_QUINT, new EaseListener() {
            @Override
            public float ease(float t) {
                t--;
                return t*t*t*t*t + 1;
            }
        });
        res.put(EaseListener.EASE_IN_OUT_QUINT, new EaseListener() {
            @Override
            public float ease(float t) {
                t *= 2;
                if (t < 1) return t*t*t*t*t/2;
                return ((t-=2)*t*t*t*t + 2)/2;
            }
        });
        res.put(EaseListener.EASE_IN_SINE, new EaseListener() {
            @Override
            public float ease(float t) {
                return -(float)Math.cos(t * (Math.PI/2))+1;
            }
        });
        res.put(EaseListener.EASE_OUT_SINE, new EaseListener() {
            @Override
            public float ease(float t) {
                return (float)Math.sin(t * (Math.PI/2));
            }
        });
        res.put(EaseListener.EASE_IN_OUT_SINE, new EaseListener() {
            @Override
            public float ease(float t) {
                return -(float)(Math.cos(Math.PI*t) - 1)/2;
            }
        });
        res.put(EaseListener.EASE_IN_EXPO, new EaseListener() {
            @Override
            public float ease(float t) {
                return (t==0) ? 0 : (float)Math.pow(2, 10*(t-1));
            }
        });
        res.put(EaseListener.EASE_OUT_EXPO, new EaseListener() {
            @Override
            public float ease(float t) {
                return (t==1) ? 1 : -(float)Math.pow(2, -10*t) + 1;
            }
        });
        res.put(EaseListener.EASE_IN_OUT_EXPO, new EaseListener() {
            @Override
            public float ease(float t) {
                if (t==0) return 0;
                if (t==1) return 1;
                t *= 2;
                if (t < 1) return (float)Math.pow(2, 10 * (t - 1))/2;
                return (-(float)Math.pow(2, -10 * --t) + 2)/2;
            }
        });
        res.put(EaseListener.EASE_IN_CIRC, new EaseListener() {
            @Override
            public float ease(float t) {
                return -((float)Math.sqrt(1 - t*t) - 1);
            }
        });
        res.put(EaseListener.EASE_OUT_CIRC, new EaseListener() {
            @Override
            public float ease(float t) {
                t--;
                return (float)Math.sqrt(1 - t*t);
            }
        });
        res.put(EaseListener.EASE_IN_OUT_CIRC, new EaseListener() {
            @Override
            public float ease(float t) {
                t *= 2;
                if (t < 1) return -((float)Math.sqrt(1 - t*t) - 1)/2;
                return ((float)Math.sqrt(1 - (t-=2)*t) + 1)/2;
            }
        });
        res.put(EaseListener.EASE_IN_ELASTIC, new EaseListener() {
            @Override
            public float ease(float t) {
                if (t==0) return 0;  if (t==1) return 1;
                return -((float)Math.pow(2,10*(t-=1))*(float)Math.sin((t-0.075f)*(2*Math.PI)/0.3f));
            }
        });
        res.put(EaseListener.EASE_OUT_ELASTIC, new EaseListener() {
            @Override
            public float ease(float t) {
                if (t==0) return 0;  if (t==1) return 1;
                return (float)Math.pow(2,-10*t)*(float)Math.sin((t-0.075f)*(2*Math.PI)/0.3f)+1;
            }
        });
        res.put(EaseListener.EASE_IN_OUT_ELASTIC, new EaseListener() {
            @Override
            public float ease(float t) {
                if (t==0) return 0;  if (t==1) return 1;
                t *= 2;
                if (t < 1) return -((float)Math.pow(2,10*(t-=1)) * (float)Math.sin((t-0.075f)*(2*Math.PI)/0.3f))/2;
                return (float)Math.pow(2,-10*(t-=1)) * (float)Math.sin( (t-0.075f)*(2*Math.PI)/0.3f)/2 + 1;
            }
        });
        res.put(EaseListener.EASE_IN_BACK, new EaseListener() {
            @Override
            public float ease(float t) {
                float s = 1.70158f;
                return t*t*((s+1)*t - s);
            }
        });
        res.put(EaseListener.EASE_OUT_BACK, new EaseListener() {
            @Override
            public float ease(float t) {
                float s = 1.70158f;
                t--;
                return (t*t*((s+1)*t + s) + 1);
            }
        });
        res.put(EaseListener.EASE_IN_OUT_BACK, new EaseListener() {
            @Override
            public float ease(float t) {
                float s = 1.70158f;
                t *= 2;
                if (t < 1) return (t*t*(((s*=(1.525))+1)*t - s))/2;
                return ((t-=2)*t*(((s*=(1.525))+1)*t + s) + 2)/2;
            }
        });
        res.put(EaseListener.EASE_IN_BOUNCE, new EaseListener() {
            @Override
            public float ease(float t) {
                return 1-easingFunctions.get(EaseListener.EASE_OUT_BOUNCE).ease(1-t);
            }
        });
        res.put(EaseListener.EASE_OUT_BOUNCE, new EaseListener() {
            @Override
            public float ease(float t) {
                if (t < (1f/2.75f))
                    return 7.5625f*t*t;
                else if (t < (2f/2.75f))
                    return 7.5625f*(t-=(1.5f/2.75f))*t + .75f;
                else if (t < (2.5f/2.75f))
                    return 7.5625f*(t-=(2.25f/2.75f))*t + .9375f;
                else
                    return 7.5625f*(t-=(2.625f/2.75f))*t + .984375f;
            }
        });
        res.put(EaseListener.EASE_IN_OUT_BOUNCE, new EaseListener() {
            @Override
            public float ease(float t) {
                t *= 2;
                if (t < 1) return easingFunctions.get(EaseListener.EASE_IN_BOUNCE).ease(t)/2;
                return easingFunctions.get(EaseListener.EASE_OUT_BOUNCE).ease(t-1)/2 + 0.5f;
            }
        });
        return res;
    }

    /**
     * Converts an animation with only start function implemented to an AnimationListener object
     */
    private static AnimationListener toAnimListener(final StartListener start) {
        if (start != null)
            return new AnimationListener() {
                @Override
                public void start(View v) {
                    start.start(v);
                }
                @Override
                public void step(View v, float t) {
                }
                @Override
                public void complete(View v) {
                }
                @Override
                public void always(View v) {
                }
            };
        return null;
    }
    /**
     * Converts an animation with only end function implemented to an AnimationListener object
     */
    private static AnimationListener toAnimListener(final CompleteListener complete) {
        if (complete != null)
            return new AnimationListener() {
                @Override
                public void start(View v) {
                }
                @Override
                public void step(View v, float t) {
                }
                @Override
                public void complete(View v) {
                    complete.complete(v);
                }
                @Override
                public void always(View v) {
                }
            };
        return null;
    }

    /**
     * Creates an array with a single element
     */
    private static PropertyTransition[] singleton(PropertyTransition elt) {
        return new PropertyTransition[]{elt};
    }

    /**
     * Returns the easing function that has the perticullar name
     * Throws an exception if the function doesn't exist
     */
    private EaseListener toEasing(String name) {
        EaseListener res = easingFunctions.get(name);
        if (res == null)
            throw new IllegalArgumentException("Unknown easing function \""+ name +"\"");
        return res;
    }

    /**
     * Checks if the first element in the set of elements is animating
     * @return
     * true if it's animating, false otherwise
     */
    public boolean animating() {
        return $Element.isAnimating(head());
    }

    /**
     * Set a timer to delay execution of subsequent animation in the queue
     */
    public AQuery delay() {
        return delay(DEFAULT_DELAY);
    }
    /**
     * Set a timer to delay execution of subsequent animation in the queue
     * @param timeMS
     * The amount of time to wait, in MS
     */
    public AQuery delay(int timeMS) {
        return animate(new PropertyTransition[0], timeMS);
    }

    /**
     * Performs a custom animation of an XML property
     * @param attr
     * The XML property to change
     */
    public AQuery animate(PropertyTransition attr) {
        return animate(singleton(attr));
    }
    public AQuery animate(PropertyTransition[] attrs) {
        return animate(attrs, DEFAULT_DELAY);
    }
    /**
     * Performs a custom animation of an XML property
     * @param attr
     * The XML property to change
     */
    public AQuery animate(PropertyTransition attr, boolean queue) {
        return animate(singleton(attr), queue);
    }
    public AQuery animate(PropertyTransition[] attrs, boolean queue) {
        return animate(attrs, DEFAULT_DELAY, queue);
    }
    /**
     * Performs a custom animation of an XML property
     * @param attr
     * The XML property to change
     */
    public AQuery animate(PropertyTransition attr, int timeMS) {
        return animate(singleton(attr), timeMS);
    }
    public AQuery animate(PropertyTransition[] attrs, int timeMS) {
        return animate(attrs, timeMS, (AnimationListener) null);
    }
    /**
     * Performs a custom animation of an XML property
     * @param attr
     * The XML property to change
     */
    public AQuery animate(PropertyTransition attr, int timeMS, boolean queue) {
        return animate(singleton(attr), timeMS, queue);
    }
    public AQuery animate(PropertyTransition[] attrs, int timeMS, boolean queue) {
        return animate(attrs, timeMS, (AnimationListener) null, queue);
    }
    /**
     * Performs a custom animation of an XML property
     * @param attr
     * The XML property to change
     */
    public AQuery animate(PropertyTransition attr, int timeMS, String easing) {
        return animate(singleton(attr), timeMS, easing);
    }
    public AQuery animate(PropertyTransition[] attrs, int timeMS, String easing) {
        return animate(attrs, timeMS, easing, null);
    }
    /**
     * Performs a custom animation of an XML property
     * @param attr
     * The XML property to change
     */
    public AQuery animate(PropertyTransition attr, int timeMS, String easing, boolean queue) {
        return animate(singleton(attr), timeMS, easing, queue);
    }
    public AQuery animate(PropertyTransition[] attrs, int timeMS, String easing, boolean queue) {
        return animate(attrs, timeMS, easing, null, queue);
    }
    /**
     * Performs a custom animation of an XML property
     * @param attr
     * The XML property to change
     * @param timeMS
     * The animation duration, in MS
     * @param start
     * The function to call when the animation starts
     */
    public AQuery animate(PropertyTransition attr, int timeMS, StartListener start) {
        return animate(singleton(attr), timeMS, start);
    }
    /**
     * Performs a custom animation of an XML property
     * @param attr
     * The XML property to change
     * @param timeMS
     * The animation duration, in MS
     * @param complete
     * The function to call when the animation is complete
     */
    public AQuery animate(PropertyTransition attr, int timeMS, CompleteListener complete) {
        return animate(singleton(attr), timeMS, complete);
    }
    /**
     * Performs a custom animation of a set of XML properties
     * @param attrs
     * The XML properties to change
     * @param timeMS
     * The animation duration, in MS
     * @param start
     * The function to call when the animation starts
     */
    public AQuery animate(PropertyTransition[] attrs, int timeMS, StartListener start) {
        return animate(attrs, timeMS, toAnimListener(start));
    }
    /**
     * Performs a custom animation of a set of XML properties
     * @param attrs
     * The XML properties to change
     * @param timeMS
     * The animation duration, in MS
     * @param complete
     * The function to call when the animation is complete
     */
    public AQuery animate(PropertyTransition[] attrs, int timeMS, CompleteListener complete) {
        return animate(attrs, timeMS, toAnimListener(complete));
    }
    /**
     * Performs a custom animation of an XML property
     * @param attr
     * The XML property to change
     * @param timeMS
     * The animation duration, in MS
     * @param start
     * The function to call when the animation starts
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery animate(PropertyTransition attr, int timeMS, StartListener start, boolean queue) {
        return animate(singleton(attr), timeMS, start, queue);
    }
    /**
     * Performs a custom animation of an XML property
     * @param attr
     * The XML property to change
     * @param timeMS
     * The animation duration, in MS
     * @param complete
     * The function to call when the animation is complete
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery animate(PropertyTransition attr, int timeMS, CompleteListener complete, boolean queue) {
        return animate(singleton(attr), timeMS, complete, queue);
    }
    /**
     * Performs a custom animation of a set of XML properties
     * @param attrs
     * The XML properties to change
     * @param timeMS
     * The animation duration, in MS
     * @param start
     * The function to call when the animation starts
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery animate(PropertyTransition[] attrs, int timeMS, StartListener start, boolean queue) {
        return animate(attrs, timeMS, toAnimListener(start), queue);
    }
    /**
     * Performs a custom animation of a set of XML properties
     * @param attrs
     * The XML properties to change
     * @param timeMS
     * The animation duration, in MS
     * @param complete
     * The function to call when the animation is complete
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery animate(PropertyTransition[] attrs, int timeMS, CompleteListener complete, boolean queue) {
        return animate(attrs, timeMS, toAnimListener(complete), queue);
    }
    /**
     * Performs a custom animation of an XML property
     * @param attr
     * The XML property to change
     * @param timeMS
     * The animation duration, in MS
     * @param callback
     * The functions to call at each step of the animation
     */
    public AQuery animate(PropertyTransition attr, int timeMS, AnimationListener callback) {
        return animate(singleton(attr), timeMS, callback);
    }
    /**
     * Performs a custom animation of a set of XML properties
     * @param attrs
     * The XML properties to change
     * @param timeMS
     * The animation duration, in MS
     * @param callback
     * The functions to call at each step of the animation
     */
    public AQuery animate(PropertyTransition[] attrs, int timeMS, AnimationListener callback) {
        return animate(attrs,timeMS,DEFAULT_EASING,callback);
    }
    /**
     * Performs a custom animation of an XML property
     * @param attr
     * The XML property to change
     * @param timeMS
     * The animation duration, in MS
     * @param callback
     * The functions to call at each step of the animation
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery animate(PropertyTransition attr, int timeMS, AnimationListener callback, boolean queue) {
        return animate(singleton(attr), timeMS, callback, queue);
    }
    /**
     * Performs a custom animation of a set of XML properties
     * @param attrs
     * The XML properties to change
     * @param timeMS
     * The animation duration, in MS
     * @param callback
     * The functions to call at each step of the animation
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery animate(PropertyTransition[] attrs, int timeMS, AnimationListener callback, boolean queue) {
        return animate(attrs,timeMS,DEFAULT_EASING,callback, queue);
    }
    /**
     * Performs a custom animation of an XML property
     * @param attr
     * The XML property to change
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param callback
     * The functions to call at each step of the animation
     */
    public AQuery animate(PropertyTransition attr, int timeMS, String easing, AnimationListener callback) {
        return animate(singleton(attr), timeMS, easing, callback);
    }
    /**
     * Performs a custom animation of a set of XML properties
     * @param attrs
     * The XML properties to change
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param callback
     * The functions to call at each step of the animation
     */
    public AQuery animate(PropertyTransition[] attrs, int timeMS, String easing, AnimationListener callback) {
        return animate(attrs,timeMS,toEasing(easing),callback);
    }
    /**
     * Performs a custom animation of an XML property
     * @param attr
     * The XML property to change
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param callback
     * The functions to call at each step of the animation
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery animate(PropertyTransition attr, int timeMS, String easing, AnimationListener callback, boolean queue) {
        return animate(singleton(attr), timeMS, easing, callback, queue);
    }
    /**
     * Performs a custom animation of a set of XML properties
     * @param attrs
     * The XML properties to change
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param callback
     * The functions to call at each step of the animation
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery animate(PropertyTransition[] attrs, int timeMS, String easing, AnimationListener callback, boolean queue) {
        return animate(attrs,timeMS,toEasing(easing),callback, queue);
    }
    /**
     * Performs a custom animation of an XML property
     * @param attr
     * The XML property to change
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param callback
     * The functions to call at each step of the animation
     */
    public AQuery animate(PropertyTransition attr, int timeMS, EaseListener easing, AnimationListener callback) {
        return animate(singleton(attr), timeMS, easing, callback);
    }
    /**
     * Performs a custom animation of a set of XML properties
     * @param attrs
     * The XML properties to change
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param callback
     * The functions to call at each step of the animation
     */
    public AQuery animate(PropertyTransition[] attrs, int timeMS, EaseListener easing, AnimationListener callback) {
        return animate(attrs, timeMS, easing, callback, true);
    }
    /**
     * Performs a custom animation of an XML property
     * @param attr
     * The XML property to change
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param callback
     * The functions to call at each step of the animation
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery animate(PropertyTransition attr, int timeMS, EaseListener easing, AnimationListener callback, boolean queue) {
        return animate(singleton(attr), timeMS, easing, callback, queue);
    }

    /**
     * Performs a custom animation of a set of XML properties
     * @param attrs
     * The XML properties to change
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param callback
     * The functions to call at each step of the animation
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery animate(PropertyTransition[] attrs, int timeMS, EaseListener easing, AnimationListener callback, boolean queue) {
        for (View v : list())
            $Element.animateView(ctx, v, new AnimationParams(attrs, timeMS, easing, callback, queue));
        return this;
    }

    /**
     * Stops the current animation
     */
    public AQuery stop() {
        return stop(false);
    }
    /**
     * Stops the current animations
     * @param clearQueue
     * true to stop the next animations as well. Default is false
     */
    public AQuery stop(boolean clearQueue) {
        return stop(clearQueue,false);
    }
    /**
     * Stops the current animations
     * @param clearQueue
     * true to stop the next animations as well. Default is false
     * @param jumpToEnd
     * true to complete the current animations immediately. Default is false
     */
    public AQuery stop(boolean clearQueue, boolean jumpToEnd) {
        for (View v : list())
            $Element.stopAnimations(v, clearQueue, jumpToEnd);
        return this;
    }

    /**
     * Shows the elements progressively
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery show(boolean queue) {
        return show(DEFAULT_DELAY, queue);
    }
    /**
     * Shows the elements progressively
     * @param timeMS
     * The animation duration, in MS
     */
    public AQuery show(int timeMS) {
        return show(timeMS, (AnimationListener) null);
    }
    /**
     * Shows the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery show(int timeMS, boolean queue) {
        return show(timeMS, (AnimationListener) null, queue);
    }
    /**
     * Shows the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     */
    public AQuery show(int timeMS, String easing) {
        return show(timeMS, easing, null);
    }
    /**
     * Shows the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery show(int timeMS, String easing, boolean queue) {
        return show(timeMS, easing, null, queue);
    }
    /**
     * Shows the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param start
     * The function to call when the animation starts
     */
    public AQuery show(int timeMS, StartListener start) {
        return show(timeMS, toAnimListener(start));
    }
    /**
     * Shows the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param complete
     * The function to call when the animation is complete
     */
    public AQuery show(int timeMS, CompleteListener complete) {
        return show(timeMS, toAnimListener(complete));
    }
    /**
     * Shows the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param start
     * The function to call when the animation starts
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery show(int timeMS, StartListener start, boolean queue) {
        return show(timeMS, toAnimListener(start), queue);
    }
    /**
     * Shows the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param complete
     * The function to call when the animation is complete
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery show(int timeMS, CompleteListener complete, boolean queue) {
        return show(timeMS, toAnimListener(complete), queue);
    }
    /**
     * Shows the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param callback
     * The functions to call at each step of the animation
     */
    public AQuery show(int timeMS, AnimationListener callback) {
        return show(timeMS,DEFAULT_EASING,callback);
    }
    /**
     * Shows the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param callback
     * The functions to call at each step of the animation
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery show(int timeMS, AnimationListener callback, boolean queue) {
        return show(timeMS,DEFAULT_EASING,callback, queue);
    }
    /**
     * Shows the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param callback
     * The functions to call at each step of the animation
     */
    public AQuery show(int timeMS, String easing, AnimationListener callback) {
        return show(timeMS,toEasing(easing),callback);
    }
    public AQuery show(int timeMS, String easing, AnimationListener callback, boolean queue) {
        return show(timeMS,toEasing(easing),callback, queue);
    }
    /**
     * Shows the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param callback
     * The functions to call at each step of the animation
     */
    public AQuery show(int timeMS, EaseListener easing, AnimationListener callback) {
        return show(timeMS, easing, callback, true);
    }
    /**
     * Shows the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param callback
     * The functions to call at each step of the animation
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery show(final int timeMS, final EaseListener easing, final AnimationListener callback, boolean queue) {
        for (View v : list()) {
            if (queue) {
                $Element.postAnimate(ctx, v, new AnimationHandler() {
                    @Override
                    public AnimationParams create(View v) {
                        return showProgressively(v, timeMS, easing, callback, true);
                    }
                });
            }
            else
                $Element.animateView(ctx, v, showProgressively(v, timeMS, easing, callback, false));
        }
        return this;
    }

    /**
     * Returns the animation step that make the view appearing progressively
     */
    private AnimationParams showProgressively(View v, int timeMS, EaseListener easing, AnimationListener callback, boolean queue) {
        final $Element jElt = new $Element(ctx, v);
        int lWidth = jElt.propi("layout_width"), lHeight = jElt.propi("layout_height");
        float alpha = jElt.propf("alpha");
        jElt.prop("layout_width", 0);
        jElt.prop("layout_height", 0);
        jElt.prop("alpha", 0);
        show(v);
        return new AnimationParams(new PropertyTransition[]{
                Transition.prop("layout_width", lWidth),
                Transition.prop("layout_height", lHeight),
                Transition.prop("alpha", alpha)
        }, timeMS, easing, callback, queue);
    }

    /**
     * Hides the elements progressively
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery hide(boolean queue) {
        return hide(DEFAULT_DELAY, queue);
    }
    /**
     * Hides the elements progressively
     * @param timeMS
     * The animation duration, in MS
     */
    public AQuery hide(int timeMS) {
        return hide(timeMS, (AnimationListener) null);
    }
    /**
     * Hides the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery hide(int timeMS, boolean queue) {
        return hide(timeMS, (AnimationListener) null, queue);
    }
    /**
     * Hides the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     */
    public AQuery hide(int timeMS, String easing) {
        return hide(timeMS, easing, null);
    }
    /**
     * Hides the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery hide(int timeMS, String easing, boolean queue) {
        return hide(timeMS, easing, null, queue);
    }
    /**
     * Hides the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param start
     * The function to call when the animation starts
     */
    public AQuery hide(int timeMS, StartListener start) {
        return hide(timeMS, toAnimListener(start));
    }
    /**
     * Hides the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param complete
     * The function to call when the animation is complete
     */
    public AQuery hide(int timeMS, CompleteListener complete) {
        return hide(timeMS, toAnimListener(complete));
    }
    /**
     * Hides the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param start
     * The function to call when the animation starts
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery hide(int timeMS, StartListener start, boolean queue) {
        return hide(timeMS, toAnimListener(start), queue);
    }
    /**
     * Hides the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param complete
     * The function to call when the animation is complete
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery hide(int timeMS, CompleteListener complete, boolean queue) {
        return hide(timeMS, toAnimListener(complete), queue);
    }
    /**
     * Hides the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param callback
     * The functions to call at each step of the animation
     */
    public AQuery hide(int timeMS, AnimationListener callback) {
        return hide(timeMS,DEFAULT_EASING,callback);
    }
    /**
     * Hides the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param callback
     * The functions to call at each step of the animation
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery hide(int timeMS, AnimationListener callback, boolean queue) {
        return hide(timeMS,DEFAULT_EASING,callback, queue);
    }
    /**
     * Hides the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param callback
     * The functions to call at each step of the animation
     */
    public AQuery hide(int timeMS, String easing, AnimationListener callback) {
        return hide(timeMS,toEasing(easing),callback);
    }
    /**
     * Hides the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param callback
     * The functions to call at each step of the animation
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery hide(int timeMS, String easing, AnimationListener callback, boolean queue) {
        return hide(timeMS,toEasing(easing),callback, queue);
    }
    /**
     * Hides the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param callback
     * The functions to call at each step of the animation
     */
    public AQuery hide(int timeMS, EaseListener easing, AnimationListener callback) {
        return hide(timeMS, easing, callback, true);
    }
    /**
     * Hides the elements progressively
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param callback
     * The functions to call at each step of the animation
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery hide(final int timeMS, final EaseListener easing, final AnimationListener callback, boolean queue) {
        for (View v : list()) {
            if (queue) {
                $Element.postAnimate(ctx, v, new AnimationHandler() {
                    @Override
                    public AnimationParams create(View v) {
                        return hideProgressively(v, timeMS, easing, callback, true);
                    }
                });
            }
            else
                $Element.animateView(ctx, v, hideProgressively(v, timeMS, easing, callback, false));
        }
        return this;
    }

    /**
     * Returns the animation step that make the view disappearing progressively
     */
    private AnimationParams hideProgressively(View v, int timeMS, EaseListener easing, final AnimationListener callback, boolean queue) {
        $Element jElt = new $Element(ctx, v);
        final int lWidth = jElt.propi("layout_width"), iHeight = jElt.propi("layout_height");
        final float alpha = jElt.propf("alpha");
        return new AnimationParams(new PropertyTransition[]{
                Transition.prop("layout_width", 0),
                Transition.prop("layout_height", 0),
                Transition.prop("alpha", 0)
        }, timeMS, easing, new AnimationListener() {
            @Override
            public void start(View v) {
                if (callback != null)
                    callback.start(v);
            }

            @Override
            public void step(View v, float t) {
                if (callback != null)
                    callback.step(v, t);
            }

            @Override
            public void complete(View v) {
                hide(v);
                $Element jElt = new $Element(ctx, v);
                jElt.prop("layout_width", lWidth);
                jElt.prop("layout_height", iHeight);
                jElt.prop("alpha", alpha);
                if (callback != null)
                    callback.complete(v);
            }

            @Override
            public void always(View v) {
                if (callback != null)
                    callback.always(v);
            }
        }, queue);
    }

    /**
     * Hides progressively the visible elements, and shows progressively the others
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery toggle(boolean queue) {
        return toggle(DEFAULT_DELAY, queue);
    }
    /**
     * Hides progressively the visible elements, and shows progressively the others
     * @param timeMS
     * The animation duration, in MS
     */
    public AQuery toggle(int timeMS) {
        return toggle(timeMS, (AnimationListener) null);
    }
    /**
     * Hides progressively the visible elements, and shows progressively the others
     * @param timeMS
     * The animation duration, in MS
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery toggle(int timeMS, boolean queue) {
        return toggle(timeMS, (AnimationListener) null, queue);
    }
    /**
     * Hides progressively the visible elements, and shows progressively the others
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     */
    public AQuery toggle(int timeMS, String easing) {
        return toggle(timeMS, easing, null);
    }
    /**
     * Hides progressively the visible elements, and shows progressively the others
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery toggle(int timeMS, String easing, boolean queue) {
        return toggle(timeMS, easing, null, queue);
    }
    /**
     * Hides progressively the visible elements, and shows progressively the others
     * @param timeMS
     * The animation duration, in MS
     * @param complete
     * The function to call when the animation is complete
     */
    public AQuery toggle(int timeMS, CompleteListener complete) {
        return toggle(timeMS, toAnimListener(complete));
    }
    /**
     * Hides progressively the visible elements, and shows progressively the others
     * @param timeMS
     * The animation duration, in MS
     * @param complete
     * The function to call when the animation is complete
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery toggle(int timeMS, CompleteListener complete, boolean queue) {
        return toggle(timeMS, toAnimListener(complete), queue);
    }
    /**
     * Hides progressively the visible elements, and shows progressively the others
     * @param timeMS
     * The animation duration, in MS
     * @param callback
     * The functions to call at each step of the animation
     */
    public AQuery toggle(int timeMS, AnimationListener callback) {
        return toggle(timeMS,DEFAULT_EASING,callback);
    }
    /**
     * Hides progressively the visible elements, and shows progressively the others
     * @param timeMS
     * The animation duration, in MS
     * @param callback
     * The functions to call at each step of the animation
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery toggle(int timeMS, AnimationListener callback, boolean queue) {
        return toggle(timeMS,DEFAULT_EASING,callback, queue);
    }
    /**
     * Hides progressively the visible elements, and shows progressively the others
     */
    public AQuery toggle(int timeMS, String easing, AnimationListener callback) {
        return toggle(timeMS,toEasing(easing),callback);
    }
    /**
     * Hides progressively the visible elements, and shows progressively the others
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param callback
     * The functions to call at each step of the animation
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery toggle(int timeMS, String easing, AnimationListener callback, boolean queue) {
        return toggle(timeMS, toEasing(easing), callback, queue);
    }
    /**
     * Hides progressively the visible elements, and shows progressively the others
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param callback
     * The functions to call at each step of the animation
     */
    public AQuery toggle(int timeMS, EaseListener easing, AnimationListener callback) {
        return toggle(timeMS, easing, callback, true);
    }
    /**
     * Hides progressively the visible elements, and shows progressively the others
     * @param timeMS
     * The animation duration, in MS
     * @param easing
     * The easing function
     * @param callback
     * The functions to call at each step of the animation
     * @param queue
     * false to run the animation immediatly, true to wait until all running animations are complete.
     * Default is true
     */
    public AQuery toggle(final int timeMS, final EaseListener easing, final AnimationListener callback, boolean queue) {
        for (View v : list()) {
            if (queue) {
                $Element.postAnimate(ctx, v, new AnimationHandler() {
                    @Override
                    public AnimationParams create(View v) {
                        return toggleProgressively(v, timeMS, easing, callback, true);
                    }
                });
            }
            else
                $Element.animateView(ctx, v, toggleProgressively(v, timeMS, easing, callback, false));
        }
        return this;
    }
    /**
     * Returns the animation step that make the view appearing/disappearing progressively
     */
    private AnimationParams toggleProgressively(View v, int timeMS, EaseListener easing, final AnimationListener callback, boolean queue) {
        if (visible(v))
            return hideProgressively(v, timeMS, easing, callback, queue);
        else
            return showProgressively(v, timeMS, easing, callback, queue);
    }

    /*
     * All the format functions
     * Thos functions make the link between an XML value and their corresponding attribute
     * For example, the function formatString can convert "@string/my_resource_string" to its corresponding value
     */

    private int formatId(String text) {
        try {
            return Integer.valueOf(text);
        }
        catch (NumberFormatException e) {
            try {
                return getIdentifier("id", text);
            }
            catch (Resources.NotFoundException e2) {
                throw new IllegalArgumentException("Unable to find ID \""+ text +"\"");
            }
        }
    }
    private String formatString(String text) {
        try {
            return ctx.getResources().getString(getIdentifier("string", text));
        }
        catch (Resources.NotFoundException e) {
            return text;
        }
    }
    private String[] formatStringArray(String text) {
        try {
            return ctx.getResources().getStringArray(getIdentifier("array", text));
        }
        catch (Resources.NotFoundException e) {
            throw e;
        }
    }
    private int[] formatIntArray(String text) {
        String[] sNumbers = formatString(text).split(" *, *");
        int[] res = new int[sNumbers.length];
        for (int i=0;i<sNumbers.length;i++)
            res[i] = Integer.valueOf(sNumbers[i]);
        return res;
    }
    private boolean formatBool(String text) {
        if ("true".equals(text))
            return true;
        else if ("false".equals(text))
            return false;
        else
            return ctx.getResources().getBoolean(getIdentifier("bool", text));
    }
    private int formatInt(String text) {
        try {
            return Integer.valueOf(text);
        }
        catch (NumberFormatException e) {
            try {
                return ctx.getResources().getInteger(getIdentifier("integer", text));
            }
            catch (Resources.NotFoundException e2) {
                throw e;
            }
        }
    }
    private long formatLong(String text) {
        try {
            return Long.valueOf(text);
        }
        catch (NumberFormatException e) {
            try {
                return formatInt(text);
            }
            catch (NumberFormatException e2) {
                throw e;
            }
        }
    }
    private int formatSize(String text) {
        if ("match_parent".equals(text) || "fill_parent".equals(text))
            return ViewGroup.LayoutParams.MATCH_PARENT;
        else if ("wrap_content".equals(text))
            return ViewGroup.LayoutParams.WRAP_CONTENT;
        return Math.round(formatDimen(text));
    }
    @SuppressLint("SimpleDateFormat")
    private static long formatDate(String text) {
        try {
            return new SimpleDateFormat("MM/dd/yyyy").parse(text).getTime();
        }
        catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private static int formatGravity(String text) {
        String[] args = text.split("\\|");
        int res = 0;
        for (String arg : args) {
            if ("left".equals(arg))
                res |= Gravity.LEFT;
            else if ("center".equals(arg))
                res |= Gravity.CENTER;
            else if ("right".equals(arg))
                res |= Gravity.RIGHT;
            else if ("top".equals(arg))
                res |= Gravity.TOP;
            else if ("bottom".equals(arg))
                res |= Gravity.BOTTOM;
            else if ("center_vertical".equals(arg))
                res |= Gravity.CENTER_VERTICAL;
            else if ("center_horizontal".equals(arg))
                res |= Gravity.CENTER_HORIZONTAL;
            else if ("start".equals(arg))
                res |= Gravity.START;
            else if ("end".equals(arg))
                res |= Gravity.END;
            else if ("fill".equals(arg))
                res |= Gravity.FILL;
            else if ("fill_vertical".equals(arg))
                res |= Gravity.FILL_VERTICAL;
            else if ("fill_horizontal".equals(arg))
                res |= Gravity.FILL_HORIZONTAL;
            else
                throw new IllegalArgumentException("Unknown gravity \""+ arg +"\"");
        }
        return res;
    }
    private static ImageView.ScaleType formatScale(String text) {
        if ("matrix".equals(text))
            return ImageView.ScaleType.MATRIX;
        if ("fitXY".equals(text))
            return ImageView.ScaleType.FIT_XY;
        if ("fitStart".equals(text))
            return ImageView.ScaleType.FIT_START;
        if ("fitCenter".equals(text))
            return ImageView.ScaleType.FIT_CENTER;
        if ("fitEnd".equals(text))
            return ImageView.ScaleType.FIT_END;
        if ("center".equals(text))
            return ImageView.ScaleType.CENTER;
        if ("centerCrop".equals(text))
            return ImageView.ScaleType.CENTER_CROP;
        if ("centerInside".equals(text))
            return ImageView.ScaleType.CENTER_INSIDE;
        throw new IllegalArgumentException("Unknown scale type \""+ text +"\"");
    }
    private static int formatOrientation(String text) {
        if ("vertical".equals(text))
            return LinearLayout.VERTICAL;
        if ("horizontal".equals(text))
            return LinearLayout.HORIZONTAL;
        throw new IllegalArgumentException("Unknown orientation \""+ text +"\"");
    }
    private static PorterDuff.Mode formatMode(String text) {
        if ("add".equals(text)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                return PorterDuff.Mode.ADD;
        }
        else if ("multiply".equals(text))
            return PorterDuff.Mode.MULTIPLY;
        else if ("screen".equals(text))
            return PorterDuff.Mode.SCREEN;
        else if ("src_atop".equals(text))
            return PorterDuff.Mode.SRC_ATOP;
        else if ("src_in".equals(text))
            return PorterDuff.Mode.SRC_IN;
        else if ("src_over".equals(text))
            return PorterDuff.Mode.SRC_OVER;
        throw new IllegalArgumentException("Unknown tint mode \""+ text +"\"");
    }
    private static int formatFocusability(String text) {
        if ("afterDescendants".equals(text))
            return ViewGroup.FOCUS_AFTER_DESCENDANTS;
        if ("beforeDescendants".equals(text))
            return ViewGroup.FOCUS_BEFORE_DESCENDANTS;
        if ("blocksDescendants".equals(text))
            return ViewGroup.FOCUS_BLOCK_DESCENDANTS;
        throw new IllegalArgumentException("Unknown focusability \""+ text +"\"");
    }
    private static int formatQuality(String text) {
        if ("auto".equals(text))
            return View.DRAWING_CACHE_QUALITY_AUTO;
        if ("high".equals(text))
            return ViewGroup.DRAWING_CACHE_QUALITY_HIGH;
        if ("low".equals(text))
            return ViewGroup.DRAWING_CACHE_QUALITY_LOW;
        throw new IllegalArgumentException("Unknown quality \""+ text +"\"");
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static int formatLayer(String text) {
        if ("hardware".equals(text))
            return View.LAYER_TYPE_HARDWARE;
        if ("software".equals(text))
            return View.LAYER_TYPE_SOFTWARE;
        if ("none".equals(text))
            return View.LAYER_TYPE_NONE;
        throw new IllegalArgumentException("Unknown layer type \""+ text +"\"");
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static int formatLayout(String text) {
        if ("clipBounds".equals(text))
            return ViewGroup.LAYOUT_MODE_CLIP_BOUNDS;
        if ("opticalBounds".equals(text))
            return ViewGroup.LAYOUT_MODE_OPTICAL_BOUNDS;
        throw new IllegalArgumentException("Unknown layer mode \""+ text +"\"");
    }
    private static int formatStretch(String text) {
        if ("none".equals(text))
            return GridView.NO_STRETCH;
        if ("columnWidth".equals(text))
            return GridView.STRETCH_COLUMN_WIDTH;
        if ("spacingWidth".equals(text))
            return GridView.STRETCH_SPACING;
        if ("spacingWidthUniform".equals(text))
            return GridView.STRETCH_SPACING_UNIFORM;
        throw new IllegalArgumentException("Unknown stretch mode \""+ text +"\"");
    }
    private static int formatStroke(String text) {
        if ("single".equals(text))
            return GestureOverlayView.GESTURE_STROKE_TYPE_SINGLE;
        if ("multiple".equals(text))
            return GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE;
        throw new IllegalArgumentException("Unknown stroke type\""+ text +"\"");
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static int formatDividers(String text) {
        int res = 0;
        String[] args = text.split("\\|");
        for (String arg : args) {
            if ("beginning".equals(arg))
                res |= LinearLayout.SHOW_DIVIDER_BEGINNING;
            else if ("middle".equals(arg))
                res |= LinearLayout.SHOW_DIVIDER_MIDDLE;
            else if ("end".equals(arg))
                res |= LinearLayout.SHOW_DIVIDER_END;
            else if ("none".equals(arg))
                res |= LinearLayout.SHOW_DIVIDER_NONE;
            else
                throw new IllegalArgumentException("Unknown divider \""+ arg +"\"");
        }
        return res;
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static ViewOutlineProvider formatProvider(String text) {
        if ("background".equals(text))
            return ViewOutlineProvider.BACKGROUND;
        if ("bounds".equals(text))
            return ViewOutlineProvider.BOUNDS;
        if ("paddedBounds".equals(text))
            return ViewOutlineProvider.PADDED_BOUNDS;
        if ("none".equals(text))
            return null;
        throw new IllegalArgumentException("Unknown outline provider \""+ text +"\"");
    }
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static int formatScroll(String text) {
        if ("always".equals(text))
            return View.OVER_SCROLL_ALWAYS;
        if ("ifContentScrolls".equals(text))
            return View.OVER_SCROLL_IF_CONTENT_SCROLLS;
        if ("never".equals(text))
            return View.OVER_SCROLL_NEVER;
        throw new IllegalArgumentException("Unknown overScroll mode \""+ text +"\"");
    }
    private static int formatIndicator(String text) {
        String[] args = text.split("\\|");
        int res = 0;
        for (String arg : args) {
            if (!"none".equals(text)) {
                if ("bottom".equals(text))
                    res |= View.SCROLL_INDICATOR_BOTTOM;
                else if ("end".equals(text))
                    res |= View.SCROLL_INDICATOR_END;
                else if ("left".equals(text))
                    res |= View.SCROLL_INDICATOR_LEFT;
                else if ("right".equals(text))
                    res |= View.SCROLL_INDICATOR_RIGHT;
                else if ("start".equals(text))
                    res |= View.SCROLL_INDICATOR_START;
                else if ("top".equals(text))
                    res |= View.SCROLL_INDICATOR_TOP;
                else
                    throw new IllegalArgumentException("Unknown scroll indicator \""+ text +"\"");
            }
        }
        return res;
    }
    private static int formatChoice(String text) {
        if ("none".equals(text))
            return AbsListView.CHOICE_MODE_NONE;
        if ("singleChoice".equals(text))
            return AbsListView.CHOICE_MODE_SINGLE;
        if ("multipleChoice".equals(text))
            return AbsListView.CHOICE_MODE_MULTIPLE;
        if ("multipleChoiceModal".equals(text))
            return AbsListView.CHOICE_MODE_MULTIPLE_MODAL;
        throw new IllegalArgumentException("Unknown choice mode \""+ text +"\"");
    }
    private static int formatTranscript(String text) {
        if ("normal".equals(text))
            return AbsListView.TRANSCRIPT_MODE_NORMAL;
        if ("disabled".equals(text))
            return AbsListView.TRANSCRIPT_MODE_DISABLED;
        if ("alwaysScroll".equals(text))
            return AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL;
        throw new IllegalArgumentException("Unknown transcript mode \""+ text +"\"");
    }
    private static int formatCache(String text) {
        if ("all".equals(text))
            return ViewGroup.PERSISTENT_ALL_CACHES;
        if ("animation".equals(text))
            return ViewGroup.PERSISTENT_ANIMATION_CACHE;
        if ("scrolling".equals(text))
            return ViewGroup.PERSISTENT_SCROLLING_CACHE;
        if ("none".equals(text))
            return ViewGroup.PERSISTENT_NO_CACHE;
        throw new IllegalArgumentException("Unknown drawing cache \""+ text +"\"");
    }
    private static int formatStyle(String text) {
        if ("insideInset".equals(text))
            return View.SCROLLBARS_INSIDE_INSET;
        if ("insideOverlay".equals(text))
            return View.SCROLLBARS_INSIDE_OVERLAY;
        if ("outsideInset".equals(text))
            return View.SCROLLBARS_OUTSIDE_INSET;
        if ("outsideOverlay".equals(text))
            return View.SCROLLBARS_OUTSIDE_OVERLAY;
        throw new IllegalArgumentException("Unknown scrollbar style \""+ text +"\"");
    }
    private static int formatTextStyle(String text) {
        String[] args = text.split("\\|");
        int res = 0;
        for (String arg : args) {
            if ("normal".equals(arg))
                res |= Typeface.NORMAL;
            else if ("bold".equals(arg))
                res |= Typeface.BOLD;
            else if ("italic".equals(arg))
                res |= Typeface.ITALIC;
            else
                throw new IllegalArgumentException("Unknown text style \""+ text +"\"");
        }
        return res;
    }
    private static int formatAlignment(String text) {
        if ("alignBounds".equals(text))
            return GridLayout.ALIGN_BOUNDS;
        if ("alignMargins".equals(text))
            return GridLayout.ALIGN_MARGINS;
        throw new IllegalArgumentException("Unknown alignment mode\""+ text +"\"");
    }
    private static int formatOptions(String text) {
        String[] args = text.split("\\|");
        int res = 0;
        for (String arg : args) {
            if (!"normal".equals(text)) {
                if ("actionUnspecified".equals(text))
                    res |= EditorInfo.IME_NULL;
                else if ("actionNone".equals(text))
                    res |= EditorInfo.IME_ACTION_NONE;
                else if ("actionGo".equals(text))
                    res |= EditorInfo.IME_ACTION_GO;
                else if ("actionSearch".equals(text))
                    res |= EditorInfo.IME_ACTION_SEARCH;
                else if ("actionSend".equals(text))
                    res |= EditorInfo.IME_ACTION_SEND;
                else if ("actionNext".equals(text))
                    res |= EditorInfo.IME_ACTION_NEXT;
                else if ("actionDone".equals(text))
                    res |= EditorInfo.IME_ACTION_DONE;
                else if ("actionPrevious".equals(text)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                        res |= EditorInfo.IME_ACTION_PREVIOUS;
                }
                else if ("flagNoFullscreen".equals(text)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                        res |= EditorInfo.IME_FLAG_NO_FULLSCREEN;
                }
                else if ("flagNavigatePrevious".equals(text)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                        res |= EditorInfo.IME_FLAG_NAVIGATE_PREVIOUS;
                }
                else if ("flagNavigateNext".equals(text)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                        res |= EditorInfo.IME_FLAG_NAVIGATE_NEXT;
                }
                else if ("flagNoExtractUi".equals(text))
                    res |= EditorInfo.IME_FLAG_NO_EXTRACT_UI;
                else if ("flagNoAccessoryAction".equals(text))
                    res |= EditorInfo.IME_FLAG_NO_ACCESSORY_ACTION;
                else if ("flagNoEnterAction".equals(text))
                    res |= EditorInfo.IME_FLAG_NO_EXTRACT_UI;
                else if ("flagForceAscii".equals(text)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        res |= EditorInfo.IME_FLAG_FORCE_ASCII;
                }
                else
                    throw new IllegalArgumentException("Unknown format option \""+ text +"\"");
            }
        }
        return res;
    }
    private static Typeface formatTypeface(String text) {
        if ("normal".equals(text))
            return null;
        if ("monospace".equals(text))
            return Typeface.MONOSPACE;
        if ("serif".equals(text))
            return Typeface.SERIF;
        if ("sans".equals(text))
            return Typeface.SANS_SERIF;
        throw new IllegalArgumentException("Unknown typeface \""+ text +"\"");
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static int formatTextAlign(String text) {
        if ("inherit".equals(text))
            return View.TEXT_ALIGNMENT_INHERIT;
        if ("center".equals(text))
            return View.TEXT_ALIGNMENT_CENTER;
        if ("gravity".equals(text))
            return View.TEXT_ALIGNMENT_GRAVITY;
        if ("textStart".equals(text))
            return View.TEXT_ALIGNMENT_TEXT_START;
        if ("textEnd".equals(text))
            return View.TEXT_ALIGNMENT_TEXT_END;
        if ("viewStart".equals(text))
            return View.TEXT_ALIGNMENT_VIEW_START;
        if ("viewEnd".equals(text))
            return View.TEXT_ALIGNMENT_VIEW_END;
        throw new IllegalArgumentException("Unknown text alignment \""+ text +"\"");
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static int formatDirection(String text) {
        if ("inherit".equals(text))
            return View.TEXT_DIRECTION_INHERIT;
        if ("anyRtl".equals(text))
            return View.TEXT_DIRECTION_ANY_RTL;
        if ("firstStrong".equals(text))
            return View.TEXT_DIRECTION_FIRST_STRONG;
        if ("locale".equals(text))
            return View.TEXT_DIRECTION_LOCALE;
        if ("ltr".equals(text))
            return View.TEXT_DIRECTION_LTR;
        if ("rtl".equals(text))
            return View.TEXT_DIRECTION_RTL;
        throw new IllegalArgumentException("Unknown text direction \""+ text +"\"");
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static int formatPosition(String text) {
        if ("defaultPosition".equals(text))
            return View.SCROLLBAR_POSITION_DEFAULT;
        if ("left".equals(text))
            return View.SCROLLBAR_POSITION_LEFT;
        if ("right".equals(text))
            return View.SCROLLBAR_POSITION_RIGHT;
        throw new IllegalArgumentException("Unknown scrollbar position \""+ text +"\"");
    }
    private static int formatVisibility(String text) {
        if ("visible".equals(text))
            return View.VISIBLE;
        else if ("invisible".equals(text))
            return View.INVISIBLE;
        else if ("gone".equals(text))
            return View.GONE;
        throw new IllegalArgumentException("Unknown visibility \""+ text +"\"");
    }
    private static int formatAutoLink(String text) {
        String[] args = text.split("\\|");
        int res = 0;
        for (String arg : args) {
            if (!"none".equals(arg)) {
                if ("web".equals(arg))
                    res |= Linkify.WEB_URLS;
                else if ("email".equals(arg))
                    res |= Linkify.EMAIL_ADDRESSES;
                else if ("phone".equals(arg))
                    res |= Linkify.PHONE_NUMBERS;
                else if ("map".equals(arg))
                    res |= Linkify.MAP_ADDRESSES;
                else if ("all".equals(arg))
                    res |= Linkify.ALL;
                else
                    throw new IllegalArgumentException("Unknown autolink \"" + arg + "\"");
            }
        }
        return res;
    }
    private static int formatCaps(String text) {
        if ("capitalize".equals(text))
            return InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;
        else if ("sentences".equals(text))
            return InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
        else if ("words".equals(text))
            return InputType.TYPE_TEXT_FLAG_CAP_WORDS;
        else if ("none".equals(text))
            return 0;
        throw new IllegalArgumentException("Unknows caps type \""+ text +"\"");
    }
    private static int formatInputType(String text) {
        if ("none".equals(text))
            return InputType.TYPE_NULL;
        if ("date".equals(text))
            return InputType.TYPE_DATETIME_VARIATION_DATE;
        if ("datetime".equals(text))
            return InputType.TYPE_DATETIME_VARIATION_TIME;
        if ("number".equals(text))
            return InputType.TYPE_CLASS_NUMBER;
        if ("numberDecimal".equals(text))
            return InputType.TYPE_NUMBER_FLAG_DECIMAL;
        if ("numberPassword".equals(text))
            return InputType.TYPE_NUMBER_VARIATION_PASSWORD;
        if ("numberSigned".equals(text))
            return InputType.TYPE_NUMBER_FLAG_SIGNED;
        if ("phone".equals(text))
            return InputType.TYPE_CLASS_PHONE;
        if ("phone".equals(text))
            return InputType.TYPE_CLASS_PHONE;
        if ("text".equals(text))
            return InputType.TYPE_CLASS_TEXT;
        if ("textAutoComplete".equals(text))
            return InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE;
        if ("textAutoCorrect".equals(text))
            return InputType.TYPE_TEXT_FLAG_AUTO_CORRECT;
        if ("textCapCharacters".equals(text))
            return InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;
        if ("textCapSentences".equals(text))
            return InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
        if ("textCapWords".equals(text))
            return InputType.TYPE_TEXT_FLAG_CAP_WORDS;
        if ("textEmailAddress".equals(text))
            return InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
        if ("textEmailSubject".equals(text))
            return InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT;
        if ("textFilter".equals(text))
            return InputType.TYPE_TEXT_VARIATION_FILTER;
        if ("textImeMultiLine".equals(text))
            return InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE;
        if ("textLongMessage".equals(text))
            return InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE;
        if ("textMultiLine".equals(text))
            return InputType.TYPE_TEXT_FLAG_MULTI_LINE;
        if ("textNoSuggestions".equals(text))
            return InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
        if ("textPassword".equals(text))
            return InputType.TYPE_TEXT_VARIATION_PASSWORD;
        if ("textPersonName".equals(text))
            return InputType.TYPE_TEXT_VARIATION_PERSON_NAME;
        if ("textPhonetic".equals(text))
            return InputType.TYPE_TEXT_VARIATION_PHONETIC;
        if ("textPostalAddress".equals(text))
            return InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS;
        if ("textShortMessage".equals(text))
            return InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE;
        if ("textUri".equals(text))
            return InputType.TYPE_TEXT_VARIATION_URI;
        if ("textImeMultiLine".equals(text))
            return InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE;
        if ("textCapCharacters".equals(text))
            return InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;
        if ("textCapSentences".equals(text))
            return InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
        if ("textVisiblePassword".equals(text))
            return InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
        if ("textNoSuggestions".equals(text))
            return InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
        if ("textWebEditText".equals(text))
            return InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT;
        if ("textWebEmailAddress".equals(text))
            return InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
        if ("textWebPassword".equals(text)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                return InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD;
        }
        throw new IllegalArgumentException("Unable to parse input type \""+ text +"\"");
    }
    private LayoutAnimationController formatAnimation(String text) {
        try {
            return AnimationUtils.createLayoutAnimationFromXml(ctx, ctx.getResources().getAnimation(getIdentifier("anim", text)));
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.toString());
        }
    }
    private View.OnClickListener formatOnClick(String text) {
        try {
            final Method callback = ctx.getClass().getMethod(text, View.class);
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        callback.invoke(ctx, v);
                    }
                    catch (IllegalAccessException e) {
                    }
                    catch (InvocationTargetException e) {
                    }
                }
            };
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Method "+ text +" is missing in "+ ctx.getClass().getName() +" or has incorrect signature");
        }
    }
    private static TextUtils.TruncateAt formatEllipse(String text) {
        if ("none".equals(text))
            return null;
        if ("start".equals(text))
            return TextUtils.TruncateAt.START;
        if ("middle".equals(text))
            return TextUtils.TruncateAt.MIDDLE;
        if ("end".equals(text))
            return TextUtils.TruncateAt.END;
        if ("marquee".equals(text))
            return TextUtils.TruncateAt.MARQUEE;
        throw new IllegalArgumentException("Unknown truncate type \""+ text +"\"");
    }
    private static boolean[] formatOrientations(String text) {
        String[] attrs = text.split("\\|");
        boolean[] res = new boolean[2];
        for (String attr : attrs) {
            if ("horizontal".equals(attr))
                res[0] = true;
            else if ("vertical".equals(attr))
                res[1] = true;
            else
                throw new IllegalArgumentException("Unknown orientation \""+ text +"\"");
        }
        return res;
    }
    private static String orientationFlags(boolean horizontal, boolean vertical) {
        if (horizontal && vertical)
            return "horizontal|vertical";
        else if (horizontal)
            return "horizontal";
        else if (vertical)
            return "vertical";
        else
            return "none";
    }
    private float formatFloat(String text) {
        try {
            return Float.valueOf(text);
        }
        catch (NumberFormatException e) {
            try {
                TypedValue outValue = new TypedValue();
                ctx.getResources().getValue(getIdentifier("dimen", text), outValue, true);
                return outValue.getFloat();
            }
            catch (Resources.NotFoundException e2) {
                throw e;
            }
        }
    }
    private static final Pattern DIMEN_MATCHER = Pattern.compile("^([0-9]*(?:\\.[0-9]*)?)([a-z]*)$");
    private float formatDimen(String text) {
        Matcher m = DIMEN_MATCHER.matcher(text);
        if (m.find())
            return toPX(Float.valueOf(m.group(1)), m.group(2));
        else {
            try {
                return ctx.getResources().getDimension(getIdentifier("dimen", text));
            }
            catch (Resources.NotFoundException e) {
            }
        }
        throw new IllegalArgumentException("Unable to parse dimension \""+ text +"");
    }
    private int getIdentifier(String text) throws Resources.NotFoundException {
        return getIdentifier(ctx, text);
    }
    protected static int getIdentifier(Context ctx, String text) throws Resources.NotFoundException {
        Matcher m = Pattern.compile("^@([a-z]+)/([a-zA-Z0-9_]+)$").matcher(text);
        if (m.find())
            return ctx.getResources().getIdentifier(m.group(1), m.group(2), ctx.getPackageName());
        throw new Resources.NotFoundException("Unable to find resource \""+ text +"\"");
    }
    private int getIdentifier(String folder, String text) throws Resources.NotFoundException {
        return getIdentifier(ctx, folder, text);
    }
    protected static int getIdentifier(Context ctx, String folder, String text) throws Resources.NotFoundException {
        Matcher m = Pattern.compile("^@"+ folder +"/([a-zA-Z0-9_]+)$").matcher(text);
        if (m.find())
            return ctx.getResources().getIdentifier(m.group(1), folder, ctx.getPackageName());
        throw new Resources.NotFoundException("Unable to find resource \""+ text +"\"");
    }
    private int formatColor(String text) {
        String color = text.replaceAll("(?i)^#([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])$", "#$1$1$2$2$3$3$4$4");
        color = color.replaceAll("(?i)^#([0-9A-F])([0-9A-F])([0-9A-F])$", "#$1$1$2$2$3$3");
        try {
            return Color.parseColor(color);
        }
        catch (IllegalArgumentException e) {
            try {
                return Integer.valueOf(text);
            }
            catch (NumberFormatException e2) {
                try {
                    return ContextCompat.getColor(ctx, getIdentifier("color", text));
                }
                catch (Exception e3) {
                    throw new IllegalArgumentException("Unable to parse color \""+ text +"\"");
                }
            }
        }
    }
    private static ColorStateList toColorState(int c) {
        return new ColorStateList(new int[][]{new int[0]}, new int[]{c});
    }
    private ColorStateList formatTint(String text) {
        return toColorState(formatColor(text));
    }
    private int formatResource(String text) {
        return getIdentifier(text);
    }
    private static int intCast(Object value) {
        try {
            return (int) value;
        }
        catch (ClassCastException e) {
            try {
                return Math.round((float) value);
            }
            catch (ClassCastException e2) {
                return (int) Math.round((double) value);
            }
        }
    }
    private static long longCast(Object value) {
        try {
            return (long) value;
        }
        catch (ClassCastException e) {
            try {
                return Math.round((float) value);
            }
            catch (ClassCastException e2) {
                try {
                    return Math.round((double) value);
                }
                catch (ClassCastException e3) {
                    try {
                        return (int) value;
                    }
                    catch (ClassCastException e4) {
                        throw e;
                    }
                }
            }
        }
    }
    protected int widthCast(Object value) {
        int res = intCast(value);
        switch (res) {
            case ViewGroup.LayoutParams.MATCH_PARENT :
                return parent().width();
            case ViewGroup.LayoutParams.WRAP_CONTENT :
                return scrollWidth();
            default:
                return res;
        }
    }
    protected int heightCast(Object value) {
        int res = intCast(value);
        switch (res) {
            case ViewGroup.LayoutParams.MATCH_PARENT :
                return parent().height();
            case ViewGroup.LayoutParams.WRAP_CONTENT :
                return scrollHeight();
            default:
                return res;
        }
    }
    private static float floatCast(Object value) {
        try {
            return (float) value;
        }
        catch (ClassCastException e) {
            try {
                return (int) value;
            }
            catch (ClassCastException e2) {
                return (float) (double) value;
            }
        }
    }
    private static double doubleCast(Object value) {
        try {
            return (double) value;
        }
        catch (ClassCastException e) {
            try {
                return (int) value;
            }
            catch (ClassCastException e2) {
                return (float) value;
            }
        }
    }
    @SuppressWarnings("deprecation")
    private static Drawable drawableCast(Object value) {
        try {
            return (Drawable) value;
        }
        catch (ClassCastException e) {
            try {
                return new BitmapDrawable((Bitmap) value);
            }
            catch (ClassCastException e2) {
                try {
                    return new ColorDrawable((int) value);
                }
                catch (ClassCastException e3) {
                    throw e;
                }
            }
        }
    }
    @SuppressWarnings("deprecation")
    private Drawable formatDrawable(String text) {
        int resourceID;
        try {
            resourceID = getIdentifier("drawable", text);
        }
        catch (Resources.NotFoundException e) {
            try {
                resourceID = getIdentifier("mipmap", text);
            }
            catch (Resources.NotFoundException e2) {
                try {
                    return new ColorDrawable(formatColor(text));
                }
                catch (IllegalArgumentException e3) {
                    throw e;
                }
            }
        }
        return ctx.getResources().getDrawable(resourceID);
    }

    /*
     * End of format functions
     */

    /**
     * Converts sp dimension to px
     */
    private float toSP(float px) {
        return px/ctx.getResources().getDisplayMetrics().scaledDensity;
    }

    /**
     * Converts a dimension in any given unit to px
     */
    private float toPX(float size, int unit) {
        return toPX(ctx, size, unit);
    }
    /**
     * Converts a dimension in any given unit to px
     */
    public static float toPX(Context ctx, float size, int unit) {
        return TypedValue.applyDimension(unit, size, ctx.getResources().getDisplayMetrics());
    }
    /**
     * Converts a dimension in any given unit to px
     */
    private float toPX(float size, String unit) {
        return toPX(ctx, size, unit);
    }
    /**
     * Converts a dimension in any given unit to px
     */
    public static float toPX(Context ctx, float size, String unit) {
        return toPX(ctx, size, $Utils.unitID(unit));
    }

    /**
     * A class to handles the [attr="value"] CSS selector
     */
    private static class BracketAnalyser {
        private String completeString;
        private int bracketStartID, bracketEndID;
        private int attrStartID, attrEndID;
        private int valStartID, valEndID;
        private char flag;
        private char shutoff;

        private BracketAnalyser(String selector, int bracketID) {
            bracketStartID = bracketID;
            completeString = selector;
            int i = bracketID;
            i++;
            attrStartID = i;
            try {
                for (;completeString.charAt(i)!='=';i++);
            }
            catch (StringIndexOutOfBoundsException e) {
                throwSyntaxError();
            }
            i--;
            flag = completeString.charAt(i);
            if ("|~^$*".indexOf(flag) != -1) {
                attrEndID = i;
                i++;
            }
            else {
                i++;
                attrEndID = i;
                flag = 0;
            }
            i++;
            shutoff = completeString.charAt(i);
            boolean escaped = ((shutoff == '\'') || (shutoff == '"'));
            if (escaped)
                i++;
            else
                shutoff = ']';
            boolean antislach = false;
            valStartID = i;
            for (;i<completeString.length();i++) {
                if (antislach) {
                    antislach = false;
                    continue;
                }
                char iChar = completeString.charAt(i);
                if (iChar == shutoff)
                    break;
                if (completeString.charAt(i) == '\\')
                    antislach = true;
            }
            valEndID = i;
            if (escaped) {
                i++;
                if (completeString.charAt(i) != ']')
                    throwSyntaxError();
            }
            i++;
            bracketEndID = i;
        }

        public static ArrayList<BracketAnalyser> matchAll(String selector) {
            ArrayList<BracketAnalyser> res = new ArrayList<BracketAnalyser>();
            int bracketID = selector.indexOf('[');
            while (bracketID != -1) {
                BracketAnalyser nextAnalyser = new BracketAnalyser(selector,bracketID);
                res.add(nextAnalyser);
                bracketID = selector.substring(nextAnalyser.bracketEndID).indexOf('[');
                if (bracketID != -1)
                    bracketID += nextAnalyser.bracketEndID;
            }
            return res;
        }

        public int getBracketStartID() {
            return bracketStartID;
        }
        public int getBracketEndID() {
            return bracketEndID;
        }
        public String getBracket() {
            return completeString.substring(bracketStartID,bracketEndID);
        }
        public int getAttrStartID() {
            return attrStartID;
        }
        public int getAttrEndID() {
            return attrEndID;
        }
        public String getAttr() {
            return completeString.substring(attrStartID,attrEndID);
        }
        public int getValStartID() {
            return valStartID;
        }
        public int getValEndID() {
            return valEndID;
        }
        public String getVal() {
            return completeString.substring(valStartID,valEndID).replaceAll("\\\\([^a-z])", "$1");
        }
        public char getFlag() {
            return flag;
        }

        private void throwSyntaxError() {
            throw new IllegalArgumentException("Syntax error, unrecognized expression \""+ completeString +"\"");
        }
    }

    /**
     * A class to analyse selectos
     * This class is basically an array of filters, like ["#my_id", "TextView"],
     * and a list of functions to treat brackets filters
     */
    private static class AnalyzedSelectors {
        public final String[] selectors;
        public final ArrayList<BracketAnalyser> brackets;

        public AnalyzedSelectors(String selector, ArrayList<BracketAnalyser> brackets) {
            this(selector.split(" *, *"),brackets);
        }
        public AnalyzedSelectors(String[] selectors, ArrayList<BracketAnalyser> brackets) {
            this.selectors = selectors;
            this.brackets = brackets;
        }
    }

    /**
     * Splits a CSS selector String with colons to treat each single filter individually
     * @param selector
     * The selector. Example : "#my_id, TextView"
     * @return
     * A String[] containing each selector individually. For example : ["#my_id", "TextView"]
     */
    private static AnalyzedSelectors splitSelectors(String selector) {
        ArrayList<BracketAnalyser> brackets = BracketAnalyser.matchAll(selector);
        if (brackets.isEmpty())
            return new AnalyzedSelectors(selector,brackets);
        String toSplit = "";
        int bracketID = 0;
        for (int i=0;i<brackets.size();i++) {
            BracketAnalyser bracket = brackets.get(i);
            toSplit += selector.substring(bracketID, bracket.getBracketStartID());
            toSplit += "["+ i +"]";
            bracketID = bracket.getBracketEndID();
        }
        return new AnalyzedSelectors(toSplit,brackets);
    }

    /**
     * Get the descendants of each element in the current set of matched elements, filtered by a selector
     */
    public AQuery find(String selector) {
        return findAll(splitSelectors(selector));
    }

    /**
     * Find the descendants of each element in the current set of matched elements, filtered by an AnalyzedSelectors
     */
    private AQuery findAll(AnalyzedSelectors analSelectors) {
        List<View> res = new ArrayList<>();
        for (String selector : analSelectors.selectors)
            res = union(res, findEach(selector,analSelectors.brackets).list());
        return new $Array(ctx, res);
    }
    /**
     * Find the descendants of each element in the current set of elements, filtered by a single selector
     */
    private AQuery findEach(String selector, List<BracketAnalyser> analyser) {
        String[] selectors = selector.split(" +");
        FindListener l = find(new $List<String>(selectors),analyser);
        return l.get(this);
    }

    /**
     * Finds all descendants of each element that has the given id
     */
    public AQuery find(int id) {
        $Array res = new $Array(ctx);
        for (View elt : children().list()) {
            if (elt.getId() == id)
                res.add(elt);
            res.addAll(new $Element(ctx,elt).find(id).list());
        }
        return res;
    }

    /**
     * Ad the views matching a given criteria
     */
    private static void addViewsMatching($Array nodes, View v, MatchListener subChecker, FindListener subFinder) {
        $Element jElt = new $Element(nodes.ctx, v);
        if (subChecker.match(jElt))
            nodes.addAll(subFinder.get(jElt).list());
    }

    /**
     * A listener to get the views matching a given criteria
     */
    private interface FindListener {
        AQuery get(AQuery q);
    }

    /**
     * Returns the direct children of the elements
     */
    private AQuery getDirectChildren(FindListener subFinder, MatchListener subChecker) {
        $Array nodes = new $Array(ctx);
        for (View element : list()) {
            ViewGroup vGroup;
            try {
                vGroup = (ViewGroup)element;
            }
            catch (ClassCastException e) {
                continue;
            }
            for (int i=0;i<vGroup.getChildCount();i++)
                addViewsMatching(nodes,vGroup.getChildAt(i),subChecker,subFinder);
        }
        return nodes;
    }
    /**
     * Returns all children and subchildren of the elements
     */
    private AQuery getAllChildren(FindListener subFinder, MatchListener subChecker) {
        $Array nodes = new $Array(ctx);
        for (View element : list()) {
            ViewGroup vGroup;
            try {
                vGroup = (ViewGroup)element;
            }
            catch (ClassCastException e) {
                continue;
            }
            for (View child : getDescendants(vGroup))
                addViewsMatching(nodes,child,subChecker,subFinder);
        }
        return nodes;
    }
    /**
     * Returns the elements directly after the elements in the set of elements
     */
    private AQuery getElementsAfter(FindListener subFinder, MatchListener subChecker) {
        $Array nodes = new $Array(ctx);
        for (View element : list()) {
            ViewGroup parent = (ViewGroup) element.getParent();
            int pos = findPosition(parent,element);
            pos++;
            if (pos < parent.getChildCount())
                addViewsMatching(nodes,parent.getChildAt(pos),subChecker,subFinder);
        }
        return nodes;
    }
    /**
     * Returns the elements directly before the elements in the set of elements
     */
    private AQuery getElementsBefore(FindListener subFinder, MatchListener subChecker) {
        $Array nodes = new $Array(ctx);
        for (View element : list()) {
            ViewGroup parent = (ViewGroup) element.getParent();
            int pos = findPosition(parent,element);
            if (pos > 0)
                addViewsMatching(nodes,parent.getChildAt(pos-1),subChecker,subFinder);
        }
        return nodes;
    }

    /**
     * Returns the function that will, for a given View check if the view matches the selectors
     */
    protected static FindListener find($List<String> selectors, List<BracketAnalyser> analyser) {
        if (selectors.isEmpty())
            return new FindListener() {
                @Override
                public AQuery get(AQuery q) {
                    return q;
                }
            };
        else {
            final String firstSelector = selectors.head();
            selectors = selectors.tail();
            if (">".equals(firstSelector)) {
                String secondSelector = selectors.head();
                selectors = selectors.tail();
                final FindListener subFinder = find(selectors, analyser);
                final MatchListener subChecker = typeChecker(secondSelector, analyser);
                return new FindListener() {
                    @Override
                    public AQuery get(AQuery q) {
                        return q.getDirectChildren(subFinder,subChecker);
                    }
                };
            }
            else if ("+".equals(firstSelector)) {
                String secondSelector = selectors.head();
                selectors = selectors.tail();
                final FindListener subFinder = find(selectors, analyser);
                final MatchListener subChecker = typeChecker(secondSelector, analyser);
                return new FindListener() {
                    @Override
                    public AQuery get(AQuery q) {
                        return q.getElementsAfter(subFinder, subChecker);
                    }
                };
            }
            else if ("~".equals(firstSelector)) {
                String secondSelector = selectors.head();
                selectors = selectors.tail();
                final FindListener subFinder = find(selectors, analyser);
                final MatchListener subChecker = typeChecker(secondSelector, analyser);
                return new FindListener() {
                    @Override
                    public AQuery get(AQuery q) {
                        return q.getElementsBefore(subFinder, subChecker);
                    }
                };
            }
            else {
                final FindListener subFinder = find(selectors, analyser);
                final MatchListener subChecker = typeChecker(firstSelector, analyser);
                return new FindListener() {
                    @Override
                    public AQuery get(AQuery q) {
                        return q.getAllChildren(subFinder, subChecker);
                    }
                };
            }
        }
    }

    public interface TestFunction {
        public boolean test(View v);
    }

    /**
     * Reduce the set of matched elements to those that match the selector
     */
    public AQuery filter(String criteria) {
        return new $Array(ctx, intersection(list(), new $Document(ctx).find(criteria).list()));
    }
    /**
     * Reduce the set of matched elements to those that matches the test function
     */
    public AQuery filter(TestFunction function) {
        List<View> elements = list();
        ArrayList<View> res = new ArrayList<>(elements.size());
        for (View v : elements) {
            if (function.test(v))
                res.add(v);
        }
        return new $Array(ctx, res);
    }
    /**
     * The test function that always return true
     */
    private static final MatchListener ALWAYS_MATCH = new MatchListener() {
        @Override
        public boolean match($Element jElt) {
            return true;
        }
    };
    /**
     * The test function that always return false
     */
    private static final MatchListener NEVER_MATCH = new MatchListener() {
        @Override
        public boolean match($Element jElt) {
            return true;
        }
    };

    /**
     * Returns the test function that checks if a View has a given tag
     */
    private static MatchListener tagChecker(String tag) {
        if ("*".equals(tag))
            return ALWAYS_MATCH;
        final String vTag = tag;
        return new MatchListener() {
            @Override
            public boolean match($Element jElt) {
                return hasTag(jElt,vTag);
            }
        };
    }
    /**
     * Returns the test function that checks if a View has NOT a given tag
     */
    private static MatchListener nottagChecker(String tag) {
        if ("*".equals(tag))
            return NEVER_MATCH;
        final String vTag = tag;
        return new MatchListener() {
            @Override
            public boolean match($Element jElt) {
                return !hasTag(jElt,vTag);
            }
        };
    }

    /**
     * Checks if a View has the given tag
     */
    private static boolean hasTag($Element jElt, String tag) {
        return jElt.head().getClass().getSimpleName().equals(tag);
    }
    /**
     * Returns the function test that checks if an element has a given id
     */
    private static MatchListener idChecker(final String id) {
        return new MatchListener() {
            @Override
            public boolean match($Element jElt) {
                return hasId(jElt,id);
            }
        };
    }
    private static final Pattern ID_MATCHER = Pattern.compile(":id/(.+)$");
    /**
     * Checks if a View has the given id
     */
    private static boolean hasId($Element jElt, String id) {
        View v = jElt.head();
        if (v.getId() == View.NO_ID)
            return false;
        String vID;
        try {
            vID = v.getResources().getResourceName(v.getId());
        }
        catch (Resources.NotFoundException e) {
            return false;
        }
        Matcher m = ID_MATCHER.matcher(vID);
        if (m.find()) {
            if (id.equals(m.group(1)))
                return true;
        }
        return false;
    }

    /**
     * Format a given coefficient, given by an integer and/or a +/- sign before
     */
    private static int formatCoeff(String coeff) {
        if ("".equals(coeff) || "+".equals(coeff))
            return 1;
        if ("-".equals(coeff))
            return -1;
        return Integer.valueOf(coeff);
    }
    private static final Pattern AFFINE_FUNC_MATCHER = Pattern.compile("^([+-]?[0-9]*)n([+-])([0-9]*)$");
    private static final Pattern LINEAR_FUNC_MATCHER = Pattern.compile("^([+-]?[0-9]*)n$");
    /**
     * Format the coefficients of an expression of the form "bn+a" or "bn" or "a"
     * @param expression
     * The expression where to retrieve the coefficiens
     * @return
     * An array containing [a,b]
     */
    private static int[] formatCoeffs(String expression) {
        int b, a;
        try {
            Matcher affineFunction = AFFINE_FUNC_MATCHER.matcher(expression);
            if (affineFunction.find()) { // "bn+a"-like expression
                b = formatCoeff(affineFunction.group(1));
                a = Integer.valueOf(affineFunction.group(3));
                if ("-".equals(affineFunction.group(2)))
                    a = -a;
            } else {
                Matcher linearFunction = LINEAR_FUNC_MATCHER.matcher(expression);
                if (linearFunction.find()) { // "bn"-like expression
                    b = formatCoeff(linearFunction.group(1));
                    a = 0;
                } else { // Constant
                    a = Integer.valueOf(expression);
                    b = 0;
                }
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Unrecognized expression : \""+ expression +"\"");
        }
        return new int[]{a,b};
    }

    /**
     * Returns true if and only if the position can be written as coeffs[1]*k+coeffs[0] with k positive integer
     */
    private static boolean isNth(int position, int[] coeffs) {
        return isNth(position, coeffs[0], coeffs[1]);
    }
    /**
     * Returns true if and only if the position can be written as a*k+b with k positive integer
     */
    private static boolean isNth(int position, int a, int b) {
        int diff = position-a;
        if (b == 0)
            return (diff == 0);
        int quotient = diff/b;
        if (quotient >= 0)
            return ((b*quotient) == diff);
        return false;
    }
    private static final Pattern NTH_CHILD_MATCHER = Pattern.compile("^nth-child\\((.+)\\)$");
    private static final Pattern NTH_LAST_CHILD_MATCHER = Pattern.compile("^nth-last-child\\((.+)\\)$");
    private static final Pattern NOT_TAG_MATCHER = Pattern.compile("^not\\((.+)\\)$");

    /**
     * Returns the function that checks if a given View matches the filter condition
     * @param condition
     * The filter condition, like "empty", "first-child", etc
     */
    private static MatchListener conditionChecker(String condition) {
        if ("empty".equals(condition)) {
            return new MatchListener() {
                @Override
                public boolean match($Element jElt) {
                    return (jElt.children().length() == 0);
                }
            };
        } else if ("first-child".equals(condition)) {
            return new MatchListener() {
                @Override
                public boolean match($Element jElt) {
                    return (findPosition(jElt.head()) == 0);
                }
            };
        }
        else if ("last-child".equals(condition)) {
            return new MatchListener() {
                @Override
                public boolean match($Element jElt) {
                    return (findLastPosition(jElt.head()) == 1);
                }
            };
        } else if ("only-child".equals(condition)) {
            return new MatchListener() {
                @Override
                public boolean match($Element jElt) {
                    return (jElt.parent().children().length() == 1);
                }
            };
        } else if ("root".equals(condition)) {
            return new MatchListener() {
                @Override
                public boolean match($Element jElt) {
                    return (jElt.head() == new $Utils(jElt.ctx).root().head());
                }
            };
        }
        else {
            Matcher m = NTH_CHILD_MATCHER.matcher(condition);
            if (m.find()) {
                final int[] coeffs = formatCoeffs(m.group(1));
                return new MatchListener() {
                    @Override
                    public boolean match($Element jElt) {
                        return isNth(findPosition(jElt.head()) + 1, coeffs);
                    }
                };
            }
            m = NTH_LAST_CHILD_MATCHER.matcher(condition);
            if (m.find()) {
                final int[] coeffs = formatCoeffs(m.group(1));
                return new MatchListener() {
                    @Override
                    public boolean match($Element jElt) {
                        return isNth(findLastPosition(jElt.head()), coeffs);
                    }
                };
            }
            m = NOT_TAG_MATCHER.matcher(condition);
            if (m.find())
                return nottagChecker(m.group(1));
            throw new IllegalArgumentException("Invalid filter condition \""+ condition +"\"");
        }
    }

    /** Returns the function that checks if a View matches a "bracket" condition, like [attr="value"]
     */
    private static MatchListener bracketChecker(BracketAnalyser analyser) {
        final AttrSetter callback = getAttr(analyser.getAttr());
        final String val = analyser.getVal();
        switch (analyser.getFlag()) {
            case 0 :
                return new MatchListener() {
                    @Override
                    public boolean match($Element jElt) {
                        return checkAttrEquals(jElt, callback, val);
                    }
                };
            case '|' :
                return new MatchListener() {
                    @Override
                    public boolean match($Element jElt) {
                        return checkAttrStarts(jElt, callback, val);
                    }
                };
            case '~' :
                return new MatchListener() {
                    @Override
                    public boolean match($Element jElt) {
                        return checkAttrHas(jElt, callback, val);
                    }
                };
            case '^' :
                return new MatchListener() {
                    @Override
                    public boolean match($Element jElt) {
                        return checkAttrBegins(jElt, callback, val);
                    }
                };
            case '$' :
                return new MatchListener() {
                    @Override
                    public boolean match($Element jElt) {
                        return checkAttrEnds(jElt, callback, val);
                    }
                };
            case '*' :
                return new MatchListener() {
                    @Override
                    public boolean match($Element jElt) {
                        return checkAttrContains(jElt, callback, val);
                    }
                };
            default :
                return null; // Dead code
        }
    }
    private static boolean checkAttrEquals($Element jElt, AttrSetter callback, String val) {
        Object jVal, eVal;
        try {
            jVal = callback.get(jElt.head());
            eVal = callback.format(jElt,val);
        }
        catch (Exception e) {
            return false;
        }
        return equals(jVal,eVal);
    }

    private static boolean checkAttrBegins($Element jElt, AttrSetter callback, String val) {
        String jVal, eVal;
        try {
            jVal = callback.get(jElt.head()).toString();
            eVal = callback.format(jElt,val).toString();
        }
        catch (Exception e) {
            return false;
        }
        return jVal.startsWith(eVal);
    }
    private static boolean checkAttrStarts($Element jElt, AttrSetter callback, String val) {
        String jVal, eVal;
        try {
            jVal = callback.get(jElt.head()).toString();
            eVal = callback.format(jElt,val).toString();
        }
        catch (Exception e) {
            return false;
        }
        return jVal.equals(val) || jVal.startsWith(eVal + ' ');
    }
    private static boolean checkAttrEnds($Element jElt, AttrSetter callback, String val) {
        String jVal, eVal;
        try {
            jVal = callback.get(jElt.head()).toString();
            eVal = callback.format(jElt,val).toString();
        }
        catch (Exception e) {
            return false;
        }
        return jVal.endsWith(eVal);
    }
    private static boolean checkAttrHas($Element jElt, AttrSetter callback, String val) {
        String jVal, eVal;
        try {
            jVal = callback.get(jElt.head()).toString();
            eVal = callback.format(jElt,val).toString();
        }
        catch (Exception e) {
            return false;
        }
        return jVal.equals(eVal) || jVal.startsWith(eVal +' ') || jVal.endsWith(' '+ eVal) || jVal.contains(' '+ eVal +' ');
    }
    private static boolean checkAttrContains($Element jElt, AttrSetter callback, String val) {
        String jVal, eVal;
        try {
            jVal = callback.get(jElt.head()).toString();
            eVal = callback.format(jElt,val).toString();
        }
        catch (Exception e) {
            return false;
        }
        return jVal.contains(eVal);
    }

    /**
     * Returns the function that checks if a selector matches a given filter condition, such as "TextView" or ":first-child"
     */
    private static MatchListener selectorChecker(String selector, List<BracketAnalyser> analyser) {
        String value;
        switch (selector.charAt(0)) {
            case '#' :
                return idChecker(selector.substring(1));
            case ':' :
                return conditionChecker(selector.substring(1));
            case '[' :
                return bracketChecker(analyser.get(Integer.valueOf(selector.substring(1, selector.length() - 1))));
        }
        throw new IllegalArgumentException("Invalid selector \""+ selector +"\"");
    }
    private static Pattern CONDITION_PATTERN = Pattern.compile("(?:[#:][^\\#:\\[]+)|(?:\\[[0-9]+\\])");

    /**
     * The listener for test-functions
     */
    private interface MatchListener {
        /**
         * Checks if the given element matches the given condition
         * @return
         * true if it does, false otherwise
         */
        boolean match($Element elt);
    }

    /**
     * Returns the function that checks if a selector matches a given set of conditions, such as "TextView#my_id:first-child"
     */
    protected static MatchListener typeChecker(String expression, List<BracketAnalyser> analyser) {
        Matcher m = CONDITION_PATTERN.matcher(expression); // Getting each filter (like #id, :selector)
        if (m.find()) { // If there is at least one filter other than tag filter
            final ArrayList<MatchListener> selectors = new ArrayList<>();
            if (m.start() != 0) { // If there is a tag filter (TextView, LinearLayout, etc)
                String viewTag = expression.substring(0,m.start()); // The tag name
                selectors.add(tagChecker(viewTag));
            }
            do { // For each filter, check if the view matches the condition
                selectors.add(selectorChecker(m.group(), analyser));
            } while (m.find());
            return new MatchListener() {
                @Override
                public boolean match($Element jElt) {
                    for (MatchListener selector : selectors) {
                        if (!selector.match(jElt))
                            return false;
                    }
                    return true;
                }
            };
        }
        else
            return tagChecker(expression);
    }

    /**
     * Returns all the direct children of a given view
     */
    protected static ArrayList<View> getChildren(View v) {
        ArrayList<View> res = new ArrayList<>();
        ViewGroup vGroup;
        try {
            vGroup = (ViewGroup)v;
        }
        catch (Exception e) {
            return res;
        }
        for (int i=0;i<vGroup.getChildCount();i++) {
            res.add(vGroup.getChildAt(i));
            res.addAll(getDescendants(vGroup.getChildAt(i)));
        }
        return res;
    }
    /**
     * Returns all the children and subchildren of a given view
     */
    protected static ArrayList<View> getDescendants(View v) {
        ArrayList<View> res = new ArrayList<>();
        ViewGroup vGroup;
        try {
            vGroup = (ViewGroup)v;
        }
        catch (Exception e) {
            return res;
        }
        for (int i=0;i<vGroup.getChildCount();i++) {
            res.add(vGroup.getChildAt(i));
            res.addAll(getDescendants(vGroup.getChildAt(i)));
        }
        return res;
    }
    /**
     * Returns all the children and subchildren of a given view, plus the view itself
     */
    protected static ArrayList<View> getFamily(View v) {
        ArrayList<View> res = new ArrayList<>();
        res.add(v);
        res.addAll(getDescendants(v));
        return res;
    }
    /**
     * Returns all the direct children of the elements
     */
    public AQuery children() {
        $Array res = new $Array(ctx);
        for (View v : list())
            res.addAll(getChildren(v));
        return res;
    }

    /**
     * Returns the i-th child of each element
     */
    public AQuery child(int i) {
        List<View> views = list();
        ArrayList<View> res = new ArrayList<>(views.size());
        for (View v : views)
            res.add(((ViewGroup) v).getChildAt(i));
        return new $Array(ctx,res);
    }
    /**
     * Returns all the children and subchildren of the elements
     */
    public AQuery descendants() {
        List<View> res = new ArrayList<View>();
        for (View v : list())
            res = union(res, getDescendants(v));
        return new $Array(ctx,res);
    }
    /**
     * Returns all the children and subchildren of the elements, plus the elements themselves
     */
    public AQuery family() {
        List<View> res = new ArrayList<View>();
        for (View v : list())
            res = union(res,getFamily(v));
        return new $Array(ctx,res);
    }

    /**
     * An interface to do the "foreah"-like loop
     */
    public interface EachListener {
        /**
         * The function called at each entry of the loop
         */
        void foreach(int index, View element);
    }
    public void each(EachListener e) {
        List<View> views = list();
        for (int i=0;i<views.size();i++)
            e.foreach(i, views.get(i));
    }

    /**
     * Returns the union of 2 sets, removing duplicates.
     * This function assumes that the first list has no duplicated
     */
    public static <T> List<T> union(List<T> l1, List<T> l2) {
        List<T> res = new ArrayList<>(l1.size()+l2.size());
        res.addAll(l1);
        for (T elt2 : l2) {
            if (!res.contains(elt2))
                res.add(elt2);
        }
        return res;
    }
    /**
     * Returns the intersection of 2 sets, removing duplicates.
     * This function assumes that the first list has no duplicated
     */
    public static <T> List<T> intersection(List<T> l1, List<T> l2) {
        List<T> res = new ArrayList<>(Math.min(l1.size(),l2.size()));
        for (T elt1 : l1) {
            if (l2.contains(elt1))
                res.add(elt1);
        }
        return res;
    }

    /**
     * Checks if 2 values are equal
     * Handles the null case
     */
    public static boolean equals(Object a, Object b) {
        if (a != null)
            return a.equals(b);
        return (b == null);
    }

    @Override
    public String toString() {
        return list().toString();
    }
}
