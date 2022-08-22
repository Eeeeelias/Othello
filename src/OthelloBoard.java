import szte.mi.Move;

import java.util.ArrayList;

public class OthelloBoard{

    private Integer[][] board;
    private final int blacks = 0;
    private final int whites = 1;

    public OthelloBoard(){
        this.board = new Integer[8][8];
        fillBoard(this.board);
        this.board[3][3] = whites;
        this.board[4][3] = blacks;
        this.board[4][4] = whites;
        this.board[3][4] = blacks;
    }

    public Integer[][] getBoard() {
        return board;
    }

    public void setBoard(Integer[][] toCopy) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                try {
                    board[i][j] = toCopy[i][j];
                } catch (NullPointerException e){}
            }
        }
    }

    private Integer[][] fillBoard(Integer[][] board){
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board.length; j++){
                board[i][j] = null;
            }
        }
        return board;
    }

    //takes a move m and sets it on the board. also gets the positions that need to switch colour and switches those
    //given that it does not return null
    public void makeMove(Move m, int col) throws Exception{
        ArrayList<Integer[]> moves = getPointsToChange(m, col);
        if (moves != null){
            if (col % 2 != 0) {
                this.board[m.x][m.y] = whites;
            } else {
                this.board[m.x][m.y] = blacks;
            }
            moves.forEach(arr -> board[arr[0]][arr[1]] = col%2);
        } else{
            throw new Exception();
        }
    }
    //if previous Move and current move are both null the function goes through the array and checks if there are more
    //black or white pieces; returns the winner, else returns false
    public String checkForWinner(Move prevMove, Move currMove){
        int countBlacks = 0;
        int countWhites = 0;
        if ((prevMove == null && currMove == null)){
            for (int i = 0; i < board.length; i++){
                for (int j = 0; j < board.length; j++) {
                    if(board[i][j] == null){
                        continue;
                    }
                    if (board[i][j] == 0)
                        countBlacks++;
                    if (board[i][j] == 1)
                        countWhites++;
                }
            }
            if (countBlacks > countWhites){
                return "Black wins!";
            } else if (countBlacks < countWhites){
                return "White wins!";
            } else{
                return "Draw!";
            }
        }
        return null;
    }

    //checks what elements in the array need to change and writes them in an ArrayList. It does that for all 8 directions
    //starting from the position that the player wants make their move on. If no points change it returns null
    private ArrayList<Integer[]> pointsToChange(Move move, int counter){
        ArrayList<Integer[]> changes = new ArrayList<>();
        if(this.board[move.x][move.y] != null){
            return null;
        }
        ArrayList<Integer[]> colsRows = new ArrayList<>();
        int row = move.x;
        //check for any changes in both directions of the rows
        for(int i = move.y+1;i <8; i++){
            //a certain position in the board can have 3 states: null, 0 or 1
            //if it's null the loop breaks and goes on to the next direction since there is nothing to change there
            if(row > 7 || board[row][i] == null){
                break;
            }
            //if it's the opposing players piece it adds the coordinates to a temporary array
            if(board[row][i] != counter%2){
                Integer[] tmp = {row, i};
                colsRows.add(tmp);
            }
            //the last possibility is that the field contains the current players piece. in that case it checks if the
            //temporary array is empty, if the changes are added to the final array
            else {
                if (colsRows.size() != 0){
                    changes.addAll(colsRows);
                }
                break;
            }
        }
        colsRows = new ArrayList<>();
        for(int i = move.y-1;i >=0; i--){
            if(row < 0 ||  board[row][i] == null){
                break;
            }
            if(board[row][i] != counter%2){
                Integer[] tmp = {row, i};
                colsRows.add(tmp);
            }
            else {
                if (colsRows.size() != 0){
                    changes.addAll(colsRows);
                }
                break;
            }
        }
        int col = move.y;
        //check for any changes in both directions of the columns
        colsRows = new ArrayList<>();
        for(int i = move.x+1; i<8;i++){
            if(col > 7 || board[i][col] == null){
                break;
            }
            if(board[i][col] != counter%2){
                Integer[] tmp = {i, col};
                colsRows.add(tmp);
            }
            else {
                if (colsRows.size() != 0){
                    changes.addAll(colsRows);
                }
                break;
            }
        }
        colsRows = new ArrayList<>();
        for(int i = move.x-1; i>=0;i--){
            if(col < 0 ||  board[i][col] == null){
                break;
            }
            if(board[i][col] != counter%2){
                Integer[] tmp = {i, col};
                colsRows.add(tmp);
            }
            else {
                if (colsRows.size() != 0){
                    changes.addAll(colsRows);
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
            if( diag11 >7 || board[i][diag11] == null){
                break;
            }
            if(board[i][diag11] != counter%2){
                Integer[] tmp = {i, diag11};
                diags.add(tmp);
                diag11++;
            }
            else {
                if (diags.size() != 0){
                    changes.addAll(diags);
                }
                break;
            }
        }
        diags = new ArrayList<>();
        for(int i = move.x-1; i >= 0; i--){
            if( diag12 < 0 || board[i][diag12] == null){
                break;
            }
            if(board[i][diag12] != counter%2){
                Integer[] tmp = {i, diag12};
                diags.add(tmp);
                diag12--;
            }
            else {
                if (diags.size() != 0){
                    changes.addAll(diags);
                }
                break;
            }
        }
        int diag21 = move.y-1;
        int diag22 = move.y+1;
        diags = new ArrayList<>();
        for(int i = move.x+1; i < 8; i++){
            if(diag21 < 0 || board[i][diag21] == null){
                break;
            }
            if(board[i][diag21] != counter%2){
                Integer[] tmp = {i, diag21};
                diags.add(tmp);
                diag21--;
            } else {
                if (diags.size() != 0){
                    changes.addAll(diags);
                }
                break;
            }
        }
        diags = new ArrayList<>();
        for(int i = move.x-1; i >=0; i--){
            if(diag22 > 7 || board[i][diag22] == null){
                break;
            }
            if(board[i][diag22] != counter%2){
                Integer[] tmp = {i, diag22};
                diags.add(tmp);
                diag22++;
            } else {
                if (diags.size() != 0){
                    changes.addAll(diags);
                }
                break;
            }
        }


        if(changes.size() != 0){
            return changes;
        }
        return null;
    }

    public ArrayList<Integer[]> getPointsToChange(Move move, int counter){
        return pointsToChange(move, counter);
    }

    public static void printBoard(Integer[][] board){
        System.out.println(" |1|2|3|4|5|6|7|8|");
        for (int i = 0; i < board.length; i++){
            System.out.print((i+1) + "|");
            for (int j = 0; j < board.length; j++){
                if (board[i][j] == null){
                    System.out.print(" ");
                } else{
                    System.out.print(board[i][j]);
                }
                if (j < board.length){
                    System.out.print("|");
                }
            }
            System.out.print("\n");
        }
    }

    public ArrayList<Move> getValidMoves(int counter) {
        ArrayList<Move> validMoves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == null && getPointsToChange(new Move(i, j), counter) != null)
                    validMoves.add(new Move(i, j));
            }
        }
        return validMoves;
    }
}
