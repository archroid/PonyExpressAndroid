package ir.archroid.ponyexpress.Model;

public class User {

    private String id;
    private String username;
    private String email;
    private String imageURL;
    private String bio;
    private String verified;
    private String status;
    private long lastSeen;

    public User(String id, String username, String email, String imageURL, String bio, String verified, String status, long lastSeen) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.imageURL = imageURL;
        this.bio = bio;
        this.verified = verified;
        this.status = status;
        this.lastSeen = lastSeen;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }
}

