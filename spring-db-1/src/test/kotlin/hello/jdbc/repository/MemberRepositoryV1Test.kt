package hello.jdbc.repository

import com.zaxxer.hikari.HikariDataSource
import hello.jdbc.connection.ConnectionConst.PASSWORD
import hello.jdbc.connection.ConnectionConst.URL
import hello.jdbc.connection.ConnectionConst.USERNAME
import hello.jdbc.domain.Member
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory

internal class MemberRepositoryV1Test {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private lateinit var memberRepositoryV1: MemberRepositoryV1

    @BeforeEach
    fun beforeEach() {
        // DriverManager - 항상 새로운 커넥션 획득
//        val dataSource = DriverManagerDataSource(URL, USERNAME, PASSWORD)

        // connection pooling
        val dataSource = HikariDataSource()
        dataSource.jdbcUrl = URL
        dataSource.username = USERNAME
        dataSource.password = PASSWORD

        memberRepositoryV1 = MemberRepositoryV1(dataSource = dataSource)
    }

    @Test
    fun crud() {
        // save
        val member = Member("memberV100", 10_000)
        memberRepositoryV1.save(member)

        // findById
        val foundMember = memberRepositoryV1.findById(memberId = member.memberId)
        logger.info("foundMember=$foundMember")
        assertThat(foundMember == member)

        // update: money: 10_000 -> 20_000
        memberRepositoryV1.update(memberId = member.memberId, money = 20_000)
        val updatedMember = memberRepositoryV1.findById(memberId = member.memberId)
        assertThat(updatedMember.money == 20_000)

        // delete
        memberRepositoryV1.delete(memberId = member.memberId)
        assertThrows<NoSuchElementException> { memberRepositoryV1.findById(memberId = member.memberId) }

        Thread.sleep(1000)
    }
}
