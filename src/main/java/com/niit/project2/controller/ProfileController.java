package com.niit.project2.controller;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.niit.project2.dao.ProfileDao;
import com.niit.project2.dao.UserDao;
import com.niit.project2.model.Profile;
import com.niit.project2.model.User;

@RestController
public class ProfileController 
{
	@Autowired
	private UserDao userDAO;

	@Autowired
	private ProfileDao profileDAO;
	
	@Autowired
	private Profile profile;
 
	@Autowired
	private HttpSession session;

 
 // http://localhost:8086/WeiboRestServices/uploadimage
 @RequestMapping("/uploadimage")
 public ResponseEntity<?> uploadimage(@RequestParam("profilepicture") CommonsMultipartFile profilepicture)
 {
	 Profile profile=new Profile(); //uncommed this part
	 String loginname=(String) session.getAttribute("loginname");
	 profile.setLoginname(loginname);
	 
	 profile.setProfilepicture(profilepicture.getBytes());
	 
	 if(profileDAO.uploadProfile(profile))
	 {
		 return new ResponseEntity<Void>(HttpStatus.OK);
	 }
	 else
	 {
		 return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
	 }
 }
 
 
 
//	http://localhost:8086/WeiboRestServices/profile/getProfilePicture/{loginname}
	@RequestMapping("profile/getProfilePicture/{loginname}")
	public @ResponseBody byte[] getProfilePicture(@PathVariable String loginname)
	{
		loginname = (String) session.getAttribute("loginname");
		Profile profile = profileDAO.getProfileDetails(loginname);
		
		if(profile == null)
		{
			return null;
		}
		else
		{
			byte[] image = profile.getProfilepicture();
			return image;
		}
	}
	
	
	// http://localhost:8086/WeiboRestServices/friends/profile/getProfilePicture/{loginname}
	@RequestMapping("friends/profile/getProfilePicture/{loginname}")
	public @ResponseBody byte[] getFriendsProfilePicture(@PathVariable String loginname)
	{
		Profile profile = profileDAO.getProfileDetails(loginname);
		
		if(profile == null)
		{
			return null;
		}
		else
		{
			byte[] image = profile.getProfilepicture();
			return image;
		}
	}
}
