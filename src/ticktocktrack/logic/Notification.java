	package ticktocktrack.logic;
	
	import java.time.Duration;
	import java.time.LocalDateTime;
	import java.time.format.DateTimeFormatter;

	/**
	 * Represents a notification with a unique identifier, message, timestamp, status, sender information,
	 * and the sender's profile path.
	 */
	public class Notification {
	    
	    /** The unique identifier for the notification. */
	    private int notificationId;
	    
	    /** The message content of the notification. */
	    private String message;
	    
	    /** The timestamp indicating when the notification was sent. */
	    private LocalDateTime dateSent;
	    
	    /** The status or type of the notification (e.g., info, warning, alert). */
	    private String status;
	    
	    /** The user ID of the sender of the notification. */
	    private int senderUserId;
	    
	    /** The profile path or URL of the sender's profile picture. */
	    private String senderProfilePath; // Added this field

	    /**
	     * Constructs a Notification with given message, date sent, status, and sender's user ID.
	     * 
	     * @param message The notification message content.
	     * @param dateSent The date and time the notification was sent.
	     * @param status The current status of the notification.
	     * @param senderUserId The user ID of the sender.
	     */
	    public Notification(String message, LocalDateTime dateSent, String status, int senderUserId) {
	    	this.notificationId = notificationId;
	    	this.message = message;
	        this.dateSent = dateSent;
	        this.status = status;
	        this.senderUserId = senderUserId;
	    }

	    /**
	     * Gets the notification message.
	     * @return The message content.
	     */
	    public String getMessage() {
	        return message;
	    }

	    /**
	     * Gets the date and time the notification was sent.
	     * @return The dateSent as LocalDateTime.
	     */
	    public LocalDateTime getDateSent() {
	        return dateSent;
	    }

	    /**
	     * Gets the current status of the notification.
	     * @return The status string.
	     */
	    public String getStatus() {
	        return status;
	    }

	    /**
	     * Gets the sender's user ID.
	     * @return The senderUserId integer.
	     */
	    public int getSenderUserId() {
	        return senderUserId;
	    }

	    /**
	     * Returns a human-readable string describing how long ago the notification was sent.
	     * @return A string like "5 minutes ago" or "2 days ago".
	     */
	    public String getTimeAgo() {
	        Duration duration = Duration.between(dateSent, LocalDateTime.now());
	        long seconds = duration.getSeconds();

	        if (seconds < 60) {
	            return seconds + " seconds ago";
	        } else if (seconds < 3600) {
	            return (seconds / 60) + " minutes ago";
	        } else if (seconds < 86400) {
	            return (seconds / 3600) + " hours ago";
	        } else if (seconds < 2592000) {
	            return (seconds / 86400) + " days ago";
	        } else if (seconds < 31536000) {
	            return (seconds / 2592000) + " months ago";
	        } else {
	            return (seconds / 31536000) + " years ago";
	        }
	    }

	    /**
	     * Returns the date sent formatted as "MMM dd, yyyy HH:mm".
	     * @return The formatted date string.
	     */
	    public String getDateSentFormatted() {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
	        return dateSent.format(formatter);
	    }
	    /**
	     * Retrieves the profile path of the sender.
	     *
	     * @return The sender's profile path as a String.
	     */
	    public String getSenderProfilePath() {
	        return senderProfilePath;
	    }

	    /**
	     * Sets the profile path for the sender.
	     *
	     * @param senderProfilePath The sender's profile path to set.
	     */
	    public void setSenderProfilePath(String senderProfilePath) {
	        this.senderProfilePath = senderProfilePath;
	    }
	    
	    /**
	     * Retrieves the unique identifier of the notification.
	     *
	     * @return The notification's unique identifier.
	     */
	    public int getNotificationId() {
	        return notificationId;
	    }

	    /**
	     * Sets the unique identifier for the notification.
	     *
	     * @param notificationId The unique identifier to set for the notification.
	     */
	    public void setNotificationId(int notificationId) {
	        this.notificationId = notificationId;
	    }

	}
