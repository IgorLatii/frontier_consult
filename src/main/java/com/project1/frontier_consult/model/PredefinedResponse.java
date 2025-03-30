package com.project1.frontier_consult.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
//import javax.persistence.Id;
//import java.sql.Timestamp;

@Setter
@Getter
@Entity//(name = "predefined_responses")
@Table(name = "predefined_responses", uniqueConstraints = {@UniqueConstraint(columnNames = {"command", "language"})})


public class PredefinedResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String command;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String responseText;

    @Column(nullable = false, length = 10)
    private String language;
}
