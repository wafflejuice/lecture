package hello.jdbc.repository

import hello.jdbc.domain.Member
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory

internal class MemberRepositoryV0Test {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val memberRepositoryV0 = MemberRepositoryV0()

    @Test
    fun crud() {
        // save
        val member = Member("memberV100", 10_000)
        memberRepositoryV0.save(member)

        // findById
        val foundMember = memberRepositoryV0.findById(memberId = member.memberId)
        logger.info("foundMember=$foundMember")
        assertThat(foundMember == member)

        // update: money: 10_000 -> 20_000
        memberRepositoryV0.update(memberId = member.memberId, money = 20_000)
        val updatedMember = memberRepositoryV0.findById(memberId = member.memberId)
        assertThat(updatedMember.money == 20_000)

        // delete
        memberRepositoryV0.delete(memberId = member.memberId)
        assertThrows<NoSuchElementException> { memberRepositoryV0.findById(memberId = member.memberId) }
    }
}
