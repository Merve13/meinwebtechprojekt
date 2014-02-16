package controllers;

import java.util.Map;

import models.User;
import models.ValidUser;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class loginmfg extends Controller {

	public static Result index() {

		return ok(views.html.log.render(""));

	}

	public static Result reg() {

		return ok(views.html.registrierung.reg.render("Registrierung", ""));

	}

	public static Result loggen() {

		String email = "";
		String user = "";
		String fahrertyp = "";

		Map<String, String[]> parameters = request().body().asFormUrlEncoded();
	
		if (parameters == null && !parameters.containsKey("email")
				&& !parameters.containsKey("password")) {
		

			Logger.warn("bad login request");
			index();

		} else {

			email = parameters.get("email")[0];
			String password = parameters.get("password")[0];

			if (email.isEmpty() || password.isEmpty()) {
				return ok(views.html.log.render("Felder sind leer"));
			}

			try {

				MongoClient mongoClient = new MongoClient("localhost", 27017);
				DB db = mongoClient.getDB("play_basics");
				DBCollection coll = db.getCollection("user");

				// sucht in der Datenbank
				com.mongodb.DBCursor cursor = coll.find();
				BasicDBObject query = new BasicDBObject("email", email);
				cursor = coll.find(query);

				String sucheEmail = "";


				for (DBObject s : cursor) {

					sucheEmail = s.get("email").toString();
					String passwordPruefen = s.get("password").toString();
					user = s.get("username").toString();

					if (!password.equals(passwordPruefen)) {
						return ok(views.html.log.render("Password ungültig!"));
					}

				}

				if (sucheEmail.isEmpty()) {
					// Nach User suchen, email auch als user oben definiert
					query = new BasicDBObject("username", email);
					cursor = coll.find(query);

					for (DBObject i : cursor) {

						if (i.get("username").toString().isEmpty()) {
							mongoClient.close();
							return ok(views.html.log
									.render("Anmeldedaten sind ungültig!"));

						} else {
							String passwordPruefen = i.get("password")
									.toString();

							if (!password.equals(passwordPruefen)) {
								return ok(views.html.log
										.render("Password ungültig!"));
							}

						}

					}
				}

				mongoClient.close();

			} catch (UnknownHostException e) {
				e.printStackTrace();
			}

			// login wird in die DB übertragen!

			try {

				MongoClient mongoClient = new MongoClient("localhost", 27017);
				DB db = mongoClient.getDB("play_basics");
				DBCollection coll = db.getCollection("mfglogin");

				BasicDBObject doc = new BasicDBObject("email", email).append(
						"password", password);

				coll.insert(doc);
				mongoClient.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}

		}
		// Auch User möglich.
		session("connected", email);

		return ok(views.html.fahrerodermitfahrer.render(user,
				"Herzlich Willkommen"));
	}

	public static Result logout() {

		session().clear();
		return ok(views.html.log.render("Tschüs, bis zur nächsten Fahrt!"));
	}
}
