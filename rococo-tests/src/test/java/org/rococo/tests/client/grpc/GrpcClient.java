package org.rococo.tests.client.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.rococo.tests.client.grpc.interceptor.GrpcConsoleInterceptor;
import org.rococo.tests.config.Config;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public abstract class GrpcClient implements AutoCloseable {

    protected static final Config CFG = Config.getInstance();
    protected final ManagedChannel channel;
    private final String host;
    private final int port;

    public GrpcClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.channel = getChannel();
    }

    @Nonnull
    protected ManagedChannel getChannel() {
        return channel == null || channel.isShutdown() || channel.isTerminated()
                ? getNewChannel()
                : channel;
    }

    @Nonnull
    private ManagedChannel getNewChannel() {
        return ManagedChannelBuilder
                .forAddress(host, port)
                .intercept(new GrpcConsoleInterceptor())
                .usePlaintext()
                .build();
    }

    @Override
    public void close() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
            try {
                channel.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
