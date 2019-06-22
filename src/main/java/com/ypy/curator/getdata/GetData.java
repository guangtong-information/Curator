package com.ypy.curator.getdata;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * 04 获取节点数据
 */
public class GetData {

    public static void main(String[] args) throws Exception{

        String path = "/zk-book";

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

        client.start();

        client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path, "init".getBytes());

        /**
         * 注意：client.getData().forPath(path) :返回的是节点byte[]
         */
        System.out.println(client.getData().forPath(path));
        System.out.println(new String(client.getData().forPath(path)));

        // 用于存储节点状态信息
        Stat stat = new Stat();
        /**
         * 读取一个节点数据，同时获取该节点的stat
         * 注意：
         * Curator通过传入一个旧的stat对象，用于存储服务端返回的最新节点状态信息
         */
        System.out.println(new String(client.getData().storingStatIn(stat).forPath(path)));
        System.out.println(stat.toString());

        Thread.sleep(Integer.MAX_VALUE);
    }

}
