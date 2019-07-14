package com.example.base.model.network.errors

class ServerErrorException(
    val serverError: ServerError
) : Exception()
