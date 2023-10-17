package study.querydsl.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.util.StringUtils
import study.querydsl.dto.MemberSearchCondition
import study.querydsl.dto.MemberTeamDto
import study.querydsl.dto.QMemberTeamDto
import study.querydsl.entity.QMember
import study.querydsl.entity.QTeam

class MemberRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : MemberRepositoryCustom {
    
    override fun search(condition: MemberSearchCondition): List<MemberTeamDto> {
        return queryFactory
            .select(
                QMemberTeamDto(
                    QMember.member.id.`as`("memberId"),
                    QMember.member.username,
                    QMember.member.age,
                    QTeam.team.id.`as`("teamId"),
                    QTeam.team.name.`as`("teamName"),
                )
            )
            .from(QMember.member)
            .leftJoin(QMember.member.team, QTeam.team)
            .where(
                usernameEq(condition.username),
                teamNameEq(condition.teamName),
                ageGoe(condition.ageGoe),
                ageLoe(condition.ageLoe),
            )
            .fetch()
    }

    private fun usernameEq(username: String?): BooleanExpression? {
        return if (StringUtils.hasText(username)) {
            QMember.member.username.eq(username)
        } else {
            null
        }
    }

    private fun teamNameEq(teamName: String?): BooleanExpression? {
        return if (StringUtils.hasText(teamName)) {
            QTeam.team.name.eq(teamName)
        } else {
            null
        }
    }

    private fun ageGoe(ageGoe: Int?): BooleanExpression? {
        return ageGoe?.let { QMember.member.age.goe(it) }
    }

    private fun ageLoe(ageLoe: Int?): BooleanExpression? {
        return ageLoe?.let { QMember.member.age.loe(it) }
    }
}
