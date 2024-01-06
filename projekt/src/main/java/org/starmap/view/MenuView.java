package org.starmap.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.starmap.controller.StarMapController;
import org.starmap.model.Constellation;
import org.starmap.model.Star;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.lang.Thread.sleep;

class MultipleDialogInput extends TextInputDialog{

    private Dialog<ArrayList<String>> dialog;
    public MultipleDialogInput(String name, List<String> names){
        dialog = new Dialog<>();
        dialog.setTitle(name);

        // Set the button types.
        ButtonType okButton= new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CLOSE);

        //Create a box
        VBox gridPane = new VBox();
        gridPane.setPadding(new Insets(20, 150, 10, 10));
        gridPane.setAlignment(Pos.CENTER);

        ArrayList<TextField> textFields = new ArrayList<>();

        for(int i = 0; i < names.size(); i++){
            TextField textField = new TextField();
            textField.setPromptText(names.get(i));
            gridPane.getChildren().add(textField);
            textFields.add(textField);
        }

        dialog.getDialogPane().setContent(gridPane);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                ArrayList<String> answears = new ArrayList<>();
                for(TextField result : textFields) {
                    if(result.getText() == null){
                        return null;
                    }else {
                        answears.add(result.getText());
                    }
                }
                return answears;
                }
            return null;
        });
    }
    public Dialog<ArrayList<String>> getDialogButton(){
        return dialog;
    }
}



public class MenuView extends MenuBar {
    private FileControllerView fileControllerView;
    public MenuView(){
    }

    //This methods, add a menu with buttons
    public void addMenu(String menuName,List<String> labels){
        Menu menu = new Menu(menuName);

        for(String label : labels){
            menu.getItems().add(new MenuItem(label));
        }
        this.getMenus().add(menu);
        this.getStylesheets().add("style.css");
    }

    //I need controler here because i will sent him a new stars to draw
    public void buttonsEventHandlers(StarMapView controllerView){
        List<MenuItem> items = this.getMenus().get(0).getItems();

        items.get(0).setOnAction(event -> {
            String options[] = {"Add","Delete","Edit parameters"};
            ChoiceDialog<String> dialog = new ChoiceDialog("",options);
            dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK,ButtonType.CLOSE);
            dialog.setTitle("Chose option");
            dialog.setHeaderText("");
            dialog.setGraphic(null);
            dialog.showAndWait();
            if(dialog.getResult() == null) return;

            switch(dialog.getResult()){
                case "Add":
                    //Creating dialog windows
                    MultipleDialogInput multipleDialogInput = new MultipleDialogInput("ADD STAR", Arrays.asList("Name","xCord","yCord","Brightness","Constelatin"));
                    Dialog<ArrayList<String>> dialogAdd = multipleDialogInput.getDialogButton();
                    dialogAdd.showAndWait();
                    ArrayList<String> resultAdd = dialogAdd.getResult();
                    //Checking resultAdds
                    if(resultAdd != null) {
                        Star star = new Star(resultAdd.get(0), Double.parseDouble(resultAdd.get(1)), Double.parseDouble(resultAdd.get(2)), Double.parseDouble(resultAdd.get(3)));
                        //Geting controler to set a new star
                        StarMapController controller = controllerView.getController();
                        Optional<Constellation> consIf = controller.getConstellationByName(resultAdd.get(4));
                        if(consIf.isPresent()){
                            Constellation constellation = consIf.get();
                            constellation.getStars().add(star);
                            controller.getStars().add(star);
                        }
                        controllerView.drawMap();
                    }
                    break;
                case "Delete":
                    ArrayList<String> starNames = new ArrayList<>();
                    for(Star star : controllerView.getController().getStars()){
                        starNames.add(star.getName());
                    }
                    ChoiceDialog<String> dialogDelete = new ChoiceDialog("",starNames);
                    dialogDelete.getDialogPane().getButtonTypes().setAll(ButtonType.OK,ButtonType.CANCEL);
                    dialogDelete.showAndWait();
                    String resultDelete = dialogDelete.getSelectedItem();
                    //Checking Results
                    if(resultDelete != null) {
                        controllerView.getController().removeStar(resultDelete);
                        controllerView.drawMap();
                    }
                    break;
                case "Edit parameters":
                    MultipleDialogInput multipleDialogInput2 = new MultipleDialogInput("Edit Star", Arrays.asList("Name","new xCords","new yCords","new Brighness"));
                    Dialog<ArrayList<String>> dialogMove = multipleDialogInput2.getDialogButton();
                    dialogMove.showAndWait();
                    ArrayList<String> resultMove = dialogMove.getResult();
                    //Checking Results
                    if(resultMove != null) {
                        Optional<Star> star = controllerView.getController().getStarByName(resultMove.get(0));
                        if(star.isPresent()){
                            Star starGet = star.get();
                            Double xcord = Double.parseDouble(resultMove.get(1));
                            Double ycord = Double.parseDouble(resultMove.get(2));
                            Double bright = Double.parseDouble(resultMove.get(3));
                            if(xcord <2000 && xcord >=0) starGet.setxPosition(xcord);
                            if(ycord <2000 && ycord >=0) starGet.setyPosition(ycord);
                            if(bright > 0 && bright < 3) starGet.setBrightness(bright);
                        }
                        controllerView.drawMap();
                    }
                    break;
                default:
                    break;
            }
        });
        items.get(1).setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File help = new File("src/main/resources");
            fileChooser.setInitialDirectory(help);
            File file = fileChooser.showOpenDialog(this.getParent().getScene().getWindow());
            //It's a little bit unclear, but those functions add stars and constelations to a controller, the same controler
            //that StarMapView have, but cuz loadConstellationFromFile needs List<Stars> i at the same time return it
            fileControllerView.loadConstellationsFromFile(file.getPath(),fileControllerView.loadStarsFromFile(file.getPath()));
            controllerView.drawMap();
        });
        items.get(2).setOnAction(event -> {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file to which you want to save current configuration");
        File help = new File("src/main/resources");
        fileChooser.setInitialDirectory(help);
        File file = fileChooser.showSaveDialog(this.getParent().getScene().getWindow());
        fileControllerView.writeConfigurationToFile(file.getPath());
        });
        items.get(3).setOnAction(event -> {
            controllerView.showGrid();
        });
        items.get(4).setOnAction(event -> {
            controllerView.hideGrid();
        });
        items.get(5).setOnAction(event -> {
            controllerView.getController().setStars(new ArrayList<Star>());
            controllerView.getController().setConstellations(new ArrayList<Constellation>());
            controllerView.drawMap();
        });
    }
    public void setFileControllerView(FileControllerView fileControllerView){
        this.fileControllerView = fileControllerView;
    }

}
