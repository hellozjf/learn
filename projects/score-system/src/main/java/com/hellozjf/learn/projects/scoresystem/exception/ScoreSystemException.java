package com.hellozjf.learn.projects.scoresystem.exception;

import com.hellozjf.learn.projects.scoresystem.constant.ResultEnum;
import lombok.Getter;

/**
 * @author Jingfeng Zhou
 */
@Getter
public class ScoreSystemException extends RuntimeException {

    private Integer code;

    public ScoreSystemException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ScoreSystemException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }
}
