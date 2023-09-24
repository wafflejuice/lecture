package hello.jdbc.service

import hello.jdbc.domain.Member
import hello.jdbc.repository.MemberRepository
import org.springframework.transaction.annotation.Transactional

open class MemberServiceV4(
    private val memberRepository: MemberRepository,
) {
    
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
