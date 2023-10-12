package study.querydsl.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import study.querydsl.entity.Member
import study.querydsl.entity.QMember.member

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
}
