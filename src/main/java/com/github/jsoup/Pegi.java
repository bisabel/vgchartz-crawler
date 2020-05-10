package com.github.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.regex.Pattern;
import java.net.URL;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

public class Pegi {

  public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
  public static final String PEGI_URL_SEARCH ="https://pegi.info/search-pegi?q=";

  /*
  https://pegi.info/search-pegi?
  q=&
  op=Search&filter-age%5B%5D=&
  filter-descriptor%5B%5D=&
  filter-publisher=&
  filter-platform%5B%5D=Playstation+4&
  filter-release-year%5B%5D=2020&
  page=1&
  form_build_id=form-thVEBC_3xtTstc7MaWUzQvPiVyUbBGeXxpTOr2iOs5c&
  form_id=pegi_search_form
  */

  public enum Descriptors {
    BADLANGUAGE,//("Bad language", ""),
    DISCRIMINATION,//("Discrimination",""),
    DRUGS,//("Drugs",""),
    FEAR,//("Fear",""),
    GAMBLING,//("Gambling",""),
    SEX,//("Sex",""),
    VIOLENCE,//("Violence",""),
    INGAMEPURCHASES;//("In game purchases","");

    private static final Descriptors[] POSITION_DESCRIPTORS = {
      Descriptors.BADLANGUAGE,
      Descriptors.DISCRIMINATION,
      Descriptors.DRUGS,
      Descriptors.FEAR,
      Descriptors.GAMBLING,
      Descriptors.SEX,
      Descriptors.VIOLENCE,
      Descriptors.INGAMEPURCHASES
    };

    public static Descriptors get(String url){
      switch (url) {
      case "https://pegi.info/themes/pegi/public-images/violence.png":
        return Descriptors.VIOLENCE;
      case "https://pegi.info/themes/pegi/public-images/bad_language.png":
        return Descriptors.BADLANGUAGE;
      case "https://pegi.info/themes/pegi/public-images/in-game_purchases.png":
        return Descriptors.INGAMEPURCHASES;
      case "https://pegi.info/themes/pegi/public-images/fear.png":
        return Descriptors.FEAR;
      case "https://pegi.info/themes/pegi/public-images/sex.png":
        return Descriptors.SEX;

      case "https://pegi.info/themes/pegi/public-images/gambling.png":
        return Descriptors.GAMBLING;
      case "https://pegi.info/themes/pegi/public-images/discrimination.png":
        return Descriptors.DISCRIMINATION;
      case "https://pegi.info/themes/pegi/public-images/drugs.png":
        return Descriptors.DRUGS;
      default:
        return null;
      }
    }

    public static String print(ArrayList<Descriptors> alist){
      String result = "";
      for(Descriptors buscado: POSITION_DESCRIPTORS){
        //Descriptors buscado = Descriptors.BADLANGUAGE;
        boolean encontrado = false;
        for (Descriptors element : alist) {
          if ( element == buscado)
            encontrado = true;
        }
        result += (encontrado)?"Y|":"N|";
      }
      return result;
    }
  }//end enum

