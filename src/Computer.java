import szte.mi.Move;
import szte.mi.Player;
import java.util.ArrayList;
import java.util.Random;

public class Computer implements Player {
    static int playerOrder;
    private Integer[][] oBoard;
    OthelloBoard othello;
    private Move lastMove;
    private Random random;
    private int difficulty;
    //0 is black, 1 is white
    private int player = 0;
    //obtained by randomly trying things, letting it run 10,000 times and seeing what works best
    private final Integer[][] myWeightMatrix = {
            {50, -20, 10, 5, 5, 10, -20, 50},
            {-20, -30, 1, 1, 1, 1, -20, -30},
            {10, 1, 7, 7, 7, 7, 1, 10},
            {5, 1, 7, 15, 15, 7, 1, 5},
            {5, 1, 7, 15, 15, 7, 1, 5},
            {10, 1, 7, 7, 7, 7, 1, 10},
            {-20, -30, 1, 1, 1, 1, -20, -30},
            {50, -20, 10, 5, 5, 10, -20, 50}
    };
    private final Integer[][] naiveWeights = {
            {6, 0, 3, 3, 3, 3, 0, 6},
            {0, 0, 1, 1, 1, 1, 0, 0},
            {3, 1, 2, 1, 1, 2, 1, 3},
            {3, 1, 1, 0, 0, 1, 1, 3},
            {3, 1, 1, 0, 0, 1, 1, 3},
            {3, 1, 2, 1, 1, 2, 1, 3},
            {0, 0, 1, 1, 1, 1, 0, 0},
            {6, 0, 3, 3, 3, 3, 0, 6}
    };

    public Computer() {
        difficulty = 3;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getDifficulty() {
        return difficulty;
    }

    @Override
    public void init(int order, long t, Random rnd) {
        this.othello = new OthelloBoard();
        this.oBoard = othello.getBoard();
        lastMove = null;
        playerOrder = order;
        //making sure the right player starts
        if (playerOrder == 0) {
            this.player = 1;
        } else {
            this.player = 0;
        }
        random = rnd;
    }

    @Override
    public Move nextMove(Move prevMove, long tOpponent, long t) {
        try {
            if (prevMove != null) {
                othello.makeMove(prevMove, player);
                lastMove = prevMove;
            }
            player++;
            ArrayList<Move> valMoves = othello.getValidMoves(player);
            if (valMoves.size() == 0) {
                player++;
                return null;
            }
            Move aiMove;
            if (difficulty % 3 == 1) {
                aiMove = randomMove(valMoves);
            } else if (difficulty % 3 == 2) {
                aiMove = greedyMove(valMoves, player);
            } else {
                aiMove = weightMatrixMove(valMoves, player);
            }
            othello.makeMove(aiMove, player);
            player++;
            return aiMove;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //using a weight matrix to decide best move
    private Move weightMatrixMove(ArrayList<Move> nextMoves, int player) throws Exception {
        if (nextMoves.size() == 0) {
            return null;
        } else {
            changeWeightMatrix();
            double moveScore = -200;
            ArrayList<Move> bestMoves = new ArrayList<>(nextMoves);
            for (Move m : nextMoves) {
                double numMoves;
                double score;
                int pureScore;
                OthelloBoard tmp = new OthelloBoard();
                tmp.setBoard(othello.getBoard());
                tmp.makeMove(m, player);
                //evaluate current move in case of missing later moves
                numMoves = tmp.getValidMoves(player+1).size();
                pureScore = calculateMoveScores(tmp, player);
                if(pureScore > 0){
                    score = pureScore / numMoves;
                }else{
                    score = pureScore * numMoves;
                }
                //make best move for enemy
                Move enemyMove = makeBestMove(tmp, player + 1);
                if (enemyMove != null) {
                    tmp.makeMove(enemyMove, player + 1);
                }
                Move layer3Move = makeBestMove(tmp, player);
                if (layer3Move != null) {
                    tmp.makeMove(layer3Move, player);
                    numMoves = tmp.getValidMoves(player+1).size();
                    pureScore = calculateMoveScores(tmp, player);
                    if(pureScore > 0){
                        score = pureScore / numMoves;
                    }else{
                        score = pureScore * numMoves;
                    }
                }

                if (moveScore < score) {
                    moveScore = score;
                    bestMoves = new ArrayList<>();
                    bestMoves.add(m);
                } else if (moveScore == score) {
                    bestMoves.add(m);
                }
            }
            return bestMoves.get(random.nextInt(bestMoves.size()));
        }
    }

    //just gives back a random valid move
    private Move randomMove(ArrayList<Move> nextMoves) {
        int i = random.nextInt(nextMoves.size());
        return nextMoves.get(i);
    }

    //makes a greedy (maximal) move
    private Move greedyMove(ArrayList<Move> nextMoves, int player) {
        if (nextMoves.size() == 0) {
            return null;
        } else {
            Move aiMove = null;
            //if it can go into a corner, it will
            for (Move m : nextMoves) {
                if (m.x == 0 && m.y == 0) {
                    aiMove = m;
                } else if (m.x == 0 && m.y == 7) {
                    aiMove = m;
                } else if (m.x == 7 && m.y == 0) {
                    aiMove = m;
                } else if (m.x == 7 && m.y == 7) {
                    aiMove = m;
                }
            }
            if (aiMove != null) {
                return aiMove;
            }
            int changes = 0;
            Move greedyMove;
            //gets the moves that make the least changes to the board
            ArrayList<Move> bestPosMoves = new ArrayList<>();
            for (Move m : nextMoves) {
                ArrayList<Integer[]> tmp = othello.getPointsToChange(m, player);
                if (tmp.size() > changes) {
                    changes = tmp.size();
                    bestPosMoves = new ArrayList<>();
                    bestPosMoves.add(m);
                } else if (tmp.size() == changes) {
                    bestPosMoves.add(m);
                }
            }
            //selects one of them at random
            greedyMove = bestPosMoves.get(random.nextInt(bestPosMoves.size()));
            return greedyMove;
        }
    }

    //scores the board for the player after a hypothetical move using the weightMatrix
    private int calculateMoveScores(OthelloBoard board, int currPlayer) {
        int score = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getBoard()[i][j] != null && board.getBoard()[i][j] == currPlayer % 2) {
                    if (currPlayer != player){
                        score += naiveWeights[i][j];
                    } else{
                        score += myWeightMatrix[i][j];
                    }
                }
            }
        }
        return score;
    }

