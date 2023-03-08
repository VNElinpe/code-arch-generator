package com.example.codearchgenerator.service;

import com.example.codearchgenerator.dto.GenerationParamsDTO;

/**
 * 生成代码的服务
 *
 * @author VNElinpe
 * @since 2023/3/7
 **/
public interface CodeGenerateService {
    /**
     * 生成代码
     */
    void generateCode(GenerationParamsDTO paramsDTO);
}
