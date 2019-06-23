package com.ypy.curator.createnode;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 使用异步的方式，创建节点
 *
 * */
public class CreatNodeByBackGround {

    public static void main(String[] args) throws Exception {
        // 创建一个只含有一个线程池数量的线程
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // 当5个节点都创建完成以后，释放锁，程序结束
        CountDownLatch countDownLatch = new CountDownLatch(5);

        String path = "/zk-book";

        // 第一步：创建会话
        CuratorFramework client = CuratorFrameworkFactory.
                builder().
                connectString("127.0.0.1:2181").
                retryPolicy(new ExponentialBackoffRetry(1000,3)).
                build();

        client.start();

        // 第二步：采用异步的方式，创建节点
        //inBackground：异步的含义：说明在当前主线程，启动了一个新的子线程去执行创建节点的动作
        for(int i=0;i<5;i++) {
            // 耗时长的任务，需要启动一个单独的线程池来处理
            if (i==2 || i==3) {
                client.create().
                        creatingParentsIfNeeded().
                        withMode(CreateMode.EPHEMERAL).
                        inBackground(new BackgroundCallbackImpl2(i,countDownLatch), executorService).
                        forPath(path + i,"init".getBytes());
            } else {
                client.create().
                        creatingParentsIfNeeded().
                        withMode(CreateMode.EPHEMERAL).
                        // 一个线程串行处理的，EventThread线程中处理；
                        //可以保证基本上都是按照先后顺序，处理业务。如果当中有一个任务，耗时很久，那么会导致剩下的任务被阻塞
                        //未了解决该问题，可以针对耗时长的任务，单独启动线程去执行
                                inBackground(new BackgroundCallbackImpl2(i, countDownLatch)).
                        forPath(path + i, "init".getBytes());
            }
        }


        // 阻塞，让主线程不退出
        // 这种方式不优雅
//        Thread.sleep(Integer.MAX_VALUE);

        // 优雅的方式：希望子线程，执行完以后自动的退出
        countDownLatch.await();
    }

}
