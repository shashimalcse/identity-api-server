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

package org.wso2.carbon.identity.api.server.application.management.v1.core;

import edu.emory.mathcs.backport.java.util.Arrays;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.api.server.application.management.common.ApplicationManagementServiceHolder;
import org.wso2.carbon.identity.api.server.application.management.v1.GroupAssignedRoleResponse;
import org.wso2.carbon.identity.api.server.application.management.v1.Role;
import org.wso2.carbon.identity.api.server.application.management.v1.RoleAssignPatchModel;
import org.wso2.carbon.identity.api.server.application.management.v1.RoleAssignedGroup;
import org.wso2.carbon.identity.api.server.application.management.v1.RoleAssignedUser;
import org.wso2.carbon.identity.api.server.application.management.v1.RoleCreationModel;
import org.wso2.carbon.identity.api.server.application.management.v1.RolePatchModel;
import org.wso2.carbon.identity.api.server.application.management.v1.RolePatchOp;
import org.wso2.carbon.identity.api.server.application.management.v1.RolePatchOpValue;
import org.wso2.carbon.identity.api.server.application.management.v1.UserAssignedRoleResponse;
import org.wso2.carbon.identity.api.server.application.management.v1.util.ApplicationRoleMgtEndpointUtil;
import org.wso2.carbon.identity.api.server.common.ContextLoader;
import org.wso2.carbon.identity.application.role.mgt.ApplicationRoleManager;
import org.wso2.carbon.identity.application.role.mgt.exceptions.ApplicationRoleManagementException;
import org.wso2.carbon.identity.application.role.mgt.model.ApplicationRole;
import org.wso2.carbon.identity.application.role.mgt.model.Group;
import org.wso2.carbon.identity.application.role.mgt.model.User;
import org.wso2.carbon.identity.core.ServiceURLBuilder;
import org.wso2.carbon.identity.core.URLBuilderException;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;

import static org.wso2.carbon.identity.api.server.application.management.common.ApplicationManagementConstants.GROUPS;
import static org.wso2.carbon.identity.api.server.application.management.common.ApplicationManagementConstants.LOCAL_IDP;
import static org.wso2.carbon.identity.api.server.application.management.common.ApplicationManagementConstants.SCIM2_ENDPOINT;
import static org.wso2.carbon.identity.api.server.application.management.common.ApplicationManagementConstants.TENANT_URL_SEPERATOR;
import static org.wso2.carbon.identity.api.server.application.management.common.ApplicationManagementConstants.URL_SEPERATOR;
import static org.wso2.carbon.identity.api.server.application.management.common.ApplicationManagementConstants.USERS;
import static org.wso2.carbon.identity.api.server.application.management.v1.constants.ApplicationRoleMgtEndpointConstants.ErrorMessage.ERROR_CODE_INVALID_PATCH_OPERATION;
import static org.wso2.carbon.identity.organization.management.service.constant.OrganizationManagementConstants.PATCH_OP_ADD;
import static org.wso2.carbon.identity.organization.management.service.constant.OrganizationManagementConstants.PATCH_OP_REMOVE;

/**
 * Application role management service.
 */
public class ApplicationRoleManagementService {

    /**
     * Add a new role to the application.
     *
     * @param applicationId Application ID.
     * @param role          Role.
     * @return Created role.
     */
    public Role addApplicationRole(String applicationId, RoleCreationModel role) {

        // Filter names of the permissions in role.getPermissions() to List[] permissions
        List<String> permissions = null;
        if (role.getPermissions() !=  null) {
            permissions =
                    role.getPermissions().stream().map(permission -> permission.getName()).collect(Collectors.toList());
        }
        String roleId = UUID.randomUUID().toString();
        try {
            ApplicationRole appRole = getApplicationRoleManager().addApplicationRole(
                    new ApplicationRole(roleId, role.getName(), permissions != null ?
                            permissions.toArray(new String[0]) : new String[0],
                            applicationId));

            Role createdRole = new Role();
            createdRole.setId(appRole.getRoleId());
            createdRole.setName(appRole.getRoleName());
            if (appRole.getPermissions() != null) {
                createdRole.setPermissions(Arrays.asList(appRole.getPermissions()));
            }
            return createdRole;
        } catch (ApplicationRoleManagementException e) {
            throw ApplicationRoleMgtEndpointUtil.handleApplicationRoleMgtException(e);
        }
    }

