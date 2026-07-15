package com.krushna.commercecore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String name;

    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public enum PredefinedRole {
        ROLE_USER("ROLE_USER"),
        ROLE_ADMIN("ROLE_ADMIN"),
        ROLE_SELLER("ROLE_SELLER"),
        ROLE_SUPPORT_AGENT("ROLE_SUPPORT_AGENT"),
        ROLE_DELIVERY_PARTNER("ROLE_DELIVERY_PARTNER");

        private final String roleName;

        PredefinedRole(String roleName) {
            this.roleName = roleName;
        }

        public String getRoleName() {
            return roleName;
        }
    }
}
