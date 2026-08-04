package com.substring.auth.auth_app.entities;
 import jakarta.persistence.*;
 import lombok.AllArgsConstructor;
 import lombok.Getter;
 import lombok.NoArgsConstructor;
 import lombok.Setter;
 import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;
 import org.springframework.boot.autoconfigure.web.WebProperties;

 import javax.naming.Name;
 import java.time.Instant;
 import java.util.UUID;
 import java.util.*;

 @Getter
 @Setter
 @AllArgsConstructor
 @NoArgsConstructor

@Entity
 @Table(name="users")
 public class User {
     @Id
     @GeneratedValue(strategy = GenerationType.UUID)
     @Column(name = "user_id")
     private UUID id;
     @Column(name="email_id",unique = true)
     private String email;
     private String name;
     private String password;
     private String image;
     private boolean enable = true;
     private Instant createdAt = Instant.now();
     private Instant updatedAt = Instant.now();
     private String gender;
     private Provider provider=Provider.Local;

     @ManyToMany(fetch=FetchType.EAGER )
     private Set<Role> roles = new HashSet<>();
}