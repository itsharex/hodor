package org.dromara.hodor.server.restservice.service;

import java.util.List;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.utils.HashUtils;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.core.service.JobInfoService;
import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.model.enums.JobStatus;
import org.dromara.hodor.model.enums.JobType;
import org.dromara.hodor.model.job.JobInstance;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.DataInterval;
import org.dromara.hodor.remoting.api.RemotingClient;
import org.dromara.hodor.scheduler.api.SchedulerManager;
import org.dromara.hodor.server.manager.CopySetManager;
import org.dromara.hodor.server.manager.MetadataManager;
import org.dromara.hodor.server.restservice.HodorRestService;
import org.dromara.hodor.server.restservice.RestMethod;
import org.dromara.hodor.server.service.LeaderService;
import org.dromara.hodor.server.service.RegisterService;

/**
 * scheduler controller
 *
 * @author tomgs
 * @since 2021/1/28
 */
@HodorRestService(value = "scheduler", desc = "scheduler rest service")
@SuppressWarnings("unused")
public class SchedulerService {

    private final LeaderService leaderService;

    private final RegisterService registerService;

    private final JobInfoService jobInfoService;

    private final SchedulerManager schedulerManager;

    private final MetadataManager metadataManager;

    public SchedulerService(final RegisterService registerService, final LeaderService leaderService,
                            final JobInfoService jobInfoService) {
        this.registerService = registerService;
        this.leaderService = leaderService;
        this.jobInfoService = jobInfoService;
        this.schedulerManager = SchedulerManager.getInstance();
        this.metadataManager = MetadataManager.getInstance();
    }

    @RestMethod("isAlive")
    public HodorResult<String> isAlive() {
        return HodorResult.success("success");
    }

    @RestMethod("createJob")
    public HodorResult<String> createJob(JobInstance jobInstance) {
        JobInfo jobInfo = convertJobInfo(jobInstance);
        jobInfoService.addJobIfAbsent(jobInfo);
        fireJobCreateEvent();
        return HodorResult.success("success");
    }

    @RestMethod("batchCreateJob")
    public HodorResult<String> batchCreateJob(List<JobInstance> jobs) {
        for (JobInstance jobInstance : jobs) {
            JobInfo jobInfo = convertJobInfo(jobInstance);
            jobInfoService.addJobIfAbsent(jobInfo);
        }
        fireJobCreateEvent();
        return HodorResult.success("success");
    }

    private void fireJobCreateEvent() {
        RemotingClient client = RemotingClient.getInstance();
        //client.sendHttpRequest();
    }

    @RestMethod("onEvent")
    public HodorResult<String> onEvent(Event<Object> event) {
        String serverEndpoint = registerService.getServerEndpoint();
        CopySet activeCopySet = CopySetManager.getInstance().getCopySet(serverEndpoint);
        DataInterval activeDataInterval = activeCopySet.getDataInterval();

        return HodorResult.success("success");
    }

    private JobInfo convertJobInfo(JobInstance job) {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setHashId(HashUtils.hash(job.getGroupName() + job.getJobName()));
        jobInfo.setGroupName(job.getGroupName());
        jobInfo.setJobName(job.getJobName());
        jobInfo.setJobCommandType(job.getCommandType());
        jobInfo.setJobStatus(JobStatus.READY);
        jobInfo.setCron(job.getCron());
        jobInfo.setTimeout(job.getTimeout());
        jobInfo.setFireNow(job.isFireNow());
        jobInfo.setIsBroadcast(job.isBroadcast());
        jobInfo.setJobType(JobType.TIME_JOB);
        return jobInfo;
    }

}
