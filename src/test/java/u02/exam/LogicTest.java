package u02.exam;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class LogicTest {

    private Logic logic;
    private Logger logger;

    @BeforeEach
    void init() {
        this.logger = spy(mock(Logger.class));
        this.logic = new LogicImpl(11,11,this.logger);
    }


    @Test
    public void testLogBeforeHitting() {
        verifyNoInteractions(this.logger);
    }

    @Test
    public void testSingleLog() {
        // hitting the right corner should only log once
        this.logic.hit(new Position(10,10));
        verify(this.logger, times(1)).log(anyString());
    }

    @Test
    public void testMultipleLogs() {
        // hitting the 5,0 should log 10 times
        this.logic.hit(new Position(0,5));
        verify(this.logger, times(36)).log(anyString());
    }

}
