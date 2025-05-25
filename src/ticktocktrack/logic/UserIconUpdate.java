package ticktocktrack.logic;

import javafx.scene.image.Image;
import ticktocktrack.database.DatabaseUserProfile;

/**
 * Utility class to update and retrieve the user's profile image.
 */
public class UserIconUpdate {

    /**
     * Returns the current logged-in user's profile image if available,
     * otherwise returns null.
     * <p>
     * The profile image is loaded from the profile path stored in the database
     * for the current user.
     *
     * @return the Image object of the user's profile image, or null if not set or
     *         if an error occurs while loading.
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