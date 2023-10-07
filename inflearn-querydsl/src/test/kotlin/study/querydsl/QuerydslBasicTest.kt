package study.querydsl

import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.JPAExpressions.select
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.PersistenceUnit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import study.querydsl.dto.MemberDto
import study.querydsl.dto.MemberDtoA
import study.querydsl.dto.QMemberDto
import study.querydsl.dto.UserDto
import study.querydsl.dto.UserDtoA
import study.querydsl.entity.Member
import study.querydsl.entity.QMember
import study.querydsl.entity.QMember.member
import study.querydsl.entity.QTeam.team
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

//        val fetchOne = queryFactory
//            .selectFrom(member)
//            .fetchOne()

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

    @Test
    fun paging1() {
        val result = queryFactory
            .selectFrom(member)
            .orderBy(member.username.desc())
            .offset(1)
            .limit(2)
            .fetch()

        assertEquals(2, result.size)
    }

    @Test
    fun paging2() {
        val queryResults = queryFactory
            .selectFrom(member)
            .orderBy(member.username.desc())
            .offset(1)
            .limit(2)
            .fetchResults()

        assertEquals(4, queryResults.total)
        assertEquals(2, queryResults.limit)
        assertEquals(1, queryResults.offset)
        assertEquals(2, queryResults.results.size)
    }

    @Test
    fun aggregation() {
        val result = queryFactory
            .select(
                member.count(),
                member.age.sum(),
                member.age.avg(),
                member.age.max(),
                member.age.min(),
            )
            .from(member)
            .fetch()

        val tuple = result[0]
        assertEquals(4, tuple.get(member.count()))
        assertEquals(100, tuple.get(member.age.sum()))
        assertEquals(25.0, tuple.get(member.age.avg()))
        assertEquals(40, tuple.get(member.age.max()))
        assertEquals(10, tuple.get(member.age.min()))
    }

    /**
     * 팀의 이름과 각 팀의 평균 연령을 구해라.
     */
    @Test
    fun group() {
        val result = queryFactory
            .select(team.name, member.age.avg())
            .from(member)
            .join(member.team, team)
            .groupBy(team.name)
            .fetch()

        val teamA = result[0]
        val teamB = result[1]

        assertEquals("teamA", teamA.get(team.name))
        assertEquals(15.0, teamA.get(member.age.avg()))

        assertEquals("teamB", teamB.get(team.name))
        assertEquals(35.0, teamB.get(member.age.avg()))
    }

    /**
     * 팀 A에 소속된 모든 회원
     */
    @Test
    fun join() {
        val result = queryFactory
            .selectFrom(member)
            .join(member.team, team)
            .where(team.name.eq("teamA"))
            .fetch()

        assertThat(result)
            .extracting("username")
            .containsExactly("member1", "member2")
    }

    /**
     * 세타 조인
     * 회원의 이름이 팀 이름과 같은 회원 조회
     */
    @Test
    fun theta_join() {
        em.persist(Member.new(username = "teamA"))
        em.persist(Member.new(username = "teamB"))
        em.persist(Member.new(username = "teamC"))

        val result = queryFactory
            .select(member)
            .from(member, team)
            .where(member.username.eq(team.name))
            .fetch()

        assertThat(result)
            .extracting("username")
            .containsExactly("teamA", "teamB")
    }

    /**
     * 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * JPQL: select m, t from Member m left join m.team t on t.name = 'teamA'
     */
    @Test
    fun join_on_filtering() {
        val result = queryFactory
            .select(member, team)
            .from(member)
            .leftJoin(member.team, team).on(team.name.eq("teamA"))
            .fetch()

        result.forEach { tuple ->
            println("tuple = $tuple")
        }
    }

    /**
     * 연관관계 없는 엔티티 외부 조인
     * 회원의 이름이 팀 이름과 같은 회원 외부 조인
     */
    @Test
    fun join_on_no_relation() {
        em.persist(Member.new(username = "teamA"))
        em.persist(Member.new(username = "teamB"))
        em.persist(Member.new(username = "teamC"))

        val result = queryFactory
            .select(member, team)
            .from(member)
            .leftJoin(team).on(member.username.eq(team.name))
            .fetch()

        result.forEach { tuple ->
            println("tuple = $tuple")
        }
    }

    @PersistenceUnit
    private lateinit var emf: EntityManagerFactory

    @Test
    fun noFetchJoin() {
        em.flush()
        em.clear()

        val foundMember = queryFactory
            .selectFrom(member)
            .where(member.username.eq("member1"))
            .fetchOne()!!

        val loaded = emf.persistenceUnitUtil.isLoaded(foundMember.team)
        assertFalse(loaded)
    }

    @Test
    fun useFetchJoin() {
        em.flush()
        em.clear()

        val foundMember = queryFactory
            .selectFrom(member)
            .join(member.team, team).fetchJoin()
            .where(member.username.eq("member1"))
            .fetchOne()!!

        val loaded = emf.persistenceUnitUtil.isLoaded(foundMember.team)
        assertTrue(loaded)
    }

    /**
     * 나이가 가장 많은 회원을 조회
     */
    @Test
    fun subQuerEq() {
        val memberSub = QMember("memberSub")

        val result = queryFactory
            .selectFrom(member)
            .where(
                member.age.eq(
                    select(memberSub.age.max())
                        .from(memberSub)
                )
            )
            .fetch()

        assertThat(result).extracting("age").containsExactly(40)
    }

    /**
     * 나이가 평균 이상인 회원을 조회
     */
    @Test
    fun subQueryGoe() {
        val memberSub = QMember("memberSub")

        val result = queryFactory
            .selectFrom(member)
            .where(
                member.age.goe(
                    select(memberSub.age.avg())
                        .from(memberSub)
                )
            )
            .fetch()

        assertThat(result).extracting("age").containsExactly(30, 40)
    }

    @Test
    fun subQueryIn() {
        val memberSub = QMember("memberSub")

        val result = queryFactory
            .selectFrom(member)
            .where(
                member.age.`in`(
                    select(memberSub.age)
                        .from(memberSub)
                        .where(memberSub.age.gt(10))
                )
            )
            .fetch()

        assertThat(result).extracting("age").containsExactly(20, 30, 40)
    }

    @Test
    fun selectSubQuery() {
        val memberSub = QMember("memberSub")

        val result = queryFactory
            .select(
                member.username,
                select(memberSub.age.avg())
                    .from(memberSub)
            )
            .from(member)
            .fetch()

        result.forEach { tuple ->
            println("tuple = $tuple")
        }
    }

    @Test
    fun baseCase() {
        val result = queryFactory
            .select(
                member.age
                    .`when`(10).then("열살")
                    .`when`(20).then("스무살")
                    .otherwise("기타")
            )
            .from(member)
            .fetch()

        result.forEach { s ->
            println("s = $s")
        }
    }

    @Test
    fun complexCase() {
        val result = queryFactory
            .select(
                CaseBuilder()
                    .`when`(member.age.between(0, 20)).then("0~20살")
                    .`when`(member.age.between(21, 30)).then("21~30살")
                    .otherwise("기타")
            )
            .from(member)
            .fetch()

        result.forEach { s ->
            println("s = $s")
        }
    }

    @Test
    fun constant() {
        val result = queryFactory
            .select(member.username, Expressions.constant("A"))
            .from(member)
            .fetch()

        result.forEach { tuple ->
            println("tuple = $tuple")
        }
    }

    @Test
    fun concat() {
        val result = queryFactory
            .select(member.username.concat("_").concat(member.age.stringValue()))
            .from(member)
            .fetch()

        result.forEach { tuple ->
            println("tuple = $tuple")
        }
    }

    @Test
    fun simpleProjection() {
        val result = queryFactory
            .select(member.username)
            .from(member)
            .fetch()

        result.forEach { s ->
            println("s = $s")
        }
    }

    @Test
    fun tupleProjection() {
        val result = queryFactory
            .select(member.username, member.age)
            .from(member)
            .fetch()

        result.forEach { tuple ->
            val username = tuple.get(member.username)
            val age = tuple.get(member.age)
            println("username = $username")
            println("age = $age")
        }
    }

    @Test
    fun findDtoByJPQL() {
        val result = em.createQuery(
            "select new study.querydsl.dto.MemberDto(m.username, m.age) from Member m",
            MemberDto::class.java
        )
            .resultList

        result.forEach { memberDto ->
            println("memberDto = $memberDto")
        }
    }

    @Test
    fun findDtoBySetter() {
        val result = queryFactory
            .select(
                Projections.bean(
                    MemberDtoA::class.java,
                    member.username,
                    member.age,
                )
            )
            .from(member)
            .fetch()

        result.forEach { memberDto ->
            println("memberDto = $memberDto")
        }
    }

    @Test
    fun findDtoByField() {
        val result = queryFactory
            .select(
                Projections.fields(
                    MemberDtoA::class.java,
                    member.username,
                    member.age,
                )
            )
            .from(member)
            .fetch()

        result.forEach { memberDto ->
            println("memberDto = $memberDto")
        }
    }

    @Test
    fun findDtoByConstructor() {
        val result = queryFactory
            .select(
                Projections.constructor(
                    MemberDto::class.java,
                    member.username,
                    member.age,
                )
            )
            .from(member)
            .fetch()

        result.forEach { memberDto ->
            println("memberDto = $memberDto")
        }
    }

    @Test
    fun findUserDtoByField() {
        val memberSub = QMember("memberSub")
        val result = queryFactory
            .select(
                Projections.fields(
                    UserDtoA::class.java,
                    member.username.`as`("name"),
                    ExpressionUtils.`as`(
                        JPAExpressions
                            .select(memberSub.age.max())
                            .from(memberSub),
                        "age",
                    )
                )
            )
            .from(member)
            .fetch()

        result.forEach { userDto ->
            println("userDto = $userDto")
        }
    }

    @Test
    fun findUserDtoByConstructor() {
        val result = queryFactory
            .select(
                Projections.constructor(
                    UserDto::class.java,
                    member.username,
                    member.age,
                )
            )
            .from(member)
            .fetch()

        result.forEach { userDto ->
            println("userDto = $userDto")
        }
    }

    @Test
    fun findDtoByQueryProjection() {
        val result = queryFactory
            .select(QMemberDto(member.username, member.age))
            .from(member)
            .fetch()

        result.forEach { memberDto ->
            println("memberDto = $memberDto")
        }
    }

}
