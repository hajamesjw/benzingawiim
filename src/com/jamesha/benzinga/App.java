package com.jamesha.benzinga;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class App extends Application {

  ToggleButton tbOnOff;
  Label enumOutputLabel;
  ScrollPane enumOutputPane;
  boolean autoRefresh = false;

  public static void main(String[] args) {
    System.out.println("Launching the enum builder application.");
    //Start the application by calling launch().
    launch(args);
  }

  @Override
  public void init() {

  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    //Stage title
    primaryStage.setTitle("Invalid WIIM fetcher");

    //Use FlowPane for rootNode
    FlowPane rootNode = new FlowPane(10, 10);
    rootNode.setAlignment(Pos.TOP_LEFT);

    //Create a scene
    Scene myScene = new Scene(rootNode, 800, 900);

    //Set the scene on the stage
    primaryStage.setScene(myScene);
    
    //Create a title label for time in minutes
    Label timeLabel = new Label("Auto-Refresh Interval (min): ");

    //Enum name with TextField
    TextField time = new TextField();
    time.setPrefColumnCount(2);
    
    //Create and handle the actions of the refresh button;
    Button refreshButton = new Button("Refresh");

//    refreshButton.setOnAction(new EventHandler<ActionEvent>() {
//      @Override
//      public void handle (ActionEvent actionEvent) {
//        //Set fields on inputModificationHelper
//        enumOutputPane.setContent(enumOutputLabel);
//        while (autoRefresh) {
//            try {
//            	int minutes = Integer.parseInt(time.getText());
//				Thread.sleep(minutes * 60000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//        }
//      }
//    });
    
    //Handle the events of the 'refresh' button
    refreshButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle (ActionEvent actionEvent) {
        //Set fields on inputModificationHelper
    	try {
			enumOutputLabel = new Label(JsonReader.checkForInvalidArticles2());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        enumOutputPane.setContent(enumOutputLabel);
 

      }
    });
    
    //Manual vs automatic
    RadioButton manual = new RadioButton("Manual-Refresh");
    RadioButton auto = new RadioButton("Auto-Refresh");
    ToggleGroup toggleGroup = new ToggleGroup();
    manual.setToggleGroup(toggleGroup);
    auto.setToggleGroup(toggleGroup);
    
      manual.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle (ActionEvent actionEvent) {
        	autoRefresh = false;
        }
      });

      auto.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle (ActionEvent actionEvent) {
        	autoRefresh = true;

        }
      });



    //Output
    enumOutputLabel = new Label("");
    enumOutputPane = new ScrollPane();
    enumOutputPane.setContent(enumOutputLabel);
    enumOutputPane.setPrefViewportWidth(800);
    enumOutputPane.setPrefViewportHeight(800);
    enumOutputPane.setPannable(true);
    enumOutputPane.getAccessibleText();

  

    HBox hBox = new HBox();
    GridPane gridPane = new GridPane();
    gridPane.setVgap(20);

    //Adding the buttons

    rootNode.getChildren().addAll(enumOutputPane);
    rootNode.getChildren().addAll(refreshButton);
//    rootNode.getChildren().addAll(timeLabel);
//    rootNode.getChildren().addAll(time);
//    rootNode.getChildren().addAll(manual, auto);

    primaryStage.show();
    
    while (autoRefresh) {
		refreshButton.fire();
    	int minutes = Integer.parseInt(time.getText());
		try {
			Thread.sleep(minutes * 60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
    //TODO Preview screen

    //TODO Copy to clipboard
  }

  final void setContent(Label label) {
    enumOutputPane.setContent(label);
  }

  private Separator createSeparator() {
    Separator separator = new Separator();
    separator.setPrefWidth(100);
    return separator;

  }

  @Override
  public void stop() {

  }

}
