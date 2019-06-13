package com.hellozjf.learn.projects.mockbackend.controller;

import com.hellozjf.learn.projects.mockbackend.util.RandomValueUtils;
import com.hellozjf.learn.projects.mockbackend.util.ResultUtils;
import com.hellozjf.learn.projects.mockbackend.vo.ResultVO;
import com.hellozjf.learn.projects.mockbackend.vo.TableDataVO;
import com.hellozjf.learn.projects.mockbackend.vo.TablePageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author hellozjf
 */
@RestController
@RequestMapping("/mockapi")
public class ImoocManagerController {

    @Autowired
    private Random random;

    @GetMapping("/table/list1")
    public ResultVO tableList1(Integer page) {
        List<TableDataVO> tableDataVOList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TableDataVO tableDataVO = new TableDataVO();
            tableDataVO.setId(i + 1);
            RandomValueUtils.Person person = RandomValueUtils.getPerson();
            tableDataVO.setUserName(person.getName());
            tableDataVO.setSex(person.getSex());
            tableDataVO.setState(RandomValueUtils.getNum(1, 5));
            tableDataVO.setInterest(RandomValueUtils.getNum(1, 8));
            tableDataVO.setIsMarried(RandomValueUtils.getNum(0, 1));
            tableDataVO.setBirthday("2000-01-01");
            tableDataVO.setAddress("北京市海淀区");
            tableDataVO.setTime("09:00:00");
            tableDataVOList.add(tableDataVO);
        }

        TablePageVO tablePageVO = new TablePageVO();
        tablePageVO.setList(tableDataVOList);
        tablePageVO.setPage(page);
        tablePageVO.setPageSize(10);
        tablePageVO.setTotal(100);

        ResultVO resultVO = ResultUtils.success(tablePageVO);
//        ResultVO resultVO = ResultUtils.error(1008, "您当前未登录");
        return resultVO;
    }

    @GetMapping("/table/high/list")
    public ResultVO tableHighList(Integer page) {
        List<TableDataVO> tableDataVOList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TableDataVO tableDataVO = new TableDataVO();
            tableDataVO.setId(i + 1);
            RandomValueUtils.Person person = RandomValueUtils.getPerson();
            tableDataVO.setUserName(person.getName());
            tableDataVO.setAge(random.nextInt(40 - 10 + 1) + 10);
            tableDataVO.setSex(person.getSex());
            tableDataVO.setState(RandomValueUtils.getNum(1, 5));
            tableDataVO.setInterest(RandomValueUtils.getNum(1, 8));
            tableDataVO.setIsMarried(RandomValueUtils.getNum(0, 1));
            tableDataVO.setBirthday("2000-01-01");
            tableDataVO.setAddress("北京市海淀区");
            tableDataVO.setTime("09:00:00");
            tableDataVOList.add(tableDataVO);
        }

        TablePageVO tablePageVO = new TablePageVO();
        tablePageVO.setList(tableDataVOList);
        tablePageVO.setPage(page);
        tablePageVO.setPageSize(10);
        tablePageVO.setTotal(100);

        ResultVO resultVO = ResultUtils.success(tablePageVO);
//        ResultVO resultVO = ResultUtils.error(1008, "您当前未登录");
        return resultVO;
    }
}
