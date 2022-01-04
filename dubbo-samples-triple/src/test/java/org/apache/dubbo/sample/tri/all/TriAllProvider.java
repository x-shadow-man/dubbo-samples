package org.apache.dubbo.sample.tri.all;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.sample.tri.EmbeddedZooKeeper;
import org.apache.dubbo.sample.tri.PbGreeter;
import org.apache.dubbo.sample.tri.TriSampleConstants;
import org.apache.dubbo.sample.tri.service.PbGreeterManual;
import org.apache.dubbo.sample.tri.service.WrapGreeter;
import org.apache.dubbo.sample.tri.service.impl.PbGreeterImpl;
import org.apache.dubbo.sample.tri.service.impl.TestServiceImpl;
import org.apache.dubbo.sample.tri.service.impl.WrapGreeterImpl;

import grpc.testing.TestService;


public class TriAllProvider {

    public static void main(String[] args) {
        new EmbeddedZooKeeper(2181, false).start();
        ServiceConfig<PbGreeter> pbService = new ServiceConfig<>();
        pbService.setInterface(PbGreeter.class);
        PbGreeterImpl greeterImpl = new PbGreeterImpl();
        pbService.setRef(greeterImpl);

        ServiceConfig<PbGreeterManual> pbManualService = new ServiceConfig<>();
        pbManualService.setInterface(PbGreeterManual.class);
        pbManualService.setRef(new PbGreeterImpl());

        ServiceConfig<WrapGreeter> wrapService = new ServiceConfig<>();
        wrapService.setInterface(WrapGreeter.class);
        wrapService.setRef(new WrapGreeterImpl());

        ServiceConfig<TestService> testService = new ServiceConfig<>();
        testService.setInterface(TestService.class);
        testService.setRef(new TestServiceImpl());

        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        bootstrap.application(new ApplicationConfig("demo-provider"))
                .registry(new RegistryConfig(TriSampleConstants.ZK_ADDRESS_MODE_ALL))
                .protocol(new ProtocolConfig(CommonConstants.TRIPLE, TriSampleConstants.SERVER_PORT))
                .service(pbService)
                .service(pbManualService)
                .service(wrapService)
                .service(testService)
                .start()
                .await();
    }
}