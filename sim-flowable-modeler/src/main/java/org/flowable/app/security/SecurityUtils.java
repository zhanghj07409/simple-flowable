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
package org.flowable.app.security;

import com.sim.common.entity.UserLoginEntity;
import com.sim.common.entity.UserStatusConstant;
import org.flowable.app.model.common.RemoteGroup;
import org.flowable.app.model.common.RemoteUser;
import org.flowable.app.service.idm.RemoteIdmService;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.Privilege;
import org.flowable.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class for Spring Security.
 */
@Component
public final class SecurityUtils {

    private static IdmIdentityService idmIdentityService;

    private static RemoteIdmService remoteIdmService;

    @Autowired
    public void setIdmIdentityService(IdmIdentityService idmIdentityService) {
        SecurityUtils.idmIdentityService = idmIdentityService;
    }

    @Autowired
    public void setRemoteIdmService(RemoteIdmService remoteIdmService) {
        SecurityUtils.remoteIdmService = remoteIdmService;
    }

    private SecurityUtils() {
    }

    /**
     * Get the login of the current user.
     */
    public static String getCurrentUserId() {
        UserLoginEntity userLoginDto = getSysUserLoginDto();
        if(userLoginDto != null){
            return userLoginDto.getUserId();
        }
        return null;
    }

    /**
     * @return the {@link User} object associated with the current logged in user.
     */
    public static User getCurrentUserObject() {
        User user = null;
        UserLoginEntity userLoginDto = getSysUserLoginDto();
        if(userLoginDto != null) {
            user = getRemoteUser(userLoginDto.getUserId());
        }
        return user;
    }

    public static UserLoginEntity getSysUserLoginDto(){
        UserLoginEntity userLoginDto = null;
        try{
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            userLoginDto = (UserLoginEntity)request.getSession().getAttribute(UserStatusConstant.USER_LOGIN_SESSION);
        }catch (Exception e){
            //异常不处理
        }
        return userLoginDto;
    }

    @Deprecated
    public static RemoteUser getOldRemoteUser(String userId){
        User idmUser = idmIdentityService.createUserQuery().userId(userId).singleResult();
        if(idmUser == null){
            return null;
        }
        RemoteUser remoteUser = new RemoteUser(idmUser);
        List<Privilege> userPrivileges = idmIdentityService.createPrivilegeQuery().userId(userId).list();
        Set<String> privilegeNames = new HashSet<>();
        for (Privilege userPrivilege : userPrivileges) {
            privilegeNames.add(userPrivilege.getName());
        }
        List<RemoteGroup> remoteGroups = new ArrayList<>();
        List<Group> groups = idmIdentityService.createGroupQuery().groupMember(userId).list();
        if (groups.size() > 0) {
            List<String> groupIds = new ArrayList<>();
            for (Group group : groups) {
                groupIds.add(group.getId());
                remoteGroups.add(new RemoteGroup(group.getId(),group.getName()));
            }

            List<Privilege> groupPrivileges = idmIdentityService.createPrivilegeQuery().groupIds(groupIds).list();
            for (Privilege groupPrivilege : groupPrivileges) {
                privilegeNames.add(groupPrivilege.getName());
            }
        }
        remoteUser.setGroups(remoteGroups);
        remoteUser.setPrivileges(new ArrayList<String>(privilegeNames));
        return remoteUser;
    }

    public static RemoteUser getRemoteUser(String userId){
        return remoteIdmService.getUser(userId);
    }

}
