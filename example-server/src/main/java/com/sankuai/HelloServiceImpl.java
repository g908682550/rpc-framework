package com.sankuai;

import com.sankuai.dto.HelloParam;

public class HelloServiceImpl implements HelloService{

    @Override
    public String hello(HelloParam helloParam) {
        return "server: "+helloParam.getMessage()+helloParam.getDescription();
    }
}
