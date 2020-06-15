package src.com.LaurenzSommerlad.battleship;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.util.Duration;
import javafx.geometry.*;

public class BattleshipMain extends Application {

    private boolean running = false;
    private Board enemyBoard, playerBoard;

    private int shipsToPlace = 5;

    private boolean enemyTurn = false;

    private Random random = new Random();
    private GameMenu gameMenu;
    private Scene endScene;
    private GameMenu gameMenu1;

    private Parent root() throws IOException {
        BorderPane root = new BorderPane();
        root.setPrefSize(960, 540);

        enemyBoard = new Board(true, event -> {
            if (!running)
                return;

            Board.Cell cell = (Board.Cell) event.getSource();
            if (cell.wasShot)
                return;

            enemyTurn = !cell.shoot();

            if (enemyBoard.ships == 0) {
                try {
                    endScene = new Scene(player_won(true));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                window.setScene(endScene);
                System.out.println("YOU WIN");
                //System.exit(0); // implemtent exit screen
            }

            if (enemyTurn) {
                try {
                    enemyMove();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        playerBoard = new Board(false, event -> {
            if (running)
                return;

            Board.Cell cell = (Board.Cell) event.getSource();
            if (playerBoard.placeShip(new Ship(shipsToPlace, event.getButton() == MouseButton.PRIMARY), cell.x, cell.y)) {
                if (--shipsToPlace == 0) {
                    startGame();
                }
            }
        });

        HBox hbox = new HBox(340, enemyBoard, playerBoard);
        hbox.setAlignment(Pos.TOP_LEFT);

        //VBox stats = new VBox();
        //HBox stats = new HBox();
        //System.out.println(health);

        InputStream is = Files.newInputStream(Paths.get("GitHub/Battleship/src/com/LaurenzSommerlad/battleship/Battleship.jpg"));
        Image img = new Image(is);
        is.close();

        ImageView imgView = new ImageView(img);
        imgView.setFitWidth(960);
        imgView.setFitHeight(540);


        gameMenu = new GameMenu();
        gameMenu.setVisible(false);

        root.getChildren().addAll(imgView, hbox, gameMenu);

        //root.setCenter(hbox);

        return root;
    }

    public Parent player_won(boolean player_won) throws IOException {
        Scene EndScene;
        Text text;
        BorderPane endPane = new BorderPane();

        endPane.setPrefSize(850, 600);

        InputStream is = Files.newInputStream(Paths.get("GitHub/Battleship/src/com/LaurenzSommerlad/battleship/gameover"));
        Image endimg = new Image(is);
        is.close();

        ImageView endimgView = new ImageView(endimg);
        endimgView.setFitWidth(900);
        endimgView.setFitHeight(800);

        if(player_won){
            text = new Text("CONGRATULATIONS YOU WON !!!");
            text.setFont(Font.font(50));
            text.setFill(Color.GREEN);
            text.setX(50);text.setY(200);
        }
        else {
            text = new Text("ALL YOUR SHIPS WERE DESTROYED :( \nYOU SUCK!!!");
            text.setFont(Font.font(40));
            text.setFill(Color.RED);
            text.setX(100);text.setY(150);
            text.setTextAlignment(TextAlignment.CENTER);
        }

        //VBox vbox = new VBox(50);
        //MenuButton restart = new MenuButton("RESTART");
        //MenuButton Main_Menu = new MenuButton("Main Menu");
        //MenuButton exit = new MenuButton("Exit");
        //VBox vbox = new VBox(50, restart, Main_Menu, exit);
        //vbox.getChildren().addAll(restart);
        //vbox.setAlignment(Pos.BOTTOM_LEFT);

        gameMenu1 = new GameMenu();
        endPane.getChildren().addAll(endimgView, text,gameMenu1);

        return endPane;

    }

    /*private Parent createContent() {
        BorderPane root = new BorderPane();
        root.setPrefSize(600, 800);

        root.setRight(new Text("RIGHT SIDEBAR - CONTROLS"));

        enemyBoard = new Board(true, event -> {
            if (!running)
                return;

            Board.Cell cell = (Board.Cell) event.getSource();
            if (cell.wasShot)
                return;

            enemyTurn = !cell.shoot();

            if (enemyBoard.ships == 0) {

                System.out.println("YOU WIN");
                //System.exit(0); // implemtent exit screen
            }

            if (enemyTurn) {
                try {
                    enemyMove();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        playerBoard = new Board(false, event -> {
            if (running)
                return;

            Board.Cell cell = (Board.Cell) event.getSource();
            if (playerBoard.placeShip(new Ship(shipsToPlace, event.getButton() == MouseButton.PRIMARY), cell.x, cell.y)) {
                if (--shipsToPlace == 0) {
                    startGame();
                }
            }
        });

        VBox vbox = new VBox(50, enemyBoard, playerBoard);
        vbox.setAlignment(Pos.CENTER);

        root.setCenter(vbox);

        return root;
    }*/

    private void enemyMove() throws IOException {
        while (enemyTurn) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);

            Board.Cell cell = playerBoard.getCell(x, y);
            if (cell.wasShot)
                continue;

            enemyTurn = cell.shoot();

            if (playerBoard.ships == 0) {
                endScene = new Scene(player_won(false));
                window.setScene(endScene);
                //whindow.setScene(GameOver.player_won(false));
                System.out.println("YOU LOSE");
                //System.exit(0);
            }
        }
    }

    private void startGame() {
        // place enemy ships
        int type = 5;

        while (type > 0) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);

            if (enemyBoard.placeShip(new Ship(type, Math.random() < 0.5), x, y)) {
                type--;
            }
        }

        running = true;
    }
    private void reset() throws IOException {
        running = false;
        shipsToPlace=5;
        scene = new Scene(root());
        window.setScene(scene);
    }
    private int count =0;
    public class GameMenu extends Parent {

        public GameMenu() throws IOException {
            VBox menu0 = new VBox(10);
            VBox menu1 = new VBox(10);

            if (count >=2){
                menu0.setTranslateX(100);
                menu0.setTranslateY(400);

                menu1.setTranslateX(100);
                menu1.setTranslateY(400);
            }
            else {

                menu0.setTranslateX(50);
                menu0.setTranslateY(250);

                menu1.setTranslateX(50);
                menu1.setTranslateY(250);
            }

            final int offset = 400;

            menu1.setTranslateX(offset);

            MenuButton btnResume = new MenuButton("PLAY");
            if(count==0) {
                btnResume.setText(new Text("RESUME"));
            }
            if(count>=2){
                btnResume.setText(new Text("PLAY AGAIN"));
            }

            btnResume.setOnMouseClicked(event -> {
                FadeTransition ft = new FadeTransition(Duration.seconds(0.5), this);
                ft.setFromValue(1);
                ft.setToValue(0);
                ft.setOnFinished(evt -> setVisible(false));
                ft.play();
                window.setScene(scene);
                if(count<=2){
                    //displayAlert("Game Rules", "Hello");
                    System.out.println("Here should pop up an new window with game instructions");
                }
                if(count>=3){
                    try {
                        reset();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });
            count++;


            MenuButton btnOptions = new MenuButton("OPTIONS");
            btnOptions.setOnMouseClicked(event -> {
                getChildren().add(menu1);

                TranslateTransition tt = new TranslateTransition(Duration.seconds(0.25), menu0);
                tt.setToX(menu0.getTranslateX() - offset);

                TranslateTransition tt1 = new TranslateTransition(Duration.seconds(0.5), menu1);
                tt1.setToX(menu0.getTranslateX());

                tt.play();
                tt1.play();

                tt.setOnFinished(evt -> {
                    getChildren().remove(menu0);
                });
            });

            MenuButton btnExit = new MenuButton("EXIT");
            btnExit.setOnMouseClicked(event -> {
                System.exit(0);
            });

            MenuButton btnBack = new MenuButton("BACK");
            btnBack.setOnMouseClicked(event -> {
                getChildren().add(menu0);

                TranslateTransition tt = new TranslateTransition(Duration.seconds(0.25), menu1);
                tt.setToX(menu1.getTranslateX() + offset);

                TranslateTransition tt1 = new TranslateTransition(Duration.seconds(0.5), menu0);
                tt1.setToX(menu1.getTranslateX());

                tt.play();
                tt1.play();

                tt.setOnFinished(evt -> {
                    getChildren().remove(menu1);
                });
            });

            MenuButton btnSound = new MenuButton("SOUND");
            MenuButton btnVideo = new MenuButton("RETURN TO MAIN MENU");
            btnVideo.setOnMouseClicked(e -> {
                window.setScene(startScene);
                FadeTransition ft = new FadeTransition(Duration.seconds(0.5), gameMenu0);
                ft.setFromValue(0);
                ft.setToValue(1);
                gameMenu0.setVisible(true);
                ft.play();
            });

            menu0.getChildren().addAll(btnResume, btnOptions, btnExit);
            menu1.getChildren().addAll(btnBack, btnSound, btnVideo);

            Rectangle bg = new Rectangle(960, 540);
            bg.setOpacity(0);
            if(getScene() == scene) {
                bg.setFill(Color.GREY);
                bg.setOpacity(0.4);
            }

            getChildren().addAll(bg, menu0);
        }
    }

    public static class MenuButton extends StackPane {
        private Text text;

        public void setText(Text text) {
            this.getChildren().remove(1);
            this.text = text;
            text.setFont(text.getFont().font(20));
            text.setFill(Color.WHITE);
            getChildren().add(text);
        }

        public Text getText() {
            return text;
        }

        public MenuButton(String name) {
            text = new Text(name);
            text.setFont(text.getFont().font(20));
            text.setFill(Color.WHITE);

            Rectangle bg = new Rectangle(250, 30);
            bg.setOpacity(0.6);
            bg.setFill(Color.BLACK);
            bg.setEffect(new GaussianBlur(3.5));


            setAlignment(Pos.CENTER_LEFT);
            setRotate(-0.5);
            getChildren().addAll(bg, text);

            setOnMouseEntered(event -> {
                bg.setTranslateX(10);
                text.setTranslateX(10);
                bg.setFill(Color.WHITE);
                text.setFill(Color.BLACK);
            });

            setOnMouseExited(event -> {
                bg.setTranslateX(0);
                text.setTranslateX(0);
                bg.setFill(Color.BLACK);
                text.setFill(Color.WHITE);
            });

            DropShadow drop = new DropShadow(50, Color.WHITE);
            drop.setInput(new Glow());

            setOnMousePressed(event -> setEffect(drop));
            setOnMouseReleased(event -> setEffect(null));
        }
    }

    /*public static void displayAlert(String title, String message) {
        Stage window2 = new Stage();

        //Block events to other windows
        window2.initModality(Modality.APPLICATION_MODAL);
        window2.setTitle(title);
        window2.setMinWidth(250);

        //Label label = new Label(message);
        //Button closeButton = new Button("Close this window");
        //closeButton.setOnAction(e -> window2.close());

        VBox layout = new VBox(10);
        //layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);

        //Display window and wait for it to be closed before returning
        Scene scene = new Scene(layout);
        window2.setScene(scene);
        window2.showAndWait();
    }*/

    @Override
    public void start(Stage primaryStage) throws Exception {


        scene = new Scene(root());
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {

                if (!gameMenu.isVisible()) {
                    FadeTransition ft = new FadeTransition(Duration.seconds(0.5), gameMenu);
                    ft.setFromValue(0);
                    ft.setToValue(1);
                    System.out.println("Hello");
                    gameMenu.setVisible(true);
                    //gameMenu.toFront();
                    ft.play();
                }
                else {
                    FadeTransition ft = new FadeTransition(Duration.seconds(0.5), gameMenu);
                    ft.setFromValue(1);
                    ft.setToValue(0);
                    ft.setOnFinished(evt -> gameMenu.setVisible(false));
                    ft.play();
                }
            }
        });


        InputStream is = Files.newInputStream(Paths.get("GitHub/Battleship/src/com/LaurenzSommerlad/battleship/battleshipstart.jpg"));
        Image startimg = new Image(is);


        ImageView startimgView = new ImageView(startimg);
        startimgView.setFitWidth(750);
        startimgView.setFitHeight(422);

        Pane root0 = new Pane();
        root0.setPrefSize(750, 422);
        gameMenu0 = new GameMenu();
        root0.getChildren().addAll(startimgView, gameMenu0);
        gameMenu0.setVisible(true);

        startScene = new Scene(root0);
        primaryStage.setTitle("Battleship");
        window = primaryStage;
        window.setScene(startScene);
        primaryStage.setResizable(false);
        primaryStage.show();


    }
    public GameMenu gameMenu0;
    public Scene startScene;
    public Stage window;
    public Scene scene;
    public static void main(String[] args) {launch(args);}
}
