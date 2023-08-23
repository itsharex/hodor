/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hodor.admin.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.core.MsgCode;
import org.dromara.hodor.admin.exception.ServiceException;
import org.dromara.hodor.admin.service.JobOperatorService;
import org.dromara.hodor.client.HodorApiClient;
import org.dromara.hodor.client.api.JobApi;
import org.dromara.hodor.common.utils.Utils;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.core.dag.FlowData;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.core.service.JobInfoService;
import org.dromara.hodor.model.enums.JobStatus;
import org.dromara.hodor.model.enums.Priority;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.model.job.JobKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JobOperatorServiceImpl
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
@Service
public class JobOperatorServiceImpl implements JobOperatorService {

    private final JobInfoService jobInfoService;

    private final JobApi jobApi;

    public JobOperatorServiceImpl(final JobInfoService jobInfoService, final HodorApiClient hodorApiClient) {
        this.jobInfoService = jobInfoService;
        this.jobApi = hodorApiClient.createApi(JobApi.class);
    }

    @Override
    public PageInfo<JobInfo> queryByPage(JobInfo jobInfo, Integer pageNo, Integer pageSize) {
        return jobInfoService.queryByPage(jobInfo, pageNo, pageSize);
    }

    @Override
    public JobInfo queryById(Long id) {
        return jobInfoService.queryById(id);
    }

    @Override
    @Transactional
    public JobInfo addJob(JobInfo jobInfo) {
        //final JobInfo result = jobInfoService.addJob(jobInfo);
        if (jobInfoService.isExists(jobInfo)) {
            throw new ServiceException(MsgCode.CREATE_JOB_ERROR, "Job already exists");
        }
        try {
            jobApi.registerJob(jobInfo);
        } catch (Exception e) {
            log.error("Create job error", e);
            throw new ServiceException(MsgCode.CREATE_JOB_ERROR, e.getMessage());
        }
        return jobInfo;
    }

    @Override
    @Transactional
    public JobInfo updateById(JobInfo jobInfo) {
        Utils.Assert.notNull(jobInfo.getId(), "Job id must not be null");
        final JobInfo updated = jobInfoService.updateById(jobInfo);
        if (updated == null) {
            return null;
        }
        try {
            jobApi.updateJob(updated);
        } catch (Exception e) {
            log.error("Update job error", e);
            throw new ServiceException(MsgCode.UPDATE_JOB_ERROR, e.getMessage());
        }
        return updated;
    }

    @Override
    @Transactional
    public Boolean deleteById(Long id) {
        final JobInfo jobInfo = jobInfoService.queryById(id);
        if (jobInfo == null) {
            throw new ServiceException(MsgCode.INVALID_JOB_ID, id);
        }
        try {
            final boolean deleted = jobInfoService.deleteById(id);
            if (deleted) {
                jobApi.deleteJob(jobInfo);
            }
        } catch (Exception e) {
            throw new ServiceException(MsgCode.DELETE_JOB_ERROR, e.getMessage());
        }
        return true;
    }

    @Override
    @Transactional
    public Boolean stopById(Long id) {
        final JobInfo jobInfo = jobInfoService.queryById(id);
        if (jobInfo == null) {
            throw new ServiceException(MsgCode.INVALID_JOB_ID, id);
        }

        final JobKey jobKey = JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName());
        jobInfo.setJobStatus(JobStatus.STOP);
        final JobInfo updated = jobInfoService.updateById(jobInfo);
        if (updated == null) {
            throw new ServiceException(MsgCode.UPDATE_JOB_ERROR, jobKey);
        }

        try {
            jobApi.stopJob(jobInfo);
        } catch (Exception e) {
            throw new ServiceException(MsgCode.STOP_JOB_ERROR, e.getMessage());
        }
        return true;
    }

    @Override
    @Transactional
    public Boolean resumeById(Long id) {
        final JobInfo jobInfo = jobInfoService.queryById(id);
        if (jobInfo == null) {
            throw new ServiceException(MsgCode.INVALID_JOB_ID, id);
        }

        final JobKey jobKey = JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName());
        jobInfo.setJobStatus(JobStatus.RUNNING);
        final JobInfo updated = jobInfoService.updateById(jobInfo);
        if (updated == null) {
            throw new ServiceException(MsgCode.UPDATE_JOB_ERROR, jobKey);
        }

        try {
            jobApi.registerJob(jobInfo);
        } catch (Exception e) {
            throw new ServiceException(MsgCode.RESUME_JOB_ERROR, e.getMessage());
        }
        return true;
    }

    @Override
    public Boolean executeById(Long id) {
        final JobInfo jobInfo = jobInfoService.queryById(id);
        if (jobInfo == null) {
            throw new ServiceException(MsgCode.INVALID_JOB_ID, id);
        }

        try {
            jobApi.executeJob(jobInfo);
        } catch (Exception e) {
            throw new ServiceException(MsgCode.EXECUTE_JOB_ERROR, e.getMessage());
        }
        return true;
    }

    @Override
    public Boolean uploadJobs(FlowData flowData) {
        List<JobDesc> jobDescList = new ArrayList<>();
        for (FlowData f : flowData.getNodes()) {
            // 1. 创建JobInfo
            Map<String, Object> config = f.getConfig();
            JobInfo jobInfo = new JobInfo();
            jobInfo.setGroupName(flowData.getGroupName());
            jobInfo.setJobName(f.getJobName());
            jobInfo.setJobCommand((String) config.get("jobCommand"));
            jobInfo.setJobCommandType((String) config.get("jobCommandType"));
            jobInfo.setPriority(Priority.valueOf((Integer) config.get("priority")));
            jobInfo.setJobParameters(String.valueOf(config.get("jobParameters")));
            jobInfo.setTimeout((Integer) config.get("timeout"));
            jobInfo.setRetryCount((Integer) config.get("retryCount"));

            // 2. 任务是否已经存在
            if (jobInfoService.isExists(jobInfo)) {
                throw new ServiceException(MsgCode.UPLOAD_JOB_ERROR, "Job already exists");
            }
            jobDescList.add(jobInfo);
        }
        try {
            jobApi.registerJobs(jobDescList);
        } catch (Exception e) {
            log.error("upload jobs error", e);
            throw new ServiceException(MsgCode.UPLOAD_JOB_ERROR, e.getMessage());
        }
        return true;
    }
}
