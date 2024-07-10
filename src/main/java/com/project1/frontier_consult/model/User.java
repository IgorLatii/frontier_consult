package com.project1.frontier_consult.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Setter
@Getter
@Entity(name = "usersDataTable")
public class User {

    @Id
    private Long chatID;

    private String firstName;

    private String lastName;

    private String userName;

    private Timestamp registeredAt;

    @Override
    public String toString() {
        return "User{" +
                "chatID=" + chatID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", registeredAt=" + registeredAt +
                '}';
    }
}
