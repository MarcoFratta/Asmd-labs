package u02.devices;

import java.util.stream.IntStream;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

public class StandardDeviceTest {

    private Device device;

    @Test
    @DisplayName("Device must specify a strategy")
    void testNonNullStrategy() {
        assertThrows(NullPointerException.class, () -> new StandardDevice(null));
    }

    @Nested
    class ShowcaseDummies {
        @Test
        @DisplayName("Device is initially off")
        void testInitiallyOff() {
            // an unused reference, a sort of "empty implementation"
            final FailingPolicy dummyFailingPolicy = mock(FailingPolicy.class);
            StandardDeviceTest.this.device = new StandardDevice(dummyFailingPolicy);
            // checking that a device is on will not affect the strategy
            assertFalse(StandardDeviceTest.this.device.isOn());
        }
    }

    @Nested
    class ShowcaseStubs {
        private FailingPolicy stubFailingPolicy;

        @BeforeEach
        void init(){
            this.stubFailingPolicy = mock(FailingPolicy.class);
            StandardDeviceTest.this.device = new StandardDevice(this.stubFailingPolicy);
        }

        @Test
        @DisplayName("Device can be switched on")
        void testCanBeSwitchedOn() {
            // stubbing the test double, indicating "default behaviour"
            when(this.stubFailingPolicy.attemptOn()).thenReturn(true);
            StandardDeviceTest.this.device.on();
            assertTrue(StandardDeviceTest.this.device.isOn());
        }

        @Test
        @DisplayName("Device won't switch on if failing")
        void testWontSwitchOn() {
            // multiple stubbing
            when(this.stubFailingPolicy.attemptOn()).thenReturn(false);
            when(this.stubFailingPolicy.policyName()).thenReturn("mock");
            assertThrows(IllegalStateException.class, () -> StandardDeviceTest.this.device.on());
            assertEquals("StandardDevice{policy=mock, on=false}", StandardDeviceTest.this.device.toString());
        }
    }

    @Nested
    class ShowcaseFakes {
        private FailingPolicy fakeFailingPolicy;

        @BeforeEach
        void init(){
            this.fakeFailingPolicy = mock(FailingPolicy.class);
            StandardDeviceTest.this.device = new StandardDevice(this.fakeFailingPolicy);
            // faking is more than stubbing: this object pretends to be the real one
            when(this.fakeFailingPolicy.attemptOn()).thenReturn(true, true, false);
            when(this.fakeFailingPolicy.policyName()).thenReturn("mock");
        }

        @Test
        @DisplayName("Device switch on and off until failing")
        void testSwitchesOnAndOff() {
            IntStream.range(0, 2).forEach(i -> {
                StandardDeviceTest.this.device.on();
                assertTrue(StandardDeviceTest.this.device.isOn());
                StandardDeviceTest.this.device.off();
                assertFalse(StandardDeviceTest.this.device.isOn());
            });
            assertThrows(IllegalStateException.class, () -> StandardDeviceTest.this.device.on());
        }
    }

    @Nested
    class ShowcaseSpies {
        private FailingPolicy spyFailingPolicy;

        @BeforeEach
        void init(){
            // the spy is essentially a proxy to the DOC, used to capture events
            this.spyFailingPolicy = spy(new RandomFailing());
            StandardDeviceTest.this.device = new StandardDevice(this.spyFailingPolicy);
        }

        @Test
        @DisplayName("AttemptOn is called as expected")
        void testReset() {
            StandardDeviceTest.this.device.isOn();
            // no interactions with the spy yet
            verifyNoInteractions(this.spyFailingPolicy);
            try{
                StandardDeviceTest.this.device.on();
            } catch (final IllegalStateException e){}
            // has attemptOn been called?
            verify(this.spyFailingPolicy).attemptOn();
            StandardDeviceTest.this.device.reset();
            // have at least two method invocations be made?
            assertEquals(2,
                Mockito.mockingDetails(this.spyFailingPolicy).getInvocations().size());
            //  Mockito.mockingDetails gives very powerful mechanisms...
        }
    }

    @Nested
    class ShowcaseMocks {
        private FailingPolicy mockFailingPolicy;

        @BeforeEach
        void init(){
            this.mockFailingPolicy = spy(mock(FailingPolicy.class));
            StandardDeviceTest.this.device = new StandardDevice(this.mockFailingPolicy);
            // the mock is a TD used to check you are collaborating as expected
            when(this.mockFailingPolicy.attemptOn()).thenReturn(true, true, false);
            when(this.mockFailingPolicy.policyName()).thenReturn("mock");
        }

        @Test
        @DisplayName("attemptOn is called as expected")
        void testAttemptOn() {
            verify(this.mockFailingPolicy, times(0)).attemptOn();
            StandardDeviceTest.this.device.on();
            verify(this.mockFailingPolicy, times(1)).attemptOn();
            assertTrue(StandardDeviceTest.this.device.isOn());

            StandardDeviceTest.this.device.off();
            verify(this.mockFailingPolicy, times(1)).attemptOn();
            StandardDeviceTest.this.device.on();
            verify(this.mockFailingPolicy, times(2)).attemptOn();
            assertTrue(StandardDeviceTest.this.device.isOn());

            StandardDeviceTest.this.device.off();
            verify(this.mockFailingPolicy, times(2)).attemptOn();
            assertThrows(IllegalStateException.class, () -> StandardDeviceTest.this.device.on());
            verify(this.mockFailingPolicy, times(3)).attemptOn();
        }
    }
}