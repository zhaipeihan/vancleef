package com.peihan.vancleef.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpentUXTO {

    /**
     * 所有需要花费的UXTO价值总和
     */
    private int total;


    /**
     * 具体的UXTO
     */
    private Map<String, List<TxOutput>> UXTOs;
}
