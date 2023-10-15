package study.querydsl.controller

import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import study.querydsl.entity.Member
import study.querydsl.entity.Team

@Profile("local")
@Component
class InitMember(
    private val initMemberService: InitMemberService,
) {
    @PostConstruct
    fun init() {
        initMemberService.init()
    }

    companion object {
        @Component
        class InitMemberService(
            @PersistenceContext
            private val em: EntityManager,
        ) {

            @Transactional
            fun init() {
                val teamA = Team.new(name = "teamA")
                val teamB = Team.new(name = "teamB")
                em.persist(teamA)
                em.persist(teamB)

                (0 until 100).forEach {
                    val selectedTeam = if (it % 2 == 0) teamA else teamB
                    em.persist(Member.new(username = "member$it", age = it, team = selectedTeam))
                }
            }
        }
    }
}

