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
package org.flowable.app.rest.runtime;

import org.flowable.app.security.SecurityUtils;
import org.flowable.app.model.runtime.ProcessInstanceRepresentation;
import org.flowable.app.service.runtime.FlowableProcessInstanceService;
import com.flowable.extention.task.query.HistoryActTaskInstanceQuery;
import com.flowable.extention.task.service.HistoryActTaskService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.form.model.FormModel;
import org.flowable.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing a process instance.
 */
@RestController("processInstanceResourceController")
public class ProcessInstanceResource {

    @Autowired
    protected FlowableProcessInstanceService processInstanceService;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    private HistoryActTaskService historyActTaskService;

    @RequestMapping(value = "/rest/process-instances/{processInstanceId}", method = RequestMethod.GET, produces = "application/json")
    public ProcessInstanceRepresentation getProcessInstance(@PathVariable String processInstanceId, HttpServletResponse response) {
        return processInstanceService.getProcessInstance(processInstanceId, response);
    }

    @RequestMapping(value = "/rest/process-instances/{processInstanceId}/start-form", method = RequestMethod.GET, produces = "application/json")
    public FormModel getProcessInstanceStartForm(@PathVariable String processInstanceId, HttpServletResponse response) {
        return processInstanceService.getProcessInstanceStartForm(processInstanceId, response);
    }

    /**
     * 根据业务主键删除（不需要校验是否有删除权限）
     * @param businessKey
     */
    @RequestMapping(value = "/rest/process-instances/business-key/{businessKey}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteProcessInstanceByBusinessKey(@PathVariable String businessKey,@RequestBody Map<String,Object> params) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
        if(processInstance != null) {
            processInstanceService.deleteProcessInstance(processInstance.getProcessInstanceId(),params);
        }
    }

    /**
     * 根据业务主键删除（需要校验是否有删除权限）
     * @param businessKey
     */
    @RequestMapping(value = "/rest/process-instances/business-key/{businessKey}/strictly", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteProcessInstanceByBusinessKeyStrictly(@PathVariable String businessKey,@RequestBody Map<String,Object> params) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
        if(processInstance != null) {
            processInstanceService.deleteProcessInstanceStrictly(processInstance.getProcessInstanceId(),params);
        }
    }

    /**
     *根据流程步骤判断撤销功能（需要校验是否有删除权限）
     * @param businessKey
     */
    @RequestMapping(value = "/rest/process-instances/business-key/{businessKey}/operate", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public String deleteProcessInstanceByBusinessKeyOperateStrictly(@PathVariable String businessKey,@RequestBody Map<String,Object> params) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
        User currentUser = SecurityUtils.getCurrentUserObject();
        String  userId = currentUser.getId();
        List<? extends HistoryActTaskInstanceQuery> result = null;
        if(processInstance != null) {
            result = historyActTaskService.listHistoryActTaskInstance(processInstance.getProcessInstanceId(),userId,null,null);
        }
        //过滤掉经办数据
        if(!CollectionUtils.isEmpty(result)){
            for(int i=0;i<result.size();i++){
                if("startEvent".equals(result.get(i).getActivityType())){
                    result.remove(result.get(i));
                }
            }
        }

        if(!CollectionUtils.isEmpty(result)){
                return "无法撤销！";
            }
            else{
                processInstanceService.deleteProcessInstanceStrictly(processInstance.getProcessInstanceId(),params);
                return "操作成功！";
        }
    }


    @RequestMapping(value = "/rest/process-instances/variables/add", method = {RequestMethod.GET,RequestMethod.POST})
    public void addVariablesByExecutionId(@RequestParam("executionId") String executionId, @RequestBody Map<String,Object> variable){
        processInstanceService.addVariablesByExecutionId(executionId,variable);
    }

}
