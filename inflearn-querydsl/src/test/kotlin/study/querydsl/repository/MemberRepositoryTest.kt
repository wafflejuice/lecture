package study.querydsl.repository

import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import study.querydsl.dto.MemberSearchCondition
import study.querydsl.entity.Member
import study.querydsl.entity.QMember.member
import study.querydsl.entity.Team

@SpringBootTest
@Transactional
internal class MemberRepositoryTest {

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    fun basicTest() {
        val member = Member.new(username = "member1", age = 10)
        memberRepository.save(member)

        val foundMember = memberRepository.findByIdOrNull(member.id)
        assertEquals(member, foundMember)

        val result1 = memberRepository.findAll()
        org.assertj.core.api.Assertions.assertThat(result1).containsExactly(member)

        val result2 = memberRepository.findByUsername("member1")
        org.assertj.core.api.Assertions.assertThat(result2).containsExactly(member)
    }

    @Test
    fun searchTest() {
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

        val condition = MemberSearchCondition(
            username = null,
            teamName = "teamB",
            ageGoe = 35,
            ageLoe = 40,
        )

        val result = memberRepository.search(condition = condition)

        assertEquals("member4", result[0].username)

        val condition2 = MemberSearchCondition(
            username = null,
            teamName = "teamB",
            ageGoe = null,
            ageLoe = null,
        )

        val result2 = memberRepository.search(condition = condition2)

        assertIterableEquals(listOf("member3", "member4"), result2.map { it.username })
    }

    @Test
    fun searchTestSimple() {
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

        val condition = MemberSearchCondition(
            username = null,
            teamName = null,
            ageGoe = null,
            ageLoe = null,
        )
        val pageRequest = PageRequest.of(0, 3)

        val result = memberRepository.searchPageSimple(condition = condition, pageable = pageRequest)

        assertEquals(3, result.size)
        assertIterableEquals(listOf("member1", "member2", "member3"), result.content.map { it.username })
    }

    @Test
    fun querydslPredicateExecutorTest() {
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

        val result = memberRepository.findAll(
            member.age.between(10, 40)
                .and(member.username.eq("member1"))
        )
        result.forEach { member -> println("member = $member") }
    }
}
