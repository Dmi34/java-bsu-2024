package by.DmitryAntashkevich.quizer.exceptions;

public class NonExistentQuestionException extends RuntimeException {
    public NonExistentQuestionException() {
        super("Attempt to answer a non-existent question");
    }
}
