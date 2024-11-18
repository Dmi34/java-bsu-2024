package by.DmitryAntashkevich.quizer.exceptions;

public class PoolGeneratorException extends RuntimeException {
    public PoolGeneratorException() {
        super("No tasks to choose from");
    }
}
