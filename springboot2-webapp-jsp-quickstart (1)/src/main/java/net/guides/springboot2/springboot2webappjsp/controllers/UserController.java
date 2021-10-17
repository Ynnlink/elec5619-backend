package net.guides.springboot2.springboot2webappjsp.controllers;
import net.guides.springboot2.springboot2webappjsp.configuration.JwtUtil;
import net.guides.springboot2.springboot2webappjsp.domain.Artifact;
import net.guides.springboot2.springboot2webappjsp.repositories.ArtifactRepository;
import org.springframework.util.DigestUtils;
import net.guides.springboot2.springboot2webappjsp.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import net.guides.springboot2.springboot2webappjsp.repositories.UserRepository;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//code 1 : failed
//code 0 : success

@RestController
public class UserController {
	@Autowired
	UserRepository userRepo;
	ArtifactRepository artifactRepository;


	@RequestMapping(value = "searchName", method = RequestMethod.POST)
	@CrossOrigin
	public Result searchByName(@RequestParam String name) {
		Result result = new Result();
		List<User> users = userRepo.findByUserName(name);
		List<User> res = new ArrayList<>();
//		get creator user
		for (User s:users) {
			System.out.println("userName: " + s.getUsername());
			if (s.getIsCreator().equals("creator")) {
				res.add(s);
			}
		}
		List<List<Artifact>> res1 = new ArrayList<>();
		for (User s : res) {
			List<Map<String,Object>> art = artifactRepository.findByUserId(s.getId()) ;
			System.out.println(art);
//			res1.add(art);
		}

		HashMap<User, List<Artifact>> go = new HashMap<>();
		for (int i = 0; i < res1.size();i++) {
			go.put(res.get(i), res1.get(i));
		}

		result.setCode(0);
		result.setMsg("OK");
		result.setData(go);

		return result;
	}



	@RequestMapping(value = "register", method = RequestMethod.POST)
	@CrossOrigin
	public Result register(@RequestBody User user) {
		Result result = new Result();

		if (user.getEmail()== null || user.getPassword() == null) {
			result.setMsg("email_address and password cannot be null");
			result.setCode(1);
			return result;
		}

		User existUser = userRepo.getUserByEmail(user.getEmail());
		if (existUser != null) {
			result.setMsg("This user has already register");
			result.setCode(1);
			return result;
		}

		boolean flag =false;
		String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = null;
		matcher = regex.matcher(user.getEmail());
		flag = matcher.matches();
		if (!flag) {
			result.setMsg("email format does not correct");
			result.setCode(1);
			return result;
		}


		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String format = sdf.format(new Date());
		User newUser = new User();
		newUser.setUsername(user.getUsername());
		newUser.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
		newUser.setEmail(user.getEmail());
		newUser.setRegisterTime(format);
		newUser.setIsCreator("user");
		newUser.setIsAdmin("user");
		newUser.setProfilePicStore("/api/profilePicture/default.png");
		float b = 0.0f;
		newUser.setAccountBalance(b);

		userRepo.save(newUser);

		result.setMsg("register successful");
		result.setCode(0);
		return result;






	}

	//	login page
	@RequestMapping(value = "login", method = RequestMethod.POST)
	@CrossOrigin
	public Result login( @RequestBody User user) throws UnsupportedEncodingException {
		Result result = new Result();
//		normal login

		if ( user.getEmail() == null || user.getPassword() == null) {
			result.setMsg("Username and email_address and password cannot be null");
			return result;
		}

		User existUser = userRepo.getUserByEmail(user.getEmail());
		if (existUser == null) {
			result.setMsg("You have not registered, please sign in");
			result.setCode(1);
			return result;
		}

		if (!existUser.getPassword().equals(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()))) {
			result.setMsg("Wrong password, please try it again");
			result.setCode(1);
			return result;
		}

		HashMap<String,String> list = new HashMap<>();
		list.put("username",URLEncoder.encode(existUser.getUsername(),"UTF-8"));
		list.put("role",existUser.getIsCreator());

//		give token to client side
		String token = JwtUtil.sign(user.getEmail(),existUser.getPassword());
		if (token!= null) {
			result.setCode(0);
			result.setMsg("login successfully!");
			result.setData(list);
			result.setToken(token);
			return result;
		}

		result.setCode(1);
		result.setMsg("token failed!");

		return result;
	}




	//User log out
	@RequestMapping(value = "userlogout",  method = RequestMethod.POST)
	@CrossOrigin
	public Result logout() {
		Result result = new Result();

		result.setMsg("logout successfully! ");
		result.setCode(0);
		return result;


	}




}
