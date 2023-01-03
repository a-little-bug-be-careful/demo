package com.example.demo2.service;

import com.example.demo2.domain.Analysis;

import java.util.List;

public interface AnalysisService {
    List analyseUser(Analysis analysis);
    List analyseLog(Analysis analysis);
    List analyseMenu(Analysis analysis);
    List analyseVisit(Analysis analysis);
}
