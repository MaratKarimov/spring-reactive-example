package ru.maratk.spring.reactive.example.beans

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version

data class Book(@Id val id: Int
                , @Version val version: Int
                , val author: String
                , val title: String)