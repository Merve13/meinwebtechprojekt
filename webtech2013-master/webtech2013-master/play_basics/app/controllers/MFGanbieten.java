package controllers;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.List;

import models.Mfg;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

public class MFGanbieten extends Controller {

	public static Result kontakt() {

		return ok(views.html.kontakt.render());

	}

	public static Result mfgsuchen() {

		return ok(views.html.mfgsuchen.render());

	}

	public static Result verlinken() {

		return ok(views.html.mfganbieten.render());
	}

	public static Result verlinken1() {

		List<Mfg> ausgabe = new ArrayList<Mfg>();

		ausgabe = anzeigenMFG();

		return ok(views.html.startseite.render("", "", ausgabe));
	}

	public static Result verlinkenauffahrerodermitfahrer() {

		String user = "";

		return ok(views.html.fahrerodermitfahrer.render(user, "HALLO"));
	}

	public static Result speichern() {

		Map<String, String[]> parameters1 = request().body().asFormUrlEncoded();
		// Parameteruebergaben werden ueberprueft
		if (parameters1 == null && parameters1.containsKey("start")
				&& parameters1.containsKey("ziel")
				&& parameters1.containsKey("haltestelle")
				&& parameters1.containsKey("Tag")
				&& parameters1.containsKey("Monat")
				&& parameters1.containsKey("Jahre")
				&& parameters1.containsKey("Stunde")
				&& parameters1.containsKey("Minuten")
				&& parameters1.containsKey("Sekunden")
				&& parameters1.containsKey("Plaetze")) {

			Logger.warn("konnte nicht gespeichert werden!");
			return ok(views.html.mfganbieten.render());
		}

		// Parameterwerte werden ausgelesen
		String start = parameters1.get("start")[0];
		String ziel = parameters1.get("ziel")[0];
		String haltestelle = parameters1.get("haltestelle")[0];

		String tag = parameters1.get("Tag")[0];
		String monat = parameters1.get("Monat")[0];
		String jahr = parameters1.get("Jahr")[0];
		String fahrttag = tag + "." + monat + "." + jahr;

		String stunden = parameters1.get("Stunden")[0];
		String minuten = parameters1.get("Minuten")[0];
		String sekunden = parameters1.get("Sekunden")[0];

		String uhrzeit = stunden + ":" + minuten + ":" + sekunden;

		String plaetze = parameters1.get("plaetze")[0];
		String mitfahrer1 = null;
		String mitfahrer2 = null;
		String mitfahrer3 = null;
		String mitfahrer4 = null;

		String MfgId = null;

		// Daten werden an die Datenbank übertragen!
		// in der Db muss die Collection auch angelegt sein!

		try {

			String email = session("connected");
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("play_basics");
			DBCollection coll = db.getCollection("mfganbieten");

			com.mongodb.DBCursor cursor = coll.find();
			Logger.info("Cursor im Anlegen" + cursor.size());
			// Id Erzeugen
			int preId = cursor.size() + 1;
			Integer preState = new Integer(preId);
			MfgId = preState.toString();

			BasicDBObject doc = new BasicDBObject("mfgid", MfgId)
					.append("email", email).append("start", start)
					.append("ziel", ziel).append("haltestelle", haltestelle)
					.append("fahrttag", fahrttag).append("uhrzeit", uhrzeit)
					.append("plaetze", plaetze)
					.append("mitfahrer1", mitfahrer1)
					.append("mitfahrer2", mitfahrer2)
					.append("mitfahrer3", mitfahrer3)
					.append("mitfahrer4", mitfahrer4);

			coll.insert(doc);
			mongoClient.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		List<Mfg> ausgabe = new ArrayList<Mfg>();

		ausgabe = anzeigenMFG();

		return ok(views.html.startseite.render("", "", ausgabe));

	}

	public static Result weiterfahrer() {

		String fahrertyp = "Fahrer";

		try {

			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("play_basics");
			DBCollection coll = db.getCollection("fm");

			BasicDBObject doc = new BasicDBObject("fahrertyp", fahrertyp);

			coll.insert(doc);
			mongoClient.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		String user = "";
		List<Mfg> ausgabe = new ArrayList<Mfg>();
		ausgabe = anzeigenMFG();

		return ok(views.html.startseite.render("Sie sind angemeldet als:",
				fahrertyp, ausgabe));

	}

	public static Result weitermitfahrer() {

		String fahrertyp = "Mitfahrer";
		String user = "";
		String start = "";
		String ziel = "";

		try {

			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("play_basics");
			DBCollection coll = db.getCollection("fm");

			BasicDBObject doc = new BasicDBObject("fahrertyp", fahrertyp);

			coll.insert(doc);
			mongoClient.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		List<Mfg> ausgabe = new ArrayList<Mfg>();

		ausgabe = anzeigenMFG();

		return ok(views.html.startseitemitfahrer2.render(
				"Sie sind angemeldet als :", fahrertyp, ausgabe));

	}

	public static List<Mfg> anzeigenMFG() {

		// LIste zusammestellen

		BasicDBList liste = new BasicDBList();
		List<Mfg> übergabe = new ArrayList<Mfg>();

		try {
			String email = session("connected");
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("play_basics");
			DBCollection coll = db.getCollection("mfganbieten");

			DBObject clause1 = new BasicDBObject("email", email);
			DBObject clause2 = new BasicDBObject("mitfahrer1", email);
			DBObject clause3 = new BasicDBObject("mitfahrer2", email);
			DBObject clause4 = new BasicDBObject("mitfahrer3", email);
			DBObject clause5 = new BasicDBObject("mitfahrer5", email);
			BasicDBList or = new BasicDBList();
			or.add(clause1);
			or.add(clause2);
			or.add(clause3);
			or.add(clause4);
			or.add(clause5);
			DBObject query = new BasicDBObject("$or", or);
			com.mongodb.DBCursor cursor = coll.find(query);

			try {
				// DBObject liste erstellen und füllen
				while (cursor.hasNext()) {
					BasicDBObject dbObj = (BasicDBObject) cursor.next();

					liste.add(dbObj);
					Logger.info("dbObje Meine: " + dbObj.toString());

				}

				// DBOBEJCT liste in mfg liste umwandeln
				for (Object obj : liste) {
					final BasicDBObject mfgASDBObject = (BasicDBObject) obj;
					final Mfg mfg = new Mfg(mfgASDBObject.getString("mfgid"),
							mfgASDBObject.getString("email"),
							mfgASDBObject.getString("start"),
							mfgASDBObject.getString("ziel"),
							mfgASDBObject.getString("haltestelle"),
							mfgASDBObject.getString("fahrttag"),
							mfgASDBObject.getString("uhrzeit"),
							mfgASDBObject.getString("plaetze"));

					übergabe.add(mfg);
					Logger.info("Übergabe liste meine:" + übergabe.toString());

				}

			} finally {

				cursor.close();
				mongoClient.close();

			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return übergabe;

	}

	public static Result anzeigen() {

		// Methode um die Listen zu übergeben.

		List<Mfg> ausgabe = new ArrayList<Mfg>();
		ausgabe = anzeigenMFG();
		String fahrertyp = "";

		return ok(views.html.startseitemitfahrer2.render(
				"Sie sind angemeldet als :", fahrertyp, ausgabe));

	}

}
