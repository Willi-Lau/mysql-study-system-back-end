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
public class User implements Serializable {
    String studentNumber;
    String name;
    String identityCardNumber;
    String password;
    String phone;
    String university;
    String className;
}
