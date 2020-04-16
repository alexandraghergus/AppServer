package api;

import database.Manager;
import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {
    Manager man;
    public Controller(){
        this.man = new Manager();
    }

    @GetMapping("/api/verifyuser")
    public String verifyUser(@RequestParam String username){
        if(this.man.verifyUser(username))
        return "Found!!";
        return  "Not found!";
    }
    @GetMapping("/api/login")
    public String login(@RequestParam String username,@RequestParam String password){
        return this.man.login(username,password);
    }
    @GetMapping("/api/register")
    public String register(@RequestParam String username, @RequestParam String password, @RequestParam String name,@RequestParam String email, @RequestParam String type, @RequestParam String phoneNumber,String codeManager){
        return this.man.register(username,password,name,email,type,phoneNumber,codeManager);
    }
}