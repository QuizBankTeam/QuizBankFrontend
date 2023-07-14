package com.example.yiquizapp.models

import java.io.Serializable


data class AccountModel(
    val _id: String,
    val username:String,
    val email : String,
    val password:String,
    val preference : ArrayList<String>,
    val createdDate : String,
    val roles : ArrayList<RoleModel>,
    val introduction : String,
    val avatar : String,
    val group : ArrayList<String>,
    val status:Boolean,
    val questionRecords:ArrayList<String>
): Serializable