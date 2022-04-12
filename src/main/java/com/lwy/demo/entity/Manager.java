package com.lwy.demo.entity;

import lombok.*;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Manager implements Serializable {
    Integer id;
    String username;
    String managerNumber;
    String password;
    String level;
    String state;
}
