package com.lwy.demo.TO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class ManagerLoginHistoryDTO {
    Integer id;
    Integer userId;
    Long loginTime;
    String localHostAddress;
}
