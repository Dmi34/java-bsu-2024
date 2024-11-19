package by.bsu.dependency.context;

import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.DependencyLoopException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        ACTIVE
    }

    private static class Node {
        public List<String> children = new ArrayList<>();
        public NodeStatus status = NodeStatus.NOT_VISITED;
    }

    protected final Map<String, BeanInfo> beanDefinitions;
    protected final Map<String, Object> singletons = new HashMap<>();
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

        beanDefinitions.forEach((name, beanInfo) -> beanInfo.dependencies.forEach(dependency -> {
            String dependencyName = BeanInfo.getName(dependency.getClass());
            graph.get(name).children.add(dependencyName);
        }));
    }

    @Override
    public void start() {
        validateDependencyGraph();

        beanDefinitions.forEach((name, beanInfo) -> {
            if (beanInfo.scope == BeanScope.SINGLETON) {
                singletons.put(name, instantiateBean(beanInfo));
            }
        });

        singletons.forEach((name, instance) -> injectDependencies(beanDefinitions.get(name), instance));

        singletons.forEach((name, instance) -> executePostConstruct(beanDefinitions.get(name), instance));

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
            return singletons.get(name);
        }

        BeanInfo beanInfo = beanDefinitions.get(name);
        var instance = instantiateBean(beanInfo);
        injectDependencies(beanInfo, instance);
        executePostConstruct(beanInfo, instance);
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
        if (!graph.isEmpty()) {
            dfs(graph.keySet().iterator().next());
        }
    }

    private void dfs(String name) {
        Node node = graph.get(name);
        if (node.status == NodeStatus.ACTIVE) {
            throw new DependencyLoopException(name);
        }

        if (node.status == NodeStatus.NOT_VISITED) {
            node.status = NodeStatus.ACTIVE;
            for (String dependency : node.children) {
                dfs(dependency);
            }
            node.status = NodeStatus.VISITED;
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

    private void injectDependencies(BeanInfo beanInfo, Object bean) {
        try {
            for (Field field : beanInfo.dependencies) {
                field.setAccessible(true);
                field.set(bean, getBean(field.getType()));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void executePostConstruct(BeanInfo beanInfo, Object bean) {
        if (beanInfo.postConstruct.isEmpty()) {
            return;
        }
        Method postConstruct = beanInfo.postConstruct.get();
        try {
            postConstruct.setAccessible(true);
            postConstruct.invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
