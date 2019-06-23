package com.ypy.curator.distatomicint;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 分布式计数器
 */
public class DistAtomicInt2 {

    public static void main(String[] args) throws Exception {

        String distatomicint_path = "/curator_recipes_distatomicint_path";

        //  1. 创建会话
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
        client.start();

        // 2.创建分布式计数器（原子性操作）
        DistributedAtomicInteger distributedAtomicInteger = new DistributedAtomicInteger(client,distatomicint_path,new RetryNTimes(3, 1000));

        // 3.创建一个线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i=0; i<100 ; i++) {
            executorService.submit(new Increment1(distributedAtomicInteger));
        }
        executorService.shutdown();

}

}

class Increment1 implements Runnable{

    private DistributedAtomicInteger distributedAtomicInteger;

    public Increment1 (DistributedAtomicInteger distributedAtomicInteger) {
        this.distributedAtomicInteger = distributedAtomicInteger;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(5000);
            AtomicValue<Integer> value = distributedAtomicInteger.add(10);
////            System.out.println("原值为" + value.preValue());
            System.out.println("更改后的值为" + value.postValue());
            System.out.println("状态" + value.succeeded());// 操作的状态，成功、失败
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}