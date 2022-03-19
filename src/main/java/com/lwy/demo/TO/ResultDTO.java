package com.lwy.demo.TO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultDTO implements Serializable {
    private Boolean type;
    private Object object;
    private List<Object> list;
    private Map<Object,Object> map;

}
