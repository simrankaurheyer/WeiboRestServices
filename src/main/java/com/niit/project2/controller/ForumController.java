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
import org.springframework.web.bind.annotation.RestController;

import com.niit.project2.dao.ForumDao;
import com.niit.project2.model.Forum;

@RestController
public class ForumController
{
	
		@Autowired
		private ForumDao forumDAO;
		
		// http://localhost:8086/WeiboRestServices/forum/list
		@RequestMapping("/forum/list")
		public ResponseEntity<List<Forum>> getForumList()
		{
			List<Forum> forumList = forumDAO.ForumList();
			if(!forumList.isEmpty())
			{
				return new ResponseEntity<List<Forum>>(forumList, HttpStatus.OK);
			}
			else
			{
				return new ResponseEntity<List<Forum>>(forumList, HttpStatus.NOT_FOUND);
			}
		}
		
		// http://localhost:8086/WeiboRestServices/forum/approvedList
		@RequestMapping("/forum/approvedList")
		public ResponseEntity<List<Forum>> getApprovedForumList()
		{
			List<Forum> forumList = forumDAO.approvedListForum();
			if(!forumList.isEmpty())
			{
				return new ResponseEntity<List<Forum>>(forumList, HttpStatus.OK);
			}
			else
			{
				return new ResponseEntity<List<Forum>>(forumList, HttpStatus.NOT_FOUND);
			}
		}
		
//		http://localhost:8086/WeiboRestServices/forum/add
		@PostMapping("/forum/add")
		public ResponseEntity<Forum> saveForum(@RequestBody Forum forum, HttpSession session)
		{
			String useremail = (String) session.getAttribute("useremail");
			forum.setEmail(useremail);
			if(forumDAO.addForum(forum))
			{
				return new ResponseEntity<Forum>(forum, HttpStatus.OK);
			}
			else
			{
				return new ResponseEntity<Forum>(forum, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		// http://localhost:8086/WeiboRestServices/forum/delete/forumid
		@DeleteMapping("/forum/delete/{forumid}")
		public ResponseEntity<String> deleteJob(@PathVariable int forumid)
		{
			if(forumDAO.deleteForum(forumid))
			{
				return new ResponseEntity<String>("Success", HttpStatus.OK);
			}
			else
			{
				return new ResponseEntity<String>("Error Occurred", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		// http://localhost:8086/WeiboRestServices/forum/get/forumid
		@RequestMapping("/forum/get/{forumid}")
		public ResponseEntity<Forum> getForum(@PathVariable int forumid)
		{
			Forum forum = forumDAO.getForum(forumid);
			if(forum == null)
			{
				return new ResponseEntity<Forum>(forum, HttpStatus.NOT_FOUND);
			}
			else
			{
				return new ResponseEntity<Forum>(forum, HttpStatus.OK);
			}
		}
		
		// http://localhost:8086/WeiboRestServices/forum/approve/forumid
		@PutMapping("/forum/approve/{forumid}")
		public ResponseEntity<Forum> approveForum(@PathVariable int forumid)
		{
			if(forumDAO.approveForum(forumid))
			{
				Forum forum=forumDAO.getForum(forumid);
				return new ResponseEntity<Forum>(forum, HttpStatus.OK);
			}
			else
			{
				Forum forum=forumDAO.getForum(forumid);
				return new ResponseEntity<Forum>(forum, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		// http://localhost:8086/WeiboRestServices/forum/reject/forumid
		@PutMapping("/forum/reject/{forumid}")
		public ResponseEntity<Forum> rejectForum(@PathVariable int forumid)
		{
			if(forumDAO.rejectForum(forumid))
			{
				Forum forum=forumDAO.getForum(forumid);
				return new ResponseEntity<Forum>(forum, HttpStatus.OK);
			}
			else
			{
				Forum forum=forumDAO.getForum(forumid);
				return new ResponseEntity<Forum>(forum, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		// http://localhost:8086/WeiboRestServices/forum/incLike/forumid
		@RequestMapping("forum/incLike/{forumid}")
		public ResponseEntity<Forum> incLike(@PathVariable int forumid)
		{
			if(forumDAO.incrementLikes(forumid))
			{
				Forum forum=forumDAO.getForum(forumid);
				return new ResponseEntity<Forum>(forum, HttpStatus.OK);
			}
			else
			{
				Forum forum=forumDAO.getForum(forumid);
				return new ResponseEntity<Forum>(forum, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}