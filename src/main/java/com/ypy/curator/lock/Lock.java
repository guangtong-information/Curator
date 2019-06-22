package com.ypy.curator.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * 09-02 分布式锁
 *
 * zookeeper实现分布式锁是通过在zk集群上的路径实现的,
 *
 * 在获取分布式锁的时候在zk服务器集群节点上创建临时顺序节点,
 *
 * 释放锁的时候删除该临时节点
 *
 * 【注意】测试方法：启动多个应用程序，观察输出情况！
 */
public class Lock {

    public static void main(String[] args) throws Exception{

        String lock_path = "/curator_recipes_lock_path";

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

        client.start();

        final InterProcessMutex lock = new InterProcessMutex(client,lock_path);

        final CountDownLatch down = new CountDownLatch(1);

        for(int i = 0; i < 30; i++){
            new Thread(new Runnable() {
                public void run() {
                    try {
                        down.await(); // 模拟大并发
                        lock.acquire(); // 获取锁
                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");

                    String orderNo = sdf.format(new Date());

                    System.out.println("生成的订单号是 : "+orderNo);

                    try {
                        Thread.sleep(1000); // 观察zk，curator_recipes_lock_path节点情况
                        lock.release();// 释放所
                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        Thread.sleep(2000);
        down.countDown();
    }

}
