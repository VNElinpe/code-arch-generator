package com.example.codearchgenerator.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 代码生成入参
 *
 * @author VNElinpe
 * @since 2023/3/7
 **/
@Data
public class GenerationParamsDTO {
    private String dbInstance;
    private String dbUser;
    private String dbPwd;
    private String dbName;
    private String includeTableName;
    private String preffixTableName;
    private String suffixTableName;
    private String likeTableName;
    private String excludeTableName;
    private String unlikeTableName;
    private String packageName;
    private String mapperPath;
    private String author;
    private String useLombok;
    private String useSwagger;
    private String dateType;
    private String templateType;
    @JsonIgnore
    private String codePath;
}
