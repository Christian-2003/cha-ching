<img src="docs/img/icon.png" height="150" align="right">

# Changelog

## 1.2.0 (2025-08-08)

###### Release Highlights
Add an app widget showing the money earned in the last month. Furthermore, types can be enabled and disabled for the quick access on the home screen.

###### Features
* Implement a `VisualTransformation` to handle grouping- and decimal-separators for better user experience and reusability.
* Fix a bug where the text input to enter the hours worked was not visible sometimes.
* `TransferScreen` shows the name of the type for which a transfer is being created or edited.
* Move database and repository declaration from `MainActivity` to `ChaChingApplication` to make them accessible from Android widgets.
* Implement a custom overview widget which shows the total amount of money earned in the last 31 days.
* Introduce app mascot.
* Change empty placeholder icon for transfers within the app to match app mascot.
* Change the app icon to match the app mascot.
* Change the splash screen to match new app icon.
* Add "Compose Animation Gaphics" to app in order to use animated vector drawables with Jetpack Compose.
* Add dialog displaying info about quick access visibility on `MainScreen` for types.
* Transfer types can be removed from quick qccess on `MainScreen`.
* Revert to original app icon and splash screen.
* Add alterantive icon for app in debug build to better distinguish between release and debug installations.
* Change build pipeline to automatically generate the correct APK file name.

<br/>

## 1.1.1 (2025-07-13)

###### Release Highlights
This update focuses on important bugfixes that result in app crashes or bad user experience.

###### Features
* Fix a bug where some decimal numbers (e.g. "300.03") cannot be entered in the app with a desired precision.
* Fix a bug where the average of the analysis was calculated as average for types instead of average for month / quarter / year.
* Fix a bug where the app crashes if a date range for the analysis is selected, without changing either default start date OR default end date.
* Date picker through which to select the value date for a transfer now displays the correct month of the value date selected.
* Fix a bug where the app crashes during analysis if less than 4 types are available.

<br/>

## 1.1.0 (2025-06-29)

###### Release Highlights
Introduction of the transfer analysis. The analysis can analyze the transfers for a specified period of time.

###### Features
* Introduction of clean architecture and domain driven design to decrease development times in the future
* Importing a backup with option 'Replace duplicates' no longer deletes transfers that are not part of the backup
* Transfers in `TransfersScreen` are grouped by month for better user experience. Each month has a header displaying the month name
* Add animated transitions between screens
* Add analysis for transfers
* Add quick actions to `MainScreen`
* Visual enhancements to `MainScreen`

<br/>

## 1.0.0 (2025-06-08)

###### Release Highlights
Created app with basic features, such as transfer documentation by transfer types.

###### Features
* Transfer types can be created / edited / deleted
* Transfers can be created / edited / deleted
* App checks for app updates on startup
* Add onboarding feature
* Add basic analysis on MainScreen
* Data can be import / exported
* Dimissed help messages can be reactivated through settings
