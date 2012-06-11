/*
 * Copyright 2012, Unicorn-Feng
 * All rights reserved.
 * 
 * This file is part of FanfouExporter.
 * Fantalker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * Fantalker is distributed in the hope that it will be useful,
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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


/**
 * 主类
 * @author 烽麒 Unicorn-Feng
 * @link http://fq.vc
 */
public class Main
{
	private static ExportTread export = new ExportTread();
	
	/**
	 * @wbp.nonvisual location=207,147
	 */
	private static JPanel panelTop = new JPanel();
	private static Frame frame;
	private static ButtonGroup buttongroup;
	
	public static JButton btnStart;
	public static JRadioButton rdbtnCSV;
	public static JTextArea txtLog;
	public static JTextField txtUsr;
	public static JTextField txtFileName;
	public static JTextField txtPwd;
	public static boolean isStart = false;
	
	public Main()
	{
		frame = new Frame("饭否消息导出工具");
		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() 
		{
			@Override
			public void windowClosing(WindowEvent arg0) 
			{
				System.exit(1);
			}
		});
		frame.setLocationRelativeTo(null);
		frame.setSize(500, 400);
		frame.add(panelTop);
		SpringLayout sl_panelTop = new SpringLayout();
		panelTop.setLayout(sl_panelTop);
		
		JLabel lblUsr = new JLabel("用户id");
		sl_panelTop.putConstraint(SpringLayout.NORTH, lblUsr, 10, SpringLayout.NORTH, panelTop);
		lblUsr.setFont(new Font("宋体", Font.PLAIN, 14));
		panelTop.add(lblUsr);
		
		JLabel lblPwd = new JLabel("密码");
		sl_panelTop.putConstraint(SpringLayout.NORTH, lblPwd, 6, SpringLayout.SOUTH, lblUsr);
		sl_panelTop.putConstraint(SpringLayout.WEST, lblPwd, 10, SpringLayout.WEST, panelTop);
		lblPwd.setFont(new Font("宋体", Font.PLAIN, 14));
		panelTop.add(lblPwd);
		
		JLabel lblFileNmae = new JLabel("文件名");
		sl_panelTop.putConstraint(SpringLayout.NORTH, lblFileNmae, 6, SpringLayout.SOUTH, lblPwd);
		sl_panelTop.putConstraint(SpringLayout.WEST, lblFileNmae, 0, SpringLayout.WEST, lblUsr);
		lblFileNmae.setFont(new Font("宋体", Font.PLAIN, 14));
		panelTop.add(lblFileNmae);
		
		JLabel lblFileType = new JLabel("文件格式");
		sl_panelTop.putConstraint(SpringLayout.WEST, lblFileType, 0, SpringLayout.WEST, lblUsr);
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
		
		txtFileName = new JTextField();
		sl_panelTop.putConstraint(SpringLayout.NORTH, txtFileName, 3, SpringLayout.SOUTH, txtPwd);
		sl_panelTop.putConstraint(SpringLayout.EAST, txtFileName, 0, SpringLayout.EAST, txtUsr);
		panelTop.add(txtFileName);
		txtFileName.setColumns(10);
		
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		StringBuffer sb = new StringBuffer(15);
		sb.append(df.format(new Date())).append(".csv");
		txtFileName.setText(sb.toString());
		
