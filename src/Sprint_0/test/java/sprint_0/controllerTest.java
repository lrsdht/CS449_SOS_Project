package sprint_2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class controllerTest {

    @Test
    void human() {
        controller c = new controller();
        assertEquals("Human!", c.Human());
    }

    @Test
    void computer() {
        controller c = new controller();
        assertEquals("Computer!", c.Computer());
    }

    @Test
    void s() {
        controller c = new controller();
        assertEquals("S!", c.S());
    }

    @Test
    void o() {
        controller c = new controller();
        assertEquals("O!", c.O());
    }

    @Test
    void recordGame() {
        controller c = new controller();
        assertEquals("RecordGame!", c.RecordGame());
    }

    @Test
    void replay() {
        controller c = new controller();
        assertEquals("Replay!", c.Replay());
    }

    @Test
    void newGame() {
        controller c = new controller();
        assertEquals("NewGame!", c.NewGame());
    }
}