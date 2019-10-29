package com.ling.client;

import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.zookeeper.WatchedEvent;

/**
 * @author by PaPaXiong on 2019/10/28 19:32.
 */
public class ZkConfiCenterFactory {

    public static void main(String[] args) {
        String connectionString = "127.0.0.1:2181";
        CuratorClient curatorClient = new CuratorClient(connectionString);

        String path = "/test";
        curatorClient.createNode(path, "data");
        System.out.println(curatorClient.getNode(path));
        // 获取当前客户端的状态
        boolean isZkCuratorStarted = curatorClient.isStarted();
        System.out.println("当前客户端的状态：" + (isZkCuratorStarted ? "连接中..." : "已关闭..."));
    }
    public static void testWatcher(){
        String connectionString = "127.0.0.1:2181";
        CuratorClient curatorClient = new CuratorClient(connectionString);
        String path = "/testWatcher";
        curatorClient.addWatcher(path);

    }
}
