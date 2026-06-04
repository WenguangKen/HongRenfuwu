package com.athlunakms.influencer.dto;

import java.util.List;

public class BatchUpdateDto {
    private List<Long> ids;
    private String field;
    private Object value;
    private List<Long> removeTagIds;

    public List<Long> getIds() {
        return this.ids;
    }

    public String getField() {
        return this.field;
    }

    public Object getValue() {
        return this.value;
    }

    public List<Long> getRemoveTagIds() {
        return this.removeTagIds;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setRemoveTagIds(List<Long> removeTagIds) {
        this.removeTagIds = removeTagIds;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BatchUpdateDto)) {
            return false;
        }
        BatchUpdateDto other = (BatchUpdateDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        List this$ids = this.getIds();
        List other$ids = other.getIds();
        if (this$ids == null ? other$ids != null : !((Object)this$ids).equals(other$ids)) {
            return false;
        }
        String this$field = this.getField();
        String other$field = other.getField();
        if (this$field == null ? other$field != null : !this$field.equals(other$field)) {
            return false;
        }
        Object this$value = this.getValue();
        Object other$value = other.getValue();
        if (this$value == null ? other$value != null : !this$value.equals(other$value)) {
            return false;
        }
        List this$removeTagIds = this.getRemoveTagIds();
        List other$removeTagIds = other.getRemoveTagIds();
        return !(this$removeTagIds == null ? other$removeTagIds != null : !((Object)this$removeTagIds).equals(other$removeTagIds));
    }

    protected boolean canEqual(Object other) {
        return other instanceof BatchUpdateDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List $ids = this.getIds();
        result = result * 59 + ($ids == null ? 43 : ((Object)$ids).hashCode());
        String $field = this.getField();
        result = result * 59 + ($field == null ? 43 : $field.hashCode());
        Object $value = this.getValue();
        result = result * 59 + ($value == null ? 43 : $value.hashCode());
        List $removeTagIds = this.getRemoveTagIds();
        result = result * 59 + ($removeTagIds == null ? 43 : ((Object)$removeTagIds).hashCode());
        return result;
    }

    public String toString() {
        return "BatchUpdateDto(ids=" + this.getIds() + ", field=" + this.getField() + ", value=" + this.getValue() + ", removeTagIds=" + this.getRemoveTagIds() + ")";
    }
}

