package fr.solutec.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import fr.solutec.entities.User;
import fr.solutec.repository.UserRepository;

@RestController
@CrossOrigin("*")
public class UserRest {
	 @Autowired
	  private PasswordEncoder passwordEncoder;

	  @Autowired
	  private UserRepository userRepository;
	  
	  @PostMapping("registration")
	  public User Creation(@RequestBody User u){
		  u.setPassword(passwordEncoder.encode(u.getPassword()));
		  return userRepository.save(u);
	  }
	  
	  @GetMapping("liste")
	  public Iterable<User> VoirListe(){
		  return userRepository.findAll();
		  }


}
