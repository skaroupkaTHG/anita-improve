package com.thehutgroup.accelerator.connectn.player;

import com.thehutgroup.accelerator.connectn.player.Board;
import com.thehutgroup.accelerator.connectn.player.Counter;
import com.thehutgroup.accelerator.connectn.player.Player;
import com.thehutgroup.accelerator.connectn.player.Position;

import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.pow;

public class AnitaImprove2 extends Player {

    int lastMove;
    private BitBoard currentBoard;
    private Simulator simulator;
    private int turn;
    private int[] lastFullCellPerCol;

    public AnitaImprove2(Counter counter) {
        // Anita Improve, created by Radek
        super(counter, AnitaImprove2.class.getName());
        this.currentBoard = new BitBoard();
        this.simulator = new Simulator();                                    // Create Simulator
        this.turn = 0;
    }

    public BitBoard getCurrentBoard() {
        return currentBoard;
    }

    private int[] getLastFullCellPerCol(Board board) {
        int[] thisArray = new int[10];
        for (int i = 0; i < 10; i++) {
            thisArray[i] = -1;
            for (int j = 0; j < 8; j++) {
                Position pos = new Position(i, j);
                if (board.hasCounterAtPosition(pos)) {
                    if (thisArray[i] < j) { // We shouldn't need this but just to be safe
                        thisArray[i] = j;
                    }
                }
            }
        }
        return thisArray;
    }

    private int playStrategy1(Board board) {
        // Check where do we have possible counters in board (maybe if we are playing second)
        lastFullCellPerCol = getLastFullCellPerCol(board);
        // We are going to play in the middle
        if (lastFullCellPerCol[4] < 0) {
            // This middle column is empty, play at 4
            // Play move in bitboard
            currentBoard.play(1, 4);
            lastMove = 4;   // Save this move
            return 4;
        }
        // It is the first turn, so if column 4 has a counter, column 5 is empty, play here
        // Play move in bitboard
        currentBoard.play(1, 5);
        lastMove = 5;   // save this move
        return 5;
    }

    private int playStrategy2(Board board) {
        // Stupid move, play at lastMove
        return lastMove;
    }

    private int playStrategy3(Board board) {
        // Stupid move, play next to last move
        return lastMove + 1;
    }

    // METHOD CALLED IN GAME TO MAKE A NEW MOVE
    @Override
    public int makeMove(Board board) {
        turn++;     // new turn to play
        if (turn == 1) {
            currentBoard.update(this.getCounter().getOther(), board);       // Update BitBoard with opponent move
            return playStrategy1(board);                                    // Play strategy
        }
        if (turn == 2) {
            currentBoard.update(this.getCounter().getOther(), board);       // Update BitBoard with opponent move
            return playStrategy2(board);                                    // Play strategy
        }
        if (turn == 3) {
            currentBoard.update(this.getCounter().getOther(), board);       // Update BitBoard with opponent move
            return playStrategy3(board);                                    // Play strategy
        }
        currentBoard.update(this.getCounter().getOther(), board);           // Update BitBoard with opponent move
        int losingInd = simulator.learn(currentBoard);                                      // Let Simulator study the board
        if (losingInd > 0) {
            lastMove = losingInd;
//            System.out.printf("BEST MOVE %d\n", lastMove);
            currentBoard.play(1, lastMove);
            return lastMove;
        }
        lastMove = simulator.bestMove();                                    // Save last move played
//        System.out.printf("BEST MOVE %d\n", lastMove);
        currentBoard.play(1, lastMove);
        return lastMove;
    }

    public class BitBoard {
        int[][] bitCounters;

        public BitBoard() {
            this.bitCounters = new int[2][10];
            Arrays.fill(this.bitCounters[0], 0b00000000);
            Arrays.fill(this.bitCounters[1], 0b00000000);
        }

        public BitBoard(int[][] input) {
            this.bitCounters = input;
        }

