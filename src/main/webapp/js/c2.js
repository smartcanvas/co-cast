/**
 * Handles the card transition
 */
document.querySelector('core-pages.fancy').onclick = function(e) {
  this.selected = (this.selected + 1) % this.items.length;
  this.async(function() {
    if (this.selectedIndex == 0) {
      this.selectedItem.classList.remove('begin');
    } else if (this.selectedIndex == this.items.length - 1) {
      this.items[0].classList.add('begin');
    }
  });
};

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
            + "imageURL='" + this.response[i].contentImageURL + "' "
            + "authorName='" + this.response[i].authorDisplayName + "' "
            + "date='" + formatDate(this.response[i].date) + "' "
            + "></casted-card></div>";
      }
      document.getElementById("casted-card-list").innerHTML = cardList;
    }
  });

  ajax.go(); // Call its API methods.
});

/**
 * Resizes the author imageURL
 */
function resizeAuthorImage(authorImageURL) {
  indexOfQuestionMark = authorImageURL.indexOf('?');
  originalURL = authorImageURL.substring(0,indexOfQuestionMark);
  originalURL += "?sz=150";
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