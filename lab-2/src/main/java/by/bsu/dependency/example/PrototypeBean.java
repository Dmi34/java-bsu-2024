package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.PostConstruct;

@Bean(name = "counter", scope = BeanScope.PROTOTYPE)
public class PrototypeBean {

    public static int counter = 0;

    @PostConstruct
    public void postConstruct() {
        counter++;
    }
}