package com.thg.accelerator21.connectn.ai.lucanradek;

import com.thehutgroup.accelerator.connectn.player.Board;
import com.thehutgroup.accelerator.connectn.player.Counter;
import com.thehutgroup.accelerator.connectn.player.GameConfig;
import com.thg.accelerator23.connectn.ai.lucanradek.AnitaImprove2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnitaImprove2Test {

    AnitaImprove2 anitaImprove2 = new AnitaImprove2(Counter.X);

    @Test
    public void getLastFullCellPerColTest() {
        Counter[][] counterPlacements = new Counter[10][8];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j <= i; j++) {
                counterPlacements[i][j] = Counter.X;
            }
        }
        Board testBoard = new Board(counterPlacements, new GameConfig(10, 8, 4));
        int[] testArray = anitaImprove2.getLastFullCellPerCol(testBoard);
        for (int i = 0; i < 10; i++) {
            if (i < 4) {
                assertEquals(testArray[i], i);
            } else {
                assertEquals(testArray[i], -1);
            }
        }
    }

    @Test
    public void createCorrectSimulator() {
        AnitaImprove2.Simulator simulator = anitaImprove2.simulator;
        assertEquals(simulator.nTrials, 0);
        for (int i = 0; i < 10; i++) {
            assertEquals(simulator.probability[i], 0.0f);
        }
    }

    @Test
    public void createSimulatorReset() {
        AnitaImprove2.Simulator simulator = anitaImprove2.simulator;
        assertEquals(simulator.nTrials, 0);
        for (int i = 0; i < 10; i++) {
            assertEquals(simulator.probability[i], 0.0f);
        }
        for (int i = 0; i < 10; i++) {
            simulator.nTrials++;
            simulator.probability[i] += 1.0f;
        }
        assertEquals(simulator.nTrials, 10);
        for (int i = 0; i < 10; i++) {
            assertEquals(simulator.probability[i], 1.0f);
        }
        simulator.resetProbability();
        simulator.resetNTrials();
        assertEquals(simulator.nTrials, 0);
        for (int i = 0; i < 10; i++) {
            assertEquals(simulator.probability[i], 0.0f);
        }
    }

    @Test
    public void selectXTest() {
        AnitaImprove2.Simulator simulator = anitaImprove2.simulator;
        simulator.tmpBoard = anitaImprove2.currentBoard;
        for (int i = 0; i < 100; i++) {
            int testValue = simulator.selectX(10, simulator.tmpBoard);
            assertTrue(testValue < 10);
            assertTrue(testValue >= 0);
        }
    }

    @Test
    public void getPlayerTest() {
        AnitaImprove2.Simulator simulator = anitaImprove2.simulator;
        assertEquals(1, simulator.getPlayer(1));
        assertEquals(0, simulator.getPlayer(2));
        assertEquals(1, simulator.getPlayer(3));
        assertEquals(0, simulator.getPlayer(4));
    }
}
