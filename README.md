# Event Lottery System App

## Table of Contents
- [About The Project](#about-the-project)
- [Documentation](#documentation)
- [Features](#features)
- [Built With](#built-with)
- [Version Information](#version-information)
- [Built By](#built-by)

---

## About The Project

Event Lottery System App is a mobile application that allows organizers to create and manage events, entrants to browse events, join waiting lists, receive notifications, and enroll in events, and administrators to manage events, users, and system content.

Instead of a first-come-first-served approach, the app uses a **lottery system** — entrants join a waiting list during a registration window, and when it closes, the system randomly selects participants. This gives everyone a fair chance regardless of availability, making events more accessible to people with work schedules, disabilities, or other limitations.

This project was developed as part of the University of Alberta CMPUT 301 course by **Glitch1**.

[Back to top](#event-lottery-system-app)

---

## Documentation

Check out our [Wiki](../../wiki) for project documentation, UML diagrams, user stories, and development notes.

[Back to top](#event-lottery-system-app)

---

## Features

### Entrant Features
- Browse and search public events by keyword
- Filter events by availability and capacity
- Join and leave event waiting lists
- Scan QR codes to view event details
- View personal event history
- Accept or decline lottery invitations
- Accept or decline private event waiting list invitations
- Receive notifications for lottery wins, losses, and organizer messages
- Opt out of notifications
- Post and view comments on events
- Manage profile (name, email, optional phone number)
- Device-based identification — no username or password required

### Organizer Features
- Create public events with auto-generated QR codes
- Create private events (hidden from listings, no QR code)
- Invite specific entrants to private event waiting lists by name, email, or phone
- Set registration open and close periods
- Optionally limit waiting list size
- Enable or disable geolocation requirement for joining
- Upload and update event posters
- View waiting list, selected, cancelled, and enrolled participants
- Run the lottery to randomly sample a specified number of attendees
- Draw replacement participants when selected entrants decline
- Cancel entrants who did not sign up
- Send custom notifications to waiting, selected, or cancelled groups
- Export enrolled participant list as CSV
- View and delete entrant comments on events
- Post comments on own events
- Assign co-organizers for events

### Admin Features
- Browse and remove events
- Browse and remove profiles
- Browse and remove uploaded images
- Remove organizers that violate app policy
- View notification logs for all organizer-sent notifications
- Remove comments that violate app policy
- Act as an organizer or entrant using the admin profile

### Accessibility / WOW Factor
- Accessibility mode with larger text and buttons across all screens
- Setting is per-user and persists across sessions

[Back to top](#event-lottery-system-app)

---

## Built With

- Java
- Android Studio
- Firebase Firestore
- Firebase Storage
- Gradle
- Android SDK
- ZXing (QR code generation)
- Glide (image loading)
- Google Maps SDK

[Back to top](#event-lottery-system-app)

---

## Version Information

- **Java Version:** Java 11
- **Android Gradle Plugin (AGP) Version:** 9.0.1
- **Minimum SDK:** 26
- **Target SDK:** 36
- **Compile SDK:** 36
- **Google Services Plugin:** 4.4.4

[Back to top](#event-lottery-system-app)

---

## Built By

| CCID | GitHub Username |
|------|-----------------|
| aarib | [aarib44](https://github.com/aarib44) |
| emontoya | [emontoya113](https://github.com/emontoya113) |
| makhan12 | [itsAYYY](https://github.com/itsAYYY) |
| rislam2 | [rislam2-stack](https://github.com/rislam2-stack) |
| vadish | [Vadish19](https://github.com/Vadish19) |
| jchellak | [jchellak](https://github.com/jchellak) |

[Back to top](#event-lottery-system-app)
