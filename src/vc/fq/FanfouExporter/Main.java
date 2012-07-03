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

import java.awt.Frame;
import java.io.IOException;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;


/**
 * 主类
 * @author 烽麒 Unicorn-Feng
 * @link http://fq.vc
 */
public class Main
{
	
	/**
	 * @wbp.nonvisual location=207,147
	 */
	private static JPanel panelTop = new JPanel();
	private static Frame frame;
	private static ButtonGroup buttongroup;
	private static ButtonGroup buttongroup2;
	
	public static JButton btnStart;
	public static JRadioButton rdbtnCSV;
	public static JRadioButton rdbtnXML;
	public static JRadioButton rdbtnHTML;
	public static JTextArea txtLog;
	public static JTextField txtUsr;
	public static JTextField txtFilePath;
	public static JTextField txtPwd;
	public static JCheckBox chkbxPic;
	public static JTextField txtFriID;
	public static JRadioButton rdbtnUsrTL;
	public static JRadioButton rdbtnMention;
	public static JRadioButton rdbtnDM;
	public static JRadioButton rdbtnFriTL;
	public static boolean isStart = false;
	public static ExportTread export = new ExportTread();


	/**
	 * 生成图形界面
	 */
	public Main()
	{
		frame = new Frame("饭否消息导出工具");
		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) 
			{
				System.exit(1);
			}
		});
		frame.setLocationRelativeTo(null);
		frame.setSize(522, 427);
		frame.add(panelTop);
		SpringLayout sl_panelTop = new SpringLayout();
		panelTop.setLayout(sl_panelTop);
		
		JLabel lblUsr = new JLabel("用户id");
		sl_panelTop.putConstraint(SpringLayout.NORTH, lblUsr, 10, SpringLayout.NORTH, panelTop);
		lblUsr.setFont(new Font("宋体", Font.PLAIN, 14));
		panelTop.add(lblUsr);
		
		JLabel lblPwd = new JLabel("密码");
		sl_panelTop.putConstraint(SpringLayout.WEST, lblPwd, 10, SpringLayout.WEST, panelTop);
		lblPwd.setFont(new Font("宋体", Font.PLAIN, 14));
		panelTop.add(lblPwd);
		
		JLabel lblFileNmae = new JLabel("存储路径");
		sl_panelTop.putConstraint(SpringLayout.NORTH, lblFileNmae, 54, SpringLayout.NORTH, panelTop);
		sl_panelTop.putConstraint(SpringLayout.SOUTH, lblPwd, -6, SpringLayout.NORTH, lblFileNmae);
		sl_panelTop.putConstraint(SpringLayout.WEST, lblFileNmae, 10, SpringLayout.WEST, panelTop);
		lblFileNmae.setFont(new Font("宋体", Font.PLAIN, 14));
		panelTop.add(lblFileNmae);
		
		JLabel lblFileType = new JLabel("文件格式");
		sl_panelTop.putConstraint(SpringLayout.WEST, lblFileType, 10, SpringLayout.WEST, panelTop);
		lblFileType.setFont(new Font("宋体", Font.PLAIN, 14));
		panelTop.add(lblFileType);
		
		txtUsr = new JTextField();
		sl_panelTop.putConstraint(SpringLayout.EAST, lblUsr, -28, SpringLayout.WEST, txtUsr);
		sl_panelTop.putConstraint(SpringLayout.NORTH, txtUsr, 7, SpringLayout.NORTH, panelTop);
		sl_panelTop.putConstraint(SpringLayout.WEST, txtUsr, 80, SpringLayout.WEST, panelTop);
		sl_panelTop.putConstraint(SpringLayout.EAST, txtUsr, -10, SpringLayout.EAST, panelTop);
		panelTop.add(txtUsr);
		txtUsr.setColumns(10);
		
		txtPwd = new JTextField();
		sl_panelTop.putConstraint(SpringLayout.EAST, txtPwd, 0, SpringLayout.EAST, txtUsr);
		sl_panelTop.putConstraint(SpringLayout.SOUTH, txtUsr, -1, SpringLayout.NORTH, txtPwd);
		sl_panelTop.putConstraint(SpringLayout.NORTH, txtPwd, -2, SpringLayout.NORTH, lblPwd);
		sl_panelTop.putConstraint(SpringLayout.WEST, txtPwd, 42, SpringLayout.EAST, lblPwd);
		panelTop.add(txtPwd);
		txtPwd.setColumns(10);
		
		txtFilePath = new JTextField();
		sl_panelTop.putConstraint(SpringLayout.NORTH, txtFilePath, 3, SpringLayout.SOUTH, txtPwd);
		sl_panelTop.putConstraint(SpringLayout.WEST, txtFilePath, 0, SpringLayout.WEST, txtUsr);
		panelTop.add(txtFilePath);
		txtFilePath.setColumns(10);
		
		String dir = System.getProperty("user.dir");
		txtFilePath.setText(dir);
		
		rdbtnCSV = new JRadioButton(".csv");
		rdbtnCSV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				onRadioButtonClick(1);
			}
		});
		
		JButton btnBrowse = new JButton("浏览");
		btnBrowse.setEnabled(false);
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				
			}
		});
		sl_panelTop.putConstraint(SpringLayout.EAST, txtFilePath, -6, SpringLayout.WEST, btnBrowse);
		sl_panelTop.putConstraint(SpringLayout.NORTH, btnBrowse, -2, SpringLayout.NORTH, lblFileNmae);
		sl_panelTop.putConstraint(SpringLayout.EAST, btnBrowse, 0, SpringLayout.EAST, txtUsr);
		panelTop.add(btnBrowse);
		sl_panelTop.putConstraint(SpringLayout.NORTH, lblFileType, 4, SpringLayout.NORTH, rdbtnCSV);
		sl_panelTop.putConstraint(SpringLayout.WEST, rdbtnCSV, 14, SpringLayout.EAST, lblFileType);
		rdbtnCSV.setFont(new Font("宋体", Font.PLAIN, 14));
		panelTop.add(rdbtnCSV);
		
		rdbtnXML = new JRadioButton(".xml");
		rdbtnXML.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				onRadioButtonClick(2);
			}
		});
		sl_panelTop.putConstraint(SpringLayout.NORTH, rdbtnXML, -4, SpringLayout.NORTH, lblFileType);
		sl_panelTop.putConstraint(SpringLayout.WEST, rdbtnXML, 6, SpringLayout.EAST, rdbtnCSV);
		rdbtnXML.setFont(new Font("宋体", Font.PLAIN, 14));
		panelTop.add(rdbtnXML);
		
		rdbtnHTML = new JRadioButton(".html");
		rdbtnHTML.setSelected(true);
		rdbtnHTML.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				onRadioButtonClick(3);
			}
		});
		sl_panelTop.putConstraint(SpringLayout.NORTH, rdbtnHTML, -3, SpringLayout.NORTH, lblFileType);
		sl_panelTop.putConstraint(SpringLayout.WEST, rdbtnHTML, 6, SpringLayout.EAST, rdbtnXML);
		rdbtnHTML.setFont(new Font("宋体", Font.PLAIN, 14));
		panelTop.add(rdbtnHTML);
		
		buttongroup = new ButtonGroup();
		buttongroup.add(rdbtnCSV);
		buttongroup.add(rdbtnXML);
		buttongroup.add(rdbtnHTML);

		btnStart = new JButton("开始");
		sl_panelTop.putConstraint(SpringLayout.WEST, btnStart, 0, SpringLayout.WEST, lblUsr);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				onStartButtonClick();
			}
		});
		panelTop.add(btnStart);
		
		txtLog = new JTextArea();
		txtLog.setText("饭否消息导出工具 v2.1.1\r\n发布日期:2012年7月4日\r\n作者：@烽麒\r\n网站：http://fq.vc\r\n");
		txtLog.setEditable(false);
		JScrollPane scroll = new JScrollPane(txtLog);
		sl_panelTop.putConstraint(SpringLayout.NORTH, scroll, 166, SpringLayout.NORTH, panelTop);
		sl_panelTop.putConstraint(SpringLayout.SOUTH, btnStart, -6, SpringLayout.NORTH, scroll);
		sl_panelTop.putConstraint(SpringLayout.WEST, scroll, 0, SpringLayout.WEST, lblUsr);
		sl_panelTop.putConstraint(SpringLayout.SOUTH, scroll, -32, SpringLayout.SOUTH, panelTop);
		sl_panelTop.putConstraint(SpringLayout.EAST, scroll, 0, SpringLayout.EAST, txtUsr);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		panelTop.add(scroll);
		
		chkbxPic = new JCheckBox("导出图片");
		chkbxPic.setSelected(true);
		chkbxPic.setEnabled(false);
		sl_panelTop.putConstraint(SpringLayout.NORTH, chkbxPic, -4, SpringLayout.NORTH, lblFileType);
		sl_panelTop.putConstraint(SpringLayout.WEST, chkbxPic, 6, SpringLayout.EAST, rdbtnHTML);
		chkbxPic.setFont(new Font("宋体", Font.PLAIN, 14));
		panelTop.add(chkbxPic);
		
		JLabel lblExpType = new JLabel("导出内容");
		sl_panelTop.putConstraint(SpringLayout.NORTH, lblExpType, 9, SpringLayout.SOUTH, lblFileType);
		sl_panelTop.putConstraint(SpringLayout.EAST, lblExpType, 0, SpringLayout.EAST, lblFileType);
		lblExpType.setFont(new Font("宋体", Font.PLAIN, 14));
		panelTop.add(lblExpType);
		
		rdbtnUsrTL = new JRadioButton("已发消息");
		rdbtnUsrTL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				onRadioButton2Click(1);
			}
		});
		rdbtnUsrTL.setSelected(true);
		sl_panelTop.putConstraint(SpringLayout.NORTH, rdbtnUsrTL, 105, SpringLayout.NORTH, panelTop);
		sl_panelTop.putConstraint(SpringLayout.SOUTH, rdbtnCSV, -1, SpringLayout.NORTH, rdbtnUsrTL);
		sl_panelTop.putConstraint(SpringLayout.WEST, rdbtnUsrTL, 0, SpringLayout.WEST, txtUsr);
		rdbtnUsrTL.setFont(new Font("宋体", Font.PLAIN, 12));
		panelTop.add(rdbtnUsrTL);
		
		rdbtnMention = new JRadioButton("@提到我的消息");
		rdbtnMention.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				onRadioButton2Click(2);
			}
		});
		sl_panelTop.putConstraint(SpringLayout.NORTH, rdbtnMention, 0, SpringLayout.NORTH, rdbtnUsrTL);
		sl_panelTop.putConstraint(SpringLayout.WEST, rdbtnMention, 0, SpringLayout.EAST, rdbtnUsrTL);
		rdbtnMention.setFont(new Font("宋体", Font.PLAIN, 12));
		panelTop.add(rdbtnMention);
		
		rdbtnDM = new JRadioButton("私信");
		rdbtnDM.setEnabled(false);
		rdbtnDM.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				onRadioButton2Click(3);
			}
		});
		sl_panelTop.putConstraint(SpringLayout.NORTH, rdbtnDM, 0, SpringLayout.NORTH, rdbtnUsrTL);
		sl_panelTop.putConstraint(SpringLayout.WEST, rdbtnDM, -10, SpringLayout.WEST, chkbxPic);
		rdbtnDM.setFont(new Font("宋体", Font.PLAIN, 12));
		panelTop.add(rdbtnDM);
		
		rdbtnFriTL = new JRadioButton("指定好友已发消息");
		rdbtnFriTL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				onRadioButton2Click(4);
			}
		});
		sl_panelTop.putConstraint(SpringLayout.NORTH, rdbtnFriTL, -3, SpringLayout.NORTH, lblExpType);
		sl_panelTop.putConstraint(SpringLayout.WEST, rdbtnFriTL, 0, SpringLayout.EAST, rdbtnDM);
		rdbtnFriTL.setFont(new Font("宋体", Font.PLAIN, 12));
		panelTop.add(rdbtnFriTL);
		
		buttongroup2 = new ButtonGroup();
		buttongroup2.add(rdbtnUsrTL);
		buttongroup2.add(rdbtnMention);
		buttongroup2.add(rdbtnDM);
		buttongroup2.add(rdbtnFriTL);
		
		txtFriID = new JTextField();
		txtFriID.setEnabled(false);
		sl_panelTop.putConstraint(SpringLayout.NORTH, txtFriID, -2, SpringLayout.NORTH, lblExpType);
		sl_panelTop.putConstraint(SpringLayout.WEST, txtFriID, 0, SpringLayout.EAST, rdbtnFriTL);
		sl_panelTop.putConstraint(SpringLayout.EAST, txtFriID, 0, SpringLayout.EAST, txtUsr);
		txtFriID.setText("好友ID");
		txtFriID.setToolTipText("好友ID");
		panelTop.add(txtFriID);
		txtFriID.setColumns(10);
		
		frame.setVisible(true);
	}
	
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException 
	{
		Main main = new Main();
	}
	
	
	/**
	 * 单选按钮被点击
	 * @param Num 1csv，2xml. 3html
	 */
	public static void onRadioButtonClick(int num)
	{
		if(num == 3)
		{
			chkbxPic.setEnabled(false);
			chkbxPic.setSelected(true);
		}
		else
		{
			chkbxPic.setEnabled(true);
		}
	}
	
	
	/**
	 * 导出内容单选按钮点击
	 * @param num 1已发 2@ 3私信 4指定好友
	 */
	public static void onRadioButton2Click(int num)
	{
		if(num == 4)
		{
			txtFriID.setEnabled(true);
		}
		else
		{
			txtFriID.setEnabled(false);
		}
	}
	
	
	/**
	 * 开始按钮点击
	 */
	public static void onStartButtonClick()
	{
		if(isStart == false)
		{
			if(txtUsr.getText().trim() == "" || txtUsr.getText().trim().isEmpty() || txtPwd.getText().trim() == "" || txtPwd.getText().trim().isEmpty())
			{
				JOptionPane.showMessageDialog(null, "请输入用户id及密码", "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(txtFilePath.getText().trim() == "" || txtFilePath.getText().trim().isEmpty())
			{
				String dir = System.getProperty("user.dir");
				txtFilePath.setText(dir);
			}
			if(rdbtnFriTL.isSelected())
			{
				if(txtFriID.getText().trim() == "" || txtFriID.getText().trim().isEmpty() || txtFriID.getText().trim().equals("好友ID"))
				{
					JOptionPane.showMessageDialog(null, "请输入好友ID", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			isStart = true;
			btnStart.setText("停止");
			export.interrupt();
			export = new ExportTread();
			export.start();
		}
		else
		{
			export.interrupt();
		}
	}
}
