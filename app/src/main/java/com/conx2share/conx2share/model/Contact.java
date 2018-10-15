package com.conx2share.conx2share.model;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;


public class Contact implements Comparable<Contact> {

    private String name;

    private String photoUri;

    private String thumbnailUri;

    private String email;

    private boolean isChosenForInvite;

    public Contact(@NonNull Cursor contactsCursor) {
        name = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        photoUri = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
        thumbnailUri = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
        email = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compareTo(Contact other) {

        // Intermingle real names and emails (often emails look like names)
        // Put pure phone numbers at end, i.e. anything that doesn't start with a letter
        String myName = "";
        if (this.name != null && !this.name.isEmpty() && Character.isLetter(this.name.charAt(0))) {
            myName = this.name;
        }
        String otherName = "";
        if (other.name != null && !other.name.isEmpty() && Character.isLetter(other.name.charAt(0))) {
            otherName = other.name;
        }

        // Empty at end
        if (!myName.isEmpty() && otherName.isEmpty()) {
            return -1;
        } else if (myName.isEmpty() && !otherName.isEmpty()) {
            return 1;
        } else if (!myName.isEmpty() && !otherName.isEmpty()) {
            return myName.compareToIgnoreCase(otherName);
        } else {
            // Both empty or start with non-letters, do normal null-safe compare
            if (this.name != null && other.name == null) {
                return -1;
            } else if (this.name == null && other.name != null) {
                return 1;
            } else if (this.name == null && other.name == null) {
                return 0;
            } else {
                return this.name.compareToIgnoreCase(other.name);
            }
        }
    }

    public boolean isChosenForInvite() {

        return isChosenForInvite;
    }

    public void setChosenForInvite(boolean isChosenForInvite) {

        this.isChosenForInvite = isChosenForInvite;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public void setThumbnailUri(String thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }
}
