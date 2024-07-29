package com.luv2code.springmvc.repository;

import com.luv2code.springmvc.models.ScienceGrade;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScienceGradeDao extends CrudRepository<ScienceGrade, Long> {

    Iterable<ScienceGrade> findGradeByStudentId(int i);
}
