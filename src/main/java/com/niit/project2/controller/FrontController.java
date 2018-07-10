package com.niit.project2.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import com.niit.project2.model.Forum;

public class FrontController {

	// http://localhost:8086/WeiboRestServices/home
			@RequestMapping("/home")
			public ResponseEntity<List<Forum>> getForumList()
			{
				
				if(true)
				{
					return new ResponseEntity<List<Forum>>(HttpStatus.OK);
				}
				else
				{
					return new ResponseEntity<List<Forum>>(HttpStatus.NOT_FOUND);
				}
			}
			
	
	
}
