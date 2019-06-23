package com.ypy.curator.createnode.ThreadPool;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Ryan
 * @date 2019/6/23
 */
public class FixedThreadPoolDemo {

    // 闭锁。作用：等所有的检查工作完成以后，在执行火箭发射动作！
    // 10：内部有一个计数器，值为10，等到10变成0的时候，释放所
    static final CountDownLatch latch = new CountDownLatch(10);

    public static void main(String[] args) throws Exception{
        // 创建一个线程池，里面可包含10个线程
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for ( int i=0;i<10;i++){
            executorService.submit(new Runner(i,latch));
//            new Thread(new Runner(i)).start();
        }
        latch.await();
        System.out.println(Thread.currentThread().getName() + " 火箭发射！");
        executorService.shutdown();
    }
}

class Runner implements Runnable{
    private int index;
    private CountDownLatch latch;
    public Runner(int index,CountDownLatch latch){
        this.index = index;
        this.latch = latch;
    }
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() +  " 第 " + index + " 项检查工作完成！");
        this.latch.countDown();//计数器减1
    }
}