        public BitBoard deepCopy() {
            int[][] originalBitCounters = this.getBitCounters();
            int[][] copiedBitCounters = new int[2][10];
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 10; j++) {
                    copiedBitCounters[i][j] = originalBitCounters[i][j];
                }
            }
//            System.arraycopy(originalBitCounters, 0, copiedBitCounters, 0, originalBitCounters.length);
            return new BitBoard(copiedBitCounters);
        }

        public int[][] getBitCounters() {
            return bitCounters;
        }

        public int getFirstEmptyCellInCol(int colIndex) {
            int colVal = bitCounters[0][colIndex] | bitCounters[1][colIndex];
            for (int i = 1; i <= 128; i *= 2) {
                if (colVal < i)
                    return i;
            }
            return 128;
        }

        public int[] getFirstEmptyCellInAllCols(int playerIndex) {
            int[] firstEmptyCells = new int[10];
            for (int i = 0; i < firstEmptyCells.length; i++) {
                firstEmptyCells[i] = getFirstEmptyCellInCol(i);
            }
            return firstEmptyCells;
        }

        public void play(int playerIndex, int colIndex) {
            int firstEmptyCell = getFirstEmptyCellInCol(colIndex);
            this.bitCounters[playerIndex][colIndex] += firstEmptyCell;
        }

        public boolean isFullAt(int colIndex) {
            return getFirstEmptyCellInCol(colIndex) >= 128 || getFirstEmptyCellInCol(colIndex) >= 128;
        }

        public boolean isRowWin(int col1, int col2, int col3, int col4) {
            int result = col1 & col2 & col3 & col4;
            return result != 0;
        }

        public boolean isDiagShiftRightWin(int col1, int col2, int col3, int col4) {
            return isRowWin(col1, col2 << 1, col3 << 2, col4 << 3);
        }

        public boolean isDiagShiftLeftWin(int col1, int col2, int col3, int col4) {
            return isRowWin(col1 << 3, col2 << 2, col3 << 1, col4);
        }

        public boolean isColWin(int playerIndex, int colIndex) {
            int colVal = bitCounters[playerIndex][colIndex];
            for (int i = 0b00001111; i <= 0b11110000; i = i << 1) {
                if ((colVal & i) == i)
                    return true;
            }
            return false;
        }

        public boolean isWonBy(int playerIndex) {
            // rows and diagonals winning
            for (int i = 0; i <= 4; i++) {
                if (isRowWin(bitCounters[playerIndex][i], bitCounters[playerIndex][i + 1], bitCounters[playerIndex][i + 2], bitCounters[playerIndex][i + 3]))
                    return true;
                if (isDiagShiftLeftWin(bitCounters[playerIndex][i], bitCounters[playerIndex][i + 1], bitCounters[playerIndex][i + 2], bitCounters[playerIndex][i + 3]))
                    return true;
                if (isDiagShiftRightWin(bitCounters[playerIndex][i], bitCounters[playerIndex][i + 1], bitCounters[playerIndex][i + 2], bitCounters[playerIndex][i + 3]))
                    return true;
            }
            // cols winning
            for (int i = 0; i < bitCounters.length; i++) {
                if (isColWin(playerIndex, i))
                    return true;
            }
            return false;
        }

        public boolean isDraw() {
            return isFullAt(0) && isFullAt(1) && isFullAt(2) && isFullAt(3) &&
                    isFullAt(4) && isFullAt(5) && isFullAt(6) && isFullAt(7);
        }

        public int getUpdatedColumn(Board board, int colIndex, Counter opponentCounter) { // relies on Position(0,0) being at lower left corner
            int result = 0b00000000;
            for (int i = 0; i < board.getConfig().getHeight(); i++) {
                Position tempPosition = new Position(colIndex, i);
                if (board.hasCounterAtPosition(tempPosition) && board.getCounterAtPosition(tempPosition) == opponentCounter)
                    result += (int) pow(2, i);
            }
            return result;
        }

        public void printBitBoard() {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 10; j++) {
                    System.out.println(String.format("%8s", Integer.toBinaryString(bitCounters[i][j])).replace(" ", "0"));
                }
                System.out.println("\n");
            }
        }

        public void update(Counter opponentCounter, Board board) {
            printBitBoard();
            // int opponentIndex = (opponentCounter == Counter.X) ? 0 : 1; // assumes that X is first to start
            int opponentIndex = 0;
            for (int i = 0; i < bitCounters[opponentIndex].length; i++) {
                bitCounters[opponentIndex][i] = getUpdatedColumn(board, i, opponentCounter);
            }
        }
    }

    // SIMULATOR CLASS (AI)
    public class Simulator {
        public final Random random = new Random();     // Field of the class
        public BitBoard currentBoard;                  // Set with setter at each turn
        public final float[] pWin;              // Set to zero at each turn
        public int nTrials;                            // Set to zero at each turn
        public int initX;                              // Set at beginning of each trial

        // CONSTRUCTOR
        public Simulator() {
            this.nTrials = 0;                           // Initialise number of trials to zero
            this.pWin = new float[10];
            Arrays.fill(pWin, 0.0f);         // Initialise probability with zero
        }

        //SETTERS
        public void setCurrentBoard(BitBoard currentBoard) {
            this.currentBoard = currentBoard;
        }

        // RESET PROBABILITY TO ZERO
        public void resetProbability() {
            Arrays.fill(pWin, 0.0f);
        }

        // RESET NUMBER OF TRIALS TO ZERO
        public void resetNTrials() {
            this.nTrials = 0;
        }

        // RUN ONE SINGLE TRIAL GAME
        public int runTrial() {
            //  WE NEED TO DECIDE WHO IS OUR PLAYER AND WHO IS THE OPPONENT (EITHER 0 OR 1).
            // NOW I AM ASSUMING THAT WE ARE PLAYER 1 AND OPPONENT IS PLAYER 0!
            nTrials++;                                              // Accumulate one trial in counter
            BitBoard tmpBoard = currentBoard.deepCopy();                     // Copy initial board
            // Updated at each turn in each trial
            int turn = 0;                                           // Start turn counter from 0
            int playerIndex;                                        // Player Index
            int currentX;
            boolean play = true;
            while (play) {
                turn++;                                             // New turn
                // Updated at each turn in each trial
                currentX = selectX(10, tmpBoard);             // select empty random position in board
                if (turn == 1) {                                    // If this is the first turn we are playing in this trial game,
                    initX = currentX;                               // Save played move at first turn
                }
                playerIndex = getPlayer(turn);
                tmpBoard.play(playerIndex, currentX);               // Player plays turn
                if (tmpBoard.isWonBy(playerIndex)) {                // If current Player has won,
                    if (playerIndex == 1) {
                        pWin[initX] += 1.0f;                 // This should work fine, if our player is player 1
//                        System.out.printf("Anita won turn %d\n", turn);
                    } else {
//                        System.out.printf("Anita lost turn %d\n", turn); // (100% of winning from initX if player 1 won, 0% of winning from initX if player 0 won)
                        if (turn == 2) {
                            return currentX;            // WE DEFINITELY LOSE HERE
                        }
                    }
                    play = false;                                   // Finish the trial.
                } else if (tmpBoard.isDraw()) {                     // If it's a draw
                    pWin[initX] += 0.50f;                    // probability of winning from initX is 50%
                    play = false;                                   // Finish the trial
//                    System.out.printf("Draw turn %d\n", turn);
                } else {
//                    System.out.printf("still playing, turn %d\n", turn);
                }
            }
//            System.out.println("EXIT");
            return -1;
        }

        public int getPlayer(int turn) {
            return turn % 2;
        }

        public int selectX(int bound, BitBoard tmpBoard) {
            int currentX;
            while (true) {
                currentX = random.nextInt(0, bound);      // Get random x position in board
                if (!tmpBoard.isFullAt(currentX)) {              // If that column is full
                    return currentX;                             // reject random currentX
                }
            }
        }

        // EVALUATE BEST MOVE WITH PROBABILITY
        public int bestMove() {
            float max = -100.0f;
            int imax = -1;
            for (int i = 0; i < 10; i++) {
                pWin[i] = pWin[i] / nTrials;
//                System.out.printf("i %d , PWIN(i) : %.3f\n", i, pWin[i]);
                if (pWin[i] > max) {
                    max = pWin[i];
                    imax = i;
                }
            }
//            System.out.printf("NTrials : %d\n", nTrials);
//            System.out.printf("BEST MOVE : %d\n", imax);
            return imax;
        }

        public int learn(BitBoard currentBoard) {
            int losingInd;
            double TIME_LIMIT = 8;
            long start = System.currentTimeMillis();                        // Saving starting time
            boolean learn = true;                                           // Let AI learn something
            simulator.setCurrentBoard(currentBoard);                        // Set current board to simulator
            simulator.resetProbability();                                   // Reset probability
            simulator.resetNTrials();                                       // Reset number of trials
            while (learn) {
                for (int i = 0; i < 1000; i++) {                            // !!!! CHECK THIS !!! CHECK HOW MANY TRIALS WE CAN MAKE
                    losingInd = simulator.runTrial();                                   // AT THE FIRST ATTEMPTED MOVE WITH MONTE CARLO SIMULATOR (WORST CASE)
                    if (losingInd > 0) {
                        return losingInd;
                    }
                }
                long lap = System.currentTimeMillis();
                double time = (lap - start) / 1000.0;
//                System.out.printf("time passed: %f\n", time);
                if (((lap - start) / 1000.0) > TIME_LIMIT) {
                    learn = false;
                }
            }
            return -1;
        }

    }

}
