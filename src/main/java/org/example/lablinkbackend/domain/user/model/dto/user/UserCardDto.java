package org.example.lablinkbackend.domain.user.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCardDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String cityName;
    private String organizationName;
    private Double recommendationScore;
    private Set<String> commonTags;
}