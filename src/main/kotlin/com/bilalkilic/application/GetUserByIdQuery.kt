package com.bilalkilic.application

import com.bilalkilic.domain.User
import com.trendyol.kediatr.Query

data class GetUserByIdQuery(val id: String) : Query<User>

