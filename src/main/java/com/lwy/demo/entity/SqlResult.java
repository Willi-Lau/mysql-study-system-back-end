package com.lwy.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SqlResult implements Serializable {
    String sqlstatement;
    String time;
    String type;
    String result;
}
