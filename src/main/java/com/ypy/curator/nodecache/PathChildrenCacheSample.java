package com.ypy.curator.nodecache;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * 07-02子节点的监听
 *
 * 备注：
 * （1）一旦该节点新增、删除子节点或者子节点的数据发生变更，就会回调PathChildrenCacheListener
 * （2）对应节点本身的变化是不会触发回调的
 * （2）和其他zk客户端一样，curator也无法对二级子节点进行监听
 */
public class PathChildrenCacheSample {

    public static void main(String[] args) throws Exception {
        String path = "/zk-book";
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .sessionTimeoutMs(5000).build();
        client.start();

        /**
         * 子节点变更的监听
         */
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client,path,true);
        pathChildrenCache.start();

        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
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

        client.create().withMode(CreateMode.PERSISTENT).forPath(path);//对应节点本身的变化是不会触发回调的

        Thread.sleep(1000);

        client.create().withMode(CreateMode.PERSISTENT).forPath(path + "/c1");

        Thread.sleep(1000);

        client.setData().forPath(path + "/c1","init".getBytes());

        Thread.sleep(1000);

        client.delete().forPath(path + "/c1");

        Thread.sleep(1000);

        client.delete().forPath(path);//对应节点本身的变化是不会触发回调的

        Thread.sleep(Integer.MAX_VALUE);
    }

}
