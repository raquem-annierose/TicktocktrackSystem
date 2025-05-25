	package ticktocktrack.logic;
	
	import java.time.LocalDateTime;
	import java.time.Duration;
	import java.time.format.DateTimeFormatter;
	
	public class Notification {
	    private String message;
	    private LocalDateTime dateSent;
	    private String status;
	    private int senderUserId;  // Add this field
	
	    // Update constructor to accept senderUserId
	    public Notification(String message, LocalDateTime dateSent, String status, int senderUserId) {
	        this.message = message;
	        this.dateSent = dateSent;
	        this.status = status;
	        this.senderUserId = senderUserId;
	    }
	
	    public String getMessage() {
	        return message;
	    }
	
	    public LocalDateTime getDateSent() {
	        return dateSent;
	    }
	
	    public String getStatus() {
	        return status;
	    }
	
	    public int getSenderUserId() {
	        return senderUserId;
	    }
	
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
	
	    public String getDateSentFormatted() {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
	        return dateSent.format(formatter);
	    }
	    
	    private String relatedDate; // YYYY-MM-DD format

	    private String teacherName;
	    private String courseName;
	    
	    
	    
	
	}
