package com.lqkj.web.cmiep.config;

import okio.BufferedSink;
import okio.Okio;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Scanner;

/**
 * 环境文件部署
 */
public class PropertyDeploy {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String PropertyDirPath = "./config";
    public static final String PropertyFilePath = PropertyDirPath + "/application.yml";

    /**
     * 查询文件是否存在
     *
     * @return
     */
    private boolean isPropertyFileExist() {
        return Files.exists(Paths.get(PropertyFilePath));
    }

    /**
     * 数据库链接询问
     */
    private void inputDataBaseConfig() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("请输入数据库IP:");
        String ip = scanner.nextLine();

        System.out.println("请输入数据库端口:");
        String port = scanner.nextLine();

        System.out.println("请输入数据库名称");
        String dbName = scanner.nextLine();

        System.out.println("请输入数据库用户名:");
        String userName = scanner.nextLine();

        System.out.println("请输入密码");
        String passWord = scanner.nextLine();

        System.out.println("指定server绑定的地址");
        String address = scanner.nextLine();

        createConfigureFile(ip, port, dbName, userName, passWord, address);
    }

    /**
     * 创建配置文件
     */
    public void createConfigureFile(String ip, String port, String dbName, String userName, String passWord, String address) {
        if (StringUtils.isEmpty(ip)){
            ip = "127.0.0.1";
        }
        if (StringUtils.isEmpty(port)){
            port = "5432";
        }
        File applicationFile = new File(PropertyFilePath);

        ClassPathResource resource = new ClassPathResource("application-install.yml");

        Yaml yaml = new Yaml();

        BufferedSink sink = null;

        InputStream inputStream = null;

        try {
            Files.createDirectories(Paths.get(PropertyDirPath));

            Files.createFile(Paths.get(PropertyFilePath));

            inputStream = resource.getInputStream();

            LinkedHashMap<String, LinkedHashMap> application = (LinkedHashMap<String, LinkedHashMap>) yaml.load(inputStream);

            LinkedHashMap<String, Object> dateSource = (LinkedHashMap<String, Object>) application.get("spring").get("datasource");
            LinkedHashMap<String, Object> hikari = (LinkedHashMap<String, Object>)dateSource.get("hikari");
            dateSource.put("url", "jdbc:postgresql://" + ip + ":" + port + "/" + dbName);
            hikari.put("username", userName);
            hikari.put("password", passWord);

            LinkedHashMap<String, Object> serverAddress = (LinkedHashMap<String, Object>) application.get("server");
            serverAddress.put("address", address);

            sink = Okio.buffer(Okio.sink(applicationFile));

            sink.write(yaml.dump(application).getBytes());

            sink.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (sink != null) {
                    sink.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 部署文件
     */
    public void deploy() {
        try {
            if (!isPropertyFileExist()) {
                inputDataBaseConfig();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }

    }
}
