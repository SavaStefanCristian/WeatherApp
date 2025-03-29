package org.proiect.persistence.model;

import lombok.Getter;
import lombok.Setter;
import org.proiect.persistence.model.enums.Role;
//import org.proiect.persistence.model.enums.RoleConverter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "app_role", schema = "public")
public class RoleEntity implements PersistableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false)
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    //@Convert(converter = RoleConverter.class)
    private Role role;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="user_role",
            joinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "role_id"),
            inverseJoinColumns = @JoinColumn(
                    name="user_id", referencedColumnName = "user_id"))
    private List<UserEntity> users;
}