/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.app.service.idm;

import org.flowable.app.model.common.RemoteGroup;
import org.flowable.app.model.common.RemoteUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.app.model.common.RemoteToken;
import org.flowable.app.security.SecurityUtils;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RemoteIdmServiceImpl implements RemoteIdmService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteIdmServiceImpl.class);

    @Autowired
    private IdmIdentityService idmIdentityService;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected IdmServiceImpl idmService;

    private static final int MAX_USER_SIZE = 50;

    @Override
    public RemoteUser authenticateUser(String username, String password) {
        return SecurityUtils.getRemoteUser(username);
    }

    @Override
    public RemoteToken getToken(String tokenValue) {
        Token token = idmIdentityService.createTokenQuery().tokenId(tokenValue).singleResult();
        if (token != null) {
            RemoteToken remoteToken = new RemoteToken();
            remoteToken.setId(token.getId());
            remoteToken.setValue(token.getTokenValue());
            remoteToken.setUserId(token.getUserId());
            return remoteToken;
        }
        return null;
    }

    @Override
    public RemoteUser getUser(String userId) {
        return idmService.getRemoteUser(userId);
    }

    @Override
    public List<RemoteUser> findUsersByNameFilter(String filter) {
        return idmService.findUsersByNameFilter(filter,MAX_USER_SIZE);
    }
    
    @Override
    public List<RemoteUser> findUsersByGroup(String groupId) {
        return idmService.getUsersByGroupId(groupId);
    }
    
    @Override
    public RemoteGroup getGroup(String groupId) {
        return idmService.getGroup(groupId);
    }

    @Override
    public List<RemoteGroup> findGroupsByNameFilter(String filter) {
        return idmService.getGroupsByNameFilter(filter,MAX_USER_SIZE);
    }

}
