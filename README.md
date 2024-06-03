# DMuber

DMuber is a carpooling application that connects drivers and passengers for shared rides. The app allows passengers to request carpool rides and drivers to accept these requests, providing an efficient and eco-friendly way to travel.

Developed by Salahudiin Sheikhmuse

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Types of Users](#types-of-users)
- [Installation](#installation)
- [Usage](#usage)
- [Screenshots](#screenshots)
- [Technologies Used](#technologies-used)
- [Contact](#contact)

## Introduction

DMuber is designed to facilitate convenient and eco-friendly carpooling. Whether you are a passenger looking for a ride or a driver wanting to share your journey, DMuber connects you with the right people. Additionally, administrators have the tools to manage and oversee the platform's operations.

## Features

- **User Authentication**: Secure login and registration for customers, drivers, and admins.
- **Real-time Location Tracking**: Track driver and passenger locations in real-time using GPS.
- **Carpool Requests**: Different types of carpool requests that passengers can request and drivers can accept or decline.
- **Notifications**: In-app notifications for ride requests, acceptances, and arrivals.
- **Proximity Alerts**: Alerts when the driver is within a certain distance of the passenger.

## Types of Users

- **Customer**: Can make live carpool request on map and be notified upon arrival, can browse posted bookings and select one to make a ride.
- **Driver**: Can view and accept live carpool requests from customers and post bookings via calender.
- **Admin**: Can manage users, view ride statistics and reviews, and oversee platform operations.

## Installation

### Prerequisites

- Android Studio
- Firebase account

### Steps

1. **Clone the repository:**
    ```bash
    git clone https://github.com/yourusername/DMuber.git
    ```

2. **Open the project in Android Studio:**
    - Open Android Studio.
    - Click on `File` > `Open...`.
    - Select the cloned repository folder.

3. **Configure Firebase:**
    - Go to [Firebase Console](https://console.firebase.google.com/).
    - Create a new project or use an existing project.
    - Add an Android app to your Firebase project.
    - Download the `google-services.json` file and place it in the `app` directory of your project.
    - Enable Authentication, Firestore Database, and Realtime Database from the Firebase console.

4. **Build and run the app:**
    - Click on the `Run` button in Android Studio or use the `Shift + F10` shortcut.

### Customer

1. **Register/Login**: Sign up or log in to your account.
2. **Live Carpool Request**: Click on Live carpool request button, on the map select a destination to travel to by long clicking and then click on that placed marker to make carpool request
3. **Track Driver**: Once a driver accepts your request, track their location in real-time.
4. **Receive Notification**: Get notified automatically when the driver is within 5 meters of customer location via proximity monitoring services.

### Driver

1. **Register/Login**: Sign up or log in to your account.
2. **View Live carpool Requests** Click on Live carpool request page and view all live carpool requests made by customers, select destination marker to accept or decline
3. **Accept/Decline Live carpool Requests**: View and accept or decline carpool requests from passengers.
4. **Navigate**: Use the in-app map to navigate to the passenger’s location.
5. **Notify Arrival**: Automatically notifies customer of driver arrival when in 5 meters of customer location via proximity monitoring features.

### Admin

1. **Login**: Admins log in using their credentials.
2. **Manage Users**: View, edit, or remove customers and drivers.
3. **View Statistics**: Access detailed ride statistics and reviews.

## Screenshots

<img src="https://github.com/lacagmanS/finaldelivarableDMUber/assets/123553797/6783b75e-e67d-4fe9-aec2-4dd422a63a63" alt="Customer Menu" width="300">
<img src="https://github.com/lacagmanS/finaldelivarableDMUber/assets/123553797/6f9eab54-503f-4479-8a91-6e8d74a0980a" alt="Screenshot_20240502_102542" width="300">
<img src="https://github.com/lacagmanS/finaldelivarableDMUber/assets/123553797/ef64a77f-83f1-42b1-b58b-71ad008fd489" alt="Carpool Request Alert Customer Side" width="300">
<img src="https://github.com/lacagmanS/finaldelivarableDMUber/assets/123553797/4f55f034-e17b-497b-90dc-95f49aa95540" alt="Carpool Request Customer Side Only Map" width="300">

## Technologies Used

- **Android Studio**: Integrated development environment for Android development.
- **Firebase**: Backend services for authentication, database, and real-time data synchronization.
- **Google Maps API**: Integration for maps and location services.
- **Kotlin**: Programming language used for Android development.

## Contact

If you have any questions or suggestions, feel free to contact me at salahudinsheikhmuse@gmail.com.


