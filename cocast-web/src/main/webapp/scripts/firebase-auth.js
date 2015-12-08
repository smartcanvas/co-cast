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
    console.log("authDataCallback called");
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
        rootRef.child("users").child(authData.uid).set({
            provider: authData.provider,
            name: authData.google.displayName,
            email: authData.google.email,
            lastLogin: new Date()
        });
    }
}

/*
 function logout() {
 rootRef.unauth();
 }
 */

