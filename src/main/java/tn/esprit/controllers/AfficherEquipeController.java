package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import tn.esprit.entities.Equipe;
import tn.esprit.services.ServiceEquipe;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherEquipeController implements Initializable {

    @FXML
    private FlowPane cardsContainer;
    @FXML
    private Label totalTeamsLabel;
    @FXML
    private Label messageLabel;

    private final ServiceEquipe serviceEquipe = new ServiceEquipe();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadEquipes();
    }

    private void loadEquipes() {
        try {
            List<Equipe> equipes = serviceEquipe.getAll();
            totalTeamsLabel.setText(String.valueOf(equipes.size()));
            cardsContainer.getChildren().clear();

            int rank = 1;
            for (Equipe equipe : equipes) {
                cardsContainer.getChildren().add(buildCard(equipe, rank++));
            }
            messageLabel.setText("");
        } catch (SQLException e) {
            messageLabel.setText("Erreur chargement equipes : " + e.getMessage());
        }
    }

    private VBox buildCard(Equipe equipe, int rank) {
        VBox card = new VBox(8);
        card.setPrefWidth(270);
        card.setPrefHeight(250);
        card.setStyle("-fx-padding: 12; -fx-background-color: #111b3e;"
            + "-fx-background-radius: 12; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 12;");

        Label rankLabel = new Label("#" + rank);
        rankLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: rgba(255,255,255,0.20);");

        StackPane avatarPane = createAvatar(equipe);

        Label nomLabel = new Label(equipe.getNom());
        nomLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #e5e7eb;");

        Label subtitle = new Label("ESPORT TEAM");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");

        VBox header = new VBox(rankLabel);
        header.setAlignment(Pos.TOP_LEFT);

        VBox body = new VBox(10, avatarPane, nomLabel, subtitle);
        body.setAlignment(Pos.CENTER);

        card.getChildren().addAll(header, body);
        return card;
    }

    @FXML
    private void handleAddEquipe() {
        openAddEquipeForm();
    }

    private void openAddEquipeForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/equipe/ajouterEquipe.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une equipe");
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();
            loadEquipes();
        } catch (Exception e) {
            messageLabel.setText("Erreur ouverture formulaire : " + e.getMessage());
        }
    }

    private StackPane createAvatar(Equipe equipe) {
        Circle circle = new Circle(36);
        circle.setFill(Color.web("#172554"));
        circle.setStroke(Color.web("#263564"));

        StackPane avatar = new StackPane(circle);
        avatar.setAlignment(Pos.CENTER);

        String logo = equipe.getLogo();
        if (logo != null && !logo.isBlank()) {
            File file = new File(logo);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString(), 72, 72, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(72);
                imageView.setFitHeight(72);
                imageView.setClip(new Circle(36, 36, 36));
                avatar.getChildren().add(imageView);
                return avatar;
            }
        }

        Label initials = new Label(getInitials(equipe.getNom()));
        initials.setStyle("-fx-font-size: 22px; -fx-text-fill: #e5e7eb; -fx-font-weight: 700;");
        avatar.getChildren().add(initials);
        return avatar;
    }

    private String getInitials(String nom) {
        if (nom == null || nom.isBlank()) {
            return "?";
        }
        String trimmed = nom.trim();
        if (trimmed.length() == 1) {
            return trimmed.toUpperCase();
        }
        return trimmed.substring(0, 2).toLowerCase();
    }

}
