package org.kie.remote.services.rest.query;

import static org.kie.internal.query.QueryParameterIdentifiers.*;

import java.util.Date;

import org.jbpm.process.audit.command.AuditVariableInstanceLogQueryCommand;
import org.jbpm.services.task.commands.TaskQueryDataCommand;
import org.kie.api.task.model.Status;
import org.kie.internal.query.AbstractQueryBuilderImpl;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogQueryBuilder;
import org.kie.internal.task.query.TaskQueryBuilder;

public class RemoteServicesQueryCommandBuilder extends AbstractQueryBuilderImpl<RemoteServicesQueryCommandBuilder> {

    private final String taskUserId;
    
    public RemoteServicesQueryCommandBuilder() {
        this.taskUserId = null;
        intersect();
        last();
    }
    
    public RemoteServicesQueryCommandBuilder(String userId) { 
        this.taskUserId = userId;
        intersect();
    }

    // process related criteria
   
    /**
     * Add one or more deployment ids as a criteria to the query
     * @param deploymentId
     * @return the current instance
     */
    public RemoteServicesQueryCommandBuilder deploymentId( String... deploymentId ) {
        addObjectParameter(EXTERNAL_ID_LIST, "deployment id", deploymentId);
        return this;
    }

    /**
     * Add one or more process ids as a criteria to the query
     * @param processId
     * @return the current instance
     */
    public RemoteServicesQueryCommandBuilder processId( String... processId ) {
        addObjectParameter(PROCESS_ID_LIST, "process id", processId );
        return this;
    }

    /**
     * Add one or more process versions as a criteria to the query
     * @param processId
     * @return the current instance
     */
    public RemoteServicesQueryCommandBuilder processVersion( String... processVersion ) {
        addObjectParameter(PROCESS_VERSION_LIST, "process version", processVersion);
        return this;
    }

    /**
     * Add one or more process instance ids as a criteria to the query
     * @param processInstanceId
     * @return the current instance
     */
    public RemoteServicesQueryCommandBuilder processInstanceId( long... processInstanceId ) {
        addLongParameter(PROCESS_INSTANCE_ID_LIST, "process instance id", processInstanceId);
        return this;
    }

    /**
     * Specify one more statuses (in the form of an int) as criteria.
     * @param status one or more int statuses
     * @return The current instance of this query builder
     */
    public RemoteServicesQueryCommandBuilder processInstanceStatus(int... status) { 
        addIntParameter(PROCESS_INSTANCE_STATUS_LIST, "process instance status", status);
        return this;
    }

    public RemoteServicesQueryCommandBuilder startDate( Date... date ) {
        addObjectParameter(START_DATE_LIST, "start date", date);
        return this;
    }

    public RemoteServicesQueryCommandBuilder startDateMin( Date rangeStart ) {
        addRangeParameter(START_DATE_LIST, "start date range, start", rangeStart, true);
        return this;
    }

    public RemoteServicesQueryCommandBuilder startDateMax( Date rangeEnd ) {
        addRangeParameter(START_DATE_LIST, "start date range, end", rangeEnd, false);
        return this;
    }

    public RemoteServicesQueryCommandBuilder endDate( Date... date ) {
        addObjectParameter(END_DATE_LIST, "end date", date );
        return this;
    }

    public RemoteServicesQueryCommandBuilder endDateMin( Date rangeStart ) {
        addRangeParameter(END_DATE_LIST, "end date range, start", rangeStart, true);
        return this;
    }

    public RemoteServicesQueryCommandBuilder endDateMax( Date rangeEnd ) {
        addRangeParameter(END_DATE_LIST, "end date range, end", rangeEnd, false);
        return this;
    }

    // task related criteria
    
    public RemoteServicesQueryCommandBuilder workItemId( long... workItemId ) {
        addLongParameter(WORK_ITEM_ID_LIST, "work item id", workItemId);
        return this;
    }

    /**
     * Add one or more task ids as a criteria to the query
     * @param taskId
     * @return the current instance
     */
    public RemoteServicesQueryCommandBuilder taskId( long... taskId ) {
        addLongParameter(TASK_ID_LIST, "task id", taskId);
        return this;
    }

    /**
     * Add one or more statuses as a criteria to the query
     * @param status
     * @return the current instance
     */
    public RemoteServicesQueryCommandBuilder taskStatus( Status... status ) {
        addObjectParameter(TASK_STATUS_LIST, "task status", status);
        return this;
    }

    

    /**
     * Add one or more initiator ids as a criteria to the query
     * @param createdById
     * @return the current instance
     */
    public RemoteServicesQueryCommandBuilder initiator( String... createdById ) {
        addObjectParameter(CREATED_BY_LIST, "initiator", createdById);
        return this;
    }

    /**
     * Add one or more stakeholder ids as a criteria to the query
     * @param stakeHolderId
     * @return the current instance
     */
    public RemoteServicesQueryCommandBuilder stakeHolder( String... stakeHolderId ) {
        addObjectParameter(STAKEHOLDER_ID_LIST, "stakeholder", stakeHolderId);
        return this;
    }
    
    /**
     * Add one or more potential owner ids as a criteria to the query
     * @param stakeHolderId
     * @return the current instance
     */
    public RemoteServicesQueryCommandBuilder potentialOwner( String... potentialOwnerId ) {
        addObjectParameter(POTENTIAL_OWNER_ID_LIST, "potential owner", potentialOwnerId);
        return this;
    }

    /**
     * Add one or more (actual) task owner ids as a criteria to the query
     * @param taskOwnerId
     * @return the current instance
     */
    public RemoteServicesQueryCommandBuilder taskOwner( String... taskOwnerId ) {
        addObjectParameter(ACTUAL_OWNER_ID_LIST, "task owner", taskOwnerId);
        return this;
    }

    /**
     * Add one or more business administrator ids as a criteria to the query
     * @param businessAdminId
     * @return the current instance
     */
    public RemoteServicesQueryCommandBuilder businessAdmin( String... businessAdminId ) {
        addObjectParameter(BUSINESS_ADMIN_ID_LIST, "business admin", businessAdminId);
        return this;
    }

    public RemoteServicesQueryCommandBuilder variableId( String... variableId ) {
        addObjectParameter(VARIABLE_ID_LIST, "variable id", variableId );
        return this;
    }
   
    // variable related critera
    
    public RemoteServicesQueryCommandBuilder value( String... value ) {
        addObjectParameter(VALUE_LIST, "variable value", value );
        return this;
    }

    public RemoteServicesQueryCommandBuilder oldValue( String... oldVvalue ) {
        addObjectParameter(OLD_VALUE_LIST, "old variable value", oldVvalue );
        return this;
    }
    
    public RemoteServicesQueryCommandBuilder last() {
        addObjectParameter(LAST_VARIABLE_LIST, "last variable value", Boolean.TRUE.booleanValue() );
        return this;
    }

    // command generation
    
    public TaskQueryDataCommand createTaskQueryDataCommand() { 
        if( taskUserId == null ) { 
            throw new IllegalStateException("A user id is required to create a " + TaskQueryDataCommand.class.getSimpleName() );
        }
        TaskQueryDataCommand cmd = new TaskQueryDataCommand(getQueryData());
        cmd.setUserId(taskUserId);
        return cmd;
    }
    
    public AuditVariableInstanceLogQueryCommand createVariableInstanceLogQueryCommand() { 
        return new AuditVariableInstanceLogQueryCommand(getQueryData());
    }

    @Override
    public RemoteServicesQueryCommandBuilder clear() { 
      super.clear();
      intersect();
      return this;
    }
}