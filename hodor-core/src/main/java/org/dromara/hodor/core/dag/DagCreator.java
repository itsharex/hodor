package org.dromara.hodor.core.dag;

import java.util.List;
import org.dromara.hodor.common.dag.Dag;
import org.dromara.hodor.common.dag.DagBuilder;
import org.dromara.hodor.model.enums.CommandType;
import org.dromara.hodor.model.job.JobDesc;

/**
 * dag creator
 *
 * @author tomgs
 * @since 2021/9/2
 */
public class DagCreator {

    private final FlowData flowNode;

    private final DagBuilder dagBuilder;

    public DagCreator(final FlowData flowNode) {
        final String groupName = flowNode.getGroupName();
        final String jobName = flowNode.getJobName();
        this.flowNode = flowNode;
        this.dagBuilder = new DagBuilder(groupName + "#" + jobName);
    }

    public Dag create() {
        createNodes();
        linkNodes();
        return this.dagBuilder.build();
    }

    private void createNodes() {
        for (final FlowData node : this.flowNode.getNodes()) {
            createNode(node);
        }
    }

    private void createNode(final FlowData node) {
        JobDesc jobDesc = buildJobDesc(node);
        this.dagBuilder.createNode(node.getGroupName(), node.getJobName(), jobDesc);
    }

    private JobDesc buildJobDesc(FlowData node) {
        JobDesc jobDesc = new JobDesc();
        jobDesc.setGroupName(node.getGroupName());
        jobDesc.setJobName(node.getJobName());
        jobDesc.setJobCommandType(CommandType.of(node.getType()));
        jobDesc.setJobCommand(node.getConfig().get("command"));
        return jobDesc;
    }

    private void linkNodes() {
        for (final FlowData node : this.flowNode.getNodes()) {
            linkNode(node);
        }
    }

    private void linkNode(final FlowData node) {
        final String nodeName = node.getJobName();
        final List<String> parents = node.getDependsOn();
        if (parents == null) {
            return;
        }
        for (final String parentJobName : parents) {
            this.dagBuilder.addParentNode(nodeName, parentJobName);
        }
    }

}
