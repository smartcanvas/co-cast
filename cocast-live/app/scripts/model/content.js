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

    function Content(raw) {

        if (!(this instanceof Content)) {
            return new Content(raw);
        }

        $.extend(this, raw);

        if (!this.type) {
            this.type = 'quote';
            this.description = 'Type is mandatory';
        }

        if (!this.visibility) {
            this.visibility = false;
        }

        if (!this.speed) {
            this.speed = 10000;
        }

        if (this.type === 'image') {
            this.data = new ns.ContentImage(raw);
        } else if (this.type === 'quote') {
            this.data = new ns.ContentQuote(raw);
        } else {
            this.data = new ns.ContentQuote({
                type: 'quote',
                description: 'Unknown type'
            });
        }
    }

    Content.prototype.getStyles = function () {
        return this.styles;
    };

    ns.Content = Content;

}));