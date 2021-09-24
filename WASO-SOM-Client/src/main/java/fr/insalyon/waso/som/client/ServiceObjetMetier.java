package fr.insalyon.waso.som.client;

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

    public void getListeClient() throws ServiceException {
        try {
            JsonArray jsonListe = new JsonArray();

            List<Object[]> listeClients = this.dBConnection.launchQuery("SELECT ClientID, TypeClient, Denomination, Adresse, Ville FROM CLIENT ORDER BY ClientID");

            for (Object[] row : listeClients) {
                JsonObject jsonItem = new JsonObject();

                Integer clientId = (Integer) row[0];
                jsonItem.addProperty("id", clientId);
                jsonItem.addProperty("type", (String) row[1]);
                jsonItem.addProperty("denomination", (String) row[2]);
                jsonItem.addProperty("adresse", (String) row[3]);
                jsonItem.addProperty("ville", (String) row[4]);

                List<Object[]> listePersonnes = this.dBConnection.launchQuery("SELECT ClientID, PersonneID FROM COMPOSER WHERE ClientID = ? ORDER BY ClientID,PersonneID", clientId);
                JsonArray jsonSousListe = new JsonArray();
                for (Object[] innerRow : listePersonnes) {
                    jsonSousListe.add((Integer) innerRow[1]);
                }

                jsonItem.add("personnes-ID", jsonSousListe);

                jsonListe.add(jsonItem);
            }

            this.container.add("clients", jsonListe);

        } catch (DBException ex) {
            throw JsonServletHelper.ServiceObjectMetierExecutionException("Client","getListeClient", ex);
        }
    }

    public void rechercherClientParDenomination (String denomination, String ville) throws ServiceException {
        try {
            JsonArray jsonListe = new JsonArray();

            String request = "SELECT ClientID, TypeClient, Denomination, Adresse, Ville FROM " +
                    "CLIENT WHERE Denomination=? AND Ville=? ORDER BY ClientID";
            PreparedStatement statement = this.dBConnection.buildPrepareStatement(request);
            statement.setString(1, denomination);
            statement.setString(2, ville);

            ResultSet listeClients = statement.executeQuery();

            while (listeClients.next()) {
                JsonObject jsonItem = new JsonObject();

                Integer clientId = (Integer) listeClients.getInt("ClientID");
                jsonItem.addProperty("id", clientId);
                jsonItem.addProperty("type", listeClients.getString("TypeClient"));
                jsonItem.addProperty("denomination", listeClients.getString("Denomination"));
                jsonItem.addProperty("adresse", listeClients.getString("Adresse"));
                jsonItem.addProperty("ville", listeClients.getString("Ville"));

                List<Object[]> listePersonnes = this.dBConnection.launchQuery("SELECT ClientID, PersonneID FROM COMPOSER WHERE ClientID = ? ORDER BY ClientID,PersonneID", clientId);
                JsonArray jsonSousListe = new JsonArray();
                for (Object[] innerRow : listePersonnes) {
                    jsonSousListe.add((Integer) innerRow[1]);
                }

                jsonItem.add("personnes-ID", jsonSousListe);

                jsonListe.add(jsonItem);
            }

            this.container.add("clients", jsonListe);

        } catch (DBException ex) {
            throw JsonServletHelper.ServiceObjectMetierExecutionException("Client","getListeClient", ex);
        } catch (SQLException exception) {
            System.out.println("Exception in get client by denomination and city");
            exception.printStackTrace();
        }
    }

    public void rechercherClientParNumero(Integer numeroClient) throws ServiceException {
        try {
            JsonArray jsonListe = new JsonArray();

            String request = "SELECT ClientID, TypeClient, Denomination, Adresse, Ville FROM " +
                    "CLIENT WHERE ClientID=?";
            PreparedStatement statement = this.dBConnection.buildPrepareStatement(request);
            statement.setInt(1, numeroClient);

            ResultSet listeClients = statement.executeQuery();

            while (listeClients.next()) {
                JsonObject jsonItem = new JsonObject();

                Integer clientId = (Integer) listeClients.getInt("ClientID");
                jsonItem.addProperty("id", clientId);
                jsonItem.addProperty("type", listeClients.getString("TypeClient"));
                jsonItem.addProperty("denomination", listeClients.getString("Denomination"));
                jsonItem.addProperty("adresse", listeClients.getString("Adresse"));
                jsonItem.addProperty("ville", listeClients.getString("Ville"));

                List<Object[]> listePersonnes = this.dBConnection.launchQuery("SELECT ClientID, PersonneID FROM COMPOSER WHERE ClientID = ? ORDER BY ClientID,PersonneID", clientId);
                JsonArray jsonSousListe = new JsonArray();
                for (Object[] innerRow : listePersonnes) {
                    jsonSousListe.add((Integer) innerRow[1]);
                }

                jsonItem.add("personnes-ID", jsonSousListe);

                jsonListe.add(jsonItem);
            }

            this.container.add("clients", jsonListe);

        } catch (DBException ex) {
            throw JsonServletHelper.ServiceObjectMetierExecutionException("Client","getListeClient", ex);
        } catch (SQLException exception) {
            System.out.println("Exception in get client by denomination and city");
            exception.printStackTrace();
        }
    }

    public void rechercherClientParPersonne(Integer personId, String ville) throws ServiceException {
        try {
            JsonArray jsonListe = new JsonArray();

            String request = "SELECT clientId FROM " +
                    "COMPOSER WHERE personneId=?";
            PreparedStatement statement = this.dBConnection.buildPrepareStatement(request);
            statement.setInt(1, personId);

            ResultSet persons = statement.executeQuery();

            while (persons.next()) {
                int clientId = persons.getInt("clientId");
                System.out.println("Cllient : " + clientId);
                request = "SELECT ClientID, TypeClient, Denomination, Adresse, Ville FROM " +
                        "CLIENT WHERE ClientID=? AND Ville = ?";
                statement = this.dBConnection.buildPrepareStatement(request);
                statement.setInt(1, clientId);
                statement.setString(2, ville);
                ResultSet listeClients = statement.executeQuery();
                while (listeClients.next()) {
                    JsonObject jsonItem = new JsonObject();

                    clientId = (Integer) listeClients.getInt("ClientID") ;
                    System.out.println("on a trouvé un client : " + clientId);
                    jsonItem.addProperty("id", clientId);
                    jsonItem.addProperty("type", listeClients.getString("TypeClient"));
                    jsonItem.addProperty("denomination", listeClients.getString("Denomination"));
                    jsonItem.addProperty("adresse", listeClients.getString("Adresse"));
                    jsonItem.addProperty("ville", listeClients.getString("Ville"));

                    List<Object[]> listePersonnes = this.dBConnection.launchQuery("SELECT ClientID, PersonneID FROM COMPOSER WHERE ClientID = ? ORDER BY ClientID,PersonneID", clientId);
                    JsonArray jsonSousListe = new JsonArray();
                    for (Object[] innerRow : listePersonnes) {
                        jsonSousListe.add((Integer) innerRow[1]);
                    }

                    jsonItem.add("personnes-ID", jsonSousListe);

                    jsonListe.add(jsonItem);
                }
            }
            this.container.add("clients", jsonListe);

        } catch (DBException ex) {
            throw JsonServletHelper.ServiceObjectMetierExecutionException("Client","getListeClient", ex);
        } catch (SQLException exception) {
            System.out.println("Exception in get client by denomination and city");
            exception.printStackTrace();
        }
    }

}
