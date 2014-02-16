package controllers;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.List;

//import models.Orte;
//import models.Zaehler;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class Registrierung extends Controller {

	// Hier wird mit registrieren nur das Registrierungsformular aufgerufen

	public static Result registrieren() {

		return ok(views.html.registrierung.reg.render("Registrierung", ""));
	}

	@SuppressWarnings("deprecation")
	public static Result abschicken() {

		Map<String, String[]> parameters = request().body().asFormUrlEncoded();

		// Parameteruebergaben werden ueberprueft
		if (!(parameters != null && parameters.containsKey("UsernameEingabe")
				&& parameters.containsKey("EmailEingabe")
				&& parameters.containsKey("PWEingabe")
				&& parameters.containsKey("PWWDHEingabe")
				&& parameters.containsKey("VornameEingabe")
				&& parameters.containsKey("NameEingabe")
				&& parameters.containsKey("Tag")
				&& parameters.containsKey("Monat")
				&& parameters.containsKey("Jahr") && parameters
					.containsKey("TelEingabe"))) {

			Logger.warn("bad login request");
			return redirect("/assets/html/registrierung.html");
		}

		// Parameterwerte werden ausgelesen
		String username = parameters.get("UsernameEingabe")[0];
		String email = parameters.get("EmailEingabe")[0];
		String password = parameters.get("PWEingabe")[0];
		String passwordValidierung = parameters.get("PWWDHEingabe")[0];

		// Ueberpruefung ob das Password korrekt wiederholt eingegeben wurde
		if (!password.equals(passwordValidierung)) {
			return ok(views.html.registrierung.reg.render("Registrierung",
					"Sie haben das Password nicht korrekt eingegeben."));

		}

		// Die Anrede wird ermittelt
		String[] anrede = parameters.get("optionsRadios");
		String titel;

		if (anrede[0].equals("Herr")) {
			titel = "Herr";

		} else {
			titel = "Frau";
		}

		// Parameterwerte werden ausgelesen
		String tag = parameters.get("Tag")[0];
		String monat = parameters.get("Monat")[0];
		String jahr = parameters.get("Jahr")[0];
		String geburtsdatum = tag + "." + monat + "." + jahr;

		String vorname = parameters.get("VornameEingabe")[0];
		String name = parameters.get("NameEingabe")[0];
		String telefon = parameters.get("TelEingabe")[0];

		String alter = "";
		int wiealt;

		// Das Alter wird zunaechst ermittelt
		GregorianCalendar today = new GregorianCalendar();
		GregorianCalendar past = new GregorianCalendar(Integer.parseInt(jahr),
				Integer.parseInt(monat) - 1, Integer.parseInt(tag));

		long difference = today.getTimeInMillis() - past.getTimeInMillis();
		int days = (int) (difference / (1000 * 60 * 60 * 24));
		double years = (double) days / 365;

		StringBuilder sb = new StringBuilder();
		sb.append("");
		sb.append(years);
		alter = sb.toString();

		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		String registrierungsdatum = df.format(today.getTime());

		// Bestimmung des Alters als ganze Zahl
		wiealt = (int) Math.floor(Double.parseDouble(alter));

		// Daten werden an die Datenbank uebertragen

		try {
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("play_basics");
			DBCollection coll = db.getCollection("user");

			// Uberpruefung der Eindeutigkeit von Email und Username
			com.mongodb.DBCursor cursor = coll.find();
			BasicDBObject query = new BasicDBObject("username", username);
			cursor = coll.find(query);

			String sucheUsername = "";

			try {
				while (cursor.hasNext()) {
					sucheUsername += cursor.next();
				}
			} finally {
				cursor.close();
			}

			// Sicherstellung dass der Username eindeutig nur einmal in der
			// Datenbank vorkommt
			if (!sucheUsername.isEmpty()) {
				mongoClient.close();
				return ok(views.html.registrierung.reg
						.render("Registrierung",
								"Der Username ist schon vergeben. Versuchen Sie bitte einen anderen Namen."));
			}

			query = new BasicDBObject("email", email);
			cursor = coll.find(query);

			String sucheEmail = "";

			try {
				while (cursor.hasNext()) {
					sucheEmail += cursor.next();
				}
			} finally {
				cursor.close();
			}

			// Email darf nur einmal in der Datenbank vorkommen
			if (!sucheEmail.isEmpty()) {
				mongoClient.close();
				return ok(views.html.registrierung.reg
						.render("Registrierung",
								"Diese Email Adresse wird schon bereits verwendet. Verwenden Sie bitte eine andere Email Adresse."));
			}

			BasicDBObject doc = new BasicDBObject("username", username)
					.append("email", email).append("password", password)
					.append("titel", titel)
					.append("geburtsdatum", geburtsdatum)
					.append("wiealt", wiealt).append("vorname", vorname)
					.append("name", name).append("tel", telefon)
					.append("registrierungsdatum", registrierungsdatum);
			coll.insert(doc);
			mongoClient.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		String fahrertyp = "";
		String user = "";
		String start = "";
		String ziel = "";

		return redirect("/startseite/");

	}
}
