package de.christian2003.chaching.domain.analysis

data class AnalysisResult(

    val transfersByTypeDiagram: AnalysisDiagram,

    val cumulatedTransfersByTypeDiagram: AnalysisDiagram

)
