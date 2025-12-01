package com.mc.rxjava;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RxJava 编程思想学习计划，本类用于学习和实践 RxJava 的核心编程思想和概念
 *      * RxJava 编程思想学习列表
 *      *
 *      * 1. 响应式编程基础概念
 *      *    - 什么是响应式编程
 *      *    - 观察者模式 vs 响应式流
 *      *    - 数据流和变化传播
 *      *
 *      * 2. RxJava 核心组件
 *      *    - Observable/Flowable - 数据源
 *      *    - Observer/Subscriber - 数据消费者
 *      *    - Operators - 数据转换和处理
 *      *    - Scheduler - 线程调度
 *      *
 *      * 3. 核心编程思想
 *      *    - 异步数据流处理
 *      *    - 函数式编程范式
 *      *    - 声明式编程风格
 *      *    - 背压处理机制
 *      *
 *      * 4. 关键特性理解
 *      *    - Hot vs Cold Observables
 *      *    - 生命周期管理
 *      *    - 错误处理策略
 *      *    - 资源清理和取消机制
 *      *
 *      * 5. 实际应用场景
 *      *    - UI事件处理
 *      *    - 网络请求处理
 *      *    - 数据库操作
 *      *    - 复杂事件处理
 *      *
 *      * 6. 最佳实践
 *      *    - 内存泄漏避免
 *      *    - 线程切换优化
 *      *    - 操作符合理组合
 *      *    - 单元测试编写
 *      *
 *      * 7. 高级主题
 *      *    - Subject 和 Processor
 *      *    - Backpressure 策略
 *      *    - 自定义操作符
 *      *    - 与其他框架集成
 *      *
 *
 */
public class RxThinking {

    private static final Logger log = LoggerFactory.getLogger(RxThinking.class);

    public static void main(String[] args) {
        log.info("开始 RxJava 编程思想学习之旅！");

        // 第一步：创建基础的 Observable 示例
        log.info("\n=== 第一步：创建基础的 Observable 示例 ===");

        // 1. 创建一个简单的 Observable（数据源）
        Observable<String> observable = Observable.just("Hello", "RxJava", "World");
        Observable<Integer> observableInt = Observable.range(1, 10);

        // 2. 创建 Observer（数据消费者）
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                log.info("字符串类型观察者订阅成功，Disposable: " + d);
            }

            @Override
            public void onNext(String value) {
                log.info("接收到字符串类型数据: " + value);
            }

            @Override
            public void onError(Throwable e) {
                log.error("字符串发生错误: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                log.info("字符串类型数据流完成");
            }
        };
        Observer<Integer> observerInt = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                log.info("计数类型观察者订阅成功，Disposable: " + d);
            }

            @Override
            public void onNext(Integer value) {
                log.info("接收到计数类型数据: " + value);
            }

            @Override
            public void onError(Throwable e) {
                log.error("计数类型发生错误: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                log.info("计数类型数据流完成");
            }
        };

        // 3. 订阅：连接 Observable 和 Observer
        log.info("开始订阅...");
        observable.subscribe(observer);
        observableInt.subscribe(observerInt);

        // 4. 使用 lambda 表达式简化写法
        log.info("\n使用 lambda 表达式:");
        Observable.just("Lambda", "表达式", "简化")
                .subscribe(
                        data -> log.info("接收到: " + data),  // onNext
                        error -> log.error("错误: " + error),   // onError
                        () -> log.info("完成")                 // onComplete
                );

        Observable.range(1, 5)
                .subscribe(
                        data -> log.info("计数: " + data)
                );

        // 5. 在不同线程上执行
        log.info("\n在不同线程上执行:");
        Observable.just("异步", "数据", "处理")
                .subscribeOn(Schedulers.io()) // 在 IO 线程上执行数据发射
                .observeOn(Schedulers.computation()) // 在计算线程上接收数据
                .subscribe(data -> {
                    log.info("计算线程[" + Thread.currentThread().getName() +
                            "] 接收到: " + data);
                });

        Observable.range(1, 5)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(data -> {
                    log.info("io线程[" + Thread.currentThread().getName() +
                            "] 接收到: " + data);
                });

        // 等待异步操作完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error("", e);
        }

        // 6. 创建自定义 Observable
        log.info("\n自定义 Observable:");
        Observable.create(emitter -> {
            try {
                emitter.onNext("第一个数据");
                emitter.onNext("第二个数据");
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        }).subscribe(
                data -> log.info("自定义Observable: " + data),
                error -> log.error("自定义Observable错误: " + error),
                () -> log.info("自定义Observable完成")
        );

        log.info("\n第一步完成！已学习创建 Observable 和 Observer 的基本方法。");
    }
}
