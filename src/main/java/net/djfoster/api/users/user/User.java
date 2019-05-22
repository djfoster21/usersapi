package net.djfoster.api.users.user;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.Null;
import java.util.Date;

@Data
@Entity
public class User {
    private @Id
    @GeneratedValue
    Long id;
    private String username;

    private @Email String email;
    private String name;
    private String lastName;

    private
    @CreationTimestamp
    Date created_at;
    private
    @UpdateTimestamp
    Date updated_at;

    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
    public User(String username,String email, String name, String lastName){
        this.username = username;
        this.email = email;
        this.name = name;
        this.lastName = lastName;
    }
}
