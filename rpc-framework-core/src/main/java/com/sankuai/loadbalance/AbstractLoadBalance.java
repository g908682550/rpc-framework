package com.sankuai.loadbalance;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public String selectSerivceAddress(List<String> serviceAddresses) {
        if(serviceAddresses==null||serviceAddresses.size()==0){
            return null;
        }
        if(serviceAddresses.size()==1){
            return serviceAddresses.get(0);
        }
        return doSelect(serviceAddresses);
    }

    protected abstract String doSelect(List<String> serviceAddresses);
}
