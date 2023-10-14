package study.querydsl.repository

import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import study.querydsl.dto.MemberSearchCondition
import study.querydsl.entity.Member
import study.querydsl.entity.Team

@SpringBootTest
@Transactional
internal class MemberJpaRepositoryTest {

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var memberJpaRepository: MemberJpaRepository

    @Test
    fun basicTest() {
        val member = Member.new(username = "member1", age = 10)
        memberJpaRepository.save(member)

        val foundMember = memberJpaRepository.findById(member.id)
        assertEquals(member, foundMember)

        val result1 = memberJpaRepository.findAll()
        assertThat(result1).containsExactly(member)

        val result2 = memberJpaRepository.findByUsername("member1")
        assertThat(result2).containsExactly(member)
    }

    @Test
    fun basicQuerydslTest() {
        val member = Member.new(username = "member1", age = 10)
        memberJpaRepository.save(member)

        val foundMember = memberJpaRepository.findById(member.id)
        assertEquals(member, foundMember)

        val result1 = memberJpaRepository.findAll_Querydsl()
        assertThat(result1).containsExactly(member)

        val result2 = memberJpaRepository.findByUsername_Querydsl("member1")
        assertThat(result2).containsExactly(member)
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

        val result = memberJpaRepository.searchByBuilder(condition = condition)

        assertEquals("member4", result[0].username)

        val condition2 = MemberSearchCondition(
            username = null,
            teamName = "teamB",
            ageGoe = null,
            ageLoe = null,
        )

        val result2 = memberJpaRepository.searchByBuilder(condition = condition2)

        assertIterableEquals(listOf("member3", "member4"), result2.map { it.username })
    }

    @Test
    fun searchTest2() {
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

        val result = memberJpaRepository.search(condition = condition)

        assertEquals("member4", result[0].username)

        val condition2 = MemberSearchCondition(
            username = null,
            teamName = "teamB",
            ageGoe = null,
            ageLoe = null,
        )

        val result2 = memberJpaRepository.search(condition = condition2)

        assertIterableEquals(listOf("member3", "member4"), result2.map { it.username })
    }
}
