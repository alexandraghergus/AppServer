package database;

import org.javatuples.Pair;
import java.math.BigInteger;
import java.sql.*;
import java.util.Random;

public class Manager {

    Connection c;
    Statement stmt;
    boolean connected = false;
    Utils utils = new Utils();
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
                                "", "");
                this.c.setAutoCommit(false);
                this.stmt = this.c.createStatement();
                this.connected = true;
            } catch (Exception e){
                retry-=1;
            }
        }
    }

    public boolean verifyUser(String usernm){
        while(!this.connected){ // make sure that we're always connected to the database

            this.connect();
        }
        try{
            String sql = "SELECT \"Id\" FROM \"Users\" WHERE \"Username\"='"+usernm+"';";
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
            return utils.createResult("error", "User not found");
        }
        try{
            String sql= "SELECT * FROM \"Users\" WHERE \"Username\"='"+username+"';";
            ResultSet rs = stmt.executeQuery( sql );
            Pair<String, String> p = utils.userConvertToJSON(rs);
            if(pass.equals(p.getValue1())){
                return p.getValue0();
            }
            return utils.createResult("error", "Incorrect password");
        }
        catch (Exception e){
            return utils.createResult("error", "Database error");
        }
    }


    public BigInteger generateID() {
        Random rand = new Random();
        BigInteger result = new BigInteger(24, rand);
        try{
            String sql = "SELECT \"Id\" FROM \"Users\" WHERE \"Id\"="+result+";";
            ResultSet rs = this.stmt.executeQuery( sql );
            if(rs.next())
                return generateID();
        }catch(Exception e){
            return generateID();
        }
        return result;
    }
    public String register(String username,String password, String name,String email, String type,String phoneNumber,String codeManager){
        while(!this.connected){
            this.connect();
        }
        if(this.verifyUser(username)){
            return utils.createResult("error", "User already exists.");
        }
        if(!codeManager.equals("") && !verifyManagerCode(codeManager))
            return utils.createResult("error", "Invalid Manager Code.");
        try{
            String sql= String.format("INSERT INTO \"Users\"(\n" +
                    "\t\"Id\", \"Name\", \"Code Manager\", \"Type\", \"Email\", \"PhoneNumber\", \"Username\", \"Password\")\n" +
                    "\tVALUES (%s, '%s', '%s', '%s', '%s', '%s', '%s', '%s');",this.generateID().toString(),name,codeManager,type,email,phoneNumber,username,password);
            stmt.executeUpdate( sql );
            c.commit();
        }
        catch (Exception e){
            return utils.createResult("error", "Database error");
        }
        return utils.createResult("successful", "User registered.");
    }

    public boolean verifyManagerCode(String code){
        try{
            String sql = "SELECT \"Id\" FROM \"ManagerCodes\" WHERE \"code\"='"+code+"';";
            ResultSet rs = this.stmt.executeQuery( sql );
            if(rs.next())
                return true;
            return false;
        }catch(Exception e){
            return false;
        }

    }
    public BigInteger getUsernameID(String username){
        while(!this.connected){
            this.connect();
        }
        try{
            String sql = "SELECT id FROM \"Users\" WHERE username='{" + username +"}'";
            ResultSet rs = stmt.executeQuery( sql );
            rs.next();
            return BigInteger.valueOf(rs.getInt("Id"));
        }
        catch (Exception e){

        }
        return BigInteger.valueOf(0);
    }

    public void createShoppingCartID(String username){

        while(!this.connected){
            this.connect();
        }
        try{
            String id=generateID().toString();
            String sql = String.format("UPDATE \"Users\"\n" +
                    "\tSET \"ShoppingCartId\"=%s\n" +
                    "\tWHERE \"Username\"='%s';", id,username);
            stmt.executeUpdate(sql);
            c.commit();

        }
        catch (Exception e){

        }
    }
    public void search(){
    }
    public void getProduct(){
    }
    public void addToCart(int cartID,int produsID,int ammount){
    }
    public void modifyUser(String firsName,String lastName,String phoneNumber,String email){
    }
    public void getUser(String usernm){
    }
}
