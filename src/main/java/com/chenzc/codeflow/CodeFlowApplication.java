package com.chenzc.codeflow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.chenzc.codeflow.mapper")
public class CodeFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeFlowApplication.class, args);
    }

}
