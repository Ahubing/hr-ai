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
        List<String> tableNames = Arrays.asList(
                "mini_uni_user_wallet_log",
                "mini_uni_user_wallet",
                "mini_uni_user_info",
                "mini_uni_user_exchange_code",
                "mini_uni_user",
                "am_zp_platforms",
                "am_zp_local_accouts",
                "am_version",
                "am_square_roles",
                "am_sms_code",
                "am_resume",
                "am_prompt_tags",
                "am_prompt",
                "am_position_sync_task",
                "am_position_section",
                "am_position_post",
                "am_position",
                "am_pay_type",
                "am_log",
                "am_feedback",
                "am_config",
                "am_client_tasks",
                "am_chatbot_position_option",
                "am_chatbot_options_items",
                "am_chatbot_options_config",
                "am_chatbot_options",
                "am_chatbot_option_ai_role",
                "am_chatbot_greet_task",
                "am_chatbot_greet_result",
                "am_chatbot_greet_online_monitor",
                "am_chatbot_greet_messages",
                "am_chatbot_greet_config",
                "am_chatbot_greet_condition",
                "am_chatbot_chat_job",
                "am_admin"
        );
        // 生成的数据库表 属于 哪一个模块就填写哪个
        // ai   user   pay  admin    bot  creator  social permission knowledge
        String model = "hr";
        System.out.println(System.getProperty("user.dir"));
        for (String tableName : tableNames) {
            generateByTables(author, database,model,tableName);
        }
    }

    private static void generateByTables(String author, String database,String model, String... tableNames) {
        GlobalConfig config = new GlobalConfig();
        String dbUrl = "jdbc:mysql://43.153.41.128:3377/" + database + "?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC";
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
