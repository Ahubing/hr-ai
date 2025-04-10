##服务器中运行脚本：
    java -jar xxx.jar --spring.profiles.active=dev
## 本地运行：
     线上环境
     vm option添加：-Dspring.profiles.active=prod
     本地环境
     vm option添加：-Dspring.profiles.active=dev

## maven打包命令
    mvn clean install -U -Dmaven.test.skip=true

## banner 自定义网站
    https://patorjk.com/software/taag/#p=display&f=Graffiti&t=

## mapstruct的使用
    衔接：https://mp.weixin.qq.com/s/pkzFg9wxyygoxQbm_r5bJQ
    maven插件要使用3.6.0版本以上、lombok使用1.16.16版本以上，
    另外编译的lombok mapstruct的插件需要加上。
### demo
    @Mapper  使用org.mapstruct.Mapper注解
    public interface RotationMapUtil {
        RotationMapUtil INSTANCE = Mappers.getMapper(RotationMapUtil.class);
        
        @Mapping(source = "gender.name", target = "gender")
        @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd HH:mm:ss")
        StudentVO student2StudentVO(Student student);
    } 
### redis
    启动命令：/www/server/redis/redis-5.0.5/src/./redis-server /www/server/redis/redis-5.0.5/redis.conf

### 日志分割规则
采用按小时分割的策略，
以时间格式为文件命名，
存储于项目所在的同一路径下，
并设置存储期限为9999天，实质上等同于永久保存。


## maven打包命令
    mvn clean install -U -Dmaven.test.skip=true
## 部署
        1. 从git拷贝项目（这个步骤最好是手动执行，因为一般来说git需要登录，可以使用下面的命令来记录git账号密码）
        git config --global credential.helper store
## 线上执行脚本
    1. 第一次上传脚本需要执行 dos2unix server.sh 将脚本格式化为linux语法 
    2. sh server.sh start    
        

