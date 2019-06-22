package com.ypy.curator.createnode.countdownlatch;


import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 *  CountDownLatch 闭锁学习：
 *  CountDownLatch是一个非常实用的多线程控制工具类
 *  CountDown：倒计时
 *  Latch：门闩、插销
 *
 * 注意：
 * CountDownLatch(int count) //实例化一个倒计数器，count指定计数个数
 * countDown() // 计数减一
 * await() //等待，当计数减到0时，所有线程并行执行
 *
 * 业务场景：
 * 火箭发射前需要做一系列的准备工作，这些工作可以并行展开，准备工作全部完成以后，才能发射火箭！
 */
public class CountDownLatchDemo implements Runnable{

    static final CountDownLatch latch = new CountDownLatch(10);

    private int index;

    public CountDownLatchDemo(int index){
        this.index = index;
    }

    public void run() {
        try {
            Thread.sleep(new Random().nextInt(10) * 1000);
            System.out.println("第" + index +"项检查工作完成！");

            // 计数器减一
            latch.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        }

    }

    public static void main(String[] args) throws Exception{
       // 创建一个固定线程数量的线程池，同时并行处理任务
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i<10;i++) {
            executorService.submit(new CountDownLatchDemo(i));
        }

        // 等待检查（程序将会阻塞在这里，直到计数器为0）
        latch.await();

        System.out.println("发射火箭！");

        // 关闭线程池
        executorService.shutdown();
    }
}
