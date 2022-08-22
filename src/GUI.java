import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import szte.mi.*;

import java.util.ArrayList;
import java.util.Random;


public class GUI implements EventHandler<ActionEvent> {
    protected static Button[][] buttons = new Button[8][8];
    protected static BorderPane mainLayout;
    protected static Label gameStatus;
    protected int decidePlayer;
    protected int playMode;
    protected int playerOrder;
    protected Move currMove;
    protected Move lastMove;
    public OthelloBoard game;
    protected Computer comp;


    //initializes all of the GUI elements and important parameters
    public BorderPane initGUI(int player, int mode, int order) {
        playMode = mode;
        decidePlayer = player;
        playerOrder = order;
        gameStatus = new Label(playerString(decidePlayer));
        gameStatus.setFont(new Font("Helvetica", 30));
        gameStatus.setMinWidth(300);
        game = new OthelloBoard();
        comp = new Computer();
        comp.init(order, 8000, new Random());
        comp.setDifficulty(1);
        mainLayout = new BorderPane();
        GridPane layout = new GridPane();
        HBox top = new HBox(gameStatus, difficultyButton(mode));
        top.setSpacing(270);
        top.setStyle("-fx-background-color: green;");
        layout.setStyle("-fx-background-color: green;");
        //layout.setStyle("-fx-background-image: url('https://media.tenor.com/images/63c32d93053f00dd2cdc622c802dbee6/tenor.gif'); -fx-background-position: center center; -fx-background-repeat: stretch;");
        makeButtons(layout);
        startButtons();
        enableButtons(getValidMoves(decidePlayer));
        //only triggers when AI should start
        if(mode ==1 && order == 0){
            Move m = comp.nextMove(null, 8000, 0);
            buttons[m.x][m.y].fire();
        }
        mainLayout.setCenter(layout);
        mainLayout.setTop(top);
        return mainLayout;
    }

