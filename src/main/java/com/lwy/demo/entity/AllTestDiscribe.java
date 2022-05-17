package com.lwy.demo.entity;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class AllTestDiscribe implements Serializable {
    int id;
    String titleDiscribe;
    String tableName;
    String tableColumn;
    String trueSQL;
}
