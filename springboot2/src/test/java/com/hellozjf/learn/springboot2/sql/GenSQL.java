package com.hellozjf.learn.springboot2.sql;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;

import java.io.*;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class GenSQL {

    @Test
    public void main() {
        try (FileWriter fileWriter = new FileWriter("genTest.sql");
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            String username = "test";
            String password = "123456";

            for (int i = 1; i <= 60; i++) {
                String name = username + i;
                String pass = name + password;
                int zxgh = 110 + i;
                printWriter.printf("insert into " +
                        " T_XT_YH(YH_ID, YH_DM, YH_MC, MM, XB, YH_LX, YX_BJ, SC_BJ, USERJGBH, USERTYPE, ZXGH) " +
                        " values('%d', '%s', '%s', '%s', 0, 1, 'Y', 'N', '100000000', '2000001001', '%d');\n",
                        10000 + i, name, name,
                        DigestUtils.md5DigestAsHex(pass.getBytes("utf-8")).toUpperCase(), zxgh);
                printWriter.printf("insert into " +
                        " T_XT_DEPT_YH(JG_ID, YH_ID) " +
                        " values('043', '%d');\n", 10000 + i);
            }
        } catch (Exception e) {
            log.error("e = {}", e);
        }
    }
}
