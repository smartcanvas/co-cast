/*
Copyright (c) 2015 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/

(function() {
    var loading, station;

    $(document).on('firebase-changed', function(){      

      if (CoCastInitializer.accessToken) {
        getLiveStream(false);
      }
      
    });

    /**
     * Utility methods for this client
     */
    window.Util = {

        /**
         * Gets a query param by its name
         */
        getParameterByName: function (name) {
            name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
            var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
                results = regex.exec(location.search);
            return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
        }
    };

    /**
     * Configures Co-Cast and handles authentication
     */
    var Config = {

        init: function (networkMnemonic, stationMnemonic, clientId, liveToken, env) {

            //defines the Co-Cast base URL for API calls
            var coCastBaseURL;
            if (env === 'local') {
                coCastBaseURL = "http://localhost:8080/api";
            } else if (env === 'dev') {
                coCastBaseURL = "https://api-dev.cocast.io";
                //coCastBaseURL = "http://104.197.187.190:8080/api";
            } else { //PRD
                coCastBaseURL = "https://api.cocast.io";
            }

            Config.settings = {
                networkMnemonic: networkMnemonic,
                stationMnemonic: stationMnemonic,
                clientId: clientId,
                liveToken: liveToken,
                env: env,
                baseURL: coCastBaseURL
            };
        }
    };
    
    /**
     * Calls Co-Cast APIs
     */
    var APICaller = {

        get: function (uri, onSuccess, onError) {
            var requestHeaders = {
                'x-access-token': CoCastInitializer.accessToken,
                'x-client-id': Config.settings.clientId
            }

            //* mock
            // http://www.httputility.net/json-minifier.aspx
            // http://jsonlint.com/
            // var mock = {"networkMnemonic":"teamwork","stationMnemonic":"everything","contents":[{"id":"scv1_our-page-on-gwc","networkMnemonic":"teamwork","createdBy":"root","lastUpdated":1453416759546,"date":1450017459000,"title":"https://connect.googleforwork.com/community/partners/teamwork-2016","summary":"https://connect.googleforwork.com/community/partners/teamwork-2016","tags":["information","onsite"],"type":"announcement","theme":{"mnemonic":"blue_pink","primaryColor":"#2196f3","secondaryColor":"#64b5f6","accentColor":"#FF4081","primaryFont":"Roboto","secondaryFont":"Roboto","primaryFontColor":"#FFFFFF","secondaryFontColor":"#000000","accentFontColor":"#FFFFFF","createdBy":"root","lastUpdate":1451320381691},"category":"Information","authorId":"5657947837300736","authorDisplayName":"TeamWork Staff","authorImageURL":"https://lh3.googleusercontent.com/-WNI0u32wL1M/AAAAAAAAAAI/AAAAAAAAAC0/rhenGVMYIo0/photo.jpg?sz=200","imageURL":"https://lh3.googleusercontent.com/BeUa5CPk5pWuNs6LSZyrESLliF8H7wDvk-ASxkAoKa6fboN2pZXyQ9gX2cbr3TST9yKOKnFX6EmcsNEfrQ","imageWidth":1584,"imageHeight":888,"source":"Smart Canvas v1","sourceContentURL":"http://teamwork.smartcanvas.com/card/our-page-on-gwc","likeCounter":0,"active":true},{"id":"scv1_marketing-fun","networkMnemonic":"teamwork","createdBy":"root","lastUpdated":1454168607256,"date":1454086700154,"title":"Marketing fun!","tags":["photos"],"type":"photo","theme":{"mnemonic":"blue_pink","primaryColor":"#2196f3","secondaryColor":"#64b5f6","accentColor":"#FF4081","primaryFont":"Roboto","secondaryFont":"Roboto","primaryFontColor":"#FFFFFF","secondaryFontColor":"#000000","accentFontColor":"#FFFFFF","createdBy":"root","lastUpdate":1451320381691},"category":"Great Photos!","authorId":"6400954177945600","authorDisplayName":"Alan Wrafter","authorImageURL":"https://lh5.googleusercontent.com/-7WTWcW0Rz2U/AAAAAAAAAAI/AAAAAAAALsE/zPz5y0wkDHI/photo.jpg?sz=200","imageURL":"https://lh3.googleusercontent.com/ty1VzZdhDlnzFG5rg2TLbp2GU_QjYjOQ7GUuNGpw2v8yc34pyptv0XYsU3v2Oasc8akheDDwYKD2XQlf","imageWidth":1632,"imageHeight":1224,"source":"Smart Canvas v1","sourceContentURL":"http://teamwork.smartcanvas.com/card/marketing-fun","likeCounter":0,"active":true},{"id":"scv1_merry-xmas","networkMnemonic":"teamwork","createdBy":"root","lastUpdated":1453416715752,"date":1452789441140,"title":"Merry Xmas","tags":["photos"],"type":"quote","theme":{"mnemonic":"blue_pink","primaryColor":"#2196f3","secondaryColor":"#64b5f6","accentColor":"#FF4081","primaryFont":"Roboto","secondaryFont":"Roboto","primaryFontColor":"#FFFFFF","secondaryFontColor":"#000000","accentFontColor":"#FFFFFF","createdBy":"root","lastUpdate":1451320381691},"category":"Great Photos!","authorId":"5700305828184064","authorDisplayName":"Mars Cyrillo","authorImageURL":"https://lh4.googleusercontent.com/-e2ihE1RW1yg/AAAAAAAAAAI/AAAAAAABLyo/t5ONlrV1UxM/photo.jpg?sz=200","imageURL":"https://lh3.googleusercontent.com/x18-cy4MTmzpz4VE72PlCofTA5i5t2KVX456qJaVKpnORlbkr_scXT7icWxTh_INZqWfzNK0gESs1fPFaA","imageWidth":348,"imageHeight":512,"source":"Smart Canvas v1","sourceContentURL":"http://teamwork.smartcanvas.com/card/merry-xmas","likeCounter":0,"active":true},{"id":"scv1_transportation","networkMnemonic":"teamwork","createdBy":"root","lastUpdated":1453416760431,"date":1449958018000,"title":"Placeholder","summary":"Placeholder","tags":["transportation","information"],"type":"quote","theme":{"mnemonic":"blue_pink","primaryColor":"#2196f3","secondaryColor":"#64b5f6","accentColor":"#FF4081","primaryFont":"Roboto","secondaryFont":"Roboto","primaryFontColor":"#FFFFFF","secondaryFontColor":"#000000","accentFontColor":"#FFFFFF","createdBy":"root","lastUpdate":1451320381691},"category":"Information","authorId":"5657947837300736","authorDisplayName":"TeamWork Staff","authorImageURL":"https://lh3.googleusercontent.com/-WNI0u32wL1M/AAAAAAAAAAI/AAAAAAAAAC0/rhenGVMYIo0/photo.jpg?sz=200","imageURL":"https://lh3.googleusercontent.com/kvVYRITpkxS_Ia--nVZEwARANtPZQ-fncg1jV5whIIVkk1EhzNjJEeT0OweXDubqE3zLUgB3s9kF9TOhNQ","imageWidth":480,"imageHeight":360,"source":"Smart Canvas v1","sourceContentURL":"http://teamwork.smartcanvas.com/card/transportation","likeCounter":0,"active":true},{"id":"scv1_the-venetian","networkMnemonic":"teamwork","createdBy":"root","lastUpdated":1453416760012,"date":1449937847000,"title":"lodging information","summary":"lodging information","tags":["information","onsite"],"type":"announcement","theme":{"mnemonic":"blue_pink","primaryColor":"#2196f3","secondaryColor":"#64b5f6","accentColor":"#FF4081","primaryFont":"Roboto","secondaryFont":"Roboto","primaryFontColor":"#FFFFFF","secondaryFontColor":"#000000","accentFontColor":"#FFFFFF","createdBy":"root","lastUpdate":1451320381691},"category":"Information","authorId":"5657947837300736","authorDisplayName":"TeamWork Staff","authorImageURL":"https://lh3.googleusercontent.com/-WNI0u32wL1M/AAAAAAAAAAI/AAAAAAAAAC0/rhenGVMYIo0/photo.jpg?sz=200","imageURL":"https://lh3.googleusercontent.com/8Iw3goDG_qw5kp7h8IFB7ATixM4ovM13AMdqhLZ2fhvllyYxn7atVi25h5qkud9kGDy1-ofWAbRS2bfG","imageWidth":1000,"imageHeight":722,"source":"Smart Canvas v1","sourceContentURL":"http://teamwork.smartcanvas.com/card/the-venetian","likeCounter":0,"active":true},{"id":"scv1_come-rain-come-shine-contact-us","networkMnemonic":"teamwork","createdBy":"root","lastUpdated":1453416761861,"date":1449937125000,"title":"teamwork-2016-partners@google.com","summary":"teamwork-2016-partners@google.com","tags":["information"],"type":"announcement","theme":{"mnemonic":"blue_pink","primaryColor":"#2196f3","secondaryColor":"#64b5f6","accentColor":"#FF4081","primaryFont":"Roboto","secondaryFont":"Roboto","primaryFontColor":"#FFFFFF","secondaryFontColor":"#000000","accentFontColor":"#FFFFFF","createdBy":"root","lastUpdate":1451320381691},"category":"Information","authorId":"5657947837300736","authorDisplayName":"TeamWork Staff","authorImageURL":"https://lh3.googleusercontent.com/-WNI0u32wL1M/AAAAAAAAAAI/AAAAAAAAAC0/rhenGVMYIo0/photo.jpg?sz=200","imageURL":"https://lh3.googleusercontent.com/zzk9gytCqoqgDuvpdxYcP3mGLVMojPLIcDfQrDvIAlXtsCjRjjF5obNSBP_KSf6YxxrfIoUBbS532F0","imageWidth":1280,"imageHeight":960,"source":"Smart Canvas v1","sourceContentURL":"http://teamwork.smartcanvas.com/card/come-rain-come-shine-contact-us","likeCounter":0,"active":true},{"id":"scv1_1451053676684","networkMnemonic":"teamwork","createdBy":"root","lastUpdated":1453416713595,"date":1451053676587,"title":"I'll give feedback after I watch the keynote. No title here...","summary":"I&apos;ll give feedback after I watch the keynote. No title here...","tags":["discussion","feedback"],"type":"quote","theme":{"mnemonic":"blue_pink","primaryColor":"#2196f3","secondaryColor":"#64b5f6","accentColor":"#FF4081","primaryFont":"Roboto","secondaryFont":"Roboto","primaryFontColor":"#FFFFFF","secondaryFontColor":"#000000","accentFontColor":"#FFFFFF","createdBy":"root","lastUpdate":1451320381691},"category":"Q&A - Discussions","authorId":"5700305828184064","authorDisplayName":"Mars Cyrillo","authorImageURL":"https://lh4.googleusercontent.com/-e2ihE1RW1yg/AAAAAAAAAAI/AAAAAAABLyo/t5ONlrV1UxM/photo.jpg?sz=200","imageWidth":0,"imageHeight":0,"source":"Smart Canvas v1","sourceContentURL":"http://teamwork.smartcanvas.com/card/1451053676684","likeCounter":0,"active":true},{"id":"scv1_why-is-the-weather-in-brazil-sl-much-better-than-ireland","networkMnemonic":"teamwork","createdBy":"root","lastUpdated":1454168602940,"date":1454086764434,"title":"Why is the weather in Brazil sl much better than Ireland?","tags":["discussion","question"],"type":"quote","theme":{"mnemonic":"blue_pink","primaryColor":"#2196f3","secondaryColor":"#64b5f6","accentColor":"#FF4081","primaryFont":"Roboto","secondaryFont":"Roboto","primaryFontColor":"#FFFFFF","secondaryFontColor":"#000000","accentFontColor":"#FFFFFF","createdBy":"root","lastUpdate":1451320381691},"category":"Q&A - Questions","authorId":"6400954177945600","authorDisplayName":"Alan Wrafter","authorImageURL":"https://lh5.googleusercontent.com/-7WTWcW0Rz2U/AAAAAAAAAAI/AAAAAAAALsE/zPz5y0wkDHI/photo.jpg?sz=200","imageWidth":0,"imageHeight":0,"source":"Smart Canvas v1","sourceContentURL":"http://teamwork.smartcanvas.com/card/why-is-the-weather-in-brazil-sl-much-better-than-ireland","likeCounter":0,"active":true},{"id":"scv1_are-we-having-gambling","networkMnemonic":"teamwork","createdBy":"root","lastUpdated":1453416714066,"date":1451053589793,"title":"Titles should be marked as optional","summary":"Titles should be marked as optional","tags":["discussion","question"],"type":"quote","theme":{"mnemonic":"blue_pink","primaryColor":"#2196f3","secondaryColor":"#64b5f6","accentColor":"#FF4081","primaryFont":"Roboto","secondaryFont":"Roboto","primaryFontColor":"#FFFFFF","secondaryFontColor":"#000000","accentFontColor":"#FFFFFF","createdBy":"root","lastUpdate":1451320381691},"category":"Q&A - Questions","authorId":"5700305828184064","authorDisplayName":"Mars Cyrillo","authorImageURL":"https://lh4.googleusercontent.com/-e2ihE1RW1yg/AAAAAAAAAAI/AAAAAAABLyo/t5ONlrV1UxM/photo.jpg?sz=200","imageWidth":0,"imageHeight":0,"source":"Smart Canvas v1","sourceContentURL":"http://teamwork.smartcanvas.com/card/are-we-having-gambling","likeCounter":0,"active":true},{"id":"scv1_aodocs","networkMnemonic":"teamwork","createdBy":"root","lastUpdated":1453416753775,"date":1450286209000,"title":"Enterprise-grade document management","summary":"Enterprise-grade document management","tags":["recommendedapps"],"type":"quote","theme":{"mnemonic":"blue_pink","primaryColor":"#2196f3","secondaryColor":"#64b5f6","accentColor":"#FF4081","primaryFont":"Roboto","secondaryFont":"Roboto","primaryFontColor":"#FFFFFF","secondaryFontColor":"#000000","accentFontColor":"#FFFFFF","createdBy":"root","lastUpdate":1451320381691},"category":"Recommended Apps","authorId":"5444766934237184","authorDisplayName":"TeamWork Staff","authorImageURL":"https://lh3.googleusercontent.com/-WNI0u32wL1M/AAAAAAAAAAI/AAAAAAAAAC0/rhenGVMYIo0/photo.jpg?sz=200","imageURL":"https://lh3.googleusercontent.com/a4aXBq_SRcDd6IMgHChtkVDWuk8o09Cym24PLo4OY-W7OfBfu0rmmHXzrdfhI0n-MHgzn4xKUylEWzI","imageWidth":512,"imageHeight":285,"source":"Smart Canvas v1","sourceContentURL":"http://teamwork.smartcanvas.com/card/aodocs","likeCounter":0,"active":true},{"id":"scv1_ping","networkMnemonic":"teamwork","createdBy":"root","lastUpdated":1453416754137,"date":1450278832000,"title":"Secure Cloud App access","summary":"Secure Cloud App access","tags":["recommendedapps"],"type":"announcement","theme":{"mnemonic":"blue_pink","primaryColor":"#2196f3","secondaryColor":"#64b5f6","accentColor":"#FF4081","primaryFont":"Roboto","secondaryFont":"Roboto","primaryFontColor":"#FFFFFF","secondaryFontColor":"#000000","accentFontColor":"#FFFFFF","createdBy":"root","lastUpdate":1451320381691},"category":"Recommended Apps","authorId":"5444766934237184","authorDisplayName":"TeamWork Staff","authorImageURL":"https://lh3.googleusercontent.com/-WNI0u32wL1M/AAAAAAAAAAI/AAAAAAAAAC0/rhenGVMYIo0/photo.jpg?sz=200","imageURL":"https://lh3.googleusercontent.com/muB45KBY-_iWPXnJcWyFGMSaB_NDTu62XK_iTKz7Xt6eo7M4etVNRHp2h7N9C6US8MnexIxAzXDfGfU","imageWidth":1586,"imageHeight":888,"source":"Smart Canvas v1","sourceContentURL":"http://teamwork.smartcanvas.com/card/ping","likeCounter":0,"active":true},{"id":"scv1_strengthening-the-google-apps-ecosystem1","networkMnemonic":"teamwork","createdBy":"root","lastUpdated":1453416754509,"date":1450192798869,"title":"Posted by Rahul Sood, Managing Director, Google Apps for Work The promise of the cloud is to make...","summary":"Posted by Rahul Sood, Managing Director, Google Apps for Work The promise of the cloud is to make...","tags":["recommendedapps","fixed"],"type":"quote","theme":{"mnemonic":"blue_pink","primaryColor":"#2196f3","secondaryColor":"#64b5f6","accentColor":"#FF4081","primaryFont":"Roboto","secondaryFont":"Roboto","primaryFontColor":"#FFFFFF","secondaryFontColor":"#000000","accentFontColor":"#FFFFFF","createdBy":"root","lastUpdate":1451320381691},"category":"Recommended Apps","authorId":"4659498484498432","authorDisplayName":"Jane Smith","authorImageURL":"https://lh3.googleusercontent.com/-f6THhb28KpE/AAAAAAAAAAI/AAAAAAAAgT0/9kpMskhYNBs/photo.jpg","imageURL":"https://lh3.googleusercontent.com/mTjRUDbyVIjc5_TShvRqea4LYF5OkIiVJ1qYWnBf8Mmz7KfeBzq3jhbgE-STh3IeofH6Nwe7x3osd4wF","imageWidth":512,"imageHeight":257,"source":"Smart Canvas v1","sourceContentURL":"http://teamwork.smartcanvas.com/card/strengthening-the-google-apps-ecosystem1","likeCounter":0,"active":true},{"id":"scv1_powertools","networkMnemonic":"teamwork","createdBy":"root","lastUpdated":1453416754916,"date":1450188921000,"title":"Great storage is one thing. Structure is another.","summary":"Great storage is one thing. Structure is another.","tags":["recommendedapps","apps"],"type":"announcement","theme":{"mnemonic":"blue_pink","primaryColor":"#2196f3","secondaryColor":"#64b5f6","accentColor":"#FF4081","primaryFont":"Roboto","secondaryFont":"Roboto","primaryFontColor":"#FFFFFF","secondaryFontColor":"#000000","accentFontColor":"#FFFFFF","createdBy":"root","lastUpdate":1451320381691},"category":"Recommended Apps","authorId":"5657947837300736","authorDisplayName":"TeamWork Staff","authorImageURL":"https://lh3.googleusercontent.com/-WNI0u32wL1M/AAAAAAAAAAI/AAAAAAAAAC0/rhenGVMYIo0/photo.jpg?sz=200","imageURL":"https://lh3.googleusercontent.com/RuVloJb-eO-P2jlVdK-Gz8CTJWHvcoKrKZLed-gx2S7tQSn1dtkhxHDyjs2FPrv08oFc9OaAz-bUPFE","imageWidth":1736,"imageHeight":976,"source":"Smart Canvas v1","sourceContentURL":"http://teamwork.smartcanvas.com/card/powertools","likeCounter":0,"active":true},{"id":"scv1_ringcentral","networkMnemonic":"teamwork","createdBy":"root","lastUpdated":1453416755346,"date":1449981793000,"title":"Extend the power of Google for Work with live communications","summary":"Extend the power of Google for Work with live communications","tags":["recommendedapps","apps"],"type":"announcement","theme":{"mnemonic":"blue_pink","primaryColor":"#2196f3","secondaryColor":"#64b5f6","accentColor":"#FF4081","primaryFont":"Roboto","secondaryFont":"Roboto","primaryFontColor":"#FFFFFF","secondaryFontColor":"#000000","accentFontColor":"#FFFFFF","createdBy":"root","lastUpdate":1451320381691},"category":"Recommended Apps","authorId":"5657947837300736","authorDisplayName":"TeamWork Staff","authorImageURL":"https://lh3.googleusercontent.com/-WNI0u32wL1M/AAAAAAAAAAI/AAAAAAAAAC0/rhenGVMYIo0/photo.jpg?sz=200","imageURL":"https://lh3.googleusercontent.com/x4N3CncjzOkCpKakqafaqbwGVIJg9UjvP-fxtXUc-nIDfdNkKEdKzIRKICwd2_shSGGsr6yd1gzzRC4","imageWidth":1744,"imageHeight":980,"source":"Smart Canvas v1","sourceContentURL":"http://teamwork.smartcanvas.com/card/ringcentral","likeCounter":0,"active":true}]};
            var mock = {
                "stationMnemonic": "everything",
                "contents": [
                    {
                        "lastUpdated": 1454943449007,
                        "date": 1454943449007,
                        "tags": [
                            "match"
                        ],
                        "type": "match",
                        "theme": {
                            "primaryColor": "#FAFAFA",
                            "secondaryColor": "#FAFAFA",
                            "accentColor": "#4285F4",
                            "primaryFont": "Roboto-Bold",
                            "secondaryFont": "Roboto-Light",
                            "accentFont": "Roboto-Medium",
                            "primaryFontColor": "#373737",
                            "secondaryFontColor": "#717171",
                            "accentFontColor": "#FFFFFF",
                            "lastUpdate": 1455137642796
                        },
                        "category": "Match",
                        "jsonExtendedData": {
                            "person1": {
                                "alias": "iluz-ciandt-com",
                                "backgroundImageURL": "https://lh3.googleusercontent.com/rPorn9NNtZlOpraPB3sH4fb9FLWClyLay1RQC5Mv4h0uafpFbMgOiGatwmFtbvgLdfDv=s630-fcrop64=1,000017ebffffd88f",
                                "companyName": "CI&T",
                                "deviceIdentifier": "e-LOF9VJCtE:APA91bGc-hjIOlph0rjxRrMun1n8XDgpcokL0ya4mFNIh245if7hn_03dMJkBv1Zgac4EB1kieDVDP2Q1xCpsW6frNlohUjFKxQzlJ-JXNRZzeFnGGZ7ZiRgNI34OsoK1mS65xV_3_Fg",
                                "deviceIdentifierList": [
                                    "e-LOF9VJCtE:APA91bGc-hjIOlph0rjxRrMun1n8XDgpcokL0ya4mFNIh245if7hn_03dMJkBv1Zgac4EB1kieDVDP2Q1xCpsW6frNlohUjFKxQzlJ-JXNRZzeFnGGZ7ZiRgNI34OsoK1mS65xV_3_Fg"
                                ],
                                "deviceType": "android",
                                "deviceTypeList": [
                                    "android"
                                ],
                                "displayName": "Ivam dos Santos Luz",
                                "email": "iluz@ciandt.com",
                                "imageURL": "https://lh6.googleusercontent.com/-zozLKqoW6c4/AAAAAAAAAAI/AAAAAAAABCU/F0siAEhw8wA/photo.jpg?sz=50",
                                "lastUpdate": "2016-02-08T14:57:16.879+0000",
                                "position": "Product Engineer",
                                "source": "connector",
                                "tags": [
                                    "devices",
                                    "Technology Partner"
                                ]
                            },
                            "person2": {
                                "alias": "viveiros-ciandt-com",
                                "backgroundImageURL": "http://lh3.googleusercontent.com/Dhsa7BTcxj78OHlZBxrR2mrKblruKtM0MSoqNWa4AeLc9cXWlevnu6coDUJmpHZ4xLI6RR9XpgwRaR2ChPTR5WgddtDbWQ",
                                "companyName": "CI&T",
                                "deviceIdentifierList": [
                                    "dIBN3VDc3xo:APA91bGur8HBtfRW--5KSraYHwyX2-2JT4WSo6cZZaO57Xlf8YolCdIHUGy6yyMBDChPquIUNSSAicP9cNx7Q88JZyuKgPKUXCPY99gmktNsohGWiankOyzS5cRHDfgOtYUbXhpOv4hY"
                                ],
                                "deviceTypeList": [
                                    "android"
                                ],
                                "displayName": "Daniel Viveiros",
                                "email": "viveiros@ciandt.com",
                                "imageURL": "https://lh3.googleusercontent.com/-tr003KjHJYk/AAAAAAAAAAI/AAAAAAAAR3k/skFlRmoKwkk/photo.jpg?sz=50",
                                "lastUpdate": "2016-02-08T12:33:08.473+0000",
                                "position": "Head of Product Engineering",
                                "source": "connector",
                                "tags": [
                                    "cloud",
                                    "Sales & Services Partner"
                                ]
                            }
                        }
                    },
                    {
                        "lastUpdated": 1454190447276,
                        "date": 1454086700154,
                        "title": "Marketing fun!",
                        "tags": [
                            "photos"
                        ],
                        "type": "announcement",
                        "theme": {
                            "primaryColor": "#FAFAFA",
                            "secondaryColor": "#FAFAFA",
                            "accentColor": "#4285F4",
                            "primaryFont": "Roboto-Bold",
                            "secondaryFont": "Roboto-Light",
                            "accentFont": "Roboto-Medium",
                            "primaryFontColor": "#373737",
                            "secondaryFontColor": "#717171",
                            "accentFontColor": "#FFFFFF",
                            "lastUpdate": 1455137642796
                        },
                        "category": "Great Photos!",
                        "authorDisplayName": "Alan Wrafter",
                        "authorImageURL": "https://lh5.googleusercontent.com/-7WTWcW0Rz2U/AAAAAAAAAAI/AAAAAAAALsE/zPz5y0wkDHI/photo.jpg?sz=200",
                        "imageURL": "https://lh3.googleusercontent.com/ty1VzZdhDlnzFG5rg2TLbp2GU_QjYjOQ7GUuNGpw2v8yc34pyptv0XYsU3v2Oasc8akheDDwYKD2XQlf",
                        "source": "Smart Canvas v1",
                        "sourceContentURL": "http://teamwork.smartcanvas.com/card/marketing-fun"
                    },
                    {
                        "lastUpdated": 1454190447673,
                        "date": 1452789441140,
                        "title": "Merry Xmas",
                        "tags": [
                            "photos"
                        ],
                        "type": "quote",
                        "theme": {
                            "primaryColor": "#FAFAFA",
                            "secondaryColor": "#FAFAFA",
                            "accentColor": "#4285F4",
                            "primaryFont": "Roboto-Bold",
                            "secondaryFont": "Roboto-Light",
                            "accentFont": "Roboto-Medium",
                            "primaryFontColor": "#373737",
                            "secondaryFontColor": "#717171",
                            "accentFontColor": "#FFFFFF",
                            "lastUpdate": 1455137642796
                        },
                        "category": "Great Photos!",
                        "authorDisplayName": "Mars Cyrillo",
                        "authorImageURL": "https://lh4.googleusercontent.com/-e2ihE1RW1yg/AAAAAAAAAAI/AAAAAAABLyo/t5ONlrV1UxM/photo.jpg?sz=200",
                        "imageURL": "https://lh3.googleusercontent.com/x18-cy4MTmzpz4VE72PlCofTA5i5t2KVX456qJaVKpnORlbkr_scXT7icWxTh_INZqWfzNK0gESs1fPFaA",
                        "source": "Smart Canvas v1",
                        "sourceContentURL": "http://teamwork.smartcanvas.com/card/merry-xmas"
                    },
                    {
                        "lastUpdated": 1454498345477,
                        "date": 1454498336429,
                        "title": "Come hangout and mingle with Devices Sales, Services & Technology Partners.",
                        "summary": "Come hangout and mingle with Devices Sales, Services & Technology Partners.",
                        "tags": [
                            "information",
                            "onsite"
                        ],
                        "type": "quote",
                        "theme": {
                            "primaryColor": "#FAFAFA",
                            "secondaryColor": "#FAFAFA",
                            "accentColor": "#4285F4",
                            "primaryFont": "Roboto-Bold",
                            "secondaryFont": "Roboto-Light",
                            "accentFont": "Roboto-Medium",
                            "primaryFontColor": "#373737",
                            "secondaryFontColor": "#717171",
                            "accentFontColor": "#FFFFFF",
                            "lastUpdate": 1455137642796
                        },
                        "category": "Information",
                        "authorDisplayName": "TeamWork Staff",
                        "authorImageURL": "https://lh3.googleusercontent.com/-WNI0u32wL1M/AAAAAAAAAAI/AAAAAAAAAC0/rhenGVMYIo0/photo.jpg",
                        "source": "Smart Canvas v1",
                        "sourceContentURL": "http://teamwork.smartcanvas.com/card/devices-networking-zone"
                    },
                    {
                        "lastUpdated": 1454498345933,
                        "date": 1454498321126,
                        "title": "Come hangout and mingle with Maps Sales, Services & Technology Partners.",
                        "summary": "Come hangout and mingle with Maps Sales, Services & Technology Partners.",
                        "tags": [
                            "information",
                            "onsite"
                        ],
                        "type": "quote",
                        "theme": {
                            "primaryColor": "#FAFAFA",
                            "secondaryColor": "#FAFAFA",
                            "accentColor": "#4285F4",
                            "primaryFont": "Roboto-Bold",
                            "secondaryFont": "Roboto-Light",
                            "accentFont": "Roboto-Medium",
                            "primaryFontColor": "#373737",
                            "secondaryFontColor": "#717171",
                            "accentFontColor": "#FFFFFF",
                            "lastUpdate": 1455137642796
                        },
                        "category": "Information",
                        "authorDisplayName": "TeamWork Staff",
                        "authorImageURL": "https://lh3.googleusercontent.com/-WNI0u32wL1M/AAAAAAAAAAI/AAAAAAAAAC0/rhenGVMYIo0/photo.jpg",
                        "source": "Smart Canvas v1",
                        "sourceContentURL": "http://teamwork.smartcanvas.com/card/maps-networking-zone"
                    },
                    {
                        "lastUpdated": 1454498346257,
                        "date": 1454498313909,
                        "title": "Come hangout and mingle with Cloud Sales, Services & Technology Partners.",
                        "summary": "Come hangout and mingle with Cloud Sales, Services & Technology Partners.",
                        "tags": [
                            "information",
                            "onsite"
                        ],
                        "type": "quote",
                        "theme": {
                            "primaryColor": "#FAFAFA",
                            "secondaryColor": "#FAFAFA",
                            "accentColor": "#4285F4",
                            "primaryFont": "Roboto-Bold",
                            "secondaryFont": "Roboto-Light",
                            "accentFont": "Roboto-Medium",
                            "primaryFontColor": "#373737",
                            "secondaryFontColor": "#717171",
                            "accentFontColor": "#FFFFFF",
                            "lastUpdate": 1455137642796
                        },
                        "category": "Information",
                        "authorDisplayName": "TeamWork Staff",
                        "authorImageURL": "https://lh3.googleusercontent.com/-WNI0u32wL1M/AAAAAAAAAAI/AAAAAAAAAC0/rhenGVMYIo0/photo.jpg",
                        "source": "Smart Canvas v1",
                        "sourceContentURL": "http://teamwork.smartcanvas.com/card/cloud-networking-zone"
                    },
                    {
                        "lastUpdated": 1454498346561,
                        "date": 1454498205315,
                        "title": "Come hangout and mingle with Apps Sales, Services & Technology Partners.",
                        "summary": "Come hangout and mingle with Apps Sales, Services & Technology Partners.",
                        "tags": [
                            "information",
                            "onsite"
                        ],
                        "type": "quote",
                        "theme": {
                            "primaryColor": "#FAFAFA",
                            "secondaryColor": "#FAFAFA",
                            "accentColor": "#4285F4",
                            "primaryFont": "Roboto-Bold",
                            "secondaryFont": "Roboto-Light",
                            "accentFont": "Roboto-Medium",
                            "primaryFontColor": "#373737",
                            "secondaryFontColor": "#717171",
                            "accentFontColor": "#FFFFFF",
                            "lastUpdate": 1455137642796
                        },
                        "category": "Information",
                        "authorDisplayName": "TeamWork Staff",
                        "authorImageURL": "https://lh3.googleusercontent.com/-WNI0u32wL1M/AAAAAAAAAAI/AAAAAAAAAC0/rhenGVMYIo0/photo.jpg",
                        "source": "Smart Canvas v1",
                        "sourceContentURL": "http://teamwork.smartcanvas.com/card/apps-networking-zone"
                    },
                    {
                        "lastUpdated": 1454190493261,
                        "date": 1450017459000,
                        "title": "https://connect.googleforwork.com/community/partners/teamwork-2016",
                        "summary": "https://connect.googleforwork.com/community/partners/teamwork-2016",
                        "tags": [
                            "information",
                            "onsite"
                        ],
                        "type": "announcement",
                        "theme": {
                            "primaryColor": "#FAFAFA",
                            "secondaryColor": "#FAFAFA",
                            "accentColor": "#4285F4",
                            "primaryFont": "Roboto-Bold",
                            "secondaryFont": "Roboto-Light",
                            "accentFont": "Roboto-Medium",
                            "primaryFontColor": "#373737",
                            "secondaryFontColor": "#717171",
                            "accentFontColor": "#FFFFFF",
                            "lastUpdate": 1455137642796
                        },
                        "category": "Information",
                        "authorDisplayName": "TeamWork Staff",
                        "authorImageURL": "https://lh3.googleusercontent.com/-WNI0u32wL1M/AAAAAAAAAAI/AAAAAAAAAC0/rhenGVMYIo0/photo.jpg?sz=200",
                        "imageURL": "https://lh3.googleusercontent.com/BeUa5CPk5pWuNs6LSZyrESLliF8H7wDvk-ASxkAoKa6fboN2pZXyQ9gX2cbr3TST9yKOKnFX6EmcsNEfrQ",
                        "source": "Smart Canvas v1",
                        "sourceContentURL": "http://teamwork.smartcanvas.com/card/our-page-on-gwc"
                    },
                    {
                        "lastUpdated": 1454190444620,
                        "date": 1454086764434,
                        "title": "Why is the weather in Brazil sl much better than Ireland?",
                        "tags": [
                            "discussion",
                            "question"
                        ],
                        "type": "quote",
                        "theme": {
                            "primaryColor": "#FAFAFA",
                            "secondaryColor": "#FAFAFA",
                            "accentColor": "#4285F4",
                            "primaryFont": "Roboto-Bold",
                            "secondaryFont": "Roboto-Light",
                            "accentFont": "Roboto-Medium",
                            "primaryFontColor": "#373737",
                            "secondaryFontColor": "#717171",
                            "accentFontColor": "#FFFFFF",
                            "lastUpdate": 1455137642796
                        },
                        "category": "Questions",
                        "authorDisplayName": "Alan Wrafter",
                        "authorImageURL": "https://lh5.googleusercontent.com/-7WTWcW0Rz2U/AAAAAAAAAAI/AAAAAAAALsE/zPz5y0wkDHI/photo.jpg?sz=200",
                        "source": "Smart Canvas v1",
                        "sourceContentURL": "http://teamwork.smartcanvas.com/card/why-is-the-weather-in-brazil-sl-much-better-than-ireland"
                    },
                    {
                        "lastUpdated": 1454190445296,
                        "date": 1451053589793,
                        "title": "Titles should be marked as optional",
                        "summary": "Titles should be marked as optional",
                        "tags": [
                            "discussion",
                            "question"
                        ],
                        "type": "quote",
                        "theme": {
                            "primaryColor": "#FAFAFA",
                            "secondaryColor": "#FAFAFA",
                            "accentColor": "#4285F4",
                            "primaryFont": "Roboto-Bold",
                            "secondaryFont": "Roboto-Light",
                            "accentFont": "Roboto-Medium",
                            "primaryFontColor": "#373737",
                            "secondaryFontColor": "#717171",
                            "accentFontColor": "#FFFFFF",
                            "lastUpdate": 1455137642796
                        },
                        "category": "Questions",
                        "authorDisplayName": "Mars Cyrillo",
                        "authorImageURL": "https://lh4.googleusercontent.com/-e2ihE1RW1yg/AAAAAAAAAAI/AAAAAAABLyo/t5ONlrV1UxM/photo.jpg?sz=200",
                        "source": "Smart Canvas v1",
                        "sourceContentURL": "http://teamwork.smartcanvas.com/card/are-we-having-gambling"
                    },
                    {
                        "lastUpdated": 1454190443695,
                        "date": 1451053676587,
                        "title": "I'll give feedback after I watch the keynote. No title here...",
                        "summary": "I&apos;ll give feedback after I watch the keynote. No title here...",
                        "tags": [
                            "discussion",
                            "feedback"
                        ],
                        "type": "quote",
                        "theme": {
                            "primaryColor": "#FAFAFA",
                            "secondaryColor": "#FAFAFA",
                            "accentColor": "#4285F4",
                            "primaryFont": "Roboto-Bold",
                            "secondaryFont": "Roboto-Light",
                            "accentFont": "Roboto-Medium",
                            "primaryFontColor": "#373737",
                            "secondaryFontColor": "#717171",
                            "accentFontColor": "#FFFFFF",
                            "lastUpdate": 1455137642796
                        },
                        "category": "Feedback",
                        "authorDisplayName": "Mars Cyrillo",
                        "authorImageURL": "https://lh4.googleusercontent.com/-e2ihE1RW1yg/AAAAAAAAAAI/AAAAAAABLyo/t5ONlrV1UxM/photo.jpg?sz=200",
                        "source": "Smart Canvas v1",
                        "sourceContentURL": "http://teamwork.smartcanvas.com/card/1451053676684"
                    },
                    {
                        "lastUpdated": 1454190487236,
                        "date": 1450286209000,
                        "title": "Enterprise-grade document management",
                        "summary": "Enterprise-grade document management",
                        "tags": [
                            "recommendedapps"
                        ],
                        "type": "quote",
                        "theme": {
                            "primaryColor": "#FAFAFA",
                            "secondaryColor": "#FAFAFA",
                            "accentColor": "#4285F4",
                            "primaryFont": "Roboto-Bold",
                            "secondaryFont": "Roboto-Light",
                            "accentFont": "Roboto-Medium",
                            "primaryFontColor": "#373737",
                            "secondaryFontColor": "#717171",
                            "accentFontColor": "#FFFFFF",
                            "lastUpdate": 1455137642796
                        },
                        "category": "Recommended Apps",
                        "authorDisplayName": "TeamWork Staff",
                        "authorImageURL": "https://lh3.googleusercontent.com/-WNI0u32wL1M/AAAAAAAAAAI/AAAAAAAAAC0/rhenGVMYIo0/photo.jpg?sz=200",
                        "imageURL": "https://lh3.googleusercontent.com/a4aXBq_SRcDd6IMgHChtkVDWuk8o09Cym24PLo4OY-W7OfBfu0rmmHXzrdfhI0n-MHgzn4xKUylEWzI",
                        "source": "Smart Canvas v1",
                        "sourceContentURL": "http://teamwork.smartcanvas.com/card/aodocs"
                    },
                    {
                        "lastUpdated": 1454190487570,
                        "date": 1450278832000,
                        "title": "Secure Cloud App access",
                        "summary": "Secure Cloud App access",
                        "tags": [
                            "recommendedapps"
                        ],
                        "type": "announcement",
                        "theme": {
                            "primaryColor": "#FAFAFA",
                            "secondaryColor": "#FAFAFA",
                            "accentColor": "#4285F4",
                            "primaryFont": "Roboto-Bold",
                            "secondaryFont": "Roboto-Light",
                            "accentFont": "Roboto-Medium",
                            "primaryFontColor": "#373737",
                            "secondaryFontColor": "#717171",
                            "accentFontColor": "#FFFFFF",
                            "lastUpdate": 1455137642796
                        },
                        "category": "Recommended Apps",
                        "authorDisplayName": "TeamWork Staff",
                        "authorImageURL": "https://lh3.googleusercontent.com/-WNI0u32wL1M/AAAAAAAAAAI/AAAAAAAAAC0/rhenGVMYIo0/photo.jpg?sz=200",
                        "imageURL": "https://lh3.googleusercontent.com/muB45KBY-_iWPXnJcWyFGMSaB_NDTu62XK_iTKz7Xt6eo7M4etVNRHp2h7N9C6US8MnexIxAzXDfGfU",
                        "source": "Smart Canvas v1",
                        "sourceContentURL": "http://teamwork.smartcanvas.com/card/ping"
                    },
                    {
                        "lastUpdated": 1454190487909,
                        "date": 1450192798869,
                        "title": "Posted by Rahul Sood, Managing Director, Google Apps for Work The promise of the cloud is to make...",
                        "summary": "Posted by Rahul Sood, Managing Director, Google Apps for Work The promise of the cloud is to make...",
                        "tags": [
                            "recommendedapps",
                            "fixed"
                        ],
                        "type": "quote",
                        "theme": {
                            "primaryColor": "#FAFAFA",
                            "secondaryColor": "#FAFAFA",
                            "accentColor": "#4285F4",
                            "primaryFont": "Roboto-Bold",
                            "secondaryFont": "Roboto-Light",
                            "accentFont": "Roboto-Medium",
                            "primaryFontColor": "#373737",
                            "secondaryFontColor": "#717171",
                            "accentFontColor": "#FFFFFF",
                            "lastUpdate": 1455137642796
                        },
                        "category": "Recommended Apps",
                        "authorDisplayName": "Jane Smith",
                        "authorImageURL": "https://lh3.googleusercontent.com/-f6THhb28KpE/AAAAAAAAAAI/AAAAAAAAgT0/9kpMskhYNBs/photo.jpg",
                        "imageURL": "https://lh3.googleusercontent.com/mTjRUDbyVIjc5_TShvRqea4LYF5OkIiVJ1qYWnBf8Mmz7KfeBzq3jhbgE-STh3IeofH6Nwe7x3osd4wF",
                        "source": "Smart Canvas v1",
                        "sourceContentURL": "http://teamwork.smartcanvas.com/card/strengthening-the-google-apps-ecosystem1"
                    },
                    {
                        "lastUpdated": 1454190488282,
                        "date": 1450188921000,
                        "title": "Great storage is one thing. Structure is another.",
                        "summary": "Great storage is one thing. Structure is another.",
                        "tags": [
                            "recommendedapps",
                            "apps"
                        ],
                        "type": "announcement",
                        "theme": {
                            "primaryColor": "#FAFAFA",
                            "secondaryColor": "#FAFAFA",
                            "accentColor": "#4285F4",
                            "primaryFont": "Roboto-Bold",
                            "secondaryFont": "Roboto-Light",
                            "accentFont": "Roboto-Medium",
                            "primaryFontColor": "#373737",
                            "secondaryFontColor": "#717171",
                            "accentFontColor": "#FFFFFF",
                            "lastUpdate": 1455137642796
                        },
                        "category": "Recommended Apps",
                        "authorDisplayName": "TeamWork Staff",
                        "authorImageURL": "https://lh3.googleusercontent.com/-WNI0u32wL1M/AAAAAAAAAAI/AAAAAAAAAC0/rhenGVMYIo0/photo.jpg?sz=200",
                        "imageURL": "https://lh3.googleusercontent.com/RuVloJb-eO-P2jlVdK-Gz8CTJWHvcoKrKZLed-gx2S7tQSn1dtkhxHDyjs2FPrv08oFc9OaAz-bUPFE",
                        "source": "Smart Canvas v1",
                        "sourceContentURL": "http://teamwork.smartcanvas.com/card/powertools"
                    },
                    {
                        "lastUpdated": 1454190489607,
                        "date": 1449981793000,
                        "title": "Extend the power of Google for Work with live communications",
                        "summary": "Extend the power of Google for Work with live communications",
                        "tags": [
                            "recommendedapps",
                            "apps"
                        ],
                        "type": "announcement",
                        "theme": {
                            "primaryColor": "#FAFAFA",
                            "secondaryColor": "#FAFAFA",
                            "accentColor": "#4285F4",
                            "primaryFont": "Roboto-Bold",
                            "secondaryFont": "Roboto-Light",
                            "accentFont": "Roboto-Medium",
                            "primaryFontColor": "#373737",
                            "secondaryFontColor": "#717171",
                            "accentFontColor": "#FFFFFF",
                            "lastUpdate": 1455137642796
                        },
                        "category": "Recommended Apps",
                        "authorDisplayName": "TeamWork Staff",
                        "authorImageURL": "https://lh3.googleusercontent.com/-WNI0u32wL1M/AAAAAAAAAAI/AAAAAAAAAC0/rhenGVMYIo0/photo.jpg?sz=200",
                        "imageURL": "https://lh3.googleusercontent.com/x4N3CncjzOkCpKakqafaqbwGVIJg9UjvP-fxtXUc-nIDfdNkKEdKzIRKICwd2_shSGGsr6yd1gzzRC4",
                        "source": "Smart Canvas v1",
                        "sourceContentURL": "http://teamwork.smartcanvas.com/card/ringcentral"
                    }
                ]
            }
            if (onSuccess) {
              onSuccess(mock);
            }
            /*/
            $.ajax({
                url: Config.settings.baseURL + uri,
                headers: requestHeaders,
                method: 'GET',
                cors: true,
                dataType: 'json',
                success: function (data) {
                    if (onSuccess) {
                        onSuccess(data);
                    }
                },
                error: function (xhr, status) {
                    var err = eval("(" + xhr.responseText + ")");
                    console.error('Error executing a GET method on Co-Cast: URI = ' + uri + ', error = ' + status);
                    if (onError) {
                        onError(xhr, status);
                    }
                }
            });
            /**/
        }
    };

    /**
     * Co-Cast basic functions
     */
    var CoCastInitializer = {

        /**
         * Authenticates the user on Co-Cast exchanging the live token for the access token
         */
        authenticate: function (authSuccess, authError) {
            var requestHeaders = {
                'x-live-token': Config.settings.liveToken,
                'x-client-id': Config.settings.clientId
            }

            //* mock
              if (authSuccess) {
                authSuccess(true);
              }
            /*/
            $.ajax({
                url: Config.settings.baseURL + '/auth/v1/live',
                headers: requestHeaders,
                cors: true,
                method: 'POST',
                dataType: 'json',
                success: function (data) {
                    CoCastInitializer.accessToken = data.accessToken;
                    console.log("Authenticated successfully");
                    if (authSuccess) {
                        authSuccess(CoCastInitializer.accessToken);
                    }
                },
                error: function (xhr, status) {
                    console.error('Error authenticating on Co-Cast: ' + status);
                    if (authError) {
                        authError(status);
                    }
                }
            });
            /**/
        },

        getLiveStream: function (onSuccess, onError) {
            CoCastInitializer.liveStream = APICaller.get('/core/v1/live/teamwork/everything', onSuccess, onError);
        }
    };

    /**
     * Co-Cast Live! Main execution
     */
    window.isWebComponentsReady = false;
    window.addEventListener('WebComponentsReady', function() {
        window.isWebComponentsReady = true;
        console.log("Initializing Co-Cast...");

        loading = document.querySelector('#loading');
        station = document.querySelector('#station');

        loading.visibility = true;

        //gets all the basic information from query params
        var networkMnemonic = Util.getParameterByName('network');
        var stationMnemonic = Util.getParameterByName('station');
        var clientId = Util.getParameterByName('clientId');
        var liveToken = Util.getParameterByName('liveToken');
        var env = Util.getParameterByName('env');

        if (!networkMnemonic || !stationMnemonic || !clientId || !liveToken) {
            console.error("You must provide network, station, clientId and liveToken as query params");
        } else {
            //Initializes the configuration object
            Config.init(networkMnemonic, stationMnemonic, clientId, liveToken, env);
        }

        //Authenticates on Co-Cast
        CoCastInitializer.authenticate(getLiveStream);
    });

    var api = new CoCast.Api.Station();

    /**
     * Calls the live stream to get data
     */
    function getLiveStream(authenticating) {
      //Gets the live stream
      var data = CoCastInitializer.getLiveStream(function (data) {
          console.log("Got data from Co-cast, showing it", data);

          api.getStation(data).then(function(model){
            console.log("Got processed station model", model);
            station.contents = model.contents;
            //station.styles = model.getStyles();
            //station.channels = model.getChannels();
            //station.caption = model.getCaption();

            loading.visibility = false;
          });
      });

      if (authenticating) {
        //setup
        station.addEventListener('channel.finished.rendering', function(){
          this.carousel.loadNext();
        });
      }
    }


})();
