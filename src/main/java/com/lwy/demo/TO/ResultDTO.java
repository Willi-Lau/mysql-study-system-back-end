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
    /**
     * 返回成功还是失败
     */
    private Boolean type;
    /**
     * 返回对象
     */
    private Object object;
    /**
     * 返回List
     */
    private List<Object> list;
    /**
     * 返回Map
     */
    private Map<Object,Object> map;

}
