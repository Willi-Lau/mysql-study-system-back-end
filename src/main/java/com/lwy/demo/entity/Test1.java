package com.lwy.demo.entity;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class Test1 {
    int id;
    String name;
    String age;
    int englishResult;
    int mathResult;
}
