package com.ling.client;

import org.apache.curator.framework.CuratorFramework;

/**
 * @author by PaPaXiong on 2019/10/28 19:32.
 */
public class ZkConfiCenterFactory {

    private CuratorClient curatorClient;

    public CuratorClient buildCuratorClient(){
        if(curatorClient == null){
            synchronized (this){
                if (curatorClient == null){
                    curatorClient = new CuratorClient();
                }
            }
        }
        return curatorClient;
    }

}
