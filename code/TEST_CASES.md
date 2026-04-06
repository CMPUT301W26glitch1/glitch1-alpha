# Test Cases – Raiyan's User Stories

---

## US 02.08.01 – Organizer can view and delete entrant comments on own event

### TC-01 Organizer views comments on own event
**Precondition:** Organizer is logged in and at least one comment exists.

**Steps:**
1. Login as organizer
2. Open organizer event list
3. Select own event
4. Click "Comments"

**Expected Result:**
Comments for the selected event are displayed.

---

### TC-02 Organizer deletes a comment on own event
**Precondition:** Organizer is logged in and comments exist.

**Steps:**
1. Open event comments
2. Click "Delete" on a comment
3. Confirm deletion

**Expected Result:**
Comment is removed from UI and Firestore.

---

## US 02.08.02 – Organizer can post comments on own event

### TC-03 Organizer posts a comment
**Precondition:** Organizer is logged in.

**Steps:**
1. Open event comments page
2. Enter comment text
3. Click "Post Comment"

**Expected Result:**
Comment appears in list and is saved in Firestore.

---

### TC-04 Organizer username is shown
**Precondition:** Organizer is logged in.

**Steps:**
1. Post a comment
2. View comment list

**Expected Result:**
Comment shows organizer’s actual username (not "Organizer").

---

### TC-05 Organizer cannot post empty comment
**Precondition:** Organizer is logged in.

**Steps:**
1. Leave input empty
2. Click "Post Comment"

**Expected Result:**
Error message shown, comment not posted.

---

## US 03.10.01 – Admin can remove event comments that violate app policy

### TC-06 Admin views event comments
**Precondition:** Admin is logged in.

**Steps:**
1. Open "Manage Events"
2. Click "Comments" on an event

**Expected Result:**
All comments for that event are displayed.

---

### TC-07 Admin deletes a comment
**Precondition:** Admin is logged in and comments exist.

**Steps:**
1. Open event comments
2. Click "Delete"
3. Confirm

**Expected Result:**
Comment is removed from UI and Firestore.

---

### TC-08 Admin cannot post comments
**Precondition:** Admin is logged in.

**Steps:**
1. Open event comments

**Expected Result:**
Comment input and post button are hidden.

---

## US 03.xx.xx – Admin can remove organizers who violate app policy

### TC-09 Admin views organizer list
**Precondition:** Admin is logged in and organizers exist.

**Steps:**
1. Open "Manage Organizers"

**Expected Result:**
List of organizers is displayed.

---

### TC-10 Admin removes an organizer
**Precondition:** Admin is logged in.

**Steps:**
1. Open "Manage Organizers"
2. Select an organizer
3. Click "Delete" or "Remove"
4. Confirm action

**Expected Result:**
Organizer is removed from system and no longer appears in the list.

---

### TC-11 Removed organizer cannot access organizer features
**Precondition:** Organizer account has been removed.

**Steps:**
1. Attempt to login as removed organizer

**Expected Result:**
Access is denied OR organizer features are no longer available.