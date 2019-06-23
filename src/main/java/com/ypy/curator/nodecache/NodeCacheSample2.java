package com.ypy.curator.nodecache;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 *  事件的监听-监听节点（当前节点）
 *  节点的数据变换的时候，会回调对应的方法!
 */
public class NodeCacheSample2 {

    public static void main(String[] args) throws Exception{
        String path = "/zk-book/nodecache";

        // 第一步：创建会话
        CuratorFramework client = CuratorFrameworkFactory.
                                    builder().
                                    connectString("127.0.0.1:2181").
                                    retryPolicy(new ExponentialBackoffRetry(1000,3)).
                                    build();
        client.start();

        // 第二步：创建节点
        client.create().
                creatingParentsIfNeeded().
                withMode(CreateMode.EPHEMERAL).
                forPath(path,"init".getBytes());


        // 第三步：定义监听对象NodeCache
        // false：启动的时候，不需要从zk上读取对应节点的数据内容，并保存到Cache中
        NodeCache nodeCache = new NodeCache(client,path,false);
        nodeCache.start();

        // 第四步：注册回调(当前节点数据变换的时候，会回调改方法)
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("Node data update, new data: " + new String(nodeCache.getCurrentData().getData()));
            }
        });

        // 第五步：更新节点数据(触发回调接口)
        client.setData().forPath(path,"inir-new".getBytes());

        Thread.sleep(1000);

        // 将节点删除，会触发回调吗？不会触发回调！
        client.delete().deletingChildrenIfNeeded().forPath(path);

        Thread.sleep(2000);
    }

}
