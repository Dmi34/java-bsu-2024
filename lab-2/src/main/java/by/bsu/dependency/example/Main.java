package by.bsu.dependency.example;

import by.bsu.dependency.context.ApplicationContext;
import by.bsu.dependency.context.SimpleApplicationContext;

public class Main {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new SimpleApplicationContext(
                FirstBean.class, OtherBean.class, PrototypeBean.class
        );
        applicationContext.start();

        FirstBean firstBean = (FirstBean) applicationContext.getBean("firstBean");
        OtherBean otherBean = (OtherBean) applicationContext.getBean(OtherBean.class);

        for (int i = 0; i < 10; i++) {
            PrototypeBean prototypeBean = (PrototypeBean) applicationContext.getBean("counter");
        }

        firstBean.doSomething();
        otherBean.doSomething();

        System.out.println("PostConstruct executions in PrototypeBean: " + PrototypeBean.counter);

        otherBean.doSomethingWithFirst();
    }
}
