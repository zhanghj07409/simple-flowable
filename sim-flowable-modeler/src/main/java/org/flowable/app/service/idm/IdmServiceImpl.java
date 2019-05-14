package org.flowable.app.service.idm;

import org.flowable.app.model.common.RemoteGroup;
import org.flowable.app.model.common.RemoteUser;
import com.flowable.extention.task.feign.FeignRemoteIdmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: cyp
 * Date: 2018-03-26
 * Time: 18:03
 */
@Component
public class IdmServiceImpl {

    @Autowired
    private FeignRemoteIdmService feignRemoteIdmService;

    /**
     * 根据用户ID获取用户基本信息
     * @param userId
     * @return
     */
    public RemoteUser getRemoteUser(String userId){
        List<Map<String,String>> sysUserEntityList = feignRemoteIdmService.getAllUser();
        RemoteUser remoteUser = null;
        for(Map<String,String> map : sysUserEntityList){
            if(map.get("userId").equals(userId)){
                remoteUser = new RemoteUser();
                remoteUser.setId(map.get("userId"));
                remoteUser.setFirstName(map.get("userName"));
                remoteUser.setLastName("");
                remoteUser.setFullName(map.get("userName"));
                remoteUser.setEmail(map.get("email"));
                break;
            }
        }
        if(remoteUser != null){
            remoteUser.setGroups(this.getGroupsByUserId(userId));
        }
        return remoteUser;
    }

    /**
     * 根据用户组ID获取用户组信息
     * @param groupId
     * @return
     */
    public RemoteGroup getGroup(String groupId){
        RemoteGroup remoteGroup = null;
        List<Map<String,String>> roleList = feignRemoteIdmService.getAllRole();
        for(Map<String,String> map : roleList){
            if(groupId.equals(map.get("roleCode"))){
                remoteGroup = new RemoteGroup();
                remoteGroup.setId(map.get("roleCode"));
                remoteGroup.setName(map.get("roleName"));
            }
        }
        return remoteGroup;
    }

    /**
     * 根据用户组Id获取组下所有用户信息
     * @param groupId
     * @return
     */
    public List<RemoteUser> getUsersByGroupId(String groupId){
        Map<String,List<Map<String,String>>> listMap = feignRemoteIdmService.getAllRoleUser();
        List<RemoteUser> remoteUserList = new ArrayList<>();
        for(Map.Entry<String,List<Map<String,String>>> entry : listMap.entrySet()){
            String key = entry.getKey();
            if(groupId.equals(key)){
                List<Map<String,String>> list = entry.getValue();
                for(Map<String,String> map : list){
                    RemoteUser remoteUser = this.getRemoteUser(map.get("userId"));
                    remoteUserList.add(remoteUser);
                }
            }
        }
        return remoteUserList;
    }

    /**
     * 根据用户名称模糊查询所有用户信息
     * @param userName
     * @param maxSize
     * @return
     */
    public List<RemoteUser> findUsersByNameFilter(String userName,int maxSize){
        List<Map<String,String>> retUserList = this.getUserIdsByFilter(userName,maxSize);
        List<RemoteUser> remoteUserList = new ArrayList<>();
        if(retUserList != null){
            for(Map<String,String> map : retUserList){
                RemoteUser remoteUser = this.getRemoteUser(map.get("userId"));
                remoteUserList.add(remoteUser);
            }
        }
        return remoteUserList;
    }

    /**
     * 根据组名称模糊查询组信息
     * @param groupName
     * @param maxSize
     * @return
     */
    public List<RemoteGroup> getGroupsByNameFilter(String groupName,int maxSize){
        List<Map<String,String>> listMap = feignRemoteIdmService.getAllRole();
        List<Map<String,String>> subList = null;
        if(groupName == null || groupName.equals("")){
            subList = listMap.subList(0,listMap.size()>maxSize?maxSize:listMap.size());
        } else {
            if(subList == null){
                subList = new ArrayList<>();
                for(Map<String,String> map : listMap){
                    if(map.get("roleName").indexOf(groupName) > -1){
                        subList.add(map);
                    }
                }
            }
        }
        List<RemoteGroup> remoteGroupList = this.mapConvertRemoteGroup(subList);
        return remoteGroupList;
    }

    private List<RemoteGroup> mapConvertRemoteGroup(List<Map<String,String>> subList){
        List<RemoteGroup> remoteGroupList = new ArrayList<>();
        for(Map<String,String> map : subList){
            RemoteGroup remoteGroup = new RemoteGroup();
            remoteGroup.setId(map.get("roleCode"));
            remoteGroup.setName(map.get("roleName"));
            remoteGroupList.add(remoteGroup);
        }
        return remoteGroupList;
    }

    private List<Map<String,String>> getUserIdsByFilter(String userName,int maxSize){
        List<Map<String,String>> sysUserEntityList = feignRemoteIdmService.getAllUser();
        if(userName == null || userName.equals("")){
            return sysUserEntityList.subList(0,sysUserEntityList.size()>maxSize?maxSize:sysUserEntityList.size());
        } else {
            List<Map<String,String>> retUserList = new ArrayList<>();
            for(Map<String,String> map : sysUserEntityList){
                if(map.get("userName").indexOf(userName) > -1){
                    retUserList.add(map);
                }
            }
            return retUserList.subList(0,retUserList.size()>maxSize?maxSize:retUserList.size());
        }
    }

    private List<RemoteGroup> getGroupsByUserId(String userId){
        Map<String,List<Map<String,String>>> listMap = feignRemoteIdmService.getAllRoleUser();
        List<RemoteGroup> remoteGroupList = new ArrayList<>();
        for(Map.Entry<String,List<Map<String,String>>> entry : listMap.entrySet()){
            List<Map<String,String>> list = entry.getValue();
            Iterator<Map<String,String>> iterator = list.iterator();
            while (iterator.hasNext()){
                Map<String,String> map = iterator.next();
                if(userId.equals(map.get("userId"))){
                    RemoteGroup remoteGroup = new RemoteGroup();
                    remoteGroup.setId(map.get("roleCode"));
                    remoteGroup.setName(this.getRoleNameByRoleCode(map.get("roleCode")));
                    remoteGroupList.add(remoteGroup);
                }
            }
        }
        return remoteGroupList;
    }

    private String getRoleNameByRoleCode(String roleCode){
        List<Map<String,String>> roleList = feignRemoteIdmService.getAllRole();
        for(Map<String,String> map : roleList){
            if(roleCode.equals(map.get("roleCode"))){
                return map.get("roleName");
            }
        }
        return "";
    }
}