		rdbtnCSV = new JRadioButton(".csv");
		rdbtnCSV.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				onRadioButtonClick(1);
			}
		});
		rdbtnCSV.setSelected(true);
		sl_panelTop.putConstraint(SpringLayout.WEST, txtFileName, 0, SpringLayout.WEST, rdbtnCSV);
		sl_panelTop.putConstraint(SpringLayout.NORTH, lblFileType, 4, SpringLayout.NORTH, rdbtnCSV);
		sl_panelTop.putConstraint(SpringLayout.NORTH, rdbtnCSV, 4, SpringLayout.SOUTH, txtFileName);
		sl_panelTop.putConstraint(SpringLayout.WEST, rdbtnCSV, 14, SpringLayout.EAST, lblFileType);
		rdbtnCSV.setFont(new Font("宋体", Font.PLAIN, 14));
		panelTop.add(rdbtnCSV);
		
		JRadioButton rdbtnXML = new JRadioButton(".xml");
		rdbtnXML.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				onRadioButtonClick(2);
			}
		});
		sl_panelTop.putConstraint(SpringLayout.NORTH, rdbtnXML, -4, SpringLayout.NORTH, lblFileType);
		sl_panelTop.putConstraint(SpringLayout.WEST, rdbtnXML, 6, SpringLayout.EAST, rdbtnCSV);
		rdbtnXML.setFont(new Font("宋体", Font.PLAIN, 14));
		panelTop.add(rdbtnXML);
		
		buttongroup = new ButtonGroup();
		buttongroup.add(rdbtnCSV);
		buttongroup.add(rdbtnXML);
		
		btnStart = new JButton("开始");
		btnStart.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				onStartButtonClick();
			}
		});
		sl_panelTop.putConstraint(SpringLayout.NORTH, btnStart, 6, SpringLayout.SOUTH, lblFileType);
		sl_panelTop.putConstraint(SpringLayout.WEST, btnStart, 0, SpringLayout.WEST, lblUsr);
		panelTop.add(btnStart);
		
		txtLog = new JTextArea();
		txtLog.setText("饭否消息导出工具 v1.0\r\n发布日期:2012年6月11日\r\n作者：@烽麒\r\n网站：http://fq.vc\r\n");
		txtLog.setEditable(false);
		JScrollPane scroll = new JScrollPane(txtLog);
		sl_panelTop.putConstraint(SpringLayout.SOUTH, scroll, 234, SpringLayout.SOUTH, btnStart);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sl_panelTop.putConstraint(SpringLayout.NORTH, scroll, 6, SpringLayout.SOUTH, btnStart);
		sl_panelTop.putConstraint(SpringLayout.WEST, scroll, 10, SpringLayout.WEST, panelTop);
		sl_panelTop.putConstraint(SpringLayout.EAST, scroll, -10, SpringLayout.EAST, panelTop);
		panelTop.add(scroll);
		frame.setVisible(true);
	}
	
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException 
	{
		Main main = new Main();
	}
	
	
	/**
	 * 单选按钮被点击
	 * @param Num 1csv，2xml
	 */
	public static void onRadioButtonClick(int num)
	{
		if(txtFileName.getText().trim() == "" || txtFileName.getText().trim().isEmpty())
		{
			DateFormat df = new SimpleDateFormat("yyyyMMdd");
			StringBuffer sb = new StringBuffer(15);
			sb.append(df.format(new Date()));
			if(num == 1)
			{
				sb.append(".csv");
			}
			else
			{
				sb.append(".xml");
			}
			txtFileName.setText(sb.toString());
			return;
		}
		String filename = txtFileName.getText().trim();
		int length = filename.length();
		String extension = filename.substring(length-4,length).toLowerCase();
		if(num == 1)															//csv
		{
			if(extension.equals(".xml"))
			{
				filename = filename.substring(0,length-4) + ".csv";
			}
			else if(extension.equals(".csv"))
			{
				//继续执行
			}
			else
			{
				filename = filename + ".csv";
			}
			
		}
		else																	//xml
		{
			if(extension.equals(".csv"))
			{
				filename = filename.substring(0,length-4) + ".xml";
			}
			else if(extension.equals(".xml"))
			{
				//继续执行
			}
			else
			{
				filename = filename + ".xml";
			}
		}
		txtFileName.setText(filename);
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
			
			DateFormat df = new SimpleDateFormat("yyyyMMdd");
			StringBuffer sb = new StringBuffer(15);
			sb.append(df.format(new Date()));
			if(rdbtnCSV.isSelected())
			{
				sb.append(".csv");
			}
			else
			{
				sb.append(".xml");
			}
			txtFileName.setText(sb.toString());
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
