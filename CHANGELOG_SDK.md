![Catapush Logo](https://github.com/Catapush/catapush-ios-sdk-example/blob/master/catapush_logo.png)

# Catapush Android SDK Changelog

## Catapush 10.x

Catapush 10.x targets Android 10.0 (API 29).

#### 10.0.5

- Enable and force TLS v1.2 on Android 4.1-4.4 devices
- Fix a crash when processing an updated FCM push token in the background
- Catapush UI components: don't crash when removing an item at an invalid position in CatapushMessagesAdapter

#### 10.0.4

- Improved errors handling and reporting see the [official documentation](https://www.catapush.com/docs-android-2) for details
  - `GenericLibraryException` has been replaced by `LibraryConfigurationException` and `SystemConfigurationException`
  - `ConnectionErrorCodes` has been removed, its error codes has been moved to `CatapushAuthenticationError` and `CatapushConnectionError`
- Improved support for image attachments previews, see `CatapushMessage.file().thumbnailUri()` field
  - These thumbnails are securely stored in a subfolder of your app private directory
  - You can display the thumbnail while downloading the high-resolution version of the attachment from `CatapushMessage.file().remoteUri()`

#### 10.0.3

- Revised authentication protocol to improve performances
- Catapush UI components: improved SendFieldView reply layout

#### 10.0.2

- `CatapushMessageTouchHelper` now takes as input a `SwipeBehavior` parameter so you can delete or reply to messages by swiping on them using respectively `RemoveOnSwipeBehavior` or `ReplyOnSwipeBehavior`. This replaces the previous `Callback<CatapushMessage>` parameter, but you can now set the same callback directly to `RemoveOnSwipeBehavior` or `ReplyOnSwipeBehavior`.
- The new `CatapushMessagesAdapter.reply(…)` methods lets you reply to a message in a given position. Please note: this will only work if you instantiate `CatapushMessagesAdapter` with a non-null `SendFieldViewProvider`
- The `CatapushMessagesAdapter.ActionListener` interface has been improved to give more information about its events
  - `onPdfClick` and `onImageClick` callbacks have now 3 parameters: the tapped `View`, the `CatapushMessage` and its position in the adapter
  - The new `onMessageLongClick` callback notifies you when a message gets long-clicked, it has 3 parameters like `onPdfClick` and `onImageClick`
- The new `CatapushMessageContextualMenuHelper` is a ready-to use utility to show a contextual menu on a `CatapushMessage` `View`, it is intended to be used with the new `ActionListener.onMessageLongClick` callback to quickly show reply, copy and delete actions

#### 10.0.1

- The `CatapushMessageTouchHelper` class `deleteCallback` parameter now returns the removed item instance instead of its position

#### 10.0.0

- Updated Android target SDK level to 29
- Added support to the new `channel` field, you can now group messages by channels (conversations); see [Catapush API docs](https://www.catapush.com/docs-api?php#2.1-post---send-a-new-message) for details
- The `Notification` class has been renamed to `NotificationTemplate`
- The `Catapush.setPush(…)` method has been renamed to `Catapush.setNotificationTemplate(…)`
- The `Catapush.clean()` method now requires a `Callback<Boolean>` as parameter to deliver its result
- The new `Catapush.clearMessages(…)` lets you delete all the stored messages without clearing the SDK configuration and user credentials
- Added new methods to query the stored messages database:
  - `Catapush.getMessagesAsList(…)`
  - `Catapush.getMessagesWithoutChannelAsList(…)`
  - `Catapush.getMessagesFromChannelAsList(…)`
- Using the Android Jetpack Paging Library new methods have been added to access the stored messaged database ad `DataSource`:
  - `Catapush.getMessagesAsDataSourceFactory(…)`
  - `Catapush.getMessagesWithoutChannelAsDataSourceFactory(…)`
  - `Catapush.getMessagesFromChannelAsDataSourceFactory(…)`
- It's now possible to count the unread messages, see:
  - `Catapush.countUnreadMessages(…)`
  - `Catapush.countUnreadMessagesWithoutChannel(…)`
  - `Catapush.countUnreadMessagesFromChannel(…)`
- Improved status bar notification title handling: the title will be the received message `subject` if present, else the notification template `title`; if none available, it will fallback to the default string `There is a new message`
- The packages structure of the Catapush UI components has been reworked, if you were using them you will need to fix their import statements
- The `CatapushRecyclerViewAdapter` class has been renamed to `CatapushMessagesAdapter`
- Add support for channels to Catapush UI components, see `CatapushConversationsAdapter`
- Improved the layouts of the Catapush UI components

## Catapush 9.x

Catapush 9.x targets Android 9.0 (API 28).
The SDK have been migrated from Android Support Library to Android Jetpack (AndroidX).

#### 9.0.17

- Improved errors handling and reporting see the [official documentation](https://www.catapush.com/docs-android-2) for details
  - `GenericLibraryException` has been replaced by `LibraryConfigurationException` and `SystemConfigurationException`
  - `ConnectionErrorCodes` has been removed, its error codes has been moved to `CatapushAuthenticationError` and `CatapushConnectionError`

#### 9.0.16

- Solved an issue with older devices and unsupported cryptographic algorithms

#### 9.0.15

- Solved a cache inconsistency that prevented user switching for some use cases
- The consumer Proguard configuration file has been updated with *protobuf* specific rules

#### 9.0.14

- Revised authentication protocol to improve performances
- Resolved a packaging error introduced in version 9.0.13

#### 9.0.13

NOTE: This release was withdrawn because of packaging issues

- Add a workaround to avoid error `4043 Stored user is invalid`

#### 9.0.12

- Add workaround to avoid `PendingIntent`s extras `Bundle` recycling; this is to ensure that, when the user touches a status bar notification, the correct message gets relayed to the app

#### 9.0.11

- New server-side sessions debugging feature

#### 9.0.10

- For testing purposes, it's possible to override the Catapush App Key using the deprecated `Catapush.setAppKey(…)` method
- Do not start Catapush on device boot if user credentials haven't been set

#### 9.0.9

- Connectivity checks have been improved to also work in China
- `CatapushTwoWayReceiver.onReloginNotificationClicked(…)` is now deprecated and ineffective

#### 9.0.8

- Improved implementation of some API requests

#### 9.0.7

- Added support to the new `replyTo` field, you can now set the ID of the message you're replying to
- Improved the layouts of the Catapush UI components

#### 9.0.6

- It's now possible to implement a custom FirebaseMessagingService and relay Catapush FCM wakeup notifications to the Catapush SDK using the new `Catapush.handleFcmWakeup(…)` and `Catapush.handleFcmNewToken(…)` methods

#### 9.0.5

- Improve internal NTP implementation to use more NTP pools

#### 9.0.4

- Bug fixes on internal NTP implementation

#### 9.0.3

- Stability improvements to the Catapush UI components
- The `MessagingService` foreground service notification (`Checking for new messages…`) importance is now correctly set to the lowest value: `NotificationManager.IMPORTANCE_LOW` instead of  `NotificationManager.IMPORTANCE_MIN`

#### 9.0.2

- The Catapush Android SDK won't create the local `NotificationChannel` on your behalf, from now on you must provide the ID of an already-created `NotificationChannel`; the `Catapush.init(…)` method has been updated accordingly removing the `channelName` parameter

#### 9.0.1

- Improve internal database model

#### 9.0.0

- Updated Android min SDK level to 16 and target SDK level to 28
- Migrated from the deprecated Android Support Library to its new replacement: Android Jetpack (AndroidX)
- Added support to the new `optionalData` field, see [Catapush API docs](https://www.catapush.com/docs-api?php#2.1-post---send-a-new-message) for details
- The Catapush UI components now use [Glide](https://github.com/bumptech/glide) to load messages image attachments
- The `Catapush.start(…)` method now takes a new `RecoverableErrorCallback` that has the new `warning` method to notify about device configuration issues
- It's now possible to specify a bigger (48x48dp) icon to be used in modal notifications: see `Notification.Builder.modalIconId(@DrawableRes int)`
