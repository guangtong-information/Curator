package com.ypy.curator.cyclicbarrier;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 10-3 DistributedBarrier分布式栅栏
 * 自动触发Barrier栅栏释放模式
 */
public class DistributedDoubleBarrierDemo {

    static String barrier_path = "/curator_recipes_barrier_path";

    public static void main(String[] args) {

        for (int i=0;i<5;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        CuratorFramework client = CuratorFrameworkFactory.builder()
                                .connectString("127.0.0.1:2181")
                                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
                        client.start();

                        // 和Jdk1.5CyclicBarrier非常类似，指定了进入Barrier栅栏的数值阀
                        DistributedDoubleBarrier distributedDoubleBarrier = new DistributedDoubleBarrier(client, barrier_path, 5);
                        Thread.sleep(3000);
                        System.out.println(Thread.currentThread().getName() + "号进入barrier");

                        // 调用enter以后，进入等待状态，准备进入。一旦准备进入的Barrier成员数为5个后，所有成员会被同时触发进入。
                        distributedDoubleBarrier.enter();
                        System.out.println(Thread.currentThread().getName() + "启动...");
                        Thread.sleep(3000);

                        // 调用enter以后，在次进入等待状态，准备退出。一旦准备进入的Barrier成员数为5个后，所有成员会被同时触发退出。
                        distributedDoubleBarrier.leave();
                        System.out.println(Thread.currentThread().getName() + "退出...");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

}