    /**
     * Get a role by ID.
     *
     * @param applicationId Application ID.
     * @param roleId        Role ID.
     * @return Role.
     */
    public Role getApplicationRole(String applicationId, String roleId) {

        try {
            ApplicationRole applicationRole = getApplicationRoleManager().getApplicationRoleById(roleId);
            Role role = new Role();
            role.setId(roleId);
            role.setName(applicationRole.getRoleName());
            // TODO set permissions.
            return role;
        } catch (ApplicationRoleManagementException e) {
            throw ApplicationRoleMgtEndpointUtil.handleApplicationRoleMgtException(e);
        }
    }

    /**
     * Get all roles of an application.
     *
     * @param applicationId Application ID.
     * @param before        Filter to get roles created before a given date. (optional)
     * @param after         Filter to get roles created after a given date. (optional)
     * @param limit         Maximum number of roles to return. (optional)
     * @param filter        Filter expression for filtering fields in the response. (optional)
     * @param sort          Sort expression for sorting the response. (optional)
     * @return List of roles.
     */
    public List<Role> getApplicationRoles(String applicationId, String before, String after, Integer limit,
                                          String filter, String sort) {

        List<ApplicationRole> applicationRoles = null;
        try {
            applicationRoles = getApplicationRoleManager().getApplicationRoles(applicationId);
            return applicationRoles.stream().map(applicationRole -> {
                Role role = new Role();
                role.setId(applicationRole.getRoleId());
                role.setName(applicationRole.getRoleName());
                // TODO set permissions.
                return role;
            }).collect(Collectors.toList());
        } catch (ApplicationRoleManagementException e) {
            throw ApplicationRoleMgtEndpointUtil.handleApplicationRoleMgtException(e);
        }
    }

    /**
     * Delete a role by ID.
     *
     * @param applicationId Application ID.
     * @param roleId        Role ID.
     */
    public void deleteApplicationRole(String applicationId, String roleId) {

        try {
            getApplicationRoleManager().deleteApplicationRole(roleId);
        } catch (ApplicationRoleManagementException e) {
            throw ApplicationRoleMgtEndpointUtil.handleApplicationRoleMgtException(e);
        }
    }

    /**
     * Update a role by ID.
     *
     * @param applicationId Application ID.
     * @param roleId        Role ID.
     * @param roleUpdate    Role update.
     */
    public ApplicationRole updateApplicationRole(String applicationId, String roleId, RolePatchModel roleUpdate) {

        List<String> addedPermission = null;
        List<String> removedPermission = null;
        if (roleUpdate.getAddedPermissions() !=  null) {
            addedPermission = roleUpdate.getAddedPermissions().stream()
                    .map(permission -> permission.getName()).collect(Collectors.toList());
        }
        if (roleUpdate.getRemovedPermissions() !=  null) {
            removedPermission = roleUpdate.getRemovedPermissions().stream()
                    .map(permission -> permission.getName()).collect(Collectors.toList());
        }
        try {
            return getApplicationRoleManager().updateApplicationRole(applicationId, roleId, roleUpdate.getName(),
                    addedPermission, removedPermission);
        } catch (ApplicationRoleManagementException e) {
            throw ApplicationRoleMgtEndpointUtil.handleApplicationRoleMgtException(e);
        }
    }

    /**
     * Update a app role assigned user by ID.
     *
     * @param applicationId Application ID.
     * @param roleId        Role ID.
     * @param roleAssignUpdateModel    Role assign update.
     */
    public UserAssignedRoleResponse updateApplicationRoleAssignedUsers(String applicationId, String roleId,
                                                   RoleAssignPatchModel roleAssignUpdateModel) {

        try {
            List<RolePatchOp> patchOperationList = roleAssignUpdateModel.getOperations();
            List<String> addUsers = new ArrayList<>();
            List<String> removedUsers = new ArrayList<>();

            for (RolePatchOp rolePatchOp : patchOperationList) {
                List<RolePatchOpValue> values = rolePatchOp.getValue();
                String patchOp = rolePatchOp.getOp();
                if ((CollectionUtils.isNotEmpty(values) && StringUtils.equalsIgnoreCase(patchOp, PATCH_OP_ADD))) {
                    for (RolePatchOpValue value : values) {
                        addUsers.add(value.getValue());
                    }
                } else if ((CollectionUtils.isNotEmpty(values) &&
                        StringUtils.equalsIgnoreCase(patchOp, PATCH_OP_REMOVE))) {
                    for (RolePatchOpValue value : values) {
                        removedUsers.add(value.getValue());
                    }
                } else {
                    // Invalid patch operations cannot be sent due to swagger validation.
                    // But, if values are not passed along with ADD operations, an error is thrown.
                    throw ApplicationRoleMgtEndpointUtil.handleException(Response.Status.BAD_REQUEST,
                            ERROR_CODE_INVALID_PATCH_OPERATION);
                }
            }
            String tenantDomain = ContextLoader.getTenantDomainFromContext();
            ApplicationRole applicationRole = getApplicationRoleManager().updateApplicationRoleAssignedUsers(roleId,
                    addUsers, removedUsers);
            UserAssignedRoleResponse response = new UserAssignedRoleResponse();
            List<RoleAssignedUser> users = getUsersForResponseObject(applicationRole.getAssignedUsers(),
                    tenantDomain);
            response.setAssignedUsers(users);
            return response;
        } catch (ApplicationRoleManagementException e) {
            throw ApplicationRoleMgtEndpointUtil.handleApplicationRoleMgtException(e);
        }
    }

