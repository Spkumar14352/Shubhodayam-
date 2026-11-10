# Panchang home-screen widget

Shubhodaya is a native Kotlin/Compose app, so the widget talks to the app directly and needs no
Capacitor packages. The app already saves the data. These files add the widget itself.

Gradle ignores this folder until the files are copied.

## Where each file goes

| File in `android-widget/` | Copy to |
| --- | --- |
| `PanchangWidgetProvider.kt` | `app/src/main/java/com/example/widget/PanchangWidgetProvider.kt` |
| `res/layout/widget_panchang.xml` | `app/src/main/res/layout/widget_panchang.xml` (create the `layout` folder) |
| `res/xml/panchang_widget_info.xml` | `app/src/main/res/xml/panchang_widget_info.xml` |
| `res/drawable/widget_bg.xml` | `app/src/main/res/drawable/widget_bg.xml` |
| `AndroidManifest-receiver.xml` | Do not copy the file. Paste its `<receiver>` block into `app/src/main/AndroidManifest.xml`, inside `<application>`, for example just before `</application>`. |

Then rebuild, install, **open the app once** so it saves today's data, and add the widget:
long-press the home screen → Widgets → Shubhodaya.

## Already in the app

| What | Where |
| --- | --- |
| Saves today's and tomorrow's Panchang, and `refresh()` redraws the widget | `app/src/main/java/com/example/widget/WidgetUpdater.kt` |
| Saves on app open, at midnight while the app is running, and when the language or city changes | `MainViewModel.observeWidgetData()` |
| "Add widget to home screen" steps | Settings, in `SettingsSubScreen` |

## Saved data

SharedPreferences file `CapacitorStorage`, key `widget_panchang`:

```json
{
  "date": "2026-11-09",
  "lang": "te",
  "vaaram": "సోమవారం",
  "tithi": "...", "tithiEnds": "14:20", "tithiEndsNextDay": false,
  "nakshatra": "...", "nakshatraEnds": "02:45", "nakshatraEndsNextDay": true,
  "sunrise": "06:14", "sunset": "17:41",
  "rahuKalam": "07:40 - 09:06",
  "festival": "",
  "tomorrow": { "date": "2026-11-10", "...": "same fields" }
}
```

`tithiEndsNextDay` and `nakshatraEndsNextDay` were added because a time like `02:45` alone can't
show that it falls after midnight. The widget uses them to say "tomorrow".

## How the widget picks what to show

Android redraws the widget about every 30 minutes, and each redraw compares the phone's date with
the saved data:

- Matches `date`: shows today's block.
- Matches `tomorrow.date`: shows tomorrow's block, so the next morning is right without opening the app.
- Matches neither, or nothing is saved: shows "open the app" instead of an old day's Panchang.

The widget can run out of data if the app isn't opened for more than a day.
