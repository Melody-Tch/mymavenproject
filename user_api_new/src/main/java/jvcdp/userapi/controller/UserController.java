package jvcdp.userapi.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jvcdp.userapi.common.Utility;
import jvcdp.userapi.model.UserInfo;
import jvcdp.userapi.service.UserInfoService;

@RestController
@RequestMapping("/api/v1")
public class UserController {

	@Autowired
	private UserInfoService userInfoService;
	
	@RequestMapping(value="users", method=RequestMethod.GET)
	public List<UserInfo> list(){
		return userInfoService.findAll();
	}
	
	@RequestMapping(value="users", method=RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public UserInfo create(@RequestBody UserInfo userinfo){
		String salt =Utility.getRandomHash();
		userinfo.setPasswordhash(Utility.md5Hash(userinfo.getPasswordhash(), salt));
		userinfo.setSalt(salt);
		Date now = new Date();
		userinfo.setCreatedate(now);
		userinfo.setLastlogin(now);
		userinfo.setLastupdate(now);
		return userInfoService.addUser(userinfo);
	}
	
	@RequestMapping(value="users/{id}", method=RequestMethod.GET)
	public UserInfo get(@PathVariable long id){
		return userInfoService.findOne(id);
	}
	
	@RequestMapping(value="users/{id}", method=RequestMethod.PUT,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public UserInfo update(@PathVariable long id, @RequestBody UserInfo user){
		UserInfo oldUserInfo = userInfoService.findOne(id);
		BeanUtils.copyProperties(user,oldUserInfo);
		return userInfoService.updateUser(oldUserInfo);
	}
	
	@RequestMapping(value="users/{id}", method=RequestMethod.DELETE,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public UserInfo delete(@PathVariable long id) throws Exception{
		UserInfo delUserInfo = userInfoService.findOne(id);
		int delete_count = userInfoService.delete(delUserInfo);;
		if(delete_count>=1){
			return delUserInfo;
		}else{
			throw new Exception("There was some error in deleting the User.");
		}
	}
}
