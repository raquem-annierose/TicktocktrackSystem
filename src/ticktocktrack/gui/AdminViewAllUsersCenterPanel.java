package ticktocktrack.gui;

import java.util.Map;

import javafx.application.Platform;
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
import javafx.scene.text.TextAlignment;
import ticktocktrack.database.UserDAO;
import ticktocktrack.logic.UsersModel;

/**
 * Represents the center panel in the Admin dashboard for viewing all users.
 */
public class AdminViewAllUsersCenterPanel {

    /**
     * Creates and returns the main pane displaying all users for the given admin.
     * The pane includes styling and a shadow image at the top.
     * 
     * @param adminId the ID of the admin viewing the users, used to fetch or filter data as needed
     * @return a Pane configured to show all users with proper UI elements
     */
    public static Pane createPanel(int adminId) {
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        // Shadow image
        String shadowPath = AdminViewAllUsersCenterPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutX(0);
        shadowView.setLayoutY(-115);

     // Search Box with icon inside (StackPane)
        StackPane searchBox = new StackPane();
        searchBox.setPrefWidth(250);
        searchBox.setPrefHeight(37);
        searchBox.setLayoutX(610);
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


     // Role Switcher ComboBox
        ComboBox<String> roleSwitcher = new ComboBox<>();
        roleSwitcher.getItems().addAll("All Users", "Admin", "Teacher", "Student");
        roleSwitcher.setValue("All Users");  
        roleSwitcher.setLayoutX(880);
        roleSwitcher.setLayoutY(40);
        roleSwitcher.setPrefWidth(150);
        roleSwitcher.setPrefHeight(35);
        roleSwitcher.setStyle(
                "-fx-background-color: white;" +
                "-fx-font-size: 11px;" +
                "-fx-padding: 6 12 6 12;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 0.9;" +
                "-fx-font-family: 'Poppins';" +
                "-fx-text-fill: black;"
        );


     // Title label for current role
        Label roleTitleLabel = new Label("View Users Table");
        roleTitleLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 30));
        roleTitleLabel.setLayoutX(10);
        roleTitleLabel.setLayoutY(35);
        
     // Year Level Filter ComboBox (initially hidden)
        ComboBox<String> yearLevelFilter = new ComboBox<>();
        yearLevelFilter.getItems().addAll("All Year Level", "1st Year", "2nd Year", "3rd Year", "4th Year", "5th year");
        yearLevelFilter.setValue("All Year Level");
        yearLevelFilter.setLayoutX(215);
        yearLevelFilter.setLayoutY(40);
        yearLevelFilter.setPrefWidth(130);
        yearLevelFilter.setPrefHeight(35);
        yearLevelFilter.setVisible(false);  // only visible for Student role
        yearLevelFilter.setStyle(
            "-fx-background-color: white;" +
            "-fx-font-size: 11px;" +
            "-fx-padding: 6 12 6 12;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-border-color: black;" +
            "-fx-border-width: 0.9;" +
            "-fx-font-family: 'Poppins';" +
            "-fx-text-fill: black;"
        );
        
        ComboBox<String> programFilter = new ComboBox<>();
        programFilter.getItems().addAll(
            "All Programs",
            "BSECE – BS in Electronics Engineering",
            "BSME – BS in Mechanical Engineering",
            "BSA – BS in Accountancy",
            "BSBA-HRDM – BSBA major in Human Resource Development Management",
            "BSBA-MM – BSBA major in Marketing Management",
            "BSENTREP – BS in Entrepreneurship",
            "BSIT – BS in Information Technology",
            "DIT – Diploma Information Technology",
            "BSAM – BS in Applied Mathematics",
            "BSED-ENGLISH – Bachelor in Secondary Education major in English",
            "BSED-MATH – Bachelor in Secondary Education major in Mathematics",
            "BSOA – BS in Office Administration"
        );
        programFilter.setValue("All Programs");
        programFilter.setLayoutX(350); // place it next to yearLevelFilter (adjust as needed)
        programFilter.setLayoutY(40);
        programFilter.setPrefWidth(230);
        programFilter.setPrefHeight(35);
        programFilter.setVisible(false);  // initially hidden
        programFilter.setStyle(
            "-fx-background-color: white;" +
            "-fx-font-size: 11px;" +
            "-fx-padding: 6 12 6 12;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-border-color: black;" +
            "-fx-border-width: 0.9;" +
            "-fx-font-family: 'Poppins';" +
            "-fx-text-fill: black;"
        );


     // TableView setup
        TableView<UsersModel> tableView = new TableView<>();
        tableView.setPrefSize(1020, 523);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        String cssPath = AdminViewAllUsersCenterPanel.class.getResource("/resources/css/table-style.css").toExternalForm();
        tableView.getStylesheets().add(cssPath);

        // ScrollPane setup
        ScrollPane tableScrollPane = new ScrollPane(tableView);
        tableScrollPane.setLayoutX(10);
        tableScrollPane.setLayoutY(90);
        tableScrollPane.setPrefSize(1020, 523);
        tableScrollPane.setFitToWidth(true);
        tableScrollPane.setFitToHeight(true);
        tableScrollPane.setStyle("-fx-background-color: transparent;");

        // Optional: Hide scrollbars unless needed
        tableScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        tableScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
      

        final var adminsList = FXCollections.observableArrayList(UserDAO.getAdmins());
        final var teachersList = FXCollections.observableArrayList(UserDAO.getTeachers());
        final var studentsList = FXCollections.observableArrayList(UserDAO.getStudents());

        final ObservableList<UsersModel> allUsersList = FXCollections.observableArrayList();
        allUsersList.addAll(adminsList);
        allUsersList.addAll(teachersList);
        allUsersList.addAll(studentsList);

        updateTableColumns(tableView, "All Users");
        tableView.setItems(allUsersList);  // Now this works

      
        // Filter function to search user list by the search text
        java.util.function.Function<String, javafx.collections.ObservableList<UsersModel>> filterUsers = searchText -> {
            String lowerSearch = searchText.toLowerCase();
            String currentRole = roleSwitcher.getValue();

            javafx.collections.ObservableList<UsersModel> sourceList;
            switch (currentRole) {
            case "All Users" -> sourceList = allUsersList;
            case "Teacher" -> sourceList = teachersList;
            case "Student" -> sourceList = studentsList;
            case "Admin" -> sourceList = adminsList;
            default -> sourceList = FXCollections.observableArrayList();
        }


            String selectedYear = yearLevelFilter.getValue();
            boolean filterByYear = currentRole.equals("Student") && selectedYear != null && !selectedYear.equals("All Year Level");
            
            String selectedProgram = programFilter.getValue();
            boolean filterByProgram = selectedProgram != null && !selectedProgram.equals("All Programs");

            javafx.collections.ObservableList<UsersModel> filtered = FXCollections.observableArrayList();

            for (UsersModel user : sourceList) {
                boolean matchesSearch = 
                    (user.getUsername() != null && user.getUsername().toLowerCase().contains(lowerSearch)) ||
                    (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerSearch)) ||
                    (user.getFirstName() != null && user.getFirstName().toLowerCase().contains(lowerSearch)) ||
                    (user.getLastName() != null && user.getLastName().toLowerCase().contains(lowerSearch)) ||
                    (user.getMiddleName() != null && user.getMiddleName().toLowerCase().contains(lowerSearch)) ||
                    (user.getProgram() != null && user.getProgram().toLowerCase().contains(lowerSearch)) ||
                    (user.getSection() != null && user.getSection().toLowerCase().contains(lowerSearch)) ||
                    (user.getYearLevel() != null && user.getYearLevel().toLowerCase().contains(lowerSearch));

                boolean matchesYear = !filterByYear || 
                    (user.getYearLevel() != null && user.getYearLevel().equalsIgnoreCase(selectedYear));
                
                boolean matchesProgram = !filterByProgram ||
                	    (user.getProgram() != null && user.getProgram().equalsIgnoreCase(selectedProgram));


                if (matchesSearch && matchesYear && matchesProgram) {
                    filtered.add(user);
                }

            }

            return filtered;
        };


     // Search field listener to filter table data
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            tableView.setItems(filterUsers.apply(newText));
        });

        // Year level filter listener
        yearLevelFilter.setOnAction(e -> {
            tableView.setItems(filterUsers.apply(searchField.getText()));
        });
        
        programFilter.setOnAction(e -> {
            tableView.setItems(filterUsers.apply(searchField.getText()));
        });

        roleSwitcher.setOnAction(e -> {
            String role = roleSwitcher.getValue();
            updateTableColumns(tableView, role);
            switch (role) {
            case "All Users" -> {
                updateTableColumns(tableView, "All Users");
                tableView.setItems(allUsersList);
                yearLevelFilter.setVisible(false);
                programFilter.setVisible(false);  // Hide program filter too
            }
            case "Admin" -> {
                updateTableColumns(tableView, "Admin");
                tableView.setItems(adminsList);
                yearLevelFilter.setVisible(false);
                programFilter.setVisible(false);
            }
            case "Teacher" -> {
                updateTableColumns(tableView, "Teacher");
                tableView.setItems(teachersList);
                yearLevelFilter.setVisible(false);
                programFilter.setVisible(false);
            }
            case "Student" -> {
                updateTableColumns(tableView, "Student");
                tableView.setItems(filterUsers.apply(searchField.getText()));
                yearLevelFilter.setVisible(true);
                programFilter.setVisible(true);  // <-- ADD THIS LINE
            }
            default -> {
                yearLevelFilter.setVisible(false);
                yearLevelFilter.setValue("All Year Level");
                programFilter.setVisible(false);
                programFilter.setValue("All Programs");
            }
            }

            Map<String, String> roleTitles = Map.of(
                "Admin", "Admin Users",
                "Teacher", "Teacher Users",
                "Student", "Student Users"
            );
            roleTitleLabel.setText(roleTitles.getOrDefault(role, "All Users"));
            searchField.clear(); // Clear search when switching role

            if (!role.equals("Student")) {
                yearLevelFilter.setVisible(false);
                yearLevelFilter.setValue("All Year Level");
                programFilter.setVisible(false);
                programFilter.setValue("All Programs");
            }
        });


        
        

        centerPanel.getChildren().addAll(shadowView, searchBox, roleSwitcher, yearLevelFilter, programFilter, roleTitleLabel, tableScrollPane);

        
    
        return centerPanel;
    }

    /**
     * Updates the columns of the given TableView based on the specified user role.
     * Clears existing columns and sets new ones relevant to the role.
     * 
     * @param table the TableView of UsersModel to update columns for
     * @param role the user role filter determining which columns to display; 
     *             e.g., "All Users" shows all user-related columns
     */
    @SuppressWarnings("unchecked")
    private static void updateTableColumns(TableView<UsersModel> table, String role) {
    	
    	table.getColumns().clear();
    	
    	if (role.equals("All Users")) {
    	    table.getColumns().clear();

    	    TableColumn<UsersModel, Integer> idCol = new TableColumn<>("User ID");
    	    idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

    	    TableColumn<UsersModel, String> usernameCol = new TableColumn<>("Username");
    	    usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

    	    TableColumn<UsersModel, String> emailCol = new TableColumn<>("Email");
    	    emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

    	    TableColumn<UsersModel, String> roleCol = new TableColumn<>("Role");
    	    roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

    	    table.getColumns().addAll(idCol, usernameCol, emailCol, roleCol);
    	    return;
    	}

        TableColumn<UsersModel, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        idCol.setPrefWidth(30);

        TableColumn<UsersModel, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(120);

        TableColumn<UsersModel, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(180);

        TableColumn<UsersModel, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameCol.setPrefWidth(120);

        TableColumn<UsersModel, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameCol.setPrefWidth(120);


        if (role.equals("Student")) {
            TableColumn<UsersModel, String> middleNameCol = new TableColumn<>("Middle Name");
            middleNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));
            middleNameCol.setPrefWidth(100);

            TableColumn<UsersModel, String> yearCol = new TableColumn<>("Year");
            yearCol.setCellValueFactory(new PropertyValueFactory<>("yearLevel"));
            yearCol.setPrefWidth(50);

            TableColumn<UsersModel, String> programCol = new TableColumn<>("Program");
            programCol.setCellValueFactory(new PropertyValueFactory<>("program"));
            programCol.setPrefWidth(220);

            TableColumn<UsersModel, String> sectionCol = new TableColumn<>("Section");
            sectionCol.setCellValueFactory(new PropertyValueFactory<>("section"));
            sectionCol.setPrefWidth(50);

            // Add columns in new order swapping First Name and Last Name
            table.getColumns().addAll(
                idCol,
                usernameCol,
                emailCol,
                lastNameCol,  // swapped here (lastName before firstName)
                firstNameCol,
                middleNameCol,
                yearCol,
                programCol,
                sectionCol
            );
        } else {
            // For other roles, add columns in default order
            table.getColumns().addAll(idCol, usernameCol, emailCol, firstNameCol, lastNameCol);
        }

    }

}
