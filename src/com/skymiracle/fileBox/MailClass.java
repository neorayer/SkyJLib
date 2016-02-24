package com.skymiracle.fileBox;
import com.skymiracle.mdo4.*;

public class MailClass extends Dao{

	public static final String[] labels = {"name", "folderName", "target", "op", "keyWord"};
	
	private String op;
	
	private String mailClassName;

	private String mailClassFolderName;

	private String target;
	
	private String keyWord;
	
	public MailClass() {
		super();
	}
	
	public MailClass(String mailClassName, String mailClassFolderName,
			String target, String op, String keyWord) {

		this.mailClassName = mailClassName;

		this.mailClassFolderName = mailClassFolderName;

		this.target = target;
		
		this.op = op;
		
		this.keyWord = keyWord;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public String getMailClassFolderName() {
		return mailClassFolderName;
	}

	public String getMailClassName() {
		return mailClassName;
	}

	public String getOp() {
		return op;
	}

	public String getTarget() {
		return target;
	}

	public void setMailClassName(String mailClassName) {
		this.mailClassName = mailClassName;
	}

	public void setMailClassFolderName(String mailClassFolderName) {
		this.mailClassFolderName = mailClassFolderName;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	@Override
	public String fatherDN(String baseDN) throws NullKeyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] keyNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] objectClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String selfDN() throws NullKeyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String table() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
