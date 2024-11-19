package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class BeanInfo {
    public final Class<?> beanClass;
    public final BeanScope scope;
    public final List<Field> dependencies;
    public final Field postConstruct;
    /// Only for {@code Singleton}
    public Object instance = null;

    BeanInfo(Class<?> beanClass) {
        this.beanClass = beanClass;
        this.scope = getScope(beanClass);
        this.dependencies = Arrays.stream(beanClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .toList();

        var post = Arrays.stream(beanClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(PostConstruct.class))
                .toList();

        if (post.size() > 1) throw new RuntimeException("Bean has more then one PostConstruct field");
        this.postConstruct = post.isEmpty() ? null : post.get(0);
    }

    public static String getName(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Bean.class)) return clazz.getAnnotation(Bean.class).name();
        String name = clazz.getSimpleName();
        name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        return name;
    }

    public static BeanScope getScope(Class<?> clazz) {
        return clazz.isAnnotationPresent(Bean.class) ? clazz.getAnnotation(Bean.class).scope() : BeanScope.SINGLETON;
    }
}