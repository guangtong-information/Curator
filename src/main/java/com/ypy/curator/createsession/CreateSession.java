package com.ypy.curator.createsession;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.retry.RetryUntilElapsed;

/**
 * 01-1 创建会话
 *
 */
public class CreateSession {

    public static void main(String[] args) {

        // 重试策略1：重试3次，每次间隔1秒
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);

        // 重试策略2：最大重试3次，每次间隔1秒
        new RetryNTimes(3,1000);

        // 重试策略3：重试1次，间隔时间为1秒
        new RetryOneTime(1000);

        // 重试策略4：最大重试时间10秒，间隔时间为1秒
        new RetryUntilElapsed(10000,1000);

        // 创建回话
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1:2181",retryPolicy);

        curatorFramework.start();

        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
