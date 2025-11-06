package com.calyrsoft.ucbp1.features.logs.data.datasource

import com.develoop.logs.LogRequest
import com.develoop.logs.LogResponse
import com.develoop.logs.LogServiceGrpcKt
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class LogsRemoteDataSource(
    host: String,
    port: Int,
    private val usePlaintext: Boolean = true
) {
    private val channel: ManagedChannel = ManagedChannelBuilder
        .forAddress(host, port)
        .apply {
            if(usePlaintext) usePlaintext()
        }
        .build()


    private val stub = LogServiceGrpcKt.LogServiceCoroutineStub(channel)


    suspend fun send(request: LogRequest): LogResponse = withContext(Dispatchers.IO) {
        stub.send(request)
    }


    fun shutdown() {
        channel.shutdownNow()
    }
}
