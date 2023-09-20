package hello.jdbc.service

import hello.jdbc.domain.Member
import hello.jdbc.repository.MemberRepositoryV3
import org.slf4j.LoggerFactory
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
class MemberServiceV3_1(
    private val transactionManager: PlatformTransactionManager,
    private val memberRepository: MemberRepositoryV3,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun accountTransfer(fromId: String, toId: String, money: Int) {
        val status = transactionManager.getTransaction(DefaultTransactionDefinition())

        try {
            bizLogic(fromId = fromId, toId = toId, money = money)
            transactionManager.commit(status) // commit for success
        } catch (e: Exception) {
            transactionManager.rollback(status) // rollback for failure
            error(e)
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
