package com.ypy.curator.masterselect;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 08 Master选举
 *
 * 场景：
 * 在分布式系统中，经常会碰到这样的场景：对于一个复杂的任务，仅需要在集群中选举出一台服务器处理即可，诸如此类的分布式问题，我们称之为“master选举”。
 * 借助zk，我们可以轻松的实现master选举的功能。
 *
 * 选举的大体思路：
 * 选择一个根节点，例如/matser-select，多台机器同时向该节点创建一个子节点/matser-select/lock，利用zk的特性，最终只有一台创建成功，成功的那台机器就作为Master。
 *
 * 【注意】测试步骤：
 * 同时启动二台应用程序，仔细观察控制台输出，可以发现在一台服务器完成Master逻辑后，另外一个服务器的takeLeadership才会被调用。
 * 这就说明，当一个程序成为Master后，其他应用程序进入等待，直到当前Master挂了或者退出才开始选举新的Master！
 *
 * 注意：
 * 观察curator_recipes_master_path下的子节点
 */
public class MasterSelect {

    public static void main(String[] args) throws Exception {

        // 选举的根节点，表明此次Master的选举，都在改节点下进行的
        String master_path = "/curator_recipes_master_path";

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

        client.start();

        /**
         * Curatro使用LeaderSelector封装了所有和master选举相关的逻辑（节点创建、事件监听、自动选举过程等）
         * takeLeadership在获取master权利以后，会立刻释放master权利
         */
        /*LeaderSelector selector = new LeaderSelector(client, master_path, new LeaderSelectorListenerAdapter() {
            public void takeLeadership(CuratorFramework client) throws Exception {
                System.out.println("成为Master角色");
                Thread.sleep(3000);
                System.out.println("完成Master操作，释放Master权利");
            }
        });*/

        LeaderSelector selector = new LeaderSelector(client,master_path, new MyLeaderSelectorListener());

        // 开启选举
        selector.autoRequeue();
        selector.start();
        Thread.sleep(Integer.MAX_VALUE);
    }

}

class MyLeaderSelectorListener extends LeaderSelectorListenerAdapter{
    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {
        System.out.println("成为Master角色");
        Thread.sleep(3000);
        System.out.println("完成Master操作，释放Master权利");
    }
}
