package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(name = "loop1", scope = BeanScope.SINGLETON)
public class FirstLoopBean {

    @Inject
    private SecondLoopBean secondLoopBean;
}