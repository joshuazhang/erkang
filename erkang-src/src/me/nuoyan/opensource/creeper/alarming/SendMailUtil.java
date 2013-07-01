package me.nuoyan.opensource.creeper.alarming;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailUtil {

	private int port;

	private String server;

	private String from;

	private String user;

	private String password;
	
	public SendMailUtil(int port, String server, String from, String user,
			String password) {
		super();
		this.port = port;
		this.server = server;
		this.from = from;
		this.user = user;
		this.password = password;
	}

	public void sendEmail(String email, String subject, String body) {
		try {
			Properties props = new Properties();
			props.put("mail.smtp.host", server);
			props.put("mail.smtp.port", String.valueOf(port));
			props.put("mail.smtp.auth", "true");
			Transport transport = null;
			Session session = Session.getDefaultInstance(props, null);
			transport = session.getTransport("smtp");
			transport.connect(server, user, password);
			MimeMessage msg = new MimeMessage(session);
			msg.setSentDate(new Date());
			InternetAddress fromAddress = new InternetAddress(from);
			msg.setFrom(fromAddress);
			InternetAddress[] toAddress = new InternetAddress[1];
			toAddress[0] = new InternetAddress(email);
			msg.setRecipients(Message.RecipientType.TO, toAddress);
			msg.setSubject(subject, "UTF-8");
			msg.setText(body, "UTF-8");
			msg.saveChanges();
			transport.sendMessage(msg, msg.getAllRecipients());
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将异常打印到邮件中
	 * @param email
	 * @param subject
	 * @param t
	 */
	public void sendExceptionToEmail(String email, String subject, Throwable t) {
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw, true);
			t.printStackTrace(pw);
			sendEmail(email, subject, sw.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pw.flush();
		        sw.flush();
		        pw.close();
		        sw.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NullPointerException np = new NullPointerException();
		StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        np.printStackTrace(pw);
        pw.flush();
        sw.flush();
        System.out.println(sw.toString());
//		sendEmail("zhangyh@chujian.com", "抓取-点评-北京-已停止", new NullPointerException("test").getMessage());
	}

}
