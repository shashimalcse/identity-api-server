/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.api.server.application.management.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.wso2.carbon.identity.api.server.application.management.v1.Permission;
import org.wso2.carbon.identity.api.server.application.management.v1.RoleAssignedGroup;
import org.wso2.carbon.identity.api.server.application.management.v1.RoleAssignedUser;
import javax.validation.constraints.*;


import io.swagger.annotations.*;
import java.util.Objects;
import javax.validation.Valid;
import javax.xml.bind.annotation.*;

public class Role  {
  
    private String id;
    private String name;
    private List<Permission> permissions = null;

    private List<RoleAssignedUser> assignedUsers = null;

    private List<RoleAssignedGroup> assignedGroups = null;


    /**
    **/
    public Role id(String id) {

        this.id = id;
        return this;
    }
    
    @ApiModelProperty(example = "85e3f4b8-0d22-4181-b1e3-1651f71b88bd", value = "")
    @JsonProperty("id")
    @Valid
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    /**
    **/
    public Role name(String name) {

        this.name = name;
        return this;
    }
    
    @ApiModelProperty(example = "administrator", value = "")
    @JsonProperty("name")
    @Valid
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    /**
    **/
    public Role permissions(List<Permission> permissions) {

        this.permissions = permissions;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("permissions")
    @Valid
    public List<Permission> getPermissions() {
        return permissions;
    }
    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public Role addPermissionsItem(Permission permissionsItem) {
        if (this.permissions == null) {
            this.permissions = new ArrayList<>();
        }
        this.permissions.add(permissionsItem);
        return this;
    }

        /**
    **/
    public Role assignedUsers(List<RoleAssignedUser> assignedUsers) {

        this.assignedUsers = assignedUsers;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("assignedUsers")
    @Valid
    public List<RoleAssignedUser> getAssignedUsers() {
        return assignedUsers;
    }
    public void setAssignedUsers(List<RoleAssignedUser> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public Role addAssignedUsersItem(RoleAssignedUser assignedUsersItem) {
        if (this.assignedUsers == null) {
            this.assignedUsers = new ArrayList<>();
        }
        this.assignedUsers.add(assignedUsersItem);
        return this;
    }

        /**
    **/
    public Role assignedGroups(List<RoleAssignedGroup> assignedGroups) {

        this.assignedGroups = assignedGroups;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("assignedGroups")
    @Valid
    public List<RoleAssignedGroup> getAssignedGroups() {
        return assignedGroups;
    }
    public void setAssignedGroups(List<RoleAssignedGroup> assignedGroups) {
        this.assignedGroups = assignedGroups;
    }

    public Role addAssignedGroupsItem(RoleAssignedGroup assignedGroupsItem) {
        if (this.assignedGroups == null) {
            this.assignedGroups = new ArrayList<>();
        }
        this.assignedGroups.add(assignedGroupsItem);
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
        Role role = (Role) o;
        return Objects.equals(this.id, role.id) &&
            Objects.equals(this.name, role.name) &&
            Objects.equals(this.permissions, role.permissions) &&
            Objects.equals(this.assignedUsers, role.assignedUsers) &&
            Objects.equals(this.assignedGroups, role.assignedGroups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, permissions, assignedUsers, assignedGroups);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class Role {\n");
        
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    permissions: ").append(toIndentedString(permissions)).append("\n");
        sb.append("    assignedUsers: ").append(toIndentedString(assignedUsers)).append("\n");
        sb.append("    assignedGroups: ").append(toIndentedString(assignedGroups)).append("\n");
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

