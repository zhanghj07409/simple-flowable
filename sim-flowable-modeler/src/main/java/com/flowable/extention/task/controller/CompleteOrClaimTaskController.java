package com.flowable.extention.task.controller;

import org.flowable.app.service.runtime.FlowableTaskActionService;
import com.flowable.extention.task.vo.ExtendVariableVo;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
@Controller
@RequestMapping("/workflow")
public class CompleteOrClaimTaskController {
    private static final Logger log = LoggerFactory.getLogger(CompleteOrClaimTaskController.class);

    @Autowired
    private FlowableTaskActionService flowableTaskActionService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected HistoryService historyService;

    @ResponseBody
    @RequestMapping("/save/complete/task.do")
    public String completeTask(@RequestBody Map<String,Object> extendVariable){
        Map<String, Object> map = null;
        String taskId = extendVariable.get("taskId").toString();
        Object condition = extendVariable.get("conditionExpression");
        if(condition != null && !"null".equals(condition.toString())){
            String conditionExpression = condition.toString();
            map = dealingWithCondition(conditionExpression);
        }
        extendVariable.remove("taskId");
        extendVariable.remove("conditionExpression");
        //执行成功返回结果
        Map<String,Object> resultMap = null;
        //返回执行成功结果的字段
        List<String> successList = new ArrayList<>();
        successList.add("successCode");
        successList.add("successMessage");
        successList.add("errorCode");
        successList.add("errorMessage");
        String description = (extendVariable.get("description")==null || extendVariable.get("description").toString().equals(""))?null:extendVariable.get("description").toString();
        extendVariable.remove("description");

        try {
            resultMap = flowableTaskActionService.claimAndCompleteTaskReturnSuccessMessage(taskId, map, "extendVariable", extendVariable, successList,description);
        }catch (Exception e){
            return e.getMessage();
        }
        if(resultMap != null) {
            String successCode = resultMap.get(successList.get(0)) == null ? null : resultMap.get(successList.get(0)).toString();
            String successMessage = resultMap.get(successList.get(1)) == null ? null : resultMap.get(successList.get(1)).toString();
            String errorCode = resultMap.get(successList.get(2)) == null ? null : resultMap.get(successList.get(2)).toString();
            String errorMessage = resultMap.get(successList.get(3)) == null ? null : resultMap.get(successList.get(3)).toString();
            if (StringUtils.hasText(successCode) && StringUtils.hasText(successMessage)) {
                return "操作成功！";
            }else if(StringUtils.hasText(errorCode) && StringUtils.hasText(errorMessage)){
                return "操作失败！";
            }
        }
        return "操作成功！";
    }

    @ResponseBody
    @RequestMapping("/save/complete/batchTask.do")
    public String beatchCompleteTask(@RequestBody ExtendVariableVo[] list){
        int success=0;
        int error=0;
        for(ExtendVariableVo vo:list){
            try {
                Map<String,Object> extendVariable=new HashMap<>();
                extendVariable.put("sign",vo.getSign());
                extendVariable.put("keynum",vo.getKeynum());
                extendVariable.put("processInstanceId",vo.getProcessInstanceId());
                extendVariable.put("description",vo.getDescription());
                Map<String, Object> map = null;
                String taskId = vo.getTaskId();
                Object condition = vo.getConditionExpression();
                if(condition != null && !"null".equals(condition.toString())){
                    String conditionExpression = condition.toString();
                    map = dealingWithCondition(conditionExpression);
                }
                //执行成功返回结果
                Map<String,Object> resultMap = null;
                //返回执行成功结果的字段
                List<String> successList = new ArrayList<>();
                successList.add("successCode");
                successList.add("successMessage");
                successList.add("errorCode");
                successList.add("errorMessage");
                String description = (extendVariable.get("description")==null || extendVariable.get("description").toString().equals(""))?null:extendVariable.get("description").toString();
                resultMap = flowableTaskActionService.claimAndCompleteTaskReturnSuccessMessage(taskId, map, "extendVariable", extendVariable, successList,description);
                if(resultMap != null) {
                    String successCode = resultMap.get(successList.get(0)) == null ? null : resultMap.get(successList.get(0)).toString();
                    String successMessage = resultMap.get(successList.get(1)) == null ? null : resultMap.get(successList.get(1)).toString();
                    String errorCode = resultMap.get(successList.get(2)) == null ? null : resultMap.get(successList.get(2)).toString();
                    String errorMessage = resultMap.get(successList.get(3)) == null ? null : resultMap.get(successList.get(3)).toString();
                    if (StringUtils.hasText(successCode) && StringUtils.hasText(successMessage)) {
                        success++;
                    }else if(StringUtils.hasText(errorCode) && StringUtils.hasText(errorMessage)){
                        error++;
                    }
                }else{
                    success++;
                }
            } catch (Exception e) {
                log.error("审批异常",e);
                error++;
            }
        }
        if (error == 0){
            return "一共提交处理:"+list.length+"笔，审批成功:"+success+"笔";
        }else {
            return "一共提交处理:"+list.length+"笔，审批成功:"+success+"笔，失败:"+error+"笔";
        }
    }
    @ResponseBody
    @RequestMapping("/batch/complete/task.do")
    public String completeTaskBatch(@RequestBody Map<String,Object> extendVariable){
        String taskIds = extendVariable.get("taskIds").toString();
        Object condition = extendVariable.get("condition");
        Map<String, Object> map = null;
        if(condition != null && !"null".equals(condition.toString())){
            String conditionExpression = condition.toString();
            map = dealingWithCondition(conditionExpression);
        }
        String[] taskArray = taskIds.split(",");

        //执行成功返回结果
        Map<String,Object> resultMap = null;
        //返回执行成功结果的字段
        List<String> successList = new ArrayList<>();
        successList.add("successCode");
        successList.add("successMessage");
        String description = (extendVariable.get("description")==null || extendVariable.get("description").toString().equals(""))?null:extendVariable.get("description").toString();
        extendVariable.remove("description");

        try{
            for(String taskId : taskArray){
                resultMap = flowableTaskActionService.claimAndCompleteTaskReturnSuccessMessage(taskId,map,"",null, successList,description);
            }
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        if(resultMap != null) {
            String successCode = resultMap.get(successList.get(0)) == null ? null : resultMap.get(successList.get(0)).toString();
            String successMessage = resultMap.get(successList.get(1)) == null ? null : resultMap.get(successList.get(1)).toString();

            if (StringUtils.hasText(successCode) && StringUtils.hasText(successMessage)) {
                return "操作成功！";
            }
        }
        return "操作成功！";
    }

    @Deprecated
    @ResponseBody
    @RequestMapping("/save/claim/task.do")
    public Map claimTask(@RequestParam("taskId")String taskId){
        flowableTaskActionService.claimTask(taskId);
        Map<String,String> map = new HashMap<String,String>();
        map.put("success","true");
        return map;
    }

    private Map dealingWithCondition(String condition){
        Map<String, Object> map = new HashMap<>();
        String[] arr = condition.replaceAll("\\{", "").replaceAll("}", "").replaceAll("\'", "").replaceAll("\"", "").replaceAll("\\$", "").split("==");
        map.put(arr[0], arr[1]);
        return map;
    }

}
