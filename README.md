# PaleoDelightsRider
This is a demo app for delivery riders, meant to be used together with [Paleo Delights](https://github.com/pawaka2020/PaleoDelights).

## Stacks / templates used:

1. [Material Dialogues](https://github.com/afollestad/material-dialogs) for creating a dialogue box in the app with minimal code.

2. [Firebase Firestore](https://firebase.google.com/docs/firestore) for logging in of registered riders as well as querying and updating delivery orders.

3. Navigation Drawer template as provided by Android Studio. 

## Walkthrough 

![image](https://user-images.githubusercontent.com/40174427/83357260-f000d200-a39d-11ea-84ec-49e1d4d8c1d8.png)

The user is first required to enter a username and password to log in. 

![image](https://user-images.githubusercontent.com/40174427/83357313-4241f300-a39e-11ea-8396-bbaa6f309bd8.png)

Then upon logging in, the user is presented with the current delivery screen as the starting screen. The navigation drawer displays the current and pending delivery order screens. 

![image](https://user-images.githubusercontent.com/40174427/83357270-0c047380-a39e-11ea-9d82-aad1754b5a13.png)

The current delivery screen displays the delivery location for the orders that the rider had marked for pickup as well as the rider's current location. Normally the [Directions API](https://developers.google.com/maps/documentation/directions/start#Waypoints) would be used for this purpose, but due to the need to only use free solutions for this demo app, an alternative solution has been made to provide a close approximation to the desired function.

The use can then either tap to mark the order as 'Delivered' or 'Canceled' and an update is sent to the orders databaes in Firebase Firestore, and is then queried and displayed on the Paleo Delights app afterward.

![image](https://user-images.githubusercontent.com/40174427/83357907-04df6480-a3a2-11ea-8b75-669c1c07b88c.png)

Buttons have also been added onto the map to aid in browsing between multiple current deliveries. The order CardView can also be tapped to expand and display further details, as well as a small button to call the customer for inquiries.

![image](https://user-images.githubusercontent.com/40174427/83357297-28081500-a39e-11ea-9f2e-daf74c996d35.png)

If the current delivery screen is empty, the user can switch to the pending delivery screen to view any deliveries present within the database. The user can then pickup the delivery which will then update the database and the current delivery screen accordingly.
