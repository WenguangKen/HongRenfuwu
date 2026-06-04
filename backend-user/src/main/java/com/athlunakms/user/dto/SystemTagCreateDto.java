package com.athlunakms.user.dto;

public class SystemTagCreateDto {
    private String category;
    private String name;
    private String description;
    private String backgroundColor;
    private String borderColor;
    private String textColor;
    private Integer sortOrder;
    private Boolean enabled;

    public String getCategory() {
        return this.category;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getBackgroundColor() {
        return this.backgroundColor;
    }

    public String getBorderColor() {
        return this.borderColor;
    }

    public String getTextColor() {
        return this.textColor;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SystemTagCreateDto)) {
            return false;
        }
        SystemTagCreateDto other = (SystemTagCreateDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Integer this$sortOrder = this.getSortOrder();
        Integer other$sortOrder = other.getSortOrder();
        if (this$sortOrder == null ? other$sortOrder != null : !((Object)this$sortOrder).equals(other$sortOrder)) {
            return false;
        }
        Boolean this$enabled = this.getEnabled();
        Boolean other$enabled = other.getEnabled();
        if (this$enabled == null ? other$enabled != null : !((Object)this$enabled).equals(other$enabled)) {
            return false;
        }
        String this$category = this.getCategory();
        String other$category = other.getCategory();
        if (this$category == null ? other$category != null : !this$category.equals(other$category)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        String this$backgroundColor = this.getBackgroundColor();
        String other$backgroundColor = other.getBackgroundColor();
        if (this$backgroundColor == null ? other$backgroundColor != null : !this$backgroundColor.equals(other$backgroundColor)) {
            return false;
        }
        String this$borderColor = this.getBorderColor();
        String other$borderColor = other.getBorderColor();
        if (this$borderColor == null ? other$borderColor != null : !this$borderColor.equals(other$borderColor)) {
            return false;
        }
        String this$textColor = this.getTextColor();
        String other$textColor = other.getTextColor();
        return !(this$textColor == null ? other$textColor != null : !this$textColor.equals(other$textColor));
    }

    protected boolean canEqual(Object other) {
        return other instanceof SystemTagCreateDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $sortOrder = this.getSortOrder();
        result = result * 59 + ($sortOrder == null ? 43 : ((Object)$sortOrder).hashCode());
        Boolean $enabled = this.getEnabled();
        result = result * 59 + ($enabled == null ? 43 : ((Object)$enabled).hashCode());
        String $category = this.getCategory();
        result = result * 59 + ($category == null ? 43 : $category.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        String $backgroundColor = this.getBackgroundColor();
        result = result * 59 + ($backgroundColor == null ? 43 : $backgroundColor.hashCode());
        String $borderColor = this.getBorderColor();
        result = result * 59 + ($borderColor == null ? 43 : $borderColor.hashCode());
        String $textColor = this.getTextColor();
        result = result * 59 + ($textColor == null ? 43 : $textColor.hashCode());
        return result;
    }

    public String toString() {
        return "SystemTagCreateDto(category=" + this.getCategory() + ", name=" + this.getName() + ", description=" + this.getDescription() + ", backgroundColor=" + this.getBackgroundColor() + ", borderColor=" + this.getBorderColor() + ", textColor=" + this.getTextColor() + ", sortOrder=" + this.getSortOrder() + ", enabled=" + this.getEnabled() + ")";
    }
}

