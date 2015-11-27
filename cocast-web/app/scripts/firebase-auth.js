/**
 * Firebase authentication Javascript
 */

/* Gets the reference for a Firebase object */
var rootRef = new Firebase("https://sc-core-dev.firebaseio.com/");

/**
 * On document load
 */
$(function () {
    console.log("Initializing application");
    rootRef.onAuth(authDataCallback);
    console.log("Application ready!");
});

/**
 * Handles authentication
 */
function authDataCallback(authData) {
    console.log("authDataCallback called");
    if (!authData) {
        console.log("authData is undefined");
        rootRef.authWithOAuthPopup("google", function (error) {
            console.log("Login Failed!", error);
        }, {
            remember: "default",
            scope: "email, https://www.googleapis.com/auth/drive"
        });
    }
    else {
        console.log("authData is defined!");
        console.log("Authenticated successfully with payload:", authData);
        if (authData) {
            provider = authData.provider;
            googleId = authData.google.id;
            googleAccessToken = authData.google.accessToken;
            googleDisplayName = authData.google.displayName;
            googleEmail = authData.google.email;
            googleImageURL = authData.google.profileImageURL;
            googleCachedUserProfile = authData.google.cachedUserProfile;


            var $credentials = $("#credentials");
            var credentialsMessage = "Provider = " + provider
                + ", googleId = " + googleId + ", googleAccessToken = " + googleAccessToken
                + ", googleDisplayName = " + googleDisplayName + ", googleEmail = " + googleEmail
                + ", googleImageURL = " + googleImageURL + ", googleCachedUserProfile = " + googleCachedUserProfile;
            $credentials.text(credentialsMessage);
        }
    }
}

/*
 function logout() {
 rootRef.unauth();
 }
 */

