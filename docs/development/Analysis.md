<img src="../img/icon.png" height="150" align="right">

# Analysis
This document describes the analysis algorithm used in Cha Ching.

> [!NOTE]
> This document is currently WIP.

### Table of Contents
1. [Small Analysis](#1-small-analysis)
    1. [Workflow](#11-workflow)
    2. [Analysis Steps](#12-analysis-steps)
    3. [Final Result](#13-final-result)
    4. [Test Strategy](#14-test-strategy)
2. [Large Analysis](#2-large-analysis)


<br/>

***

## 1 Small Analysis
The small analysis is the less powerful out of the two analysis algorithms used in Cha Ching. This section describes the small analysis.

<br/>

### 1.1 Workflow
The following workflow illustrates how the analysis works internally:
![Small analysis workflow](../img/development/analysis/small_workflow.drawio.svg)

<br/>

### 1.2 Analysis Steps
This section describes each of the analysis steps that are mentioned [above](#11-workflow).

<br/>

#### 1.2.1 Query Clusters
First, the analysis queries the two latest clusters from the database. The query to get a cluster looks as follows:, where `:date` is the epoch day from which to begin searching for the latest cluster:
```sql
WITH latest AS (
    SELECT MAX(valueDate) AS maxDate FROM transfers t
    WHERE t.valueDate <= :date
    AND NOT EXISTS (SELECT 1 FROM deletedTypes d WHERE d.typeId = t.type)
)
SELECT * FROM transfers
WHERE valueDate <= (SELECT maxDate FROM latest)
    AND valueDate >= (SELECT maxDate FROM latest) - :maxClusterGap
ORDER BY valueDate ASC
```
_`:date`: Epoch day from which to begin searching for the latest cluster_  
_`:maxClusterGap`: Max number of days that is considered a cluster_

For example, assume the following transfers are available in the app:  
<img src="../img/development/analysis/small_example_transfers.png" height="512"/>

If `:date` is something like Dec 29, 2025, the analysis would group the transfers into the following two clusters:

Latest cluster:
* Share Investment (Dec 25, 2025)
* Health Insurance (Dec 24, 2025)
* Salary (Dec 22, 2025)
* Taxes (Dec 22, 2025)

Previous cluster:
* Health Insurance (Nov 28, 2025)
* Salary (Nov 26, 2025)
* Taxes (Dec 26, 2025)

Each of these clusters is analyzed separately, so that the presentation layer can show differences between the most recent incomes and expenses to the previous ones.

#### 1.2.2 Analysis of Each Cluster
For each cluster, the transfers are summarized by type. For each type (e.g. "Salary", "Taxes", "Health Insurance" or "Share Investment"), the sums of incomes and expenses are counted. This is illustrated by the following workflow:  
![Small workflow clusters](../img/development/analysis/small_workflow_clusters.drawio.svg)

The algorithm assures that a maximum of three types is returned for incomes and expenses each. If the cluster contains transfers of more than three types, all but the 3 types with the largest sums are summarized into a single instance.

This algorithm is called two tines: Once to calculate the sums of incomes and once to calculate the sums of expenses.

<br/>

### 1.3 Final Result
The final result of the small analysis is described by the following UML diagram:  
![Small UML classes](../img/development/analysis/small_uml_classes.drawio.svg)

The result contains two instances of `SmallMonthResult`, one for each cluster queried at the start. These can be used to display differences between the last two months (or clusters) in the user interface.

The presentation layer displays the result as follows on the main screen:  
<img src="../img/development/analysis/small_example_result.png" height="512"/>

<br/>

### 1.4 Test Strategy
The small analysis is implemented using a single use case class in the application layer. This is only suitable since the analysis algorithm is rather simple and hence does not need any separation into further isolated testable steps. Therefore, we use unit tests to test the final outcome of the analysis for correctness.

Currently, the following test cases are implemented:
Test | State
--- | ---
data without special cases should be analyzed | :green_circle: Passing
more types then limit should make last types grouped | :green_circle: Passing
exactly 3 types should not be grouped | :green_circle: Passing
exactly 4 types where last type should get summarized | :green_circle: Passing
no data should return empty result | :green_circle: Passing

<br/>

***

## 2 Large Analysis
The large analysis is the mroe powerful out of the two analysis algorithms used in Cha Ching. This section describes the large analysis.

### 2.1 Workflow
> [!NOTE]
> Global workflow is introduced once all analysis steps are finished

### 2.2 Analysis Steps
This section describes each of the analysis steps that are mentioned [above](#21-workflow).

#### 2.2.1 Analysis Data Summary
The first step of the analysis is the summary of the data and the converting into a suitable format, as described by the following workflow:

![Analysis flow summarizer](../img/development/analysis/analysis_flow_summarizer.drawio.svg)

First, all transfers are groubed by type.
The transfers for each type are then grouped by a normalized date and summarized afterwards. The normalized date is determined based on the analysis precision. The resulting list of summarized data (for each normalized date) is then trimmed. This makes sure that in between a start and end date, each normalized date has exactly one item inside the list.

The following schematic describes the result of the summarizer:

![Analysis result summarizer](../img/development/analysis/analysis_result_summarizer.drawio.svg)

<br/>

***

2025-12-29  
&copy; Christian-2003
