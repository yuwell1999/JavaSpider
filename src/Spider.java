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
	private JMenu jm=new JMenu("����ѡ��");
	
	private JButton urlType = new JButton("��ȡ��ҳ����");
	//private JButton urlToText = new JButton("��ȡ����");
	private JButton createLibrary = new JButton("������д�");
	private JButton highLight = new JButton("�������д�");
	private JButton readTxt = new JButton("���ı���ȡ��ַ����ʾ����");
	
	private URL url = null;
	private URLConnection uc=null;
	private JTextArea jta=new JTextArea();
	private JScrollPane jsp=new JScrollPane(jta);
	
	HashSet sensitiveWords = new HashSet();//���дʼ���
	String[] urlSet = new String[10];
	
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==urlType) {
			String urlAddress=JOptionPane.showInputDialog("������Ҫ��������ַ");
			if(urlAddress!=null) {
				System.out.println("�������ַΪ"+urlAddress);
				UrlAnalysis(urlAddress,"G:\\Html.txt");
			}else {
				JOptionPane.showMessageDialog(this,"������������ַ��",null,JOptionPane.OK_CANCEL_OPTION);
			}
			
		}else if(e.getSource()==createLibrary) {
			append("G:\\���д�.txt","\n");
			String word=JOptionPane.showInputDialog("������Ҫ�����ӵ�������");
			sensitiveWords.add(word);
			append("G:\\���д�.txt",word);
		    System.out.println("���дʡ�"+word+"�������");	

		}else if(e.getSource()==readTxt) {
			
			int clearConfirm = JOptionPane.showConfirmDialog(null,"�Ƿ�����ı���","��ʾ",JOptionPane.YES_NO_OPTION);
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
				System.out.println("��ʶ��"+(urlCount-1)+"����ַ��");
				
				for(int i=0;i<urlCount+1;i++) {
					if(urlSet[i]!=null) {
						UrlAnalysis(urlSet[i],"G:\\�����ַԴ����.txt");
						jta.append("\n\n");
					}
				}
			}catch(Exception e2) {
			}	
			
		}else if(e.getSource()==highLight) {
			//�����д��ļ��ж���
			int readConfirm = JOptionPane.showConfirmDialog(null,"�Ƿ��ȡ���дʿ⣿","��ʾ",JOptionPane.YES_NO_OPTION);
			if(readConfirm == JOptionPane.YES_OPTION) {
				String name = "G:\\���д�.txt";
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
												

				JOptionPane.showMessageDialog(this,"��ַ"+address+"��HtmlԴ�����Ѿ����ɣ�",null,JOptionPane.OK_CANCEL_OPTION);
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
		    //���д�
		    java.util.Iterator ite=sensitiveWords.iterator();
		    //System.out.print("���д�");
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
	
	//���ļ��м��ַ���
	public static void append(String fileName,String content) {
		try {
			FileWriter writer = new FileWriter(fileName,true);
			writer.write(content+"\n");
			writer.close();
		}catch(Exception e) {
			
		}
	}
	
/*	//��html����ȡ���ı�	
	
	public String HtmlCodeToText(String strHtml) throws Exception{
		String txtcontent = strHtml.replaceAll("</?[^>]+>", ""); //�޳�<html>�ı�ǩ  
	    txtcontent = txtcontent.replaceAll("<a>\\s*|\t|\r|\n</a>", "");//ȥ���ַ����еĿո�,�س�,���з�,�Ʊ��  
	    return txtcontent;
	}*/
	
	//��html����ȡ���ı�
	public static String Html2Text(String inputString) {
			String htmlStr = inputString; // ��html��ǩ���ַ���
			String textStr = "";
			java.util.regex.Pattern p_script;
			java.util.regex.Matcher m_script;
			java.util.regex.Pattern p_style;
			java.util.regex.Matcher m_style;
			java.util.regex.Pattern p_html;
			java.util.regex.Matcher m_html;
			//comment.replaceAll("(emlog|����|span|color)", "<span style='background-color:red;'>$1</span>");
			try {
				String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // ����script��������ʽ{��<script[^>]*?>[\\s\\S]*?<\\/script>
		        String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // ����style��������ʽ{��<style[^>]*?>[\\s\\S]*?<\\/style>
		        String regEx_html = "<[^>]+>"; // ����HTML��ǩ��������ʽ
		        p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		        m_script = p_script.matcher(htmlStr);
		        htmlStr = m_script.replaceAll(""); // ����script��ǩ
		        p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		        m_style = p_style.matcher(htmlStr);
		        htmlStr = m_style.replaceAll(""); // ����style��ǩ
		        p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		        m_html = p_html.matcher(htmlStr);
		        htmlStr = m_html.replaceAll(""); // ����html��ǩ
		        textStr = htmlStr;
		    } catch (Exception e){
		    	System.err.println("Html2Text: " + e.getMessage()); 
		      }
			//�޳��ո���
			textStr=textStr.replaceAll("[ ]+", "");
			textStr=textStr.replaceAll("(?m)^\\s*$(\\n|\\r\\n)", "");
			return textStr;// �����ı��ַ���
		}

	public Spider() throws Exception {
		this.add(jpl,BorderLayout.NORTH);
		this.add(jsp);
		
		jpl.add(urlType);
		jpl.add(createLibrary);
		jpl.add(highLight);
		jpl.add(readTxt);

		//��ť��
		urlType.addActionListener(this);
		createLibrary.addActionListener(this);
		readTxt.addActionListener(this);
		highLight.addActionListener(this);
			
		this.setTitle("��Խ�����������������");
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