package org.wso2.carbon.identity.api.server.application.management.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

public class RoleAssignPatchOperation {

    @XmlType(name="OpEnum")
    @XmlEnum(String.class)
    public enum OpEnum {

        @XmlEnumValue("add") ADD(String.valueOf("add")), @XmlEnumValue("remove") REMOVE(String.valueOf("remove")), @XmlEnumValue("replace") REPLACE(String.valueOf("replace"));


        private String value;

        OpEnum(String v) {
            value = v;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static OpEnum fromValue(String value) {
            for (OpEnum b : OpEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    private OpEnum op;
    private String path;
    private List<String> value = null;


    /**
     **/
    public RoleAssignPatchOperation op(OpEnum op) {

        this.op = op;
        return this;
    }

    @ApiModelProperty(example = "add", value = "")
    @JsonProperty("op")
    @Valid
    public OpEnum getOp() {
        return op;
    }
    public void setOp(OpEnum op) {
        this.op = op;
    }

    /**
     **/
    public RoleAssignPatchOperation path(String path) {

        this.path = path;
        return this;
    }

    @ApiModelProperty(example = "groups", value = "")
    @JsonProperty("path")
    @Valid
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    /**
     **/
    public RoleAssignPatchOperation value(List<String> value) {

        this.value = value;
        return this;
    }

    @ApiModelProperty(value = "")
    @JsonProperty("value")
    @Valid
    public List<String> getValue() {
        return value;
    }
    public void setValue(List<String> value) {
        this.value = value;
    }

    public RoleAssignPatchOperation addValueItem(String valueItem) {
        if (this.value == null) {
            this.value = new ArrayList<>();
        }
        this.value.add(valueItem);
        return this;
    }



    @Override
    public boolean equals(java.lang.Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoleAssignPatchOperation rolePatchOperation = (RoleAssignPatchOperation) o;
        return Objects.equals(this.op, rolePatchOperation.op) &&
                Objects.equals(this.path, rolePatchOperation.path) &&
                Objects.equals(this.value, rolePatchOperation.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, path, value);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class RolePatchOperation {\n");

        sb.append("    op: ").append(toIndentedString(op)).append("\n");
        sb.append("    path: ").append(toIndentedString(path)).append("\n");
        sb.append("    value: ").append(toIndentedString(value)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {

        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n");
    }
}
