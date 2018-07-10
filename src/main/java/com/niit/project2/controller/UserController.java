package com.niit.project2.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.niit.project2.dao.JobDao;
import com.niit.project2.dao.UserDao;
import com.niit.project2.model.Job;
import com.niit.project2.model.JobApplication;
import com.niit.project2.model.User;

@RestController
public class UserController {
	@Autowired
	private UserDao userDAO;

	@Autowired
	private User user;

	@Autowired
	private Job job;

	@Autowired
	private JobDao jobDAO;
	
	@Autowired
	HttpSession session;

	// http://localhost:8086/WeiboRestServices/
	@RequestMapping("/")
	public String testServer() {
		// ModelAndView mv = new ModelAndView("Home");
		String name = "TESTING SERVER";
		return name;
	}

	// http://localhost:8086/WeiboRestServices/userList
	@RequestMapping("/userList")
	public ResponseEntity<List<User>> listUser() {
		List<User> users = userDAO.userList();
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}

	// http://localhost:8086/WeiboRestServices/getuser/{loginname}
	@RequestMapping("/getuser/{loginname}")
	public ResponseEntity<User> getUser(@PathVariable String loginname) {
		user = userDAO.getUser(loginname);

		if (user == null) {
			user = new User();
			user.setMessage("no User exists with this loginname");
			return new ResponseEntity<User>(user, HttpStatus.NOT_FOUND);
		} else {
			user.setMessage("User exists with this loginname");
			return new ResponseEntity<User>(user, HttpStatus.OK);

		}
	}



	@PostMapping("/uservalidate")
	public ResponseEntity<User> getUser(@RequestBody User user)

	{
		user = userDAO.validateUser(user.getEmail(), user.getPassword());
		
		
		
		if(user != null) 
		{
			String useremail = user.getEmail();
			String loginname=user.getLoginname();
			session.setAttribute("useremail", useremail);
			session.setAttribute("loginname", loginname);
			user.setMessage("Logged In successfully");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
		else 
		{	
			user = new User();
			user.setMessage("Invalid Credentials, Please try again...");
			return new ResponseEntity<User>(user, HttpStatus.UNAUTHORIZED);
			
		}
	}

	//  http://localhost:8086/WeiboRestServices/registerUser
		@PostMapping("/registerUser")
		public ResponseEntity<User> registerUser(@RequestBody User user)
		{
			/*if(userDAO.getUser(user.getLoginname()) != null)
			{
				user.setMessage("loginname already exists.");
				return new ResponseEntity<User>(user, HttpStatus.CONFLICT);
			}*/
				
			if(userDAO.saveUser(user))
			{
				user.setMessage("User saved Successfully.....");
				return new ResponseEntity<User>(user, HttpStatus.OK);
			}
			else
			{
				user.setMessage("Internal Server Error..");
				return new ResponseEntity<User>(user, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

	// http://localhost:8086/WeiboRestServices/deleteuser/loginname
	@DeleteMapping("/deleteuser/{loginname}")
	public ResponseEntity<User> deleteUser(@PathVariable String loginname) {
		User user = userDAO.getUser(loginname); // checking whether user exist or not
		

		List<JobApplication> appliedlist = jobDAO.userAppliedJobList(loginname); // checking whether user applied for any
																				// jobs or not
		if (!appliedlist.isEmpty()) // checking whether user application list is empty or not(here not empty
									// condition)
		{
			user = new User();
			user.setMessage("this user has already applied for a job  so cant be deletd");
			return new ResponseEntity<User>(user, HttpStatus.CONFLICT);
		}

		if (userDAO.deleteUser(loginname)) // yes user exists
		{
			user.setMessage("user deleted successfully ");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		} else {
			user.setMessage("user not deleted");
			return new ResponseEntity<User>(user, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// http://localhost:8086/WeiboRestServices/updateuser
	@PutMapping("/updateuser")
	public ResponseEntity<User> updateuser(@RequestBody User user) 
	{
		if (userDAO.updateUser(user)) // got the user
		{
			user.setMessage("user details updated");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		} else {
			user.setMessage("no user details were updated");
			return new ResponseEntity<User>(user, HttpStatus.BAD_REQUEST);
		}
	}
}
