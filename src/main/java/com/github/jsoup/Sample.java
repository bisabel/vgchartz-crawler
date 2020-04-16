package com.github.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.regex.Pattern;
import java.net.URL;

import java.io.FileWriter;
import java.util.concurrent.TimeUnit;

public class Sample {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";

    public static void main(String[] args)  throws Exception{
    // 7/xbox-360/
      platformPage("7/xbox-360/","data-xbox360.csv");
      platformPage("68/xbox-one/","data-xbox-one.csv");      
      platformPage("69/playstation-4/","data-ps4.csv");
      platformPage("3/playstation-3/","data-ps3.csv");
      platformPage("1/nintendo-ds/","data-nintendo-ds.csv");
      platformPage("2/wii/","data-nintendo-wii.csv");
      platformPage("47/wii-u/","data-nintendo-wii-u.csv");
      platformPage("83/nintendo-switch/","data-nintendo-switch.csv");
    }

    public static void platformPage(String platform, String file) throws Exception{
      FileWriter pw = new FileWriter(file, true);
      pw.append("Pos, Game, Year, Genre, Publisher, North America, Europe, Japan, Rest of World, Global");

      String url = "https://www.vgchartz.com/platform/"+platform;
      Document _doc = Jsoup.parse(new URL(url).openStream(), "ISO-8859-1", url);
      Element _body = _doc.body();
      Element _table = _body.getElementsByTag("tbody").first();
      Elements _tr = _table.children();
      for(Element game : _tr) {
        Elements _td = game.children();
        String name = "NA";
        String link = "NA";
        String release = "NA";
        String genre = "NA";
        String publisher = "NA";
        String northAmerica = "NA";
        String europe = "NA";
        String japan = "NA";
        String rest = "NA";
        String total = "NA";
        int count = 0;
        for(Element info : _td) {
          if ( count == 1){
            if ( info.childrenSize() > 0 ){
              Element a = info.child(0);
              link = a.attributes().get("href");
            }
            name = info.text();
          } else if ( count == 2)
            release = info.text();
          else if ( count == 3)
            genre = info.text();
          else if ( count == 4)
            publisher = info.text();
          else if ( count == 5)
            northAmerica = info.text();
          else if ( count == 6)
            europe = info.text();
          else if ( count == 7)
            japan = info.text();
          else if ( count == 8)
            rest = info.text();
          else if ( count == 9)
            total = info.text();
          count++;
        }
        /*System.out.print(name+", "+link+", "+release+", "+genre+", "+publisher+", "+northAmerica+", "+europe+", "+japan+", "+rest+", "+total);
        TimeUnit.SECONDS.sleep(20);
        if ( link.startsWith("https://www.vgchartz.com/game"))
          gamePage(link);
        else
          System.out.println("");
        */
        pw.append(name+", "+link+", "+release+", "+genre+", "+publisher+", "+northAmerica+", "+europe+", "+japan+", "+rest+", "+total);
        pw.append("\n");
      }
      System.out.println("size "+_tr.size());
      //String gameurl = "https://www.vgchartz.com/game/180525/fifa-18";
      //String gameurl = "https://www.vgchartz.com/game/120133/marvels-spider-man";
      //String gameurl = "https://www.vgchartz.com/game/83196/grand-theft-auto-v";
      /*
      try {
        Sample.gamePage(gameurl);
      } catch (Exception e){
        System.out.println( "Exception catch: "+gameurl );
      }
      */
      pw.flush();
      pw.close();
    }

    public static void gamePage(String url) {
      try {
        Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(0).get();
        //Document doc = Jsoup.parse(new URL(url).openStream(), "ISO-8859-1", url);
        //Document doc = Jsoup.connect().userAgent("Mozilla").data("name", "jsoup").get();
        Element _body = doc.body();
        //System.out.println( "Analyce: "+url );
        if ( _body.hasText() ){
          Element _gameGenInfoBox = _body.getElementById("gameGenInfoBox");
          if ( _gameGenInfoBox != null && _gameGenInfoBox.hasText() ) {
            Element _center = _gameGenInfoBox.getElementsByTag("center").first();
            String esrb = "NA";
            String cero = "NA";
            String pegi = "NA";
            String genre = "NA";
            //System.out.println("Hello TEST pegi value: " + _center.html() );
            if ( _center != null && _center.hasText() ){
              Elements _src = _center.getElementsByAttribute("src");
              for(Element img : _src) {
                switch ( img.attributes().get("src") ) {
                case "https://www.vgchartz.com/games/images/ratings/esrb/ESRB_ao.png":
                  esrb = "ao";
                  break;
                case "https://www.vgchartz.com/games/images/ratings/esrb/ESRB_e.png":
                  esrb = "e";
                  break;
                case "https://www.vgchartz.com/games/images/ratings/esrb/ESRB_e10.png":
                  esrb = "e10";
                  break;
                case "https://www.vgchartz.com/games/images/ratings/esrb/ESRB_ec.png":
                  esrb = "ec";
                  break;
                case "https://www.vgchartz.com/games/images/ratings/esrb/ESRB_ka.png":
                  esrb = "ka";
                  break;
                case "https://www.vgchartz.com/games/images/ratings/esrb/ESRB_m.png":
                  esrb = "m";
                  break;
                case "https://www.vgchartz.com/games/images/ratings/esrb/ESRB_rp.png":
                  esrb = "rp";
                  break;
                case "https://www.vgchartz.com/games/images/ratings/esrb/ESRB_t.png":
                  esrb = "t";
                  break;

                case "https://www.vgchartz.com/games/images/ratings/cero/CERO_a.png":
                  cero = "a";
                  break;
                case "https://www.vgchartz.com/games/images/ratings/cero/CERO_b.png":
                  cero = "b";
                  break;
                case "https://www.vgchartz.com/games/images/ratings/cero/CERO_c.png":
                  cero = "c";
                  break;
                case "https://www.vgchartz.com/games/images/ratings/cero/CERO_d.png":
                  cero = "d";
                  break;
                case "https://www.vgchartz.com/games/images/ratings/cero/CERO_z.png":
                  cero = "z";
                  break;

                case "https://www.vgchartz.com/games/images/ratings/pegi/PEGI_ok.png":
                  pegi = "ok";
                  break;
                case "https://www.vgchartz.com/games/images/ratings/pegi/PEGI_3.png":
                  pegi = "3";
                  break;
                case "https://www.vgchartz.com/games/images/ratings/pegi/PEGI_7.png":
                  pegi = "7";
                  break;
                case "https://www.vgchartz.com/games/images/ratings/pegi/PEGI_12.png":
                  pegi = "12";
                  break;
                case "https://www.vgchartz.com/games/images/ratings/pegi/PEGI_16.png":
                  pegi = "16";
                  break;
                case "https://www.vgchartz.com/games/images/ratings/pegi/PEGI_18.png":
                  pegi = "18";
                  break;
              }
              }//end for
              Element _genreh2 = _gameGenInfoBox.getElementsContainingOwnText​("Genre").first();
              if ( _genreh2 != null ) {
                //System.out.println("GENRE: " + _genreh2.html() );
                genre = _genreh2.nextElementSibling().html();
              }
              System.out.println(", " + esrb +",  "+ cero +", "+ pegi +", GENRE: " + genre +";");
          } else
              System.out.print("ELSE has NO SRC: " + _center.html() );

          } else
            System.out.print("ELSE has NO gameGenInfoBox ");
        }//end if ( _body.hasText() )

      } catch (Exception e){
        System.out.println(e);
        System.exit(1);
      }
    }
}
