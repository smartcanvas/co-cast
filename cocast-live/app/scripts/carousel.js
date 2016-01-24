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

    function Carousel(itemsLength, loadNextCallback) {
        this.itemsLength = itemsLength - 1;
        this.atualPosition = 0;
        this.restarting = false;

        this.loadNext = function () {
            if (loadNextCallback) {
                loadNextCallback(this.atualPosition, this.restarting);
            }

            if (this.itemsLength === this.atualPosition) {
                this.restarting = true;
                this.atualPosition = 0;
            } else {
                this.restarting = false;
                this.atualPosition++;
            }
        };

        this.loadNext();
    }

    ns.Carousel = Carousel;
}));
