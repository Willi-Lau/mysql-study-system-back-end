package com.lwy.demo.entity;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class School implements Serializable {
    Integer id;
    String name;
    String createTime;
    String deadline;
    Integer studentNum;
}
