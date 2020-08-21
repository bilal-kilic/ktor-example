package com.bilalkilic.infra.modules

import com.bilalkilic.application.GetUserByIdQuery
import com.trendyol.kediatr.CommandBusBuilder
import org.koin.dsl.module

val commandBusModule = module {
    single(createdAtStart = true) { CommandBusBuilder(GetUserByIdQuery::class.java).build() }
}