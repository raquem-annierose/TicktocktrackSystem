package ticktocktrack.logic;

import javafx.scene.image.Image;
import ticktocktrack.database.DatabaseUserProfile;

public class UserIconUpdate {

    /**
     * Returns the user's profile image if available, otherwise returns null.
     * Loads image from DB profile path stored for the current logged-in user.
     */
    public static Image getCurrentUserProfileImage() {
        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("No current user logged in!");
            return null;
        }

        String profilePath = DatabaseUserProfile.getProfilePath(currentUser.getUserId());
        if (profilePath == null || profilePath.isEmpty()) {
            System.out.println("User has no profile image set.");
            return null;
        }

        try {
            // Load image from profile path (assuming it's a valid URI)
            return new Image(profilePath);
        } catch (Exception e) {
            System.err.println("Failed to load profile image from path: " + profilePath);
            e.printStackTrace();
            return null;
        }
    }
}
