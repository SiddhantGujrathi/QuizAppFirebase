# Quiz App

Welcome to the Quiz App repository! This app is designed to provide users with a powerful platform for taking daily tests and tracking their progress. 
Developed in Kotlin, the app leverages Firebase Authentication and Firestore for seamless and secure data management. 

## Features

- **Daily Tests**: Unlimited attempts to practice and improve. Users can easily find tests by date.
- **Date-based Search**: Easily search and retrieve tests by date.
- **Comprehensive Test Storage**: Secure storage for all your tests with the option for users to delete their data if needed.
- **Test Tracking**: Track the number of tests taken and monitor your progress over time.
- **Real-time Daily Rankings**: Live ranking updates to see where you stand among participants.
- **Lottie Animation Integration**: Enhanced user experience with engaging animations.

## Tech Stack

- **Language**: Kotlin
- **Backend**: Firebase Authentication and Firestore
- **Animations**: Lottie

## Installation

1. **Clone the repository:**

   ```
   git clone https://github.com/yourusername/quiz-app.git
   ```

2. **Open the project in Android Studio:**
   
   - Ensure you have the latest version of Android Studio installed.
   - Open the cloned project in Android Studio.

3. **Configure Firebase:**
   
   - Add your `google-services.json` file to the `app` directory.
   - Set up Firebase Authentication and Firestore in the Firebase Console.

4. **Build and Run the Project:**

   - Connect your Android device or use an emulator.
   - Build and run the project through Android Studio.

## Data Import for Admin

Currently, the app doesn't have an option to send data directly to Firebase from the app. 
To import data to Firebase, use the following command:

```bash
npx -p node-firestore-import-export firestore-import -a [private_key].json -b [quizes_collection].json
```

Replace `[private_key].json` with your Firebase private key file and `[quizes_collection].json` with your quizzes collection JSON file.

## Contributions

We welcome contributions to improve the app. If you have suggestions for enhancements or new features, feel free to open an issue or submit a pull request.

## License

This project is licensed under the MIT License.

## Contact

If you have any questions or suggestions, feel free to contact us at gujrathisiddhant@gmail.com.
