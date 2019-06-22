package com.ypy.curator.createnode.impl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * BackgroundCallbackImpl：curator用来处理异步接口调用之后服务端返回的结果信息
 *
 * 注意：
 * 在zk中，所有的异步通知事件处理器都是有EventThread，EventThread线程使用串行处理所有的事件通知。可以保证事件处理的先后顺序。
 * 弊端：一旦碰上一个耗时比较久的业务处理单元，就会消耗过长的处理时间，从而影响其他事件的处理。
 * 解决方法：允许传入一个Executor示例，可以吧那些耗时比较久的事件处理放到专门的线程池中处理。参见：CreateNodeBackground2
 *
 */
public class BackgroundCallbackImpl implements BackgroundCallback {

    private CountDownLatch countDownLatch;

    private int index;

    public BackgroundCallbackImpl(CountDownLatch countDownLatch,int index){
        this.countDownLatch = countDownLatch;
        this.index = index;
    }

    /**
     * curatorFramework:当前客户端的示例
     * CuratorEvent：定义了zk服务端发送到客户端的一系列事件参数，其中比较重要的有事件类型（getType）和响应吗（getResultCode）二个参数！
     * getType：包含create、delete、exists、get_data、set_data等
     * getResultCode：0-OK、-4-链接失效、-110-节点已经存在等
     */
    public void processResult(CuratorFramework curatorFramework, CuratorEvent event) throws Exception {

        Thread.sleep(new Random().nextInt(3) * 1000);

        System.out.println("第" + index + "节点回调！");

        /**
         * code = 0，说明创建成功
         * code = -110，说明节点已经存在
         */
        System.out.println("event[code: " + event.getResultCode() + ", type: " + event.getType() + "]");

        /**
         * 当前处理的线程
         */
        System.out.println("Thread of processResult: " + Thread.currentThread().getName());

        countDownLatch.countDown();
    }
}
