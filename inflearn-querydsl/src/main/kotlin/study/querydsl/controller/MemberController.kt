package study.querydsl.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import study.querydsl.dto.MemberSearchCondition
import study.querydsl.dto.MemberTeamDto
import study.querydsl.repository.MemberJpaRepository

@RestController
class MemberController(
    private val memberJpaRepository: MemberJpaRepository,
) {

    @GetMapping("/v1/members")
    fun searchMemberV1(condition: MemberSearchCondition): List<MemberTeamDto> {
        return memberJpaRepository.search(condition = condition)
    }
}
