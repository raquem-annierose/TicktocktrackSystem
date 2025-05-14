package ticktocktrack.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class AdminViewAllUsersCenterPanel {

    public static Pane createPanel() {
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setLayoutX(0);
        centerPanel.setLayoutY(0);
        centerPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        // Shadow image
        String shadowPath = AdminViewAllUsersCenterPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutX(0);
        shadowView.setLayoutY(-115);

        // Search bar (Responsive)
        TextField searchField = createSearchBar(centerPanel);

        // Filter dropdown for switching tables
        ComboBox<String> roleSwitcher = new ComboBox<>();
        roleSwitcher.getItems().addAll("Admin", "Faculty", "Student");
        roleSwitcher.setValue("Admin");
        roleSwitcher.setLayoutX(850);
        roleSwitcher.setLayoutY(55);
        roleSwitcher.setPrefWidth(150);
        roleSwitcher.setPrefHeight(35);

        // ===== Tables =====
        // Admin Table
        TableView<User> adminTable = new TableView<>();
        adminTable.setLayoutX(50);
        adminTable.setLayoutY(150);
        adminTable.setPrefSize(950, 450);
        adminTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<User, String> adminId = new TableColumn<>("ID");
        adminId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<User, String> adminUsername = new TableColumn<>("Username");
        adminUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<User, String> adminRole = new TableColumn<>("Role");
        adminRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        adminTable.getColumns().addAll(adminId, adminUsername, adminRole);

        ObservableList<User> adminData = FXCollections.observableArrayList(
            new User("A001", "adminUser", "Admin")
        );
        FilteredList<User> adminFiltered = new FilteredList<>(adminData, p -> true);
        adminTable.setItems(adminFiltered);

        // Faculty Table
        TableView<User> facultyTable = new TableView<>();
        facultyTable.setLayoutX(50);
        facultyTable.setLayoutY(150);
        facultyTable.setPrefSize(950, 450);
        facultyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<User, String> facId = new TableColumn<>("ID");
        facId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<User, String> facUsername = new TableColumn<>("Username");
        facUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<User, String> facRole = new TableColumn<>("Role");
        facRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        facultyTable.getColumns().addAll(facId, facUsername, facRole);

        ObservableList<User> facultyData = FXCollections.observableArrayList(
            new User("F001", "teacherJohn", "Faculty"),
            new User("F002", "teacherAnne", "Faculty")
        );
        FilteredList<User> facultyFiltered = new FilteredList<>(facultyData, p -> true);
        facultyTable.setItems(facultyFiltered);

        // Student Table
        TableView<Student> studentTable = new TableView<>();
        studentTable.setLayoutX(50);
        studentTable.setLayoutY(150);
        studentTable.setPrefSize(950, 450);
        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Student, String> stuId = new TableColumn<>("ID");
        stuId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Student, String> stuUsername = new TableColumn<>("Username");
        stuUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<Student, String> stuYear = new TableColumn<>("Year");
        stuYear.setCellValueFactory(new PropertyValueFactory<>("year"));

        studentTable.getColumns().addAll(stuId, stuUsername, stuYear);

        ObservableList<Student> studentData = FXCollections.observableArrayList(
            new Student("S001", "studentMike", "1st Year"),
            new Student("S002", "studentJane", "2nd Year")
        );
        FilteredList<Student> studentFiltered = new FilteredList<>(studentData, p -> true);
        studentTable.setItems(studentFiltered);

        // Show/hide tables based on dropdown
        roleSwitcher.setOnAction(e -> {
            String selected = roleSwitcher.getValue();
            centerPanel.getChildren().removeAll(adminTable, facultyTable, studentTable);
            if (selected.equals("Admin")) {
                centerPanel.getChildren().add(adminTable);
            } else if (selected.equals("Faculty")) {
                centerPanel.getChildren().add(facultyTable);
            } else if (selected.equals("Student")) {
                centerPanel.getChildren().add(studentTable);
            }
            searchField.clear(); // clear previous search
        });

        // Dynamic filtering for all tables
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String query = newVal.toLowerCase();
            switch (roleSwitcher.getValue()) {
                case "Admin":
                    adminFiltered.setPredicate(user -> user.getUsername().toLowerCase().contains(query));
                    break;
                case "Faculty":
                    facultyFiltered.setPredicate(user -> user.getUsername().toLowerCase().contains(query));
                    break;
                case "Student":
                    studentFiltered.setPredicate(student -> student.getUsername().toLowerCase().contains(query));
                    break;
            }
        });

        centerPanel.getChildren().addAll(shadowView, searchField, roleSwitcher, adminTable); // Admin table shown first
        return centerPanel;
    }

    // ======= User class =======
    public static class User {
        private String id;
        private String username;
        private String role;

        public User(String id, String username, String role) {
            this.id = id;
            this.username = username;
            this.role = role;
        }

        public String getId() { return id; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
    }

    // ======= Student class =======
    public static class Student {
        private String id;
        private String username;
        private String year;

        public Student(String id, String username, String year) {
            this.id = id;
            this.username = username;
            this.year = year;
        }

        public String getId() { return id; }
        public String getUsername() { return username; }
        public String getYear() { return year; }
    }

    // Search Bar Method (Responsive)
    public static TextField createSearchBar(Pane container) {
        TextField searchField = new TextField();
        searchField.setPromptText("Search by username...");
        searchField.setLayoutX(45);
        searchField.setLayoutY(50);
        searchField.setPrefSize(286, 44);

        // Initial Styling
        searchField.setStyle(
            "-fx-background-color: rgb(255, 252, 252);" +
            "-fx-border-color: rgb(112, 152, 170);" +
            "-fx-border-width: 1;" +
            "-fx-background-radius: 30;" +
            "-fx-border-radius: 30;" +
            "-fx-padding: 0 0 0 10;" +
            "-fx-font-size: 14px;"
        );

        // Inner Shadow Effect
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.web("#C5C4C4"));
        innerShadow.setOffsetY(2);
        innerShadow.setRadius(2);
        searchField.setEffect(innerShadow);

        // On Click Focus Highlight
        searchField.setOnMouseClicked(event -> {
            searchField.setFocusTraversable(true);
            searchField.requestFocus();

            // Add subtle glow effect when clicked
            DropShadow glow = new DropShadow();
            glow.setColor(Color.web("#34BCCE", 0.4)); // reduced opacity
            glow.setOffsetY(0);
            glow.setRadius(5); // reduced glow radius
            searchField.setEffect(glow);
        });

        // On Key Press (Enter) Action
        searchField.setOnAction(event -> {
            String input = searchField.getText();
            System.out.println("Searching: " + input);
            if (input.isEmpty()) {
                searchField.setPromptText("Search by username...");
            }
        });

        // Reset shadow and prompt text when focus is lost
        searchField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                if (searchField.getText().isEmpty()) {
                    searchField.setPromptText("Search by username...");
                }
                searchField.setEffect(innerShadow);
            }
        });

        // Prevent auto-focus on app load
        Platform.runLater(() -> {
            searchField.setFocusTraversable(false);
            if (container != null) {
                container.requestFocus();
            }
        });

        return searchField;
    }
}
