package com.payservice.module;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.payservice.bean.Card;
import com.payservice.bean.User;

@IocBean
@At("/pay")
@Ok("json")
@Fail("http:500")
public class CardModule {
	
	@Inject
	protected Dao dao;
		
	@At
	public int count() {
		return dao.count(Card.class);
	}
	//手机充值
	@At
	public Object pay2mobile(@Param("mobile") String phone,@Param("password") String password, @Param("amount") String amt, HttpSession session){
		int nId = (int)session.getAttribute("me");
		User userHost = dao.fetch(User.class, Cnd.where("id", "=", nId).and("password", "=", password));
		NutMap re = new NutMap();
		if (userHost == null) {
			return re.setv("ok", false).setv("msg", "用户不存在!");
		} else {
			int hostamount = userHost.getAmount()-Integer.parseInt(amt);
			if (hostamount<0){
				return re.setv("ok", false).setv("msg", "余额不足!");
			}
			userHost.setAmount(hostamount);
			userHost.setUpdateTime(new Date());
			String strRec = userHost.getRecord();
			userHost.setRecord(strRec+"1,"+amt+","+phone+";");
			dao.update(userHost);
			return re.setv("ok", true).setv("amount", hostamount);
		}
	}
	//手机银行账户转账
	@At
	public Object pay2mobileaccount(@Param("mobile") String phone,@Param("password") String password, @Param("amount") String amt, HttpSession session){
		int nId = (int)session.getAttribute("me");
		User userHost = dao.fetch(User.class, Cnd.where("id", "=", nId).and("password", "=", password));
		User user = dao.fetch(User.class, Cnd.where("mobile", "=", phone).and("password", "=", password));
		NutMap re = new NutMap();
		if (user == null||userHost == null) {
			return re.setv("ok", false).setv("msg", "用户不存在!");
		} else {
			int hostamount = userHost.getAmount()-Integer.parseInt(amt);
			if (hostamount<0){
				return re.setv("ok", false).setv("msg", "余额不足!");
			}
			userHost.setAmount(hostamount);
			userHost.setUpdateTime(new Date());
			String strRec = userHost.getRecord();
			userHost.setRecord(strRec+"1,"+amt+","+phone+";");
			dao.update(userHost);
			int newamount =user.getAmount()+Integer.parseInt(amt);
			user.setAmount(newamount);
			strRec = user.getRecord();
			user.setRecord(strRec+"0,"+amt+","+userHost.getMobile()+";");
			user.setUpdateTime(new Date());
			dao.update(user) ;
			return re.setv("ok", true).setv("amount", hostamount);
		}
	}
	//转账到银行卡
	@At
	public Object pay2card(@Param("cardNum") String phone,@Param("password") String password, @Param("amount") String amt, HttpSession session){
		
		int nId = (int)session.getAttribute("me");
		User user = dao.fetch(User.class, Cnd.where("id", "=", nId).and("password", "=", password));
		Card card = dao.fetch(Card.class, Cnd.where("cardnum", "=", phone));
		NutMap re = new NutMap();
		if (user == null||card == null) {
			return re.setv("ok", false).setv("msg", "用户不存在!");
		} else {
			int hostamount = user.getAmount()-Integer.parseInt(amt);
			if (hostamount<0){
				return re.setv("ok", false).setv("msg", "余额不足!");
			}
			
			user.setAmount(hostamount);
			user.setUpdateTime(new Date());
			String strRec = user.getRecord();
			user.setRecord(strRec+"1,"+amt+","+phone+";");
			dao.update(user);
			int newamount =card.getAmount()+Integer.parseInt(amt);
			card.setUpdateTime(new Date());
			card.setAmount(newamount);
			dao.update(card) ;
			return re.setv("ok", true).setv("amount", hostamount);
		}
	}
	//余额充值
	@At
	public Object pay2host(@Param("cardNum") String phone,@Param("password") String password, @Param("amount") String amt, HttpSession session){
		
		NutMap re = new NutMap();
		int nId = (int)session.getAttribute("me");
		User user = dao.fetch(User.class, Cnd.where("id", "=", nId).and("password", "=", password));
		Card card = dao.fetch(Card.class, Cnd.where("cardnum", "=", phone));
		
		if (user == null||card == null) {
			return re.setv("ok", false).setv("msg", "用户不存在!");
		} else {
			int hostamount = card.getAmount()-Integer.parseInt(amt);
			if (hostamount<0){
				return re.setv("ok", false).setv("msg", "余额不足!");
			}
			
			card.setAmount(hostamount);
			card.setUpdateTime(new Date());
			dao.update(card) ;
			user.setUpdateTime(new Date());
			int newamount =user.getAmount()+Integer.parseInt(amt);
			user.setAmount(newamount);
			String strRec = user.getRecord();
			user.setRecord(strRec+"0,"+amt+","+phone+";");
			dao.update(user);
			return re.setv("ok", true).setv("amount", newamount);
		}
	}
	//跨库转账
	@At
	public  Object pay2Moneybag(@Param("cardNum") String phone,@Param("password") String password, @Param("amount") String amt, HttpSession session){

		int nId = (int)session.getAttribute("me");
		User user = dao.fetch(User.class, Cnd.where("id", "=", nId).and("password", "=", password));
		Card card = dao.fetch(Card.class, Cnd.where("cardnum", "=", phone));
		NutMap re = new NutMap();
		if (user == null||card == null) {
			return re.setv("ok", false).setv("msg", "用户不存在!");
		} else {
			int hostamount = user.getAmount()-Integer.parseInt(amt);
			if (hostamount<0){
				return re.setv("ok", false).setv("msg", "余额不足!");
			}

			user.setAmount(hostamount);
			user.setUpdateTime(new Date());
			String strRec = user.getRecord();
			user.setRecord(strRec+"1,"+amt+","+phone+";");
			dao.update(user);
			int newamount =card.getAmount()+Integer.parseInt(amt);
			card.setUpdateTime(new Date());
			card.setAmount(newamount);
//			dao.update(card) ;
			return re.setv("ok", true).setv("amount", hostamount);
		}

	}

	@At
	public  Object pay2Money(@Param("cardNum") String phone, @Param("amount") String amt){

//		int nId = (int)session.getAttribute("me");
//		User user = dao.fetch(User.class, Cnd.where("id", "=", nId).and("password", "=", password));

		Card card = dao.fetch(Card.class, Cnd.where("cardnum", "=", phone));
		NutMap re = new NutMap();
//		if (user == null||card == null) {
//			return re.setv("ok", false).setv("msg", "用户不存在!");
//		} else {
//			int hostamount = user.getAmount()-Integer.parseInt(amt);
//			if (hostamount<0){
//				return re.setv("ok", false).setv("msg", "余额不足!");
//			}
//
//			user.setAmount(hostamount);
//			user.setUpdateTime(new Date());
//			String strRec = user.getRecord();
//			user.setRecord(strRec+"1,"+amt+","+phone+";");
//			dao.update(user);
			int newamount =card.getAmount()+Integer.parseInt(amt);
			card.setUpdateTime(new Date());
			card.setAmount(newamount);
			dao.update(card) ;
//			return re.setv("ok", true).setv("amount", hostamount);
		return re.setv("ok", true).setv("cardnum", card);
		}


}
