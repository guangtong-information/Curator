package com.ypy.curator.setdata;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * 05 更新节点数据
 */
public class Setdata {

    public static void main(String[] args) throws Exception{

        String path = "/zk-book";

        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181").retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

        curatorFramework.start();

        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path,"init".getBytes());

        Stat stat = new Stat();
        // 获取节点数据和节点最新的状态信息
        System.out.println(new String(curatorFramework.getData().storingStatIn(stat).forPath(path)));

        // 强制使用指定版本，更新节点数据内容
        curatorFramework.setData().withVersion(stat.getVersion()).forPath(path,"init-new".getBytes());

        System.out.println(new String(curatorFramework.getData().forPath(path)));

        // 默认使用最新的版本，更新节点数据内容
        curatorFramework.setData().forPath(path,"init-new-1".getBytes());

        System.out.println(new String(curatorFramework.getData().forPath(path)));

        // 注意：不能使用过期节点状态，强制更新节点数据！
        curatorFramework.setData().withVersion(stat.getVersion()).forPath(path,"init-new-2".getBytes());

    }

}
