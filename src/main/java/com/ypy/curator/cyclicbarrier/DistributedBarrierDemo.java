package com.ypy.curator.cyclicbarrier;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 10-2 DistributedBarrier分布式栅栏
 *
 * 手动释放栅栏：barrier.removeBarrier();
 */
public class DistributedBarrierDemo {

    static DistributedBarrier barrier;

    public static void main(String[] args) throws Exception{

        for (int index=0;index<5;index++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String barrier_path = "/curator_recipes_barrier_path";
                        CuratorFramework client = CuratorFrameworkFactory.builder()
                                .connectString("127.0.0.1:2181")
                                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
                        client.start();

                        barrier = new DistributedBarrier(client, barrier_path);
                        System.out.println(Thread.currentThread().getName() + " 号barrier设置完成！");
                        // 完成栅栏Barrier的设置
                        barrier.setBarrier();
                        // 等待栅栏Barrier的释放
                        barrier.waitOnBarrier();
                        System.out.println(Thread.currentThread().getName() + " 启动....");
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }).start();
        }
        // 等待5个线程，全部完成barrier的设置
        Thread.sleep(2000);
        // 释放栅栏Barrier，同时触发所有等待该栅栏Barrier的5个线程，同时进行处理各自业务逻辑
        barrier.removeBarrier();
    }

}
