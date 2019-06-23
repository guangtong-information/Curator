package com.ypy.curator.nodecache;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * 子节点的监听（一级子节点）
 */
public class PathChildrenCacheSample2 {

    public static void main(String[] args) throws Exception {

        String path = "/zk-book";

        // 第一步：创建会话
        CuratorFramework client = CuratorFrameworkFactory
                                        .builder()
                                        .connectString("127.0.0.1:2181")
                                        .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                                        .sessionTimeoutMs(5000).build();
        client.start();

        // 第二步：定义并注册子节点的监听
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client,path,true);
        pathChildrenCache.start();

        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener(){
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case CHILD_ADDED: // 节点新增
                        System.out.println("CHILD_ADDED," + event.getData().getPath());
                        break;
                    case CHILD_UPDATED: // 节点变更
                        System.out.println("CHILD_UPDATED," + event.getData().getPath());
                        break;
                    case CHILD_REMOVED:// 节点删除
                        System.out.println("CHILD_REMOVED," + event.getData().getPath());
                        break;
                    default:
                        break;
                }
            }
        });

        // 第三步：对子节点操作，观察监听是否被调用
        client.create().withMode(CreateMode.PERSISTENT).forPath(path);//对应节点本身的变化是不会触发回调的

        Thread.sleep(1000);

        client.create().withMode(CreateMode.PERSISTENT).forPath(path + "/c1");

//        client.create().withMode(CreateMode.PERSISTENT).forPath(path + "/c1/c2");//不会触发回调函数，只能针对当前节点的子节点进行监听
        Thread.sleep(1000);

        client.setData().forPath(path + "/c1","init".getBytes());;// 更新子节点数据

        Thread.sleep(1000);
        client.delete().forPath(path + "/c1");// 删除子节点

        Thread.sleep(1000);
        client.delete().forPath(path);//不会触发回调函数

        Thread.sleep(Integer.MAX_VALUE);

    }

}
