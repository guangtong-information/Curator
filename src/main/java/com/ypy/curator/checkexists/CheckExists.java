package com.ypy.curator.checkexists;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * 检查节点是否存在
 */
public class CheckExists {

    public static void main(String[] args) throws Exception{

        String path = "/zk-book";

        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181").retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

        curatorFramework.start();

        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path,"init".getBytes());

        Stat stat = curatorFramework.checkExists().forPath(path);

        // Stat就是对zonde所有属性的一个映射， stat=null表示节点不存在！
        System.out.println(stat);
    }

}
