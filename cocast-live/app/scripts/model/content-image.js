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

    function ContentImage(raw) {

        if (!(this instanceof ContentImage)) {
            return new ContentImage(raw);
        }

        $.extend(this, raw);

        if (!this.src) {
            this.src = 'https://cdn2.iconfinder.com/data/icons/windows-8-metro-style/512/error.png';
        }

        if (!this.title) {
            this.title = 'Title default model';
        }

        if (!this.renderer) {
            this.renderer = 'cc-content-image';
        }
    }

    ns.ContentImage = ContentImage;

}));