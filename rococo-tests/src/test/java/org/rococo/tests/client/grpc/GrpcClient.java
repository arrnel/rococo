package org.rococo.tests.client.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.rococo.tests.client.grpc.interceptor.GrpcConsoleInterceptor;
import org.rococo.tests.config.Config;

public abstract class GrpcClient {

    protected static final Config CFG = Config.getInstance();
    protected final Channel channel;

    public GrpcClient(String host, int port) {
        channel = ManagedChannelBuilder
                .forAddress(host, port)
                .intercept(new GrpcConsoleInterceptor())
                .usePlaintext()
                .build();
    }

}
