package com.Amogh.url_shortener.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="urls")
@Getter
@Setter
public class Url {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)//tells the db to handle auto-incrementing via its native serializing
    private Long id;

    @Column(nullable = false , columnDefinition = "TEXT")
    private String originalUrl;

    @Column(unique = true)
    private String shortCode;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Url(){

    }

}
