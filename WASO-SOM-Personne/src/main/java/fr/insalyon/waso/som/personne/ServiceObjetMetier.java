package fr.insalyon.waso.som.personne;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.insalyon.waso.util.DBConnection;
import fr.insalyon.waso.util.JsonServletHelper;
import fr.insalyon.waso.util.exception.DBException;
import fr.insalyon.waso.util.exception.ServiceException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author WASO Team
 */
public class ServiceObjetMetier {

    protected DBConnection dBConnection;
    protected JsonObject container;

    public ServiceObjetMetier(DBConnection dBConnection, JsonObject container) {
        this.dBConnection = dBConnection;
        this.container = container;
    }
    
    public void release() {
        this.dBConnection.close();
    }

    public void getListePersonne() throws ServiceException {
        try {
            List<Object[]> listePersonne = this.dBConnection.launchQuery("SELECT PersonneID, Nom, Prenom, Mail FROM PERSONNE ORDER BY PersonneID");

            JsonArray jsonListe = new JsonArray();

            for (Object[] row : listePersonne) {
                JsonObject jsonItem = new JsonObject();

                jsonItem.addProperty("id", (Integer) row[0]);
                jsonItem.addProperty("nom", (String) row[1]);
                jsonItem.addProperty("prenom", (String) row[2]);
                jsonItem.addProperty("mail", (String) row[3]);

                jsonListe.add(jsonItem);
            }

            this.container.add("personnes", jsonListe);

        } catch (DBException ex) {
            throw JsonServletHelper.ServiceObjectMetierExecutionException("Personne", "getListePersonne", ex);
        }
    }

    void getPersonneParId(Integer idPersonne) throws ServiceException  {
       try {
            List<Object[]> listePersonne = this.dBConnection.launchQuery("SELECT PersonneID, Nom, Prenom, Mail FROM PERSONNE WHERE PersonneID ="+ idPersonne +"ORDER BY PersonneID");

            JsonArray jsonListe = new JsonArray();

            for (Object[] row : listePersonne) {
                JsonObject jsonItem = new JsonObject();

                jsonItem.addProperty("id", (Integer) row[0]);
                jsonItem.addProperty("nom", (String) row[1]);
                jsonItem.addProperty("prenom", (String) row[2]);
                jsonItem.addProperty("mail", (String) row[3]);

                jsonListe.add(jsonItem);
            }

            this.container.add("personnes", jsonListe);

        } catch (DBException ex) {
            throw JsonServletHelper.ServiceObjectMetierExecutionException("Personne", "getPersonneParId", ex);
        }
    }

    void rechercherPersonneParNom(String nomPersonneParametre)throws ServiceException  {
       try {
           // faire une requete avec LIKE pour ne pas avoir la casse !
            PreparedStatement  statement =this.dBConnection.buildPrepareStatement("SELECT PersonneID, Nom, Prenom, Mail FROM PERSONNE WHERE Nom =? ORDER BY PersonneID");
            statement.setString(1, nomPersonneParametre);
            ResultSet rs = statement.executeQuery();
            JsonArray jsonListe = new JsonArray();
            while (rs.next()) {
                JsonObject jsonItem = new JsonObject();
                
                jsonItem.addProperty("id", rs.getInt("PersonneID"));
                jsonItem.addProperty("nom", rs.getString("Nom"));
                jsonItem.addProperty("prenom", rs.getString("Prenom"));
                jsonItem.addProperty("mail", rs.getString("Mail"));

                jsonListe.add(jsonItem);
            }
         
            this.container.add("personnes", jsonListe);

        } catch (DBException ex) {
            throw JsonServletHelper.ServiceObjectMetierExecutionException("Personne", "rechercherPersonneParNom", ex);
        } catch (SQLException ex) {
            Logger.getLogger(ServiceObjetMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
