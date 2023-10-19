package study.querydsl.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.util.StringUtils
import study.querydsl.dto.MemberSearchCondition
import study.querydsl.dto.MemberTeamDto
import study.querydsl.dto.QMemberTeamDto
import study.querydsl.entity.QMember.member
import study.querydsl.entity.QTeam.team

class MemberRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : MemberRepositoryCustom {

    override fun search(condition: MemberSearchCondition): List<MemberTeamDto> {
        return queryFactory
            .select(
                QMemberTeamDto(
                    member.id.`as`("memberId"),
                    member.username,
                    member.age,
                    team.id.`as`("teamId"),
                    team.name.`as`("teamName"),
                )
            )
            .from(member)
            .leftJoin(member.team, team)
            .where(
                usernameEq(condition.username),
                teamNameEq(condition.teamName),
                ageGoe(condition.ageGoe),
                ageLoe(condition.ageLoe),
            )
            .fetch()
    }

    override fun searchPageSimple(condition: MemberSearchCondition, pageable: Pageable): Page<MemberTeamDto> {
        val results = queryFactory
            .select(
                QMemberTeamDto(
                    member.id.`as`("memberId"),
                    member.username,
                    member.age,
                    team.id.`as`("teamId"),
                    team.name.`as`("teamName"),
                )
            )
            .from(member)
            .leftJoin(member.team, team)
            .where(
                usernameEq(condition.username),
                teamNameEq(condition.teamName),
                ageGoe(condition.ageGoe),
                ageLoe(condition.ageLoe),
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetchResults()

        val content = results.results
        val total = results.total

        return PageImpl(content, pageable, total)
    }

    override fun searchPageComplex(condition: MemberSearchCondition, pageable: Pageable): Page<MemberTeamDto> {
        val content = queryFactory
            .select(
                QMemberTeamDto(
                    member.id.`as`("memberId"),
                    member.username,
                    member.age,
                    team.id.`as`("teamId"),
                    team.name.`as`("teamName"),
                )
            )
            .from(member)
            .leftJoin(member.team, team)
            .where(
                usernameEq(condition.username),
                teamNameEq(condition.teamName),
                ageGoe(condition.ageGoe),
                ageLoe(condition.ageLoe),
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total = queryFactory
            .select(member)
            .from(member)
            .where(
                usernameEq(condition.username),
                teamNameEq(condition.teamName),
                ageGoe(condition.ageGoe),
                ageLoe(condition.ageLoe),
            )
            .fetchCount()

//        return PageImpl(content, pageable, total)

        val countQuery = queryFactory
            .select(member)
            .from(member)
            .where(
                usernameEq(condition.username),
                teamNameEq(condition.teamName),
                ageGoe(condition.ageGoe),
                ageLoe(condition.ageLoe),
            )

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount)
    }

    private fun usernameEq(username: String?): BooleanExpression? {
        return if (StringUtils.hasText(username)) {
            member.username.eq(username)
        } else {
            null
        }
    }

    private fun teamNameEq(teamName: String?): BooleanExpression? {
        return if (StringUtils.hasText(teamName)) {
            team.name.eq(teamName)
        } else {
            null
        }
    }

    private fun ageGoe(ageGoe: Int?): BooleanExpression? {
        return ageGoe?.let { member.age.goe(it) }
    }

    private fun ageLoe(ageLoe: Int?): BooleanExpression? {
        return ageLoe?.let { member.age.loe(it) }
    }
}
