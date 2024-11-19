package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;
import jdk.jshell.spi.ExecutionControl;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;


public abstract class AbstractApplicationContext implements ApplicationContext {
    protected enum ContextStatus {
        NOT_STARTED,
        STARTED
    }

    private enum NodeStatus {
        NOT_VISITED,
        VISITED,
        USED
    }

    private static class Node {
        public List<String> children = new ArrayList<>();
        public NodeStatus status = NodeStatus.NOT_VISITED;
    }

    protected final Map<String, BeanInfo> beanDefinitions;
    protected ContextStatus status = ContextStatus.NOT_STARTED;
    private final Map<String, Node> graph = new HashMap<>();

    AbstractApplicationContext(Class<?>... beanClasses) {
        this(Arrays.asList(beanClasses));
    }

    AbstractApplicationContext(List<Class<?>> beanClasses) {
        this.beanDefinitions = beanClasses.stream().collect(
                Collectors.toMap(
                        BeanInfo::getName,
                        BeanInfo::new
                ));
    }

    @Override
    public void start() {
        validateDependencyGraph();

        beanDefinitions.values().stream()
                .filter(beanInfo -> beanInfo.scope == BeanScope.SINGLETON)
                .forEach(this::instantiateBean);

        beanDefinitions.values().stream()
                .filter(beanInfo -> beanInfo.scope == BeanScope.SINGLETON)
                .forEach(this::injectDependencies);

        beanDefinitions.values().stream()
                .filter(beanInfo -> beanInfo.scope == BeanScope.SINGLETON)
                .forEach(this::postConstruct);

        status = ContextStatus.STARTED;
    }

    @Override
    public boolean isRunning() {
        return status == ContextStatus.STARTED;
    }

    @Override
    public boolean containsBean(String name) {
        if (!isRunning()) {
            throw new ApplicationContextNotStartedException();
        }
        return beanDefinitions.containsKey(name);
    }

    @Override
    public Object getBean(String name) {
        if (!isRunning()) {
            throw new ApplicationContextNotStartedException();
        }
        if (!containsBean(name)) {
            throw new NoSuchBeanDefinitionException(name);
        }
        if (isSingleton(name)) {
            return beanDefinitions.get(name).instance;
        }
        // TODO
        return null;
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return clazz.cast(getBean(BeanInfo.getName(clazz)));
    }

    @Override
    public boolean isPrototype(String name) {
        if (!beanDefinitions.containsKey(name)) {
            throw new NoSuchBeanDefinitionException(name);
        }
        return beanDefinitions.get(name).scope == BeanScope.PROTOTYPE;
    }

    @Override
    public boolean isSingleton(String name) {
        if (!beanDefinitions.containsKey(name)) {
            throw new NoSuchBeanDefinitionException(name);
        }
        return beanDefinitions.get(name).scope == BeanScope.SINGLETON;
    }

    private void validateDependencyGraph() {
        beanDefinitions.forEach((beanName, beanInfo) -> {
            List<String> children = beanInfo.dependencies.stream()
                    .map(field -> BeanInfo.getName(field.getClass()))
                    .toList();
            graph.get(beanName).children.addAll(children);
        });

        if (!graph.isEmpty()) {
            dfs(graph.keySet().iterator().next());
        }
    }

    private void dfs(String name) {
        Node node = graph.get(name);
        switch (node.status) {
            case USED -> throw new RuntimeException("Detected loop in dependency graph");
            case NOT_VISITED -> {
                node.status = NodeStatus.USED;
                for (String dependency : node.children) {
                    dfs(dependency);
                }
                node.status = NodeStatus.VISITED;
            }
        }
    }

    private Object instantiateBean(BeanInfo beanInfo) {
        try {
            return beanInfo.beanClass.getConstructor().newInstance();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private void injectDependencies(BeanInfo beanInfo) {
        // TODO
    }

    private void postConstruct(BeanInfo beanInfo) {
        // TODO
    }
}