    //creating all the buttons that are needed
    public void makeButtons(GridPane layout) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Button b = new Button("");
                b.setPrefSize(90, 90);
                b.setOpacity(0.1);
                b.setStyle("-fx-background-color: lightgray;");
                b.setShape(new Circle(0.5));
                buttons[i][j] = b;
                b.addEventHandler(ActionEvent.ACTION, this);
                b.setDisable(true);
                layout.add(b, j, i);
            }
        }
    }

    //sets the first 4 buttons
    public void startButtons(){
        buttons[3][3].setStyle("-fx-background-color: white;");
        buttons[3][3].setOpacity(1);
        buttons[3][4].setStyle("-fx-background-color: black;");
        buttons[3][4].setOpacity(1);
        buttons[4][3].setStyle("-fx-background-color: black;");
        buttons[4][3].setOpacity(1);
        buttons[4][4].setStyle("-fx-background-color: white;");
        buttons[4][4].setOpacity(1);
    }

    //switches the difficulty when playing against a bot
    public Node difficultyButton(int mode){
        if(mode == 1) {
            Button setDifficulty = new Button();
            setDifficulty.setPrefSize(150, 50);
            setDifficulty.setFont(new Font("Helvetica", 15));
            setDifficulty.setStyle("-fx-background-color: darkgreen; -fx-border-color: black;");
            setDifficulty.setText("Difficulty: " + diff(comp.getDifficulty()));
            setDifficulty.addEventHandler(ActionEvent.ACTION, (ActionEvent e) -> comp.setDifficulty(comp.getDifficulty()+1));
            setDifficulty.addEventHandler(ActionEvent.ACTION, (ActionEvent e) -> setDifficulty.setText("Difficulty: " + diff(comp.getDifficulty())));
            return setDifficulty;
        }
        return new Label("");
    }

    //helper method for difficultyButton
    private String diff(int difficulty){
        if(difficulty%3 == 1){
            return "Easy";
        } else if(difficulty%3 == 2){
            return "Medium";
        } else{
            return "Hard";
        }
    }

    public void handle(ActionEvent e) {
        Button b = (Button) e.getSource();
        //set move and make changes to field
        currMove = null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (b == buttons[i][j]) {
                    try {
                        currMove = new Move(i, j);
                        setColor(b, decidePlayer);
                        b.setOpacity(1);
                        ArrayList<Integer[]> changes = game.getPointsToChange(currMove, decidePlayer);
                        changes.forEach(c -> setColor(buttons[c[0]][c[1]], decidePlayer));
                        game.makeMove(currMove, decidePlayer);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        gameStatus.setText("Invalid move!");
                    }
                }
            }
        }
        lastMove = currMove;
        decidePlayer++;
        //prepare for the next move
        getNextMoves();
    }

    public void getNextMoves(){
        //sleeper for ease of use
        Task<Void> sleeper = new Task<>() {
            @Override
            protected Void call() {
                try{
                    Random rand = new Random();
                    Thread.sleep(rand.nextInt(500)+500);
                    //Thread.sleep(100);
                }catch (InterruptedException e){}
                return null;
            }
        };
        sleeper.setOnSucceeded(workerStateEvent -> {
            gameStatus.setText(playerString(decidePlayer));
            Move m = comp.nextMove(lastMove, 8000, 0);
            buttons[m.x][m.y].fire();
        });

        ArrayList<Move> nextMoves = getValidMoves(decidePlayer);
        if(nextMoves.size() == 0){
            Move nextMove = null;
            //if the next move and the move after both have no valid options it checks for a winner
            if(getValidMoves(decidePlayer+1).size() == 0){
                Move nextNextMove = null;
                if (game.checkForWinner(nextMove, nextNextMove) != null){
                    gameStatus.setText(game.checkForWinner(nextMove, nextNextMove));
                    Label l = new Label(game.checkForWinner(nextMove, nextNextMove));
                    l.setFont(new Font("Helvetica", 30));
                    l.setPadding(new Insets(50, 50, 50, 50));
                    Stage s = new Stage();
                    s.setScene(new Scene(l));
                    s.show();
                    disableAllButtons();
                }
                //if playMode is set to single player the bot will make a move
            }
            else if (playMode == 1){
                decidePlayer++;
                gameStatus.setText(playerString(decidePlayer));
                if((decidePlayer)%2 == playerOrder){
                    currMove = null;
                    lastMove = null;
                    //added a sleeper to make it easier on the player
                    new Thread(sleeper).start();
                } else{
                comp.updateMove(lastMove);
                enableButtons(getValidMoves(decidePlayer));
                }
            }
            else {
                decidePlayer++;
                gameStatus.setText(playerString(decidePlayer));
                enableButtons(getValidMoves(decidePlayer));
            }
        }

        else if (playMode == 1){
            if(decidePlayer%2 == playerOrder){
                new Thread(sleeper).start();
            }
            //this is just to display the right text in the GUI
            if(playerOrder == 0){
                gameStatus.setText(playerString(decidePlayer+1));
            }else{
                gameStatus.setText(playerString(decidePlayer));
            }
            enableButtons(nextMoves);
        }
        else {
            gameStatus.setText(playerString(decidePlayer));
            enableButtons(nextMoves);
        }
    }

    //enables all the buttons that lead to a valid move
    public void enableButtons(ArrayList<Move> buttonsToEnable) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                //removes the styles from the previous turn
                if (buttons[i][j].getStyle().equals("-fx-border-color: #413bed; -fx-border-width: 5")){
                    buttons[i][j].setStyle("-fx-background-color: lightgray;");
                    buttons[i][j].setOpacity(0.1);
                }
                buttons[i][j].setDisable(true);
            }
        }
        //enables the button to press & marks it so players know where to click
        for (Move m : buttonsToEnable) {
            buttons[m.x][m.y].setDisable(false);
            //only makes buttons visible for players when the player can actually press them
            if(decidePlayer%2 != playerOrder) {
                buttons[m.x][m.y].setStyle("-fx-border-color: #413bed; -fx-border-width: 5");
                buttons[m.x][m.y].setOpacity(0.5);
            }
        }

    }

    public void disableAllButtons(){
        for (int i = 0; i<8; i++){
            for (int j = 0; j < 8; j++){
                buttons[i][j].setDisable(true);
            }
        }
    }

    //returns valid moves depending on the player
    public ArrayList<Move> getValidMoves(int counter) {
        ArrayList<Move> validMoves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (game.getBoard()[i][j] == null && game.getPointsToChange(new Move(i, j), counter) != null)
                    validMoves.add(new Move(i, j));
            }
        }
        return validMoves;
    }

    //sets color of the button
    public void setColor(Button button, int player){
        if (player % 2 != 0){
            button.setStyle("-fx-background-color: white;");
        } else{
            button.setStyle("-fx-background-color: black;");
        }
    }

    //gives the label for the gameStatus
    public String playerString(int decidePlayer){
        if(decidePlayer % 2 == 1 && playMode == 2){
            return "It's White's turn";
        } else if(decidePlayer % 2 == 0 && playMode == 2){
            return "It's Black's turn";
        }else if(decidePlayer%2 == 0 && playMode == 1) {
            return "Your turn!";
        } else{
            return "Computer is thinking";
        }
    }

    public String printValidMoves(ArrayList<Move> validMoves) {
        String valids = "Valid moves:\n";
        if (validMoves.size() > 0) {
            for (Move m : validMoves) {
                valids += "row: " + (m.x + 1) + " column: " + (m.y + 1) + "\n";
            }
        } else {
            valids += "no valid moves";
        }
        return valids;
    }
}
