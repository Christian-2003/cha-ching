<img src="../img/icon.png" height="150" align="right">

# Analysis
This document describes the analysis algorithm used in Cha Ching.

> [!NOTE]
> This document is currently WIP.

### Table of Contents
1. [Database Scheme](#database-scheme)
2. [Relations](#relations)

<br/>

## 1 Workflow
> [!NOTE]
> Global workflow is introduced once all analysis steps are finished

<br/>

## 2 Analysis Steps
This section describes each of the analysis steps that are mentioned [above](#1-workflow).

### 2.1 Analysis Data Summary
The first step of the analysis is the summary of the data and the converting into a suitable format, as described by the following workflow:

![Analysis flow summarizer](../img/development/analysis/analysis_flow_summarizer.drawio.svg)

First, all transfers are groubed by type.
The transfers for each type are then grouped by a normalized date and summarized afterwards. The normalized date is determined based on the analysis precision. The resulting list of summarized data (for each normalized date) is then trimmed. This makes sure that in between a start and end date, each normalized date has exactly one item inside the list.

The following schematic describes the result of the summarizer:

![Analysis result summarizer](../img/development/analysis/analysis_result_summarizer.drawio.svg)

<br/>

***

2025-12-16  
&copy; Christian-2003
