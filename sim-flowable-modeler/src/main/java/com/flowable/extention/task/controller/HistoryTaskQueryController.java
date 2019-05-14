package com.flowable.extention.task.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.flowable.app.security.SecurityUtils;
import org.flowable.app.service.exception.NotFoundException;
import com.flowable.extention.task.query.HistoryActTaskInstanceQuery;
import com.flowable.extention.task.service.HistoricProcessInstanceService;
import com.flowable.extention.task.service.HistoryActTaskService;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
@Controller
@RequestMapping("/workflow")
public class HistoryTaskQueryController {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoricProcessInstanceService historicProcessInstanceService;

    @Autowired
    private HistoryActTaskService historyActTaskService;

    @ResponseBody
    @RequestMapping("/query/hisTask.do")
    public Map queryHisTask(@RequestParam("processInstanceId") String processInstanceId,
                            @RequestParam("processDefinitionId") String processDefinitionId,
                             @RequestParam("page") String page,
                             @RequestParam("rows") String rows){
        if(!StringUtils.hasText(page)){
            page = "1";
        }
        if(!StringUtils.hasText(rows)){
            rows = "50";
        }
        Integer limitStart = (Integer.valueOf(page) - 1) * Integer.valueOf(rows);
        Integer limitRows = Integer.valueOf(rows);

        String userId = null;
        if(processDefinitionId!=null&&processDefinitionId.split(":")[0].equals("financeandquote")){
            User currentUser = SecurityUtils.getCurrentUserObject();
            if(!StringUtils.hasText(userId)){
                userId = currentUser.getId();
            }
        }
        Long total = Long.valueOf(historyActTaskService.listHistoryActTaskInstanceCount(processInstanceId,userId));
        List<? extends HistoryActTaskInstanceQuery> result = historyActTaskService.listHistoryActTaskInstance(processInstanceId,userId,limitStart,limitRows);
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("total",total);
        resultMap.put("page",page);
        resultMap.put("totalpage",total/limitRows+1);
        resultMap.put("rows",convertIdToName(result,"assignee", "assigneeName"));
        return resultMap;
    }

    /**
     * 查询 我已完成的请求
     * @param userId
     * @param page
     * @param rows
     * @return
     */
    @ResponseBody
    @RequestMapping("/history/process-instance/listcompleted.do")
    public Map listCompletedProcessInstance(@RequestParam(value = "userId", required = false) String userId,
                                         @RequestParam(value = "page", required = false) String page,
                                         @RequestParam(value = "rows", required = false) String rows){
        if(!StringUtils.hasText(page)){
            page = "1";
        }
        if(!StringUtils.hasText(rows)){
            rows = "50";
        }
        Integer limitStart = (Integer.valueOf(page) - 1) * Integer.valueOf(rows);
        Integer limitRows = Integer.valueOf(rows);
        User currentUser = SecurityUtils.getCurrentUserObject();
        if(!StringUtils.hasText(userId)){
            userId = currentUser.getId();
        }
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        historicProcessInstanceQuery.startedBy(userId).finished().orderByProcessInstanceEndTime().desc();
        Long total = historicProcessInstanceQuery.count();

        List<HistoricProcessInstance> result = historicProcessInstanceQuery.listPage(limitStart,limitRows);
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("total",total);
        resultMap.put("page",page);
        resultMap.put("totalpage",total/limitRows+1);
        resultMap.put("rows",convertIdToName(result,"startUserId", "startUserName"));
        return resultMap;
    }

    /**
     * 查询 我未完成的请求
     * @param userId
     * @param page
     * @param rows
     * @return
     */
    @ResponseBody
    @RequestMapping("/history/process-instance/listuncompleted.do")
    public Map listUncompletedProcessInstance(@RequestParam(value = "userId", required = false) String userId,
                                     @RequestParam(value = "page", required = false) String page,
                                     @RequestParam(value = "rows", required = false) String rows){
        if(!StringUtils.hasText(page)){
            page = "1";
        }
        if(!StringUtils.hasText(rows)){
            rows = "50";
        }
        Integer limitStart = (Integer.valueOf(page) - 1) * Integer.valueOf(rows);
        Integer limitRows = Integer.valueOf(rows);
        User currentUser = SecurityUtils.getCurrentUserObject();
        if(!StringUtils.hasText(userId)){
            userId = currentUser.getId();
        }
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        historicProcessInstanceQuery.startedBy(userId).unfinished().orderByProcessInstanceStartTime().desc();
        Long total = historicProcessInstanceQuery.count();

        List<HistoricProcessInstance> result = historicProcessInstanceQuery.listPage(limitStart,limitRows);
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("total",total);
        resultMap.put("page",page);
        resultMap.put("totalpage",total/limitRows+1);
        resultMap.put("rows",convertIdToName(result,"startUserId", "startUserName"));
        return resultMap;
    }

    /**
     * 查询 已办任务
     * @param userId
     * @param page
     * @param rows
     * @return
     */
    @ResponseBody
    @RequestMapping("/history/process-instance/listfinished.do")
    public Map listTaskFinished(@RequestParam(value = "userId", required = false) String userId,
                                                        @RequestParam(value = "page", required = false) String page,
                                                        @RequestParam(value = "rows", required = false) String rows){
        if(!StringUtils.hasText(page)){
            page = "1";
        }
        if(!StringUtils.hasText(rows)){
            rows = "50";
        }
        Integer limitStart = (Integer.valueOf(page) - 1) * Integer.valueOf(rows);
        Integer limitRows = Integer.valueOf(rows);
        User currentUser = SecurityUtils.getCurrentUserObject();
        if(!StringUtils.hasText(userId)){
            userId = currentUser.getId();
        }
        if(!StringUtils.hasText(userId)){
            throw new NotFoundException("当前用户未登录，请登录后重试");
        }
        Long total = Long.valueOf(historicProcessInstanceService.listProcessInstanceByTaskAssignCount(userId));
        List<? extends HistoricProcessInstance> result = historicProcessInstanceService.listProcessInstanceByTaskAssignAndFinished(userId,limitStart,limitRows);
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("total",total);
        resultMap.put("page",page);
        resultMap.put("totalpage",total/limitRows+1);
        resultMap.put("rows",convertIdToName(result,"startUserId", "startUserName"));
        return resultMap;
    }

