package by.bsu.dependency.context;

import by.bsu.dependency.example.*;
import by.bsu.dependency.exceptions.*;
import by.bsu.dependency.test.loop.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SimpleApplicationContextTest {

    private ApplicationContext applicationContext;

    @BeforeEach
    void init() {
        applicationContext = new SimpleApplicationContext(FirstBean.class, OtherBean.class, PrototypeBean.class);
    }

    @Test
    void testIsRunning() {
        assertThat(applicationContext.isRunning()).isFalse();
        applicationContext.start();
        assertThat(applicationContext.isRunning()).isTrue();
    }

    @Test
    void testContextContainsNotStarted() {
        assertThrows(
                ApplicationContextNotStartedException.class,
                () -> applicationContext.containsBean("firstBean")
        );
    }

    @Test
    void testContextContainsBeans() {
        applicationContext.start();

        assertThat(applicationContext.containsBean("firstBean")).isTrue();
        assertThat(applicationContext.containsBean("otherBean")).isTrue();
        assertThat(applicationContext.containsBean("randomName")).isFalse();
    }

    @Test
    void testContextGetBeanNotStarted() {
        assertThrows(
                ApplicationContextNotStartedException.class,
                () -> applicationContext.getBean("firstBean")
        );
    }

    @Test
    void testGetBeanReturns() {
        applicationContext.start();

        assertThat(applicationContext.getBean("firstBean")).isNotNull().isInstanceOf(FirstBean.class);
        assertThat(applicationContext.getBean("otherBean")).isNotNull().isInstanceOf(OtherBean.class);
    }

    @Test
    void testGetBeanThrows() {
        applicationContext.start();

        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.getBean("randomName")
        );
    }

    @Test
    void testIsSingletonReturns() {
        assertThat(applicationContext.isSingleton("firstBean")).isTrue();
        assertThat(applicationContext.isSingleton("otherBean")).isTrue();
        assertThat(applicationContext.isSingleton("counter")).isFalse();
    }

    @Test
    void testIsSingletonThrows() {
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.isSingleton("randomName")
        );
    }

    @Test
    void testIsPrototypeReturns() {
        assertThat(applicationContext.isPrototype("firstBean")).isFalse();
        assertThat(applicationContext.isPrototype("otherBean")).isFalse();
        assertThat(applicationContext.isPrototype("counter")).isTrue();
    }

    @Test
    void testIsPrototypeThrows() {
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.isPrototype("randomName")
        );
    }

    @Test
    void testPostConstruct() {
        applicationContext.start();
        assertThat(((FirstBean) applicationContext.getBean("firstBean")).isPostConstructCalled()).isTrue();
    }

    @Test
    void testPrototype() {
        applicationContext.start();

        assertNotEquals(
                applicationContext.getBean("counter"),
                applicationContext.getBean("counter")
        );
    }

    @Test
    void testPrototypePostConstruct() {
        applicationContext.start();

        for (int i = 0; i < 100; i++) {
            applicationContext.getBean("counter");
            applicationContext.getBean(PrototypeBean.class);
        }

        assertEquals(PrototypeBean.counter, 200);
    }

    @Test
    void testDependencyLoops() {
        var apContext = new SimpleApplicationContext(FirstLoopBean.class, SecondLoopBean.class);
        assertThrows(
                DependencyLoopException.class,
                apContext::start
        );
    }
}