# Mu-RulesApp-Merhawi

MU RulesApp is a mobile application developed using Android Studio and Kotlin. It provides a digital platform to access campus rules and regulations, organized into chapters and subsections. The app also includes features like user authentication, favorite sections, and dynamic content retrieval from Firebase Realtime Database.

Features

Firebase Integration: Fetches and displays rulebook content from Firebase Realtime Database.

User Authentication: Users can sign in using Google or through standard registration and login.

Favorites: Save and access favorite sections for quick reference.

Dynamic Navigation: Navigate through chapters and sections smoothly.

Offline Persistence: Firebase offline capabilities for accessing content without internet connection.


Screenshots

Add screenshots of your app here if possible.

Tech Stack

Language: Kotlin

IDE: Android Studio

Database: Firebase Realtime Database

Authentication: Google Sign-In and Email/Password Authentication

Version Control: GitHub


Installation

1. Clone the repository:

git clone <repository-link>


2. Open the project in Android Studio.


3. Sync Gradle files and ensure all dependencies are installed.


4. Set up Firebase:

Go to Firebase Console and create a project.

Download google-services.json and place it in the app directory.

Ensure your package name matches the one registered in Firebase.



5. Build and run the app on an emulator or physical device.



Firebase Setup

Ensure the following in Firebase Console:

Authentication: Enable Google Sign-In under Authentication → Sign-in Method.

Database Rules: Set rules to allow read and write operations:

{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}


Usage

Open the app and sign in using Google or email and password.

Navigate through chapters and subsections.

Mark sections as favorites for easy access.

Return to the main screen using the navigation buttons.


Contributing

Feel free to open issues or submit pull requests. For major changes, please open an issue first to discuss what you would like to change.

License

This project is licensed under the MIT License - see the LICENSE file for details.

Acknowledgments

Android Developer Documentation

Firebase Documentation

Open-source libraries used in the project


Contact

For any questions or inquiries, feel free to reach out:

Name: Merhawi 
Email: merhawitareke27@gmail.com 
