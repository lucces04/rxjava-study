package com.mc.rxjava;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class CoreComponentStudy {

    private static final Logger log = LoggerFactory.getLogger(CoreComponentStudy.class);
    public static void main(String[] args) {
        log.info("RxJava 核心组件详解");

        // ==============================
        // 1. Observable - 数据源（发布者）
        // ==============================
        log.info("\n=== 1. Observable - 数据源 ===");

        // Observable.just() 创建发出有限个数据的 Observable
        Observable<String> stringObservable = Observable.just("数据1", "数据2", "数据3");

        // Observable.range() 创建发出连续整数的 Observable
        Observable<Integer> intObservable = Observable.range(1, 5);

        // Observable.create() 创建自定义数据流
        Observable<String> customObservable = Observable.create(emitter -> {
            try {
                emitter.onNext("自定义数据1");
                emitter.onNext("自定义数据2");
                // 模拟可能发生错误的情况
                if (Math.random() > 0.5) {
                    emitter.onNext("随机数据");
                }
                emitter.onComplete(); // 数据流结束
            } catch (Exception e) {
                emitter.onError(e); // 发生错误
            }
        });

        // ==============================
        // 2. Observer - 数据消费者（订阅者）
        // ==============================
        log.info("\n=== 2. Observer - 数据消费者 ===");

        // 完整的 Observer 实现
        Observer<String> stringObserver = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                log.info("字符串观察者订阅成功，Disposable: {}", d);
                // 可以在这里保存 Disposable 用于取消订阅
            }

            @Override
            public void onNext(String value) {
                log.info("接收到数据: {}", value);
            }

            @Override
            public void onError(Throwable e) {
                log.error("发生错误: {}", e.getMessage());
            }

            @Override
            public void onComplete() {
                log.info("数据流完成");
            }
        };

        // 简化的 Observer（使用 lambda）
        Observer<Integer> intObserver = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                log.info("整数观察者订阅成功");
            }

            @Override
            public void onNext(Integer value) {
                log.info("接收到数字: {}", value);
            }

            @Override
            public void onError(Throwable e) {
                log.error("数字流发生错误: {}", e.getMessage());
            }

            @Override
            public void onComplete() {
                log.info("整数数据流完成");
            }
        };

        customObservable.subscribe(ObserverBuilder.create());
        stringObservable.subscribe(ObserverBuilder.create());
        intObservable.subscribe(ObserverBuilder.create());
        // ==============================
        // 3. Operators - 操作符（数据转换和处理）
        // ==============================
        log.info("\n=== 3. Operators - 操作符 ===");

        // map 操作符 - 转换数据
        Observable<Integer> numbers = Observable.just(1, 2, 3, 4, 5);
        numbers.map(n -> n * n) // 将每个数字平方
                .subscribe(result -> log.info("平方结果: {}", result));

        // filter 操作符 - 过滤数据
        Observable<Integer> evenNumbers = Observable.range(1, 10)
                .filter(n -> n % 2 == 0); // 只保留偶数
        evenNumbers.subscribe(n -> log.info("偶数: {}", n));

        // flatMap 操作符 - 扁平化映射
        Observable.just("Hello", "World")
                .flatMap(str -> Observable.fromArray(str.split("")))
                .subscribe(ch -> log.info("字符: {}", ch));

        // ==============================
        // 4. Scheduler - 线程调度器
        // ==============================
        log.info("\n=== 4. Scheduler - 线程调度 ===");

        // subscribeOn - 指定 Observable 执行的线程
        // observeOn - 指定 Observer 接收数据的线程

        Observable.just("主线程任务")
                .subscribeOn(Schedulers.io()) // 在 IO 线程执行
                .observeOn(Schedulers.computation()) // 在计算线程接收
                .subscribe(data -> {
                    log.info("线程[{}] 处理: {}",
                            Thread.currentThread().getName(), data);
                });

        // 多次线程切换示例
        Observable.range(1, 3)
                .subscribeOn(Schedulers.newThread()) // 在新线程发射数据
                .doOnNext(i -> log.info("发射线程: {}", Thread.currentThread().getName()))
                .observeOn(Schedulers.io()) // 切换到 IO 线程处理
                .map(i -> i * 2)
                .doOnNext(i -> log.info("IO线程处理: {}", Thread.currentThread().getName()))
                .observeOn(Schedulers.computation()) // 再切换到计算线程
                .subscribe(i -> log.info("计算线程接收: {} on thread: {}",
                        i, Thread.currentThread().getName()));

        // 等待异步操作完成
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // ==============================
        // 组件间的关系和交互流程
        // ==============================
        log.info("\n=== 组件交互流程演示 ===");

        // 1. 创建 Observable（数据源）
        Observable<String> workflow = Observable.create(emitter -> {
            log.info("1. Observable 开始发射数据 on thread: {}",
                    Thread.currentThread().getName());
            emitter.onNext("数据A");
            emitter.onNext("数据B");
            emitter.onComplete();
        });

        // 2. 应用操作符进行数据变换
        workflow
                .subscribeOn(Schedulers.io()) // 3. 在 IO 线程执行数据发射
                .map(data -> { // 4. 使用 map 操作符转换数据
                    log.info("2. map 操作 on thread: {}", Thread.currentThread().getName());
                    return data.toLowerCase();
                })
                .observeOn(Schedulers.computation()) // 5. 切换到计算线程接收
                .filter(data -> { // 6. 使用 filter 操作符过滤数据
                    log.info("3. filter 操作 on thread: {}", Thread.currentThread().getName());
                    return data.contains("a");
                })
                // 7. 订阅 Observer（数据消费者）
                .subscribe(
                        data -> log.info("4. Observer 接收数据: {} on thread: {}",
                                data, Thread.currentThread().getName()),
                        error -> log.error("发生错误", error),
                        () -> log.info("5. 数据流完成")
                );

        // 等待完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("\n=== RxJava 核心组件总结 ===");
        log.info("1. Observable/Flowable: 数据的发射源，负责产生数据流");
        log.info("2. Observer/Subscriber: 数据的接收方，消费数据流");
        log.info("3. Operators: 数据处理和转换工具，如 map, filter, flatMap 等");
        log.info("4. Scheduler: 线程调度器，控制在哪个线程执行");
        log.info("整个流程：Observable -> Operators -> Observer，通过 subscribe() 连接");
    }
}