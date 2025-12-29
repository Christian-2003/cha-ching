<img src="../img/icon.png" height="150" align="right">

# Database
This document describes the database architecture for the app Cha Ching.

###### Table of Contents
1. [Database Scheme](#database-scheme)
2. [Relations](#relations)

<br/>

## Database Scheme
The database scheme can be described through the following UML diagram:

![](../img/development/database/database_scheme.drawio.svg)

###### `transfers` Table
The database table transfers is modeled through the class `TransferEntity`. The table has the following attributes:

&nbsp; | Attribute | Remarks
--- | --- | ---
Primary Key | transferId | Unique type 4 UUID for the transfer within the database.
Foreign Key | type | UUID of the type of the transfer.
&nbsp; | hoursWorked | Stores the number of hours that were worked for the transfer.
&nbsp; | valueDate | Stores the value date of the transfer in epoch days.
&nbsp; | value | Value of the transfer in cents. A value of "$ 3,500.89" would be stored as 350089
&nbsp; | isSalary | Field indicates whether the transfer is an income or not. Currently, this field is unused and always set to `true`. In the future, the app might be extended to include the documentation of expenses as well. Expenses will have this field set to `false`.
&nbsp; | created | Stores the date and time on which the transfer was created in epoch seconds. This is used for statistical purposes.
&nbsp; | edited | Stores the date and time on which the transfer was last edited in epoch seconds. This is used for statistical purposes.

###### `types` Table
The database table types is modeled through the class `TypeEntity`. The table has the following attributes:

&nbsp; | Attribute | Remarks
--- | --- | ---
Primary Key | typeId | Unique type 4 UUID for the type within the database.
&nbsp; | name | Stores the name of the type as string. This name is shown to the user.
&nbsp; | icon | Stores the icon of the type. The value within this field corresponds to the ordinal of the icon within the enum `TypeIcon`.
&nbsp; | isHoursWorkedEditable | Field indicates whether transfers for this type should track the hours worked. If this is set to `false`, the hours worked will not be tracked.
&nbsp; | created | Stores the date and time on which the type was created in epoch seconds. This is used for statistical purposes.
&nbsp; | edited | Stores the date and time on which the type was last edited in epoch seconds. This is used for statistical purposes.
&nbsp; | isEnabledInQuickAccess | Indicates whether transfers of this type can be created through the quick access.
&nbsp; | isSalaryByDefault | Indicates whether transfers of this type are created as salary by default.

<br/>

## Relations
The tables `transfers` and `types` have a many-to-one relation.

![](../img/development/database/database_relations.drawio.svg)

Each transfer must have exactly one corresponding type. Therefore, each type can have an unlimited number of transfers.

The relation is realized through the field `type` within the table `transfers`. This field indicates the type associated with the transfer.

If a type is deleted, the deletion is cascaded so that all associated transfers are deleted as well!

<br/>

***

2025-12-29  
&copy; Christian-2003
