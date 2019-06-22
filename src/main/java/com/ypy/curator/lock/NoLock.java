package com.ypy.curator.lock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * 09-01 分布式锁
 * 在没有锁的情况下，并发的时候，生成的序列号可能一样。
 *
 * 业务场景：生成业务流水号，需要保证在大并发的情况下，流水号也不重复。
 */
public class NoLock {

    public static void main(String[] args) {
        final CountDownLatch down = new CountDownLatch(1);
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        down.await(); // 模拟并发场景
                    } catch (Exception e) {
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
                    String orderNo = sdf.format(new Date());
                    System.out.println("生成的订单号是 : " + orderNo);
                }
            }).start();
        }
        down.countDown();
    }
}