  /**
   * function processPeginGame
   * print in the standard console the videogame info recovery in a Element html
   **/
  public static String processPeginGame(Element article, String platformFilter){
    String title = "";
    int age = 0;
    ArrayList<Descriptors> descriptors = new ArrayList<Descriptors>();
    String content = "";
    String publisher = "";
    String releaseDate = "";
    String platform = "";

    if ( article.hasClass​("game")){
      //get the game description
      Element _description = article.getElementsByClass​("description").first();

      //sibilings are: age-ratting + info + technical-info
      if ( _description.child(0).hasClass("age-rating") ){
        switch (_description.child(0).child(0).attributes().get("src")) {
        case "https://pegi.info/themes/pegi/public-images/pegi/pegi18.png":
          age = 18;
          break;
        case "https://pegi.info/themes/pegi/public-images/pegi/pegi16.png":
          age = 16;
          break;
        case "https://pegi.info/themes/pegi/public-images/pegi/pegi12.png":
          age = 12;
          break;
        case "https://pegi.info/themes/pegi/public-images/pegi/pegi7.png":
          age = 7;
          break;
        case "https://pegi.info/themes/pegi/public-images/pegi/pegi3.png":
          age = 3;
          break;
        }
      } else
        System.out.println("Different element get with class "+_description.child(0).html() );

      //info
      if ( _description.child(1).hasClass("info") ){
        if ( _description.child(1).getElementsByTag("h3").size() > 0 )
          title = _description.child(1).getElementsByTag("h3").first().html();
        else
          System.out.println("no TITLE");

        if ( _description.child(1).getElementsByClass("publisher").size() > 0 )
          publisher = _description.child(1).getElementsByClass("publisher").first().html();
        else
          System.out.println("no publisher");

        if ( _description.child(1).getElementsByClass("content-info").size() > 0 )
          content = _description.child(1).getElementsByClass("content-info").first().html();
        else
          System.out.println("no content-info");

      } else
        System.out.println("no info");

      //technical-info
      if ( _description.child(2).hasClass("technical-info") ){
        if ( _description.child(2).child(0).hasClass("platform") ){
          platform = _description.child(2).child(0).text().replace("System:","");
        } else
          System.out.println(_description.child(2).html());

        if ( _description.child(2).child(1).hasClass("release-date") ){
          releaseDate = _description.child(2).child(1).text().replace("Release Date:","");
          //releaseDate = _description.child(2).child(1).text();
        } else
          System.out.println(_description.child(2).html());
      }
      else
        System.out.println("no technical-info");

      //get the content descriptors
      Element _descriptors = article.getElementsByClass​("descriptors").first();
      Elements _imgs = _descriptors.getElementsByTag("img");

      for(Element _img : _imgs) {
        descriptors.add(Descriptors.get(_img.attributes().get("src")));
      }
      return platformFilter+"|"+title +"|"+age+"|"+releaseDate+"|"+Descriptors.print(descriptors)+content+"|"+publisher+"|"+platform+"\n";

    }//end if ( article.hasClass​("game"))
    else {
      System.out.println(">Not a game: " + article.html() );
      return null;
    }
  }


  public static void main(String[] args) throws IOException{
    String platformFilter = "Nintendo DS";
    FileWriter pw = new FileWriter("pegi-Nintendo-DS-2.csv", true);
    try {
      pw.append("platform filter|title|age|releaseDate|BADLANGUAGE|DISCRIMINATION|DRUGS|FEAR|GAMBLING|SEX|VIOLENCE|INGAMEPURCHASES|content|publisher|platform list\n");
    } catch (IOException e) {
      System.out.println("exception occoured when file append" + e);
      System.exit(0);
    }
    String url = PEGI_URL_SEARCH+"";
    String url_parameters = "&filter-platform[]=Nintendo+DS&op=Search&form_build_id=form-PdHQzZNgwUeUYLDw3ZSA4ngoRQZ-Wc7iNXoRa3sFkvQ&form_id=pegi_search_form";
    int page = 1;
    boolean found = true;
    while (found){
      found = false;
      //filter-platform[]=Xbox+One
      url = PEGI_URL_SEARCH+url_parameters+"&page="+page;
      try {
        Document _doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(0).get();
        //Document _doc = Jsoup.parse(new URL(url).openStream(), "ISO-8859-1", url);
        Element _body = _doc.body();
        System.out.println("url>"+url);
        Element _result = _body.getElementById("results");
        Elements _articles = _result.getElementsByTag("article");
        for(Element article : _articles) {
          pw.append(processPeginGame(article,platformFilter));
          found = true;
        }
      } catch (IOException e) {
        System.out.println("exception occoured inside while: " + e);
      }
      try {
        TimeUnit.SECONDS.sleep(2);
      } catch (InterruptedException ie) {
        System.out.println("exception occoured inside while: " + ie);
      }
      page++;
      pw.flush();
    }
    pw.close();
  }

}
