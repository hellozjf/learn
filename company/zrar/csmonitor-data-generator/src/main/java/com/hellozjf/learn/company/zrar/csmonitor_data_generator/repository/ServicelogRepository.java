package com.hellozjf.learn.company.zrar.csmonitor_data_generator.repository;

import com.hellozjf.learn.company.zrar.csmonitor_data_generator.domain.Servicelog;
import org.springframework.data.cassandra.repository.CassandraRepository;

/**
 * @author Jingfeng Zhou
 */
public interface ServicelogRepository extends CassandraRepository<Servicelog, Servicelog.Key> {
}
