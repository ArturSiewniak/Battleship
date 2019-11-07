package siwy;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import siwy.Board.Cell;

import java.util.Random;

public class Battleship extends Application {
    private boolean running = false;
    private Board playerBoard, enemyBoard;

    private int shipsToPlace = 5;
    private boolean enemyTurn = false;
    private Random random = new Random();

    private Parent createContent() {
        BorderPane root = new BorderPane();
        root.setPrefSize(600, 800);

        root.setRight(new Text("CONTROLS: Left click to place ships vertically, \nright click to place ships horizontally. " +
                "\n " +
                "\nHOW TO PLAY: You place ships in order: \n5-length, 4-length, 3-length, 2-length, 1-length. " +
                "\nYou can't place ships adjacent to other ships. \nAfter hitting a ship you get another move. " +
                "\nFirst to sink whole enemy fleet wins."));

        enemyBoard = new Board(true, event -> {
            if(!running){
                return;
            }

            Cell cell = (Cell) event.getSource();
            if (cell.wasShot){
               return;
            }

            enemyTurn = !cell.shoot();

            if (enemyBoard.ships == 0) {
                System.out.println("You win!");
                System.exit(0);
            }

            if (enemyTurn) {
                enemyMove();
            }
        });

        playerBoard = new Board(false, event ->{
            if (running){
                return;
            }

            Cell cell = (Cell) event.getSource();
            if (playerBoard.placeShip(new Ship(shipsToPlace, event.getButton() == MouseButton.PRIMARY), cell.x,
                    cell.y)){
                if(--shipsToPlace == 0){
                    startGame();
                }
            }
        });

        VBox vbox = new VBox(50, enemyBoard, playerBoard);
        vbox.setAlignment(Pos.CENTER);

        root.setCenter(vbox);

        return root;
    }

    private void enemyMove() {
        while (enemyTurn) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);

            Cell cell = playerBoard.getCell(x, y);
            if (cell.wasShot) {
                continue;
            }

            enemyTurn = cell.shoot();

            if (playerBoard.ships == 0){
                System.out.println("You lose!");
                System.exit(0);
            }
        }
    }

    private void startGame() {
        int type = 5;

        while (type > 0) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);

            if (enemyBoard.placeShip(new Ship(type, Math.random() < 0.5), x, y)){
                type--;
            }
        }

        running = true;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("Battleship by Siwy");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
