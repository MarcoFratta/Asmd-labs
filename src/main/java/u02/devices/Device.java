package u02.devices;

public interface Device {
    void on() throws IllegalStateException;
    void off();
    boolean isOn();
    void reset();
}
