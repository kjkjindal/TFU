package app.controller.protocol;

import app.model.Logger;
import app.model.protocol.*;
import app.model.protocol.commands.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/20/16
 */
public class ProtocolController {

    private Protocol protocol;
    private CommandFactory commandFactory;

    @FXML
    private TreeView<ProtocolComponent> protocolTree;
    @FXML
    private StackPane controlsPane;

    private Map<ProtocolComponent, Node> componentSettingsMap;

    @FXML
    public void initialize() {
        this.protocol = Protocol.getInstance();
        this.protocol.setName("New protocol");
        this.protocol.getTaskThread().setUncaughtExceptionHandler((t, e) -> {
//            Platform.runLater(() -> {
//                Alert alert = new Alert(Alert.AlertType.ERROR);
//
//                alert.setTitle("Protocol Error");
//                alert.setHeaderText("An error occurred during execution!");
//                alert.setContentText(e.getMessage());
//
//                alert.showAndWait();
//            });
            System.out.println("ERROR");
            this.protocol.getTaskThread().run();

        });

        this.componentSettingsMap = new HashMap<>();

        this.protocolTree.setCellFactory(f -> new ProtocolTreeCell());

        this.bindTreeSelection();
        this.drawExperimentTree();
    }

    public void configure(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    private void drawExperimentTree() {
        TreeItem<ProtocolComponent> root = new TreeItem<>(this.protocol);

        for (Cycle c : this.protocol.getCycleList()) {
            TreeItem<ProtocolComponent> cycleItem = new TreeItem<>(c);

            for (Command cmd : c.getCommands()) {
                TreeItem<ProtocolComponent> commandItem = new TreeItem<>(cmd);
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
            TreeItem treeItem = item.getValue();

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

    private Map<ProtocolComponent, ChangeListener<Status>> statusListenerMap = new HashMap<>();

    private final class ProtocolTreeCell extends TreeCell<ProtocolComponent> {

        private Node cell;
        private HBox controls;
        private Label name;
        private Label duration;
        private ImageView add;
        private ImageView remove;
        private ImageView icon;

        private void setIconByStatus(ProtocolComponent pc) {
            System.out.println("/resources/img/" + pc.getClass().getSimpleName() + "-" + pc.getStatus().getCompoundName() + "-icon.png");
            icon.setImage(
                    new Image(
                            String.valueOf(
                                    getClass().getResource("/resources/img/" + pc.getClass().getSimpleName() + "-" + pc.getStatus().getCompoundName() + "-icon.png")
                            )
                    )
            );
        }

        ProtocolTreeCell() {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/FXcomponents/protocolTreeCell.fxml")
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
                Logger.log("" + ex.getLocalizedMessage() + "\n" + ex);
            }
        }

        private void configureCell(ProtocolComponent pc) {
            name.textProperty().bind(pc.nameProperty());
            duration.setText(pc.getDuration() + " min");

            setIconByStatus(pc);

            if (pc instanceof Command || pc instanceof Cycle) {
                if (statusListenerMap.get(pc) != null)
                    pc.statusProperty().removeListener(statusListenerMap.get(pc));

                ChangeListener<Status> listener = (item, oldVal, newVal) -> Platform.runLater(() -> setIconByStatus(pc));
                pc.statusProperty().addListener(listener);
                statusListenerMap.put(pc, listener);
            }

            if (pc instanceof Command) {
                add.setVisible(false);

                Command cmd = (Command) getItem();

                remove.setOnMouseClicked(e -> {

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Please Confirm");
                    String s = "Are you sure you wish to delete command '" + cmd.getName() + "' ?";
                    alert.setContentText(s);

                    Optional<ButtonType> result = alert.showAndWait();

                    if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
                        ((Cycle) getTreeItem().getParent().getValue()).removeCommand(cmd);
                        getTreeItem().getParent().getChildren().remove(getTreeItem());
                    }
                    componentSettingsMap.remove(pc);
                });
            } else if (pc instanceof Cycle) {
                add.setVisible(true);
                add.setOnMouseClicked(e -> {

                    List<String> commandOptions = Arrays.stream(CommandType.values())
                            .map(CommandType::getFullName)
                            .collect(Collectors.toList());

                    ChoiceDialog<String> dialog = new ChoiceDialog<>(commandOptions.get(0), commandOptions);
                    dialog.setTitle("Add Command");
                    dialog.setHeaderText("Select the type of command to add");

                    Optional<String> result = dialog.showAndWait();

                    if (result.isPresent()) {
                        Command newCmd = ProtocolController.this.commandFactory.createCommand(CommandType.getByFullName(result.get()));

                        TreeItem<ProtocolComponent> newItem = new TreeItem<>(newCmd);

                        ((Cycle) getItem()).addCommand(newCmd);

                        getTreeItem().getChildren().add(newItem);
                        getTreeItem().setExpanded(true);

                        //System.out.println(ProtocolController.this.protocol.getCycleList());
                    }
                });
                remove.setOnMouseClicked(e -> {
                    Cycle cycle = (Cycle) getItem();

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Please Confirm");
                    String s = "Are you sure you wish to delete cycle '" + cycle.getName() + "' ?";
                    alert.setContentText(s);

                    Optional<ButtonType> result = alert.showAndWait();

                    if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
                        ((Protocol) getTreeItem().getParent().getValue()).removeCycle(cycle);

                        getTreeItem().getParent().getChildren().remove(getTreeItem());
                    }
                    componentSettingsMap.remove(pc);
                });
            } else if (pc instanceof Protocol) {
                add.setVisible(true);

                add.setOnMouseClicked(e -> {
                    Cycle newCycle = new Cycle();

                    TreeItem<ProtocolComponent> newItem = new TreeItem<>(newCycle);

                    protocol.addCycle(newCycle);

                    getTreeItem().getChildren().add(newItem);
                });
//                remove.setOnMouseClicked(e -> {
//                    System.out.println("remove protocol "+pc.getName());
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

    @FXML
    public void startProtocol() {
        try {
            this.protocol.execute();
        } catch (CommandExecutionException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void resetCycleCounter() {
        for (Cycle cycle : this.protocol.getCycleList()) {
            cycle.setStatus(Status.NOT_STARTED);
            for (Command cmd : cycle.getCommands())
                cmd.setStatus(Status.NOT_STARTED);
        }
        this.protocol.resetCycleIndex();
    }

}
