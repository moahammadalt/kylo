package com.thinkbiganalytics.nifi.rest.client;

import org.apache.nifi.web.api.dto.ConnectableDTO;
import org.apache.nifi.web.api.dto.ConnectionDTO;
import org.apache.nifi.web.api.dto.FlowSnippetDTO;
import org.apache.nifi.web.api.dto.PortDTO;
import org.apache.nifi.web.api.dto.ProcessGroupDTO;

import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Exposes the NiFi Process Groups REST endpoint as a Java class.
 */
public interface NiFiProcessGroupsRestClient {

    /**
     * Creates a process group.
     *
     * @param parentProcessGroupId the parent process group id
     * @param name the name for the new process group
     * @return the new process group
     * @throws NifiComponentNotFoundException if the parent process group does not exist
     */
    @Nonnull
    ProcessGroupDTO create(@Nonnull String parentProcessGroupId, @Nonnull String name);

    /**
     * Creates a connection.
     *
     * @param processGroupId the process group id
     * @param source the connection source
     * @param dest the connection destination
     * @return the connection
     * @throws NifiComponentNotFoundException if the process group does not exist
     */
    @Nonnull
    ConnectionDTO createConnection(@Nonnull String processGroupId, @Nonnull ConnectableDTO source, @Nonnull ConnectableDTO dest);

    /**
     * Creates an input port.
     *
     * @param processGroupId the process group id
     * @param inputPort the input port to create
     * @return the new input port
     * @throws NifiComponentNotFoundException if the process group does not exist
     */
    @Nonnull
    PortDTO createInputPort(@Nonnull String processGroupId, @Nonnull PortDTO inputPort);

    /**
     * Creates an output port.
     *
     * @param processGroupId the process group id
     * @param outputPort the output port to create
     * @return the new output port
     * @throws NifiComponentNotFoundException if the process group does not exist
     */
    @Nonnull
    PortDTO createOutputPort(@Nonnull String processGroupId, @Nonnull PortDTO outputPort);

    /**
     * Deletes a process group.
     *
     * @param processGroup the process group to delete
     * @return the deleted process group, if found
     * @throws NifiClientRuntimeException if the operation times out
     */
    @Nonnull
    Optional<ProcessGroupDTO> delete(@Nonnull ProcessGroupDTO processGroup);

    /**
     * Gets all child process groups.
     *
     * @param parentGroupId the parent process group id
     * @return the child process groups
     * @throws NifiComponentNotFoundException if the parent process group does not exist
     */
    @Nonnull
    Set<ProcessGroupDTO> findAll(@Nonnull String parentGroupId);

    /**
     * Gets a process group.
     *
     * @param processGroupId the process group id
     * @param recursive {@code true} to include all encapsulated components, or {@code false} for just the immediate children
     * @param verbose {@code true} to include any encapsulated components, or {@code false} for just details about the process group
     * @return the process group, if found
     */
    @Nonnull
    Optional<ProcessGroupDTO> findById(@Nonnull String processGroupId, boolean recursive, boolean verbose);

    /**
     * Gets the child process group with the specified name, optionally including all sub-components.
     *
     * @param parentGroupId the id of the parent process group
     * @param groupName the name of the process group to find
     * @param recursive {@code true} to include all encapsulated components, or {@code false} for just the immediate children
     * @param verbose {@code true} to include any encapsulated components, or {@code false} for just details about the process group
     * @return the child process group, or {@code null} if not found
     * @throws NifiComponentNotFoundException if the parent process group does not exist
     */
    @Nonnull
    Optional<ProcessGroupDTO> findByName(@Nonnull String parentGroupId, @Nonnull String groupName, boolean recursive, boolean verbose);

    /**
     * Gets the root process group.
     *
     * @return the root process group
     */
    @Nonnull
    ProcessGroupDTO findRoot();

    /**
     * Gets all connections.
     *
     * @param processGroupId the process group id
     * @return all connections within the process group
     * @throws NifiComponentNotFoundException if the process group does not exist
     */
    @Nonnull
    Set<ConnectionDTO> getConnections(@Nonnull String processGroupId);

    /**
     * Gets all input ports.
     *
     * @param processGroupId the process group id
     * @return all input ports within the process group
     * @throws NifiComponentNotFoundException if the process group does not exist
     */
    @Nonnull
    Set<PortDTO> getInputPorts(@Nonnull String processGroupId);

    /**
     * Gets all output ports.
     *
     * @param processGroupId the process group id
     * @return all output ports within the process group
     * @throws NifiComponentNotFoundException if the process group does not exist
     */
    @Nonnull
    Set<PortDTO> getOutputPorts(@Nonnull String processGroupId);

    /**
     * Instantiates a template.
     *
     * @param processGroupId the process group id
     * @param templateId the template id
     * @return the created flow
     * @throws NifiComponentNotFoundException if the process group or template does not exist
     */
    @Nonnull
    FlowSnippetDTO instantiateTemplate(@Nonnull String processGroupId, @Nonnull String templateId);

    /**
     * Schedule or unschedule components in the specified Process Group.
     *
     * @param processGroupId the process group id
     * @param parentGroupId the parent process group id
     * @param state the desired state of the descendant components
     * @throws NifiComponentNotFoundException if the process group does not exist
     */
    void schedule(@Nonnull String processGroupId, @Nonnull String parentGroupId, @Nonnull NiFiComponentState state);

    /**
     * Updates a process group.
     *
     * @param processGroupEntity the process group
     * @return the updated process group
     * @throws NifiComponentNotFoundException if the process group does not exist
     */
    @Nonnull
    ProcessGroupDTO update(@Nonnull ProcessGroupDTO processGroupEntity);
}
