package com.deloria.socmed;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String author;
    private String title;
    private String content;
    private String imageUrl;

    private ZonedDateTime timestamp;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments;

    public Integer getId() { return id; }
    public String getAuthor() { return author; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
    public ZonedDateTime getTimestamp() { return timestamp; }
    public List<Comment> getComments() { return comments; }

    public void setAuthor(String author) { this.author = author; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setTimestamp(ZonedDateTime timestamp) { this.timestamp = timestamp; }
    public void setComments(List<Comment> comments) { this.comments = comments; }

    public void setTimestampToNowInJakarta() {
        this.timestamp = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"));
    }

    @Transient
    public String getRelativeTime() {
        return toRelativeTimeString(this.timestamp);
    }

    private static String toRelativeTimeString(ZonedDateTime timestamp) {
        if (timestamp == null) return "";
        ZonedDateTime now = ZonedDateTime.now(timestamp.getZone());
        Duration duration = Duration.between(timestamp, now);

        long seconds = duration.getSeconds();
        if (seconds < 60)
            return "Just now";
        if (seconds < 3600)
            return (seconds / 60) + "m";
        if (seconds < 86400)
            return (seconds / 3600) + "h";
        if (seconds < 2592000) // 30 days
            return (seconds / 86400) + "d";

        if (timestamp.getYear() != now.getYear())
            return timestamp.format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
        return timestamp.format(DateTimeFormatter.ofPattern("MMM d"));
    }
}