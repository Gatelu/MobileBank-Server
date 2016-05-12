package com.payservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import com.payservice.bean.Card;
import com.payservice.bean.User;

public class MainSetup implements Setup {

	@Override
	public void destroy(NutConfig arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(NutConfig arg0) {
		// TODO Auto-generated method stub
		 Ioc ioc = arg0.getIoc();
	        Dao dao = ioc.get(Dao.class);
	        Daos.createTablesInPackage(dao, "com.payservice", false);
	        
	        // 初始化默认根用户
	        if (dao.count(User.class) == 0) {
	            User user = new User();
	            user.setMobile("13761369128");
	            user.setName("admin");
	            user.setPassword("888888");
	            user.setAnswer("my name is qibin.hu");
	            user.setSex("男");
	            String dateString = "1986-12-06";
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	            Date bdate = null;
				try {
					bdate = sdf.parse(dateString);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
	            user.setBirthday(bdate);
	            user.setCreateTime(new Date());
	            user.setUpdateTime(new Date());
	            user.setAmount(80000);
	            user.setPhoto("");
	            user.setRecord("");
	            dao.insert(user);
	        }
	        //初始化卡信息
	        if ( dao.count(Card.class)==0 ){
	        	Card card = new Card();
	        	card.setCardnum("123");
	        	card.setName("zhangsan");
	        	card.setUpdateTime(new Date());
	        	card.setAmount(2000);
	        	card.setRecord("");
	        	dao.insert(card);
	        	card.setCardnum("1234");
	        	card.setName("lisi");
	        	card.setUpdateTime(new Date());
	        	card.setAmount(2000);
	        	dao.insert(card);
	        	card.setCardnum("12345");
	        	card.setName("wangwu");
	        	card.setUpdateTime(new Date());
	        	card.setAmount(2000);
	        	dao.insert(card);
	        }
	}

}
