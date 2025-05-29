package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ticktocktrack.database.UserDAO;
import ticktocktrack.logic.UsersModel;

/**
 * Provides functionality for editing user details within the administration panel.
 * <p>
 * This class includes a list of available academic programs and a method to display 
 * a modal dialog for editing the information of a selected user.
 * </p>
 */
public class AdminEditUsers {

	/**
	 * List of academic programs available for selection when editing user details.
	 */
    private static final String[] PROGRAMS = {
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
    };

    // Place this method here, outside of showEditDialog
    private static String validateUserEditFields(String username, String email, String role, String firstName, String lastName, String middleName, String program, String section, String yearLevel) {
        if (username == null || username.length() < 6 || username.length() > 12) {
            return "Username must be between 6 and 12 characters.";
        }
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            return "Please enter a valid email address.";
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            return "First name is required.";
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            return "Last name is required.";
        }
        if (role != null && role.equalsIgnoreCase("Student")) {
            if (middleName == null || middleName.trim().isEmpty()) {
                return "Middle name is required for students.";
            }
            if (program == null || program.trim().isEmpty()) {
                return "Program is required for students.";
            }
            if (section == null || section.trim().isEmpty()) {
                return "Section is required for students.";
            }
            if (yearLevel == null || yearLevel.trim().isEmpty()) {
                return "Year level is required for students.";
            }
        }
        return null;
    }

    /**
     * Displays a modal dialog to edit the details of the specified user.
     * 
     * @param user the UsersModel instance representing the user to edit
     */
    public static void showEditDialog(UsersModel user) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit User: " + user.getUsername());

        // Root container
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f9f9f9; -fx-font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;");
        root.setPrefWidth(720);

        // Top colored bar based on role
        Region roleBar = new Region();
        roleBar.setPrefHeight(5);
        roleBar.setMaxWidth(Double.MAX_VALUE);
        roleBar.getStyleClass().add("role-bar");
        roleBar.getStyleClass().add(user.getRole().toLowerCase()); // expects css classes admin, teacher, student

        // Form GridPane
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setMaxWidth(Double.MAX_VALUE);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(35);
        col1.setHalignment(javafx.geometry.HPos.LEFT); // change RIGHT to LEFT

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(65);
        col2.setHalignment(javafx.geometry.HPos.LEFT);

        form.getColumnConstraints().addAll(col1, col2);


        // Form fields
        TextField usernameField = new TextField(user.getUsername());
        TextField emailField = new TextField(user.getEmail());
        Label roleLabel = new Label(user.getRole());

        TextField firstNameField = new TextField(user.getFirstName());
        TextField lastNameField = new TextField(user.getLastName());
        TextField middleNameField = new TextField(user.getMiddleName());

        ComboBox<String> programComboBox = new ComboBox<>();
        programComboBox.getItems().addAll(PROGRAMS);
        if (user.getProgram() != null && !user.getProgram().isEmpty()) {
            programComboBox.setValue(user.getProgram());
        } else {
            programComboBox.setPromptText("Select Program");
        }

        TextField sectionField = new TextField(user.getSection());
        TextField yearLevelField = new TextField(user.getYearLevel());

        // Add CSS style classes for uniform input styling
        usernameField.getStyleClass().add("input-field");
        emailField.getStyleClass().add("input-field");
        firstNameField.getStyleClass().add("input-field");
        lastNameField.getStyleClass().add("input-field");
        middleNameField.getStyleClass().add("input-field");
        programComboBox.getStyleClass().add("input-field");
        sectionField.getStyleClass().add("input-field");
        yearLevelField.getStyleClass().add("input-field");
        roleLabel.getStyleClass().add("role-label");

        int row = 0;
        form.addRow(row++, new Label("Username:"), usernameField);
        form.addRow(row++, new Label("Email:"), emailField);
        form.addRow(row++, new Label("Role:"), roleLabel);
        form.addRow(row++, new Label("First Name:"), firstNameField);
        form.addRow(row++, new Label("Last Name:"), lastNameField);

        if (user.getRole().equalsIgnoreCase("Student")) {
            form.addRow(row++, new Label("Middle Name:"), middleNameField);
            form.addRow(row++, new Label("Program:"), programComboBox);
            form.addRow(row++, new Label("Section:"), sectionField);
            form.addRow(row++, new Label("Year Level:"), yearLevelField);
        }

        // Buttons at bottom right
        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");

        String saveButtonStyle =
                "-fx-background-color: #FFFFFF;" +
                "-fx-text-fill: #4CB17B;" +
                "-fx-font-size: 11px;" +
                "-fx-padding: 6 18 6 18;" +
                "-fx-background-radius: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: #4CB17B;" +
                "-fx-border-width: 0.7;" +
                "-fx-font-family: 'Poppins';" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;";

        String cancelButtonStyle =
                "-fx-background-color: #FFFFFF;" +
                "-fx-text-fill: #DC5E55;" +
                "-fx-font-size: 11px;" +
                "-fx-padding: 6 18 6 18;" +
                "-fx-background-radius: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: #DC5E55;" +
                "-fx-border-width: 0.7;" +
                "-fx-font-family: 'Poppins';" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;";

        saveBtn.setStyle(saveButtonStyle);
        cancelBtn.setStyle(cancelButtonStyle);
        buttons.getChildren().addAll(saveBtn, cancelBtn);


        // Center the GridPane horizontally by wrapping it in an HBox
        HBox formContainer = new HBox();
        formContainer.setAlignment(Pos.CENTER);  // centers the GridPane inside
        formContainer.getChildren().add(form);

        // Add role bar, form container, and buttons to root
        
        root.getChildren().addAll(roleBar, formContainer, buttons);

        // Add a role-specific style class to input fields to color their border
        String roleClass = user.getRole().toLowerCase(); // "admin", "teacher", or "student"
        usernameField.getStyleClass().add(roleClass);
        emailField.getStyleClass().add(roleClass);
        firstNameField.getStyleClass().add(roleClass);
        lastNameField.getStyleClass().add(roleClass);
        middleNameField.getStyleClass().add(roleClass);
        programComboBox.getStyleClass().add(roleClass);
        sectionField.getStyleClass().add(roleClass);
        yearLevelField.getStyleClass().add(roleClass);


        // Save button action
        saveBtn.setOnAction(e -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String middleName = middleNameField.getText();
            String program = programComboBox.getValue();
            String section = sectionField.getText();
            String yearLevel = yearLevelField.getText();
            String role = user.getRole();

            String validationError = validateUserEditFields(
                username, email, role, firstName, lastName, middleName, program, section, yearLevel
            );
            if (validationError != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, validationError, ButtonType.OK);
                alert.initOwner(dialog);
                alert.showAndWait();
                return;
            }

            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);

            if (role.equalsIgnoreCase("Student")) {
                user.setMiddleName(middleName);
                user.setProgram(program != null ? program : "");
                user.setSection(section);
                user.setYearLevel(yearLevel);
            }

            boolean success = UserDAO.updateUser(user);
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "User updated successfully!", ButtonType.OK);
                alert.initOwner(dialog);
                alert.showAndWait();
                dialog.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update user.", ButtonType.OK);
                alert.initOwner(dialog);
                alert.showAndWait();
            }
        });

        // Cancel closes dialog
        cancelBtn.setOnAction(e -> dialog.close());

        // Scene & styles
        double baseHeight = 100; // top bar, padding, buttons space, etc.
        double rowHeight = 40; // estimated height per form row

        // Count rows based on role
        int formRows = 6; // common fields (username, email, role, first name, last name)
        if (user.getRole().equalsIgnoreCase("Student")) {
            formRows += 5; // middle name, program, section, year level
        }

        double sceneHeight = baseHeight + rowHeight * formRows;

        Scene scene = new Scene(root, 630, sceneHeight);

        scene.getStylesheets().add(AdminEditUsers.class.getResource("/resources/css/table-style.css").toExternalForm());

        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }
}
