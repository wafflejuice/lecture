package hello.jdbc.repository

import hello.jdbc.domain.Member
import org.junit.jupiter.api.Test

internal class MemberRepositoryV0Test {

    private val memberRepositoryV0 = MemberRepositoryV0()

    @Test
    fun crud() {
        val member = Member("memberV1", 10_000)
        memberRepositoryV0.save(member)
    }
}
