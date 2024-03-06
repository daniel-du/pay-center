package com.tfjt.pay.external.unionpay.utils;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/7 11:39
 * @description
 */
public class GeneratorUtil {

    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://rm-8vb4l3qwxx07dea92wo.mysql.zhangbei.rds.aliyuncs.com:3306/tf_pay","root","tongfumy#T")
                // 全局配置
                .globalConfig((scanner, builder) -> builder
                        .author("Du Penglun")//生成的作者名字,自行修改
                        .outputDir("D:\\myProject\\tongfu\\tf-cloud-pay-center\\pay-external-service\\src\\main\\java")
                )
                // 包配置
                .packageConfig(
                        (scanner, builder) ->
                                builder
                                        .parent("com.tfjt.pay.external.unionpay")//在项目的那个文件夹下生成
                                        .pathInfo(Collections.singletonMap(OutputFile.xml, "D:\\myProject\\tongfu\\tf-cloud-pay-center\\pay-external-service\\src\\main\\resources\\mapper")))
                // 策略配置
                .strategyConfig((scanner, builder) -> builder.addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all")))
                        .controllerBuilder().enableRestStyle().enableHyphenStyle()
                        .entityBuilder().enableLombok().addTableFills(
                                new Column("create_time", FieldFill.INSERT)
                        ).build())
                /*
                    模板引擎配置，默认 Velocity 可选模板引擎 Beetl 或 Freemarker
                   .templateEngine(new BeetlTemplateEngine())
                   .templateEngine(new FreemarkerTemplateEngine())
                 */
                .execute();


    }

    protected static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }

}
