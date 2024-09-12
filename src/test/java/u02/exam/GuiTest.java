package u02.exam;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;


public class GuiTest {

    private Logic logic;
    private Logger logger;
    private GUI gui;

    private <G> Object getPrivateField(final G v, final String name) throws NoSuchFieldException, IllegalAccessException {
        final Field privateField = GUI.class.getDeclaredField(name);
        privateField.setAccessible(true);
        return privateField.get(v);
    }

    @BeforeEach
    void init() {
        this.logger = spy(mock(Logger.class));
        this.logic = spy(mock(Logic.class));
        when(this.logic.getGoal()).thenReturn(new Position(5,5));
        this.gui = new GUI(11,11,this.logic, this.logger, false);
    }

    @Test
    public void testLogBeforeHitting() {
        verify(this.logger, times(1)).log(anyString());
    }

    @Test
    public void testGameOver() throws NoSuchFieldException, IllegalAccessException {
        // using a private field
        final var cells = (Map<JButton, Position>)this.getPrivateField(gui, "cells");
        // hitting the goal should end the game
        when(this.logic.isOver()).thenReturn(true);
        final var button = cells.keySet().stream().findFirst();
        button.get().doClick();
        verify(this.logger, times(2)).log(anyString());
    }
}
