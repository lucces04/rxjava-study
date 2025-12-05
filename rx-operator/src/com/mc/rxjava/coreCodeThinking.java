package com.mc.rxjava;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class coreCodeThinking {

        private static final Logger log = LoggerFactory.getLogger(coreCodeThinking.class);
        public static void main(String[] args) {

        // 使用Flowable处理背压
        Flowable.range(1, 0)
//                .onBackpressureBuffer(1000)  // 缓冲策略
                .onBackpressureDrop(data -> log.info("数据被丢弃: " + data))
                .observeOn(Schedulers.io())
                .subscribe(data -> {
                    // 模拟慢速处理
                    Thread.sleep(1);
                    log.info("处理数据: " + data);
                },
                        error -> log.error("错误: " + error),
                        () -> log.info("完成")

        );

        // 暂停一段时间，留给我们观察消费情况
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.error("", e);
            }

            // 或者使用采样策略
        Flowable.interval(1, TimeUnit.MILLISECONDS)  // 快速发射
        .onBackpressureDrop()  // 丢弃策略
        .sample(1, TimeUnit.SECONDS)  // 采样每秒一次
        .subscribe(data -> log.info("采样数据: " + data), error -> log.error("错误: " + error), () -> log.info("完成"));

            // 暂停一段时间，留给我们观察消费情况
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.error("", e);
            }

        }
}
