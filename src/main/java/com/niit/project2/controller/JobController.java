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

import com.niit.project2.dao.JobDao;
import com.niit.project2.dao.UserDao;
import com.niit.project2.model.Job;
import com.niit.project2.model.JobApplication;
import com.niit.project2.model.User;

@RestController
public class JobController
{
	
	@Autowired
    private	Job job;
	
	@Autowired
	private	JobDao jobDAO;
	
	@Autowired
	private UserDao userDAO;
	
	@Autowired
	private User user;
	
	@Autowired
	HttpSession session;
	
	@Autowired
	private JobApplication jobApplication;
	

	 
//	http://localhost:8081/CollaborationRestService/list
	@RequestMapping("/joblist") 							//get list of jobs
	public ResponseEntity<List<Job>> jobList()
	{
		List<Job>  jobs=jobDAO.jobList();
		if(jobs.isEmpty())
		{
			job.setMessage("no jobs available");
			jobs.add(job);
			return new ResponseEntity<List<Job>>(jobs, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<Job>>(jobs, HttpStatus.OK);
	}
	
	
//	http://localhost:8081/CollaborationRestService/addjob
	@PostMapping("/addjob")
	public ResponseEntity<Job> saveJob(@RequestBody Job job)   //adding new job
	{
		if(jobDAO.saveJob(job))
		{
			job.setMessage("job added successfully");
			return new ResponseEntity<Job> (job,HttpStatus.OK);
		}
		else
		{
			job.setMessage("job not added,try again");
			
			return new ResponseEntity<Job> (job,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	
//	http://localhost:8081/CollaborationRestService/updatejob
	@PutMapping("/updatejob")
    public ResponseEntity<Job> updatejob(@RequestBody Job job)
    {
		if(jobDAO.updateJob(job))
		{
			job.setMessage("job updated successfully");
			return new ResponseEntity<Job> (job,HttpStatus.OK);
		}
		else
		{
			job.setMessage("job not updated,try again");
			
			return new ResponseEntity<Job> (job,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
    }
	
//	http://localhost:8081/CollaborationRestService/joblist/status
	@RequestMapping("joblist/{jobstatus}")      //get the list of jobs based on status
	public ResponseEntity<List<Job>> jobList(@PathVariable char jobstatus)
	{
		List<Job> jobs=jobDAO.jobList(jobstatus);
		if(jobs.isEmpty())
		{
			job.setMessage("no jobs available with the status "+jobstatus);
			jobs.add(job);
			return new ResponseEntity<List<Job>>(jobs, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<List<Job>>(jobs, HttpStatus.OK);
	}
	
	@RequestMapping("job/get/{jobid}")      //get the list of jobs based on status
	public ResponseEntity<Job> jobList(@PathVariable int jobid)
	{
		Job job = jobDAO.getJob(jobid);
		if(job != null)
		{
			session.setAttribute("jobid", jobid);
			return new ResponseEntity<Job>(job, HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<Job>(job, HttpStatus.NOT_FOUND);
		}
	}



//	http://localhost:8081/CollaborationRestService/updatejobstatus/jobid/jobstatus
	@PutMapping("/updatejobstatus/{jobid}/{jobstatus}")
	public ResponseEntity<Job> updatejob(@PathVariable int jobid,@PathVariable char jobstatus)    // updating job by providing only jobid nd job status
	{
		
		Job job=jobDAO.getJob(jobid);           //get job by calling getjobid() for  checking whether job with that id exists or not
		if(job==null)        
		{
			job=new Job();     //instance created to avoid null pointer exception
			job.setMessage("no job exist with this job id "+jobid);
			return new ResponseEntity<Job>(job, HttpStatus.NOT_FOUND);
		}
	
	
	    job.setJobstatus(jobstatus);      // if job exists update the status
	    if( jobDAO.updateJob(job))
	    {
	    	job.setMessage("job status updated successfully");
	    	return new ResponseEntity<Job>(job, HttpStatus.OK);
	    }
	    else
	    {
	    	job.setMessage("job status not updated successfully");
	    	return new ResponseEntity<Job>(job, HttpStatus.INTERNAL_SERVER_ERROR);
	     }
	}
	
	// ********job application  ************
	
	
	@PostMapping("/applyforjob")
	public ResponseEntity<JobApplication> applyjob(@RequestBody JobApplication jobApplication)
	{
		String email = (String) session.getAttribute("useremail");
		int jobid = (Integer)session.getAttribute("jobid");
		
		jobApplication.setEmail(email);
		jobApplication.setJobid(jobid);
						
		if(!jobDAO.isAlreadyApplied(email, jobid))  //if the user already applied or not
		{
			if(jobDAO.saveJobApplication(jobApplication))
			{
				jobApplication.setMessage("successfully applied for the job");
				return new ResponseEntity<JobApplication> (jobApplication,HttpStatus.OK);
			}	
			else
			{
				jobApplication.setMessage("could not apply for the job");
				return new ResponseEntity<JobApplication> (jobApplication,HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
		}
		else
		{
			jobApplication.setMessage("you already applied for this job	");
			return new ResponseEntity<JobApplication> (jobApplication,HttpStatus.NOT_FOUND);
		}
				
		
	}	
	
	//accept/reject/select/call for an interview---admin activities
	@RequestMapping("/jobapplication/update/{jobid}/{email},{jobapplicationstatus}") // to update user job application for particular jobid
	 																  //which jod id status to update,
	                                                                      //which user status to update and what to update ie status accept,reject ,select or call	
	public 	ResponseEntity<JobApplication> update(@PathVariable int jobid,@PathVariable String email,@PathVariable char jobapplicationstatus)
	{
		if(userDAO.getUser(email)==null)   //user exist or not
		{
			jobApplication.setMessage("user does not exist");
			return new ResponseEntity<JobApplication> (jobApplication,HttpStatus.NOT_FOUND);
		}
		
		if(jobDAO.getJob(jobid)==null)  //job exist or not
		{
			jobApplication.setMessage("job does not exist");
			return new ResponseEntity<JobApplication> (jobApplication,HttpStatus.NOT_FOUND);
		}
		
			
		if(jobDAO.getjobstatus(jobid, 'N')==null)  //still the job is opened or not 		
		{
			jobApplication.setMessage("this job is not open");
			return new ResponseEntity<JobApplication> (jobApplication,HttpStatus.NOT_FOUND);
		}
		
		jobApplication.setJobapplicationstatus(jobapplicationstatus);
		if(jobDAO.update(jobApplication))
		{
			jobApplication.setMessage("job application status successfully updated");
			return new ResponseEntity<JobApplication> (jobApplication,HttpStatus.OK);	
		}
		jobApplication.setMessage("job application status not updated");
		return new ResponseEntity<JobApplication> (jobApplication,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	
	
	
	//list of people who applied for a particular job
	@RequestMapping("appliedjobs")
	public ResponseEntity<List<JobApplication>> userjoblist()
	{
		String email = (String) session.getAttribute("useremail");
		List<JobApplication>  jobapplications=jobDAO.userAppliedJobList(email);
		if(jobapplications.isEmpty())
		{
			return new ResponseEntity <List<JobApplication>> (jobapplications,HttpStatus.NOT_FOUND);
		}
		else
		{
			return new ResponseEntity <List<JobApplication>> (jobapplications,HttpStatus.OK);
		}
	}
	
	@RequestMapping("application/approve/{jobapplicationid}")
	public ResponseEntity<?> approveApplication(@PathVariable int jobapplicationid)
	{
		if(!jobDAO.approveApplication(jobapplicationid))
		{
			return new ResponseEntity <Void> (HttpStatus.NOT_FOUND);
		}
		else
		{
			return new ResponseEntity <Void> (HttpStatus.OK);
		}
	}
	
	@RequestMapping("application/reject/{jobapplicationid}")
	public ResponseEntity<?> rejectApplication(@PathVariable int jobapplicationid)
	{
		if(!jobDAO.rejectApplication(jobapplicationid))
		{
			return new ResponseEntity <Void> (HttpStatus.NOT_FOUND);
		}
		else
		{
			return new ResponseEntity <Void> (HttpStatus.OK);
		}
	}
	
	@RequestMapping("allapplications")
	public ResponseEntity<List<JobApplication>> allapps()
	{
		List<JobApplication> j = jobDAO.listapp();
		if(j.isEmpty())
		{
			return new ResponseEntity<List<JobApplication>> (j,HttpStatus.NOT_FOUND);
		}
		else
		{
			return new ResponseEntity<List<JobApplication>>(j,HttpStatus.OK);
		}
	}
	
	@DeleteMapping("/deletejob/{jobid}")
	public ResponseEntity<?> saveJob(@PathVariable int jobid){
		if(jobDAO.deletejob(jobid)) {
			return new ResponseEntity<Void> (HttpStatus.OK);
		}
		else {
			return new ResponseEntity<Void> (HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}



	

