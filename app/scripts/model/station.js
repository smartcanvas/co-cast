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

    if(this.contents && this.contents.length > 0){
      $.each(this.contents, function(i, raw){
        self.contents[i] = new ns.Content(raw);
      });
    }else{
      this.contents = [];
      this.contents.push( new ns.Content({
        type: 'quote',
        title: 'The channels configured for this station returned no content',
        category: 'Co-Cast',
        summary: 'Review your channels configuration',
        authorImageURL: '../../../images/touch/icon-128x128.png',
        authorDisplayName: 'Co-Cast',
        date: (new Date()).getTime(),
        speed: 10000,
        theme: {
          primaryFont: 'Roboto-Regular',
          primaryColor: '#fff',
          primaryFontColor: 'rgba(0,0,0,0.87)',
          accentColor: '#4285f4',
          accentFontColor: '#fff'
        }
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