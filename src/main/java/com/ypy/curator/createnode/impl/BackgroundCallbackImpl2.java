package com.ypy.curator.createnode.impl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorEventType;

import java.util.concurrent.CountDownLatch;

/**
 * 用于异步接受回调的信息
 */
public class BackgroundCallbackImpl2 implements BackgroundCallback {

    private int index;

    private CountDownLatch countDownLatch;

    public BackgroundCallbackImpl2(int index, CountDownLatch countDownLatch){
        this.index = index;
        this.countDownLatch = countDownLatch;
    }

    /**
     *
     * @param client 当前的会话
     * @param event 回调事件，提供回调的相关信息，包括事件处理的结果编码，以及事件类型
     * @throws Exception
     */
    @Override
    public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {

        if(index == 2 || index ==3){
            Thread.sleep(5000);
        }

        System.out.println("第 " + index + " 个节点创建的回调。");

        // 回调事件的结果编码
        // 0:表示事件执行成功
        // -4:链接失效、
        // -110:节点已经存在等
        int resultCode = event.getResultCode();

        // 获取事件的类型：创建节点、更新节点数据、删除节点等
        CuratorEventType curatorEventType = event.getType();

//        System.out.println(resultCode);
//        System.out.println(curatorEventType);
        System.out.println(Thread.currentThread().getName());

        // 计数器减1
        this.countDownLatch.countDown();;
    }
}
