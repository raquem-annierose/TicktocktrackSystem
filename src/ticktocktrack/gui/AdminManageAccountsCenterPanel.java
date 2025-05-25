package ticktocktrack.gui;

import java.util.Optional;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import ticktocktrack.database.UserDAO;
import ticktocktrack.logic.UsersModel;

public class AdminManageAccountsCenterPanel {

    public static Pane createPanel(int adminId) {
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        // Shadow background
        String shadowPath = AdminManageAccountsCenterPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutY(-115);

     // Search Box with icon inside (StackPane)
        StackPane searchBox = new StackPane();
        searchBox.setPrefWidth(250);
        searchBox.setPrefHeight(37);
        searchBox.setLayoutX(620);
        searchBox.setLayoutY(41);
        searchBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: black;" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 0 3 0 3;"  // padding left for icon space
        );

        // Search icon
        String searchIconPath = AdminViewAllUsersCenterPanel.class.getResource("/resources/search_icon.png").toExternalForm();
        ImageView searchIcon = new ImageView(new Image(searchIconPath));
        searchIcon.setFitWidth(20);
        searchIcon.setFitHeight(20);
        StackPane.setAlignment(searchIcon, Pos.CENTER_LEFT);
       

        // Search text field (inside StackPane)
        TextField searchField = new TextField();
        searchField.setPromptText("Search User...");
        searchField.setFont(Font.font("Poppins", 11));
        searchField.setPrefHeight(40);
        searchField.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-width: 0;" +
            "-fx-font-family: 'Poppins';" +
            "-fx-text-fill: black;" +
            "-fx-padding: 0 0 0 20;"  // top right bottom left padding
        );

        searchBox.getChildren().addAll(searchField, searchIcon);
        

        // Role title label
        Label roleTitleLabel = new Label("Manage Accounts");
        roleTitleLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 30));
        roleTitleLabel.setLayoutX(10);
        roleTitleLabel.setLayoutY(35);

        // Role Switcher
        ComboBox<String> roleSwitcher = new ComboBox<>();
        roleSwitcher.getItems().addAll("All Users", "Admin", "Teacher", "Student");
        roleSwitcher.setValue("All Users");
        roleSwitcher.setLayoutX(880);
        roleSwitcher.setLayoutY(40);
        roleSwitcher.setPrefSize(150, 35);
        roleSwitcher.setStyle(getComboBoxStyle());

        // Year Level Filter
        ComboBox<String> yearLevelFilter = new ComboBox<>();
        yearLevelFilter.getItems().addAll("All Year Level", "1st Year", "2nd Year", "3rd Year", "4th Year", "5th year");
        yearLevelFilter.setValue("All Year Level");
        yearLevelFilter.setLayoutX(275);
        yearLevelFilter.setLayoutY(40);
        yearLevelFilter.setPrefSize(130, 35);
        yearLevelFilter.setVisible(false);
        yearLevelFilter.setStyle(getComboBoxStyle());

        // Program Filter
        ComboBox<String> programFilter = new ComboBox<>();
        programFilter.getItems().addAll(
                "All Programs",
                "BSECE – BS in Electronics Engineering",
                "BSME – BS in Mechanical Engineering",
                "BSA – BS in Accountancy",
                "BSBA-HRDM – BSBA major in HRDM",
                "BSBA-MM – BSBA major in Marketing",
                "BSENTREP – BS in Entrepreneurship",
                "BSIT – BS in Information Technology",
                "DIT – Diploma Information Technology",
                "BSAM – BS in Applied Mathematics",
                "BSED-ENGLISH – BSEd major in English",
                "BSED-MATH – BSEd major in Math",
                "BSOA – BS in Office Administration"
        );
        programFilter.setValue("All Programs");
        programFilter.setLayoutX(410);
        programFilter.setLayoutY(40);
        programFilter.setPrefSize(200, 35);
        programFilter.setVisible(false);
        programFilter.setStyle(getComboBoxStyle());

        // TableView Setup
        TableView<UsersModel> tableView = new TableView<>();
        tableView.setPrefSize(1020, 523);
        tableView.setLayoutX(10);
        tableView.setLayoutY(90);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        String cssPath = AdminManageAccountsCenterPanel.class.getResource("/resources/css/table-style.css").toExternalForm();
        tableView.getStylesheets().add(cssPath);

        // Fetch user data
        ObservableList<UsersModel> adminsList = FXCollections.observableArrayList(UserDAO.manageAdmins());
        ObservableList<UsersModel> teachersList = FXCollections.observableArrayList(UserDAO.manageTeachers());
        ObservableList<UsersModel> studentsList = FXCollections.observableArrayList(UserDAO.manageStudents());

        ObservableList<UsersModel> allUsersList = FXCollections.observableArrayList();
        allUsersList.addAll(adminsList);
        allUsersList.addAll(teachersList);
        allUsersList.addAll(studentsList);

        updateTableColumns(tableView, "All Users");
        tableView.setItems(allUsersList);

        

        // Filter logic
        java.util.function.Function<String, ObservableList<UsersModel>> filterUsers = searchText -> {
            String lowerSearch = searchText.toLowerCase();
            String currentRole = roleSwitcher.getValue();
            ObservableList<UsersModel> sourceList = switch (currentRole) {
                case "Admin" -> adminsList;
                case "Teacher" -> teachersList;
                case "Student" -> studentsList;
                default -> allUsersList;
            };

            boolean filterByYear = currentRole.equals("Student") && !yearLevelFilter.getValue().equals("All Year Level");
            boolean filterByProgram = currentRole.equals("Student") && !programFilter.getValue().equals("All Programs");

            ObservableList<UsersModel> filtered = FXCollections.observableArrayList();

            for (UsersModel user : sourceList) {
                boolean matchesSearch = (user.getUsername() != null && user.getUsername().toLowerCase().contains(lowerSearch)) ||
                        (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerSearch)) ||
                        (user.getFirstName() != null && user.getFirstName().toLowerCase().contains(lowerSearch)) ||
                        (user.getLastName() != null && user.getLastName().toLowerCase().contains(lowerSearch));

                boolean matchesYear = !filterByYear || (user.getYearLevel() != null && user.getYearLevel().equalsIgnoreCase(yearLevelFilter.getValue()));
                boolean matchesProgram = !filterByProgram || (user.getProgram() != null && user.getProgram().equalsIgnoreCase(programFilter.getValue()));

                if (matchesSearch && matchesYear && matchesProgram) {
                    filtered.add(user);
                }
            }

            return filtered;
        };

        // Event listeners
        searchField.textProperty().addListener((obs, oldVal, newVal) -> tableView.setItems(filterUsers.apply(newVal)));
        yearLevelFilter.setOnAction(e -> tableView.setItems(filterUsers.apply(searchField.getText())));
        programFilter.setOnAction(e -> tableView.setItems(filterUsers.apply(searchField.getText())));
        roleSwitcher.setOnAction(e -> {
            String role = roleSwitcher.getValue();
            updateTableColumns(tableView, role);
            switch (role) {
                case "Admin" -> {
                    tableView.setItems(adminsList);
                    yearLevelFilter.setVisible(false);
                    programFilter.setVisible(false);
                }
                case "Teacher" -> {
                    tableView.setItems(teachersList);
                    yearLevelFilter.setVisible(false);
                    programFilter.setVisible(false);
                }
                case "Student" -> {
                    tableView.setItems(filterUsers.apply(searchField.getText()));
                    yearLevelFilter.setVisible(true);
                    programFilter.setVisible(true);
                }
                default -> {
                    tableView.setItems(allUsersList);
                    yearLevelFilter.setVisible(false);
                    programFilter.setVisible(false);
                }
            }
        });

        centerPanel.getChildren().addAll(
                shadowView,
                roleTitleLabel,
                roleSwitcher,
                yearLevelFilter,
                programFilter,
                searchBox,
                tableView
        );

        return centerPanel;
    }

    private static String getComboBoxStyle() {
        return "-fx-background-color: white;" +
                "-fx-font-size: 11px;" +
                "-fx-padding: 6 12 6 12;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 0.9;" +
                "-fx-font-family: 'Poppins';" +
                "-fx-text-fill: black;";
    }

    
    @SuppressWarnings("unchecked")
    private static void updateTableColumns(TableView<UsersModel> table, String role) {
        table.getColumns().clear();

        TableColumn<UsersModel, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        idCol.setPrefWidth(50);

        TableColumn<UsersModel, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(150);

        TableColumn<UsersModel, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameCol.setPrefWidth(150);

        TableColumn<UsersModel, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameCol.setPrefWidth(150);

        TableColumn<UsersModel, String> createdByCol = new TableColumn<>("Created By");
        createdByCol.setCellValueFactory(new PropertyValueFactory<>("createdByName"));
        createdByCol.setPrefWidth(150);

        TableColumn<UsersModel, String> dateCreatedCol = new TableColumn<>("Date Created");
        dateCreatedCol.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        dateCreatedCol.setPrefWidth(150);

        TableColumn<UsersModel, UsersModel> manageCol = new TableColumn<>("Manage");
        manageCol.setPrefWidth(140); // Adjusted column width
        manageCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

        manageCol.setCellFactory(col -> new TableCell<UsersModel, UsersModel>() {
            private final Button deleteButton = new Button("Delete");
            private final Button editButton = new Button("Edit");
            private final HBox manageBox = new HBox(5); // Reduced spacing

            {
            	deleteButton.setStyle(
            		    "-fx-background-color: #FFFFFF; " +
            		    "-fx-text-fill: #e74c3c; " +
            		    "-fx-font-size: 10 px; " +
            		    "-fx-padding: 5 10 6 10; " +
            		    "-fx-background-radius: 5; " +
            		    "-fx-border-radius: 5; " +
            		    "-fx-border-color: #e74c3c; " +
            		    "-fx-border-width: 0.7; " +
            		    "-fx-font-family: 'Poppins'; " +
            		    "-fx-font-weight: bold; " +
            		    "-fx-cursor: hand;"
            		);

            		editButton.setStyle(
            		    "-fx-background-color: #FFFFFF; " +
            		    "-fx-text-fill: #3498db; " +
            		    "-fx-font-size: 10 px; " +
            		    "-fx-padding: 5 18 6 18; " +
            		    "-fx-background-radius: 5; " +
            		    "-fx-border-radius: 5; " +
            		    "-fx-border-color: #3498db; " +
            		    "-fx-border-width: 0.7; " +
            		    "-fx-font-family: 'Poppins'; " +
            		    "-fx-font-weight: bold; " +
            		    "-fx-cursor: hand;"
            		);

                deleteButton.setPrefWidth(55);
                editButton.setPrefWidth(55);

                manageBox.setAlignment(Pos.CENTER);
                manageBox.getChildren().addAll(editButton, deleteButton); // Edit comes first

                deleteButton.setOnAction(e -> {
                    UsersModel selectedUser = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm Deletion");
                    alert.setHeaderText("Delete User");
                    alert.setContentText("Are you sure you want to delete user: " + selectedUser.getUsername() + "?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        boolean deleted = UserDAO.deleteUserById(selectedUser.getUserId());
                        if (deleted) {
                            getTableView().getItems().remove(selectedUser);
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Failed to delete user.").showAndWait();
                        }
                    }
                });

                editButton.setOnAction(e -> {
                    UsersModel selectedUser = getTableView().getItems().get(getIndex());
                    AdminEditUsers.showEditDialog(selectedUser);
                    getTableView().refresh(); // Refresh UI after editing
                });

            }

       
            protected void updateItem(UsersModel item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : manageBox);
            }
        });

        table.getColumns().addAll(
            idCol, usernameCol, firstNameCol, lastNameCol,
            createdByCol, dateCreatedCol, manageCol
        );
    }




}
