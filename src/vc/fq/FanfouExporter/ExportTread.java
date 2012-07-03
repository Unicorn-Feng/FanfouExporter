/*
 * Copyright 2012, Unicorn-Feng
 * All rights reserved.
 * 
 * This file is part of FanfouExporter.
 * FanfouExporter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * FanfouExporter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with FanfouExporter.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *饭否消息导出工具 FanfouExporter
 *Export Tool for Fanfou
 *Author: 烽麒 Unicorn-Feng
 *Website: http://fq.vc 
 */

package vc.fq.FanfouExporter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 此类用于完成消息导出工作
 * @author 烽麒 Unicorn-Feng
 * @link http://fq.vc
 */
public class ExportTread extends Thread
{
	public static final String consumer_key = "5ee8c6fae7022acc00e5f59cbeb80372";
	public static final String consumer_secret = "3385a4a2951d47b5f66dc6b039539c00";
	public static final String HMAC_SHA1 = "HmacSHA1";
	public String oauth_token;
	public String oauth_token_secret;
	private PrintWriter outstream;
	private int picnum = 0;
	private int errtime = 0;
	private int num = 0;
	private int format;
	private boolean exportPic;
	private String picpath;
	private String filepath;

	
	/**
     * 完成线程结束前的收尾工作
     */
    public void exitTread()
    {
		if(format == 1)
		{
			outstream.println();
			outstream.close();
		}
		else if(format == 2)
		{
			outstream.println("</statuses>");
			outstream.close();
		}
    	Main.isStart = false;
    	Main.btnStart.setText("开始");
    }
    
    
    /**
	 * 多线程主函数
	 */
	public void run() 
	{
		String username = Main.txtUsr.getText();
		String password = Main.txtPwd.getText();
		String userID = Main.txtFriID.getText();
		filepath = Main.txtFilePath.getText();
		exportPic = Main.chkbxPic.isSelected();
		String filename;
		
		if(filepath.charAt(filepath.length()-1) == '/' || filepath.charAt(filepath.length()-1) == '\\')
		{
			filepath = filepath.substring(0,filepath.length()-1);
		}
		
		int content;
		if(Main.rdbtnUsrTL.isSelected())
		{
			content = 1;
		}
		else if(Main.rdbtnMention.isSelected())
		{
			content = 2;
		}
		else if(Main.rdbtnDM.isSelected())
		{
			content = 3;
		}
		else
		{
			content = 4;
		}
		

        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        StringBuffer sb = new StringBuffer(15);
		if(content == 4)
		{
			sb.append(df.format(new Date()));
			sb.append(userID);
		}
		else
		{
	        sb.append(df.format(new Date()));
			sb.append(username);
		}
		
		if(Main.rdbtnCSV.isSelected())
		{
			format = 1;
			sb.append(".csv");
			filename = sb.toString();
		}
		else if(Main.rdbtnXML.isSelected())
		{
			format = 2;
			sb.append(".xml");
			filename = sb.toString();
		}
		else
		{
			format = 3;
			sb.append("_1.html");
			filename = sb.toString();
		}
		sb = null;
		setLog("\r\n导出数据存储在" + filepath + "\\" + filename);
		if(exportPic)
		{
			setLog("图片存储在" + filepath + "\\" + df.format(new Date()) + "pic 目录下");
		}
		
		setLog("\r\n尝试连接饭否...");
		
		/* 进行XAuth认证,获取Access_Token及Access_Token_Secret */
		String strxauth = XAuth(username,password);
		if(strxauth == null)
		{
	    	Main.isStart = false;
	    	Main.btnStart.setText("开始");
			return;
		}
		try {
			String[] tokenarr = strxauth.split("&");
			String[] tokenarr2 = tokenarr[0].split("=");
			oauth_token = tokenarr2[1];
			tokenarr2 = tokenarr[1].split("=");
			oauth_token_secret = tokenarr2[1];
			setLog("成功连接饭否\r\n");
		} catch (ArrayIndexOutOfBoundsException e) {
			setLog("获取到的Access_Token异常\r\n请重试");
	    	Main.isStart = false;
	    	Main.btnStart.setText("开始");
			return;
		}
		
		filepath = filepath.replace("\\", "/");
		
		File dirFile = new File(filepath);
		boolean mkdir;
		if(!dirFile.exists() && !dirFile.isDirectory())
		{
			int option = JOptionPane.showConfirmDialog(null, "该目录不存在,是否创建?", "找不到路径", JOptionPane.YES_NO_OPTION);
			if(option == JOptionPane.YES_OPTION)
			{
				mkdir = dirFile.mkdirs();
				if(mkdir == false)
				{
					setLog("创建文件夹失败");
			    	Main.isStart = false;
			    	Main.btnStart.setText("开始");
					return;
				}
				setLog("路径创建成功");
			}
			else
			{
				setLog("请重新选择路径");
		    	Main.isStart = false;
		    	Main.btnStart.setText("开始");
				return;
			}
		}
		
		/* 创建图片文件夹 */
		if(content == 4)
		{
			picpath = filepath + "/" + df.format(new Date()) + userID + "pic";
		}
		else
		{
			picpath = filepath + "/" + df.format(new Date()) + username + "pic";
		}
		if(exportPic)
		{
			dirFile = new File(picpath);
			if(!dirFile.exists() && !dirFile.isDirectory())
			{
				mkdir = dirFile.mkdirs();
				if(mkdir == false)
				{
					setLog("创建图片文件夹失败");
			    	Main.isStart = false;
			    	Main.btnStart.setText("开始");
					return;
				}
				setLog("成功创建图片文件夹");
			}
		}
		
		/* 打开文件 */
		if(format != 3)
		{
			try {
				outstream = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename),"gb2312"));
			} catch (FileNotFoundException e) {
				setLog(e.getMessage());
		    	Main.isStart = false;
		    	Main.btnStart.setText("开始");
				return;
			} catch (UnsupportedEncodingException e) {
				setLog(e.getMessage());
		    	Main.isStart = false;
		    	Main.btnStart.setText("开始");
				return;
			}
		}
		
		if(format == 1)
		{
			outstream.println("时间,消息,消息id,来源,图片地址,用户id,用户昵称,用户自述,用户头像地址,用户地址");
		}
		else if(format == 2)
		{
			outstream.println("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
			outstream.println("<statuses>");
		}
		
		HttpURLConnection connection;
		
		int page = 1;
			
		while(true)
		{
	    	try {
	    		setLog("等待5s,准备开始导出第" + String.valueOf(num+1) + "条消息");
	    		Thread.sleep(5000);	//暂停5s
	    	} catch (InterruptedException e) {
	    		setLog("成功终止\r\n已导出" + String.valueOf(num) + "条消息");
	        	exitTread();
	    		return;
	    	}

	    	if(content == 1)
	    	{
	    		connection = timeline(page);
	    	}
	    	else if(content == 2)
	    	{
	    		connection = mention(page);
	    	}
	    	else if(content == 3)
	    	{
	    		connection = timeline(page);
	    	}
	    	else
	    	{
	    		connection = timeline(page,userID);
	    	}
			
			if(errtime >= 5)
			{
				setLog("出现错误，请新开始导出\r\n已导出" + String.valueOf(num) + "条消息");
				exitTread();
	    		return;
			}
			String line;
			try {
				if(connection.getResponseCode() == 200)
				{
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					line = reader.readLine();
				}
				else if(connection.getResponseCode() == 403)
				{
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
					String log = "";
					line = reader.readLine();
					while(line != null)
					{
						log = log + "\r\n" + line;
						line = reader.readLine();
					}
					setLog(log);
					setLog("您没有对该用户的访问权限，请确认您已加TA为好友");
					exitTread();
					return;
				}
				else if(connection.getResponseCode() == 404)
				{
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
					String log = "";
					line = reader.readLine();
					while(line != null)
					{
						log = log + "\r\n" + line;
						line = reader.readLine();
					}
					setLog(log);
					setLog("没有这个用户");
					exitTread();
					return;
				}
				else
				{
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
					String log = "";
					line = reader.readLine();
					while(line != null)
					{
						log = log + "\r\n" + line;
						line = reader.readLine();
					}
					errtime++;
					setLog("出现错误:\r\n" + log + "\r\n准备进行第" + errtime + "次重试");
					continue;
				}
			} catch (SocketTimeoutException e) {
				errtime++;
				setLog("出现错误:\r\n" + e.getMessage() + "\r\n准备进行第" + errtime + "次重试");
				continue;
			} catch (IOException e) {
				errtime++;
				setLog("出现错误:\r\n" + e.getMessage() + "\r\n准备进行第" + errtime + "次重试");
				continue;
			}
			writeFile(line,format,page);
			page++;
		}//while(true)
	}

	
	/**
	 * 获取月份
	 * @param month
	 * @return 数字月份
	 */
	public static int getMonth(String month)
	{
		if(month.equals("Jan"))
			return 1;
		else if(month.equals("Feb"))
			return 2;
		else if(month.equals("Mar"))
			return 3;
		else if(month.equals("Apr"))
			return 4;
		else if(month.equals("May"))
			return 5;
		else if(month.equals("Jun"))
			return 6;
		else if(month.equals("Jul"))
			return 7;
		else if(month.equals("Aug"))
			return 8;
		else if(month.equals("Sep"))
			return 9;
		else if(month.equals("Oct"))
			return 10;
		else if(month.equals("Nov"))
			return 11;
		else if(month.equals("Dec"))
			return 12;
		else
			return -1;
	}


	/**
	 * 格式化时间
	 * @param strdate UTC时间 "Mon Mar 26 09:28:48 +0000 2012"
	 * @return 北京时间 "2012-03-26 17:28:48"
	 */
	public static String getStrDate(String strdate)
	{
		String strTmp;
		int year,month,day,hour,minute,second;
		
		strTmp = strdate.substring(26,30);
		year = Integer.parseInt(strTmp);
		strTmp = strdate.substring(4,7);
		month = getMonth(strTmp);
		strTmp = strdate.substring(8,10);
		day = Integer.parseInt(strTmp);
		strTmp = strdate.substring(11,13);
		hour = Integer.parseInt(strTmp);
		strTmp = strdate.substring(14,16);
		minute = Integer.parseInt(strTmp);
		strTmp = strdate.substring(17,19);
		second = Integer.parseInt(strTmp);
		
		Calendar calendar = new GregorianCalendar(year,month-1,day,hour,minute,second);
		calendar.add(Calendar.HOUR_OF_DAY, 8);
		SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		return format.format(calendar.getTime());
	}


	/**
	 * 调用 GET /statuses/mentions 显示回复/提到当前用户的60条消息
	 * @param pageID 指定返回结果的页码
	 * @return
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/statuses.mentions
	 */
	@SuppressWarnings("deprecation")
	public HttpURLConnection mention(int page)
	{
		long timestamp = System.currentTimeMillis() / 1000;
		long nonce = System.nanoTime();
		String strURL = "http://api.fanfou.com/statuses/mentions.json";
		String pageID = String.valueOf(page);
		
		StringBuffer strBuf = new StringBuffer(200);
		strBuf.append("count=60");
		strBuf.append("&oauth_consumer_key=").append(consumer_key);
		strBuf.append("&oauth_nonce=").append(nonce);
		strBuf.append("&oauth_signature_method=HMAC-SHA1");
		strBuf.append("&oauth_timestamp=").append(timestamp);
		strBuf.append("&oauth_token=").append(oauth_token);
		String params = strBuf.toString();
		
	
		params = params + "&page=" + pageID;
	
		params = "GET&" + URLEncoder.encode(strURL)
					+ "&" + URLEncoder.encode(params);
		String sig = generateSignature(params,oauth_token_secret);
	
		strBuf = new StringBuffer(280); 
		strBuf.append("OAuth realm=\"Fantalker\",oauth_consumer_key=\"");
		strBuf.append(consumer_key);
		strBuf.append("\",oauth_signature_method=\"HMAC-SHA1\"");
		strBuf.append(",oauth_timestamp=\"").append(timestamp).append("\"");
		strBuf.append(",oauth_nonce=\"").append(nonce).append("\"");
		strBuf.append(",oauth_signature=\"").append(sig).append("\"");
		strBuf.append(",oauth_token=\"").append(oauth_token).append("\"");
		String authorization =  strBuf.toString();
		
		strURL = strURL + "?count=60&page=" + pageID;
		try {
			URL url = new URL(strURL);
			HttpURLConnection connection;
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.addRequestProperty("Authorization",authorization);
			connection.connect();
			return connection;
		} catch (SocketTimeoutException e) {
			setLog("连接超时 " + e.getMessage());
		} catch (IOException e) {
			setLog(e.getMessage());
		}
		return null;
	}


	/**
	 * 从photourl中读取图片并保持到outpath指示的目录下
	 * @param photourl 图片URL地址
	 * @param outpath 目标文件夹名称
	 * @return 图片文件名
	 */
	public String savePhoto(String photourl, String outpath)
	{
		URL url;
		String photo;
		BufferedInputStream bfis;
		try {
			url = new URL(photourl);

			int index = photourl.lastIndexOf("/");
			photo = photourl.substring(index+1, photourl.length());
	
			bfis = new BufferedInputStream(url.openStream());
		} catch (IOException e) {
			setLog(e.getMessage());
			return "";
		}

		File file = new File(outpath + "\\" + photo);
		byte[] bytes = new byte[100];
		OutputStream outstream;
		
		try {
			outstream = new FileOutputStream(file);
			int len;
			len = bfis.read(bytes);
			while(len>0)
			{
			    outstream.write(bytes,0,len);
			    len = bfis.read(bytes);
			}
			bfis.close();
			outstream.flush();
			outstream.close();
			picnum++;
			setLog("成功保存" + String.valueOf(picnum) + "张图片");
			return photo;
		} catch (FileNotFoundException e) {
			setLog(e.getMessage());
			return "";
		} catch (IOException e) {
			setLog(e.getMessage());
			return "";
		}
	}

	
	/**
     * 在txtLog中显示日志
     * @param str
     */
    public static void setLog(String str)
    {
		String log = Main.txtLog.getText();
		log = log + "\r\n" + str;
		Main.txtLog.setText(log);
		int length = Main.txtLog.getText().length();
		Main.txtLog.select(length, length);
    }
    
    
	/**
	 * 调用 GET /statuses/user_timeline 浏览指定用户已发送消息
	 * @param pageID 指定返回结果的页码
	 * @param userID 指定用户ID
	 * @return
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/statuses.user-timeline
	 */
	@SuppressWarnings("deprecation")
	public HttpURLConnection timeline(int page, String userID)
	{
		long timestamp = System.currentTimeMillis() / 1000;
		long nonce = System.nanoTime();
		String strURL = "http://api.fanfou.com/statuses/user_timeline.json";
		String pageID = String.valueOf(page);
		
		StringBuffer strBuf = new StringBuffer(200);
		strBuf.append("count=60");
		if(userID != null)
		{
			strBuf.append("&id=").append(userID);
		}
		strBuf.append("&oauth_consumer_key=").append(consumer_key);
		strBuf.append("&oauth_nonce=").append(nonce);
		strBuf.append("&oauth_signature_method=HMAC-SHA1");
		strBuf.append("&oauth_timestamp=").append(timestamp);
		strBuf.append("&oauth_token=").append(oauth_token);
		String params = strBuf.toString();
		

		params = params + "&page=" + pageID;

		params = "GET&" + URLEncoder.encode(strURL)
					+ "&" + URLEncoder.encode(params);
		String sig = generateSignature(params,oauth_token_secret);

		strBuf = new StringBuffer(280); 
		strBuf.append("OAuth realm=\"Fantalker\",oauth_consumer_key=\"");
		strBuf.append(consumer_key);
		strBuf.append("\",oauth_signature_method=\"HMAC-SHA1\"");
		strBuf.append(",oauth_timestamp=\"").append(timestamp).append("\"");
		strBuf.append(",oauth_nonce=\"").append(nonce).append("\"");
		strBuf.append(",oauth_signature=\"").append(sig).append("\"");
		strBuf.append(",oauth_token=\"").append(oauth_token).append("\"");
		String authorization =  strBuf.toString();
		
		strURL = strURL + "?count=60&page=" + pageID;
		if(userID != null)
		{
			strURL = strURL + "&id=" + userID;
		}
		try {
			URL url = new URL(strURL);
			HttpURLConnection connection;
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.addRequestProperty("Authorization",authorization);
			connection.connect();
			return connection;
		} catch (SocketTimeoutException e) {
			setLog("连接超时 " + e.getMessage());
		} catch (IOException e) {
			setLog(e.getMessage());
		}
		return null;
	}
    
	
	/**
	 * 调用 GET /statuses/user_timeline 浏览指定用户已发送消息
	 * @param pageID 指定返回结果的页码
	 * @return
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/statuses.user-timeline
	 */
	public HttpURLConnection timeline(int page)
	{
		return timeline(page,null);
	}
    
	
	/**
	 * 完成XAuth绑定
	 * @param username
	 * @param password
	 * @return oauth_token=Access_Token&oauth_token_secret=Access_Token_Secret
	 */
	@SuppressWarnings("deprecation")
	public String XAuth(String username, String password)
	{
		URL url;
		try {
			url = new URL("http://fanfou.com/oauth/access_token");
		} catch (MalformedURLException e) 
		{
			setLog(e.getMessage());
			return null;
		}
		long timestamp = System.currentTimeMillis() / 1000;
		long nonce = System.nanoTime();
		String params;
		String authorization;
		params = "oauth_consumer_key=" + consumer_key
				+ "&oauth_nonce=" + String.valueOf(nonce)
				+ "&oauth_signature_method=HMAC-SHA1"
				+ "&oauth_timestamp=" + String.valueOf(timestamp)
				+ "&x_auth_username=" + username
				+ "&x_auth_password=" + password
				+ "&x_auth_mode=client_auth";
	
		params = "GET&" + URLEncoder.encode(url.toString())
					+ "&" + URLEncoder.encode(params);
		String sig = generateSignature(params);
		
		authorization = "OAuth realm=\"Fantalker\",oauth_consumer_key=\"" + consumer_key
					+ "\",oauth_signature_method=\"HMAC-SHA1\""
					+ ",oauth_timestamp=\"" + String.valueOf(timestamp) + "\""
					+ ",oauth_nonce=\"" + String.valueOf(nonce) + "\""
					+ ",oauth_signature=\"" + sig + "\""
					+ ",x_auth_username=\"" + username + "\""
					+ ",x_auth_password=\"" + password + "\""
					+ ",x_auth_mode=\"client_auth\"";
		
		try {
			HttpURLConnection connection;
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.addRequestProperty("Authorization",authorization);
			connection.connect();
			
	
			String line;
			if(connection.getResponseCode() == 200)
			{
				
			}
			else if(connection.getResponseCode() == 401)
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				String log = Main.txtLog.getText();
				line = reader.readLine();
				while(line != null)
				{
					log = log + "\r\n" + line;
					line = reader.readLine();
				}
				Main.txtLog.setText(log);
				JOptionPane.showMessageDialog(null, "用户id或密码错误", "错误", JOptionPane.ERROR_MESSAGE);
				return null;
			}
			else
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				String log = Main.txtLog.getText();
				line = reader.readLine();
				while(line != null)
				{
					log = log + "\r\n" + line;
					line = reader.readLine();
				}
				setLog(log);
				JOptionPane.showMessageDialog(null, "未知错误，请重试", "错误", JOptionPane.ERROR_MESSAGE);
				return null;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			line = reader.readLine();
			return line;
		} catch (SocketTimeoutException e) {
			setLog("连接超时，请重试:" + e.getMessage());
			return null;
		} catch (IOException e) {
			setLog("创建输入流时发生 I/O错误:" + e.getMessage());
			return null;
		}
	}


	/**
	 * 写文件
	 * @param line 读取到的JSON内容
	 * @param format 格式 1csv 2xml 3html
	 * @param page 页码
	 */
	public void writeFile(String line, int format, int page)
	{
		String created_at;
		String id;
		String text;
		String source;
		JSONObject userjson;
		String userid;
		String screen_name;
		String description;
		String profile_image_url;
		String location;
		JSONObject photojson;
		String photourl = "";
		String filename;
		String fname = null;	//文件名(不含扩展)
		@SuppressWarnings("unused")
		String picname;
		/* 由JSON中分离信息并写入文件 */
		try {
			JSONArray jsonarr = new JSONArray(line);
			int arrlen = jsonarr.length();
			if(arrlen == 0)
			{
				setLog("\r\n完成导出\r\n共导出" + String.valueOf(num) + "条消息");
				exitTread();
				Main.export.interrupt();
	    		return;
			}
			
			if(format == 3)
			{
				DateFormat df = new SimpleDateFormat("yyyyMMdd");
		        StringBuffer sb = new StringBuffer(20);
				if(Main.rdbtnFriTL.isSelected())
				{
					sb.append(df.format(new Date())).append(Main.txtFriID.getText());
					fname = sb.toString();
					sb.append("_").append(page).append(".html");
				}
				else
				{
			        sb.append(df.format(new Date()));
					sb.append(Main.txtUsr.getText());
					fname = sb.toString();
			        sb.append("_").append(page).append(".html");
				}
				filename = filepath + "/" + sb.toString();
				try {
					outstream = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename),"gb2312"));
					outstream.println("<html>");
					outstream.println("<head>");
					outstream.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">");
					outstream.println("<title>" + filename + "</title>");
					outstream.println("</head>");
					outstream.println("<body>");
					outstream.print("<a href=\"" + fname + "_" + String.valueOf(page-1) + ".html\">上一页</a>  ");
					outstream.println("<a href=\"" + fname + "_" + String.valueOf(page+1) + ".html\">下一页</a><br /><hr><br />");
				} catch (FileNotFoundException e) {
					setLog(e.getMessage());
			    	Main.isStart = false;
			    	Main.btnStart.setText("开始");
			    	Main.export.interrupt();
					return;
				} catch (UnsupportedEncodingException e) {
					setLog(e.getMessage());
			    	Main.isStart = false;
			    	Main.btnStart.setText("开始");
			    	Main.export.interrupt();
					return;
				}
			}
			
			JSONObject[] json = new JSONObject[61];
			for(int j=0;j<arrlen;j++)
			{
				json[j] = jsonarr.getJSONObject(j);
				created_at = json[j].getString("created_at");
				created_at = getStrDate(created_at);
				id = json[j].getString("id");
				text = json[j].getString("text");
				source = json[j].getString("source");
				userjson = json[j].getJSONObject("user");
	
				userid = userjson.getString("id");
				screen_name = userjson.getString("screen_name");
				description = userjson.getString("description");
				profile_image_url = userjson.getString("profile_image_url");
				location = userjson.getString("location");
				
				photourl = "";
				try {
					photojson = json[j].getJSONObject("photo");
					photourl = photojson.getString("largeurl");
					if(exportPic)
					{
						picname = savePhoto(photourl,picpath);
					}
				} catch (JSONException e){
					
				}
				
				if(format == 1)		//csv
				{					
					StringBuffer sb = new StringBuffer(500);
					sb.append(created_at).append(",\"").append(text).append("\",");
					sb.append(id).append(",").append(source).append(",");
					sb.append(photourl).append(",");
					sb.append(userid).append(",").append(screen_name).append(",\"");
					sb.append(description).append("\",").append(profile_image_url).append(",");
					sb.append(location);
					outstream.println(sb.toString());
				}
				else if(format == 2)	//xml
				{
					outstream.println("  <status>");
					outstream.println("    <created_at>" + created_at + "</created_at>");
					outstream.println("    <text>");
					outstream.println("      " + text);
					outstream.println("    </text>");
					outstream.println("    <id>" + id + "</id>");
					outstream.println("    <source>");
					outstream.println("      " + source);
					outstream.println("    </source>");
					if(photourl != "")
					{
						outstream.println("    <photo>" + photourl + "</photo>");
					}
					outstream.println("    <user>");
					outstream.println("      <user_id>" + userid + "</user_id>");
					outstream.println("      <screen_name>" + screen_name + "</screen_name>");
					outstream.println("      <description>" + description + "</description>");
					outstream.println("      <profile_image_url>" + profile_image_url + "</profile_image_url>");
					outstream.println("      <location>" + location + "</location>");
					outstream.println("    </user>");
					outstream.println("  </status>");
				}
				else	//html
				{
					outstream.println("<b>" + screen_name + "</b> " + userid);
					outstream.println("<br />");
					outstream.println("<small>" + description + "</small>");
					outstream.println("<br />");
					outstream.println("<small><i>" + location + "</i></small>");
					outstream.println("<br /><br />");
					outstream.println(text);
					outstream.println("<br /><br />");
					if(photourl != "")
					{
						outstream.println("<img src=\"" + photourl + "\" /><br />");
					}
					outstream.println("<small>" + created_at + "</small>");
					outstream.println("<br /><small>" + "通过 " + source + "</small>");
					outstream.println("<br />");
					outstream.println("<small>id: " + id + "</small>");
					outstream.println("<br /><br />");
					outstream.println("<hr><br />");
				}
				
			}//for
			
			num = num + arrlen;
			setLog("完成导出"+ String.valueOf(num) + "条消息");
			errtime = 0;
	
		} catch (JSONException e) {
			errtime++;
			setLog("出现错误:\r\n" + e.getMessage() + "\r\n准备进行第" + errtime + "次重试");
		}
		if(format == 3)
		{
			outstream.print("<a href=\"" + fname + "_" + String.valueOf(page-1) + ".html\">上一页</a>  ");
			outstream.println("<a href=\"" + fname + "_" + String.valueOf(page+1) + ".html\">下一页</a><br />");
			outstream.println("</body>");
			outstream.println("</html>");
			outstream.close();
		}
	}


	/**
     * Computes RFC 2104-compliant HMAC signature.
	 * @author Yusuke Yamamoto - yusuke at mac.com
	 * @edit Unicorn-Feng
	 * @see <a href="http://oauth.net/core/1.0/">OAuth Core 1.0</a>
     * @param data the data to be signed
     * @param access token secret
     * @return signature
     * @see <a href="http://oauth.net/core/1.0/#rfc.section.9.2.1">OAuth Core - 9.2.1.  Generating Signature</a>
     */
    @SuppressWarnings("deprecation")
	public static String generateSignature(String data,String token) 
    {
        byte[] byteHMAC = null;
        try {
            Mac mac = Mac.getInstance(HMAC_SHA1);
            SecretKeySpec spec;
            if(token == null)
            {
            	String oauthSignature = encode(consumer_secret) + "&";
            	spec = new SecretKeySpec(oauthSignature.getBytes(), HMAC_SHA1);
            }
            else
            {
	            String oauthSignature = encode(consumer_secret) + "&" + encode(token);
	            spec = new SecretKeySpec(oauthSignature.getBytes(), HMAC_SHA1);
            }
            mac.init(spec);
            byteHMAC = mac.doFinal(data.getBytes());
        } catch (InvalidKeyException e) {
            setLog(e.getMessage());
        } catch (NoSuchAlgorithmException ignore) {
            // should never happen
        }
        return URLEncoder.encode(BASE64Encoder.encode(byteHMAC));
    }
    
    
    public static String generateSignature(String data)
    {
    	return generateSignature(data,null);
    }
    
    
    /**
     * @author Yusuke Yamamoto - yusuke at mac.com
     * @see <a href="http://oauth.net/core/1.0/">OAuth Core 1.0</a>
     * @param value string to be encoded
     * @return encoded string
     * @see <a href="http://wiki.oauth.net/TestCases">OAuth / TestCases</a>
     * @see <a href="http://groups.google.com/group/oauth/browse_thread/thread/a8398d0521f4ae3d/9d79b698ab217df2?hl=en&lnk=gst&q=space+encoding#9d79b698ab217df2">Space encoding - OAuth | Google Groups</a>
     * @see <a href="http://tools.ietf.org/html/rfc3986#section-2.1">RFC 3986 - Uniform Resource Identifier (URI): Generic Syntax - 2.1. Percent-Encoding</a>
     */
    public static String encode(String value) {
        String encoded = null;
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }
        StringBuffer buf = new StringBuffer(encoded.length());
        char focus;
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                buf.append("%2A");
            } else if (focus == '+') {
                buf.append("%20");
            } else if (focus == '%' && (i + 1) < encoded.length()
                    && encoded.charAt(i + 1) == '7' && encoded.charAt(i + 2) == 'E') {
                buf.append('~');
                i += 2;
            } else {
                buf.append(focus);
            }
        }
        return buf.toString();
    }
    
}
