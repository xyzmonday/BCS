package com.richfit.data.net.http;


import com.richfit.common_lib.rxutils.RxManager;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 详细的OkIO的源码分析请看我后面的源码分析。简单地说OKIO的思想就是
 * 将数据（字节）采用链表的形式缓存起来，这样读些数据只需要将操作链表即可。
 */
public class ProgressResponseBody extends ResponseBody {
    public interface ProgressListener {
        void update(long bytesRead, long contentLength, boolean done);
    }

    private ResponseBody responseBody;
    private RxManager mRxManager;
    private ProgressListener mProgressListener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
        this.responseBody = responseBody;
        this.mProgressListener = progressListener;
    }

    public ProgressResponseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
        this.mRxManager = RxManager.getInstance();
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long bytesReaded = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                final long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                bytesReaded += bytesRead != -1 ? bytesRead : 0;
                if(mProgressListener != null)
                    mProgressListener.update(bytesReaded, responseBody.contentLength(), bytesRead == -1);
                mRxManager.post("download_event", new ProgressWrapper(contentLength(), bytesReaded));
                return bytesRead;
            }
        };
    }
}


