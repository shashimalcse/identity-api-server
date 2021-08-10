/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.identity.api.server.secret.management.v1.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.wso2.carbon.identity.api.server.common.ContextLoader;
import org.wso2.carbon.identity.api.server.secret.management.v1.SecretsApiService;
import org.wso2.carbon.identity.api.server.secret.management.v1.core.SecretManagementService;
import org.wso2.carbon.identity.api.server.secret.management.v1.model.Secret;
import org.wso2.carbon.identity.api.server.secret.management.v1.model.SecretAdd;
import org.wso2.carbon.identity.api.server.secret.management.v1.model.SecretUpdateRequest;

import java.net.URI;
import javax.ws.rs.core.Response;

import static org.wso2.carbon.identity.api.server.secret.management.common.SecretManagementConstants.SECRET_CONTEXT_PATH;
import static org.wso2.carbon.identity.api.server.secret.management.common.SecretManagementConstants.V1_API_PATH_COMPONENT;

/**
 * Implementation of Secret Management REST API.
 */
public class SecretsApiServiceImpl implements SecretsApiService {

    @Autowired
    private SecretManagementService secretManagementService;

    @Override
    public Response createSecret(String secretType, SecretAdd secretAdd) {

        Secret secret = secretManagementService.addSecret(secretType, secretAdd);
        URI location = ContextLoader.buildURIForHeader(V1_API_PATH_COMPONENT + SECRET_CONTEXT_PATH + "/"
                + secret.getSecretName());
        return Response.created(location).entity(secret).build();

    }

    @Override
    public Response deleteSecret(String secretType, String name) {

        secretManagementService.deleteSecret(secretType, name);
        return Response.noContent().build();
    }

    @Override
    public Response getSecret(String secretType, String name) {

        return Response.ok().entity(secretManagementService.getSecret(secretType, name)).build();
    }

    @Override
    public Response getSecretsList(String secretType) {

        return Response.ok().entity(secretManagementService.getSecretsList(secretType)).build();
    }

    @Override
    public Response updateSecret(String secretType, String name, SecretUpdateRequest secretUpdateRequest) {

        return Response.ok()
                .entity(secretManagementService.updateSecret(secretType, name, secretUpdateRequest)).build();
    }
}
