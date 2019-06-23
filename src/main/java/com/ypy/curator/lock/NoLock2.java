package com.ypy.curator.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ryan
 * @date 2019/6/23
 */
public class NoLock2 {
    public static void main(String[] args) {
        final CountDownLatch down = new CountDownLatch(1);
        String lock_path = "/curator_recipes_lock_path";

        // 首先创建会话
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
        client.start();
        // 创建zk锁
        final InterProcessMutex lock = new InterProcessMutex(client,lock_path);

        // 第一步：创建一个国定线程数据的线程池
        ExecutorService executorService  = Executors.newFixedThreadPool(10);
        // 第二步：同时去生成订单号
        for (int i=0; i<30; i++) {
            executorService.submit(new GenOrderNum(down,lock));
        }
        down.countDown();
        // 第三步：关闭线程
        executorService.shutdown();
    }
}

// 负责生成订单号
class GenOrderNum implements Runnable{
    private CountDownLatch down ;
    private InterProcessMutex lock;

    public GenOrderNum(CountDownLatch down,InterProcessMutex lock){
        this.down = down;
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            down.await();
            // 锁获取
            lock.acquire();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
            String orderNo = sdf.format(new Date());
            System.out.println("生成的订单号是 : " + orderNo);
            Thread.sleep(1000);
            //锁释放
            lock.release();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}