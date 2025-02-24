package com.open.ai.eros.db.util;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.io.File;

public class MybatisPlusGeneratorUtil {
    public static void main(String[] args) {
        String author = "Eros-AI";
        String database = "hr-ai";
        String model = "hr";
        generateByTables(author, database, model, "ic_config", "ic_record");
    }

    private static void generateByTables(String author, String database, String model, String... tableNames) {
        // 输出目录
        String outputDir = System.getProperty("user.dir") + "/eros-db/src/main/java/";
        System.out.println("Output Directory: " + outputDir);
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs(); // 确保目录存在
            System.out.println("Created directory: " + outputDir);
        }

        // 全局配置
        GlobalConfig config = new GlobalConfig();
        String dbUrl = "jdbc:mysql://43.153.41.128:7777/" + database +
                "?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai";
        config.setActiveRecord(false)
                .setAuthor(author)
                .setOutputDir(outputDir)
                .setFileOverride(true)
                .setEnableCache(false)
                .setEntityName("%s")
//                .setMapperName("%sMapper")
//                .setServiceName("%sService")
//                .setServiceImplName("%sServiceImpl")
                .setOpen(false);

        // 数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL)
                .setUrl(dbUrl)
                .setUsername("root")
                .setPassword("blueCat666")
                .setDriverName("com.mysql.cj.jdbc.Driver");

        // 策略配置
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setCapitalMode(true)
                .setEntityLombokModel(true)
                .setNaming(NamingStrategy.underline_to_camel)
                .setInclude(tableNames); // 指定表名

        // 执行生成
        try {
            new AutoGenerator().setGlobalConfig(config)
                    .setDataSource(dataSourceConfig)
                    .setStrategy(strategyConfig)
                    .setPackageInfo(
                            new PackageConfig()
                                    .setParent("com.open.ai.eros.db.mysql." + model)
                                    .setEntity("entity")
//                                    .setMapper("mapper")
//                                    .setService("service")
//                                    .setServiceImpl("service.impl")
//                                    .setXml("mybatis.mappers")
                    ).execute();
            System.out.println("CRUD files generated successfully!");
        } catch (Exception e) {
            System.err.println("Error during code generation:");
            e.printStackTrace();
        }
    }
}