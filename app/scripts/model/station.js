(function (root, factory) {

    if (typeof define === 'function' && define.amd) {
    define([], factory);
    } else if (typeof exports === 'object') {
        module.exports = factory();
    } else {
    root.CoCast = root.CoCast || {};
    root.CoCast.Model = root.CoCast.Model || {};
        factory(root.CoCast.Model);
    }

}(this, function (ns) {

  function Station(raw) {
    var self = this;

    if(!(this instanceof Station)){
      return new Station(raw);
    }
    
    $.extend(this, raw);

    if(!this.type){
      this.type = 'station';
    }

    if(this.contents){
      $.each(this.contents, function(i, raw){
        self.contents[i] = new ns.Content(raw);
      });
    }else{
      this.channels = [];
      this.channels.push( new ns.Content({
        type: 'quote',
        title: 'Co-Cast by Smart Canvas'
      }) );
    }

  }

  Station.prototype.getStyles = function () {
    return this.styles;
  };

  Station.prototype.getContents = function () {
    return this.contents;
  };

  Station.prototype.getCaption = function () {
    return this.caption;
  };

  ns.Station = Station;

}));