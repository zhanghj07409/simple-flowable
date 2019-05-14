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

import org.flowable.app.model.runtime.CaseInstanceRepresentation;
import org.flowable.app.service.runtime.FlowableCaseInstanceService;
import org.flowable.form.model.FormModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * REST controller for managing a case instance.
 */
@RestController
public class CaseInstanceResource {

    @Autowired
    protected FlowableCaseInstanceService caseInstanceService;

    @RequestMapping(value = "/rest/case-instances/{caseInstanceId}", method = RequestMethod.GET, produces = "application/json")
    public CaseInstanceRepresentation getCaseInstance(@PathVariable String caseInstanceId, HttpServletResponse response) {
        return caseInstanceService.getCaseInstance(caseInstanceId, response);
    }

    @RequestMapping(value = "/rest/case-instances/{caseInstanceId}/start-form", method = RequestMethod.GET, produces = "application/json")
    public FormModel getCaseInstanceStartForm(@PathVariable String caseInstanceId, HttpServletResponse response) {
        //return caseInstanceService.getProcessInstanceStartForm(caseInstanceId, response);
        return null;
    }

    @RequestMapping(value = "/rest/case-instances/{caseInstanceId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteCaseInstance(@PathVariable String caseInstanceId) {
        caseInstanceService.deleteCaseInstance(caseInstanceId);
    }

}
