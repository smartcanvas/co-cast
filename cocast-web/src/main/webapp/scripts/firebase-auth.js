/**
 * Firebase authentication Javascript
 */

/* Gets the reference for a Firebase object */
var rootRef = new Firebase("https://co-cast-dev.firebaseio.com/");

/**
 * On document load
 */
$(function () {
    rootRef.onAuth(authDataCallback);
});

/**
 * Handles authentication
 */
function authDataCallback(authData) {
    if (!authData) {
        console.log("authData is undefined");
        rootRef.authWithOAuthRedirect("google", function (error) {
            console.log("Login Failed!", error);
        }, {
            remember: "default",
            scope: "email"
        });
    }
    else {
        console.log("Authenticated successfully with payload:", authData);
        var nowUTC = Math.floor((new Date()).getTime() / 1000);

        rootRef.child("users").child(authData.uid).set({
            uid: authData.uid,
            provider: authData.provider,
            name: authData.google.displayName,
            email: authData.google.email,
            lastLogin: nowUTC
        });
    }
}

/*
 function logout() {
 rootRef.unauth();
 }
 */

