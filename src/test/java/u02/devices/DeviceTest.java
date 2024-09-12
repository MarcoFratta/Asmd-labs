package u02.devices;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeviceTest {
    private Device device;
    private FailingPolicy policy;

    @BeforeEach
    void init() {
        this.policy = spy(mock(FailingPolicy.class));
        this.device = new StandardDevice(this.policy);
    }

    @Test
    @DisplayName("Device must specify a strategy")
    void testNonNullStrategy() {
        assertThrows(NullPointerException.class, () -> new StandardDevice(null));
    }

    @Test
    @DisplayName("Device is initially off")
    void testIsInitiallyOff() {
        assertFalse(this.device.isOn());
        verifyNoInteractions(this.policy);
    }

    @Test
    @DisplayName("Device can be switched on")
    void testCanBeSwitchedOn() {
        when(this.policy.attemptOn()).thenReturn(true);
        this.device.on();
        assertTrue(this.device.isOn());
        verify(this.policy).attemptOn();
    }

    @Test
    @DisplayName("Device can be switched off after being on")
    void testCanBeSwitchedOff() {
        when(this.policy.attemptOn()).thenReturn(true);
        this.device.on();
        assertTrue(this.device.isOn());
        this.device.off();
        assertFalse(this.device.isOn());
        verify(this.policy).attemptOn();
    }

    @Test
    @DisplayName("Device fail if can't be switched on")
    void testCanBeSwitchedOffAfterOn() {
        when(this.policy.attemptOn()).thenReturn(false);
        assertThrows(IllegalStateException.class, () -> this.device.on());
    }

    @Test
    @DisplayName("Device can be reset")
    void testCanBeReset() {
        when(this.policy.attemptOn()).thenReturn(true);
        this.device.on();
        this.device.reset();
        verify(this.policy).reset();
        assertFalse(this.device.isOn());
        verify(this.policy, times(1)).attemptOn();
    }
}
