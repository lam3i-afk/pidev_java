package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.entities.Equipe;
import tn.esprit.services.ServiceEquipe;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class EquipeDashboardController implements Initializable {

    @FXML
    private TableView<Equipe> equipeTable;
    @FXML
    private TableColumn<Equipe, Integer> idCol;
    @FXML
    private TableColumn<Equipe, String> nomCol;
    @FXML
    private TableColumn<Equipe, Integer> maxMembersCol;
    @FXML
    private TableColumn<Equipe, String> logoCol;
    @FXML
    private TableColumn<Equipe, String> actionsCol;
    @FXML
    private Label messageLabel;
    @FXML
    private Label totalEquipesLabel;

    private final ServiceEquipe serviceEquipe = new ServiceEquipe();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumns();
        loadEquipes();
    }

    private void setupColumns() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        maxMembersCol.setCellValueFactory(new PropertyValueFactory<>("maxMembers"));
        logoCol.setCellValueFactory(new PropertyValueFactory<>("logo"));

        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑");
            private final HBox box = new HBox(6, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");

                editBtn.setOnAction(e -> {
                    Equipe equipe = getTableView().getItems().get(getIndex());
                    openEditWindow(equipe);
                });

                deleteBtn.setOnAction(e -> {
                    Equipe equipe = getTableView().getItems().get(getIndex());
                    confirmDelete(equipe);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void loadEquipes() {
        try {
            List<Equipe> list = serviceEquipe.getAll();
            equipeTable.setItems(FXCollections.observableArrayList(list));
            totalEquipesLabel.setText(String.valueOf(list.size()));
            messageLabel.setText("");
        } catch (SQLException e) {
            messageLabel.setStyle("-fx-text-fill: #e74c3c;");
            messageLabel.setText("Erreur chargement equipes : " + e.getMessage());
        }
    }

    @FXML
    private void handleNewEquipe() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/equipe/ajouterEquipe.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une equipe");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadEquipes();
        } catch (IOException e) {
            messageLabel.setStyle("-fx-text-fill: #e74c3c;");
            messageLabel.setText("Erreur ouverture formulaire : " + e.getMessage());
        }
    }

    private void openEditWindow(Equipe equipe) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/equipe/ajouterEquipe.fxml"));
            Parent root = loader.load();
            AjouterEquipeController controller = loader.getController();
            controller.setEquipeToEdit(equipe);
            Stage stage = new Stage();
            stage.setTitle("Modifier une equipe");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadEquipes();
        } catch (IOException e) {
            messageLabel.setStyle("-fx-text-fill: #e74c3c;");
            messageLabel.setText("Erreur ouverture formulaire : " + e.getMessage());
        }
    }

    private void confirmDelete(Equipe equipe) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Supprimer l'equipe " + equipe.getNom() + " ?");
        confirm.showAndWait().ifPresent(button -> {
            if (button == ButtonType.OK) {
                try {
                    serviceEquipe.supprimer(equipe.getId());
                    messageLabel.setStyle("-fx-text-fill: #27ae60;");
                    messageLabel.setText("Equipe supprimee avec succes.");
                    loadEquipes();
                } catch (SQLException e) {
                    messageLabel.setStyle("-fx-text-fill: #e74c3c;");
                    messageLabel.setText("Suppression impossible : " + e.getMessage());
                }
            }
        });
    }
}
