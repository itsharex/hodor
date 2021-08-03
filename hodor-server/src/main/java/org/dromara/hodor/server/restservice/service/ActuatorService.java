package org.dromara.hodor.server.restservice.service;

import com.google.common.base.Preconditions;
import org.dromara.hodor.model.actuator.ActuatorInfo;
import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.server.restservice.HodorRestService;
import org.dromara.hodor.server.restservice.RestMethod;
import org.dromara.hodor.server.service.RegisterService;

/**
 * actuator service
 *
 * @author tomgs
 * @since 2021/2/5
 */
@HodorRestService(value = "actuator", desc = "actuator rest service")
@SuppressWarnings("unused")
public class ActuatorService {

    private final RegisterService registerService;

    public ActuatorService(RegisterService registerService) {
        this.registerService = registerService;
    }

    @RestMethod("heartbeat")
    public HodorResult<String> heartbeat(ActuatorInfo actuatorInfo) {
        checkActuatorInfo(actuatorInfo);
        actuatorInfo.setLastHeartbeat(System.currentTimeMillis());
        registerService.createActuator(actuatorInfo);
        return HodorResult.success("success");
    }

    @RestMethod("offline")
    public HodorResult<String> offline(ActuatorInfo actuatorInfo) {
        checkActuatorInfo(actuatorInfo);
        registerService.removeActuator(actuatorInfo);
        return HodorResult.success("success");
    }

    private void checkActuatorInfo(ActuatorInfo actuatorInfo) {
        Preconditions.checkNotNull(actuatorInfo.getNodeInfo(), "actuator node info must be not null.");
        Preconditions.checkNotNull(actuatorInfo.getGroupNames(), "actuator group names must be not null.");
    }

}
