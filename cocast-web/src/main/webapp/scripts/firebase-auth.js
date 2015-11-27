/**
 * Firebase authentication Javascript
 */

/* Gets the reference for a Firebase object */
var rootRef = new Firebase("https://co-cast-dev.firebaseio.com/");

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
        rootRef.authWithOAuthRedirect("google", function (error) {
            console.log("Login Failed!", error);
        }, {
            remember: "default",
            scope: "email"
        });
    }
    else {
        console.log("authData is defined!");
        console.log("Authenticated successfully with payload:", authData);
    }
}

/*
 function logout() {
 rootRef.unauth();
 }
 */

