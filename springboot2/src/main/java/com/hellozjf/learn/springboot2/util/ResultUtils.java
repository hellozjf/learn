package com.hellozjf.learn.springboot2.util;

import com.hellozjf.learn.projects.common.vo.ResultVO;
import com.hellozjf.learn.springboot2.constant.ResultEnum;

/**
 * Created by 廖师兄
 * 2017-01-21 13:39
 */
public class ResultUtils extends com.hellozjf.learn.projects.common.util.ResultUtils {

    public static ResultVO error(ResultEnum resultEnum) {
        return error(resultEnum.getCode(), resultEnum.getMessage());
    }
}
