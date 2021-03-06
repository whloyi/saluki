/*
 * Copyright 1999-2012 DianRong.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.quancheng.saluki.core.grpc.client.hystrix;

import java.util.concurrent.ExecutionException;

import com.google.protobuf.Message;
import com.quancheng.saluki.core.common.Constants;
import com.quancheng.saluki.core.common.GrpcURL;
import com.quancheng.saluki.core.common.RpcContext;
import com.quancheng.saluki.core.grpc.client.failover.GrpcClientCall;
import com.quancheng.saluki.core.grpc.exception.RpcErrorMsgConstant;
import com.quancheng.saluki.core.grpc.exception.RpcServiceException;

import io.grpc.MethodDescriptor;

/**
 * @author liushiming 2017年4月26日 下午5:56:29
 * @version $Id: GrpcBlockingUnaryCommand.java, v 0.0.1 2017年4月26日 下午5:56:29 liushiming
 */
public class GrpcBlockingUnaryCommand extends GrpcHystrixCommand {

    private final GrpcClientCall                 grpcAsyncCall;

    private final Message                            request;

    private final MethodDescriptor<Message, Message> methodDesc;

    public GrpcBlockingUnaryCommand(GrpcClientCall grpcAsyncCall, GrpcURL refUrl,
                                    MethodDescriptor<Message, Message> methodDesc, Message request){
        super(refUrl, methodDesc);
        this.grpcAsyncCall = grpcAsyncCall;
        this.methodDesc = methodDesc;
        this.request = request;

    }

    @Override
    protected Message run() throws Exception {
        try {
            return grpcAsyncCall.unaryFuture(request, methodDesc).get();
        } catch (InterruptedException | ExecutionException e) {
            RpcContext.getContext().setAttachment(Constants.REMOTE_ADDRESS,
                                                  String.valueOf(grpcAsyncCall.getAffinity().get(GrpcClientCall.GRPC_CURRENT_ADDR_KEY)));
            RpcServiceException rpcService = new RpcServiceException(e, RpcErrorMsgConstant.BIZ_DEFAULT_EXCEPTION);
            throw rpcService;
        }
    }

}
