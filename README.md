# Spotify Streamer

This is the first project for the Android Nanodegree.  It's broken down into two stages:
* Stage 1 allows the user to serach for an artist and display that artist's top 10 tracks
* Stage 2 TBD

## Implementation Details
* Icons from material icon pack: https://www.google.com/design/icons/
* Used helper class to write to and read from a Bundle for when the device is rotated.  Initially I thought about implementing `Parcelable` on the model classes.  However, since only a few of the class fields are used in the adapters I felt that parcelizing only a few fields doesn't accurately preserve the entire model via parceling.  Thus, only the consumers of the data (fragments and adapters) control what fields should be written / read.
* Master/Detail implementation based on Android Studio's template
* Used tips from http://developer.android.com/guide/components/bound-services.html for `PlaybackService`
* When the 'next' or 'previos' button is tapped: (mimics behavior of Google Play Music)
  * The next or previous track starts playing even if the current track was paused
  * The first track of the list will play if the current track is the last one on the list
  * The last track of the list will play if the current track is the first one on the list

## Tests
To run tests: `./gradlew test --continue`
