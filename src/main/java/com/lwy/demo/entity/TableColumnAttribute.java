package com.lwy.demo.entity;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class TableColumnAttribute {
    int id;
    String tableName;
    String tableColumn;
}
