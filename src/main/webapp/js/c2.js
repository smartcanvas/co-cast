//Javascript file for C2

var maxCards = 0;
var intervalId;

/**
 * Wait for 'polymer-ready'. Ensures the element is upgraded.
 */
window.addEventListener('polymer-ready', function(e) {
  var ajax = document.querySelector('core-ajax');

  // Respond to events it fires.
  ajax.addEventListener('core-response', function(e) {
    console.log(this.response);

    if (this.response != null) {
      var cardList = "";
      for (i = 0; i < this.response.length; i++ ) {
        cardList += "<div><casted-card contentTitle='" + this.response[i].title + "' "
            + "content='" + this.response[i].content + "' "
            + "authorImageURL='" + resizeAuthorImage(this.response[i].authorImageURL) + "' "
            + "imageURL='" + this.response[i].contentImageURL + "=s0' "
            + "authorName='" + this.response[i].authorDisplayName + "' "
            + "date='" + formatDate(this.response[i].date) + "' "
            + "></casted-card></div>";
            maxCards++;
      }
      document.getElementById("casted-card-list").innerHTML = cardList;
    }
  });

  ajax.go(); // Call its API methods.
});

/**
 * Method called when each page finishes loading
 */
addEventListener('polymer-ready', function() {
  /** Interval for progress bar */
  intervalId = window.setInterval("updateProgressBar()", 100);
});

/**
 * Handles the card transition
 */
function changeCard() {
  var corePages = document.querySelector('core-pages.fancy');
  if (corePages.selected + 1 >= maxCards) {
    window.clearInterval( intervalId );
    document.forms[0].submit();
  } else {
    corePages.selected++;
    progressBar.value = 0;
  }
};

/**
 * Updates the progres bar
 */
function updateProgressBar() {
  var progressBar = document.querySelector('paper-progress');
  progressBar.value += 1;
  if (progressBar.value >= 100) {
    changeCard();
  }
}

/**
 * Resizes the author imageURL
 */
function resizeAuthorImage(authorImageURL) {
  indexOfQuestionMark = authorImageURL.indexOf('?');
  originalURL = authorImageURL.substring(0,indexOfQuestionMark);
  originalURL += "?sz=75";
  return originalURL;
}

/**
 * Format the date in a string 
 */
function formatDate(dateInUTC) {

  var monthNames = [
      "January", "February", "March",
      "April", "May", "June", "July",
      "August", "September", "October",
      "November", "December"
  ];

  var date = new Date( dateInUTC );
  var day = date.getDate();
  var monthIndex = date.getMonth();
  var year = date.getFullYear();

  return monthNames[monthIndex] + " " + day + ", " + year;

}