<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>

<html>

  <head>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
  </head>

  <body>


<c:if test="${userAuthenticated == 'true'}">
<p>Hello, ${nickname}! (You can <a href="${logoutURL}">sign out</a>.)</p>
</c:if>

<c:if test="${userAuthenticated == 'false'}">
<p>Hello! <a href="${loginURL}">Sign in</a> to include your name with greetings you post.</p>
</c:if>

<c:if test="${greetingsSize == 0}">
<p>The guestbook has no messages.</p>
</c:if>

<c:if test="${greetingsSize > 0}">
	<c:forEach var="greeting" items="${greetings}">
		<c:if test="${greeting.author == null}">
			<p>An anonymous person wrote:</p>
		</c:if>
		<c:if test="${greeting.author != null}">
			<p><b>${greeting.author.nickname}</b> wrote:</p>
		</c:if>
		<blockquote>${greeting.content}</blockquote>
	</c:forEach>
</c:if>


  <form action="/sign" method="post">
    <div><textarea name="content" rows="3" cols="60"></textarea></div>
    <div><input type="submit" value="Post Greeting" /></div>
  </form>

  </body>
</html>