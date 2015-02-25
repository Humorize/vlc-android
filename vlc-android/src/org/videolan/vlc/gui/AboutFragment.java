/*****************************************************************************
 * AboutActivity.java
 *****************************************************************************
 * Copyright © 2011-2012 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package org.videolan.vlc.gui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.android.widget.SlidingTabLayout;

import org.videolan.vlc.BuildConfig;
import org.videolan.vlc.R;
import org.videolan.vlc.gui.audio.AudioPagerAdapter;
import org.videolan.vlc.util.Util;
import org.videolan.vlc.widget.FlingViewGroup;

import java.util.ArrayList;

public class AboutFragment extends Fragment {
    public final static String TAG = "VLC/AboutActivity";

    public final static int MODE_ABOUT = 0;
    public final static int MODE_LICENCE = 1;
    public final static int MODE_TOTAL = 2; // Number of audio browser modes

    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("VLC " + BuildConfig.VERSION_NAME);

        View v = inflater.inflate(R.layout.about, container, false);

        View aboutMain = v.findViewById(R.id.about_main);
        WebView t = (WebView)v.findViewById(R.id.webview);
        String revision = getString(R.string.build_revision);
        t.loadData(Util.readAsset("licence.htm", "").replace("!COMMITID!",revision), "text/html", "UTF8");

        TextView link = (TextView) v.findViewById(R.id.main_link);
        link.setText(Html.fromHtml(this.getString(R.string.about_link)));

        String builddate = getString(R.string.build_time);
        String builder = getString(R.string.build_host);

        TextView compiled = (TextView) v.findViewById(R.id.main_compiled);
        compiled.setText(builder + " (" + builddate + ")");
        TextView textview_rev = (TextView) v.findViewById(R.id.main_revision);
        textview_rev.setText(getResources().getString(R.string.revision) + " " + revision + " (" + builddate + ")");

        final ImageView logo = (ImageView) v.findViewById(R.id.logo);
        logo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationSet anim = new AnimationSet(true);
                RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(800);
                rotate.setInterpolator(new DecelerateInterpolator());
                anim.addAnimation(rotate);
                logo.startAnimation(anim);
            }
        });

        ArrayList<View> lists = new ArrayList<>();
        lists.add(aboutMain);
        lists.add(t);
        String[] titles = new String[] {getString(R.string.about), getString(R.string.licence)};
        mViewPager = (ViewPager) v.findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(MODE_TOTAL-1);
        mViewPager.setAdapter(new AudioPagerAdapter(lists, titles));

        mSlidingTabLayout = (SlidingTabLayout) v.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

        return v;
    }

    private static class DummyContentFactory implements TabHost.TabContentFactory {
        private final Context mContext;
        public DummyContentFactory(Context ctx) {
            mContext = ctx;
        }
        @Override
        public View createTabContent(String tag) {
            View dummy = new View(mContext);
            return dummy;
        }
    }

    private void addNewTab(TabHost tabHost, String tag, String title) {
        DummyContentFactory dcf = new DummyContentFactory(tabHost.getContext());
        TabSpec tabSpec = tabHost.newTabSpec(tag);
        tabSpec.setIndicator(getNewTabIndicator(tabHost.getContext(), title));
        tabSpec.setContent(dcf);
        tabHost.addTab(tabSpec);
    }

    @SuppressLint("InflateParams")
    private View getNewTabIndicator(Context context, String title) {
        View v = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        TextView tv = (TextView) v.findViewById(R.id.textView);
        tv.setText(title);
        return v;
    }
}
