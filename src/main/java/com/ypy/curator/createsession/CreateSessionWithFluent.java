package com.ypy.curator.createsession;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 01-2 创建会话，使用Fluent链式风格
 */
public class CreateSessionWithFluent {

    public static void main(String[] args) {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        // 使用链式风格的API接口来创建会话
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().
                                                                    connectString("127.0.0.1:2181").
                                                                    retryPolicy(retryPolicy).
                                                                    build();
        curatorFramework.start();

        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
