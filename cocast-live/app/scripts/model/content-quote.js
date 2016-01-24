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

    function ContentQuote(raw) {

        if (!(this instanceof ContentQuote)) {
            return new ContentQuote(raw);
        }

        $.extend(this, raw);

        if (!this.description) {
            this.description = 'Description default model';
        }

        if (!this.renderer) {
            this.renderer = 'cc-content-quote';
        }

    }

    ns.ContentQuote = ContentQuote;

}));