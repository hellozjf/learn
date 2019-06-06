package com.hellozjf.learn.company.zrar.csmonitor_data_generator.service;

/**
 * @author Jingfeng Zhou
 */
public interface InitService {
    void initAll();
    void initCsadState();
    void initMessagetemp();
    void initMessageinfo();
    void initServicelog();
    void initCustomservice();
    void initCacsiresult();
}
