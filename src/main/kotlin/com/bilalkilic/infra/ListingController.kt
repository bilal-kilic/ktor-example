package com.bilalkilic.infra

import com.bilalkilic.application.GetUserByIdQuery
import com.bilalkilic.domain.User
import com.trendyol.kediatr.CommandBus
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject

fun Route.userController() {
    val commandBus by inject<CommandBus>()

    get("/users/{id}") {
        val id = call.parameters.getOrFail("id")
        val user = commandBus.executeQueryAsync(GetUserByIdQuery(id))
        call.respondText(Json.encodeToString(User.serializer(), user), ContentType.Application.Json)
    }
}