package u02.exam;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LoggerImpl implements Logger {

    private final List<String> messages = new ArrayList<>();
    private final List<Consumer<String>> handlers = new ArrayList<>();
    @Override
    public void log(final String message) {
        this.messages.add(message);
        this.handlers.forEach(h -> h.accept(message));
        System.out.println(message);
    }

    @Override
    public void handle(final Consumer<String> handler) {
        this.handlers.add(handler);
        this.messages.forEach(handler);
    }
}
