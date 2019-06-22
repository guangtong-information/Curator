package com.ypy.curator.nodecache;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * 07-1 节点的事件监听
 *
 * 当数据节点的内容发生变化的时候，就会回调该方法
 */
public class NodeCacheSample {

    public static void main(String[] args) throws Exception {

        String path = "/zk-book/nodecache";

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
        // 启动会话
        client.start();

        // 创建节点
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path,"init".getBytes());

        // 创建Node节点的监听,当数据节点的内容发生变化的时候，就会回调该方法
        // 注意：
        // （1）Cache是Curator中对事件监听的包装，其对事件的监听，可以近似看做本地缓存视图和远程zk视图的对比过程！
        // （2）如果设置为true，那么NodeCacha在第一次启动的时候，从zk上读取对应节点的数据内容，并保存到Cache中
        final NodeCache nodeCache = new NodeCache(client,path,false);
        nodeCache.start();
        // Node监听的实现类
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            public void nodeChanged() throws Exception {
                System.out.println("Node data update, new data: " + new String(nodeCache.getCurrentData().getData()));
            }
        });

        client.setData().forPath(path,"init-new".getBytes());

        Thread.sleep(1000);

        client.delete().deletingChildrenIfNeeded().forPath(path);// 节点删除是不会触发事件监控回调

        Thread.sleep(Integer.MAX_VALUE);
    }

}
