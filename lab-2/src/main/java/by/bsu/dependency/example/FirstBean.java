package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.PostConstruct;

@Bean(name = "firstBean")
public class FirstBean {

    private boolean isPostConstructCalled = false;

    void printSomething() {
        System.out.println("Hello, I'm first bean");
    }

    void doSomething() {
        System.out.println("First bean is working on a project...");
    }

    @PostConstruct
    void postConstruct() {
        System.out.println("First bean post construct");
        isPostConstructCalled = true;
    }

    public boolean isPostConstructCalled() {
        return isPostConstructCalled;
    }
}