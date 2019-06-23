package com.ypy.curator.masterselect;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Master选举
 *
 * 【测试验证】：
 *
 */
public class MasterSelect2 {

    public static void main(String[] args) throws Exception{

        // 选举的根节点，表明此次Master的选举，都在改节点下进行的
        String master_path = "/curator_recipes_master_path";

        // 第一步：创建会话
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

        client.start();

        // 第二步：开启选举
        // LeaderSelector封装了选举的动作（节点创建、事件监听、自动选举过程等）
        LeaderSelector leaderSelector = new LeaderSelector(client,master_path,new MyLeaderSelectorListener1());
        leaderSelector.autoRequeue();
        leaderSelector.start();
        Thread.sleep(Integer.MAX_VALUE);
    }

}

class MyLeaderSelectorListener1 extends LeaderSelectorListenerAdapter {
    // 获取Master权限
    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {

        System.out.println("成为Master角色");

        Thread.sleep(2000);

        System.out.println("释放Master权限");

    }

}
