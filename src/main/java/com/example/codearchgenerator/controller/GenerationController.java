package com.example.codearchgenerator.controller;

import com.example.codearchgenerator.dto.GenerationParamsDTO;
import com.example.codearchgenerator.service.CodeGenerateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author VNElinpe
 * @since 2023/3/7
 **/
@RestController
@RequestMapping("/gen")
public class GenerationController {
    @Autowired
    private CodeGenerateService codeGenerateService;

    /**
     * 生成代码
     * @param paramsDTO 请求参数
     */
    @PostMapping("/code")
    public void generateCode(@RequestBody GenerationParamsDTO paramsDTO) {
        codeGenerateService.generateCode(paramsDTO);
    }
}
