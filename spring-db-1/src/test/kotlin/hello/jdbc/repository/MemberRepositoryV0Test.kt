package hello.jdbc.repository

import hello.jdbc.domain.Member
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal class MemberRepositoryV0Test {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val memberRepositoryV0 = MemberRepositoryV0()

    @Test
    fun crud() {
        // save
        val member = Member("memberV3", 10_000)
        memberRepositoryV0.save(member)

        // findById
        val foundMember = memberRepositoryV0.findById(memberId = member.memberId)
        logger.info("foundMember=$foundMember")
        assertThat(foundMember == member)
    }
}
