package com.example.demo2.mapper;

import com.example.demo2.domain.Analysis;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisMapper {
    List analyseUser(Analysis analysis);
    List analyseLog(Analysis analysis);
    List analyseMenu(Analysis analysis);
    List analyseVisit(Analysis analysis);
}
