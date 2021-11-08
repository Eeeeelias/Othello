import szte.mi.GameBoard;
import szte.mi.Move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Runner {

    public static void  printBoard(Integer[][] board){
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

    public static void printMoves (ArrayList<Move> moves){
        System.out.println("Valid moves: ");
        if(moves.size() > 0){
            for (Move m: moves){
                    System.out.println("row: " + (m.x+1) + " column: " +  (m.y+1));
            }
        } else{
            System.out.println("No valid moves");
        }
    }

    public static void main(String[] args) {
        GameBoard newGame = new GameBoard(0);
        Integer[][] testBoard = newGame.getBoard();
        printBoard(testBoard);
        int counter = 0;
        Move prefMove = null;

        while (true){
            try {
                Scanner scan = new Scanner(System.in);
                System.out.println(" Player " + counter%2 +" please type in row & col number");
                ArrayList<Move> possibleMoves = new ArrayList<>();
                for(int i = 0; i < 8; i++){
                    for (int j = 0; j <8; j++){
                        //ERROR: adds the changes to the possibleMoves not the valid moves
                        if (testBoard[i][j] == null){
                            if(newGame.getPointsToChange(new Move(i, j), counter) != null)
                            possibleMoves.add(new Move(i, j));
                        }
                    }
                }
                possibleMoves.removeAll(Collections.singleton(null));
                printMoves(possibleMoves);
                Move currMove;
                if(possibleMoves.size() <= 0){
                    currMove = null;
                } else {
                    int x_coord = scan.nextInt();
                    int y_coord = scan.nextInt();
                    currMove = new Move(x_coord - 1, y_coord - 1);
                    newGame.makeMove(currMove, counter);
                }
                if(newGame.checkForWinner(currMove, prefMove)){
                    System.out.println("The Winner is: Player "+ counter %2);
                    System.exit(0);
                }
                printBoard(testBoard);
                counter++;
                prefMove = currMove;
            } catch (ArrayIndexOutOfBoundsException e){
                System.out.println("invalid move, out of field");
            } catch (NullPointerException e){
                e.printStackTrace();
                System.out.println("invaid move, empty field");
                counter--;
            } catch (InputMismatchException e){
                System.out.println("invalid move, full field");
                //counter--;
            } catch (Exception e) {
                System.out.println("invalid move");
            }
        }
    }
}
