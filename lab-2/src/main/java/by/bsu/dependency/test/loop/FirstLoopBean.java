package by.bsu.dependency.test.loop;

import by.bsu.dependency.annotation.Inject;

public class FirstLoopBean {

    @Inject
    private SecondLoopBean secondLoopBean;
}