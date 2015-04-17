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
        cardList += "<casted-card contentTitle='" + this.response[i].title + "' "
            + "content='" + this.response[i].content + "' "
            + "authorImageURL='" + this.response[i].authorImageURL + "' "
            + "imageURL='" + this.response[i].contentImageURL + "' "
            + "authorName='" + this.response[i].authorDisplayName + "' "
            + "date='" + this.response[i].date + "' "
            + "></casted-card>";
      }
      document.getElementById("casted-card-list").innerHTML = cardList;
    }
  });

  ajax.go(); // Call its API methods.
});