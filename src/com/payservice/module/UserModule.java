package com.payservice.module;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.payservice.bean.User;

@IocBean
@At("/user")
@Ok("json")
@Fail("http:500")
public class UserModule {

	@Inject
	protected Dao dao;
	
	protected String checkUser(User user, boolean create) {
        if (user == null) {
            return "空对象";
        }
        if (create) {
            if (Strings.isBlank(user.getMobile()) || Strings.isBlank(user.getPassword()))
                return "电话/密码不能为空";
        } else {
            if (Strings.isBlank(user.getPassword()))
                return "密码不能为空";
        }
        String passwd = user.getPassword().trim();
        if (6 > passwd.length() || passwd.length() > 18) {
            return "密码长度错误,6~18位";
        }
        user.setPassword(passwd);
        if (create) {
            int count = dao.count(User.class, Cnd.where("name", "=", user.getName()));
            if (count != 0) {
                return "用户名已经存在";
            }
        } else {
            if (user.getId() < 1) {
                return "用户Id非法";
            }
        }
        if (user.getName() != null)
            user.setName(user.getName().trim());
        return null;
    }

	@At
	public int count() {
		return dao.count(User.class);
	}

	@At
	public Object login(@Param("mobile") String phone, @Param("password") String password, HttpSession session) {
		User user = dao.fetch(User.class, Cnd.where("mobile", "=", phone).and("password", "=", password));
		NutMap re = new NutMap();
		if (user == null) {
			return re.setv("ok", false);
		} else {
			session.setAttribute("me", user.getId());			
			return re.setv("ok", true).setv("data", user);
		//	return true;
		}
	}

	@At
	@Ok(">>:/")
	public void logout(HttpSession session) {
		session.invalidate();
	}

	@At
	public Object regist(@Param("..") User user) {
		NutMap re = new NutMap();
		String msg = checkUser(user, true);
		if (msg != null) {
			return re.setv("ok", false).setv("msg", msg);
		}
		user.setCreateTime(new Date());
		user.setUpdateTime(new Date());
		user.setAmount(8000);
		user.setPhoto("");
		user.setRecord("");
		user = dao.insert(user);
		return re.setv("ok", true).setv("data", user);
	}
	
	@At
	public Object getamount(@Param("mobile") String phone) {
		User user = dao.fetch(User.class, Cnd.where("mobile", "=", phone));
		NutMap re = new NutMap();
		if (user == null) {
			return re.setv("ok", false);
		} else {
			return re.setv("ok", true).setv("mobile", phone).setv("amount", user.getAmount()).setv("record", user.getRecord());
		}
	}
	
	@At
	public Object rename(@Param("mobile") String phone, @Param("newName") String name){
		User user = dao.fetch(User.class, Cnd.where("mobile", "=", phone));
		NutMap re = new NutMap();
//		String msg = checkUser(user, true);
		if (user == null) {
			return re.setv("ok", false).setv("msg", "用户不存在!");
		} else {			
			user.setName(name);
			dao.update(user);
			return re.setv("ok", true).setv("name", name);
		}
	}
	
	@At
	public Object updatebirthday(@Param("mobile") String phone, @Param("newDate") String newdate){
		User user = dao.fetch(User.class, Cnd.where("mobile", "=", phone));
		NutMap re = new NutMap();
//		String msg = checkUser(user, true);
		if (user == null) {
			return re.setv("ok", false).setv("msg", "用户不存在!");
		} else {
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	            Date bdate = null;
				try {
					bdate = sdf.parse(newdate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			user.setBirthday(bdate);;
			dao.update(user);
			return re.setv("ok", true).setv("birthday", newdate);
		}
	}
	
	@At
	public Object updatephoto(@Param("mobile") String phone, @Param("newPhoto") String newphoto){
		User user = dao.fetch(User.class, Cnd.where("mobile", "=", phone));
		NutMap re = new NutMap();
//		String msg = checkUser(user, true);
		if (user == null) {
			return re.setv("ok", false).setv("msg", "用户不存在!");
		} else {			
			user.setPhoto(newphoto);
			dao.update(user);
			return re.setv("ok", true).setv("photo", newphoto);
		}
	}
}
