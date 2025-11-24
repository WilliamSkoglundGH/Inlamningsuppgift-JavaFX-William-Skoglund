package com.skoglund;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicInteger;

public class Main extends Application {
    public static void main(String[] args){
        launch(args);
    }
    //Timer variables
    private final AtomicInteger totalSeconds = new AtomicInteger(0);
    private int timerSeconds = 0;
    private int timerMinutes = 0;
    private int timerHours = 0;
    private boolean timerRunning = false;

    //TimerButtons
    private Button start;
    private Button stop;
    private Button reset;

    //Label som flera metoder behöver tillgång til
    private Label time;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("My first JavaFX Application");

        //Toppen av layouten (BorderPane top)
        Label borderPaneTopLabel = new Label();
        borderPaneTopLabel.setText("Medlemsformulär med tidtagarur");
        borderPaneTopLabel.setFont(Font.font("Arial Rounded MT Bold", 25));
        HBox borderPaneTop = new HBox();
        borderPaneTop.setAlignment(Pos.CENTER);
        borderPaneTop.getChildren().add(borderPaneTopLabel);

        //Botten av layouten (BorderPaneBottom)
        Label borderPaneBottomLabel = new Label();
        borderPaneBottomLabel.setText("Bekräftelse av sparad medlem visas här: ");
        borderPaneBottomLabel.setFont(Font.font("Arial Rounded MT Bold", 17));
        Label savedMember = new Label();
        VBox borderPaneBottom = new VBox();
        borderPaneBottom.getChildren().addAll(borderPaneBottomLabel, savedMember);

        //Vänster av layouten (BorderPane left)
        Label borderPaneLeftLabel = new Label();
        borderPaneLeftLabel.setText("Registrera medlem:");
        borderPaneLeftLabel.setFont(Font.font("Arial Rounded MT Bold", 18));

        TextField firstName = new TextField();
        firstName.setPromptText("Ange förnamn: ");

        TextField lastName = new TextField();
        lastName.setPromptText("Ange efternamn: ");

        TextField phoneNumber = new TextField();
        phoneNumber.setPromptText("Ange telefonnummer: ");

        TextField adress = new TextField();
        adress.setPromptText("Ange adress: ");

        Button saveButton = new Button();
        saveButton.setText("Spara Medlem");
        saveButton.setOnAction(e ->{
            savedMember.setText(printSavedMember(firstName,lastName,phoneNumber,adress));
            firstName.clear();
            lastName.clear();
            phoneNumber.clear();
            adress.clear();
        } );


        VBox borderPaneLeft = new VBox();
        borderPaneLeft.setSpacing(15);
        borderPaneLeft.getChildren().addAll(borderPaneLeftLabel, firstName, lastName, phoneNumber, adress, saveButton);

        //Höger av layouten (BorderPane left)
        Label stopwatchName = new Label();
        stopwatchName.setText("Tidtagarur: ");
        stopwatchName.setFont(Font.font("Arial Rounded MT Bold", 18));
        time = new Label();
        time.setFont(Font.font(25));
        time.setText("00:00:00");


        start = new Button();
        start.setText("Starta");
            start.setOnAction(e -> {
                activateStopwatch();
                    Thread worker = new Thread(() -> {
                        while (timerRunning) {
                            startStopwatch();
                            Platform.runLater(() -> {
                                time.setText(String.format("%02d:%02d:%02d", timerHours, timerMinutes, timerSeconds));
                            });
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                            }
                        }
                    });
                    worker.setDaemon(true);
                    worker.start();

            });

        stop = new Button();
        stop.setText("Stoppa");
        stop.setDisable(true);
        stop.setOnAction(e -> {
            stopStopwatch();
        });

        reset = new Button();
        reset.setText("Nollställ");
        reset.setDisable(true);
        reset.setOnAction(e -> {
            resetStopwatch();
        });

        HBox buttons = new HBox();
        buttons.setSpacing(10);
        buttons.getChildren().addAll(start,stop,reset);

        VBox borderPaneRight = new VBox();
        borderPaneRight.setSpacing(20);
        borderPaneRight.getChildren().addAll(stopwatchName, time, buttons);

        //Root layouten (övergripande layouten för scenen(yttersta skalet)
        BorderPane rootLayout = new BorderPane();
        rootLayout.setTop(borderPaneTop);
        rootLayout.setBottom(borderPaneBottom);
        rootLayout.setLeft(borderPaneLeft);
        rootLayout.setRight(borderPaneRight);

        //Scenen
        Scene scene = new Scene(rootLayout, 800, 800);
        stage.setScene(scene);
        rootLayout.requestFocus();
        stage.show();


    }

    public String printSavedMember(TextField firstName, TextField lastName, TextField phoneNumber, TextField adress){
        return "Sparad: " + firstName.getText() + " " + lastName.getText() + ", " + phoneNumber.getText()
        + ", " + adress.getText();
    }
    public void startStopwatch(){
        totalSeconds.incrementAndGet();
        timerSeconds = totalSeconds.get() % 60;
        timerMinutes = (totalSeconds.get() / 60) % 60;
        timerHours = totalSeconds.get() / 3600;
    }
    public void stopStopwatch(){
        stop.setDisable(true);
        timerRunning = false;
        start.setDisable(false);
        reset.setDisable(false);
    }

    public void resetStopwatch(){
        stop.setDisable(true);
        reset.setDisable(true);
        totalSeconds.set(0);
        time.setText("00:00:00");
        start.requestFocus();
    }

    public void activateStopwatch(){
        timerRunning = true;
        start.setDisable(true);
        stop.setDisable(false);
        reset.setDisable(true);
    }
}
