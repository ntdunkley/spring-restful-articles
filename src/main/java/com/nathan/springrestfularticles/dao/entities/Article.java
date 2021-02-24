package com.nathan.springrestfularticles.dao.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "article")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "content")
    private String content;

    @Column(name = "date")
    private LocalDate date;

    protected Article() {

    }

    public Article(String title, String author, String content, LocalDate date) {
        this.title = title;
        this.author = author;
        this.content = content;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("[")
                .append(id).append(", ")
                .append(title).append(", ")
                .append(author).append(", ")
                .append(content).append(", ")
                .append(date)
                .append("]")
                .toString();
    }
}
