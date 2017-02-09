package com.richfit.common_lib.flyrefresh;

/**
 * Created by monday on 15-5-19.
 */
public interface IPullHeader {
    void onPullProgress(PullHeaderLayout parent, int state, float progress);
}
