package com.skymiracle.fileBox;

import java.util.LinkedList;
import java.util.List;

import com.skymiracle.util.*;

public class MailClassCodec {

	private String charset = "UTF-8";

	public MailClassCodec() {

	}

	public MailClassCodec(String charset) {
		this.charset = charset;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public MailClass decode(MailClass mailClass) {
		MailClass newMailClass = new MailClass();
		String s = mailClass.getKeyWord();
		newMailClass.setKeyWord(FnsQPCodec.decode(s, charset));

		s = mailClass.getMailClassFolderName();
		newMailClass.setMailClassFolderName(FnsQPCodec.decode(s, charset));
		
		s = mailClass.getMailClassName();
		newMailClass.setMailClassName(FnsQPCodec.decode(s, charset));
		
		s = mailClass.getTarget();
		newMailClass.setTarget(FnsQPCodec.decode(s, charset));
		
		newMailClass.setOp(mailClass.getOp());
		return newMailClass;
	}

	public MailClass encode(MailClass mailClass) {
		MailClass newMailClass = new MailClass();
		String s = mailClass.getKeyWord();
		newMailClass.setKeyWord(FnsQPCodec.encode(s, charset));

		s = mailClass.getMailClassFolderName();
		newMailClass.setMailClassFolderName(FnsQPCodec.encode(s, charset));
		
		s = mailClass.getMailClassName();
		newMailClass.setMailClassName(FnsQPCodec.encode(s, charset));
		
		s = mailClass.getTarget();
		newMailClass.setTarget(FnsQPCodec.encode(s, charset));
		
		newMailClass.setOp(mailClass.getOp());
		return newMailClass;
	}

	public List<MailClass> encode(List<MailClass> mailClasses) {
		List<MailClass> newMailClasses = new LinkedList<MailClass>();
		for (MailClass mailClass : mailClasses) {
			newMailClasses.add(encode(mailClass));
		}
		return newMailClasses;
	}

	public List<MailClass> decode(List<MailClass> mailClasses) {
		List<MailClass> newMailClasses = new LinkedList<MailClass>();
		for (MailClass mailClass : mailClasses) {
			newMailClasses.add(decode(mailClass));
		}
		return newMailClasses;
	}

}
