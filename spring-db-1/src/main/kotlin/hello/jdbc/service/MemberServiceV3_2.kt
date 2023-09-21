package hello.jdbc.service

import hello.jdbc.domain.Member
import hello.jdbc.repository.MemberRepositoryV3
import org.slf4j.LoggerFactory
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.sql.SQLException

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
class MemberServiceV3_2(
    private val transactionManager: PlatformTransactionManager,
    private val memberRepository: MemberRepositoryV3,
) {
    private val transactionTemplate = TransactionTemplate(transactionManager)

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun accountTransfer(fromId: String, toId: String, money: Int) {
        transactionTemplate.executeWithoutResult {
            try {
                bizLogic(fromId = fromId, toId = toId, money = money)
            } catch (e: SQLException) {
                error(e)
            }
        }
    }

    private fun bizLogic(fromId: String, toId: String, money: Int) {
        val fromMember = memberRepository.findById(memberId = fromId)
        val toMember = memberRepository.findById(memberId = toId)

        memberRepository.update(memberId = fromId, money = fromMember.money - money)
        validation(toMember = toMember)
        memberRepository.update(memberId = toId, money = toMember.money + money)
    }

    private fun validation(toMember: Member) {
        if (toMember.memberId == "ex") {
            error("이체중 예외 발생")
        }
    }
}
