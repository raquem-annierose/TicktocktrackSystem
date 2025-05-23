package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ticktocktrack.database.DatabaseIndividualReport;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.Student;
import ticktocktrack.logic.UsersModel;

public class CardIndividualReport {

	public static void showStudentDetailDialog(Student student) {
	    UsersModel currentUser = Session.getCurrentUser();
	    if (currentUser == null || currentUser.getTeacherId() == null) {
	        System.err.println("No logged-in teacher found.");
	        return;
	    }
	    int teacherId = currentUser.getTeacherId();

	    Student fullStudent = DatabaseIndividualReport.getStudentById(student.getStudentId(), teacherId);
	    if (fullStudent == null) {
	        System.err.println("Student details not found for ID: " + student.getStudentId());
	        return;
	    }

	    Stage dialog = new Stage();
	    dialog.initModality(Modality.APPLICATION_MODAL);
	    dialog.setTitle("Student Details");

	    VBox dialogVBox = new VBox(10);
	    dialogVBox.setPadding(new Insets(20));

	    Label nameLabel = new Label("Name: " + fullStudent.getLastName() + ", " + fullStudent.getFirstName() + " " + fullStudent.getMiddleName());
	    Label emailLabel = new Label("Email: " + fullStudent.getEmail());
	    Label yearLabel = new Label("Year Level: " + fullStudent.getYearLevel());
	    Label programLabel = new Label("Program: " + fullStudent.getProgram());
	    Label sectionLabel = new Label("Section: " + fullStudent.getSection());
	    Label totalClassesLabel = new Label("Total Classes: " + fullStudent.getTotalClasses());

	    dialogVBox.getChildren().addAll(nameLabel, emailLabel, yearLabel, programLabel, sectionLabel, totalClassesLabel);

	    Scene dialogScene = new Scene(dialogVBox);
	    dialog.setScene(dialogScene);
	    dialog.showAndWait();
	}

}
