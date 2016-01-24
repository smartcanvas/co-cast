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

        function getStation() {
            var DATA;
            if (teste) {
                teste = false;
                DATA = {
                    styles: {
                        mnemonic: 'blue_pink2',
                        primaryColor: '#000',
                        primaryFontColor: '#FFFFFF',
                        secondaryColor: '#FFF',
                        secondaryFontColor: '#000000',
                        accentColor: 'red',
                        accentFontColor: 'blue',
                        primaryFont: 'Roboto',
                        secondaryFont: 'Roboto'
                    },
                    caption: [
                        'OUTRO TEXTO',
                        'MAIS UM TEXTO',
                        'BLA BLA BLA'
                    ],
                    channels: [
                        {
                            styles: {
                                mnemonic: 'blue_pink2',
                                primaryColor: '#000',
                                primaryFontColor: '#FFFFFF',
                                secondaryColor: '#FFF',
                                secondaryFontColor: '#000000',
                                accentColor: 'red',
                                accentFontColor: 'blue',
                                primaryFont: 'Roboto',
                                secondaryFont: 'Roboto'
                            },
                            title: 'outro canal',
                            contents: [
                                {
                                    type: 'image',
                                    styles: {
                                        mnemonic: 'blue_pink2',
                                        primaryColor: '#000',
                                        primaryFontColor: '#FFFFFF',
                                        secondaryColor: '#FFF',
                                        secondaryFontColor: '#000000',
                                        accentColor: 'red',
                                        accentFontColor: 'blue',
                                        primaryFont: 'Roboto',
                                        secondaryFont: 'Roboto'
                                    },
                                    title: 'OUTRO TEXTO DE CANAL',
                                    src: 'http://cdn1.mundodastribos.com/2015/11/6-motivos-para-jogar-na-Mega-da-Virada-2016-04.jpg'
                                }
                            ]
                        },
                        {
                            styles: {
                                mnemonic: 'blue_pink2',
                                primaryColor: '#000',
                                primaryFontColor: '#FFFFFF',
                                secondaryColor: '#FFF',
                                secondaryFontColor: '#000000',
                                accentColor: 'red',
                                accentFontColor: 'blue',
                                primaryFont: 'Roboto',
                                secondaryFont: 'Roboto'
                            },
                            title: 'AAAHSJAHSJA',
                            contents: [
                                {
                                    type: 'image',
                                    styles: {
                                        mnemonic: 'blue_pink2',
                                        primaryColor: '#000',
                                        primaryFontColor: '#FFFFFF',
                                        secondaryColor: '#FFF',
                                        secondaryFontColor: '#000000',
                                        accentColor: 'red',
                                        accentFontColor: 'blue',
                                        primaryFont: 'Roboto',
                                        secondaryFont: 'Roboto'
                                    },
                                    title: 'skjadlkas dlsaj dlksajdlskajd lksadjlsakdj',
                                    src: 'http://www.pravdanews.jex.com.br/includes/imagem.php?id_jornal=20207&id_noticia=5463'
                                }
                            ]
                        }
                    ]
                };
            } else {
                teste = true;
                DATA = {
                    styles: {
                        mnemonic: 'blue_pink2',
                        primaryColor: '#2196f3',
                        primaryFontColor: '#FFFFFF',
                        secondaryColor: '#64b5f6',
                        secondaryFontColor: '#000000',
                        accentColor: '#FF4081',
                        accentFontColor: '#FFFFFF',
                        primaryFont: 'Roboto',
                        secondaryFont: 'Roboto'
                    },
                    caption: [
                        'Cólera mata 10 no maior campo de refugiados do mundo, no Quênia',
                        'Suspeito deixa celular cair durante fuga e fotos ajudam PM a localizá-lo',
                        'Você viu? Bolsas asiáticas, inflação de 2015 e outras notícias da semana'
                    ],
                    channels: [
                        {
                            styles: {
                                mnemonic: 'blue_pink2',
                                primaryColor: '#2196f3',
                                primaryFontColor: '#FFFFFF',
                                secondaryColor: '#64b5f6',
                                secondaryFontColor: '#000000',
                                accentColor: '#FF4081',
                                accentFontColor: '#FFFFFF',
                                primaryFont: 'Roboto',
                                secondaryFont: 'Roboto'
                            },
                            title: 'What\'s new',
                            contents: [
                                {
                                    type: 'image',
                                    styles: {
                                        mnemonic: 'blue_pink2',
                                        primaryColor: '#2196f3',
                                        primaryFontColor: '#FFFFFF',
                                        secondaryColor: '#64b5f6',
                                        secondaryFontColor: '#000000',
                                        accentColor: '#FF4081',
                                        accentFontColor: '#FFFFFF',
                                        primaryFont: 'Roboto',
                                        secondaryFont: 'Roboto'
                                    },
                                    title: 'Mega da Virada pode render multa de R$ 8,5 milhões por publicidade ilegível',
                                    src: 'http://cdn1.mundodastribos.com/2015/11/6-motivos-para-jogar-na-Mega-da-Virada-2016-04.jpg'
                                },
                                {
                                    type: 'quote',
                                    styles: {
                                        mnemonic: 'blue_pink2',
                                        primaryColor: '#2196f3',
                                        primaryFontColor: '#FFFFFF',
                                        secondaryColor: '#64b5f6',
                                        secondaryFontColor: '#000000',
                                        accentColor: '#FF4081',
                                        accentFontColor: '#FFFFFF',
                                        primaryFont: 'Roboto',
                                        secondaryFont: 'Roboto'
                                    },
                                    description: 'Eu pensei que fosse algum problema no site quando vi a nota maior que 1.000.'
                                }
                            ]
                        },
                        {
                            styles: {
                                mnemonic: 'blue_pink2',
                                primaryColor: '#2196f3',
                                primaryFontColor: '#FFFFFF',
                                secondaryColor: '#64b5f6',
                                secondaryFontColor: '#000000',
                                accentColor: '#FF4081',
                                accentFontColor: '#FFFFFF',
                                primaryFont: 'Roboto',
                                secondaryFont: 'Roboto'
                            },
                            title: 'What\'s hot',
                            contents: [
                                {
                                    type: 'image',
                                    styles: {
                                        mnemonic: 'blue_pink2',
                                        primaryColor: '#2196f3',
                                        primaryFontColor: '#FFFFFF',
                                        secondaryColor: '#64b5f6',
                                        secondaryFontColor: '#000000',
                                        accentColor: '#FF4081',
                                        accentFontColor: '#FFFFFF',
                                        primaryFont: 'Roboto',
                                        secondaryFont: 'Roboto'
                                    },
                                    title: 'Obama apresenta medidas para reduzir violência com armas de fogo',
                                    src: 'http://www.pravdanews.jex.com.br/includes/imagem.php?id_jornal=20207&id_noticia=5463'
                                },
                                {
                                    type: 'quote',
                                    styles: {
                                        mnemonic: 'blue_pink2',
                                        primaryColor: '#2196f3',
                                        primaryFontColor: '#FFFFFF',
                                        secondaryColor: '#64b5f6',
                                        secondaryFontColor: '#000000',
                                        accentColor: '#FF4081',
                                        accentFontColor: '#FFFFFF',
                                        primaryFont: 'Roboto',
                                        secondaryFont: 'Roboto'
                                    },
                                    description: 'Se for verificado que houve a propaganda enganosa, a Caixa certamente será multada.'
                                }
                            ]
                        }
                    ]
                };
            }

            var defer = $.Deferred();

            setTimeout(function () {
                defer.resolve(DATA);
            }, Math.floor(400 + Math.random() * 2000));

            // var promise = $.ajax({
            //   url: '/api/station/v1/station/',
            //   dataType: 'json',
            //   contentType: 'application/json',
            //   method: 'GET'
            // });

            return defer.promise().then(function (response) {
                if (response) {
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
