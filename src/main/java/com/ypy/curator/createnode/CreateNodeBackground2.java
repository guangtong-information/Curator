package com.ypy.curator.createnode;

import com.ypy.curator.createnode.impl.BackgroundCallbackImpl;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 06-2 异步创建节点
 */
public class CreateNodeBackground2 {

    public static void main(String[] args) throws Exception {

        ExecutorService tp = Executors.newFixedThreadPool(1);

        CountDownLatch countDownLatch = new CountDownLatch(5);

        CuratorFramework client = CuratorFrameworkFactory.builder().
                connectString("127.0.0.1:2181").
                sessionTimeoutMs(5000).
                retryPolicy(new ExponentialBackoffRetry(1000, 3)).
                build();

        // 启动会话
        client.start();

        // 使用异步方式创建节点
        for (int i = 0;i<5;i++) {
            if (i==3) {
                client.create().
                        creatingParentsIfNeeded().
                        withMode(CreateMode.EPHEMERAL).
                        inBackground(new BackgroundCallbackImpl(countDownLatch,i),tp).//使用自定义线程池，处理异步请求
                        forPath("/zk-book" + i, "init".getBytes());
            }else {
                client.create().
                        creatingParentsIfNeeded().
                        withMode(CreateMode.EPHEMERAL).
                        inBackground(new BackgroundCallbackImpl(countDownLatch, i)).
                        forPath("/zk-book" + i, "init".getBytes());
            }
        }

        // 等待检查（程序将会阻塞在这里，直到计数器为0）
        countDownLatch.await();

        // 关闭线程池
        tp.shutdown();
    }

}
