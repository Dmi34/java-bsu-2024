package by.DmitryAntashkevich.quizer.exceptions;

public class GroupGeneratorException extends RuntimeException {
    public GroupGeneratorException() {
        super("All generators failed generation");
    }
}
