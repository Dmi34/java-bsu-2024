package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(name = "loop2", scope = BeanScope.PROTOTYPE)
public class SecondLoopBean {

    @Inject
    private FirstLoopBean firstLoopBean;
}