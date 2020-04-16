package database;

import org.javatuples.Pair;
import java.math.BigInteger;
import java.sql.*;
import java.util.Random;

public class Manager {

    Connection c;
    Statement stmt;
    boolean connected = false;
    public Manager(){
        this.connect();
    }

    public void connect(){
        // Trying for 5 times to connect. If it can't, it breaks
        int retry = 5;
        while (! connected && retry > 0)
        {
            try{
                Class.forName("org.postgresql.Driver");
                this.c = DriverManager
                        .getConnection("jdbc:postgresql://localhost:5432/ClothesOrderingApp",
                                Utils.DatabaseUser, Utils.DatabasePassword);
                this.c.setAutoCommit(false);
                this.stmt = this.c.createStatement();
                this.connected = true;
            } catch (Exception e){
                retry-=1;
            }
        }
    }

    public BigInteger generateID(String table, String field) {
        Random rand = new Random();
        BigInteger result = new BigInteger(24, rand);
        try{
            String sql = String.format("SELECT \"%s\" FROM \"%s\" WHERE \"%s\"=%s;",field, table, result, field);
            ResultSet rs = this.stmt.executeQuery( sql );
            if(rs.next())
                return generateID(table, field);
        }catch(Exception ignored){

        }
        return result;
    }

    public boolean verifyUser(String username){
        while(!this.connected){ // make sure that we're always connected to the database

            this.connect();
        }
        try{
            String sql = "SELECT \"Id\" FROM \"Users\" WHERE \"Username\"='"+username+"';";
            ResultSet rs = this.stmt.executeQuery( sql );
            return rs.next();
        }
        catch (Exception e){ return false;}
    }
    public String login(String username,String pass){
        while(!this.connected){
            this.connect();
        }
        if(!verifyUser(username)){
            return Utils.createResult("error", "User not found");
        }
        try{
            String sql= "SELECT * FROM \"Users\" WHERE \"Username\"='"+username+"';";
            ResultSet rs = stmt.executeQuery( sql );
            Pair<String, String> p = Utils.userConvertToJSON(rs);
            if(pass.equals(p.getValue1())){
                return p.getValue0();
            }
            return Utils.createResult("error", "Incorrect password");
        }
        catch (Exception e){
            return Utils.createResult("error", "Database error");
        }
    }
    public String register(String username,String password, String name,String email, String type,String phoneNumber,String codeManager){
        while(!this.connected){
            this.connect();
        }
        if(this.verifyUser(username)){
            return Utils.createResult("error", "User already exists.");
        }
        if(!codeManager.equals("") && !verifyManagerCode(codeManager))
            return Utils.createResult("error", "Invalid Manager Code.");
        try{
            String sql= String.format("INSERT INTO \"Users\"(\n" +
                    "\t\"Id\", \"Name\", \"Code Manager\", \"Type\", \"Email\", \"PhoneNumber\", \"Username\", \"Password\")\n" +
                    "\tVALUES (%s, '%s', '%s', '%s', '%s', '%s', '%s', '%s');",this.generateID("Users", "Id").toString(),name,codeManager,type,email,phoneNumber,username,password);
            stmt.executeUpdate( sql );
            c.commit();
        }
        catch (Exception e){
            return Utils.createResult("error", "Database error");
        }
        return Utils.createResult("successful", "User registered.");
    }
    public boolean verifyManagerCode(String code){
        try{
            String sql = "SELECT \"Id\" FROM \"ManagerCodes\" WHERE \"code\"='"+code+"';";
            ResultSet rs = this.stmt.executeQuery( sql );
            return rs.next();
        }catch(Exception e){
            return false;
        }

    }
    public BigInteger getUsernameID(String username){
        while(!this.connected){
            this.connect();
        }
        try{
            String sql = String.format("SELECT \"Id\" FROM \"Users\" WHERE \"Username\"='%s'", username);
            ResultSet rs = stmt.executeQuery( sql );
            rs.next();
            return BigInteger.valueOf(rs.getInt("Id"));
        }
        catch (Exception ignored){

        }
        return BigInteger.valueOf(0);
    }
    public String createShoppingCartID(String username){

        while(!this.connected){
            this.connect();
        }
        try{
            String id=generateID("ShoppingBasket", "Id").toString();
            String sql = String.format("UPDATE \"Users\"\n" +
                    "\tSET \"ShoppingCartId\"=%s\n" +
                    "\tWHERE \"Username\"='%s';", id,username);
            stmt.executeUpdate(sql);
            c.commit();
            sql = String.format("INSERT INTO \"ShoppingBasket\"(\"Id\", \"UserId\") VALUES(%s, %s)", id, getUsernameID(username).toString());
            stmt.executeUpdate(sql);
            c.commit();
            return Utils.createResult("successful", "Created ShoppingCartID");
        }
        catch (Exception e){
            return Utils.createResult("error", "Unable to update and create ShoppingCartID");
        }
    }
    public String search(String name){
        while(!this.connected){
            this.connect();
        }
        try{
            String sql = String.format("SELECT * FROM \"Products\" WHERE \"Name\" LIKE %%%s%%", name);
            ResultSet rs = stmt.executeQuery(sql);
            return Utils.convertToJSON(rs);
        }catch(Exception e){
            return Utils.createResult("error", "Malformed Query");
        }
    }
    public String getProducts(){
        while(!this.connected){
            this.connect();
        }
        try{
            String sql = "SELECT * FROM \"PRODUCTS\";";
            ResultSet rs = stmt.executeQuery(sql);
            return Utils.convertToJSON(rs);
        } catch (Exception e) {
            return Utils.createResult("error", "Malformed Query");
        }
    }
    public String addProduct(String name, String type, String size, String price, String stock, String description){
        while(!this.connected){
            this.connect();
        }
        try{
            String id = generateID("Product", "Id").toString();
            String sql = String.format("INSERT INTO public.\"Product\"(\n" +
                    "\t\"Id\", \"Name\", \"Type\", \"Size\", \"Price\", \"Stock\", \"Description\")\n" +
                    "\tVALUES (%s, '%s', '%s', '%s', '%s', '%s', '%s');", id, name, type, size, price, stock, description);
            stmt.executeUpdate(sql);
            c.commit();
            return Utils.createResult("successful", String.format("%s", id));
        }catch(Exception e){
            return Utils.createResult("error", "Malformed Query");
        }

    }
    public String addToCart(String cartID,String productID,String amount){
        while(!this.connected){
            this.connect();
        }
        try{
            String sql = String.format("UPDATE \"ShoppingBasket\"\n" +
                    "\tSET \"Cart\"=\"Cart\" || Cast(%s as bigint), \"Ammounts\"= \"Ammounts\" || Cast(%s as bigint)\n" +
                    "\tWHERE \"Id\"=%s;",productID, amount, cartID );
            stmt.executeUpdate(sql);
            c.commit();
            return Utils.createResult("successful", String.format("Added %s to the shoppingcart %s", productID, cartID));
        } catch (Exception e) {
            return Utils.createResult("error", "Malformed Query");
        }
    }
    public String modifyUser(String id, String username, String name,String email,String phoneNumber){
        while(!this.connected){
            this.connect();
        }
        try{
            String sql = String.format("UPDATE \"Users\" SET Name\"='%s', \"Email\"='%s', \"PhoneNumber\"='%s', \"Username\"='%s' WHERE \"Id\"=%s;", name, email, phoneNumber, username, id);
            stmt.executeUpdate(sql);
            c.commit();
            return Utils.createResult("successful", "Updated user");
        }catch(Exception e){
            return Utils.createResult("error", "Malformed Query");
        }
    }
    public String getUser(String username){
        while(!this.connected){
            this.connect();
        }
        if(!verifyUser(username)){
            return Utils.createResult("error", "User not found");
        }
        try{
            String sql= "SELECT * FROM \"Users\" WHERE \"Username\"='"+username+"';";
            ResultSet rs = stmt.executeQuery( sql );
            return Utils.userConvertToJSON(rs).toString();

        }
        catch (Exception e){
            return Utils.createResult("error", "Database error");
        }
    }
}
