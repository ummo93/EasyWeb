<html>
<head>
    <meta charset="UTF-8">
    <title>World, hello</title>
    <link rel="icon" type="image/png" href="favicon.ico">
    <link rel="stylesheet" href="materialize.min.css">
    <link href="http://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="style.css">
</head>
<body>
<nav class="navbar-header" role="navigation">
        <div class="nav-wrapper container">
            <a id="logo-container" href="#" class="brand-logo">
                Logo
            </a>
            <ul class="right hide-on-med-and-down">
                <li><a href="/logout">Log out</a></li>
            </ul>

            <ul id="nav-mobile" class="side-nav">
                <li><a href="#">Navbar Link</a></li>
            </ul>
            <a href="#" data-activates="nav-mobile" class="button-collapse"><i class="material-icons">menu</i></a>
        </div>
    </nav>

    <div class="section no-pad-bot" id="index-banner">
        <div class="container">
            <h5 id="tutor_title" class="center" style="margin-top:75px;">Hello ${user}, on page "/"</h5>
            <#if user == "Dima"><p class="center">Freemarker templating engine</p></#if>
        </div>
    </div>

    <script type="text/javascript" src="jquery-2.1.1.min.js"></script>
    <script src="materialize.min.js"></script>
</body>
</html>