    /**
     * 根据businesskey查询任务信息
     * @param businesskey
     * @param page
     * @param rows
     * @return
     */
    @ResponseBody
    @RequestMapping("/history/process-instance/listtaskhiprocinst.do")
    public JSONObject listTaskHiProcinst(@RequestParam(value = "userId", required = false) String userId,
                                         @RequestParam(value = "businesskey", required = false) String businesskey,
                                @RequestParam(value = "page", required = false) String page,
                                @RequestParam(value = "rows", required = false) String rows){
        if(!StringUtils.hasText(page)){
            page = "1";
        }
        if(!StringUtils.hasText(rows)){
            rows = "1";
        }
        Integer limitStart = (Integer.valueOf(page) - 1) * Integer.valueOf(rows);
        Integer limitRows = Integer.valueOf(rows);
        User currentUser = SecurityUtils.getCurrentUserObject();
        if(!StringUtils.hasText(userId)){
            userId = currentUser.getId();
        }
        if(!StringUtils.hasText(userId)){
            throw new NotFoundException("当前用户未登录，请登录后重试");
        }
        int total=1;
        List<? extends HistoricProcessInstance> result = historicProcessInstanceService.listProcessInstanceByBusinesskey(businesskey,limitStart,limitRows);
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("page",page);
        jsonObject.put("total",total);
        jsonObject.put("totalpage",total/limitRows+1);
        jsonObject.put("rows",JSONObject.parseArray(JSONObject.toJSONString(result)));

        return jsonObject;
    }

    /**
     * 根据流程定义ID和开始节点获取第一个任务节点
     * @param processDefinitionId
     * @param startActivityId
     * @return
     */
    @ResponseBody
    @RequestMapping("/get/form-key.do")
    public Map getFirstUserTaskFormKey(@RequestParam("processDefinitionId") String processDefinitionId,
                                       @RequestParam("startActivityId") String startActivityId){
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        List<Process> processes = bpmnModel.getProcesses();
        FlowElement flowElement = null;
        for(Process process : processes){
            flowElement = process.getFlowElement(startActivityId);
            if(flowElement != null){
                break;
            }
        }
        List<FlowElement> comparedList = new ArrayList<>();
        comparedList.add(flowElement);
        String formKey = this.getFormKey(processes, comparedList);
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("formKey", formKey);
        return resultMap;
    }

    public String getFormKey(List<Process> processes, List<FlowElement> comparedList){
        List<FlowElement> result = this.getProcessElement(processes,comparedList);
        String formKey = null;
        for(FlowElement flowElement : result){
            if(flowElement instanceof UserTask && StringUtils.hasText(((UserTask)flowElement).getFormKey())){
                formKey = ((UserTask)flowElement).getFormKey();
                break;
            }
        }
        //循环查找第一个用户任务节点（并且比较的集合不能为空）
        if(!StringUtils.hasText(formKey) && result != null && result.size() > 0){
            this.getFormKey(processes, result);
        }
        return formKey;
    }

    private List<FlowElement> getProcessElement(List<Process> processes, List<FlowElement> comparedList){
        List<FlowElement> result = new ArrayList<>();
        for(Process process : processes) {
            Collection<FlowElement> flowElements = process.getFlowElements();
            for (FlowElement flowElement : flowElements) {
                for(FlowElement comparedElement : comparedList) {
                    if (flowElement instanceof SequenceFlow && ((SequenceFlow) flowElement).getSourceFlowElement().getId().equals(comparedElement.getId())) {
                        result.add(((SequenceFlow) flowElement).getTargetFlowElement());
                    }
                    if (flowElement instanceof SubProcess) {
                        this.getSubProcessElement((SubProcess) flowElement, result, comparedElement.getId());
                    }
                }
            }
        }
        return result;
    }

    private void getSubProcessElement(SubProcess subProcess, List<FlowElement> result, String startActivityId){
        Collection<FlowElement> flowElements = subProcess.getFlowElements();
        for(FlowElement flowElement : flowElements){
            if(flowElement instanceof SequenceFlow && ((SequenceFlow)flowElement).getSourceFlowElement().getId().equals(startActivityId)){
                result.add(((SequenceFlow)flowElement).getTargetFlowElement());
            }
            if(flowElement instanceof SubProcess){
                this.getSubProcessElement((SubProcess)flowElement,result,startActivityId);
            }
        }
    }



    /**
     * 翻译userId对应的userName
     * @param result
     * @param userId
     * @param userName
     * @return
     */
    private JSONArray convertIdToName(List<? extends Object> result, String userId, String userName){
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(result, SerializerFeature.WriteDateUseDateFormat));
        for(int i=0;i<jsonArray.size();i++){
            ((JSONObject)jsonArray.get(i)).put(userName, userId);
            String activityType = (String) ((JSONObject)jsonArray.get(i)).get("activityType");
            if("startEvent".equals(activityType)){
                ((JSONObject)jsonArray.get(i)).put("activityName", "发起流程");
            }else if ("endEvent".equals(activityType)){
                ((JSONObject)jsonArray.get(i)).put("activityName", "流程结束");
            }
        }
        return jsonArray;
    }
}
