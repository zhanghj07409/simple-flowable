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

import org.flowable.app.model.runtime.TaskRepresentation;
import org.flowable.app.model.runtime.TaskUpdateRepresentation;
import org.flowable.app.service.runtime.FlowableTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController("taskResourceController")
public class TaskResource {

    @Autowired
    protected FlowableTaskService taskService;

    @RequestMapping(value = "/rest/tasks/{taskId}", method = RequestMethod.GET, produces = "application/json")
    public TaskRepresentation getTask(@PathVariable String taskId, HttpServletResponse response) {
        return taskService.getTask(taskId, response);
    }

    @RequestMapping(value = "/rest/tasks/{taskId}", method = RequestMethod.PUT, produces = "application/json")
    public TaskRepresentation updateTask(@PathVariable("taskId") String taskId, @RequestBody TaskUpdateRepresentation updated) {
        return taskService.updateTask(taskId, updated);
    }

}
