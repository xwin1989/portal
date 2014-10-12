package com.nx.domain.security;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Neal on 10/12 012.
 */
@Entity
public class Resource {
    @Id
    private Long id;

    private String name;

    private ResourceType Type;

    private int priority;

    private Resource parent;

    private String permission;

    private boolean available;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceType getType() {
        return Type;
    }

    public void setType(ResourceType type) {
        Type = type;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Resource getParent() {
        return parent;
    }

    public void setParent(Resource parent) {
        this.parent = parent;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
