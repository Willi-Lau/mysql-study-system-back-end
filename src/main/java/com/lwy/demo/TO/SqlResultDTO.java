package com.lwy.demo.TO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqlResultDTO {
    int id;
    String sqlstatement;
    String time;
    String type;
    String result;
}
