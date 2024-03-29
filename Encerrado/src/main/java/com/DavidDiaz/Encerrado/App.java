package com.DavidDiaz.Encerrado;


import java.util.Optional;

import com.DavidDiaz.Encerrado.Board.Player;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Provides methods to show the game state trough a Window, and to evolve the game trought user input.
 * @author Jose David Garcia Diaz
 */
public class App extends Application {

    static Scene scene;
    Group root;
    static Stage mainWindow;
    static Circle[] circles;
    static Text turnIndicator;
    static Text configInfo;
    static Text anuncio;

    public static void main(String args[]){
        launch();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        root = new Group();
        scene = new Scene(root, Color.BLACK);
        Stage s = new Stage();
        s.setScene(scene);
        Image board = new Image("Tablero.png");
        ImageView imageView = new ImageView(board);
        
        //Set the tokens in the windows and the action for when each one is clicked
        circles = new Circle[5];
        for(int i=0; i<5; i++){
            circles[i] = new Circle();
            circles[i].setRadius(50);
        }

        circles[0].setCenterX(104);
        circles[0].setCenterY(72);
        circles[0].setOnMouseClicked((event) -> {
                GameManager.makeMove(0);
        });

        circles[1].setCenterX(664);
        circles[1].setCenterY(72);
        circles[1].setOnMouseClicked((event) -> {
                GameManager.makeMove(1);
        });

        circles[3].setCenterX(101);
        circles[3].setCenterY(506);
        circles[3].setOnMouseClicked((event) -> {
                GameManager.makeMove(3);
        });

        circles[2].setCenterX(385);
        circles[2].setCenterY(300);
        circles[2].setOnMouseClicked((event) -> {
                GameManager.makeMove(2);
        });

        circles[4].setCenterX(668);
        circles[4].setCenterY(506);
        circles[4].setOnMouseClicked((event) -> {
                GameManager.makeMove(4);
        });

        //Add turn indicator to window
        turnIndicator = new Text();
        turnIndicator.setTranslateX(300);
        turnIndicator.setTranslateY(30);

        //Add the config info to window
        configInfo = new Text();
        configInfo.setTranslateX(300);
        configInfo.setTranslateY(400);

        //Add text that will show who wins
        anuncio = new Text();
        anuncio.setTranslateX(300);
        anuncio.setTranslateY(500);

        //Add tokens to the scene root
        root.getChildren().add(imageView);
        for(int i=0; i<5; i++){
            root.getChildren().add(circles[i]); 
        }

        //Set the button that change IA MODE
        Button changeIAMode = new Button("Cambiar modo de la IA");
        changeIAMode.setOnAction(e->{
            GameManager.iaMiniMaxMode = !GameManager.iaMiniMaxMode;
            updateConfigInfo();
        });
        changeIAMode.setTranslateX(300);
        changeIAMode.setTranslateY(550);

        //Add everything to the scene root
        root.getChildren().add(changeIAMode);
        root.getChildren().add(turnIndicator);
        root.getChildren().add(configInfo);
        root.getChildren().add(anuncio);
        s.show();

        //Set up the game, and update al visuals
        setUpGame();
        GameManager.updateVisualsAndCheckForWinner();
        configInfo.setText(getConfigInfo());

        //If IA is the player that moves first make that move
        if(GameManager.playingAgainstIA && GameManager.board.currentTurn == GameManager.iaPlayer){
            PauseTransition p = new PauseTransition(Duration.millis(2000));
            p.play();
            p.setOnFinished(e->{
                GameManager.makeIAMove();
            });
        }
           
    }

