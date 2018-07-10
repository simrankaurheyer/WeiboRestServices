package com.niit.project2.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.niit.project2.dao.BlogDao;
import com.niit.project2.model.Blog;
import com.niit.project2.model.BlogComment;


@RestController
public class BlogController
{

	@Autowired
	private Blog blog;
	
	@Autowired
	private BlogDao  blogDAO;
	
	@Autowired
	private BlogComment blogcomment;
	
	@Autowired
	HttpSession session;
	
	// http://localhost:8086/WeiboRestServices/blog/list
	@RequestMapping("/blog/list")
	public ResponseEntity<List<Blog>> blogList()
	{
		List<Blog> blogs=blogDAO.blogList();
		return new ResponseEntity<List<Blog>>(blogs, HttpStatus.OK);
	}

	@RequestMapping("/getblog/{blogid}")
	public ResponseEntity<Blog> getblog(@PathVariable int blogid)
	{
		
		blog=blogDAO.getBlog(blogid);
		
		if(blog==null)
		{
			return new ResponseEntity<Blog>(blog, HttpStatus.NOT_FOUND);
			
		}
		else
		{
			session.setAttribute("blogidforcomment", blogid);
			return new ResponseEntity<Blog>(blog, HttpStatus.OK);
		}
		
	}
	
	 //	http://localhost:8086/WeiboRestServices/insertblog
			@PostMapping("/insertblog")
			public ResponseEntity<Blog> insertblog(@RequestBody Blog blog)
			{	
				String useremail = (String) session.getAttribute("useremail");
				blog.setEmail(useremail);
				if(blogDAO.insertBlog(blog))
				{
					blog.setMessage("Blog saved Successfully.....");
					return new ResponseEntity<Blog>(blog, HttpStatus.OK);
				}
				else
				{
					blog.setMessage("Internal Server Error..");
					return new ResponseEntity<Blog>(blog, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
					
				@RequestMapping("/blog/approvedList")
				public ResponseEntity<List<Blog>> approvedBlogList()
				{
					List<Blog> approvedBlogs = blogDAO.approvedblogList();
					if(approvedBlogs.isEmpty())
					{
						return new ResponseEntity<List<Blog>>(approvedBlogs, HttpStatus.NOT_FOUND);
					}
					else
					{
						return new ResponseEntity<List<Blog>>(approvedBlogs, HttpStatus.OK);
					}
				}
				
				
				
				@DeleteMapping("blog/delete/{blogid}")
				public ResponseEntity<Blog> deleteBlog(@PathVariable int blogid)
				{
					Blog blog = blogDAO.getBlog(blogid);
					if(blog == null)
					{
						return new ResponseEntity<Blog>(blog, HttpStatus.NOT_FOUND);
					}
					
					if(blogDAO.deleteBlog(blogid))
					{
						return new ResponseEntity<Blog>(blog, HttpStatus.OK);
					}
					else
					{
						return new ResponseEntity<Blog>(blog, HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}
				
				@RequestMapping("blog/approve/{blogid}")
				public ResponseEntity<Blog> approveBlog(@PathVariable int blogid)
				{
					if(blogDAO.approveBlog(blogid))
					{
						return new ResponseEntity<Blog>(blog, HttpStatus.OK);
					}
					else
					{
						return new ResponseEntity<Blog>(blog, HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}
				
				@RequestMapping("/blog/reject/{blogid}")
				public ResponseEntity<Blog> rejectBlog(@PathVariable int blogid)
				{
					if(blogDAO.rejectBlog(blogid))
					{
						return new ResponseEntity<Blog>(blog, HttpStatus.OK);
					}
					else
					{
						return new ResponseEntity<Blog>(blog, HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}
				
				@RequestMapping("blog/incrementLike/{blogid}")
				public ResponseEntity<Blog> incLike(@PathVariable int blogid)
				{
					if(blogDAO.incrementLikes(blogid))
					{
						return new ResponseEntity<Blog>(blog, HttpStatus.OK);
					}
					else
					{
						return new ResponseEntity<Blog>(blog, HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}
				
				
				/*blog commen
				 * t*/
				
				@RequestMapping("blog/listComments/{blogid}")
				public ResponseEntity<List<BlogComment>> listBlogComments(@PathVariable int blogid)
				{
					List<BlogComment> blogcomments = blogDAO.blogCommentList(blogid);
					if(blogcomments.isEmpty())
					{
						return new ResponseEntity<List<BlogComment>>(blogcomments, HttpStatus.NOT_FOUND);
					}
					else
					{
						return new ResponseEntity<List<BlogComment>>(blogcomments, HttpStatus.OK);
					}
				}
					
				@PostMapping("blog/comment")
				public ResponseEntity<BlogComment> commentBlog(@RequestBody BlogComment blogComment)
				{
					String useremail = (String) session.getAttribute("useremail");
					//User u=userDAO.getUser(useremail);
					String loginname = (String) session.getAttribute("loginname");
					int blogid = (Integer)session.getAttribute("blogidforcomment");
					
					blogComment.setBlogid(blogid);
					blogComment.setLoginname(loginname);
				   // blogcomment.setComments("nice blog");
				    
					if(blogDAO.saveBlogComment(blogComment))
					{
						return new ResponseEntity<BlogComment>(blogComment, HttpStatus.OK);
					}
					else
					{
						return new ResponseEntity<BlogComment>(blogComment, HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}
				
				@PostMapping("/blog/comment/delete/{blogcommentid}")
				public ResponseEntity<?> commentdelete(@PathVariable int blogcommentid)
				{
					if(blogDAO.deleteBlogComment(blogcommentid))
					{
						/*BlogComment b = new BlogComment();*/
						return new ResponseEntity<Void>(HttpStatus.OK);
					}
					else
					{
						/*BlogComment b = new BlogComment();
						b = blogDAO.getBlogComment(blogcommentid);*/
						return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
					}
				} 
				
				
			}

