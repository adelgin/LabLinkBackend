package org.example.lablinkbackend.domain.social.repository;

import org.example.lablinkbackend.domain.social.model.entity.GroupMember;
import org.example.lablinkbackend.domain.social.model.entity.GroupMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {
    boolean existsById(GroupMemberId id);
}
