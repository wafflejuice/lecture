package hello.jdbc.service

import hello.jdbc.connection.ConnectionConst.PASSWORD
import hello.jdbc.connection.ConnectionConst.URL
import hello.jdbc.connection.ConnectionConst.USERNAME
import hello.jdbc.domain.Member
import hello.jdbc.repository.MemberRepositoryV3
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

/**
 * 트랜잭션 - @Transactional AOP
 */
@SpringBootTest
internal class MemberServiceV3_3Test(
    @Autowired private val memberRepository: MemberRepositoryV3,
    @Autowired private val memberService: MemberServiceV3_3,
) {
    private val MEMBER_A = "memberA"
    private val MEMBER_B = "memberB"
    private val MEMBER_EX = "ex"
    
    private val logger = LoggerFactory.getLogger(this::class.java)

    @TestConfiguration
    class TestConfig {
        @Bean
        fun dataSource(): DataSource =
            DriverManagerDataSource(URL, USERNAME, PASSWORD)

        @Bean
        fun transactionManager(): PlatformTransactionManager =
            DataSourceTransactionManager(dataSource())

        @Bean
        fun memberRepositoryV3(): MemberRepositoryV3 =
            MemberRepositoryV3(dataSource())

        @Bean
        fun memberServiceV3_3(): MemberServiceV3_3 =
            MemberServiceV3_3(memberRepository = memberRepositoryV3())
    }

    @AfterEach
    fun after() {
        memberRepository.delete(memberId = MEMBER_A)
        memberRepository.delete(memberId = MEMBER_B)
        memberRepository.delete(memberId = MEMBER_EX)
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
