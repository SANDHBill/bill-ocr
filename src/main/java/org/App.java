package org;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
//import java.util.ResourceBundle.Control;
import java.util.concurrent.atomic.AtomicInteger;

import org.opencv.core.Core;

public class App extends Application {
	private void setButtonLoadImage(Button btnLoadImage,DrawingPaneController c) {
		btnLoadImage.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				c.handleButtonLoadImage(e);
			}
		});
	}
	private void setButtonSaveImage(Button btnSaveImage,DrawingPaneController c) {
		btnSaveImage.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				c.handleButtonSaveImage(e);
			}
		});
	}
	private void setButtonFilter(Button btnFilter,DrawingPaneController c) {
		btnFilter.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				c.handleButtonFilter(e);
			}
		});
	}
	private void setButtonRestart(Button btnRestart,DrawingPaneController c) {
		btnRestart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				c.handleButtonRestart(e);
			}
		});
	}
	private void setButtonOCR(Button btnOCR,DrawingPaneController c) {
		btnOCR.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				c.handleButtonOCR(e);
			}
		});
	}
	private void addHBox(BorderPane pane,DrawingPaneController c) {
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10);
		hbox.setStyle("-fx-background-color: #336699;");
		Button buttonLoadImage = new Button("Load Image");
		buttonLoadImage.setPrefSize(100, 20);
		Button buttonSaveImage = new Button("Save selected");
		buttonSaveImage.setPrefSize(100, 20);
		Button buttonFilter = new Button("Filter");
		buttonFilter.setPrefSize(100, 20);
		Button buttonRestart = new Button("Restart");
		buttonRestart.setPrefSize(100, 20);
		Button buttonOCR = new Button("OCR");
		buttonOCR.setPrefSize(100, 20);
		hbox.getChildren().addAll(buttonLoadImage, buttonSaveImage, buttonFilter, buttonRestart, buttonOCR);
		pane.setTop(hbox);
		setButtonLoadImage(buttonLoadImage, c);
		setButtonSaveImage(buttonSaveImage, c);
		setButtonFilter(buttonFilter, c);
		setButtonRestart(buttonRestart,c);
		setButtonOCR(buttonOCR,c);
	}
	public void setPoly(Polygon poly, BorderPane pan ,DrawingPaneController c, Map<Integer,Circle> mapCircles)
	{
		poly.setFill(Color.web("ANTIQUEWHITE", 0.8));
		poly.setStroke(Color.web("ANTIQUEWHITE"));
		poly.setStrokeWidth(2);	
		pan.getChildren().addAll(poly);
		for (int i = 0; i < poly.getPoints().size(); i += 2) {
			Circle circle = addCircle(poly, i);
			mapCircles.put(i/2, circle);
			setDragHandler(circle,c);
			pan.getChildren().add(circle);
		}
	}
	private Circle addCircle(Polygon poly, int i) {
		Circle circle = new Circle(poly.getPoints().get(i), poly.getPoints().get(i + 1), 5);
		circle.setFill(Color.web("PERU", 0.8));
		circle.setStroke(Color.PERU);
		circle.setStrokeWidth(2);

		AtomicInteger polyCoordinateIndex = new AtomicInteger(i);
		circle.centerXProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				poly.getPoints().set(polyCoordinateIndex.get(), newValue.doubleValue());
			}
		});
		circle.centerYProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				poly.getPoints().set(polyCoordinateIndex.get() + 1, (Double) newValue);
			}
		});
		
		return circle;
	}
	private void setDragHandler(Circle circle, DrawingPaneController c) {
		circle.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				c.handleOnMousePressed(mouseEvent, circle);
			}
		});

		circle.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				c.handleOnMouseDragged(mouseEvent, circle);
			}
		});

		circle.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				circle.setCursor(Cursor.HAND);
			}
		});

		circle.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				circle.setCursor(Cursor.HAND);
			}
		});
	}
	@Override
	public void start(Stage stage) {
		Image image;
		image = new Image("file:/Users/user/Documents/workspace/BillAnalyser/resource/reciepts/i5.jpg");
		Polygon poly = new Polygon(50, 80, 350, 80, 350, 400, 100, 400);
		BorderPane drawingPane = new BorderPane(poly);
		ImageView im = new ImageView(image);
		im.setFitWidth(250);
		im.setPreserveRatio(true);
		drawingPane.setCenter(im);
		Map<Integer,Circle> mapCircles = new HashMap<Integer,Circle>();
		DrawingPaneController conroller = new DrawingPaneController();
		conroller.setPointsSelect(50, 80, 350, 80, 350, 400, 100, 400);
		setPoly(poly, drawingPane, conroller, mapCircles);
		addHBox(drawingPane, conroller);
		
		Scene scene = new Scene(drawingPane, 450, 300);
		stage.setScene(scene);
		
		conroller.setOriginalImage(image);
		conroller.stage = stage;
		conroller.im = im;
		conroller.poly = poly;
		
		stage.show();
	}
	public static void main(String[] args) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME);
		launch(args);
	}
}
