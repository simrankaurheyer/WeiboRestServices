package com.niit.project2.controller;


import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.niit.project2.dao.FriendDao;
import com.niit.project2.dao.UserDao;
import com.niit.project2.model.Friend;
import com.niit.project2.model.User;


@RestController
public class FriendController 
{
	@Autowired
	HttpSession session;
	
	@Autowired
	private FriendDao friendDAO;
	
	@Autowired
	private Friend friend;
	
	@Autowired
	private UserDao userDAO;
	
	// http://localhost:8086/WeiboRestServices/friend/list/{loginname}
	@RequestMapping("friend/list")
	public ResponseEntity<List<Friend>> showFriendList()
	{
		String loginname = (String) session.getAttribute("loginname");
		
		List<Friend> friends = friendDAO.friendList(loginname);
		
		if(friends.isEmpty())
		{
			Friend friend = new Friend();
			friend.setMessage("No Friends Yet !!!");
			friends.add(friend);
			return new ResponseEntity<List<Friend>>(friends, HttpStatus.NOT_FOUND);
		}
		else
		{
			return new ResponseEntity<List<Friend>>(friends, HttpStatus.OK);
		}
	}
	
	@RequestMapping("friend/pendingRequest")
	public ResponseEntity<List<Friend>> PendingFriendRequest()
	{
		String loginname = (String) session.getAttribute("loginname");
				
		List<Friend> friends = friendDAO.pendingFriendRequestList(loginname);
		
		if(friends.isEmpty())
		{
			Friend friend = new Friend();
			friend.setMessage("No Pending Friend Requests Yet !!!");
			friends.add(friend);
			return new ResponseEntity<List<Friend>>(friends, HttpStatus.NOT_FOUND);
		}
		else
		{
			return new ResponseEntity<List<Friend>>(friends, HttpStatus.OK);
		}
	}
	
	@RequestMapping("friend/suggested")
	public ResponseEntity<List<User>> suggestedPeople()						// @PathVariable String loginname
	{		
		String loginname = (String) session.getAttribute("loginname");
		
		List<User> suggestedPeople = friendDAO.suggestedPeopleList(loginname);	
		if(suggestedPeople.isEmpty())
		{
			return new ResponseEntity<List<User>>(suggestedPeople, HttpStatus.NOT_FOUND);
		}
		else
		{
			return new ResponseEntity<List<User>>(suggestedPeople, HttpStatus.OK);
		}
	}
	
	@RequestMapping("friend/sendRequest/{friendname}")
	public ResponseEntity<Friend> sendRequest(@PathVariable String friendname)
	{
		String loginname = (String) session.getAttribute("loginname");
				
		friend.setLoginname(loginname);
		friend.setFriendname(friendname);
		
		if(friendDAO.sendFriendRequest(friend))
		{
			friend.setMessage("Request Sent");
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		else
		{
			friend.setMessage("Request not Sent");
			return new ResponseEntity<Friend>(friend, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("friend/acceptRequest/{friendid}")
	public ResponseEntity<Friend> acceptRequest(@PathVariable int friendid)
	{
		Friend friend = new Friend();
		if(friendDAO.acceptFriendRequest(friendid))
		{
			friend = friendDAO.getFriend(friendid);
			friend.setMessage("Request Accepted");
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		else
		{
			friend = friendDAO.getFriend(friendid);
			friend.setMessage("internal server error");
			return new ResponseEntity<Friend>(friend, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("friend/deleteRequest/{friendid}")
	public ResponseEntity<Friend> rejectRequest(@PathVariable int friendid)
	{
		Friend friend = new Friend();
		if(friendDAO.deleteFriendRequest(friendid))
		{
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<Friend>(friend, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("friend/delete/{friendid}")
	public ResponseEntity<Friend> DeleteFriend(@PathVariable int friendid)
	{
		
		if(friendDAO.deleteFriendRequest(friendid))
		{
			Friend friend = new Friend();
			friend.setMessage("Success");
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		else
		{
			Friend friend = new Friend();
			friend.setMessage("internal server error");
			return new ResponseEntity<Friend>(friend, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}

