package com.bilalkilic.infra.modules

import com.couchbase.client.core.cnc.DefaultEventBus
import com.couchbase.client.core.cnc.tracing.ThresholdRequestTracer
import com.couchbase.client.core.env.CompressionConfig
import com.couchbase.client.core.env.TimeoutConfig
import com.couchbase.client.java.Cluster
import com.couchbase.client.java.ClusterOptions
import com.couchbase.client.java.codec.JacksonJsonSerializer
import com.couchbase.client.java.env.ClusterEnvironment
import com.couchbase.client.java.json.JsonValueModule
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.koin.dsl.module
import reactor.core.scheduler.Schedulers
import java.time.Duration
import kotlin.math.max

val couchbaseModule = module {
    single(createdAtStart = true) {
        val objectMapper = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModules(KotlinModule(), JsonValueModule())


        val schedulerPoolSize = max(Schedulers.DEFAULT_POOL_SIZE, 8)
        val cbScheduler = Schedulers.newParallel("cb-comp", schedulerPoolSize, true)
        val cbEventBus = DefaultEventBus.create(cbScheduler)
        cbEventBus.start().block()
        val thresholdRequestTracer = ThresholdRequestTracer.builder(cbEventBus)
            .queryThreshold(Duration.ofMillis(200))
            .kvThreshold(Duration.ofMillis(200))
            .build()
        thresholdRequestTracer.start().block()

        val env = ClusterEnvironment
            .builder()
            .scheduler(cbScheduler)
            .eventBus(cbEventBus)
            .compressionConfig(CompressionConfig.builder().enable(true))
            .timeoutConfig(
                TimeoutConfig
                    .builder()
                    .kvTimeout(Duration.ofMillis(500))
                    .connectTimeout(Duration.ofMillis(10000))
                    .queryTimeout(Duration.ofMillis(500))
                    .viewTimeout(Duration.ofMillis(500))
            )
            .requestTracer(thresholdRequestTracer)
            .jsonSerializer(JacksonJsonSerializer.create(objectMapper))
            .build()
        val clusterOptions = ClusterOptions.clusterOptions("User", "123456")
            .environment(env)

        val cluster = Cluster.connect("localhost", clusterOptions)
        cluster.waitUntilReady(Duration.ofMinutes(1))
        cluster.bucket("User").also {
            it.waitUntilReady(Duration.ofSeconds(30))
        }.defaultCollection()
            .reactive()
    }
}