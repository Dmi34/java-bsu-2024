package by.bsu.dependency.test.loop;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(scope = BeanScope.PROTOTYPE)
public class SecondLoopBean {

    @Inject
    private FirstLoopBean firstLoopBean;
}