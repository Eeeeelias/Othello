package szte.mi;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;

public class GameBoard implements Player{

    private Integer[][] board;
    private int blacks = 0;
    private int whites = 1;

    public GameBoard(int startingPlayer){
        this.board = new Integer[8][8];
        fillBoard(this.board);
        this.board[3][3] = whites;
        this.board[4][3] = blacks;
        this.board[4][4] = whites;
        this.board[3][4] = blacks;
        init(startingPlayer, 8000, new Random());
    }

    public Integer[][] getBoard() {
        return board;
    }

    private Integer[][] fillBoard(Integer[][] board){
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board.length; j++){
                board[i][j] = null;
            }
        }
        return board;
    }

    public void makeMove(Move m, int col) throws Exception {
        ArrayList<Integer[]> moves = pointsToChange(m, col);
        if (moves != null){
            if (col % 2 != 0) {
                this.board[m.x][m.y] = whites;
            } else {
                this.board[m.x][m.y] = blacks;
            }
            moves.forEach(arr -> board[arr[0]][arr[1]] = col%2);
        } else {
            throw new Exception();
        }
    }
    //TODO: check if there is winner
    public boolean checkForWinner(Move prefMove, Move currMove){
        int countBlacks = 0;
        int countWhites = 0;
        if ((prefMove == null && currMove == null)){
            for (int i = 0; i < board.length; i++){
                for (int j = 0; j < board.length; j++) {
                    if (board[i][j] == 0)
                        countBlacks++;
                    if (board[i][j] == 1)
                        countWhites++;
                }
            }
            if (countBlacks > countWhites){
                System.out.println("Black wins");
                return true;
            } else{
                System.out.println("White wins");
                return true;
            }
        }
        return false;
    }

    //TODO: check if move is legal
    private ArrayList<Integer[]> pointsToChange(Move move, int counter){
        ArrayList<Integer[]> changes = new ArrayList<>();
        if(this.board[move.x][move.y] != null){
            throw new InputMismatchException();
        }
        ArrayList<Integer[]> colsRows = new ArrayList<>();
        int row = move.x;
        //check for any changes in both directions of the rows
        for(int i = move.y+1;i <8; i++){
            if(row > 7 || i > 7 || board[row][i] == null){
                break;
            }
            if(board[row][i] != counter%2){
                Integer[] tmp = {row, i};
                colsRows.add(tmp);
            }
            else {
                if (colsRows.size() != 0){
                    colsRows.forEach(arr -> changes.add(arr));
                }
                break;
            }
        }
        colsRows = new ArrayList<>();
        for(int i = move.y-1;i >=0; i--){
            if(row < 0 || i < 0 || board[row][i] == null){
                break;
            }
            if(board[row][i] != counter%2){
                Integer[] tmp = {row, i};
                colsRows.add(tmp);
            }
            else {
                if (colsRows.size() != 0){
                    colsRows.forEach(arr -> changes.add(arr));
                }
                break;
            }
        }
        int col = move.y;
        //check for any changes in both directions of the columns
        colsRows = new ArrayList<>();
        for(int i = move.x+1; i<8;i++){
            if(col > 7 || i > 7 || board[i][col] == null){
                break;
            }
            if(board[i][col] != counter%2){
                Integer[] tmp = {i, col};
                colsRows.add(tmp);
            }
            else {
                if (colsRows.size() != 0){
                    colsRows.forEach(arr -> changes.add(arr));
                }
                break;
            }
        }
        colsRows = new ArrayList<>();
        for(int i = move.x-1; i>=0;i--){
            if(col < 0 || i < 0 || board[i][col] == null){
                break;
            }
            if(board[i][col] != counter%2){
                Integer[] tmp = {i, col};
                colsRows.add(tmp);
            }
            else {
                if (colsRows.size() != 0){
                    colsRows.forEach(arr -> changes.add(arr));
                }
                break;
            }
        }
        ArrayList<Integer[]> diags = new ArrayList<>();
        //check for both directions in both the diagonals
        //diag11, diag12, diag21, diag22 just help navigate the field diagonally
        int diag11 = move.y+1;
        int diag12= move.y-1;
        for(int i = move.x+1; i < 8; i++){
            if(i > 7 || diag11 >7 || board[i][diag11] == null){
                break;
            }
            if(board[i][diag11] != counter%2){
                Integer[] tmp = {i, diag11};
                diags.add(tmp);
                diag11++;
            }
            else {
                if (diags.size() != 0){
                    diags.forEach(arr -> changes.add(arr));
                }
                break;
            }
        }
        diags = new ArrayList<>();
        for(int i = move.x-1; i >= 0; i--){
            if(i < 0 || diag12 < 0 || board[i][diag12] == null){
                break;
            }
            if(board[i][diag12] != counter%2){
                Integer[] tmp = {i, diag12};
                diags.add(tmp);
                diag12--;
            }
            else {
                if (diags.size() != 0){
                    diags.forEach(arr -> changes.add(arr));
                }
                break;
            }
        }
        int diag21 = move.y-1;
        int diag22 = move.y+1;
        diags = new ArrayList<>();
        for(int i = move.x+1; i < 8; i++){
            if(diag21 < 0 || i > 7 || board[i][diag21] == null){
                break;
            }
            if(board[i][diag21] != counter%2){
                Integer[] tmp = {i, diag21};
                diags.add(tmp);
                diag21--;
            } else {
                if (diags.size() != 0){
                    diags.forEach(arr -> changes.add(arr));
                }
                break;
            }
        }
        diags = new ArrayList<>();
        for(int i = move.x-1; i >=0; i--){
            if(diag22 < 0 || i <0 || board[i][diag22] == null){
                break;
            }
            if(board[i][diag22] != counter%2){
                Integer[] tmp = {i, diag22};
                diags.add(tmp);
                diag22++;
            } else {
                if (diags.size() != 0){
                    diags.forEach(arr -> changes.add(arr));
                }
                break;
            }
        }


        if(changes.size() != 0){
            return changes;
        }
        return null;
    }

    public ArrayList<Integer[]> getPointsToChange(Move m, int counter){
        return pointsToChange(m, counter);
    }

    @Override
    public void init(int order, long t, Random rnd) {

    }


    @Override
    public Move nextMove(Move prevMove, long tOpponent, long t) {
        int x = 3;
        int y = 4;
        Move m = new Move(y, x);;
        if (pointsToChange(m, 10) != null) {
            return null;
        } else{
            m = null;
        }
        return m;
    }

}
