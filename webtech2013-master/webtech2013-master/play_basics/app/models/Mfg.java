package models;



public class Mfg extends Entity{
    
    public String mfgid;
    public String email;
    public String start;
	public String ziel;
    public String haltestelle;
	public String fahrttag;
    public String uhrzeit;
    public String plaetze;

	
	public Mfg() {

	}
    
	public Mfg(String mfgid, String email, String start, String ziel, String haltestelle, String fahrttag, String uhrzeit, String plaetze) {
		
        this.mfgid = mfgid;
        this.email = email;
		this.start = start;
		this.ziel = ziel;
        this.haltestelle = haltestelle;
		this.fahrttag = fahrttag;
        this.uhrzeit = uhrzeit;
        this.plaetze = plaetze;
       
	}


    public String getMfgid(){
        return mfgid;
    }
    
    public void setMfgid(String mfgid){
        this.mfgid = mfgid;
    }
    
     public String getEmail(){
        return email;
    }
    
    public void setEmail(String email){
        this.email = email;
    }
    

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getZiel() {
		return ziel;
	}

	public void setZiel(String ziel) {
		this.ziel = ziel;
	}
    
     public String getHaltestelle(){
        return haltestelle;
    }
    
    public void setHaltestelle(String haltestelle){
        this.haltestelle = haltestelle;
    }

	public String getPlaetze() {
		return plaetze;
	}

	public void setPlaetze(String plaetze) {
		this.plaetze = plaetze;
	}



	public String getFahrttag() {
		return fahrttag;
	}

	public void setFahrttag(String fahrttag) {
		this.fahrttag = fahrttag;
	}
    
        public String getUhrzeit() {
		return uhrzeit;
	}

	public void setUhrzeit(String uhrzeit) {
		this.uhrzeit = uhrzeit;
	}

	
	@Override
	public String toString() {
		return super.toString() + "Email:" + email + "Start:" + start + " Ziel:" + ziel +"Haltestelle:"
		+haltestelle + "Fahrttag:" + fahrttag  + " Uhrzeit:" + uhrzeit + " Platze:" + plaetze;
	}

}
