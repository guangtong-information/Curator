package com.ypy.curator.createnode;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * 02-01 创建节点
 */
public class CreateNode {

    public static void main(String[] args) throws Exception{
        String path = "/zk-book/c1";

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

        client.start();

        /**
         * 创建一个临时节点，内容为“init”
         *
         * 注意：
         * （1）creatingParentsIfNeeded 如果父节点不存在，将自动递归创建父节点
         * （2）zookeep中规定所有的非叶子节点必须为持久节点，所有只有path参数对应的数据节点是临时节点，其余节点都是持久节点
         */
        client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path, "init".getBytes());

        Thread.sleep(Integer.MAX_VALUE);
    }

}
