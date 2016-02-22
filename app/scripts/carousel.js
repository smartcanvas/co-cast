(function (root, factory) {

  if (typeof define === 'function' && define.amd) {
    define([], factory);
  } else if (typeof exports === 'object') {
    module.exports = factory();
  } else {
    root.CoCast = root.CoCast || {};
    factory(root.CoCast);
  }

}(this, function (ns) {

  function Carousel(itemsLength, loadNextCallback){
    this.itemsLength = itemsLength - 1;
    this.lastPosition = -1;
    this.atualPosition = 0;
    this.restarting = false;
    this.offset = 0;

    this.loadNext = function(){
      if(loadNextCallback){
        loadNextCallback(this.atualPosition, this.restarting, this.lastPosition);  
      }
      this.lastPosition = this.atualPosition;

      if(this.itemsLength === this.atualPosition || this.atualPosition > this.itemsLength){
        this.restarting = true;
        this.atualPosition = this.offset;
      }else{
        this.restarting = false;
        this.atualPosition++;
      }
    };

    this.reset = function(newItemsLength){
      this.atualPosition = this.itemsLength + this.atualPosition - this.offset + 1;
      this.offset = this.itemsLength + 1;
      this.itemsLength = newItemsLength - 1;
      this.restarting = true;
    };

    this.loadNext();
  }

  ns.Carousel = Carousel;
}));
