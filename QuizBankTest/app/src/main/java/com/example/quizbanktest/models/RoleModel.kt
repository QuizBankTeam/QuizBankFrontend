package com.example.quizbanktest.models

import java.io.Serializable


data class RoleModel(
    val  entityId:String,
    val  permission: String
): Serializable