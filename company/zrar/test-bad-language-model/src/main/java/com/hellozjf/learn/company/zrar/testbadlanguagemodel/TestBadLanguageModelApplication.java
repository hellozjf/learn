package com.hellozjf.learn.company.zrar.testbadlanguagemodel;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
@SpringBootApplication
public class TestBadLanguageModelApplication implements CommandLineRunner {

    @Autowired
    private CustomConfig customConfig;

    public static void main(String[] args) {
        SpringApplication.run(TestBadLanguageModelApplication.class, args);
    }

    /**
     * 将一个tsv文件转化成句子列表文件
     *
     * @param inputFile
     * @param outputFile
     */
    private void genSentences(File inputFile, File outputFile) {
        FileSystemResource inputResource = new FileSystemResource(inputFile);
        FileSystemResource outputResource = new FileSystemResource(outputFile);
        try (InputStream inputStream = inputResource.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             OutputStream outputStream = outputResource.getOutputStream();
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
             PrintWriter printWriter = new PrintWriter(outputStreamWriter)) {
            // 跳过第一行
            bufferedReader.readLine();
            while (true) {
                String line = bufferedReader.readLine();
                if (StringUtils.isEmpty(line)) {
                    break;
                }
                String sentence = line.split("\t")[1];
                log.debug("sentence = {}", sentence);
                printWriter.println(sentence);
            }
        } catch (Exception e) {
            log.error("e = {}", e);
        }
    }

    /**
     * 从句子列表文件中提取出脏话列表文件
     *
     * @param inputFile
     * @param outputFile
     */
    private void getBadSentences(File inputFile, File outputFile) {

        ExecutorService executorService = Executors.newFixedThreadPool(customConfig.getThreadNum());

        CloseableHttpClient httpClient = HttpClients.createDefault();
        ObjectMapper objectMapper = new ObjectMapper();

        FileSystemResource inputResource = new FileSystemResource(inputFile);
        FileSystemResource outputResource = new FileSystemResource(outputFile);
        try (InputStream inputStream = inputResource.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             OutputStream outputStream = outputResource.getOutputStream();
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
             PrintWriter printWriter = new PrintWriter(outputStreamWriter)) {
            int lineNum = 0;
            while (true) {
                // 首先将sentence.txt里面的话都提取出来
                String sentence = bufferedReader.readLine();
                if (sentence == null) {
                    // 获取到null，说明文件读完了
                    break;
                }
                if (sentence.equalsIgnoreCase("")) {
                    // 跳过空行
                    continue;
                }

                lineNum++;
                executorService.execute(new JudgeBadSentenceRunnable(httpClient, objectMapper, lineNum, sentence, printWriter));
            }

            executorService.shutdown();
            while (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                log.debug("解析脏话继续中...");
            }
            log.info("解析脏话完毕");
        } catch (IOException e) {
            log.error("e = {}", e);
        } catch (InterruptedException e) {
            log.error("e = {}", e);
        }
    }

    @Override
    public void run(String... args) {
        if (args.length != 3) {
            printUsage();
        } else {
            if (args[0].equalsIgnoreCase("genSentences")) {
                genSentences(new File(args[1]), new File(args[2]));
            } else if (args[0].equalsIgnoreCase("getBadSentences")) {
                getBadSentences(new File(args[1]), new File(args[2]));
            } else {
                printUsage();
            }
        }
    }

    private void printUsage() {
        log.info("usage: \n\tjava -jar test-bad-language-model-1.0.0.jar genSentences|getBadSentences inputFileName outputFileName");
    }
}
