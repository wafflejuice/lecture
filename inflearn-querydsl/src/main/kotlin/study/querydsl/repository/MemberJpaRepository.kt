package study.querydsl.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import org.springframework.util.StringUtils
import study.querydsl.dto.MemberSearchCondition
import study.querydsl.dto.MemberTeamDto
import study.querydsl.dto.QMemberTeamDto
import study.querydsl.entity.Member
import study.querydsl.entity.QMember.member
import study.querydsl.entity.QTeam.team

@Repository
class MemberJpaRepository(
    private val em: EntityManager,
    private val queryFactory: JPAQueryFactory,
) {
    fun save(member: Member) {
        em.persist(member)
    }

    fun findById(id: Long): Member? {
        return em.find(Member::class.java, id)
    }

    fun findAll(): List<Member> {
        return em.createQuery("select m from Member m", Member::class.java)
            .resultList
    }

    fun findAll_Querydsl(): List<Member> {
        return queryFactory
            .selectFrom(member)
            .fetch()
    }

    fun findByUsername(username: String): List<Member> {
        return em.createQuery("select m from Member m where m.username = :username", Member::class.java)
            .setParameter("username", username)
            .resultList
    }

    fun findByUsername_Querydsl(username: String): List<Member> {
        return queryFactory
            .selectFrom(member)
            .where(member.username.eq(username))
            .fetch()
    }

    fun searchByBuilder(condition: MemberSearchCondition): List<MemberTeamDto> {
        val builder = BooleanBuilder()
        if (StringUtils.hasText(condition.username)) {
            builder.and(member.username.eq(condition.username))
        }
        if (StringUtils.hasText(condition.teamName)) {
            builder.and(team.name.eq(condition.teamName))
        }
        if (condition.ageGoe != null) {
            builder.and(member.age.goe(condition.ageGoe))
        }
        if (condition.ageLoe != null) {
            builder.and(member.age.loe(condition.ageLoe))
        }
        return queryFactory
            .select(
                QMemberTeamDto(
                    member.id.`as`("memberId"),
                    member.username,
                    member.age,
                    team.id.`as`("teamId"),
                    team.name.`as`("teamName"),
                )
            )
            .from(member)
            .leftJoin(member.team, team)
            .where(builder)
            .fetch()
    }
}
