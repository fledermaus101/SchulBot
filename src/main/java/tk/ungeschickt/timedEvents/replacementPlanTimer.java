package tk.ungeschickt.timedEvents;

import org.jetbrains.annotations.Nullable;
import tk.ungeschickt.main.Info;
import tk.ungeschickt.main.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class replacementPlanTimer extends TimerTask {

    private final Info info;
    private List<HttpCookie> cookies;

    public replacementPlanTimer(Info info) {
        this.info = info;
    }

    @Override
    public void run() {

    }
    /* TODO:
        Do timer
        Arbeitsplan + Message

     */
    @Nullable
    public String getCookie() throws IOException {
        CookieManager manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
        URL url = new URL("http://www.friedrichgymnasium-altenburg.de/interne-bereiche");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:78.0) Gecko/20100101 Firefox/78.0");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,/;q=0.8");
        connection.setRequestProperty("Accept-Language", "de,en-US;q=0.7,en;q=0.3");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        connection.setRequestProperty("REFERER", "http://www.friedrichgymnasium-altenburg.de/interne-bereiche");
        connection.connect();

        connection.getContent();
        CookieStore cookieJar =  manager.getCookieStore();
        List<HttpCookie> cookies = cookieJar.getCookies();
        for (HttpCookie httpCookie: cookies) if (info.isDebug()) System.out.println("CookieHandler retrieved cookie: " + httpCookie);
        this.cookies = cookies;

        final int responseCode = connection.getResponseCode();
        final String responseMessage = connection.getResponseMessage();
        if (info.isDebug()) {
            if (responseCode != 0)
                System.out.println("Debug: getCookie() - responseCode: " + responseCode);
            if (responseMessage != null && !responseMessage.equals(""))
                System.out.println("Debug: getCookie() - responseMessage: " + responseMessage);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while (true) {
            String input = in.readLine();
            if (input == null) {
                break;
            }
            // Searching for the form where the cookie lies
            Pattern pattern = Pattern.compile("<input type=\"hidden\" name=\"([a-z]|[0-9]){32}\" value=\"1\" />", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                if (info.isDebug())
                    System.out.println("found: " + matcher.group());
                Pattern pattern2 = Pattern.compile("([a-z]|[0-9]){32}", Pattern.CASE_INSENSITIVE);
                Matcher matcher2 = pattern2.matcher(matcher.group());
                if (matcher2.find()) {
                    // Getting the value idk for what it is used - Current guess a session cookie
                    // But we need it to authenticate
                    if (info.isDebug())
                        System.out.println("Got token: " + matcher2.group());
                    return matcher2.group();
                }
                break;
            }
        }

        return null;
    }

    private void authenticate(String cookie) throws IOException, URISyntaxException {
        CookieManager manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
        URL url = new URL("http://www.friedrichgymnasium-altenburg.de/interne-bereiche?task=user.login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:78.0) Gecko/20100101 Firefox/78.0");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,/;q=0.8");
        connection.setRequestProperty("Accept-Language", "de,en-US;q=0.7,en;q=0.3");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        connection.setRequestProperty("REFERER", "http://www.friedrichgymnasium-altenburg.de/interne-bereiche");
        CookieStore cookieJar = manager.getCookieStore();
        for (HttpCookie httpCookie : cookies) cookieJar.add(url.toURI(), httpCookie);
        connection.setDoOutput(true);
        connection.connect();

        String body = "username=" + info.getWebsiteUsername() + "&password=" + info.getWebsitePassword() + "&return=aHR0cDovL3d3dy5mcmllZHJpY2hneW1uYXNpdW0tYWx0ZW5idXJnLmRlL2JlcmVpY2gtc2NodWVsZXItaW50ZXJuL2JlbnV0emVya29udG8tYWVuZGVybi9wcm9maWxl&" + cookie + "=1";
        byte[] outputInBytes = body.getBytes(StandardCharsets.UTF_8);
        OutputStream os = connection.getOutputStream();
        os.write(outputInBytes);
        os.close();

        connection.getContent();
        List<HttpCookie> cookies = cookieJar.getCookies();
        for (HttpCookie httpCookie: cookies) if (info.isDebug()) System.out.println("CookieHandler retrieved cookie: " + httpCookie);
        final int responseCode = connection.getResponseCode();
        final String responseMessage = connection.getResponseMessage();
        if (info.isDebug()) {
            if (responseCode != 0)
                System.out.println("Debug: authenticate() - responseCode: " + responseCode);
            if (responseMessage != null && !responseMessage.equals(""))
                System.out.println("Debug: authenticate() - responseMessage: " + responseMessage);
        }

        this.cookies = cookies;
    }

    private void getPDF() throws IOException, URISyntaxException {
        URL url = new URL("http://www.friedrichgymnasium-altenburg.de/bereich-schueler-intern/aktueller-vertretungsplan");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:78.0) Gecko/20100101 Firefox/78.0");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,/;q=0.8");
        connection.setRequestProperty("Accept-Language", "de,en-US;q=0.7,en;q=0.3");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        connection.setRequestProperty("REFERER", "http://www.friedrichgymnasium-altenburg.de/interne-bereiche");

        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);
        CookieStore cookieJar = manager.getCookieStore();
        for (HttpCookie httpCookie : cookies) cookieJar.add(url.toURI(), httpCookie);

        connection.connect();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        Calendar cal = Calendar.getInstance();
        String day = new SimpleDateFormat("EEEE", Locale.GERMAN).format(cal.getTime());
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String date = dateFormat.format(cal.getTime());
        StringBuilder site = new StringBuilder();
        while (true) {
            String input = in.readLine();
            if (input == null) {
                break;
            }
            site.append(input);
            Pattern pattern = Pattern.compile("<a class=\"\" href=\"/bereich-schueler-intern/aktueller-vertretungsplan\\?download=\\d{4}:" + day + "-" + date + "\">" + day + ", " + date + "</a>", Pattern.MULTILINE);
            //                                      <a class="" href="/bereich-schueler-intern/aktueller-vertretungsplan?download=3100:montag-14-12-2020">Montag, 14. 12. 2020</a>
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                if (info.isDebug())
                    System.out.println(matcher.group());
                break;
            }
        }
        final int responseCode = connection.getResponseCode();
        final String responseMessage = connection.getResponseMessage();
        if (info.isDebug()) {
            if (!site.toString().equals("<!DOCTYPE HTML><html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"de-de\" lang=\"de-de\" dir=\"ltr\"><head>  <base href=\"http://www.friedrichgymnasium-altenburg.de/interne-bereiche\" />  <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />  <meta name=\"keywords\" content=\"Friedrichgymnasium, Altenburg, Gymnasium, Bildungseinrichtung, Schule, Leistung, Freude, Altenburger Land\" />  <meta name=\"rights\" content=\"Staatliches Friedrichgymnasium Altenburg\" />  <meta name=\"description\" content=\"Das Staatliche Friedrichgymnaium Altenburg ist eine Bildungseinrichtung in der Skatstadt, welche eine fast 500 jährige Geschichte aufweist.\" />  <meta name=\"Konzeption / Realisation\" content=\"Florian Voos\" />  <meta name=\"Template-Design\" content=\"ah-68; Florian Voos\" />  <title>Staatliches Friedrichgymnasium Altenburg - Interne Bereiche</title>  <link href=\"http://www.friedrichgymnasium-altenburg.de/interne-bereiche\" rel=\"canonical\" />  <link href=\"/templates/fgymabg-standard/favicon.ico\" rel=\"shortcut icon\" type=\"image/vnd.microsoft.icon\" />  <script src=\"/media/system/js/mootools-core.js\" type=\"text/javascript\"></script>  <script src=\"/media/system/js/core.js\" type=\"text/javascript\"></script>  <script src=\"/media/jui/js/jquery.min.js\" type=\"text/javascript\"></script>  <script src=\"/media/jui/js/jquery-noconflict.js\" type=\"text/javascript\"></script>  <script src=\"/media/jui/js/jquery-migrate.min.js\" type=\"text/javascript\"></script>  <script src=\"/media/system/js/html5fallback.js\" type=\"text/javascript\"></script>  <script src=\"/media/jui/js/bootstrap.min.js\" type=\"text/javascript\"></script>  <script type=\"text/javascript\">function keepAlive() {\tvar myAjax = new Request({method: \"get\", url: \"index.php\"}).send();} window.addEvent(\"domready\", function(){ keepAlive.periodical(840000); });jQuery(document).ready(function()\t\t\t\t{\t\t\t\t\tjQuery('.hasTooltip').tooltip({\"html\": true,\"container\": \"body\"});\t\t\t\t});  </script><style type=\"text/css\">/*--------------------------------------------------------------------------------# ah-68-Flexi 3.1 - September 2013 (J3.1)# Copyright (C) 2006-2013 www.ah-68.de All Rights Reserved.----------------------------------------------------------------------------------*/body {    font-family: Arial, Helvetica, sans-serif !important;\tfont-size: 14px !important;\tline-height: 20px !important;    color: #5C5C5C;\tbackground-color: #EFEFEF;\t\t\t\tbackground-image: url(/templates/fgymabg-standard/images/bg/-1);\t\t\tbackground-repeat: repeat;}\th1 {    color: #0086C4 !important;}h2 {    color: #0086C4 !important;}h3 {    color: #0086C4 !important;}h4, h5, h6 {    color: #0199E0 !important;}a, a:link, a:visited, a:active, a:focus {   color: #0199E0;}a:hover {   color: #00AEFF;}.highlight {\tcolor : #0199E0;}.invalid {\tborder-color: #00AEFF !important;}label.invalid {\tcolor : #00AEFF;}.item-separator {\tborder-color: BBBBBB !important;\tborder-bottom: 1px;\tborder-bottom-style: solid\t}.top-line {\tbackground-color: #0086C4;\theight: 2px;}.bottom-line {\tbackground-color: #0086C4;\theight: 2px;}.gotop {\tcolor: #0199E0;}.gotop:hover {\tcolor: #00AEFF}.logo {\theight: 120px;\twidth: 480px;}ul.menusf-menu li a, .menusf-menu li .separator  {\tfont-size: 18px;\tfont-weight: bold;\tcolor: #0199E0;\tborder-color: #EFEFEF !important;}ul.menusf-menu li a:hover {\tcolor : #0199E0;\tborder-color: #00AEFF !important;}ul.menusf-menu li.active > a {\tcolor : #0199E0;\tborder-color: #EFEFEF !important;}ul.menusf-menu ul li a {    color: #FAFAFA;\tbackground-color: #0199E0;  \tfont-size: 13px;\tfont-weight: normal;}ul.menusf-menu ul li a:hover {    color: #FFFFFF;\tbackground-color: #0199E0;}ul.menusf-menu ul li.active > a {    color: #FFFFFF;\tbackground-color: #0199E0;}ul.menusf-vmenu li a  {    color: #FAFAFA;\tbackground-color: #0199E0;\tfont-size: 14px;\tfont-weight: normal;}ul.menusf-vmenu li a:hover {    color: #FFFFFF;\tbackground-color: #00AEFF;}ul.menusf-vmenu li.active > a {\tcolor : #FFFFFF;\tbackground-color: #00AEFF;}ul.menusf-vmenu ul li a  {    color: #FAFAFA;\tbackground-color: #0199E0;\tfont-size: 13px;\tfont-weight: normal;}ul.menusf-vmenu ul li a:hover {    color: #FFFFFF;\tbackground-color: #00AEFF;}ul.menusf-vmenu ul li.active > a {\tcolor : #FFFFFF;\tbackground-color: #00AEFF;}.inputbox, input, textarea {\tcolor: #5C5C5C;\tbackground-color: #FAFAFA;}.inputbox:hover, input:hover, textarea:hover {\tcolor: #6C6C6C;\tbackground-color: #FFFFFF;}.cat-list-row0 {\tbackground-color : #F5F5F5;\tborder-bottom-color: #CCCCCC !important;}.cat-list-row1 {\tbackground-color : #FAFAFA;\tborder-bottom-color: #DDDDDD !important;}td.hits {\tborder-left-color: #DDDDDD !important;}.button, .validate, button, input.button, button.button, button.validate, .dropdown-menu > li > a:hover,.dropdown-menu > li > a:focus, .dropdown-submenu:hover > a, .dropdown-submenu:focus > a, .label-info,.badge-info, .label-info[href], .badge-info[href], .tagspopular li a, .tagssimilar li a {\tcolor: #FAFAFA;\tbackground-color: #0199E0;}.button:hover, .validate:hover, button:hover, input.button:hover, button.button:hover, button.validate:hover, .dropdown-menu > .active > a,.dropdown-menu > .active > a:hover, .dropdown-menu > .active > a:focus, .label-info:hover[href], .badge-info:hover[href], .tagspopular li a:hover, .tagssimilar li a:hover {\tcolor: #FFFFFF;\tbackground-color: #00AEFF;}.readmore {    color: #FAFAFA;\tbackground-color: #0199E0;}.readmore:hover {\t    color: #FFFFFF;\tbackground-color: #00AEFF;}.readmore a, a.readmore {\tcolor: #FAFAFA;}.readmore a:hover, a:hover.readmore {    color: #FFFFFF;}div.moduletable h3, div.moduletable_menu h3, div.moduletablenew h3, div.moduletablehot h3 {    color:  #0086C4;\tbackground-color: #FFFFFF;}div.moduletable, div.moduletable_menu, div.moduletablenew, div.moduletablehot {    color: #5C5C5C;\tbackground-color: #FFFFFF;}</style><link href=\"/templates/fgymabg-standard/css/template.css.php\" rel=\"stylesheet\" type=\"text/css\" media=\"all\" /><!-- Enabling HTML5 support for Internet Explorer --><!--[if lt IE 9]>          <script src=\"http://html5shiv.googlecode.com/svn/trunk/html5.js\"></script><![endif]--><script type=\"text/javascript\" src=\"/templates/fgymabg-standard/js/jquery.custom.php\"></script></head><body><div id=\"sidebar\">  <div class=\"sidebar\">    <div class=\"custom\"  >\t<ul><li><a class=\"home-hover\" title=\"Startseite\" href=\"/index.php\"> </a></li><li><a class=\"facebook-hover\" title=\"Facebook\" href=\"https://www.facebook.com/pages/Friedrichgymnasium-Altenburg/110122902343594\" target=\"_blank\"> </a></li><li><a class=\"google-hover\" title=\"Google Plus\" href=\"https://plus.google.com/114914013349863914537/about\" target=\"_blank\"> </a></li><li><a class=\"feed-hover\" title=\"Feed\" href=\"/interne-bereiche?format=feed&amp;type=rss\"> </a></li><li><a class=\"login-hover\" title=\"Login\" href=\"/index.php/interne-bereiche\"> </a></li></ul></div>  </div></div><!-- Start Top Line --><div class=\"top-line\"> <!-- Start Wrapper --><div id=\"wrapper\">  <!-- Start Wrap -->  <div id=\"wrap\">    <!-- Start Header -->    <div id=\"header\">      <div class=\"header-float\">        <!-- Start Topnav -->        <div id=\"topnav\">          <div class=\"topnav\">            <ul class=\"nav menunav menu-topnav\"><li class=\"item-407\"><a href=\"/\" >Startseite</a></li><li class=\"item-408\"><a href=\"/kontakt\" >Kontakt</a></li><li class=\"item-409\"><a href=\"/impresum\" >Impressum</a></li><li class=\"item-552\"><a href=\"/datenschutz\" >Datenschutz</a></li></ul>          </div>        </div>        <!-- Start Topnav -->\t\t<div style=\"height: 50px; \"></div>        <!-- Start Nav -->        <div id=\"topmenu\">                    <!-- Start Superfish -->          <div id=\"superfish-frame\">            <ul class=\"nav menusf-menu\"><li class=\"item-392\"><a href=\"/aktuelles\" >Aktuelles</a></li><li class=\"item-393\"><a href=\"/aktivitaeten-projekte\" >Aktivitäten / Projekte</a></li><li class=\"item-128\"><a href=\"/die-schule\" >Die Schule</a></li><li class=\"item-411 current active\"><a href=\"/interne-bereiche\" >Interne Bereiche</a></li><li class=\"item-412\"><a href=\"/interaktiv\" >Interaktiv</a></li><li class=\"item-410\"><a href=\"/lernen-unterricht\" >Lernen / Unterricht</a></li></ul>          </div>          <!-- Ende Superfish -->                            </div>        <!-- Ende Nav -->      </div>      <div class=\"header-foot\"></div>    </div>    <!-- Ende Header -->    </div><!-- Ende Top Line -->    <!-- Start Contentframe -->    <div id=\"contentframe\">      <div class=\"content-head\"></div>      <div class=\"content-float\">        <!-- Start Content-Frame -->        <div class=\"content-frame\">                              <!-- Start leftframe -->          <div id=\"leftframe\">            \t\t<div class=\"moduletable\">\t\t\t\t\t<h3>Interne Bereiche</h3>\t\t\t\t\t<ul class=\"nav menusf-vmenu\"><li class=\"item-339\"><a href=\"/bereich-eltern\" >Bereich Eltern</a></li><li class=\"item-262\"><a href=\"/bereich-lehrer-intern\" >Bereich Lehrer</a></li><li class=\"item-263\"><a href=\"/bereich-schueler-intern\" >Bereich Schüler</a></li></ul>\t\t</div>\t            <!-- Ende Leftframe -->          </div>                    <!-- Start Content -->          <div class=\"content_med\">                        <div id=\"content\">              <div id=\"system-message-container\"><div id=\"system-message\"><div class=\"alert alert-message\"><a class=\"close\" data-dismiss=\"alert\">×</a><h4 class=\"alert-heading\">Nachricht</h4><div>\t\t<p>Bitte zuerst anmelden!</p></div></div></div></div>              <div class=\"login \">\t\t\t\t\t\t\t\t<form action=\"/interne-bereiche?task=user.login\" method=\"post\" class=\"form-horizontal\">\t\t<fieldset class=\"well\">\t\t\t\t\t\t\t\t\t\t\t\t<div class=\"control-group\">\t\t\t\t\t\t<div class=\"control-label\">\t\t\t\t\t\t\t<label id=\"username-lbl\" for=\"username\" class=\" required\">Benutzername<span class=\"star\">&#160;*</span></label>\t\t\t\t\t\t</div>\t\t\t\t\t\t<div class=\"controls\">\t\t\t\t\t\t\t<input type=\"text\" name=\"username\" id=\"username\" value=\"\" class=\"validate-username\" size=\"25\" required aria-required=\"true\" />\t\t\t\t\t\t</div>\t\t\t\t\t</div>\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class=\"control-group\">\t\t\t\t\t\t<div class=\"control-label\">\t\t\t\t\t\t\t<label id=\"password-lbl\" for=\"password\" class=\" required\">Passwort<span class=\"star\">&#160;*</span></label>\t\t\t\t\t\t</div>\t\t\t\t\t\t<div class=\"controls\">\t\t\t\t\t\t\t<input type=\"password\" name=\"password\" id=\"password\" value=\"\" class=\"validate-password\" size=\"25\" maxlength=\"99\" required aria-required=\"true\" />\t\t\t\t\t\t</div>\t\t\t\t\t</div>\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div  class=\"control-group\">\t\t\t\t<div class=\"control-label\"><label>Angemeldet bleiben</label></div>\t\t\t\t<div class=\"controls\"><input id=\"remember\" type=\"checkbox\" name=\"remember\" class=\"inputbox\" value=\"yes\"/></div>\t\t\t</div>\t\t\t\t\t\t<div class=\"controls\">\t\t\t\t<button type=\"submit\" class=\"btn btn-primary\">\t\t\t\t\tAnmelden\t\t\t\t</button>\t\t\t</div>\t\t\t<input type=\"hidden\" name=\"return\" value=\"aHR0cDovL3d3dy5mcmllZHJpY2hneW1uYXNpdW0tYWx0ZW5idXJnLmRlL2JlcmVpY2gtc2NodWVsZXItaW50ZXJuL2FrdHVlbGxlci12ZXJ0cmV0dW5nc3BsYW4=\" />\t\t\t<input type=\"hidden\" name=\"2f411a00fe39ecd9f523bb28c8c95ebf\" value=\"1\" />\t\t</fieldset>\t</form></div><div>\t<ul class=\"nav nav-tabs nav-stacked\">\t\t<li>\t\t\t<a href=\"/interne-bereiche?view=reset\">\t\t\tPasswort vergessen?</a>\t\t</li>\t\t<li>\t\t\t<a href=\"/interne-bereiche?view=remind\">\t\t\tBenutzername vergessen?</a>\t\t</li>\t\t\t\t<li>\t\t\t<a href=\"/interne-bereiche?view=registration\">\t\t\t\tRegistrieren!</a>\t\t</li>\t\t\t</ul></div>            </div>                        <!-- Ende Content -->          </div>                    <!-- Ende Content-Frame -->        </div>      </div>      <!-- Ende Contentframe -->    <!-- Start Footer -->      <div class=\"footer-float\">        <div class=\"footer-inside\">          <div id=\"breadcrumb-container\">            <div class=\"breadcrumb-container\">              <ul class=\"breadcrumb\">\t<li class=\"active\"><span class=\"divider icon-location hasTooltip\" title=\"Aktuelle Seite: \"></span></li><li><a href=\"/\" class=\"pathway\">Startseite</a><span class=\"divider\"><img src=\"/templates/fgymabg-standard/images/system/arrow.png\" alt=\"\" /></span></li><li class=\"active\"><span>Interne Bereiche</span></li></ul>            </div>          </div>                    <!-- Start Footer Module -->          <div id=\"footer_container\">                        <div class=\"small-footer-container\">              \t\t<div class=\"moduletable\">\t\t\t\t\t<h3>Aktuelle Neuigkeiten</h3>\t\t\t\t\t<div class=\"custom\"  >\t<p style=\"text-align: justify;\"><a href=\"/index.php/aktuelles\"><img style=\"float: left; margin: 10px;\" src=\"/images/bilder_module/artikel.png\" alt=\"\" width=\"48\" height=\"48\" border=\"0\" /></a>Über 300 News, Berichte oder Ergebnisse warten unter <a href=\"/index.php/aktuelles\">Aktuelles</a>.</p></div>\t\t</div>\t            </div>                                    <div class=\"small-footer-container\">              \t\t<div class=\"moduletable\">\t\t\t\t\t<h3>Bilder, Bilder, Bilder</h3>\t\t\t\t\t<div class=\"custom\"  >\t<p style=\"text-align: justify;\"><a href=\"/index.php/bildergalerien\"><img style=\"float: left; margin: 10px;\" src=\"/images/bilder_module/bilder.png\" alt=\"\" width=\"48\" height=\"48\" border=\"0\" /></a>Über 7500 Bilder in den <a href=\"/index.php/bildergalerien\">Bildergalerien</a> bringen das Schulleben einem näher.</p></div>\t\t</div>\t            </div>                                    <div class=\"small-footer-container\">              \t\t<div class=\"moduletable\">\t\t\t\t\t<h3>Moodle Lernplattform</h3>\t\t\t\t\t<div class=\"custom\"  >\t<p style=\"text-align: justify;\"><img style=\"float: left; margin: 10px;\" src=\"/images/bilder_module/moodle.png\" alt=\"\" width=\"48\" height=\"48\" border=\"0\" />Unterrichtsmaterialien werden zum Download im <a href=\"http://moodle.friedrichgymnasium-altenburg.de\" target=\"_blank\">Moodle</a> bereitgestellt.</p></div>\t\t</div>\t            </div>                      </div>          <!-- Ende Footer Module -->                    <!-- Start Copyright -->          <div id=\"copyright\">            <div class=\"copyright\"> &copy; 2013 - 2015 Alle Rechte vorbehalten: <a href=\"/index.php\" target=\"_self\">Staatliches Friedrichgymnasium Altenburg</a> </div>                     </div>          <!-- Ende Copyright -->        </div>      </div>      <div class=\"footer-foot\"></div>      <!-- Ende Footer -->    </div>    <!-- Ende Wrap -->  </div>  <!-- Ende Wrapper --></div><!-- Start ToTop --><div id=\"toTop\">  <div class=\"gotop\"><span title=\"nach oben\">&uarr;</span></div></div><!-- Ende ToTop --><!-- Start Bottom Line --><div class=\"bottom-line\"> </div><!-- Ende Bottom Line --><script type=\"text/javascript\">( function(ah) {ah('ul.menusf-vmenu').supersubs({\t\tminWidth:    18,\t\tmaxWidth:    28,\t\textraWidth:  0}).superfish();ah('ul.menusf-vmenu').superfish({      delay:       800,        animation: {opacity:'show',height:'show'},        speed:       'fast',\t  easing      : 'swing'}); ah(\"ul.menusf-vmenu\").supposition();<!-- ToTop -->ah(window).scroll(function() {if(jQuery(this).scrollTop() != 0) {ah('#toTop').fadeIn();\t} else {ah('#toTop').fadeOut();}});ah('#toTop').click(function() {ah('body,html').animate({scrollTop:0},800);});ah(\"#sidebar\").hover(function() {ah(this).animate({right:10}, 400);},function() {ah(this).animate({right:-25}, 400);});\tah(\".logo-hover, .home-hover, .facebook-hover, .google-hover, .tweet-hover, .feed-hover, .login-hover\").find(\"span\").hide().end().hover(function() {ah(this).find(\"span\").stop(true, true).fadeIn('slow');}, function() {ah(this).find(\"span\").stop(true, true).fadeOut('slow');});\t} ) ( jQuery ); </script><script type=\"text/javascript\">( function(ah) {ah('ul.menusf-menu').supersubs({\t\tminWidth:    18,\t\tmaxWidth:    28,\t\textraWidth:  0}).superfish();ah('ul.menusf-menu').superfish({      delay:       800,        animation: {opacity:'show',height:'show'},        speed:       'fast',\t  easing      : 'swing'}); ah(\"ul.menusf-menu\").supposition();} ) ( jQuery );</script></body></html>"))
                System.out.println(site);
            else
                System.out.println("Warning could not get pdf. Got redirected to login page.");
            if (responseCode != 0)
                System.out.println("Debug: getPDF() - responseCode: " + responseCode);
            if (responseMessage != null && !responseMessage.equals(""))
                System.out.println("Debug: getPDF() - responseMessage: " + responseMessage);
        }
    }
}
