package com.sankuai.loadbalance;

import java.util.List;

public interface LoadBalance {

    String selectSerivceAddress(List<String> serviceAddresses);

}
