package org.dromara.hodor.actuator.agent;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.api.HodorActuatorManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.concurrent.CountDownLatch;

/**
 * HodorBigdataActuatorInit
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HodorAgentActuatorInit implements ApplicationRunner {

    private final HodorActuatorManager actuatorManager;

    private final CountDownLatch latch;

    public HodorAgentActuatorInit(final HodorActuatorManager actuatorManager) {
        this.actuatorManager = actuatorManager;
        this.latch = new CountDownLatch(1);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("HodorBigdataActuator starting");
        Thread thread = new Thread(() -> {
            try {
                actuatorManager.start();
                latch.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        thread.setDaemon(false);
        thread.setName("hodor-agent-server");
        thread.start();

        log.info("HodorBigdataActuator starting success");
        // add close shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("HodorBigdataActuator closed");
            actuatorManager.close();
            latch.countDown();
        }));
    }

}
