package com.example.base.model.network.errors

enum class ServerCode(
    val intCode: Int
) {
    UNKNOWN_CODE(-2),
    NETWORK_ERROR(-1),
    SUCCESS(0),
    INVALID_USERNAME_OR_PASSWORD(1),
    ACCESS_DENIED(2),
    METHOD_NOT_FOUND(3),
    INVALID_PARAMETERS_FORMAT(4),
    INTERNAL_ERROR(5),
    INVALID_HTTP_METHOD(6),
    USERNAME_ALREADY_EXISTS(7),
    OBJECT_NOT_FOUND(8),
    OBJECT_ALREADY_EXISTS(9),
}