    /**Shows an alert to the user
     * @param title
     * @param message
     */
    public static void showAlertToUser(String title, String message){
        Alert alert = new Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setResizable(true);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Ask for user input
     * @param question
     * @param formatDescription
     * @param defaultValue
     * @return
     */
    public static String askForUserInput(String question,String formatDescription, String defaultValue){
        TextInputDialog textInputDialog = new TextInputDialog(defaultValue);
        textInputDialog.setResizable(true);
        textInputDialog.setHeaderText(question);
        textInputDialog.setContentText(formatDescription);
        
        Optional<String> result = textInputDialog.showAndWait();
        if(result.isPresent() ){
            String resultMayus = result.get().toUpperCase();
            return resultMayus;
        }
        else {
            System.exit(0);
            return "";
        }
            
    }

    /**
     * Show relevant info to the user
     * @param title
     * @param message
     */
    public static void showMessageToUser(String message){
        anuncio.setText(message);
    }

    /**Updates colors of tokens in the board */
    public static void updateTokens(){
        for(int i=0; i<5; i++){
            if(GameManager.board.positions[i] == Player.Red)
                circles[i].setFill(Color.RED);
            if(GameManager.board.positions[i] == Player.Blue)
                circles[i].setFill(Color.BLUE);
            if(GameManager.board.positions[i] == Player.None)
                circles[i].setFill(Color.TRANSPARENT);
        }
    }

    /**
     * Updates turn indicator
     */
    public static void updateTurnIndicator(){
        if(GameManager.board.currentTurn == Player.Blue){
            turnIndicator.setText("Turno actual: Jugador azul");
            turnIndicator.setFill(Color.BLUE);
        }            
        else{
            turnIndicator.setText("Turno actual: Jugador Rojo");
            turnIndicator.setFill(Color.RED);
        }
    }

    /**
     * Returns a string with the game configuration
     * @return
     */
    public static String getConfigInfo(){
        String info = "";
        if(GameManager.playingAgainstIA){
            info += "Jugando contra IA \n";
            info += "Color de la IA: " + Board.playerToStr(GameManager.iaPlayer) + "\n";
        }    
        else
            info += "Jugando localmente \n";
        info += "Tipo de IA: ";
        if(GameManager.iaMiniMaxMode)
            info+= " Minimax";
        else
            info+= " Azar";
        return info;
    }

    /**
     * Update config info showing in the window
     */
    public static void updateConfigInfo(){
        configInfo.setText(getConfigInfo());
    }

    /**
     * Set up for the game
     */
    public static void setUpGame(){
        //Set if player plays against IA
        String playAgainstIA = "";
        while( !playAgainstIA.equals("IA") && !playAgainstIA.equals("LOCAL")){
            playAgainstIA = askForUserInput("¿Quieres jugar contra la IA o localmente contra otro jugador? ", "Escribe: IA o LOCAL", "IA");
            if( !playAgainstIA.equals("IA") && !playAgainstIA.equals("LOCAL")){
                showAlertToUser("FORMATO INVÁLIIDO", "Por favor introduce un formato válido");
            }
        }
        if(playAgainstIA.equals("IA")){
            GameManager.playingAgainstIA = true;
        }else{
            GameManager.playingAgainstIA = false;
        }

        //Select IA color
        if(GameManager.playingAgainstIA){
            String iaColor = "";
            while( !iaColor.equals("ROJO") && !iaColor.equals("AZUL")){
                iaColor = askForUserInput("Elige el color de la IA ", "Escribe: ROJO o AZUL", "ROJO");
                if( !iaColor.equals("ROJO") && !iaColor.equals("AZUL")){
                    showAlertToUser("FORMATO INVÁLIIDO", "Por favor introduce un formato válido");
                }
            }

            if(iaColor.equals("ROJO")){
                GameManager.iaPlayer = Player.Red;
            }else{
                GameManager.iaPlayer = Player.Blue;
            }

            String iaMode = "";
            while( !iaMode.equals("AZAR") && !iaMode.equals("MINIMAX")){
                iaMode = askForUserInput("Elige el tipo de IA ", "Escribe: AZAR o  MINIMAX", "MINIMAX");
                if( !iaMode.equals("AZAR") && !iaMode.equals("MINIMAX")){
                    showAlertToUser("FORMATO INVÁLIIDO", "Por favor introduce un formato válido");
                }
            }
            if(iaMode.equals("MINIMAX")){
                GameManager.iaMiniMaxMode = true;
            }
            else
                GameManager.iaMiniMaxMode = false;
        }

        //Select starter player
        String starterPlayer = "";
        while( !starterPlayer.equals("AZUL") && !starterPlayer.equals("ROJO")){
            starterPlayer = askForUserInput("¿Que jugador quieres que empiece? ", "Escribe: ROJO o AZUL", "AZUL");
            if( !starterPlayer.equals("AZUL") && !starterPlayer.equals("ROJO")){
                showAlertToUser("FORMATO INVÁLIIDO", "Por favor introduce un formato válido");
            }
        }
        Player starter = Player.None;
        if(starterPlayer.equals("ROJO")){
            starter = Player.Red;
        }else{
            starter = Player.Blue;
        }

        //Select board configuration 
        String tokenConfig = "";
        boolean error = true;
        boolean defaultConfig = false;
        Player[] tokens = new Player[5];
        while(error){
            error = false;
            tokenConfig = askForUserInput("Introduce la configuración de fichas, o escribe DEFAULT", 
            "Favor de leer el archivo readme para más información de como introducir la configuración de fichas. ", "DEFAULT");
            if(tokenConfig.equals("DEFAULT")){
                defaultConfig = true;
                continue;
            }
            String[] tokenPositions = tokenConfig.split(":");
            if(tokenPositions.length != 5){
                error = true;
                continue;
            }
            int blue =0;
            int red = 0;
            int none =0;
            for(int i=0; i<5; i++){
                if(!tokenPositions[i].equals("A") && !tokenPositions[i].equals("R") && !tokenPositions[i].equals("N")){
                    error = true;
                    continue;
                }
                if(tokenPositions[i].equals("R")){
                    tokens[i] = Player.Red;
                    red++;
                }
                if(tokenPositions[i].equals("A")){
                    tokens[i] = Player.Blue;
                    blue++;
                }
                if(tokenPositions[i].equals("N")){
                    tokens[i] = Player.None;
                    none++;
                }                
            }
            if( blue != 2 || red != 2 || none != 1)
                error = true;
        }
        if(defaultConfig)
            GameManager.board = new Board(starter);
        else
            GameManager.board = new Board(tokens, starter);
    }
    
}
