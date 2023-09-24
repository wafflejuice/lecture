package hello.jdbc.service

import hello.jdbc.domain.Member
import hello.jdbc.repository.MemberRepository
import hello.jdbc.repository.MemberRepositoryV5
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import org.springframework.aop.support.AopUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import javax.sql.DataSource

/**
 * 트랜잭션 - DataSource, TransactionManager 자동 등록
 */
@SpringBootTest
internal class MemberServiceV4Test(
    @Autowired private val memberRepository: MemberRepository,
    @Autowired private val memberService: MemberServiceV4,
) {
    private val MEMBER_A = "memberA"
    private val MEMBER_B = "memberB"
    private val MEMBER_EX = "ex"

    private val logger = LoggerFactory.getLogger(this::class.java)

    @TestConfiguration
    class TestConfig(
        private val dataSource: DataSource,
    ) {
        @Bean
        fun memberRepository(): MemberRepository =
//            MemberRepositoryV4_2(dataSource)
            MemberRepositoryV5(dataSource)

        @Bean
        fun memberServiceV4(memberRepository: MemberRepository): MemberServiceV4 =
            MemberServiceV4(memberRepository = memberRepository)
    }

    @AfterEach
    fun after() {
        memberRepository.delete(memberId = MEMBER_A)
        memberRepository.delete(memberId = MEMBER_B)
        memberRepository.delete(memberId = MEMBER_EX)
    }

    @Test
    fun aopCheck() {
        logger.info("memberService class=${memberService.javaClass}")
        logger.info("memberRepository class=${memberRepository.javaClass}")

        assertTrue(AopUtils.isAopProxy(memberService))
        assertFalse(AopUtils.isAopProxy(memberRepository))
    }

    @Test
    fun `정상 이체`() {
        // given
        val memberA = Member(memberId = MEMBER_A, money = 10_000)
        val memberB = Member(memberId = MEMBER_B, money = 10_000)

        memberRepository.save(memberA)
        memberRepository.save(memberB)

        // when
        logger.info("START TX")
        memberService.accountTransfer(fromId = memberA.memberId, toId = memberB.memberId, money = 2_000)
        logger.info("END TX")

        // then
        val foundMemberA = memberRepository.findById(memberA.memberId)
        val foundMemberB = memberRepository.findById(memberB.memberId)

        assertEquals(8_000, foundMemberA.money)
        assertEquals(12_000, foundMemberB.money)
    }

    @Test
    fun `이체중 예외 발생`() {
        // given
        val memberA = Member(memberId = MEMBER_A, money = 10_000)
        val memberEx = Member(memberId = MEMBER_EX, money = 10_000)

        memberRepository.save(memberA)
        memberRepository.save(memberEx)

        // when
        assertThrows<IllegalStateException> {
            memberService.accountTransfer(fromId = memberA.memberId, toId = memberEx.memberId, money = 2_000)
        }

        // then
        val foundMemberA = memberRepository.findById(memberA.memberId)
        val foundMemberB = memberRepository.findById(memberEx.memberId)

        assertEquals(10_000, foundMemberA.money)
        assertEquals(10_000, foundMemberB.money)
    }
}
