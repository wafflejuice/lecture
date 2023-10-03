package study.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import study.querydsl.entity.Member
import study.querydsl.entity.QMember.member
import study.querydsl.entity.Team

@SpringBootTest
@Transactional
class QuerydslBasicTest {

    @Autowired
    private lateinit var em: EntityManager
    private lateinit var queryFactory: JPAQueryFactory

    @BeforeEach
    fun before() {
        queryFactory = JPAQueryFactory(em)

        val teamA = Team.new("teamA")
        val teamB = Team.new("teamB")

        em.persist(teamA)
        em.persist(teamB)

        val member1 = Member.new("member1", 10, teamA)
        val member2 = Member.new("member2", 20, teamA)

        val member3 = Member.new("member3", 30, teamB)
        val member4 = Member.new("member4", 40, teamB)

        em.persist(member1)
        em.persist(member2)
        em.persist(member3)
        em.persist(member4)
    }

    @Test
    fun startJPQL() {
        val qlString = "select m from Member m where m.username = :username"
        val foundMember = em.createQuery(qlString, Member::class.java)
            .setParameter("username", "member1")
            .singleResult

        assertEquals("member1", foundMember.username)
    }

    @Test
    fun startQuerydsl() {
        val foundMember = queryFactory
            .select(member)
            .from(member)
            .where(member.username.eq("member1")) // parameter binding
            .fetchOne()!!

        assertEquals("member1", foundMember.username)
    }

    @Test
    fun search() {
        val foundMember = queryFactory
            .selectFrom(member)
            .where(
                member.username.eq("member1")
                    .and(member.age.eq(10))
            )
            .fetchOne()!!

        assertEquals("member1", foundMember.username)
    }

    @Test
    fun searchAndParam() {
        val foundMember = queryFactory
            .selectFrom(member)
            .where(
                member.username.eq("member1"),
                member.age.eq(10),
            )
            .fetchOne()!!

        assertEquals("member1", foundMember.username)
    }

    @Test
    fun resultFetch() {
        val fetch = queryFactory
            .selectFrom(member)
            .fetch()

        val fetchOne = queryFactory
            .selectFrom(member)
            .fetchOne()

        val fetchFirst = queryFactory
            .selectFrom(member)
            .fetchFirst()

        val results = queryFactory
            .selectFrom(member)
            .fetchResults()

        results.total
        val content = results.results

        val total = queryFactory
            .selectFrom(member)
            .fetchCount()
    }

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순(desc)
     * 2. 회원 이름 올림차순(asc)
     * 단 2에서 회원 이름이 없으면 마지막에 출력 (nulls last)
     */
    @Test
    fun sort() {
        em.persist(Member.new(username = null, age = 100, team = null))
        em.persist(Member.new(username = "member5", age = 100, team = null))
        em.persist(Member.new(username = "member6", age = 100, team = null))

        val result = queryFactory
            .selectFrom(member)
            .where(member.age.eq(100))
            .orderBy(member.age.desc(), member.username.asc().nullsLast())
            .fetch()

        val member5 = result[0]
        val member6 = result[1]
        val memberNull = result[2]

        assertEquals("member5", member5.username)
        assertEquals("member6", member6.username)
        assertNull(memberNull.username)
    }
}
