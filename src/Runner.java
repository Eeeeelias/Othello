import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Objects;

public class Runner extends Application implements EventHandler<ActionEvent> {

    GUI gui = new GUI();
    int mode = 5;
    int order = 5;
    Button multi = new Button("Play against \n another player");
    Button single = new Button("Play against \n an AI");
    Button orderBlack = new Button("Player should \n start");
    Button orderWhite = new Button("AI should \n start");

    @Override
    public void start(Stage stage) {
        //Set up the first window so the player can decide whether to play with another player or the AI
        GridPane selectMode;
        selectMode = makeInitButtons();
        selectMode.setStyle("-fx-background-color: green;");
        selectMode.setPadding(new Insets(50, 50, 50, 50));
        stage.setTitle("Select Gamemode");
        stage.setScene(new Scene(selectMode, Color.GREEN));
        stage.show();
    }

    @Override
    public void handle(ActionEvent event) {
        Button b = (Button) event.getSource();
        Stage stage1 = new Stage();
        if (Objects.equals(b.getText(), "Play against \n an AI")){
            mode = 1;
            b.setStyle("-fx-background-color: #1e2021; -fx-text-fill: white; -fx-border-color: #413bed; -fx-border-width: 4");
        } else if(Objects.equals(b.getText(), "Play against \n another player")){
            mode = 2;
        }
        if (Objects.equals(b.getText(), "AI should \n start")){
            order = 0;
            b.setStyle("-fx-background-color: white; -fx-border-color: #413bed; -fx-border-width: 4");
        } else if(Objects.equals(b.getText(), "Player should \n start")){
            order = 1;
            b.setStyle("-fx-background-color: #1e2021; -fx-text-fill: white; -fx-border-color: #413bed; -fx-border-width: 4");
        }
        if(order != 5 && mode != 5 || mode == 2){
            BorderPane mainLayout = gui.initGUI(0, mode, order);
            order = 5;
            mode = 5;
            multi.setStyle("-fx-background-color: white");
            single.setStyle("-fx-background-color: #1e2021; -fx-text-fill: white;");
            orderBlack.setStyle("-fx-background-color: #1e2021; -fx-text-fill: white;");
            orderWhite.setStyle("-fx-background-color: white");
            stage1.setTitle("Othello");
            stage1.setScene(new Scene(mainLayout, Color.GREEN));
            stage1.show();
        }
    }

    public GridPane makeInitButtons(){
        multi.setPrefSize(100, 100);
        multi.setStyle("-fx-background-color: white");
        multi.addEventHandler(ActionEvent.ACTION, this);
        single.addEventHandler(ActionEvent.ACTION, this);
        single.setPrefSize(100, 100);
        single.setStyle("-fx-background-color: #1e2021; -fx-text-fill: white;");
        orderBlack.setPrefSize(100,100);
        orderBlack.setStyle("-fx-background-color: #1e2021; -fx-text-fill: white;");
        orderBlack.addEventHandler(ActionEvent.ACTION, this);
        orderWhite.setPrefSize(100,100);
        orderWhite.setStyle("-fx-background-color: white");
        orderWhite.addEventHandler(ActionEvent.ACTION, this);
        GridPane pane = new GridPane();
        pane.add(multi, 0, 0);
        pane.add(single, 1, 0);
        pane.add(orderBlack, 0, 1);
        pane.add(orderWhite, 1, 1);
        return pane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
