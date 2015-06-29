<!DOCTYPE html>
<html>

<%
  String castViewMnemonic = "whatsnew";
  String title = "What's New";
  String nextCastView = "teste";
  String headerBackgroundColor = "#FFF";
  String headerColor = "#80b33a";
  String progressContainerColor = "#000";
  String activeProgressColor = "#94cf43";
%>

<head>
  <!-- Load webcomponents.min.js for polyfill support. -->
  <script src="bower_components/webcomponentsjs/webcomponents.min.js"></script>

  <!-- Common imports and JS -->
  <link rel="import" href="common.html">

  <!-- CSS -->
  <link href='http://fonts.googleapis.com/css?family=Roboto' rel='stylesheet' type='text/css'>
  <link rel="stylesheet" href="css/cocast.css">

  <style type="text/css">

    body {
      background-color: #ecf0f1;
      overflow: hidden;
    }

    .header{
      background-color: <%= headerBackgroundColor %>;
      font: normal normal 18px 'Roboto', sans-serif;
      text-transform: uppercase;
      color: <%= headerColor %>;
      height: 78px;
      -webkit-box-shadow: 0 2px 4px 0 rgba(0,0,0,.25);
      -moz-box-shadow: 0 2px 4px 0 rgba(0,0,0,.25);
      box-shadow: 0 2px 4px 0 rgba(0,0,0,.25);
    }

    .header-title{
      margin-left: 20px;
    }

    core-pages {
      margin: 0 !important;
      height: 100%;
      position: absolute;
      top: 82px;
      bottom: 0;
      left: 0;
      right: 0;
    }

    /* BAR */
    paper-progress{
      width: 100%;
    }

    paper-progress::shadow #progressContainer {
      background-color: <%= progressContainerColor %>;
    }

    /* PROGRESS */
    paper-progress::shadow #activeProgress {
      background-color: <%= activeProgressColor %>;
    }

  </style>
  
</head>

<body fullbleed layout vertical>

  <!-- Gets the data from the API -->
  <core-ajax url="/api/castview/<%= castViewMnemonic %>" handleAs="json"></core-ajax>

  <div class="header">
    <h1 class="header-title"><%= title %></h1>
  </div>
  
  <paper-progress class="bottom fit" value="0" id="progressBar"></paper-progress>

  <div class="content">
    <!-- <div class="fancy" selected="0" id="casted-card-list"></div> -->
    <core-pages class="fancy" selected="0" id="casted-card-list"></core-pages>
  </div>

  <script>var nextPage='<%= nextCastView %>'</script>

</body>

</html>