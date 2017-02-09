package com.richfit.common_lib.IInterface;


/**
 * mvp的view层
 * Created by monday on 2016/9/30.
 */

public interface IView {
    /**
     * 网络连接超时重试.
     *
     * @param retryAction：重试的action
     */
    void networkConnectError(String retryAction);
}
