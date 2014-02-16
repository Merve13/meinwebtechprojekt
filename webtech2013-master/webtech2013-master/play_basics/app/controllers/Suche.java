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

public class Suche extends Controller {

	public static Result anfrage(String MfgId) {
		MongoClient mongoClientMfg;
		MongoClient mongoClient;

		try {
			// Fahrer herausfinden.
			mongoClientMfg = new MongoClient("localhost", 27017);
			DB dbMfg = mongoClientMfg.getDB("play_basics");
			DBCollection collMfg = dbMfg.getCollection("mfganbieten");
			com.mongodb.DBCursor cursorMfg = collMfg.find();
			BasicDBObject query = new BasicDBObject();
			query.append("mfgid", MfgId);
			cursorMfg = collMfg.find(query);
			String anUser = null;

			if (cursorMfg.hasNext()) {
				anUser = (String) cursorMfg.next().get("email");

			}
			mongoClientMfg.close();
			cursorMfg.close();

			String art = "Anfrage";
			String vonUser = session("connected");
			// Nachricht anlegen
			mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("play_basics");
			DBCollection coll = db.getCollection("nachricht");
			com.mongodb.DBCursor cursor = coll.find();
			int preId = cursor.size() + 1;
			Integer preState = new Integer(preId);
			String nachrichtid = preState.toString();

			BasicDBObject doc = new BasicDBObject("nachrichtid", nachrichtid)
					.append("mfgid", MfgId).append("art", art)
					.append("von", vonUser).append("an", anUser);

			coll.insert(doc);

			mongoClient.close();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<Mfg> übergabe = new ArrayList<Mfg>();
		übergabe = listerstellen();
		String fahrertyp = "";
		return ok(views.html.startseitemitfahrer2.render(
				"Anfrage wurde versendet", fahrertyp, übergabe));
	}

	public static Result list() {
		List<Mfg> übergabe = new ArrayList<Mfg>();
		übergabe = listerstellen();

		String fahrertyp = "";
		return ok(views.html.startseitemitfahrer2.render(
				"Anfrage wurde versendet", fahrertyp, übergabe));
	}

	public static List<Mfg> listerstellen() {

		BasicDBList liste = new BasicDBList();
		List<Mfg> übergabe = new ArrayList<Mfg>();
		try {
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("play_basics");
			DBCollection coll = db.getCollection("mfganbieten");
			com.mongodb.DBCursor cursor = coll.find();
			int zähler = 0;

			try {
				// DBObject liste erstellen und füllen
				while (cursor.hasNext() && zähler < 10) {
					BasicDBObject dbObj = (BasicDBObject) cursor.next();

					liste.add(dbObj);
					Logger.info("dbObje: " + dbObj.toString());
					int groeße = cursor.size();
					Integer bla = new Integer(groeße);
					Logger.info("Cursor:" + bla.toString());
				}
				// DBObject Liste in MfgListe umwandeln
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
				zähler++;

			} finally {
				cursor.close();
				mongoClient.close();
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return übergabe;
	}

}
