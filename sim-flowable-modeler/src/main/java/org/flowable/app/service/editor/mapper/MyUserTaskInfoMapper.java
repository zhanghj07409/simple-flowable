package org.flowable.app.service.editor.mapper;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FormProperty;
import org.flowable.bpmn.model.UserTask;
import org.flowable.editor.language.json.converter.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: cyp
 * Date: 2018-01-23
 * Time: 16:47
 */
public class MyUserTaskInfoMapper extends UserTaskInfoMapper{

    private Boolean isCompleteOrCurrentTask = false;
    private String Assignment;

    @Override
    protected void mapProperties(Object element) {
        UserTask userTask = (UserTask)element;
        if(!isCompleteOrCurrentTask) {
            this.createPropertyNode("Assignee", userTask.getAssignee());
            this.createPropertyNode("Candidate users", userTask.getCandidateUsers());
            this.createPropertyNode("Candidate groups", userTask.getCandidateGroups());
        }else {
            this.createPropertyNode("Assignee", Assignment);
        }
        this.createPropertyNode("Due date", userTask.getDueDate());
        this.createPropertyNode("Form key", userTask.getFormKey());
        this.createPropertyNode("Priority", userTask.getPriority());
        if(CollectionUtils.isNotEmpty(userTask.getFormProperties())) {
            ArrayList formPropertyValues = new ArrayList();

            StringBuilder propertyBuilder;
            for(Iterator var4 = userTask.getFormProperties().iterator(); var4.hasNext(); formPropertyValues.add(propertyBuilder.toString())) {
                FormProperty formProperty = (FormProperty)var4.next();
                propertyBuilder = new StringBuilder();
                if(StringUtils.isNotEmpty(formProperty.getName())) {
                    propertyBuilder.append(formProperty.getName());
                } else {
                    propertyBuilder.append(formProperty.getId());
                }

                if(StringUtils.isNotEmpty(formProperty.getType())) {
                    propertyBuilder.append(" - ");
                    propertyBuilder.append(formProperty.getType());
                }

                if(formProperty.isRequired()) {
                    propertyBuilder.append(" (required)");
                } else {
                    propertyBuilder.append(" (not required)");
                }
            }

            this.createPropertyNode("Form properties", formPropertyValues);
        }

        this.createListenerPropertyNodes("Task listeners", userTask.getTaskListeners());
        this.createListenerPropertyNodes("Execution listeners", userTask.getExecutionListeners());
    }

    public Boolean getCompleteOrCurrentTask() {
        return isCompleteOrCurrentTask;
    }

    public void setCompleteOrCurrentTask(Boolean completeOrCurrentTask) {
        isCompleteOrCurrentTask = completeOrCurrentTask;
    }

    public String getAssignment() {
        return Assignment;
    }

    public void setAssignment(String assignment) {
        Assignment = assignment;
    }
}