    //evaluates the best move
    private Move makeBestMove(OthelloBoard board, int currPlayer) throws Exception {
        int moveScore = 0;
        OthelloBoard tmp = new OthelloBoard();
        tmp.setBoard(board.getBoard());
        ArrayList<Move> validMoves = tmp.getValidMoves(currPlayer);
        if (validMoves.size() == 0) {
            return null;
        }
        Move nextMove = null;
        for (Move move : validMoves) {
            OthelloBoard tmp2 = new OthelloBoard();
            tmp2.setBoard(board.getBoard());
            tmp2.makeMove(move, currPlayer);
            int score;
            score =calculateMoveScores(tmp, currPlayer);
            if (moveScore < score) {
                moveScore = score;
                nextMove = move;
            }
        }
        return nextMove;
    }

    //changes the weight matrix "myWeightMatrix" in certain situations
    private void changeWeightMatrix() {
        try {
            if (oBoard[0][0] == playerOrder) {
                myWeightMatrix[1][0] = 0;
                myWeightMatrix[0][1] = 0;
                myWeightMatrix[1][1] = 0;
            }
            if (oBoard[0][7] == playerOrder) {
                myWeightMatrix[0][6] = 0;
                myWeightMatrix[1][6] = 0;
                myWeightMatrix[1][7] = 0;
            }
            if (oBoard[7][0] == playerOrder) {
                myWeightMatrix[6][0] = 0;
                myWeightMatrix[6][1] = 0;
                myWeightMatrix[7][1] = 0;
            }
            if (oBoard[7][7] == playerOrder) {
                myWeightMatrix[6][7] = 0;
                myWeightMatrix[6][6] = 0;
                myWeightMatrix[7][6] = 0;
            }
        }
        catch (NullPointerException e) {}
    }

    //helper method for gui
    public void updateMove(Move move){
        try {
            if(move != null){
                othello.makeMove(move, player);}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
