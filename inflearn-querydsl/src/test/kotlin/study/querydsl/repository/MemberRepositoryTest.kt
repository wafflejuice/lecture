package study.querydsl.repository

import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import study.querydsl.entity.Member

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
        Assertions.assertEquals(member, foundMember)

        val result1 = memberRepository.findAll()
        org.assertj.core.api.Assertions.assertThat(result1).containsExactly(member)

        val result2 = memberRepository.findByUsername("member1")
        org.assertj.core.api.Assertions.assertThat(result2).containsExactly(member)
    }

}
