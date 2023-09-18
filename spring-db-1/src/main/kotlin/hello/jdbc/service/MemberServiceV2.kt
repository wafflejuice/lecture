package hello.jdbc.service

import hello.jdbc.domain.Member
import hello.jdbc.repository.MemberRepositoryV2
import org.slf4j.LoggerFactory
import java.sql.Connection
import javax.sql.DataSource

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
class MemberServiceV2(
    private val dataSource: DataSource,
    private val memberRepository: MemberRepositoryV2,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun accountTransfer(fromId: String, toId: String, money: Int) {
        val con = dataSource.connection

        try {
            con.autoCommit = false // start a transaction

            bizLogic(con = con, fromId = fromId, toId = toId, money = money)

            con.commit() // commit for success
        } catch (e: Exception) {
            con.rollback() // rollback for failure
            error(e)
        } finally {
            release(con = con)
        }
    }

    private fun bizLogic(con: Connection, fromId: String, toId: String, money: Int) {
        val fromMember = memberRepository.findById(con = con, memberId = fromId)
        val toMember = memberRepository.findById(con = con, memberId = toId)

        memberRepository.update(con = con, memberId = fromId, money = fromMember.money - money)
        validation(toMember = toMember)
        memberRepository.update(con = con, memberId = toId, money = toMember.money + money)
    }

    private fun validation(toMember: Member) {
        if (toMember.memberId == "ex") {
            error("이체중 예외 발생")
        }
    }

    private fun release(con: Connection) {
        if (con != null) {
            try {
                con.autoCommit = true // set autoCommit before releasing the connection to the connection pool
                con.close()
            } catch (e: Exception) {
                logger.info("error message", e)
            }
        }
    }
}
