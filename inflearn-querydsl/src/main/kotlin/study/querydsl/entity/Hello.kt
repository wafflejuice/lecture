package study.querydsl.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Hello(
    @Id
    @GeneratedValue
    val id: Long = 0L,
)
