package com.mc.rxjava;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObserverBuilder<T> {

    private static final Logger log = LoggerFactory.getLogger(ObserverBuilder.class);
    private Observer<T> observer;

    public static <T> Observer<T> create() {
        return new ObserverBuilder<T>().observer;
    }

    private ObserverBuilder() {
        observer = new Observer<T>() {

            @Override
            public void onSubscribe(@NonNull Disposable d) {

                log.info("数据订阅成功, {}", d.isDisposed());
            }

            @Override
            public void onNext(@NonNull T t) {

                log.info("接收到数据: {}", t);
            }

            @Override
            public void onError(@NonNull Throwable e) {

                log.error("发生错误: {}", e.getMessage());
            }

            @Override
            public void onComplete() {

                log.info("数据流完成");
            }
        };

    }

}
