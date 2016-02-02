package com.github.trask;

import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import org.github.trask.Hello.HelloReply;
import org.github.trask.Hello.HelloRequest;
import org.github.trask.HelloServiceGrpc;
import org.github.trask.HelloServiceGrpc.HelloServiceStub;
import org.junit.Test;

public class GrpcNettyLeakTest {

    @Test
    public void test() throws InterruptedException {
        ResourceLeakDetector.setLevel(Level.ADVANCED);
        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 8025)
                .negotiationType(NegotiationType.PLAINTEXT)
                .build();
        HelloServiceStub client = HelloServiceGrpc.newStub(channel);
        for (int i = 0; i < 500; i++) {
            client.hello(HelloRequest.getDefaultInstance(),
                    new StreamObserver<HelloReply>() {
                        @Override
                        public void onNext(HelloReply value) {}
                        @Override
                        public void onError(Throwable t) {}
                        @Override
                        public void onCompleted() {}
                    });
            System.gc();
        }
    }
}
