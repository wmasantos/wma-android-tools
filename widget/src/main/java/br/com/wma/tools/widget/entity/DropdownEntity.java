package br.com.wma.tools.widget.entity;

import java.io.Serializable;

public class DropdownEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String description;
    private boolean selected;

    public DropdownEntity() {
    }

    public DropdownEntity(String id, String description, boolean selected) {
        this.id = id;
        this.description = description;
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
