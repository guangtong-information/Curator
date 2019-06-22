package com.ypy.curator.cyclicbarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 10-1 CyclicBarrier 栅栏使用(Jdk1.5提供)
 *
 * 业务场景：模拟选手赛跑比赛
 *
 * 注意：在同一个JVM里面，使用CyclicBarrier完全可以解决类似的多线程同步问题；但是在分布式环境下，如何解决该问题？
 *
 * Curator提供了DistributedBarrier来实现分布式Barrier
 *
 * @author Ryan
 * @date 2019/6/22
 */
public class CyclicBarrierDemo {

    // 创建一个CyclicBarrier栅栏,屏障拦截的3个线程
    public static CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(new Thread(new Runner("1号选手")));
        executorService.submit(new Thread(new Runner("2号选手")));
        executorService.submit(new Thread(new Runner("3号选手")));
        executorService.shutdown();
    }
}

class Runner implements Runnable{

    private String name;

    public Runner(String name){
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println(name + " 准备好了.");
        try {
            // 告诉CyclicBarrier自己已经到达同步点，然后当前线程被阻塞
            CyclicBarrierDemo.cyclicBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        System.out.println(name + " 起跑！");

    }
}
