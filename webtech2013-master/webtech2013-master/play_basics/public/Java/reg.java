package controllers;

import java.net.UnknownHostException;
import java.util.GregorianCalndar;
import java.util.Map;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;


public class Reg extends Controller {
    //ich gibr hier die view zurucük in meinem fall muss ich hier die html seite zurück geben
    public static Result reg(){
        
        return ok(views.html.registrierung.reg.render("Registrierung","");  // in die zweite "" komme informationnen man kann da auch ein text mitgeben
		
        
    }
    
    public static Result abschicken(){
        
        Map<String, String[]> parameters = request().body().asFormUrlEncoded();
        
    }
    
    //Pramaeterübergabe überprüfen
    
    if(!(parameters != null
        
        &&parameters.containsKey("InputUsername");
        &&parameters.containsKey("InputEmail");
        &&parameters.containsKey("InputPw");
        &&parameters.containsKey("InputPwWDH");
        &&parameters.containsKey("InputName");
        &&parameters.containsKey("InputVorname");
        &&parameters.containsKey("Tag");
        &&parameters.containsKey("Monat");
        &&parameters.containsKey("Jahr");
        &&parameters.containsKey("Telefon"))){
            
            Logger.warn("bad login request");"
            return redirect("/assest/html/reg.html");
        }
        
        
        //parameterwerte werden ausgelese
        
        String username = parameters.get("InputUsername")[0];
        String email = parameters.get("InputEmail")[0];
        String pw = parameters.get("InputPw")[0];
        String pwwdh = parameters.get("InputPwWDH")[0];
        
        
        if(!pw.equals(pwwdh)){
            
            return ok(views.html.reg.reg.html("Registrierung", "Sie haben das Password nicht korrekt eingegeben!"));
             
        }
        
        //Die Anrede wird ermittelt
        
        String[] anrede = parameters.get("Radio");
        String titel;
        
        if(anrede[0].equals("Herr")){
            titel ="Herr";
            
        }else{
            titel ="Frau";
        }
        
        //Parameterwerde werden ausgelesen
        
        
        String tag = parameters.get("Tag")[0];
        String monat = parameters.get("Monat")[0];
        String jahr = parameters.get("Jahr")[0];
        String geburstag = tag + "." + monat + "." + jahr;
        
        String vorname = parameters.get("InputVorname")[0];
        String name = parameters.get("InputName")[0];
        String tel = parameters.get("InputTelefon")[0];
        
        String alter = "";
        int alt;
        
        //Das alter wird ermittelt
 /*       
        GregorianCalender today = new GregorianCalender();
        GregorianCalender past = new GregorianCalender(Integer.parseInt(jahr),
        Integer.ParseInt(monat) -1, Integer.parseInt(tag));
        
        long unterschied = today.getTimeInMillis() - past.getTimeInMillis();
        int days = (int) (unterschied / (100 * 60 * 60 * 24));
        double years = (double) days / 365;
        
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(years);
        alter = sb.toString();
        
        //bestimmen des Alters als ganze Zahl 
         alt = (int) Math.floor(Double.parseDouble(alter));
         
  */
  
    //daten werden in die db ubertragen
    
    try {
        MongoClient mongoClient = new MongoClient ("localhost", 27017);
        DB db = mongoClient.getDB("play_basics");
        DBCollection coll = db.getCollection("user");
        
        //uberprufen der einduetigkeit von email und username
        
        com.mongodb.DBCursor cursor = coll.find();
        BasicDBObject query = new BasicDBObject("username", username);
        cursor = coll.find(query);
        
        String sucheUsername= "";
        
        try {
            while (cursor.hasNext()){ //geh die ganzen zeilen durch
            sucheUsername += cursor.next();
            }
            
            
        }finally { 
            cursor.close();
            
    }
        
        //Sicherstellung dasss der Username eindeutig nur einmal in der 
        //Datenbank vorkommt
        
        if(!sucheUsername.isEmpty()){
            mongoClient.close();
            return ok (views.html.reg.reg.html("Registrierung","Der Username ist schon vergeben"));
            }
        
        query = new BasicDBObject("email", email);
        cursor = coll.find(query);
        
        String sucheEmail ="";
        
        try{
            while (cursor.hasNext()) {
    				sucheEmail += cursor.next();
				}
			} finally {
				cursor.close();
			}
        
    //emaik darf nur einaml in der db vorkommen
    
    // Email darf nur einmal in der Datenbank vorkommen
    		if (!sucheEmail.isEmpty()) {
				mongoClient.close();
				return ok(views.html.reg.reg.html("Registrierung","Diese Email Adresse wird schon bereits verwendet. Verwenden Sie bitte eine andere Email Adresse."));
			}
            
          
        //schreibe in die DB
        
        Basic.DBObject doc = new BasicDBObject ("username", username);
            .append("email", email).append("pw", pw).append("titel", titel)
            .append("geburtstag", geburtstag).append("alt", alt).append("vorname", vorname)
            .append("name", name).append("tel", telefon);
            
            coll.insert(doc);
            mongoClient.close();
            catch(UnknownHostException e){
                e.printStackTrace();
            }
            
            return ok(views.html.anwendung.anwednung.render(ProTramp Mitfahrgelegenheit", "", "", ""));
        
    }
}

        
        
        
        
        