package com.open.ai.eros.db.util;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.apache.commons.collections.ArrayStack;

import java.util.Arrays;
import java.util.List;

/**
 * @author melo
 * @date 2020/5/31
 */
public class MybatisPlusGeneratorUtil {
    public static void main(String[] args) {
        String author = "Eros-AI";
        String database = "hr-ai";
        // 生成的数据库表 属于 哪一个模块就填写哪个
        // ai   user   pay  admin    bot  creator  social permission knowledge
        String model = "hr";
        System.out.println(System.getProperty("user.dir"));
        generateByTables(author, database,model,"am_mask");
    }

    private static void generateByTables(String author, String database,String model, String... tableNames) {
        GlobalConfig config = new GlobalConfig();
        String dbUrl = "jdbc:mysql://43.153.41.128:7777/" + database + "?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC";
        config.setActiveRecord(false)
                .setAuthor(author)
                .setOutputDir(System.getProperty("user.dir")+"/eros-db" +"/src/main/java/")
                .setFileOverride(true)
                .setEnableCache(false)
                .setEntityName("%s")
//                .setMapperName("%sMapper")
               // .setServiceName("%sService")
                //.setServiceImplName("%sServiceImpl")
                .setOpen(false);

        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL)
                .setUrl(dbUrl)
                .setUsername("root")
                .setPassword("blueCat666")
                .setDriverName("com.mysql.cj.jdbc.Driver");

        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig
                .setCapitalMode(true)
                .setEntityLombokModel(true)
                .setNaming(NamingStrategy.underline_to_camel)
//				.setSuperMapperClass("")
                .setInclude(tableNames);//修改替换成你需要的表名，多个表名传数组

        new AutoGenerator().setGlobalConfig(config)
                .setDataSource(dataSourceConfig)
                .setStrategy(strategyConfig)
                .setPackageInfo(
                        new PackageConfig()
                                .setParent("com.open.ai.eros.db.mysql"+"."+model)
                                .setEntity("entity")
//                                .setMapper("mapper")
//                                .setService("service")
//                                .setServiceImpl("service.impl")
                                //.setXml("mybatis.mappers")
                ).execute();
    }
}
