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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    //Timer variabler
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
    private Label stopwatchTime;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("My first JavaFX Application");

        //Toppen av layouten (BorderPane top)
        Label applicationTitel = new Label();
        applicationTitel.setText("Medlemsformulär med tidtagarur");
        applicationTitel.setFont(Font.font("Arial Rounded MT Bold", 25));
        HBox borderPaneTop = new HBox();
        borderPaneTop.setAlignment(Pos.CENTER);
        borderPaneTop.getChildren().add(applicationTitel);

        //Botten av layouten (BorderPaneBottom)
        Label borderPaneBottomLabel = new Label();
        borderPaneBottomLabel.setText("Bekräftelse av sparad medlem visas här: ");
        borderPaneBottomLabel.setFont(Font.font("Arial Rounded MT Bold", 17));
        borderPaneBottomLabel.setVisible(false);

        Label savedMemberInfo = new Label();
        VBox saveMember = new VBox();
        saveMember.getChildren().addAll(borderPaneBottomLabel, savedMemberInfo);

        //Vänster av layouten (BorderPane left)
        Label registerMemberLabel = new Label();
        registerMemberLabel.setText("Registrera medlem:");
        registerMemberLabel.setFont(Font.font("Arial Rounded MT Bold", 18));

        TextField firstName = new TextField();
        firstName.setPromptText("Ange förnamn: ");

        TextField lastName = new TextField();
        lastName.setPromptText("Ange efternamn: ");

        TextField phoneNumber = new TextField();
        phoneNumber.setPromptText("Ange telefonnummer: ");

        TextField adress = new TextField();
        adress.setPromptText("Ange adress: ");

        Label insufficientInfo = new Label();
        insufficientInfo.setText("(Du måste fylla i alla fält för att spara!)");
        insufficientInfo.setFont(Font.font(16));
        insufficientInfo.setVisible(false);


        Button saveButton = new Button();
        saveButton.setText("Spara Medlem");
        saveButton.setOnAction(e -> {
            boolean correctInput = enoughMemberInfoOrNot(firstName, lastName, phoneNumber, adress);
            if (!correctInput) {
                insufficientInfo.setVisible(true);
            } else {
                borderPaneBottomLabel.setVisible(true);
                insufficientInfo.setVisible(false);
                saveMember(firstName, lastName, phoneNumber, adress);
                savedMemberInfo.setText(printSavedMember(firstName, lastName, phoneNumber, adress));
                firstName.clear();
                lastName.clear();
                phoneNumber.clear();
                adress.clear();
            }
        });


        VBox registerMember = new VBox();
        registerMember.setSpacing(15);
        registerMember.getChildren().addAll(registerMemberLabel, firstName, lastName, phoneNumber,
                adress, saveButton, insufficientInfo);

        //Höger av layouten (BorderPane right)
        Label stopwatchName = new Label();
        stopwatchName.setText("Tidtagarur: ");
        stopwatchName.setFont(Font.font("Arial Rounded MT Bold", 18));
        stopwatchTime = new Label();
        stopwatchTime.setFont(Font.font(25));
        stopwatchTime.setText("00:00:00");


        start = new Button();
        start.setText("Starta");
        start.setOnAction(e -> {
            activateStopwatch();
            Thread worker = new Thread(() -> {
                while (timerRunning) {
                    startStopwatch();
                    Platform.runLater(() -> {
                        stopwatchTime.setText(String.format("%02d:%02d:%02d", timerHours, timerMinutes,
                                timerSeconds));
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        System.out.println("Tråd avbruten");
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
        buttons.getChildren().addAll(start, stop, reset);

        VBox stopwatch = new VBox();
        stopwatch.setSpacing(20);
        stopwatch.getChildren().addAll(stopwatchName, stopwatchTime, buttons);

        //Root layouten (övergripande layouten för scenen(yttersta skalet)
        BorderPane rootLayout = new BorderPane();
        rootLayout.setTop(borderPaneTop);
        rootLayout.setBottom(saveMember);
        rootLayout.setLeft(registerMember);
        rootLayout.setRight(stopwatch);

        //Scenen
        Scene scene = new Scene(rootLayout, 800, 800);

        stage.setScene(scene);
        rootLayout.requestFocus();
        stage.show();
    }

    public boolean enoughMemberInfoOrNot(TextField firstName, TextField lastName, TextField phoneNumber, TextField adress) {
        if (firstName.getText().isEmpty() || lastName.getText().isEmpty() || phoneNumber.getText().isEmpty()
                || adress.getText().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public String printSavedMember(TextField firstName, TextField lastName, TextField phoneNumber, TextField adress) {
        return "Sparad: " + firstName.getText() + " " + lastName.getText() + ", " + phoneNumber.getText()
                + ", " + adress.getText();
    }

    public void saveMember(TextField firstName, TextField lastName, TextField phoneNumber, TextField adress) {
        String file = "savedMembers.txt";
        try (BufferedWriter toFile = new BufferedWriter(new FileWriter(file, true));) {
            toFile.write("Namn: " + firstName.getText() + " " + lastName.getText() + "\n");
            toFile.write("Telefonnummer: " + phoneNumber.getText() + "\n");
            toFile.write("Adress: " + adress.getText() + "\n");
            toFile.write("\n");
        } catch (IOException e) {
            System.out.println("Fel vid sparandet till fil: " + e.getMessage());
        }
    }

    public void startStopwatch() {
        totalSeconds.incrementAndGet();
        timerSeconds = totalSeconds.get() % 60;
        timerMinutes = (totalSeconds.get() / 60) % 60;
        timerHours = totalSeconds.get() / 3600;
    }

    public void stopStopwatch() {
        stop.setDisable(true);
        timerRunning = false;
        start.setDisable(false);
        reset.setDisable(false);
    }

    public void resetStopwatch() {
        stop.setDisable(true);
        reset.setDisable(true);
        totalSeconds.set(0);
        stopwatchTime.setText("00:00:00");
        start.requestFocus();
    }

    public void activateStopwatch() {
        timerRunning = true;
        start.setDisable(true);
        stop.setDisable(false);
        reset.setDisable(true);
    }
}
