package com.ling.client;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author by PaPaXiong on 2019/10/28 20:54.
 */
public class CuratorClient {
    private final static Logger log = LoggerFactory.getLogger(CuratorClient.class);
    private CuratorFramework curatorFramework;
    private final String connectionString;
    private final RetryPolicy retryPolicy;
    private final int connectionTimeoutMs;
    private final int sessionTimeoutMs;

    private final int BASE_SLEEP_TIMEMS = 1000;
    private final int MAX_RETRIES =3;
    private final int CONNECTION_TIMEOUTMS = 1000;
    private final int SESSION_TIMEOUTMS = 5000;
    private final String NAMESPACE = "config";

    public CuratorClient(String connectionString) {
        this.connectionString = connectionString;
        this.retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIMEMS, MAX_RETRIES);
        this.connectionTimeoutMs = CONNECTION_TIMEOUTMS;
        this.sessionTimeoutMs = SESSION_TIMEOUTMS;
        this.start();
    }

    public CuratorClient(String connectionString, RetryPolicy retryPolicy, int connectionTimeoutMs, int sessionTimeoutMs) {
        this.connectionString = connectionString;
        this.retryPolicy = retryPolicy;
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.sessionTimeoutMs = sessionTimeoutMs;
        this.start();
    }

    private void start(){
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(connectionString)
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(connectionTimeoutMs)
                .sessionTimeoutMs(sessionTimeoutMs)
                .namespace(NAMESPACE)
                .build();
        curatorFramework.start();
    }

    public boolean isNodeExist(String path){
        try {
            Stat stat = curatorFramework.checkExists().forPath(path);
            if (stat != null){
                return true;
            }
        } catch (Exception e) {
            log.error("isNodeExist path :{}", path, e);
            return false;
        }
        return false;
    }

    public boolean createNode(String path){
        try {
            String stat = curatorFramework.create().forPath(path);
            if (stat != null){
                return true;
            }
        } catch (Exception e) {
            log.error("create path :{}", path, e);
            return false;
        }
        return false;
    }

    public boolean createNode(String path, String data){
        try {
            String stat = curatorFramework.create().forPath(path, data.getBytes());
            System.out.println("stat: " + stat);
            if (stat != null){
                return true;
            }
        } catch (Exception e) {
            log.error("create path :{}", path, e);
            return false;
        }
        return false;
    }

    public void addWatcher(String path){
        final NodeCache nodeCache = new NodeCache(curatorFramework, path);
        //如果设置为true则在首次启动时就会缓存节点内容到Cache中。 nodeCache.start(true);
        try {
            nodeCache.start(true);
            nodeCache.getListenable().addListener(new NodeCacheListener() {
                @Override
                public void nodeChanged() throws Exception {
                    System.out.println("触发监听回调，当前节点数据为：" + new String(nodeCache.getCurrentData().getData()));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteNode(String path){
        try {
            curatorFramework.delete().forPath(path);
            return true;
        } catch (Exception e) {
            log.error("delete path :{}", path, e);
            return false;
        }

    }

    public String getNode(String path){
        try {
            byte[] data = curatorFramework.getData().forPath(path);
            return String.valueOf(data);
        } catch (Exception e) {
            log.error("delete path :{}", path, e);
            return null;
        }
    }

    public boolean updateNode(String path, String data){
        try {
            curatorFramework.setData().forPath(path, data.getBytes());
            return true;
        } catch (Exception e) {
            log.error("delete path :{}", path, e);
            return false;
        }
    }

    public boolean isStarted(){
        return this.curatorFramework.isStarted();
    }
}
