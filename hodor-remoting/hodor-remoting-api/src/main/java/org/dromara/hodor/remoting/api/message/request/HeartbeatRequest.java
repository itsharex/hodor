package org.dromara.hodor.remoting.api.message.request;

import org.dromara.hodor.remoting.api.message.RequestBody;

/**
 *  heartbeat request
 *
 * @author tomgs
 * @version 2021/3/3 1.0 
 */
public class HeartbeatRequest implements RequestBody {

    @Override
    public Long getRequestId() {
        return null;
    }

}
