package com.example.codearchgenerator.service.impl;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.builder.Entity;
import com.baomidou.mybatisplus.generator.config.po.LikeTable;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.example.codearchgenerator.dto.GenerationParamsDTO;
import com.example.codearchgenerator.service.CodeGenerateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author VNElinpe
 * @since 2023/3/7
 **/
@Service
public class CodeGenerateServiceImpl implements CodeGenerateService {
    @Value("${generator.save-path}")
    private String codeSaveRootPath;
    @Value("${generator.jdbc-url-template}")
    private String jdbcUrlTemplate;
    @Autowired
    private HttpServletRequest httpServletRequest;

    @Override
    public void generateCode(GenerationParamsDTO paramsDTO) {
        paramsDTO.setCodePath(String.format("%s/%s", codeSaveRootPath, UUID.randomUUID().toString().replace("-", "")));
        FastAutoGenerator generator = collectDB(paramsDTO);
        globalConfig(generator, paramsDTO);
        packageConfig(generator, paramsDTO);
        strategyConfig(generator, paramsDTO);
        templateConfig(generator, paramsDTO);
        // 生成文件
        generator.templateEngine(new FreemarkerTemplateEngine()).execute();
    }

    /**
     * 连接数据库
     * @param paramsDTO 参数
     * @return
     */
    private FastAutoGenerator collectDB(GenerationParamsDTO paramsDTO) {
        return FastAutoGenerator.create(String.format(jdbcUrlTemplate, paramsDTO.getDbInstance(), paramsDTO.getDbName()), paramsDTO.getDbUser(), paramsDTO.getDbPwd());
    }

    /**
     * 全局设置
     * @param generator 生成器
     * @param paramsDTO 参数
     * @return
     */
    private FastAutoGenerator globalConfig(FastAutoGenerator generator, GenerationParamsDTO paramsDTO) {
        return generator.globalConfig(builder -> {
            builder.author(paramsDTO.getAuthor()) // 设置作者
                    .disableOpenDir() // 禁止打开生成目录
                    .commentDate("y/M/d") // 设置注释里的日期格式
                    .outputDir(paramsDTO.getCodePath()); // 指定输出目录
            // 开启 swagger 模式
            if ("yes".equalsIgnoreCase(paramsDTO.getUseSwagger())) {
                builder.enableSwagger();
            }
            // 设置时间类型
            switch (paramsDTO.getDateType()) {
                case "java.util.*":
                    builder.dateType(DateType.ONLY_DATE);
                    break;
                case "java.sql.*":
                    builder.dateType(DateType.SQL_PACK);
                    break;
                case "java.time.*":
                default:
                    builder.dateType(DateType.TIME_PACK);
                    break;
            }
        });
    }

    /**
     * 包配置
     * @param generator 生成器
     * @param paramsDTO 参数
     * @return
     */
    private FastAutoGenerator packageConfig(FastAutoGenerator generator, GenerationParamsDTO paramsDTO) {
        return generator.packageConfig(builder -> {
            builder.parent(paramsDTO.getPackageName()) // 设置父包名
                    .pathInfo(Collections.singletonMap(OutputFile.xml, StringUtils.hasText(paramsDTO.getMapperPath()) ? String.format("%s/%s", paramsDTO.getCodePath(), paramsDTO.getMapperPath().replace(".", "/")) : paramsDTO.getCodePath())); // 设置mapperXml生成路径
        });
    }

    /**
     * 策略配置
     * @param generator 生成器
     * @param paramsDTO 参数
     * @return
     */
    private FastAutoGenerator strategyConfig(FastAutoGenerator generator, GenerationParamsDTO paramsDTO) {
        return generator.strategyConfig(builder -> {
            // 表名(==)
            if (StringUtils.hasText(paramsDTO.getIncludeTableName())) {
                builder.addInclude(Arrays.stream(paramsDTO.getIncludeTableName().split(",")).map(String::trim).collect(Collectors.toList()));
            }
            // 表名(前缀)
            if (StringUtils.hasText(paramsDTO.getPreffixTableName())) {
                builder.addTablePrefix(Arrays.stream(paramsDTO.getPreffixTableName().split(",")).map(String::trim).collect(Collectors.toList()));
            }
            // 表名(后缀)
            if (StringUtils.hasText(paramsDTO.getSuffixTableName())) {
                builder.addTableSuffix(Arrays.stream(paramsDTO.getSuffixTableName().split(",")).map(String::trim).collect(Collectors.toList()));
            }
            // 表名(like)
            if (StringUtils.hasText(paramsDTO.getLikeTableName())) {
                builder.likeTable(new LikeTable(paramsDTO.getLikeTableName().trim()));
            }
            // 排除表名(==)
            if (StringUtils.hasText(paramsDTO.getExcludeTableName())) {
                builder.addExclude(Arrays.stream(paramsDTO.getExcludeTableName().split(",")).map(String::trim).collect(Collectors.toList()));
            }
            // 排除表名(not like)
            if (StringUtils.hasText(paramsDTO.getUnlikeTableName())) {
                builder.notLikeTable(new LikeTable(paramsDTO.getUnlikeTableName().trim()));
            }
            Entity.Builder entityBuilder = builder.entityBuilder().enableFileOverride();
            if ("yes".equalsIgnoreCase(paramsDTO.getUseLombok())) {
                entityBuilder.enableLombok();
            }
            entityBuilder.mapperBuilder().enableBaseResultMap().enableBaseColumnList().enableFileOverride().serviceBuilder().enableFileOverride().controllerBuilder().enableRestStyle().enableFileOverride();
            if ("jpa".equalsIgnoreCase(paramsDTO.getTemplateType())) {
                entityBuilder.mapperBuilder().formatMapperFileName("%sRepository").superClass(JpaReposi)
            }
        });
    }

    /**
     * 模板配置
     * @param generator 生成器
     * @param paramsDTO 参数
     * @return
     */
    private FastAutoGenerator templateConfig(FastAutoGenerator generator, GenerationParamsDTO paramsDTO) {
        return generator.templateConfig(builder -> {
            switch (paramsDTO.getTemplateType()) {
                case "mybatis":
                    builder.entity("/template/mybatis/entity.java")
                            .mapper("/template/mybatis/mapper.java")
                            .xml("/template/mybatis/mapper.xml")
                            .service("/template/mybatis/service.java")
                            .serviceImpl("/template/mybatis/serviceImpl.java")
                            .controller("/template/mybatis/controller.java");
                    break;
                case "mybatis-plus":
                    builder.entity("/template/mybatis-plus/entity.java")
                            .mapper("/template/mybatis-plus/mapper.java")
                            .xml("/template/mybatis-plus/mapper.xml")
                            .service("/template/mybatis-plus/service.java")
                            .serviceImpl("/template/mybatis-plus/serviceImpl.java")
                            .controller("/template/mybatis-plus/controller.java");
                    break;
                case "jpa":
                    builder.entity("/template/jpa/entity.java")
                            .mapper("/template/jpa/mapper.java")
                            .xml(null)
                            .service("/template/jpa/service.java")
                            .serviceImpl("/template/jpa/serviceImpl.java")
                            .controller("/template/jpa/controller.java");
                    break;
            }
        });
    }
}
