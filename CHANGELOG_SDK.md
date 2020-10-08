![Catapush Logo](https://github.com/Catapush/catapush-ios-sdk-example/blob/master/catapush_logo.png)

# Catapush Android SDK Changelog

## Catapush 10.2.x

Catapush 10.2.x targets Android 10.0 (API 29) and requires Android 4.1 (API 18).

The SDK has now been split in different artifacts to reduce its size and optimize your project build times.  
This rework will give us the ability to add new new feature modules in the near future!

The available modules are:
- `core` the main Catapush SDK implementation
- `gms` the integration of Catapush SDK with Google Mobile Services / Firebase Cloud Messaging
- `ui` the Catapush UI Components

Replace your Catapush SDK dependency declaration in your app `build.gradle` file to include the `gms` module:
```
implementation('com.catapush.catapush-android-sdk:gms:10.2.+')
```
The `core` module will be automatically added as a transitive dependency.

If you also need the Catapush UI Components add the `ui` module as a dependency with this line:
```
implementation('com.catapush.catapush-android-sdk:ui:10.2.+')
```

#### 10.2.7

- Fixed an error introduced in version 10.2.6 that might prevent messages from being displayed as status bar notifications

#### 10.2.6

- Added the new callback `onPushServicesError(PushServicesException, Context)` to `CatapushReceiver` and `CatapushTwoWayReceiver`.
  This callback will forward you any error raised by the device push services installation
  - Please update your `CatapushReceiver` and `CatapushTwoWayReceiver` implementation, see the class `com.catapush.example.app.communications.SampleReceiver` from this repository as a reference
  - Update your `AndroidManifest.xml` to add a filter to your receiver for the new action `com.catapush.library.action.PUSH_SERVICE_ERROR`
- Fixed an error-handling related bug that prevented the SDK from starting on devices without push services
- Fixed an error that may cause some notifications to not be displayed in the status bar when multiple messages are delivered in a short time

#### 10.2.5

- The SDK will now connect to Catapush even if no push services providers are available and working on the device.
  This will allow foreground messaging on devices running Android 8.0+ and also background messaging on previous Android versions.
- If the SDK is started with new user credentials while already running it will automatically stop and disconnect the previous user before connecting the new one.
- Fixed a crash on devices without GMS when including the `com.catapush.catapush-android-sdk:gms` module

#### 10.2.4

- Add a new method Catapush.getInstance().rebuildSecureCredentialsStore(…) that clean secure store contents, delete cryptographic keys, recreate them and rebuild the secure store.
  Might be helpful when the Android KeyStore refuses to load or use the current keys.

#### 10.2.3

- Improve DNS resolution strategy

#### 10.2.2

- Fix: the "checking for new messages" notification wouldn't automatically get canceled on some older phones
- Minor improvements on secure credentials storage initialization

#### 10.2.1

- Minor improvements on secure credentials storage initialization

#### 10.2.0

- The `Catapush.init(…)` method has been updated and requires an additional `List<ICatapushMobileServicesAdapter>` parameter.
  i.e. if you are using GMS/FCM: `Catapush.getInstance().init(context, channelId, Collections.singletonList(CatapushGms.INSTANCE), …`
- Minor improvements on secure credentials storage initialization

## Catapush 10.1.x

Catapush 10.1.x targets Android 10.0 (API 29) and requires Android 4.1 (API 18).

#### 10.1.0

- Minimum SDK level raised to 18.
- The cryptographic key used to encrypt user credentials are now stored into Android KeyStore.
- You can listen for the secure credentials store initialization status providing a callback through `Catapush.setSecureCredentialsStoreCallback(…)`, please use this method before invoking `Catapush.init(…)`
  - `Callback.success(…)` will be invoked as soon as the secure credentials store is initialized successfully.
  - `Callback.failure(…)` is invoked when an exception is raised while configuring the secure credentials store.
- New feature: received message transformation. You can now manipulate the body of the messages received through Catapush before they get persisted inside the local database. See `Catapush.setMessageTransformation(…)`, please use this method before invoking `Catapush.init(…)`
  - The body property will be used to display the full content of the message to the user i.e. Android notifications multiline layout, see `Notification.BigTextStyle` class.
  - The previewText property will be used to display a short preview of the message to the user i.e. Android notifications single line layout, see `Notification.Builder(…).setContentText(…)`.

## Catapush 10.0.x

Catapush 10.0.x targets Android 10.0 (API 29) and requires Android 4.1 (API 16).

⚠️ Please note:

If your app is using Catapush 9.1.x the only upgrade path supported is to Catapush 10.1.x or higher.
Do not update active installations to Catapush 10.0.x if you're using Catapush 9.1.x.

#### 10.0.6

- New feature: use `NotificationTemplate.useAttachmentPreviewAsLargeIcon()` to use image attachments thumbnails as large icons for status bar notifications
- Catapush UI components: add support for GIF attachments
- Catapush UI components: improve image message layout measuring to avoid scrolling issues

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

## Catapush 9.1.x

Catapush 9.1.x targets Android 9.0 (API 28) and requires Android 4.3 (API 18).
The SDK now supports advanced security features provided by Android KeyStore.

#### 9.1.7

- Add a new method Catapush.getInstance().rebuildSecureCredentialsStore(…) that clean secure store contents, delete cryptographic keys, recreate them and rebuild the secure store.
  Might be helpful when the Android KeyStore refuses to load or use the current keys.

#### 9.1.6

- Improve DNS resolution strategy

#### 9.1.5

- Fix: the "checking for new messages" notification wouldn't automatically get canceled on some older phones
- Minor improvements on secure credentials storage initialization

#### 9.1.4

- Minor improvements on secure credentials storage initialization

#### 9.1.3

- Minor improvements on secure credentials storage initialization

#### 9.1.2

- Minor fix to avoid persistence of the "checking for new messages" notification in the status bar.

#### 9.1.1

- Improve encryption/decryption on Android 6.0+ (API 23) devices.

#### 9.1.0

- Minimum SDK level raised to 18.
- The cryptographic key used to encrypt user credentials are now stored into Android KeyStore.
- You can listen for the secure credentials store initialization status providing a callback through `Catapush.setSecureCredentialsStoreCallback(…)`, please use this method before invoking `Catapush.init(…)`
  - `Callback.success(…)` will be invoked as soon as the secure credentials store is initialized successfully.
  - `Callback.failure(…)` is invoked when an exception is raised while configuring the secure credentials store.
- New feature: received message transformation. You can now manipulate the body of the messages received through Catapush before they get persisted inside the local database. See `Catapush.setMessageTransformation(…)`, please use this method before invoking `Catapush.init(…)`
  - The body property will be used to display the full content of the message to the user i.e. Android notifications multiline layout, see `Notification.BigTextStyle` class.
  - The previewText property will be used to display a short preview of the message to the user i.e. Android notifications single line layout, see `Notification.Builder(…).setContentText(…)`.

## Catapush 9.0.x

Catapush 9.0.x targets Android 9.0 (API 28) and requires Android 4.1 (API 16).
The SDK have been migrated from Android Support Library to Android Jetpack (AndroidX).

#### 9.0.19

- Repackaged version to avoid dependency resolution conflicts

#### 9.0.18

- Fixed a bug that occurred when updating the Firebase Messaging push token but no user was configured in the SDK

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
