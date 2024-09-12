package u02.exam;

import java.util.function.Consumer;

public interface Logger {
    void log(String message);
    void handle(Consumer<String> handler);
}
