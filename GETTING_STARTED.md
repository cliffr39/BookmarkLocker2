Getting Started with Bookmark Locker

This guide provides step-by-step instructions on how to set up, build, and run the Bookmark Locker Android application on your local machine.
🛠️ Prerequisites

Before you begin, ensure you have the following installed:

    Android Studio: The latest stable version is recommended. Download from developer.android.com/studio.

    A Google account: Required for Firebase project setup.

    Node.js and npm: (Optional) For Firebase CLI, if you plan to use it for advanced Firebase project management (e.g., deploying Cloud Functions, managing Firestore indexes from CLI).

🚀 Installation and Setup

Follow these steps to get Bookmark Locker up and running:

    Clone the Repository:
    Open your terminal or command prompt and clone the project:

    git clone https://github.com/cliffr39/BookmarkLocker2.git
    cd BookmarkLocker2

    (Remember to replace your-username/bookmark-locker.git with your actual repository URL)

    Open in Android Studio:

        Launch Android Studio.

        Select File > Open and navigate to the cloned bookmark-locker directory. Android Studio will automatically import the project and sync Gradle files.

    Set up Firebase Project:
    Bookmark Locker uses Firebase for user authentication and cloud synchronization.

        Go to the Firebase Console.

        Create a new Firebase project. Choose a descriptive name (e.g., BookmarkLockerAppSync).

        Add an Android app to your new Firebase project:

            Provide your app's applicationId. You can find this in your app/build.gradle file under the android { defaultConfig { applicationId "your.package.name" } } block.

            Crucially, provide your SHA-1 debug signing certificate fingerprint. This links your development builds to your Firebase project. To get it, open your project's root directory in the terminal and run:

            ./gradlew signingReport

            Copy the SHA1: value from the debug variant in the output.

        Download the google-services.json file provided by Firebase after registering your app.

        Place this google-services.json file directly into your app/ directory within your Android Studio project (e.g., bookmark-locker/app/). Important: Do NOT commit this file to your Git repository! It's already listed in the .gitignore for security.

    Enable Firestore Database:

        In the Firebase Console, navigate to the Build section and click on Firestore Database.

        Click the "Create database" button.

        For development purposes, choose "Start in test mode". This allows you to read and write data immediately. Remember to secure your rules before deploying your app to production!

    Configure Environment Variables (Mac/Linux):
    For convenient development, it's recommended to set your Java Development Kit (JDK) home and Android Debug Bridge (ADB) paths permanently.

        Open your shell configuration file (e.g., ~/.zshrc for Zsh, or ~/.bash_profile for Bash) in a text editor.

        Add the following lines to the end of the file:

        export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
        export PATH="$HOME/platform-tools:$PATH"

        Save the file and apply the changes by running source ~/.zshrc (or source ~/.bash_profile) in your terminal, or by simply closing and reopening your terminal.

    Sync Project with Gradle Files:
    After making any changes to build.gradle or adding google-services.json, ensure Android Studio syncs your project. You can usually click the "Sync Now" button if prompted, or go to File > Sync Project with Gradle Files in the top menu.

    Run the App:

        Connect an Android device to your computer with USB debugging enabled, or start an Android Emulator.

        In Android Studio, click the "Run" button (the green triangle icon) in the toolbar. Android Studio will build and install the app on your selected device/emulator.

You should now have Bookmark Locker running, ready for development and testing!