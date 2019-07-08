package com.hellozjf.learn.company.zrar.testbadlanguagemodel;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TestBadLanguageModelApplicationTest {

    @Test
    public void getBadLanguage() {
    }

    @Test
    public void changeCsvToSentences() {

    }

    @Test
    public void judgeRight() {
        String fileName = "test_result_dirtyWord.xlsx";
        Resource xlsxResource = new ClassPathResource(fileName);
        Resource resultResource = new ClassPathResource("result.txt");
        FileSystemResource outputResource = new FileSystemResource("output.tsv");
        try (InputStream inputStream = xlsxResource.getInputStream();
             InputStream resultInputStream = resultResource.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(resultInputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             OutputStream outputStream = outputResource.getOutputStream();
             PrintWriter printWriter = new PrintWriter(outputStream)) {
            List<ResultDirtyWord> resultDirtyWordList = ExcelUtils.readExcel(fileName, inputStream, ResultDirtyWord.class);

            List<ResultDirtyWord> myResultDirtyWordList = new ArrayList<>();
            Pattern pattern = Pattern.compile("lineNum = (\\d+), sentence = ([\\d|\\u4e00-\\u9fa5|\\<|\\>]+), not = (\\d\\.\\d+), is = (\\d\\.\\d+)");
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    ResultDirtyWord resultDirtyWord = new ResultDirtyWord();
                    resultDirtyWord.setLineNum(matcher.group(1));
                    resultDirtyWord.setSentence(matcher.group(2));
                    resultDirtyWord.setLabelT("");
                    resultDirtyWord.setLabelPre(matcher.group(3));
                    myResultDirtyWordList.add(resultDirtyWord);
                } else {
                    log.error("unfind line={}", line);
                }
            }
            myResultDirtyWordList.forEach(resultDirtyWord -> log.debug("{}", resultDirtyWord));

            // 都按照lineNum排序
            resultDirtyWordList = resultDirtyWordList.stream()
                    .sorted(Comparator.comparingInt(ResultDirtyWord::getIntLineNum))
                    .collect(Collectors.toList());
            log.debug("resultDirtyWordList");
            resultDirtyWordList.stream().forEach(e -> log.debug("{}", e));

            myResultDirtyWordList = myResultDirtyWordList.stream()
                    .map(resultDirtyWord -> {
                        // 这里行号要减一，并且非脏话概率小于0.5预测脏话为1
                        ResultDirtyWord resultDirtyWord2 = new ResultDirtyWord();
                        BeanUtils.copyProperties(resultDirtyWord, resultDirtyWord2);
                        resultDirtyWord2.setLineNum(String.valueOf(resultDirtyWord.getIntLineNum() - 1));
                        resultDirtyWord2.setLabelT(resultDirtyWord.getLabelPre());
                        resultDirtyWord2.setLabelPre(Double.valueOf(resultDirtyWord.getLabelPre()) < 0.5 ? "1" : "0");
                        return resultDirtyWord2;
                    })
                    .sorted(Comparator.comparingInt(ResultDirtyWord::getIntLineNum))
                    .collect(Collectors.toList());
            log.debug("myResultDirtyWordList");
            myResultDirtyWordList.stream().forEach(e -> log.debug("{}", e));

            // 判断相同行号的预测值是否有相同
            int min = Math.min(resultDirtyWordList.size(), myResultDirtyWordList.size());
            printWriter.println("lineNum\tsentence\texcel\tpredict\tnotPrediction");
            for (int i = 0; i < min; i++) {
                ResultDirtyWord resultDirtyWord = resultDirtyWordList.get(i);
                ResultDirtyWord myResultDirtyWord = myResultDirtyWordList.get(i);
                if (resultDirtyWord.getLineNum().equalsIgnoreCase(myResultDirtyWord.getLineNum()) &&
                    resultDirtyWord.getLabelPre().equalsIgnoreCase(myResultDirtyWord.getLabelPre())) {
                    continue;
                } else {
                    log.error("i = {}, line1 = {}, line2 = {}, predict1 = {}, predict2 = {}, sentence = {}",
                            i, resultDirtyWord.getLineNum(), myResultDirtyWord.getLineNum(),
                            resultDirtyWord.getLabelPre(), myResultDirtyWord.getLabelPre(), resultDirtyWord.getSentence());
                    if (resultDirtyWord.getIntLineNum().intValue() != myResultDirtyWord.getIntLineNum().intValue()) {
                        log.error("lineNum not same");
                        return;
                    }
                    printWriter.println(i + "\t" + resultDirtyWord.getSentence() + "\t" +
                            resultDirtyWord.getLabelPre() + "\t" + myResultDirtyWord.getLabelPre() + "\t" +
                            myResultDirtyWord.getLabelT());
                }
            }
        } catch (Exception e) {
            log.error("e = {}", e);
        }
    }
}