    /**
     * Get app role's assigned users by ID.
     *
     * @param applicationId Application ID.
     * @param roleId        Role ID.
     */
    public UserAssignedRoleResponse getApplicationRoleAssignedUsers(String applicationId, String roleId) {

        ApplicationRole applicationRole = null;
        try {
            String tenantDomain = ContextLoader.getTenantDomainFromContext();
            applicationRole = getApplicationRoleManager().getApplicationRoleAssignedUsers(roleId);
            UserAssignedRoleResponse response = new UserAssignedRoleResponse();
            List<RoleAssignedUser> users = getUsersForResponseObject(applicationRole.getAssignedUsers(),
                    tenantDomain);
            response.setAssignedUsers(users);
            return response;
        } catch (ApplicationRoleManagementException e) {
            throw ApplicationRoleMgtEndpointUtil.handleApplicationRoleMgtException(e);
        }
    }

    /**
     * Update a app role assigned groups by ID.
     *
     * @param applicationId Application ID.
     * @param roleId        Role ID.
     * @param roleAssignUpdateModel    Role assign update.
     */
    public GroupAssignedRoleResponse updateApplicationRoleAssignedGroups(String applicationId, String roleId,
                                                                         String idpId,
                                                                         RoleAssignPatchModel roleAssignUpdateModel) {

        try {
            List<RolePatchOp> patchOperationList = roleAssignUpdateModel.getOperations();
            List<String> addGroups = new ArrayList<>();
            List<String> removedGroups = new ArrayList<>();

            for (RolePatchOp rolePatchOp : patchOperationList) {
                List<RolePatchOpValue> values = rolePatchOp.getValue();
                String patchOp = rolePatchOp.getOp().toString();
                if ((CollectionUtils.isNotEmpty(values) && StringUtils.equalsIgnoreCase(patchOp, PATCH_OP_ADD))) {
                    for (RolePatchOpValue value : values) {
                        addGroups.add(value.getValue());
                    }
                } else if ((CollectionUtils.isNotEmpty(values) &&
                        StringUtils.equalsIgnoreCase(patchOp, PATCH_OP_REMOVE))) {
                    for (RolePatchOpValue value : values) {
                        removedGroups.add(value.getValue());
                    }
                } else {
                    // Invalid patch operations cannot be sent due to swagger validation.
                    // But, if values are not passed along with ADD operations, an error is thrown.
                    throw ApplicationRoleMgtEndpointUtil.handleException(Response.Status.BAD_REQUEST,
                            ERROR_CODE_INVALID_PATCH_OPERATION);
                }
            }
            ApplicationRole applicationRole = getApplicationRoleManager().updateApplicationRoleAssignedGroups(roleId,
                    idpId, addGroups, removedGroups);
            String tenantDomain = ContextLoader.getTenantDomainFromContext();
            GroupAssignedRoleResponse response = new GroupAssignedRoleResponse();
            List<RoleAssignedGroup> groups = getGroupsForResponseObject(applicationRole.getAssignedGroups(),
                    tenantDomain);
            response.setAssignedGroups(groups);
            return response;
        } catch (ApplicationRoleManagementException e) {
            throw ApplicationRoleMgtEndpointUtil.handleApplicationRoleMgtException(e);
        }
    }

    /**
     * Get app role's assigned groups by ID.
     *
     * @param applicationId Application ID.
     * @param roleId        Role ID.
     */
    public GroupAssignedRoleResponse getApplicationRoleAssignedGroups(String applicationId, String roleId, String idp) {

        ApplicationRole applicationRole = null;
        try {
            String tenantDomain = ContextLoader.getTenantDomainFromContext();
            applicationRole = getApplicationRoleManager().getApplicationRoleAssignedGroups(roleId, idp);
            GroupAssignedRoleResponse response = new GroupAssignedRoleResponse();
            List<RoleAssignedGroup> groups = getGroupsForResponseObject(applicationRole.getAssignedGroups(),
                    tenantDomain);
            response.setAssignedGroups(groups);
            return response;
        } catch (ApplicationRoleManagementException e) {
            throw ApplicationRoleMgtEndpointUtil.handleApplicationRoleMgtException(e);
        }
    }

