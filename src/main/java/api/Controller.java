package api;

import database.Manager;
import database.Pictures;
import database.Utils;
import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {
    Manager man;
    public Controller(){
        this.man = new Manager();
    }

    @GetMapping("/api/verifyUser")
    public String verifyUser(@RequestParam String key, @RequestParam String username){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        if(this.man.verifyUser(username))
            return Utils.createResult("successful", "User found");
        return  Utils.createResult("error", "User not found");
    }
    @GetMapping("/api/login")
    public String login(@RequestParam String key, @RequestParam String username,@RequestParam String password){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.login(username,password);
    }

    @GetMapping("/api/register")
    public String register(@RequestParam String key,@RequestParam String username, @RequestParam String password, @RequestParam String name,@RequestParam String email, @RequestParam String type, @RequestParam String phoneNumber,String codeManager){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.register(username,password,name,email,type,phoneNumber,codeManager);
    }

    @GetMapping("/api/createShoppingCartID")
    public String createShoppingCartID(@RequestParam String key,@RequestParam String username){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.createShoppingCartID(username);
    }

    @GetMapping("/api/getPicture")
    public String getPicture(@RequestParam String key,@RequestParam String pictureID){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return Pictures.GetPicture(pictureID);
    }

    @GetMapping("/api/uploadPicture")
    public String uploadPicture(@RequestParam String key,@RequestParam String pictureID, @RequestParam String blob){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return Pictures.UploadPicture(blob, pictureID);
    }

    @GetMapping("/api/search")
    public String search(@RequestParam String key,@RequestParam String name){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.search(name);
    }

    @GetMapping("/api/getProducts")
    public String getProducts(@RequestParam String key){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.getProducts();
    }

    @GetMapping("/api/addToCart")
    public String addToCart(@RequestParam String key,@RequestParam String cartID, @RequestParam String productID, @RequestParam String amount){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.addToCart(cartID, productID, amount);
    }

    @GetMapping("/api/getUser")
    public String getUser(@RequestParam String key,@RequestParam String username){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.getUser(username);
    }

    @GetMapping("/api/modifyUser")
    public String modifyUser(@RequestParam String key,@RequestParam String id,@RequestParam String username, @RequestParam String name,@RequestParam String email, @RequestParam String phoneNumber){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.modifyUser(id, username, name, email, phoneNumber);
    }

    @GetMapping("/api/addProduct")
    public String addProduct(@RequestParam String key,@RequestParam String name,@RequestParam String type,@RequestParam String size,@RequestParam String price,@RequestParam String stock,@RequestParam String description){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.addProduct(name, type, size, price, stock, description);
    }

}