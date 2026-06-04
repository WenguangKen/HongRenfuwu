package com.athlunakms.influencer.dto;

public class StageDistributionDto {
    private String label;
    private Long value;
    private String color;

    public String getLabel() {
        return this.label;
    }

    public Long getValue() {
        return this.value;
    }

    public String getColor() {
        return this.color;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof StageDistributionDto)) {
            return false;
        }
        StageDistributionDto other = (StageDistributionDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$value = this.getValue();
        Long other$value = other.getValue();
        if (this$value == null ? other$value != null : !((Object)this$value).equals(other$value)) {
            return false;
        }
        String this$label = this.getLabel();
        String other$label = other.getLabel();
        if (this$label == null ? other$label != null : !this$label.equals(other$label)) {
            return false;
        }
        String this$color = this.getColor();
        String other$color = other.getColor();
        return !(this$color == null ? other$color != null : !this$color.equals(other$color));
    }

    protected boolean canEqual(Object other) {
        return other instanceof StageDistributionDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $value = this.getValue();
        result = result * 59 + ($value == null ? 43 : ((Object)$value).hashCode());
        String $label = this.getLabel();
        result = result * 59 + ($label == null ? 43 : $label.hashCode());
        String $color = this.getColor();
        result = result * 59 + ($color == null ? 43 : $color.hashCode());
        return result;
    }

    public String toString() {
        return "StageDistributionDto(label=" + this.getLabel() + ", value=" + this.getValue() + ", color=" + this.getColor() + ")";
    }

    public StageDistributionDto(String label, Long value, String color) {
        this.label = label;
        this.value = value;
        this.color = color;
    }

    public StageDistributionDto() {
    }
}

