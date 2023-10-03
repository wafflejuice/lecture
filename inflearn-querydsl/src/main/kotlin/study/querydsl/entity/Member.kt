package study.querydsl.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Member
private constructor(
    username: String?,
    age: Int,
) {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    val id: Long = 0L

    var username: String? = username
        protected set

    var age: Int = age
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    lateinit var team: Team
        protected set

    fun changeTeam(team: Team) {
        this.team = team
        team.members += this
    }

    override fun toString(): String {
        return "id=$id, username=$username, age=$age"
    }

    companion object {
        fun new(
            username: String?,
            age: Int,
            team: Team?,
        ): Member {
            val member = Member(
                username = username,
                age = age,
            )
            if (team != null) {
                member.changeTeam(team = team)
            }

            return member
        }
    }
}
