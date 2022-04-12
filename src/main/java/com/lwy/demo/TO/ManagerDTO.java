package com.lwy.demo.TO;

import lombok.*;

/**
 * @author Administrator
 */
@Data
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class ManagerDTO {
    Integer id;
    String username;
    String managerNumber;
    String password;
    String level;
    String state;
}
