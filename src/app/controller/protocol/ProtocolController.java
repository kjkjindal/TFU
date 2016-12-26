package app.controller.protocol;

import app.App;
import app.model.Logger;
import app.model.protocol.*;
import app.model.protocol.commands.Command;
import app.model.protocol.commands.CommandFactory;
import app.model.protocol.commands.CommandType;
import app.model.protocol.commands.OneWayCommand;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by alexskrynnyk on 12/20/16.
 */
public class ProtocolController {

    private Protocol protocol;

    @FXML private TreeView protocolTree;
    @FXML private StackPane controlsPane;

    private Map<ProtocolComponent, Node> componentSettingsMap;

    @FXML
    public void initialize() {
        this.protocol = Protocol.getInstance();
        this.protocol.setName("SOLiD 7bp in situ seq");

        this.componentSettingsMap = new HashMap<>();

        this.protocolTree.setCellFactory(f -> new ProtocolTreeCell());

        this.bindTreeSelection();

        this.drawExperimentTree();
    }

    private void drawExperimentTree() {
        TreeItem<ProtocolComponent> root = new TreeItem(this.protocol);
        for(Cycle c: this.protocol.getCycleList()) {
            TreeItem<ProtocolComponent> cycleItem = new TreeItem(c);
            for(Command cmd: c.getCommands()){
                TreeItem<ProtocolComponent> commandItem = new TreeItem(cmd);
                cycleItem.getChildren().add(commandItem);
            }
            cycleItem.setExpanded(true);
            root.getChildren().add(cycleItem);
        }
        root.setExpanded(true);
        this.protocolTree.setRoot(root);
    }

    private void bindTreeSelection() {
        this.protocolTree.getSelectionModel().selectedItemProperty().addListener((item, oldVal, newVal) -> {
            TreeItem treeItem = (TreeItem) item.getValue();

            ProtocolComponent component = (ProtocolComponent) treeItem.getValue();

            this.loadAppropriateControls(component);
        });
    }

    private void loadAppropriateControls(ProtocolComponent component) {
        this.controlsPane.getChildren().clear();
        try {
            String name = component.getClass().getSimpleName();

            if (!this.componentSettingsMap.containsKey(component)) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/protocol/" + name + "-settings.fxml")
                );
                Node controls = loader.load();

                SettingsController controller = loader.getController();
                controller.configure(component);

//                System.out.println("not present in the map, putting");

                this.componentSettingsMap.put(component, controls);
            }

            this.controlsPane.getChildren().add(this.componentSettingsMap.get(component));

//            System.out.println(componentSettingsMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Protocol getProtocol() {
        return this.protocol;
    }

    private final class ProtocolTreeCell extends TreeCell<ProtocolComponent> {

        private Node cell;
        private HBox controls;
        private Label name;
        private Label duration;
        private ImageView add;
        private ImageView remove;
        private ImageView icon;

        public ProtocolTreeCell() {

            FXMLLoader fxmlLoader = new FXMLLoader(
                    App.class.getResource("/FXcomponents/protocolTreeCell.fxml")
            );
            try {
                cell = fxmlLoader.load();
                controls = (HBox) cell.lookup("#controls");
                name = (Label) cell.lookup("#name");
                duration = (Label) cell.lookup("#duration");
                add = (ImageView) cell.lookup("#add");
                remove = (ImageView) cell.lookup("#remove");
                icon = (ImageView) cell.lookup("#icon");
            } catch (IOException ex) {
                Logger.log(""+ex.getLocalizedMessage()+"\n"+ex);
            }
        }

        private void configureCell(ProtocolComponent obj) {
            name.textProperty().bind(obj.nameProperty());
            duration.setText(obj.getDuration()+" min");

            String url = String.valueOf(
                    App.class.getResource("../resources/img/"+obj.getClass().getSimpleName()+"-"+obj.getStatus().getCompoundName()+"-icon.png")
            );

//            System.out.println("../resources/img/"+obj.getClass().getSimpleName()+"-"+obj.getStatus().getCompoundName()+"-icon.png");

            Image img = new Image(url);
            icon.setImage(img);

            if (obj instanceof Command) {
                add.setVisible(false);

                remove.setOnMouseClicked(e -> {
                    OneWayCommand cmd = (OneWayCommand) getItem();

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Please Confirm");
                    String s = "Are you sure you wish to delete command '"+cmd.getVolume()+"' ?";
                    alert.setContentText(s);

                    Optional<ButtonType> result = alert.showAndWait();

                    if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
                        ((Cycle) getTreeItem().getParent().getValue()).removeCommand(cmd);
                        getTreeItem().getParent().getChildren().remove(getTreeItem());
                    }
                    componentSettingsMap.remove(obj);
                });
            } else if (obj instanceof Cycle) {
                add.setVisible(true);
                add.setOnMouseClicked(e -> {

                    List<String> commandOptions = Arrays.asList(CommandType.values()).stream()
                            .map(c -> c.getFullName())
                            .collect(Collectors.toList());

                    ChoiceDialog dialog = new ChoiceDialog(commandOptions.get(0), commandOptions);
                    dialog.setTitle("Idk");
                    dialog.setHeaderText("Select the type of command to add");

                    Optional<String> result = dialog.showAndWait();

                    if (result.isPresent()) {
                        Command newCmd = CommandFactory.createCommand(CommandType.getByFullName(result.get()));

                        TreeItem newItem = new TreeItem(newCmd);

                        ((Cycle) getItem()).addCommand(newCmd);

                        getTreeItem().getChildren().add(newItem);
                        getTreeItem().setExpanded(true);
                    }
                });
                remove.setOnMouseClicked(e -> {
                    Cycle cycle = (Cycle) getItem();

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Please Confirm");
                    String s = "Are you sure you wish to delete cycle '"+cycle.getName()+"' ?";
                    alert.setContentText(s);

                    Optional<ButtonType> result = alert.showAndWait();

                    if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
                        ((Protocol) getTreeItem().getParent().getValue()).removeCycle(cycle);

                        getTreeItem().getParent().getChildren().remove(getTreeItem());
                    }
                    componentSettingsMap.remove(obj);
                });
            } else if (obj instanceof Protocol) {
                add.setVisible(true);
                add.setOnMouseClicked(e -> {
                    Cycle newCycle = new Cycle();

                    TreeItem newItem = new TreeItem(newCycle);

                    protocol.addCycle(newCycle);

                    getTreeItem().getChildren().add(newItem);
                });
//                remove.setOnMouseClicked(e -> {
//                    System.out.println("remove protocol "+obj.getName());
//                });
            }
        }

        @Override
        public void updateItem(ProtocolComponent obj, boolean empty) {
            super.updateItem(obj, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setText(null);
                this.configureCell(obj);
                setGraphic(cell);
            }
        }
    }

}
