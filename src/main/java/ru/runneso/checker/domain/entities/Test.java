package ru.runneso.checker.domain.entities;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Test extends BaseEntity {
    private String input;
    private String output;
}
