package study.querydsl.repository

import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import study.querydsl.entity.Member

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
}
