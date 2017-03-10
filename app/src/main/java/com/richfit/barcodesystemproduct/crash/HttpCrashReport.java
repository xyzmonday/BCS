package com.richfit.barcodesystemproduct.crash;

import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.L;
import com.richfit.domain.bean.ResultEntity;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * 将崩溃日志上传到服务器
 * Created by monday on 2016/4/8.
 */
public class HttpCrashReport extends BaseCrashReport {

    public HttpCrashReport() {
    }


    @Override
    public void sendLogFileToTarget(Thread thread, Throwable ex,final File logFile) {
        ArrayList<ResultEntity> results = new ArrayList<>();
        ResultEntity result = new ResultEntity();
        result.transFileToServer = "DEBUG";
        result.imagePath = logFile.getAbsolutePath();
        results.add(result);
        if (mRepository == null) {
            closeApp(thread,ex);
            return;
        }
        mRepository.uploadMultiFiles(results)
                .compose(TransformerHelper.io2main())
                .subscribe(new ResourceSubscriber<String>() {
                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable t) {
                        L.e("onError = " + t.getMessage());
//                        deleteLogFile(logFile);
                        closeApp(thread,ex);
                    }

                    @Override
                    public void onComplete() {
                        L.d("onComplete");
//                        deleteLogFile(logFile);
                        closeApp(thread,ex);
                    }
                });
    }

    /**
     * 删除崩溃日志文件
     * @param file
     */
    private void deleteLogFile(final File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }
}
