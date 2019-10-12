import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.*;
import java.util.HashSet;
import java.util.Iterator;

public class Spider extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JPanel jpl=new JPanel();
	private JMenuBar bar=new JMenuBar();
	private JMenu jm=new JMenu("操作选项");
	
	private JButton urlType = new JButton("提取网页文字");
	//private JButton urlToText = new JButton("提取文字");
	private JButton createLibrary = new JButton("添加敏感词");
	private JButton highLight = new JButton("高亮敏感词");
	private JButton readTxt = new JButton("从文本读取网址并显示文字");
	
	private URL url = null;
	private URLConnection uc=null;
	private JTextArea jta=new JTextArea();
	private JScrollPane jsp=new JScrollPane(jta);
	
	HashSet sensitiveWords = new HashSet();//敏感词集合
	String[] urlSet = new String[10];
	
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==urlType) {
			String urlAddress=JOptionPane.showInputDialog("请输入要分析的网址");
			if(urlAddress!=null) {
				System.out.println("输入的网址为"+urlAddress);
				UrlAnalysis(urlAddress,"G:\\Html.txt");
			}else {
				JOptionPane.showMessageDialog(this,"请重新输入网址！",null,JOptionPane.OK_CANCEL_OPTION);
			}
			
		}else if(e.getSource()==createLibrary) {
			append("G:\\敏感词.txt","\n");
			String word=JOptionPane.showInputDialog("请输入要新增加的敏感字");
			sensitiveWords.add(word);
			append("G:\\敏感词.txt",word);
		    System.out.println("敏感词【"+word+"】已添加");	

		}else if(e.getSource()==readTxt) {
			
			int clearConfirm = JOptionPane.showConfirmDialog(null,"是否清空文本框？","提示",JOptionPane.YES_NO_OPTION);
			if(clearConfirm == JOptionPane.YES_OPTION) {
				jta.setText("");
			}
			
			String multiURLs = "G:\\URLs.txt";
			File multiFileName = new File(multiURLs);
			int urlCount=0;
			try {
				InputStreamReader newisr = new InputStreamReader(new FileInputStream(multiFileName));
				BufferedReader newbr = new BufferedReader(newisr);
				String line = "";
				line=newbr.readLine();
				while(line!=null) {
					line = newbr.readLine();
					if(line!=null) System.out.println(line);
					urlSet[urlCount]=line;
					urlCount++;
				}
				System.out.println("共识别到"+(urlCount-1)+"个网址。");
				
				for(int i=0;i<urlCount+1;i++) {
					if(urlSet[i]!=null) {
						UrlAnalysis(urlSet[i],"G:\\多个网址源代码.txt");
						jta.append("\n\n");
					}
				}
			}catch(Exception e2) {
			}	
			
		}else if(e.getSource()==highLight) {
			//从敏感词文件中读出
			int readConfirm = JOptionPane.showConfirmDialog(null,"是否读取敏感词库？","提示",JOptionPane.YES_NO_OPTION);
			if(readConfirm == JOptionPane.YES_OPTION) {
				String name = "G:\\敏感词.txt";
				File fileName = new File(name);
				
				try {
					InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName));
					BufferedReader br = new BufferedReader(isr);
					String line = "";
					line=br.readLine();
					while(line!=null) {
						line = br.readLine();
						sensitiveWords.add(line);
					}
						
				}catch(Exception exp) {
				}	
			}
			highLightString();
		}
	}

	public void UrlAnalysis(String address,String filePath) {
		if(address!=null) {
			try {
				url=new URL(address);
				uc=url.openConnection();
				InputStream is=uc.getInputStream();
				BufferedReader br=new BufferedReader(new InputStreamReader(is,"UTF-8"));
				String str;
				
				StringBuilder sb = new StringBuilder();
				while((str=br.readLine())!=null) {
					sb.append(str);
					append("G:\\Html.txt",str);
				}

				jta.append(Html2Text(sb.toString()));
												

				JOptionPane.showMessageDialog(this,"网址"+address+"的Html源代码已经生成！",null,JOptionPane.OK_CANCEL_OPTION);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	public void highLightString() {
		try {
			Highlighter hl = jta.getHighlighter();
			String passage = jta.getText();
			DefaultHighlighter.DefaultHighlightPainter dhp = 
					new DefaultHighlighter.DefaultHighlightPainter(Color.PINK);
		    //敏感词
		    java.util.Iterator ite=sensitiveWords.iterator();
		    //System.out.print("敏感词");
		    while(ite.hasNext()) {
		    	String str = (String)ite.next();
		    	if(str!=null && str!="\n") {
			    	//System.out.println(str);
		    		int pos=0;
		    		while((pos=passage.indexOf(str, pos))>0) {
		    			hl.addHighlight(pos, pos+str.length(), dhp);
		    			pos = pos+str.length();
		    		}
		    	}
		    }
		}catch(Exception e) {
			
		}
	}
	
	//往文件中加字符串
	public static void append(String fileName,String content) {
		try {
			FileWriter writer = new FileWriter(fileName,true);
			writer.write(content+"\n");
			writer.close();
		}catch(Exception e) {
			
		}
	}
	
/*	//从html中提取纯文本	
	
	public String HtmlCodeToText(String strHtml) throws Exception{
		String txtcontent = strHtml.replaceAll("</?[^>]+>", ""); //剔出<html>的标签  
	    txtcontent = txtcontent.replaceAll("<a>\\s*|\t|\r|\n</a>", "");//去除字符串中的空格,回车,换行符,制表符  
	    return txtcontent;
	}*/
	
	//从html中提取纯文本
	public static String Html2Text(String inputString) {
			String htmlStr = inputString; // 含html标签的字符串
			String textStr = "";
			java.util.regex.Pattern p_script;
			java.util.regex.Matcher m_script;
			java.util.regex.Pattern p_style;
			java.util.regex.Matcher m_style;
			java.util.regex.Pattern p_html;
			java.util.regex.Matcher m_html;
			//comment.replaceAll("(emlog|高亮|span|color)", "<span style='background-color:red;'>$1</span>");
			try {
				String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
		        String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
		        String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
		        p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		        m_script = p_script.matcher(htmlStr);
		        htmlStr = m_script.replaceAll(""); // 过滤script标签
		        p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		        m_style = p_style.matcher(htmlStr);
		        htmlStr = m_style.replaceAll(""); // 过滤style标签
		        p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		        m_html = p_html.matcher(htmlStr);
		        htmlStr = m_html.replaceAll(""); // 过滤html标签
		        textStr = htmlStr;
		    } catch (Exception e){
		    	System.err.println("Html2Text: " + e.getMessage()); 
		      }
			//剔除空格行
			textStr=textStr.replaceAll("[ ]+", "");
			textStr=textStr.replaceAll("(?m)^\\s*$(\\n|\\r\\n)", "");
			return textStr;// 返回文本字符串
		}

	public Spider() throws Exception {
		this.add(jpl,BorderLayout.NORTH);
		this.add(jsp);
		
		jpl.add(urlType);
		jpl.add(createLibrary);
		jpl.add(highLight);
		jpl.add(readTxt);

		//按钮绑定
		urlType.addActionListener(this);
		createLibrary.addActionListener(this);
		readTxt.addActionListener(this);
		highLight.addActionListener(this);
			
		this.setTitle("余越开发的网络爬虫软件");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(jsp);
		this.setSize(1000,620);
		this.setLocation(100,100);
		this.setVisible(true);
		
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		
		jta.setEditable(false);
	}
	
	public static void main(String[] args) throws Exception{
		Spider spd = new Spider();
	}
}