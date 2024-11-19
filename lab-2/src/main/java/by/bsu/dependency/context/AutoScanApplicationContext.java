package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.Set;

public class AutoScanApplicationContext extends AbstractApplicationContext {

    /**
     * Создает контекст, содержащий классы из пакета {@code packageName}, помеченные аннотацией {@code @Bean}.
     * <br/>
     * Если имя бина в анноации не указано ({@code name} пустой), оно берется из названия класса.
     * <br/>
     * Подразумевается, что у всех классов, переданных в списке, есть конструктор без аргументов.
     *
     * @param packageName имя сканируемого пакета
     */
    public AutoScanApplicationContext(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> set = reflections.getTypesAnnotatedWith(Bean.class);
        init(new ArrayList<>(set));
    }
}
