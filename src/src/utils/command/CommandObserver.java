package src.utils.command;

public interface CommandObserver<T> {
    void command(final T args);
}
