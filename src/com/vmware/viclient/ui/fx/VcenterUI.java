/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vmware.viclient.ui.fx;

import java.util.ArrayList;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.util.Map;
import javafx.application.*;
import javafx.scene.input.*;

/**
 *
 * @author praveen
 */
public class VcenterUI extends Application implements EventHandler<InputEvent> {
    
    ArrayList<javafx.scene.Node> list = new ArrayList<javafx.scene.Node>();
    SimpleIntegerProperty id;
    Props prop = null;
    Group content = new Group();
    TranslateTransition fadeOut, fadeIn;
    ImageView backButtonView = new ImageView();
    public Stage primaryStage = null;
    int popupWidth = 200;
    int popupHeight = 400;
    int popupX = 0;
    int popupY = 0;
    private static VcenterUI app = null;
    
    public SimpleIntegerProperty idProperty() {
        if (id == null) id = new SimpleIntegerProperty();
        this.id.addListener(new ChangeListener<Number>() {      
            @Override      
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //System.out.println("ID VAL: " + newValue.intValue()); 
                if (newValue.intValue() <=1) {
                    backButtonView.setVisible(false);
                } else {
                    backButtonView.setVisible(true);
                }
            }
        });    
        return id;
    }
    
    public int getId() {
        return this.idProperty().get();
    } 
    
    public void setId(int val) {
        this.idProperty().set(val);
    }
    
    public javafx.scene.Node constructHeader() {
        HBox header = new HBox();
        Image backButton = new Image("resources/backbutton.png");
        backButtonView.setImage(backButton);
        backButtonView.setFitWidth(50);
        backButtonView.setFitHeight(50);
        backButtonView.setPreserveRatio(true);
        backButtonView.setSmooth(true);
        
        backButtonView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                navigateBackward(false);
            }
        });
        
        Image closeButton = new Image("resources/Button-Close-icon.png");
        ImageView closeButtonView = new ImageView();
        closeButtonView.setImage(closeButton);
        closeButtonView.setFitWidth(40);
        closeButtonView.setFitHeight(40);
        closeButtonView.setPreserveRatio(true);
        closeButtonView.setSmooth(true);
        closeButtonView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                System.out.println("Mouse clicked");
                primaryStage.close();
                //Platform.exit();
            }
        });

        
        Text title = new Text();
        title.setFont(Font.font("Arial", FontWeight.BOLD, 15.0d));
        title.setTextAlignment(TextAlignment.CENTER);
        title.setText("javafx.scene.Node Properties");
        title.setWrappingWidth(100);
        title.setFill(Color.WHITE);
        
        header.getChildren().add(backButtonView);
        header.getChildren().add(title);
        header.getChildren().add(closeButtonView);
        
        VBox box = new VBox();
        box.getChildren().add(header);
        Line line = new Line();
        line.setTranslateX(popupX + 10.0f);
        line.setTranslateY(popupY);
        line.setEndX(popupWidth - 20.0f);
        line.setEndY(0.0f);
        line.setStrokeWidth(4);
        line.setStroke(Color.WHITE);
        
        box.getChildren().add(line);
        box.getChildren().add(content);
        return box;
    }
    
    public static VcenterUI getInstance() {
        return app;
    }
    
    
    public void constructUI(Props p, int lx, int ly) {
        prop = p;
        System.out.println(lx);
        System.out.println(ly);
        Group root = new Group();
        Rectangle rect = new Rectangle(0, 0, popupWidth, popupHeight);
        rect.setArcHeight(20);
        rect.setArcWidth(20);
        Stop[] stops = new Stop[] { new Stop(0, Color.BLACK), new Stop(1, Color.GRAY)};
        LinearGradient lg1 = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);

        rect.setFill(lg1);
        root.getChildren().add(rect);
        Scene scene = new Scene(root);
        scene.setFill(new Color(0,0,1,0.0));
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        
        /*Map<String, String> args = getParameters().getNamed();
        int locX = (Integer.valueOf(args.get("locY")) !=0)?Integer.valueOf(args.get("locY")):0;
        int locY = (Integer.valueOf(args.get("locY")) !=0)?Integer.valueOf(args.get("locY")):0;
        */
        primaryStage.setX(lx);
        primaryStage.setY(ly);
        primaryStage.show();
        VBox box = (VBox) constructHeader();
        root.getChildren().add(box);
        
        setId(0);
        loadNextSet();
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; 
        Platform.setImplicitExit(false);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        app = this;
        //constructUI(new Props());
    }
    
    public void navigateForward(final javafx.scene.Node view) {
        cleanup();
        fadeOut = new TranslateTransition(Duration.millis(500), content);
        fadeOut.setFromX(popupX);
        fadeOut.setToX(-popupWidth);
        
        fadeIn = new TranslateTransition(Duration.millis(500), content);
        fadeIn.setFromX(popupWidth + 10);
        fadeIn.setToX(popupX + 10);
        
        fadeOut.setOnFinished(new EventHandler<ActionEvent> () {
            public void handle(ActionEvent ae) {
                content.getChildren().add(view);
                if (view instanceof ListView) {
                    ((ListView)view).getFocusModel().focus(0);
                }
                fadeIn.play();
            }
        });
        fadeOut.play();
    }
    
    public void cleanup() {
        int index = 0;
        while (index < content.getChildren().size()) {
            javafx.scene.Node xx = content.getChildren().get(index);
            if (xx instanceof ListView || xx instanceof VBox) {
                content.getChildren().remove(xx);
            }
            index ++;
        }
        content.requestLayout();
    }
        
    public void navigateBackward(final boolean gohome) {
        fadeOut = new TranslateTransition(Duration.millis(500), content);
        fadeOut.setFromX(popupX);
        fadeOut.setToX(popupWidth);
        
        fadeIn = new TranslateTransition(Duration.millis(500), content);
        fadeIn.setFromX(-popupWidth);
        fadeIn.setToX(popupX + 10);
        
        fadeOut.setOnFinished(new EventHandler<ActionEvent> () {
            public void handle(ActionEvent ae) {
                if (!gohome) { 
                    setId(getId()-1);
                } else {
                    setId(1);
                }
                list.remove(getId());
                javafx.scene.Node view = list.get(getId()-1);
                cleanup();
                //System.out.println("POPPED " + view.getId());
                content.getChildren().add(view);
                ((ListView)view).getFocusModel().focus(0);
                fadeIn.play();
            }
        });
        fadeOut.play();
    }
    
    
    @Override public void handle (InputEvent ie) {
        //System.out.println("Event name: " + ie.getEventType().getName());
        if (ie.getEventType().getName() == "MOUSE_RELEASED") {
            loadNextSet();
        } else if (ie.getEventType().getName() == "KEY_RELEASED") {
            KeyEvent ke = (KeyEvent) ie;
            //System.out.println("Code: " + ke.getCode() + " text: " + ke.getText() + " event: " + ke);
            //System.out.println
             if (ke.getCode() == KeyCode.HOME && ke.isControlDown()) {
                System.out.println("HOME Key PRESSED");
                if (getId() > 1) {
                    navigateBackward(true);
                }
            } else if (ke.getCode() == KeyCode.ESCAPE) {
                primaryStage.hide();
            } else if (ke.getCode() == KeyCode.RIGHT) {
                loadNextSet();
            } else if (ke.getCode() == KeyCode.LEFT && getId() > 1) {
                navigateBackward(false);
            }
        }
    }
    
    
    public void loadNextSet() {
        //String item = view.getSelectionModel().getSelectedItem();
        String [] val = null;
        javafx.scene.Node view = null;
        if (id.get() == 0) {
            val = prop.navigateHostProperties();
            view = ListBuilder.getList(val);
            view.setOnMouseReleased(this);
            view.setOnKeyReleased(this);
            ((ListView)view).setMaxSize(popupWidth - 15, popupHeight - 70);
        } else if (id.get() == 1) {
            val = prop.navigateVMProperties(prop.entity.getVMs()[0]);
            view = ListBuilder.getList(val);
            view.setOnMouseReleased(this);
            view.setOnKeyReleased(this);
            ((ListView)view).setMaxSize(popupWidth - 15, popupHeight - 70);
        } else if (id.get() == 2) {
            val = prop.navigateNetworkProperties(prop.entity.getVMs()[0].getNetworks()[0]);
            view = ListBuilder.getList(val);
            view.setOnMouseReleased(this);
            view.setOnKeyReleased(this);
            ((ListView)view).setMaxSize(popupWidth - 15, popupHeight - 70);
        } else if (id.get() == 3) {
            view = buildPropertiesPage();
        } else {
            // DO nothing.
            return;
        }
        list.add(id.get(), view);
        navigateForward(view);
        id.set(id.get()+1);
    }
    
    public VBox buildPropertiesPage() {
        Rectangle tRect = new Rectangle (10, 10, Color.TRANSPARENT);
        Label val1 = new Label();
        val1.setFont(Font.font("Arial", FontWeight.BOLD, 15.0f));
        val1.setText("VMName: vmwb-kannan");
        val1.setTextFill(Color.CADETBLUE);
        Label val2 = new Label();
        val2.setFont(Font.font("Arial", FontWeight.BOLD, 15.0f));
        val2.setText("CPU: Quad Core");
        val2.setTextFill(Color.CADETBLUE);
        Label val3 = new Label();
        val3.setFont(Font.font("Arial", FontWeight.BOLD, 15.0f));
        val3.setText("Memory: 2048 MB");
        val3.setTextFill(Color.CADETBLUE);
        Label val4 = new Label();
        val4.setFont(Font.font("Arial", FontWeight.BOLD, 15.0f));
        val4.setText("Graphics: Nvidia");
        val4.setTextFill(Color.CADETBLUE);
        VBox vb = new VBox();
        vb.getChildren().add(tRect);
        vb.getChildren().add(val1);
        vb.getChildren().add(val2);
        vb.getChildren().add(val3);
        vb.getChildren().add(val4);
        tRect.setOnKeyReleased(this);
        tRect.setFocusTraversable(true);
        tRect.requestFocus();
        return vb;
    }
        
    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
