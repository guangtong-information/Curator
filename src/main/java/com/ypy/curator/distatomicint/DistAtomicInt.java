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
 * 09 分布式计数器
 *
 * 场景：分布式计数器，用于统计在线人数
 *
 * 实现原理：指定一个zk数据节点作为计数器，多个应用实例在分布式锁的控制下，通过更新该数据节点的内容来实现计数功能！
 *
 * 【注意】测试方法：启动多个应用程序，观察输出结果。
 */
public class DistAtomicInt {

    public static void main(String[] args) throws Exception {

        String distatomicint_path = "/curator_recipes_distatomicint_path";

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

        client.start();

        // 指定计数器存放路径和重试次数
        DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(client, distatomicint_path,
                new RetryNTimes(3, 1000));

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i=0;i<10;i++) {
            executorService.submit(new Increment(atomicInteger));
        }
        executorService.shutdown();

    }
}

class Increment implements Runnable{

    private DistributedAtomicInteger distributedAtomicInteger;

    public Increment(DistributedAtomicInteger distributedAtomicInteger) {
        this.distributedAtomicInteger = distributedAtomicInteger;
    }

    @Override
    public void run() {
        try {
            AtomicValue<Integer> value = distributedAtomicInteger.increment();
//            System.out.println("原值为" + value.preValue());
            System.out.println("更改后的值为" + value.postValue());
            System.out.println("状态" + value.succeeded());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
