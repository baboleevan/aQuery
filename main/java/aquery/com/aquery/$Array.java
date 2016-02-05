package aquery.com.aquery;

import android.app.Activity;
import android.app.FragmentBreadCrumbs;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toolbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.AbstractCollection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timothé on 01/01/2016.
 */
public class $Array extends AQuery {
    List<View> views;

    public $Array(Activity ctx) {
        this(ctx, new ArrayList<View>());
    }
    public $Array(Activity ctx, List<View> views) {
        super(ctx);
        this.views = views;
    }
    public $Array(Activity ctx, String xml, AQuery parent, boolean append) {
        super(ctx);
        try {
            this.views = parseXML(xml, parent, append);
        }
        catch (XmlPullParserException e) {
            throw new IllegalArgumentException(e);
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
    public $Array(Activity ctx, String xml, AQuery parent) {
        this(ctx, xml, parent, true);
    }
    public $Array(Activity ctx, String xml, View parent) {
        this(ctx, xml, new $Element(ctx, parent));
    }
    public $Array(Activity ctx, String xml, ViewParent parent) {
        this(ctx, xml, new $Element(ctx, (View) parent));
    }
    public $Array(Activity ctx, String xml, View parent, boolean append) {
        this(ctx, xml, new $Element(ctx, parent), append);
    }
    public $Array(Activity ctx, String xml, ViewParent parent, boolean append) {
        this(ctx, xml, new $Element(ctx, (View) parent), append);
    }

    @Override
    public View head() {
        return views.get(0);
    }

    @Override
    public List<View> list() {
        return views;
    }

    public void add(View v) {
        views.add(v);
    }
    public void add(int id, View v) {
        views.add(id, v);
    }
    public void addAll($Array a) {
        addAll(a.list());
    }
    public void addAll(List<View> a) {
        views.addAll(a);
    }
    public void set(int id, View v) {
        views.set(id, v);
    }
    public void remove(int id) {
        views.remove(id);
    }
    public void remove(View v) {
        views.remove(v);
    }

    private ArrayList<View> parseXML(String xml, AQuery parent, boolean append) throws XmlPullParserException, IOException {
        if (null == parent)
            append = false;
        else if (null == parent.head()) {
            parent = null;
            append = false;
        }
        ArrayList<View> res = new ArrayList<>();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(xml));
        int eventType = xpp.getEventType();
        AbstractCollection<AQuery> nodes;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
            nodes = new ArrayDeque<>();
        else
            nodes = new ArrayList<>();
        while (eventType != XmlResourceParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlResourceParser.START_TAG:
                    AQuery elt = $Utils.create(ctx, xpp.getName());
                    AQuery node = parent;
                    if (!nodes.isEmpty())
                        node = getLast(nodes);
                    elt.lp(getSuitableLayoutParams(node));
                    for (int i=0;i<xpp.getAttributeCount();i++) {
                        try {
                            elt.attr(xpp.getAttributeName(i), xpp.getAttributeValue(i));
                        }
                        catch (IllegalArgumentException e) {
                        }
                    }
                    nodes.add(elt);
                    if (append || (node != parent))
                        node.append(elt);
                    if (node == parent)
                        res.add(elt.head());
                    break;
                case XmlPullParser.END_TAG:
                    removeLast(nodes);
                    break;
            }
            eventType = xpp.next();
        }
        return res;
    }
    private static ViewGroup.LayoutParams getSuitableLayoutParams(AQuery parent) {
        if (null == parent)
            return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        return getSuitableLayoutParams(parent.head());
    }
    @SuppressWarnings("deprecation")
    private static ViewGroup.LayoutParams getSuitableLayoutParams(View parent) {
        ViewGroup.LayoutParams res;
        int wLP = ViewGroup.LayoutParams.WRAP_CONTENT, hLP = ViewGroup.LayoutParams.WRAP_CONTENT, xLP = 0, yLP = 0;
        if (parent instanceof RelativeLayout)
            res = new RelativeLayout.LayoutParams(wLP,hLP);
        else if (parent instanceof TableLayout)
            res = new TableLayout.LayoutParams(wLP,hLP);
        else if (parent instanceof TableRow)
            res = new TableRow.LayoutParams(wLP,hLP);
        else if (parent instanceof LinearLayout)
            res = new LinearLayout.LayoutParams(wLP,hLP);
        else if (parent instanceof GridLayout) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                res = new GridLayout.LayoutParams();
            else
                res = new ViewGroup.LayoutParams(wLP,wLP);
        }
        else if (parent instanceof FrameLayout)
            res = new FrameLayout.LayoutParams(wLP,hLP);
        else if (parent instanceof LinearLayoutCompat)
            res = new LinearLayoutCompat.LayoutParams(wLP,hLP);
        else if (parent instanceof DrawerLayout)
            res = new DrawerLayout.LayoutParams(wLP,hLP);
        else if (parent instanceof SlidingPaneLayout)
            res = new SlidingPaneLayout.LayoutParams(wLP,hLP);
        else if (parent instanceof SwipeRefreshLayout)
            res = new SwipeRefreshLayout.LayoutParams(wLP,hLP);
        else if (parent instanceof ViewPager)
            res = new ViewPager.LayoutParams();
        else if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) && (parent instanceof Toolbar)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                res = new Toolbar.LayoutParams(wLP, hLP);
            else
                res = new ViewGroup.LayoutParams(wLP,wLP);
        }
        else if (parent instanceof PagerTitleStrip)
            res = new PagerTitleStrip.LayoutParams(wLP,hLP);
        else if (parent instanceof SlidingDrawer)
            res = new SlidingDrawer.LayoutParams(wLP,hLP);
        else if (parent instanceof FragmentBreadCrumbs)
            res = new FragmentBreadCrumbs.LayoutParams(wLP,hLP);
        else if (parent instanceof AbsoluteLayout)
            res = new AbsoluteLayout.LayoutParams(wLP,hLP, xLP,yLP);
        else
            res = new ViewGroup.LayoutParams(wLP,hLP);
        return res;
    }
    private static <T> void removeLast(AbstractCollection<T> a) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
            ((ArrayDeque<T>) a).removeLast();
        else {
            List<T> la = (List<T>) a;
            la.remove(la.size()-1);
        }
    }
    private static <T> T getLast(AbstractCollection<T> a) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
            return ((ArrayDeque<T>) a).getLast();
        else {
            List<T> la = (List<T>) a;
            return la.get(la.size()-1);
        }
    }
}
