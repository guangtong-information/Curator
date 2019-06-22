package com.ypy.curator.deletenode;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 03 删除节点
 */
public class DeleteNode {

    public static void main(String[] args) throws Exception{

        String path = "/zk-book";

        /**
         * 创建回话
         */
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

        client.start();

        /**
         * 注意：
         * （1）deletingChildrenIfNeeded 删除一个节点并递归删除所有的子节点
         * （2）guaranteed 保证一定可以删除节点（如果因为一些原因，例如网络，导致删除失败；curator后台会自动开启重试机制，删除节点，直到删除为止！）
         */
        client.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);

    }

}
