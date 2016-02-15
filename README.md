![Catapush Logo](https://github.com/Catapush/catapush-ios-sdk-example/blob/master/catapush_logo.png)

# Catapush Android Example

This project shows how quickly Catapush Android SDK can be integrated into your current app to receive Catapush messages and display them with a customizable bubble layout. Check out the official website: [Catapush - reliable push notification service](http://www.catapush.com).

[![](/images/flight_message.png?raw=true)]()

## Usage

1. Clone this repo;
2. Create a new app on [Catapush Dashboard](http://www.catapush.com/)
3. Place your *Catapush App Key* and *GCM SenderID* in the [MainActivity](https://github.com/Catapush/catapush-android-sdk-example/blob/master/app/src/main/java/com/catapush/example/app/MainActivity.java#L63)
3. Create a new user with your [Catapush Dashboard](http://www.catapush.com/) and, in [MainActivity](https://github.com/Catapush/catapush-android-sdk-example/blob/master/app/src/main/java/com/catapush/example/app/MainActivity.java#L65), replace example identifier and password with your proper ones;
4. Run the app;
5. Back to your [Catapush Dashboard](http://www.catapush.com/) and send some important message.

Note:
Catapush needs Google Play Services. Currently, Google and Genimotion do not provide an Android Emulator with Google Play Services. You should run this app on a real world device or install GApps on your emulator.

## Advanced

Catapush SDK comes with a native ready-to-go solution to display your messages.

### Catapush RecyclerView Adapter

Catapush SDK provides full support for `RecyclerView` via `CatapushRecyclerViewAdapter`. This adapter can receive a list of messages and display them in a stylish bubble cell:

![](/images/messages.1.png?raw=true)

### Customization
#### Colors
Catapush provides a default color scheme that you can override to achieve the user experience you want. To customize the color scheme, you will need to create these colors in your **res/values/colors.xml**:

```html
<color name="catapush_message_list_item_bg">#b6e6ed</color>
<color name="catapush_message_border_color">#b6e6ed</color>

<color name="catapush_message_title_color">#ff0000</color>
<color name="catapush_message_subtitle_color">#999292</color>
<color name="catapush_message_datetime_color">#ff9d9d9d</color>
```

Changing these colors, you will change the previous image into this:

[![](/images/messages.2.png?raw=true)]()

#### Text
Catapush provides a default text style that you can override to achieve the user experience you want. To customize the text, you will need to create these styles in your **res/values/styles.xml**

```html
<style name="catapush_message_title" parent="android:Widget.TextView">
    <item name="android:singleLine">false</item>
    <item name="android:textColor">@color/catapush_message_title_color</item>
    <item name="android:textSize">@dimen/catapush_message_title_size</item>
    <item name="android:typeface">sans</item>
    <item name="android:textStyle">normal</item>
</style>

<style name="catapush_message_subtitle" parent="android:Widget.TextView">
    <item name="android:textColor">@color/catapush_message_subtitle_color</item>
    <item name="android:textSize">@dimen/catapush_message_list_item_subtitle_size</item>
    <item name="android:textStyle">bold</item>
    <item name="android:typeface">sans</item>
</style>

<style name="catapush_message_datetime" parent="android:Widget.TextView">
    <item name="android:textColor">@color/catapush_message_datetime_color</item>
    <item name="android:textSize">@dimen/catapush_message_list_item_datetime_size</item>
    <item name="android:textStyle">italic</item>
    <item name="android:typeface">sans</item>
</style>        
```

These styles relay on **colors** and **dimensions**. You can override Catapush default dimensions adding these values to your **res/values/dimens.xml** file:

```html
<dimen name="catapush_message_title_size">22sp</dimen>
<dimen name="catapush_message_list_item_padding">5dp</dimen>
<dimen name="catapush_message_list_item_subtitle_size">12sp</dimen>
<dimen name="catapush_message_list_item_datetime_size">6sp</dimen>
```

Changing these dimensions like this will produce this result:

![](/images/messages.3.png?raw=true)

#### Corners
Catapush message bubbles come with rounded corners. You can change them overriding this property in your **res/values/dimens.xml** file:

    <dimen name="catapush_message_corner_radius">10dp</dimen>

If you don't want rounded corners at all, simply set it to `0dp`.

## Contatcs
If you need help, you have feedbacks or questions, send us a mail at **info [at] catapush [dot] com** or join us on [Gitter](https://gitter.im/Catapush/support).

[![Analytics](https://ga-beacon.appspot.com/UA-60582915-4/android-example/readme)](https://github.com/igrigorik/ga-beacon)

