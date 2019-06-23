package com.ypy.curator.cyclicbarrier;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CyclicBarrier演示示例 Jdk1.5提供的
 */
public class CyclicBarrierDemo2 {

    // 创建一个CyclicBarrier栅栏,屏障拦截的5个线程
    public static CyclicBarrier cyclicBarrier = new CyclicBarrier(5);

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i=0;i<5;i++) {
            executorService.submit(new Runner2(i + " 号选手"));
        }
        executorService.shutdown();
    }

}

class Runner2 implements Runnable{

    private String name;

    public Runner2 (String name) {
        this.name = name;
    }

    @Override
    public void run() {

        System.out.println(name + " 准备好了.");
        // 等待所有人全部准备好以后，才能继续往下执行
        // 告诉CyclicBarrier自己已经到达同步点，然后当前线程被阻塞
        try {
            CyclicBarrierDemo2.cyclicBarrier.await();
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(name + " 起跑！");
    }
}