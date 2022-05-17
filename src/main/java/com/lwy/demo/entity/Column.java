package com.lwy.demo.entity;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Column implements Serializable {
    String columnDiscribe;
    String columnAttribute;
}