    private ApplicationRoleManager getApplicationRoleManager() {

        return ApplicationManagementServiceHolder.getApplicationRoleManagerService();
    }

    /**
     * Set the users for the response if they exist.
     *
     * @param roleAssignedUsers      The users assigned to a role.
     * @param tenantDomain tenantDomain.
     * @return The ApplicationRoleGetResponseUser list.
     */
    private List<RoleAssignedUser> getUsersForResponseObject(List<User> roleAssignedUsers,
                                                             String tenantDomain) {

        List<RoleAssignedUser> users = new ArrayList<>();
        for (User basicUser : roleAssignedUsers) {
            String uri = getSCIMUserURL(basicUser.getId(), tenantDomain);

            RoleAssignedUser user = new RoleAssignedUser();
            user.value(basicUser.getId());
            user.display(basicUser.getUserName());
            user.$ref(uri);
            users.add(user);
        }
        return users;
    }

    /**
     * Set the groups for the response if they exist.
     *
     * @param roleAssignedGroups      The groups assigned to a role.
     * @param tenantDomain tenantDomain.
     * @return The ApplicationRoleGetResponseUser list.
     */
    private List<RoleAssignedGroup> getGroupsForResponseObject(List<Group> roleAssignedGroups,
                                                               String tenantDomain) {

        List<RoleAssignedGroup> groups = new ArrayList<>();
        for (Group group : roleAssignedGroups) {
            String uri = getSCIMGroupURL(group.getGroupId(), tenantDomain);

            RoleAssignedGroup assignedGroup = new RoleAssignedGroup();
            assignedGroup.value(group.getGroupId());
            assignedGroup.display(group.getGroupName());
            if (LOCAL_IDP.equals(group.getIdpName())) {
                assignedGroup.$ref(uri);
            }
            groups.add(assignedGroup);
        }
        return groups;
    }

    private String getSCIMUserURL(String id, String tenantDomain) {
        return org.apache.commons.lang.StringUtils.isNotBlank(id) ? getSCIMUserURL(tenantDomain) + URL_SEPERATOR +
                id : null;
    }

    private String getSCIMUserURL(String tenantDomain) {

        String scimURL = getSCIMURL(tenantDomain);
        return scimURL + USERS;
    }

    private String getSCIMGroupURL(String id, String tenantDomain) {
        return org.apache.commons.lang.StringUtils.isNotBlank(id) ? getSCIMGroupURL(tenantDomain) + URL_SEPERATOR +
                id : null;
    }

    private String getSCIMGroupURL(String tenantDomain) {

        String scimURL = getSCIMURL(tenantDomain);
        return scimURL + GROUPS;
    }

    private String getSCIMURL(String tenantDomain) {

        String scimURL;
        try {
            if (IdentityTenantUtil.isTenantQualifiedUrlsEnabled()) {
                scimURL = ServiceURLBuilder.create().addPath(SCIM2_ENDPOINT).build()
                        .getAbsolutePublicURL();
            } else {
                String serverUrl = ServiceURLBuilder.create().build().getAbsolutePublicURL();
                if (isNotASuperTenantFlow(tenantDomain)) {
                    scimURL = serverUrl + "/t/" + tenantDomain + SCIM2_ENDPOINT;
                } else {
                    scimURL = serverUrl + SCIM2_ENDPOINT;
                }
            }
            return scimURL;
        } catch (URLBuilderException e) {
            // Fallback to legacy approach during error scenarios to maintain backward compatibility.
            return getSCIMURLLegacy();
        }
    }

    private String getSCIMURLLegacy() {

        String scimURL;
        String tenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();
        if (isNotASuperTenantFlow(tenantDomain)) {
            scimURL = IdentityUtil.getServerURL(
                    TENANT_URL_SEPERATOR + tenantDomain + SCIM2_ENDPOINT,
                    true, true);
        } else {
            scimURL = IdentityUtil.getServerURL(SCIM2_ENDPOINT, true, true);
        }
        return scimURL;
    }

    private boolean isNotASuperTenantFlow(String tenantDomain) {
        return !MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equals(tenantDomain);
    }
}
