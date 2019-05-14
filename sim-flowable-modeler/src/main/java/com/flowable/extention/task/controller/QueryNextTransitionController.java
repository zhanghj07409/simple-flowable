package com.flowable.extention.task.controller;

import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
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
public class QueryNextTransitionController {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private CmmnTaskService cmmnTaskService;

    /**
     * 只有有两个以上的输出线路时进行输出
     * @param taskId
     * @return
     */
    @ResponseBody
    @RequestMapping("/query/transitions.do")
    public Map queryTransitions(@RequestParam("taskId") String taskId){
        Map<String,Object> retMap = null;
        //获取流程定义
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        retMap = this.normalButtonValue(task);

        return retMap;
    }

    /**
     * 查询当前操作人可操作按钮
     * @param task
     * @return
     */
    private Map<String,Object> normalButtonValue(Task task){
        Map<String,Object> retMap = new HashMap<>();
        String taskDefinitionKey = task.getTaskDefinitionKey();
        List<SequenceFlow> transitionNames = queryOutGoingFlows(task.getProcessDefinitionId(),taskDefinitionKey);
        this.sort(transitionNames);
        if(transitionNames == null || transitionNames.size() == 0){
            SequenceFlow sequenceFlow = new SequenceFlow();
            sequenceFlow.setName("同意");
            sequenceFlow.setConditionExpression("");
            transitionNames.add(sequenceFlow);
        }
        retMap.put("buttonType", "normal");
        retMap.put("buttonValue", transitionNames);
        return retMap;
    }

    /**
     * 查询当前操作人接收任务按钮
     * @return
     */
    private Map<String,Object> claimButtonValue(){
        Map<String,Object> retMap = new HashMap<>();
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setName("接收");
        List<SequenceFlow> transitionNames = new ArrayList<>();
        transitionNames.add(sequenceFlow);
        retMap.put("buttonType", "claim");
        retMap.put("buttonValue", transitionNames);
        return retMap;
    }

    /**
     * 大于一条分支时查出所有分支
     * @param taskDefinitionKey
     * @return
     */
    private List<SequenceFlow> queryOutGoingFlows(String processDefinitionId,String taskDefinitionKey){
        List<SequenceFlow> transitionNames = new ArrayList<SequenceFlow>();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        List<Process> processes = bpmnModel.getProcesses();
        for(Process process : processes) {
            Collection<FlowElement> flowElements = process.getFlowElements();
            for (FlowElement flowElement : flowElements) {
                if (flowElement instanceof UserTask) {
                    UserTask userTask = (UserTask) flowElement;
                    if (taskDefinitionKey.equals(userTask.getId())) {
                        List<SequenceFlow> outGoingFlows = userTask.getOutgoingFlows();
                        if (outGoingFlows != null && outGoingFlows.size() > 0) {
                            transitionNames.addAll(outGoingFlows);
                        }
                    }
                }
                if(flowElement instanceof SubProcess){
                    transitionNames.addAll(getSubProcessFlows((SubProcess)flowElement,taskDefinitionKey));
                }
            }
        }
        return transitionNames;
    }

    private List<SequenceFlow> getSubProcessFlows(SubProcess subProcess, String taskDefinitionKey){
        List<SequenceFlow> transitionNames = new ArrayList<SequenceFlow>();
        Collection<FlowElement> flowElements = subProcess.getFlowElements();
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                if (taskDefinitionKey.equals(userTask.getId())) {
                    List<SequenceFlow> outGoingFlows = userTask.getOutgoingFlows();
                    if (outGoingFlows != null && outGoingFlows.size() > 0) {
                        transitionNames.addAll(outGoingFlows);
                    }
                }
            }
            if(flowElement instanceof SubProcess){
                SubProcess process = (SubProcess) flowElement;
                transitionNames.addAll(getSubProcessFlows(process, taskDefinitionKey));
            }
        }
        return transitionNames;
    }

    /**
     * 根据备注documentation字段进行升序排列，对前台按钮进行排序显示
     * @param sequenceFlows
     */
    private void sort(List<SequenceFlow> sequenceFlows){
        Collections.sort(sequenceFlows, new Comparator<SequenceFlow>() {
            @Override
            public int compare(SequenceFlow o1, SequenceFlow o2) {
                if(!StringUtils.hasText(o1.getDocumentation()) && !StringUtils.hasText(o2.getDocumentation())){
                    return 0;
                }
                if(StringUtils.hasText(o1.getDocumentation()) && !StringUtils.hasText(o2.getDocumentation())){
                    return 1;
                }
                if(!StringUtils.hasText(o1.getDocumentation()) && StringUtils.hasText(o2.getDocumentation())){
                    return -1;
                }
                return o1.getDocumentation().compareTo(o2.getDocumentation());
            }
        });
    }
}
