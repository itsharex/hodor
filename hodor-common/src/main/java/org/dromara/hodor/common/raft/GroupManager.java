package org.dromara.hodor.common.raft;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.ratis.protocol.RaftGroup;
import org.apache.ratis.protocol.RaftGroupId;
import org.apache.ratis.protocol.RaftPeer;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.apache.ratis.util.NetUtils;
import org.dromara.hodor.common.utils.StringUtils;

/**
 * GroupManager
 *
 * @author tomgs
 * @since 2022/3/24
 */
public class GroupManager {

    private static final GroupManager INSTANCE = new GroupManager();

    // 这个会自动转换为UUID，但是需要确保字符串为16位
    public static final RaftGroupId RATIS_KV_GROUP_ID = RaftGroupId.valueOf(ByteString.copyFromUtf8("RatisKVGroup0000"));

    private final Map<String, RaftGroup> raftGroupMap = new ConcurrentHashMap<>();

    private GroupManager() {
    }

    public static GroupManager getInstance() {
        return INSTANCE;
    }

    public RaftGroup getRaftGroup(String groupId) {
        return raftGroupMap.get(groupId);
    }

    public RaftGroup createRaftGroup(String groupId, String[] addresses) {
        if (StringUtils.isBlank(groupId) || groupId.length() != 16) {
            throw new IllegalArgumentException("groupId length must equals 16.");
        }
        return raftGroupMap.computeIfAbsent(groupId, key -> {
            final List<RaftPeer> peers = new ArrayList<>(addresses.length);
            for (String address : addresses) {
                final String[] segmentAddress = StringUtils.split(address, ":");
                // ip:port
                if (segmentAddress.length == 2) {
                    peers.add(buildRaftPeer(segmentAddress[0], Integer.parseInt(segmentAddress[1]), 0));
                }

                // ip:port:priority
                if (segmentAddress.length == 3) {
                    peers.add(buildRaftPeer(segmentAddress[0], Integer.parseInt(segmentAddress[1]),
                        Integer.parseInt(segmentAddress[2])));
                }
            }
            final RaftGroupId raftGroupId = RaftGroupId.valueOf(ByteString.copyFromUtf8(groupId));
            final List<RaftPeer> raftPeers = Collections.unmodifiableList(peers);
            return RaftGroup.valueOf(raftGroupId, raftPeers);
        });
    }

    public RaftPeer buildRaftPeer(InetSocketAddress socketAddress, int priority) {
        return RaftPeer.newBuilder()
            .setId(getPeerId(socketAddress))
            .setAddress(socketAddress)
            .setPriority(priority)
            .build();
    }

    public RaftPeer buildRaftPeer(String host, int port, int priority) {
        final InetSocketAddress socketAddress =
            NetUtils.createSocketAddrForHost(host, port);
        return buildRaftPeer(socketAddress, priority);
    }

    private String getPeerId(InetSocketAddress address) {
        return address.getHostName() + "_" + address.getPort();
    }

}
