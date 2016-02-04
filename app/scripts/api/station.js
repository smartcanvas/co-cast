(function (root, factory) {

  if (typeof define === 'function' && define.amd) {
    define([], factory);
  } else if (typeof exports === 'object') {
    module.exports = factory();
  } else {
    root.CoCast = root.CoCast || {};
    root.CoCast.Api = root.CoCast.Api || {};
    factory(root.CoCast.Api, root.jQuery, root.CoCast.Model);
  }

}(this, function (ns, $, modelPackage) {
  var teste = false;
  
  function Station() {
    
    function getStation(data) {

      var defer = $.Deferred();
       
      setTimeout(function() {
        defer.resolve( data );
      }, Math.floor( 400 + Math.random() * 2000 ) );

      return defer.promise().then(function(response){
        if(response){
          return new modelPackage.Station(response);
        }
      });
    }

    var self = {
      getStation: getStation
    };

    return self;
  }

  ns.Station = Station;
}));
