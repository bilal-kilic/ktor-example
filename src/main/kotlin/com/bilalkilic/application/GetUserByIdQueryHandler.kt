package com.bilalkilic.application

import com.couchbase.client.java.ReactiveCollection
import com.bilalkilic.domain.User
import com.trendyol.kediatr.AsyncQueryHandler
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.koin.core.KoinComponent
import org.koin.core.inject
import reactor.util.retry.Retry
import java.time.Duration

class GetUserByIdQueryHandler : AsyncQueryHandler<GetUserByIdQuery, User>, KoinComponent {
    private val collection by inject<ReactiveCollection>()

    override suspend fun handleAsync(query: GetUserByIdQuery): User {
        val retrySpec = Retry.backoff(2L, Duration.ofMillis(50))

        return collection.get(query.id)
            .retryWhen(retrySpec)
            .doOnError { println("An error occurred while fetching a document. id:${query.id}") }
            .map { it.contentAs(User::class.java) }
            .awaitFirstOrNull() ?: throw Exception("user.not.found")
    }
}