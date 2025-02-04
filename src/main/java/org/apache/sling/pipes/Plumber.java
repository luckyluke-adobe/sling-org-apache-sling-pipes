/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.pipes;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.Job;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

import java.io.IOException;
import java.util.Map;

/**
 * Plumber is an osgi service aiming to make pipes available to the sling system, in order to
 */
@ProviderType
public interface Plumber {

    /**
     * Instantiate a pipe from the given resource and returns it
     *
     * @param resource configuration resource
     *
     * @return pipe instantiated from the resource, null otherwise
     */
    Pipe getPipe(Resource resource);

    /**
     * Instantiate a pipe from the given resource and returns it
     *
     * @param resource      configuration resource
     * @param upperBindings already set binding we want to initiate our pipe with
     *
     * @return pipe instantiated from the resource, null otherwise
     */
    Pipe getPipe(Resource resource, PipeBindings upperBindings);

    /**
     * executes in a background thread
     *
     * @param resolver resolver used for registering the execution (id will be checked against the configuration)
     * @param path     path of the pipe to execute
     * @param bindings additional bindings to use when executing
     *
     * @return Job if registered, null otherwise
     */
    Job executeAsync(ResourceResolver resolver, String path, Map<String, Object> bindings);

    /**
     * executes in a background thread
     *
     * @param path     path of the pipe to execute
     * @param bindings additional bindings to use when executing
     *
     * @return Job if registered, null otherwise
     */
    Job executeAsync(String path, Map<String, Object> bindings);

    /**
     * Executes a pipe at a certain path
     *
     * @param resolver resource resolver with which pipe will be executed
     * @param path     path of a valid pipe configuration
     * @param bindings bindings to add to the execution of the pipe, can be null
     * @param writer   output of the pipe
     * @param save     in case that pipe writes anything, wether the plumber should save changes or not
     *
     * @return instance of <code>ExecutionResult</code>
     */
    ExecutionResult execute(ResourceResolver resolver, String path, Map<String, Object> bindings, OutputWriter writer, boolean save);

    /**
     * Executes a given pipe
     *
     * @param resolver resource resolver with which pipe will be executed
     * @param pipe     pipe to execute
     * @param bindings bindings to add to the execution of the pipe, can be null
     * @param writer   output of the pipe
     * @param save     in case that pipe writes anything, wether the plumber should save changes or not
     *
     * @return instance of <code>ExecutionResult</code>
     */
    ExecutionResult execute(ResourceResolver resolver, Pipe pipe, Map<String, Object> bindings, OutputWriter writer, boolean save);

    /**
     * Registers
     *
     * @param type      resource type of the pipe to register
     * @param pipeClass class of the pipe to register
     */
    void registerPipe(String type, Class<? extends BasePipe> pipeClass);

    /**
     * returns wether or not a pipe type is registered
     *
     * @param type resource type tested
     *
     * @return true if the type is registered, false if not
     */
    boolean isTypeRegistered(String type);

    /**
     * status of the pipe
     *
     * @param pipeResource resource corresponding to the pipe
     *
     * @return status of the pipe, can be blank, 'started' or 'finished'
     */
    String getStatus(Resource pipeResource);

    /**
     * Provides a builder helping quickly build and execute a pipe
     *
     * @param resolver resource resolver that will be used for building the pipe
     *
     * @return instance of PipeBuilder
     */
    PipeBuilder newPipe(ResourceResolver resolver);

    /**
     * returns true if the pipe is considered to be running
     *
     * @param pipeResource resource corresponding to the pipe
     *
     * @return true if still running
     */
    boolean isRunning(Resource pipeResource);

    /**
     * Extract pipe bindings from the request
     * @param request from where to extract bindings
     * @param writeAllowed should we consider this execution is about to modify content
     * @return map of bindings
     * @throws IOException in case something turns wrong with an input stream
     */
    Map<String, Object> getBindingsFromRequest(SlingHttpServletRequest request, boolean writeAllowed) throws IOException;

    /**
     * @return service user that has been configured for executing pipes;
     */
    Map<String, Object> getServiceUser();

    /**
     * @param currentResource
     * @return context aware configuration map
     */
    Map getContextAwareConfigurationMap(Resource currentResource);

    /**
     * @param referrer resource from which is made the fetch
     * @param reference reference we are searching a resource for (assuming this is *not* a full path already)
     * @return referenced resource, null otherwise
     */
    @Nullable Resource getReferencedResource(Resource referrer, String reference);

    /**
     * marks a given resource as updated
     * @param resource resource to mark
     */
    void markWithJcrLastModified(@NotNull Pipe pipe, @NotNull Resource resource);

    /*
     * Generates unique pipe path for persistence sake
     */
    String generateUniquePath();
}
