package com.hellozjf.learn.projects.scoresystem.controller;

import com.hellozjf.learn.projects.common.vo.ResultVO;
import com.hellozjf.learn.projects.scoresystem.domain.ClassEntity;
import com.hellozjf.learn.projects.scoresystem.repository.ClassRepository;
import com.hellozjf.learn.projects.scoresystem.util.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hellozjf
 */
@RestController
@RequestMapping("/class")
public class ClassController {

    @Autowired
    private ClassRepository classRepository;

    /**
     * 获取所有班级
     * @return
     */
    @GetMapping("/getAll")
    public ResultVO getAll(
            @PageableDefault(value = 10, sort = { "modifiedDateTime" }, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ClassEntity> page = classRepository.findAll(pageable);
        return ResultUtils.success(page);
    }

    /**
     * 获取名称类似于
     * @param name
     * @return
     */
    @GetMapping("/findByNameLike")
    public ResultVO findByNameLike(String name,
                                   @PageableDefault(value = 10, sort = { "modifiedDateTime" }, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ClassEntity> classEntityPage = classRepository.findByNameLike("%" + name + "%", pageable);
        return ResultUtils.success(classEntityPage);
    }

}
