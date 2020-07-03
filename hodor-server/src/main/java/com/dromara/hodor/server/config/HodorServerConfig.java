package com.dromara.hodor.server.config;

import com.dromara.hodor.server.HodorServerInit;
import com.dromara.hodor.server.service.HodorService;
import com.dromara.hodor.server.service.LeaderService;
import com.dromara.hodor.server.service.RegisterService;
import com.dromara.hodor.server.service.RemoteService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * hodor server configuration
 * @author tomgs
 * @since 2020/6/28
 */
@Configuration
public class HodorServerConfig {

    @Bean
    public HodorServerProperties serverProperties() {
        return new HodorServerProperties();
    }

    @Bean
    public RegisterService registerService() {
        return new RegisterService(serverProperties());
    }

    @Bean
    public RemoteService remoteService() {
        return new RemoteService(serverProperties());
    }

    @Bean
    public LeaderService leaderService() {
        return new LeaderService(registerService());
    }

    @Bean
    public HodorService hodorService() {
        return new HodorService(leaderService());
    }

    @Bean
    public HodorServerInit hodorServerInit() {
        return new HodorServerInit(remoteService(), registerService(), hodorService());
    }

}
