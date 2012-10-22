google-play-client
==================

Java client for using Google Play

WHAT THIS IS:
This is a stateless Google Play client inspired by:
- Jens Kristian Villadsen's Java client (https://github.com/jkiddo/gmusic.api) and
- Simon Weber's original Python client (https://github.com/simon-weber/Unofficial-Google-Music-API).

The main reason I created this client is that I wanted a stateless implementation. This makes using multiple Google Play accounts simultaneously easy. This is helpful for web applications that may have multiple users who wish to play music simultaneously.

GOOGLE PLAY REST API:
This client library wraps the Google Play REST API. I have documented this API here: https://docs.google.com/document/d/1-O3xIlBVnLDNLdeM9s2-_yj0XGn-WHjk19s-YYphVF0/edit

WHAT IT DOES:
This client library is lacking many features right now, because I personally didn't need them. I would welcome any new feature someone would like to contribute. I may get around to adding more in the future.

You can currently:
- Login to Google Play
- Search for tracks
- Get a playable track URL
- Retrieve all of the tracks in the user's collection
- Retrieve all of the playlists in the user's collection

HOW TO USE IT:
This code is written in Java and builds using Maven 2.

There are two separate Maven modules:
1. The client code and its unit tests
2. Acceptance tests

In order to create the client JAR and install it in your local Maven cache for use in your own projects, run "mvn install" in the "google-play-client" folder.

In order to run the acceptance tests, first install the client JAR. Then edit the "Test.properties" file in the "google-play-client-tests" folder with your details. Finally, run "mvn test" in the "google-play-client-tests" folder.

EXAMPLE CODE:
A great example of how to use the client is in the PlayClientIntegrationTest.java file found in the google-play-client-tests module. 

THE LICENSE:
This code is released under the LGPL -- this basically means that you can use it in whatever project you want without restriction, but if you modify this library code you need to publish your modifications. Posting it on GitHub and sending a pull request to me would be a good way to do that.

HOW TO CONTRIBUTE:
I welcome contributions to this library. Please add unit tests and acceptance tests for your new feature or defect fix. I will not accept pull requests for code that does not compile or breaks the tests.
