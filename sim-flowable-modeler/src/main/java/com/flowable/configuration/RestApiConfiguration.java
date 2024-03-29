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
package com.flowable.configuration;

import org.flowable.rest.application.ContentTypeResolver;
import org.flowable.rest.application.DefaultContentTypeResolver;
import org.flowable.rest.content.ContentRestResponseFactory;
import org.flowable.rest.dmn.service.api.DmnRestResponseFactory;
import org.flowable.rest.form.FormRestResponseFactory;
import org.flowable.rest.service.api.RestResponseFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestApiConfiguration {

    @Bean()
    public ContentTypeResolver contentTypeResolver() {
        ContentTypeResolver resolver = new DefaultContentTypeResolver();
        return resolver;
    }

    @Bean()
    public RestResponseFactory processResponseFactory() {
        RestResponseFactory restResponseFactory = new RestResponseFactory();
        return restResponseFactory;
    }

    @Bean()
    public DmnRestResponseFactory dmnResponseFactory() {
        DmnRestResponseFactory restResponseFactory = new DmnRestResponseFactory();
        return restResponseFactory;
    }

    @Bean()
    public FormRestResponseFactory formResponseFactory() {
        FormRestResponseFactory restResponseFactory = new FormRestResponseFactory();
        return restResponseFactory;
    }

    @Bean()
    public ContentRestResponseFactory contentResponseFactory() {
        ContentRestResponseFactory restResponseFactory = new ContentRestResponseFactory();
        return restResponseFactory;
    }
}
