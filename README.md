Expense Input         |Income Input
:----------------------------------:|:----------------------------------:
<img width=300 height=600 src="https://media3.giphy.com/media/hxbN1u73aVHINK9zRq/giphy.gif"/> | <img width=300 height=600 src="https://media3.giphy.com/media/9VnRSBLTAj4H4fexMH/giphy.gif"/>

# Development
To Build this app, you need Google Developer Account.
Use this <a href="https://developers.google.com/identity/sign-in/android/start-integrating"> link </a> to generate OAuth Keys into google cloud.


Now on your development machine goto

- Windows: `C:\Users\<Your Username>\.gradle`
- Mac: `/Users/<Your Username>/.gradle`
- Linux: `/home/<Your Username>/.gradle`

and then add the following 2 lines to gradle.properties file (create one if it doesn’t exist).

`GOOGLE_API_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"`
`WEB_CLIENT_ID = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"`
`ANDROID_CLIENT_SECRET = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"`

where `xxxxxxxxxxxxxx` is the google_api_key, web_client_id & android_client_secret that you will get from Google Api Console.

