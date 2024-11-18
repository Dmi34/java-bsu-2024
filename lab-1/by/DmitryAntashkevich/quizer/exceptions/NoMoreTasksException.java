package by.DmitryAntashkevich.quizer.exceptions;

public class NoMoreTasksException extends RuntimeException {
    public NoMoreTasksException() {
        super("No more tasks available: test has already finished");
    }
}
