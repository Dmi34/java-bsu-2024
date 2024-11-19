package by.bsu.dependency.exceptions;

public class DependencyLoopException extends RuntimeException {
    public DependencyLoopException(String name) {
        super("Dependency graph is cyclic, the cycle contains bean: " + name);
    }
}
