<img src="../img/icon.png" height="150" align="right">

# Architecture
This document describes the architecture of the app Cha Ching.

###### Table of Contents
1. [Domain Driven Design](#domain-driven-design)
2. [Clean Architecture](#clean-architecture)
3. [Architecure Pattern MVVM](#architecure-pattern-mvvm)

<br/>

## Domain Driven Design
The app utilizes the principle of Domain Driven Design (DDD) to ensure high quality and good maintainability within the core business logic.

###### Ubiquitous Language
The ubiquitous language (UL) of the domain model consists of the following terms:

Term | Description
--- | ---
User | A user is a person that uses the app. Users want to use the app to track their own personal incomes. One of the main goals is to get motivated by seeing the amount of money earned in a given time period.
Type | A type describes an income of the user. Since each user might have individual needs that might change over time, there are no types that are useful to all users. Therefore, types can be created by users at will in order to better describe incomes. Examples for types are 'salary', 'sick pay', 'vacation pay' and 'bonuses'.<br/>Since no type applies to all users, a different user might need more specilized types like 'salary work 1', 'salary work 2' and 'bonuses'.
Transfer | A transfer is a transaction that _transfers_ money to the user. A transfer might contain the amount of money that is earned through a monthly salary. Other transfers might be sick pay, vacation pay or bonuses.

By design, there are relations between the terms of the UL. These relations can be described as follows:

* A user can create an unlimited number of types.
* A user can edit a type. Editing a type might change the name, an icon or other visual aspects that help the user identify a type.
* A user can delete a type. Deleting a type will delete all transfers that are associated with the type.
* A user can create an unlimited number of transfers for each type.
* A user can edit a transfer. Editing a transfer might change the value of the income or the value date.
* A user can delete a transfer.
* Each transfer is associated with exactly one type.
* A transfer cannot exist without being associated with a type.
* Each type can have an unlimited number of transfers.

###### Implementation of Domain Logic
The domain logic is implemented within the domain layer of the application. For further details about the layers of the app, see [Clean Architecture](#clean-architecture).

The domain layer contains the domain model which consists of the following classes:

Class | Description
--- | ---
`Type` | This entity models the type, which consists of a name and additional metadata. Furhermore, each type has an icon which is used to visually distinguish a type from other types. The user can choose an icon from a selection of available icons which are stored in the enum `TypeIcon`.
`TypeIcon` | For each icon the user can select for a `type`, this enum contains a field.
`Transfer` | This entity models a transfer.
`TypeRepository` | Repository is used to access types from the internal app storage.
`TransferRepository` | epository is used to access transfers from the internal app storage.

For more info on how the app stores data, see [Database](Database.md).


<br/>

## Clean Architecture
In addition to [DDD](#domain-driven-design), the app utilizes clean architecture principles to ensure maintainability.

###### Layers
The clean architecture layers are displayed in the following graphic:

![](../img/development/clean_architecture.drawio.svg)

This graphic explains that the app consists of the following three layers:
* **Domain:** This layer contains the core domain model, as described [here](#implementation-of-domain-logic).
* **Application:** This layer contains the core business logic, such as backup creation or data analysis.
* **Plugin:** This layer contains all dependencies to other libraries. The layer is divided into a presentation package which contains all user interface code and a infrastructure package which contains infrastructure dependencies, such as Room database or JSON serialization.

The app does not have a adapter layer. Instead, the adapter layer is merged with the plugin layer.

###### Package Structure
Clean architecture is realized through the app package structure:
```
de.christian2003.chaching
+-- domain
|   +-- transfer
|   +-- type
|   +-- repository
|   +-- analysis
|
+-- application
|   +-- backup
|
+-- plugin
    +-- infrastructure
    |   +-- backup
    |   +-- db
    |   +-- update
    |
    +-- presentation
        +-- ui
        +-- view
```

<br/>

## Architecure Pattern MVVM
For the user interface in the `plugin.presentation`-layer, the app uses the Model View ViewModel architecture pattern.

![](../img/development/architecture_pattern.drawio.svg)

<br/>

***

2025-06-25  
&copy; Christian-2003
