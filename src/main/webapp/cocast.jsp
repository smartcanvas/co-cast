<!DOCTYPE html>
<html>

<%
  String castViewMnemonic = (String) request.getAttribute( "castViewMnemonic" );
  String title = (String) request.getAttribute( "title" );
  String nextCastView = (String) request.getAttribute( "nextCastView" );
  String headerBackgroundColor = (String) request.getAttribute( "headerBackgroundColor" );
  String headerColor = (String) request.getAttribute( "headerColor" );
  String progressContainerColor = (String) request.getAttribute( "progressContainerColor" );
  String activeProgressColor = (String) request.getAttribute( "activeProgressColor" );
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
      background-color: #<%= headerBackgroundColor %>;
      font: normal normal 18px 'Roboto', sans-serif;
      text-transform: uppercase;
      color: #<%= headerColor %>;
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
      background-color: #<%= progressContainerColor %>;
    }

    /* PROGRESS */
    paper-progress::shadow #activeProgress {
      background-color: #<%= activeProgressColor %>;
    }

  </style>
  
</head>

<body fullbleed layout vertical>

  <form action="/cocast" method="get">
    <input type="hidden" name="castView" value="<%= nextCastView %>"/>
  </form>

  <!-- Gets the data from the API -->
  <core-ajax url="/api/castviews/<%= castViewMnemonic %>" handleAs="json"></core-ajax>

  <div class="header">
    <h1 class="header-title"><%= title %></h1>
  </div>
  
  <paper-progress class="bottom fit" value="0" id="progressBar"></paper-progress>

  <div class="content">
    <!-- <div class="fancy" selected="0" id="casted-card-list"></div> -->
    <core-pages class="fancy" selected="0" id="casted-card-list"></core-pages>
  </div>

  <script>var nextPage='/cocast?castView=<%= nextCastView %>'</script>

</body>

</html>