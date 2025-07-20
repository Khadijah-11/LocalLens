# Google Maps Setup for LocalLens

## Prerequisites
1. A Google Cloud Platform account
2. Google Maps API enabled

## Setup Steps

### 1. Get Google Maps API Key
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the following APIs:
   - Maps SDK for Android
   - Places API (optional, for future features)
4. Go to Credentials and create an API key
5. Restrict the API key to Android apps with your app's package name and SHA-1 fingerprint

### 2. Add API Key to App
1. Open `app/src/main/AndroidManifest.xml`
2. Replace `YOUR_API_KEY_HERE` with your actual API key:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_ACTUAL_API_KEY" />
   ```

### 3. Get SHA-1 Fingerprint
Run this command in your project directory:
```bash
./gradlew signingReport
```
Look for the SHA-1 value in the debug variant.

### 4. Test the App
1. Build and run the app
2. Grant location permissions when prompted
3. The map should load and you can tap to select locations

## Features Added
- Interactive Google Map in the issue form
- Tap to select location (latitude/longitude)
- Current location detection (with permission)
- Location coordinates saved to database
- Visual marker showing selected location
- Location info display

## Permissions Required
- `ACCESS_FINE_LOCATION` - For precise location
- `ACCESS_COARSE_LOCATION` - For approximate location
- `INTERNET` - For map tiles and API calls 