package com.dynatrace.diagnostic.plugins;

public class FlowStatElements  
{

    private FlowStatElements(String description, String key, boolean isCalculated)
    {
        this.description = description;
        this.key = key;
        this.isCalculated = isCalculated;
    }

    public String getKey()
    {
        return key;
    }

    public boolean getIsCalculated() {
    	return isCalculated;
    }
    public static FlowStatElements[] values()
    {
    	FlowStatElements agraphableflowstats[];
        int i;
        FlowStatElements agraphableflowstats1[];
        System.arraycopy(agraphableflowstats = ENUM$VALUES, 0, agraphableflowstats1 = new FlowStatElements[i = agraphableflowstats.length], 0, i);
        return agraphableflowstats1;
    }

    public static final FlowStatElements TOTAL_ELAPSED_TIME;
    public static final FlowStatElements ELAPSED_TIME_PER_MESSAGE;
    public static final FlowStatElements MAXIMUM_ELAPSED_TIME;
    public static final FlowStatElements MINIMUM_ELAPSED_TIME;
    public static final FlowStatElements TOTAL_CPU_TIME;
    public static final FlowStatElements CPU_TIME_PER_MESSAGE;
    public static final FlowStatElements MAXIMUM_CPU_TIME;
    public static final FlowStatElements MINIMUM_CPU_TIME;
    public static final FlowStatElements CPU_TIME_WAITING_FOR_INPUT_MESSAGE;
    public static final FlowStatElements ELAPSED_TIME_WAITING_FOR_INPUT_MESSAGE;
    public static final FlowStatElements TOTAL_INPUT_MESSAGES;
    public static final FlowStatElements TOTAL_SIZE_OF_INPUT_MESSAGES;
    public static final FlowStatElements MAX_SIZE_OF_INPUT_MESSAGES;
    public static final FlowStatElements MIN_SIZE_OF_INPUT_MESSAGES;
    public static final FlowStatElements NUM_THREADS_IN_POOL;
    public static final FlowStatElements TIMES_MAX_THREADS_REACHED;
    public static final FlowStatElements TOTAL_NUM_MQ_ERRORS;
    public static final FlowStatElements TOTAL_NUMBER_OF_MESSAGES_WITH_ERRRORS;
    public static final FlowStatElements TOTAL_NUMBER_OF_ERRORS_PROCESSING_MESSAGES;
    public static final FlowStatElements TOTAL_NUMBER_OF_TIMEOUTS_WAITING_FOR_REPLIES_TO_AGGREGATE_MESSAGES;
    public static final FlowStatElements TOTAL_NUMBER_OF_COMMITS;
    public static final FlowStatElements TOTAL_NUMBER_OF_BACKOUTS;
    String description;
    String key;
    boolean isCalculated;
    private static final FlowStatElements ENUM$VALUES[];

    static 
    {
        TOTAL_ELAPSED_TIME = new FlowStatElements("Total elapsed time", "TotalElapsedTime", false);
        ELAPSED_TIME_PER_MESSAGE = new FlowStatElements("Average elapsed time per message", "ElapsedTimePerMessage", true);
        MAXIMUM_ELAPSED_TIME = new FlowStatElements("Maximum elapsed time", "MaximumElapsedTime", false);
        MINIMUM_ELAPSED_TIME = new FlowStatElements("Minimum elapsed time", "MinimumElapsedTime", false);
        TOTAL_CPU_TIME = new FlowStatElements("Total CPU time", "TotalCPUTime", false);
        CPU_TIME_PER_MESSAGE = new FlowStatElements("Average CPU time per message", "CPUTimePerMessage", true);
        MAXIMUM_CPU_TIME = new FlowStatElements("Maximum CPU time", "MaximumCPUTime", false);
        MINIMUM_CPU_TIME = new FlowStatElements("Minimum CPU time", "MinimumCPUTime", false);
        CPU_TIME_WAITING_FOR_INPUT_MESSAGE = new FlowStatElements("CPU Time waiting for input message", "CPUTimeWaitingForInputMessage", false);
        ELAPSED_TIME_WAITING_FOR_INPUT_MESSAGE = new FlowStatElements("Elapsed time waiting for input message", "ElapsedTimeWaitingForInputMessage", false);
        TOTAL_INPUT_MESSAGES = new FlowStatElements("Total input messages", "TotalInputMessages", false);
        TOTAL_SIZE_OF_INPUT_MESSAGES = new FlowStatElements("Total size of input messages", "TotalSizeOfInputMessages", false);
        MAX_SIZE_OF_INPUT_MESSAGES = new FlowStatElements("Max size of input messages", "MaximumSizeOfInputMessages", false);
        MIN_SIZE_OF_INPUT_MESSAGES = new FlowStatElements("Min size of input messages", "MinimumSizeOfInputMessages", false);
        NUM_THREADS_IN_POOL = new FlowStatElements("Number of threads in pool", "NumberOfThreadsInPool", false);
        TIMES_MAX_THREADS_REACHED = new FlowStatElements("Times max number of threads reached", "TimesMaximumNumberOfThreadsReached", false);
        TOTAL_NUM_MQ_ERRORS = new FlowStatElements("Total number of MQ errors", "TotalNumberOfMQErrors", false);
        TOTAL_NUMBER_OF_MESSAGES_WITH_ERRRORS = new FlowStatElements("Total number of messages with errors", "TotalNumberOfMessagesWithErrors", false);
        TOTAL_NUMBER_OF_ERRORS_PROCESSING_MESSAGES = new FlowStatElements("Total number of errors processing messages", "TotalNumberOfErrorsProcessingMessages", false);
        TOTAL_NUMBER_OF_TIMEOUTS_WAITING_FOR_REPLIES_TO_AGGREGATE_MESSAGES = new FlowStatElements("Total number of timeouts waiting for replies to aggregate messages", "TotalNumberOfTimeOutsWaitingForRepliesToAggregateMessages", false);
        TOTAL_NUMBER_OF_COMMITS = new FlowStatElements("Total number of commits", "TotalNumberOfCommits", false);
        TOTAL_NUMBER_OF_BACKOUTS = new FlowStatElements("Total number of backout", "TotalNumberOfBackouts", false);
        ENUM$VALUES = (new FlowStatElements[] {
            TOTAL_ELAPSED_TIME, ELAPSED_TIME_PER_MESSAGE, MAXIMUM_ELAPSED_TIME, MINIMUM_ELAPSED_TIME, TOTAL_CPU_TIME, CPU_TIME_PER_MESSAGE, MAXIMUM_CPU_TIME, MINIMUM_CPU_TIME, CPU_TIME_WAITING_FOR_INPUT_MESSAGE, ELAPSED_TIME_WAITING_FOR_INPUT_MESSAGE, 
            TOTAL_INPUT_MESSAGES, TOTAL_SIZE_OF_INPUT_MESSAGES, MAX_SIZE_OF_INPUT_MESSAGES, MIN_SIZE_OF_INPUT_MESSAGES, NUM_THREADS_IN_POOL, TIMES_MAX_THREADS_REACHED, TOTAL_NUM_MQ_ERRORS, TOTAL_NUMBER_OF_MESSAGES_WITH_ERRRORS, TOTAL_NUMBER_OF_ERRORS_PROCESSING_MESSAGES, TOTAL_NUMBER_OF_TIMEOUTS_WAITING_FOR_REPLIES_TO_AGGREGATE_MESSAGES, 
            TOTAL_NUMBER_OF_COMMITS, TOTAL_NUMBER_OF_BACKOUTS
        });
    }
}
