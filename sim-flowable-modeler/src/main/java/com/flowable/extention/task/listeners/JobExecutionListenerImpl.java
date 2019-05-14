package com.flowable.extention.task.listeners;

import com.flowable.extention.task.util.Globals;
import com.flowable.extention.task.feign.FeignExecutionListenerService;
import org.flowable.bpmn.model.*;
import org.flowable.engine.HistoryService;
import org.flowable.engine.common.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
public class JobExecutionListenerImpl implements ExecutionListener {

    private Expression fieldName;

    private Expression fieldParam;

    @Override
    public void notify(DelegateExecution execution) {
        FeignExecutionListenerService feignExecutionListenerService = (FeignExecutionListenerService) Globals.getBean("com.flowable.extention.task.feign.FeignExecutionListenerService");

        String fieldvalue = null;
        if(fieldParam!=null){
            fieldvalue = fieldParam.getExpressionText();
            fieldParam=null;
        }

        Object beanName = null;
        if(fieldName!=null){
            beanName = fieldName.getExpressionText();
            fieldName=null;
        }else {
            return;
        }

        Map<String,Object> variables = execution.getVariables();
        //添加执行ID，后置事件可进行动态添加流程变量
        variables.put("executionId",execution.getParentId());
        variables.put("eventName",execution.getEventName());
        FlowElement flowElement = execution.getCurrentFlowElement();
        variables.put("urid",execution.getProcessInstanceBusinessKey());

        Map<String,Object> timeInfoMap = this.getTimeInfo(((SequenceFlow)execution.getCurrentFlowElement()).getSourceFlowElement(),execution.getProcessInstanceId(),execution.getId());
        variables.putAll(timeInfoMap);

        if(flowElement instanceof SequenceFlow){
            variables.put("nodeName",((SequenceFlow)flowElement).getSourceFlowElement().getName());
            variables.put("condition",((SequenceFlow)flowElement).getConditionExpression());
        }
        //扩展使用字段，每一次使用都会默认覆盖前一次保存的值
        Object extendVariable = variables.get("extendVariable");
        if(extendVariable != null && extendVariable instanceof Map){
            variables.putAll((Map<String, Object>) extendVariable);
        }
        Map<String,Object> requestData = new HashMap<>();
        requestData.put("beanName", beanName.toString());
        requestData.put("variables",variables);
        Map<String,Object> result = feignExecutionListenerService.executionListenerAfter(requestData);
        if(result != null){
            for(String name : result.keySet()){
                execution.setVariable(name, result.get(name));
            }
        }

    }


    /**
     * 获取前一节点一些属性信息
     * @param flowElement
     * @param processInstanceId
     * @param executionId
     * @return
     */
    private Map<String,Object> getTimeInfo(FlowElement flowElement,String processInstanceId, String executionId){
        HistoryService historyService = (HistoryService) Globals.getBean("historyService");

        Map<String,Object> map = new HashMap();
        String taskDefinitionKey = flowElement.getId();
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).executionId(executionId).taskDefinitionKey(taskDefinitionKey).list();
        if(list != null && list.size() > 0){
            Date startTime = this.getMinDate(list);
            map.put("startTime",startTime==null?null:new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime));
        }
        if(flowElement instanceof UserTask){
            List<BoundaryEvent> events = ((UserTask) flowElement).getBoundaryEvents();
            if(events != null && events.size() > 0) {
                for (BoundaryEvent boundaryEvent : events) {
                    List<EventDefinition> eventDefinitions = boundaryEvent.getEventDefinitions();
                    if(eventDefinitions != null && eventDefinitions.size() > 0) {
                        for (EventDefinition eventDefinition : eventDefinitions) {
                            if (eventDefinition instanceof TimerEventDefinition) {
                                String timeDuration = ((TimerEventDefinition) eventDefinition).getTimeDuration();
                                map.put("timeDuration", timeDuration);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return map;
    }

    private Date getMinDate(List<HistoricTaskInstance> list){
        HistoricTaskInstance historicTaskInstance = null;
        if(list != null && list.size() > 0){
            historicTaskInstance = Collections.min(list, new Comparator<HistoricTaskInstance>() {
                @Override
                public int compare(HistoricTaskInstance o1, HistoricTaskInstance o2) {
                    return o1.getStartTime().compareTo(o2.getStartTime());
                }
            });
        }
        return historicTaskInstance.getStartTime();
    }

}
