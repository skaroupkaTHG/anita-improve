package com.thg.accelerator23.connectn.ai.lucanradek;

import com.thehutgroup.accelerator.connectn.player.Counter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BitBoardTest {

    public AnitaImprove2 generateAnita() {
        return new AnitaImprove2(Counter.X);
    }

    @Test
    public void play_Return1() {
        AnitaImprove2 anita = generateAnita();
        anita.getCurrentBoard().play(0, 0);
        assertEquals(1, anita.getCurrentBoard().getBitCounters()[0][0]);
    }

    @Test
    public void getFirstEmptyCellInCol_Return2() {
        AnitaImprove2 anita = generateAnita();
        anita.getCurrentBoard().play(0, 0);
        int result = anita.getCurrentBoard().getFirstEmptyCellInCol(0);
        assertEquals(2, result);
    }

    @Test
    public void getFirstEmptyCellInCol_TwoPlayers_Return4() {
        AnitaImprove2 anita = generateAnita();
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(1, 0);
        assertEquals(4, anita.getCurrentBoard().getFirstEmptyCellInCol(0));
    }

    @Test
    public void getFirstEmptyCellInCol_Return128() {
        AnitaImprove2 anita = generateAnita();
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(0, 0);
        int result = anita.getCurrentBoard().getFirstEmptyCellInCol(0);
        assertEquals(128, result);
    }

    @Test
    public void getFirstEmptyCellInAllCols_ReturnExpectedArray() {
        AnitaImprove2 anita = generateAnita();
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(0, 1);
        anita.getCurrentBoard().play(0, 2);
        anita.getCurrentBoard().play(0, 9);
        int[] expectedArray = new int[]{2, 2, 2, 1, 1, 1, 1, 1, 1, 2};
        int[] result = anita.getCurrentBoard().getFirstEmptyCellInAllCols(0);
        assertEquals(expectedArray[0], result[0]);
        assertEquals(expectedArray[1], result[1]);
        assertEquals(expectedArray[2], result[2]);
        assertEquals(expectedArray[3], result[3]);
        assertEquals(expectedArray[4], result[4]);
        assertEquals(expectedArray[5], result[5]);
        assertEquals(expectedArray[6], result[6]);
        assertEquals(expectedArray[7], result[7]);
        assertEquals(expectedArray[8], result[8]);
        assertEquals(expectedArray[9], result[9]);
    }

    @Test
    public void isFullAt_ReturnTrue() {
        AnitaImprove2 anita = generateAnita();
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(0, 0);
        assertTrue(anita.getCurrentBoard().isFullAt(0));
    }

    @Test
    public void isRowWin_ReturnFalse() {
        AnitaImprove2 anita = generateAnita();
        assertFalse(anita.getCurrentBoard().isRowWin(2, 2, 0, 0));
    }

    @Test
    public void isRowWin_ReturnTrue() {
        AnitaImprove2 anita = generateAnita();
        assertTrue(anita.getCurrentBoard().isRowWin(2, 3, 2, 3));
    }

    @Test
    public void isDiagShiftRightWin_ReturnFalse() {
        AnitaImprove2 anita = generateAnita();
        assertFalse(anita.getCurrentBoard().isDiagShiftRightWin(2, 2, 0, 0));
    }

    @Test
    public void isDiagShiftRightWin_ReturnTrue() {
        AnitaImprove2 anita = generateAnita();
        assertTrue(anita.getCurrentBoard().isDiagShiftRightWin(10, 7, 2, 1));
    }

    @Test
    public void isDiagShiftLeftWin_ReturnFalse() {
        AnitaImprove2 anita = generateAnita();
        assertFalse(anita.getCurrentBoard().isDiagShiftLeftWin(2, 2, 0, 0));
    }

    @Test
    public void isDiagShiftLeftWin_ReturnTrue() {
        AnitaImprove2 anita = generateAnita();
        assertTrue(anita.getCurrentBoard().isDiagShiftLeftWin(1, 2, 7, 10));
    }

    @Test
    public void isColWin_ReturnFalse() {
        AnitaImprove2 anita = generateAnita();
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(1, 0);
        anita.getCurrentBoard().play(1, 0);
        assertFalse(anita.getCurrentBoard().isColWin(0, 0));
    }

    @Test
    public void isColWin_ReturnTrue() {
        AnitaImprove2 anita = generateAnita();
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(0, 0);
        anita.getCurrentBoard().play(1, 0);
        anita.getCurrentBoard().play(1, 0);
        anita.getCurrentBoard().play(1, 0);
        anita.getCurrentBoard().play(1, 0);
        assertTrue(anita.getCurrentBoard().isColWin(1, 0));
    }
}
