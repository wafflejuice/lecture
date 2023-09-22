package hello.jdbc.service

import hello.jdbc.domain.Member
import hello.jdbc.repository.MemberRepositoryV3
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

/**
 * 트랜잭션 - @Transactional AOP
 */
open class MemberServiceV3_3(
    private val memberRepository: MemberRepositoryV3,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    open fun accountTransfer(fromId: String, toId: String, money: Int) {
        bizLogic(fromId = fromId, toId = toId, money = money)
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
