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

import org.flowable.app.model.ResultListGridDataEntity;
import org.flowable.app.model.runtime.ContentItemRepresentation;
import org.flowable.app.service.exception.InternalServerErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Frederik Heremans
 */
@RestController
public class RelatedContentResource extends AbstractRelatedContentResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelatedContentResource.class);

    protected ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(value = "/rest/tasks/{taskId}/content", method = RequestMethod.GET)
    public ResultListGridDataEntity getContentItemsForTask(@PathVariable("taskId") String taskId) {
        return super.getContentItemsForTask(taskId);
    }

    @RequestMapping(value = "/rest/process-instances/{processInstanceId}/content", method = RequestMethod.GET)
    public ResultListGridDataEntity getContentItemsForProcessInstance(@PathVariable("processInstanceId") String processInstanceId) {
        return super.getContentItemsForProcessInstance(processInstanceId);
    }

    @RequestMapping(value = "/rest/tasks/{taskId}/raw-content", method = RequestMethod.POST)
    public ContentItemRepresentation createContentItemOnTask(@PathVariable("taskId") String taskId, @RequestParam("file") MultipartFile file) {
        return super.createContentItemOnTask(taskId, file);
    }

    /*
     * specific endpoint for IE9 flash upload component
     */
    @RequestMapping(value = "/rest/tasks/{taskId}/raw-content/text", method = RequestMethod.POST)
    public String createContentItemOnTaskText(@PathVariable("taskId") String taskId, @RequestParam("file") MultipartFile file) {
        ContentItemRepresentation contentItem = super.createContentItemOnTask(taskId, file);
        String contentItemJson = null;
        try {
            contentItemJson = objectMapper.writeValueAsString(contentItem);
        } catch (Exception e) {
            LOGGER.error("Error while processing ContentItem representation json", e);
            throw new InternalServerErrorException("ContentItem on task could not be saved");
        }

        return contentItemJson;
    }

    @RequestMapping(value = "/rest/tasks/{taskId}/content", method = RequestMethod.POST)
    public ContentItemRepresentation createContentItemOnTask(@PathVariable("taskId") String taskId, @RequestBody ContentItemRepresentation contentItem) {
        return super.createContentItemOnTask(taskId, contentItem);
    }

    @RequestMapping(value = "/rest/processes/{processInstanceId}/content", method = RequestMethod.POST)
    public ContentItemRepresentation createContentItemOnProcessInstance(@PathVariable("processInstanceId") String processInstanceId, @RequestBody ContentItemRepresentation contentItem) {
        return super.createContentItemOnProcessInstance(processInstanceId, contentItem);
    }

    @RequestMapping(value = "/rest/process-instances/{processInstanceId}/raw-content", method = RequestMethod.POST)
    public ContentItemRepresentation createContentItemOnProcessInstance(@PathVariable("processInstanceId") String processInstanceId, @RequestParam("file") MultipartFile file) {
        return super.createContentItemOnProcessInstance(processInstanceId, file);
    }

    /*
     * specific endpoint for IE9 flash upload component
     */
    @RequestMapping(value = "/rest/process-instances/{processInstanceId}/raw-content/text", method = RequestMethod.POST)
    public String createContentItemOnProcessInstanceText(@PathVariable("processInstanceId") String processInstanceId, @RequestParam("file") MultipartFile file) {
        ContentItemRepresentation contentItem = super.createContentItemOnProcessInstance(processInstanceId, file);

        String contentItemJson = null;
        try {
            contentItemJson = objectMapper.writeValueAsString(contentItem);
        } catch (Exception e) {
            LOGGER.error("Error while processing ContentItem representation json", e);
            throw new InternalServerErrorException("ContentItem on process instance could not be saved");
        }

        return contentItemJson;
    }

    @RequestMapping(value = "/rest/content/raw", method = RequestMethod.POST)
    public ContentItemRepresentation createTemporaryRawContentItem(@RequestParam("file") MultipartFile file) {
        return super.createTemporaryRawContentItem(file);
    }

    /*
     * specific endpoint for IE9 flash upload component
     */
    @RequestMapping(value = "/rest/content/raw/text", method = RequestMethod.POST)
    public String createTemporaryRawContentItemText(@RequestParam("file") MultipartFile file) {
        ContentItemRepresentation contentItem = super.createTemporaryRawContentItem(file);
        String contentItemJson = null;
        try {
            contentItemJson = objectMapper.writeValueAsString(contentItem);
        } catch (Exception e) {
            LOGGER.error("Error while processing ContentItem representation json", e);
            throw new InternalServerErrorException("ContentItem could not be saved");
        }

        return contentItemJson;
    }

    @RequestMapping(value = "/rest/content", method = RequestMethod.POST)
    public ContentItemRepresentation createTemporaryRelatedContent(@RequestBody ContentItemRepresentation contentItem) {
        return addContentItem(contentItem, null, null, false);
    }

    @RequestMapping(value = "/rest/content/{contentId}", method = RequestMethod.DELETE)
    public void deleteContent(@PathVariable("contentId") String contentId, HttpServletResponse response) {
        super.deleteContent(contentId, response);
    }

    @RequestMapping(value = "/rest/content/{contentId}", method = RequestMethod.GET)
    public ContentItemRepresentation getContent(@PathVariable("contentId") String contentId) {
        return super.getContent(contentId);
    }

    @RequestMapping(value = "/rest/content/{contentId}/raw", method = RequestMethod.GET)
    public void getRawContent(@PathVariable("contentId") String contentId, HttpServletResponse response) {
        super.getRawContent(contentId, response);
    }

}
