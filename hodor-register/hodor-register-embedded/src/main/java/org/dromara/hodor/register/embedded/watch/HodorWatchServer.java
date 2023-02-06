package org.dromara.hodor.register.embedded.watch;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.raft.HodorRaftGroup;
import org.dromara.hodor.common.raft.HodorRaftStateMachine;
import org.dromara.hodor.common.raft.RaftOptions;
import org.dromara.hodor.common.raft.kv.core.HodorKVOptions;
import org.dromara.hodor.common.raft.kv.core.HodorKVServer;
import org.dromara.hodor.common.raft.kv.core.KVConstant;
import org.dromara.hodor.common.raft.kv.core.RequestHandler;

/**
 * HodorWatchServer
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HodorWatchServer extends HodorKVServer {

    public HodorWatchServer(final HodorKVOptions hodorKVOptions) throws Exception {
        super(hodorKVOptions);
        final RaftOptions raftOptions = hodorKVOptions.getRaftOptions();
        Map<HodorRaftGroup, HodorRaftStateMachine> stateMachineMap = new HashMap<>();
        HodorRaftGroup hodorRaftGroup = HodorRaftGroup.builder()
            .raftGroupName(KVConstant.HODOR_KV_GROUP_NAME)
            .addresses(raftOptions.getServerAddresses())
            .build();
        RequestHandler requestHandler = new HodorWatchRequestHandler(this.storageEngine);
        stateMachineMap.putIfAbsent(hodorRaftGroup, new HodorWatchStateMachine(requestHandler));
        raftOptions.setStateMachineMap(stateMachineMap);
    }

    @Override
    public void start() throws Exception {
        super.start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

}
