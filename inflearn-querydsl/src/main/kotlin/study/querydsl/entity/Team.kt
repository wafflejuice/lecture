package study.querydsl.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Team
private constructor(
    name: String,
) {
    @Id
    @GeneratedValue
    @Column(name = "team_id")
    val id: Long = 0L

    var name: String = name
        protected set

    @OneToMany(mappedBy = "team")
    val members: MutableList<Member> = mutableListOf()

    override fun toString(): String {
        return "Team(id=$id, name=$name)"
    }

    companion object {
        fun new(name: String): Team =
            Team(name = name)
    }
}
